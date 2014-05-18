package com.stormrage.mydmm.server.workfind.request;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.request.WorkFactory;
import com.stormrage.mydmm.server.workfind.WorkFindBean;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取作品列表的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTask implements IDispatchTask {

	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	private WorkFindBean workFindBean;
	private static Logger logger = LogManager.getLogger();
	
	public WorkFindTask(WorkFindBean workFindBean){
		this.workFindBean = workFindBean;
	}
	
	@Override
	public String getName() {
		return "获取【" + workFindBean.getActressBean().getName() + "】作品列表";
	}

	@Override
	public void run() {
		try {
			//更新url
			logger.info("开始获取演员【" + workFindBean.getActressBean().getName() + "】作品链接");
			String url = workFindBean.getUrl();
			url = TaskUtils.decode(url);
			workFindBean.setUrl(url);
			//打开连接
			Document doc = TaskUtils.getDocument(url);
			Elements tables = doc.select("#mu table");
			//解析出作品链接
			Element workTable = tables.get(13);
			Elements workTrs = workTable.select("tr");
			Iterator<Element> workTrIterator = workTrs.iterator();
			//跳过第一行
			if(workTrIterator.hasNext()){
				workTrIterator.next();
			} else {
				throw new TaskException("作品列表没有表头", TaskErrorCode.TASK_ANALYTICS_GET);
			}
			int workCount = workTrs.size() - 1;
			int i = 0;
			WorkFactory[] workFactories = new WorkFactory[workCount];
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
					workFactories[i] = new WorkFactory(workFindBean.getActressBean(), 
							workTitle, WorkPageType.ANIMATION, workUrl);
					i++;
				} else if(mailOrderLink != null){
					String workUrl = mailOrderLink.attr("href");
					workUrl = TaskUtils.decode(workUrl);
					workUrl = TaskUtils.addHostUrl(workUrl);
					workFactories[i] = new WorkFactory(workFindBean.getActressBean(), 
							workTitle, WorkPageType.MAIL_ORDER, workUrl);
					i++;
				} else {
					throw new TaskException("不支持找到作品【" + workTitle + "】的详细信息页面", TaskErrorCode.TASK_ANALYTICS_GET);
				}
			}
			if(i != workCount){
				throw new TaskException("获取作品列表任务未添加完，应添加【" + workCount + "】，只添加了【" + i +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
			}
			logger.info("开始获取演员【" + workFindBean.getActressBean().getName() + "】作品链接成功，共有" + workCount + "个");
			for(WorkFactory workFactory : workFactories){
				factoryManager.addDispatchFactory(workFactory);
			}
			logger.info("演员【" + workFindBean.getActressBean().getName() + "】作品链接已经加入网页请求任务队列");
		} catch (TaskException e) {
			logger.error("获取演员【" + workFindBean.getActressBean().getName() + "】作品链接失败" + e.getMessage());
			e.printStackTrace();
		} 
	}

}
