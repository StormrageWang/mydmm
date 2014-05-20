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
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.status.EmptyStatusProvider;
import com.stormrage.mydmm.server.task.status.ITaskFinishListener;
import com.stormrage.mydmm.server.task.status.TaskStatusManager;
import com.stormrage.mydmm.server.work.WorkActressDAO;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.WorkDAO;
import com.stormrage.mydmm.server.work.task.WorkFactory;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取作品列表的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	private String actressGuid;
	private String actressName; 
	private String url;
	private int pageIndex;
	private WorkFactory[] workFactories;
	private ITaskFinishListener finishListener;
	
	public WorkFindTask(String actressGuid, String actressName, int pageIndex, String url){
		this.actressGuid = actressGuid;
		this.actressName = actressName;
		this.pageIndex = pageIndex;
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取【" + actressName + "】作品列表-" + pageIndex;
	}

	@Override
	public void run() {
		logger.info("开始执行作品链接获取任务");
		try {
			Document doc = TaskUtils.getDocument(url);
			Elements tables = doc.select("#mu table");
			//解析出作品链接
			Element workTable = tables.get(13);
			fillWorksByTable(workTable);
			//添加链接到队列
			addWorksToManager();
			logger.info("作品链接获取任务执行完成");
		} catch (TaskException e) {
			logger.error("作品链接获取任务执行失败：" + e.getMessage());
			finish();
		} 
	}
	
	private void finish(){
		if(finishListener != null){
			finishListener.finish();
		}
	}
	
	private void addWorksToManager() throws TaskException{
		logger.debug("开始添加作品链接任务接到任务队列");
		int count = 0;
		TaskStatusManager statusManager = new TaskStatusManager(getName(), workFactories.length);
		statusManager.setFinishListener(new ITaskFinishListener() {
			
			@Override
			public void finish() {
				finish();
			}
		});
		for(WorkFactory workFactory : workFactories){
			String workTitle = workFactory.getWorkTitle();
			WorkBean workBean = getWorkBeanByTitle(workTitle);
			if(workBean != null){
				logger.warn("作品【" + workTitle + "】已存在，不添加到作品链接任务队列");
				if(existWorkActress(workBean.getGuid(), workTitle)){
					logger.debug("【" + workTitle + "】已在演员【" + actressName + "】的作品中，不添加关联关系");
				} else {
					logger.debug("【" + workTitle + "】不在演员【" + actressName + "】的作品中，添加关联关系");
					addWorkToActress(workBean.getGuid(), workTitle);
				}
				statusManager.addStatusProvider(EmptyStatusProvider.getInstance());
				//logger.error(pageIndex + "\t" + i + "\t" + workBean.getFullCode() + "\t" + workBean.getFullTitle() + "\t" + workTitle);
			} else {
				factoryManager.addDispatchFactory(workFactory);
				statusManager.addStatusProvider(workFactory);
				count ++;
			}
		}
		logger.debug("添加作品链接任务接到任务队列完成，共添加了" + count + "个");
	}
	
	private boolean existWorkActress(String workGuid, String workTitle) throws TaskException {
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkActressDAO.existWorkActress(conn, workGuid, actressGuid);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("判断【" + workTitle + "】是否在演员【" + actressName + "】的作品中时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	private void addWorkToActress(String workGuid, String workTitle) throws TaskException{
		logger.debug("添加【" + workTitle + "】到演员【" + actressName + "】的作品中");
		ActressBean actressBean = new ActressBean();
		actressBean.setGuid(actressGuid);
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				WorkActressDAO.addWorkActress(conn, workGuid, new ActressBean[]{actressBean});
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("添加作品【" + workTitle + "】到演员【" + actressName + "】的作品中时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
		logger.debug("添加【" + workTitle + "】到演员【" + actressName + "】的作品中完成");
		
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
			Element singleRentLink = workTr.child(5).select("a").first();
			if(animationLink != null){
				String workUrl = animationLink.attr("href");
				workUrl = TaskUtils.decode(workUrl);
				workUrl = TaskUtils.addHostUrl(workUrl);
				workFactories[i] = new WorkFactory(actressGuid, workTitle, WorkPageType.ANIMATION, i + 1, workUrl);
				i++;
			} else if(mailOrderLink != null){
				String workUrl = mailOrderLink.attr("href");
				workUrl = TaskUtils.decode(workUrl);
				workUrl = TaskUtils.addHostUrl(workUrl);
				workFactories[i] = new WorkFactory(actressGuid, workTitle, WorkPageType.MAIL_ORDER, i + 1, workUrl);
				i++;
			} else if(singleRentLink != null){
				String workUrl = singleRentLink.attr("href");
				workUrl = TaskUtils.decode(workUrl);
				workUrl = TaskUtils.addHostUrl(workUrl);
				workFactories[i] = new WorkFactory(actressGuid, workTitle, WorkPageType.SINGLE_RENT, i + 1, workUrl);
				i++;
			} else {
				logger.warn("不支持作品列表【" + url + "】中作品【" + workTitle + "】的链接页面格式，不添加到作品链接队列");
			}
		}
		if(i != workCount){
			throw new TaskException("解析演员的作品链接失败，应解析出【" + workCount + "】，只解析出了【" + i +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
		}
		logger.debug("解析演员的作品链接完成，共有" + workCount + "个");;
	}
	
	private WorkBean getWorkBeanByTitle(String workTitle) throws TaskException{
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkDAO.getByTitle(conn, workTitle);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("判断作品【" + workTitle + "】是否存在时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	public void setFinishListener(ITaskFinishListener finishListener) {
		this.finishListener = finishListener;
	}

}
