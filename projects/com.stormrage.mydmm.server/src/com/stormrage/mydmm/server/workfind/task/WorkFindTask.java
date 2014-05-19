package com.stormrage.mydmm.server.workfind.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.WorkDAO;
import com.stormrage.mydmm.server.work.task.WorkFactory;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取作品列表的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTask implements IDispatchTask {

	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	private static Logger logger = LogManager.getLogger();
	private String actressGuid;
	private String actressName; 
	private String url;
	private WorkFactory[] workFactories;
	
	public WorkFindTask(String actressGuid, String actressName, String url){
		this.actressGuid = actressGuid;
		this.actressName = actressName;
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取【" + actressName + "】作品列表";
	}

	@Override
	public void run() {
		logger.info("开始执行作品链接获取任务");
		try {
			Document doc = TaskUtils.getDocument(url);
			logger.debug("网页【" + url + "】打开成功");
			Elements tables = doc.select("#mu table");
			//解析出作品链接
			Element workTable = tables.get(13);
			fillWorksByTable(workTable);
			//添加链接到队列
			addWorksToManager();
			logger.info("作品链接获取任务执行完成");
		} catch (TaskException e) {
			logger.error("作品链接获取任务执行失败：" + e.getMessage());
			if(e.getErrorCode() == TaskErrorCode.TASK_REQUEST_IO){
				reTry();
			}
		} 
	}
	
	private void reTry() {
		logger.debug("作品列表链接【" + url + "】打开失败，重新添加到任务列表");
		WorkFindTaskFactory workFindTaskFactory = new WorkFindTaskFactory(actressGuid, actressName, url);
		factoryManager.addDispatchFactory(workFindTaskFactory);
	}
	
	private void addWorksToManager() throws TaskException{
		logger.debug("开始添加作品链接任务接到任务队列");
		int count = 0;
		for(WorkFactory workFactory : workFactories){
			String workTitle = workFactory.getWorkTitle();
			if(workTitleExist(workTitle)){
				logger.warn("作品【" + workTitle + "】已存在，不添加到作品链接任务队列");
			} else {
				factoryManager.addDispatchFactory(workFactory);
				count ++;
			}
		}
		logger.debug("添加作品链接任务接到任务队列完成，共添加了" + count + "个");
	}
	
	private void fillWorksByTable(Element workTable) throws TaskException {
		logger.debug("开始解析演员的作品链接");
		Elements workTrs = workTable.select("tr");
		Iterator<Element> workTrIterator = workTrs.iterator();
		//跳过第一行
		workTrIterator.next();
		int workCount = workTrs.size() - 1;
		int i = 0;
		workFactories = new WorkFactory[workCount];
		while(workTrIterator.hasNext()){
			Element workTr = workTrIterator.next();
			Element titleLink = workTr.child(0).select("a").first();
			String workTitle = titleLink.html();
			Element animationLink = workTr.child(1).select("a").first();
			Element mailOrderLink = workTr.child(4).select("a").first();
			if(animationLink != null){
				String workUrl = animationLink.attr("href");
				workUrl = TaskUtils.decode(workUrl);
				workUrl = TaskUtils.addHostUrl(workUrl);
				workFactories[i] = new WorkFactory(actressGuid, 
						workTitle, WorkPageType.ANIMATION, workUrl);
			} else if(mailOrderLink != null){
				String workUrl = mailOrderLink.attr("href");
				workUrl = TaskUtils.decode(workUrl);
				workUrl = TaskUtils.addHostUrl(workUrl);
				workFactories[i] = new WorkFactory(actressGuid, workTitle, WorkPageType.MAIL_ORDER, workUrl);
			} else {
				logger.warn("不支持作品【" + workTitle + "】的链接页面格式，不添加到作品链接队列");
			}
			i++;
		}
		if(i != workCount){
			throw new TaskException("解析演员的作品链接失败，应解析出【" + workCount + "】，只解析出了【" + i +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
		}
		logger.debug("解析演员的作品链接完成，共有" + workCount + "个");
	}
	
	private boolean workTitleExist(String workTitle) throws TaskException{
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkDAO.titleExist(conn, workTitle);
			} finally {
				conn.close();
			}
		}catch(SQLException e){
			throw new TaskException("判断作品【" + workTitle + "】是否存在时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}

}
