package com.stormrage.mydmm.server.actress.request;

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
import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.utils.StringUtils;
import com.stormrage.mydmm.server.workfind.request.WorkFindTaskFactory;

/**
 * 从获取演员信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressTask implements IDispatchTask {

	private static final String NAME_PREFIX = "&gt;";
	private static final int PAGE_CAPACITY = 50;
	
	private static Logger logger = LogManager.getLogger();
	private Set<String> workFindUrlSet = new HashSet<String>(10);
	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	private ActressBean actressBean;
	private PictureBean pictureBean;
	
	public ActressTask(ActressBean actressBean){
		this.actressBean = actressBean;
	}
	
	@Override
	public String getName() {
		return "获取演员信息";
	}

	@Override
	public void run() {
		logger.info("开始获取演员信息");
		try {
			//更新url
			String url = actressBean.getUrl();
			//打开连接
			logger.info("处理演员信息页面【" + url + "】");
			Document doc = TaskUtils.getDocument(url);
			logger.debug("网页【" + url + "】打开成功");
			//解析出演员的名称
			logger.debug("开始解析演员的名称");
			Elements tables = doc.select("#mu table");
			Element actressNameTable = tables.get(0);
			Element nameTd = actressNameTable.select("td.pankuzu").first();
			Node nameNode = nameTd.childNode(nameTd.childNodeSize() - 1);
			String name = nameNode.outerHtml();
			name = name.substring(NAME_PREFIX.length());
			name = StringUtils.trim(name);
			logger.debug("解析演员的名称完成，为【" + name + "】");
			logger.debug("判断演员【" + name + "】是否存在");
			ActressBean oldBean = getActressBeanByName(name);
			if(oldBean != null) {
				logger.debug("判断演员【" + name + "】存在，不解析其他信息");
				actressBean = oldBean;
				
			} else {
			}
			
			//解析出需要分析的作品发现链接
			logger.debug("获取演员【" + actressBean.getName() + "】作品列表链接");
			Element workFindTable = tables.get(12);
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
			WorkFindTaskFactory[] workFindFactories= new WorkFindTaskFactory[pageCount];
			url = TaskUtils.addHostUrl(url);
			workFindUrlSet.add(url);
			workFindFactories[0] = new WorkFindTaskFactory(actressBean, url);
			int i = 1;
			Element findTd = findTds.get(1);
			Elements workFindUrlElements = findTd.select("a");
			for(Element workFindUrlElement : workFindUrlElements){
				String workFindUrl = workFindUrlElement.attr("href");
				workFindUrl = TaskUtils.decode(workFindUrl);
				workFindUrl = TaskUtils.addHostUrl(workFindUrl);
				if(!workFindUrlSet.contains(workFindUrl)){
					workFindUrlSet.add(workFindUrl);
					workFindFactories[i] = new WorkFindTaskFactory(actressBean, workFindUrl);
					i++;
				}
			}
			if(i != pageCount){
				throw new TaskException("获取作品列表任务未添加完，应添加【" + pageCount + "】，只添加了【" + i +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
			}
			logger.info("获取演员【" + actressBean.getName() + "】作品列表链接完成，共有" + pageCount + "个");
			
			
			for(WorkFindTaskFactory workFindTaskFactory : workFindFactories){
				factoryManager.addDispatchFactory(workFindTaskFactory);
			}
			logger.info("演员【" + actressBean.getName() + "】所有作品列表链接已经加入网页请求任务队列");
		} catch (TaskException e) {
			logger.error("演员信息页面处理失败：" + e.getMessage(), e);
		}
	}
	
	private void fillActressByPage(Document doc) throws TaskException {
		logger.debug("开始解析演员的信息");
		Elements tables = doc.select("#mu table");
		Element actressNameTable = tables.get(0);
		Element nameTd = actressNameTable.select("td.pankuzu").first();
		Node nameNode = nameTd.childNode(nameTd.childNodeSize() - 1);
		String name = nameNode.outerHtml();
		name = name.substring(NAME_PREFIX.length());
		name = StringUtils.trim(name);
		Element fullNameH = actressNameTable.select("h1").first();
		String fullName = fullNameH.html();
		actressBean.setFullName(fullName);
		//解析出演员的图片
		Element actressPictureTable = tables.get(2);
		Element actressPicture = actressPictureTable.select("img").first();
		String pictureUrl = actressPicture.attr("src");
		pictureUrl = TaskUtils.decode(pictureUrl);
		pictureBean = new PictureBean();
		pictureBean.setGuid(Guid.newGuid());
		pictureBean.setUrl(pictureUrl);
		actressBean.setPictureGuid(pictureBean.getGuid());
		logger.debug("演员信息解析完成：" + actressBean.getDescription());
	}
	
	
	private void saveActress() throws TaskException {
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
			throw new TaskException("判断演员信息是否存在是操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}

}
