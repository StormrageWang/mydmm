package com.stormrage.mydmm.server.actress.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.actress.ActressDAO;
import com.stormrage.mydmm.server.picture.PictureBean;
import com.stormrage.mydmm.server.picture.PictureDAO;
import com.stormrage.mydmm.server.picture.PictureType;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.utils.StringUtils;
import com.stormrage.mydmm.server.workfind.task.WorkFindTaskFactory;

/**
 * 从获取演员信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressTask implements IDispatchTask {

	private static final String NAME_PREFIX = "&gt;";
	private static final int PAGE_CAPACITY = 50;
	
	private static Logger logger = LogManager.getLogger();
	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	private ActressBean actressBean;
	private PictureBean pictureBean;
	private Set<String> workFindUrlSet = new HashSet<String>(10);
	private WorkFindTaskFactory[] workFindFactories;
	
	public ActressTask(ActressBean actressBean){
		this.actressBean = actressBean;
	}
	
	@Override
	public String getName() {
		return "演员信息获取任务";
	}

	@Override
	public void run() {
		logger.info("开始执行演员信息获取任务");
		try {
			String url = actressBean.getUrl();
			//打开连接
			Document doc = TaskUtils.getDocument(url);
			logger.debug("网页【" + url + "】打开成功");
			//解析出演员的名称
			Elements tables = doc.select("#mu table");
			Element actressNameTable = tables.get(0);
			fillNameByTable(actressNameTable);
			ActressBean oldBean = getActressBeanByName(actressBean.getName());
			if(oldBean != null) {
				logger.debug("演员存在，不解析演员的图片信息");
				actressBean = oldBean;
			} else {
				//解析演员图片
				logger.debug("演员不存在，解析演员的图片信息");
				Element actressPictureTable = tables.get(2);
				fillPictureByTable(actressPictureTable);
				actressBean.setGuid(Guid.newGuid());
				saveActress();
			}
			//解析出需要分析的作品发现链接
			Element workFindTable = tables.get(12);
			fillWorkFindsByTable(workFindTable);
			addWorkFindsToManager();
			logger.info("演员信息获取任务执行完成");
		} catch (TaskException e) {
			logger.error("演员信息获取任务执行失败：" + e.getMessage(), e);
			if(e.getErrorCode() == TaskErrorCode.TASK_REQUEST_IO){
				reTry();
			}
		}
	}
	
	private void reTry() {
		String url =  actressBean.getUrl();
		logger.debug("演员链接【" + url + "】打开失败，重新添加到任务列表");
		ActressTaskFactory taskFactory = new ActressTaskFactory(actressBean.getUrl());
		factoryManager.addDispatchFactory(taskFactory);
	}
	
	
	private void addWorkFindsToManager(){
		logger.debug("开始添加作品列表链接任务接到任务队列");
		int count = 0;
		for(WorkFindTaskFactory workFindTaskFactory : workFindFactories){
			factoryManager.addDispatchFactory(workFindTaskFactory);
			count++;
		}
		logger.debug("作品列表链接任务接到任务队列完成，共添加了" + count + "个");
	}
	
	private void fillWorkFindsByTable(Element workFindTable) throws TaskException {
		logger.debug("开始解析演员的作品列表链接");
		Elements findTds = workFindTable.select("td");
		//获取总页数
		Element headerTd = findTds.get(0);
		Node fullDescNode = headerTd.childNode(0);
		String fullDescStr =  fullDescNode.outerHtml();
		int workCount = ActressUtils.getTotalWorkCount(fullDescStr);
		int pageCount = workCount / PAGE_CAPACITY;
		if(workCount % PAGE_CAPACITY != 0){
			pageCount = pageCount + 1;
		}
		//获取获取作品的链接
		workFindFactories = new WorkFindTaskFactory[pageCount];
		workFindUrlSet.add(actressBean.getUrl());
		workFindFactories[0] = new WorkFindTaskFactory(actressBean.getGuid(), actressBean.getName(), actressBean.getUrl());
		int i = 1;
		Element findTd = findTds.get(1);
		Elements workFindUrlElements = findTd.select("a");
		for(Element workFindUrlElement : workFindUrlElements){
			String workFindUrl = workFindUrlElement.attr("href");
			workFindUrl = TaskUtils.decode(workFindUrl);
			workFindUrl = TaskUtils.addHostUrl(workFindUrl);
			if(!workFindUrlSet.contains(workFindUrl)){
				workFindUrlSet.add(workFindUrl);
				workFindFactories[i] = new WorkFindTaskFactory(actressBean.getGuid(), actressBean.getName(), workFindUrl);
				i++;
			}
		}
		if(i != pageCount){
			throw new TaskException("作品列表链接解析错误 ，应添加【" + pageCount + "】，只添加了【" + i +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
		}
		logger.debug("作品列表链接解析完成，共有" + pageCount + "个");
	}
	
	private void fillNameByTable(Element actressNameTable) throws TaskException {
		//解析出演员的名称
		logger.debug("开始解析演员的名称信息");
		Element nameTd = actressNameTable.select("td.pankuzu").first();
		Node nameNode = nameTd.childNode(nameTd.childNodeSize() - 1);
		String name = nameNode.outerHtml();
		name = name.replace(NAME_PREFIX, "");
		name = StringUtils.trim(name);
		actressBean.setName(name);
		Element fullNameH = actressNameTable.select("h1").first();
		String fullName = fullNameH.html();
		actressBean.setFullName(fullName);
		logger.debug("开始解析演员的名称信息解析完成，为【" + fullName + "】");
	}
	
	
	private void fillPictureByTable(Element actressPictureTable) throws TaskException {
		//解析出演员的图片
		logger.debug("开始解析演员的图片信息");
		Element actressPicture = actressPictureTable.select("img").first();
		String pictureUrl = actressPicture.attr("src");
		pictureUrl = TaskUtils.decode(pictureUrl);
		pictureBean = new PictureBean();
		pictureBean.setGuid(Guid.newGuid());
		pictureBean.setUrl(pictureUrl);
		pictureBean.setType(PictureType.SMALL);
		actressBean.setPictureGuid(pictureBean.getGuid());
		logger.debug("演员的图片信息解析完成，为【" + pictureUrl + "】" );
	}
	
	private void saveActress() throws TaskException {
		logger.debug("开始保存演员信息");
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				conn.setAutoCommit(false);
				PictureDAO.addPictures(conn, new PictureBean[]{pictureBean});
				ActressDAO.addActress(conn, actressBean);
				conn.commit();
			} finally {
				conn.close();
			}
		} catch(SQLException e){
			throw new TaskException("保存演员信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
		logger.debug("演员信息保存成功");
	}
	
	private ActressBean getActressBeanByName(String actressName) throws TaskException{
		ActressBean oldBean = null;
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				oldBean = ActressDAO.getByName(conn, actressName);
				return oldBean;
			} finally {
				conn.close();
			}
		}catch(SQLException e){
			throw new TaskException("获取演员【" + actressName + "】信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}

}