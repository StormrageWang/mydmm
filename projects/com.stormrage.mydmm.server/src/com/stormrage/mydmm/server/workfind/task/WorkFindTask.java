package com.stormrage.mydmm.server.workfind.task;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.task.WorkTaskFactory;

/**
 * 获取作品列表的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkFindTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private String actressName; 
	private String url;
	private int pageIndex;
	private WorkTaskFactory[] workFactories;
	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	
	public WorkFindTask(String actressName, int pageIndex, String url){
		this.actressName = actressName;
		this.pageIndex = pageIndex;
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取【" + actressName + "】的作品列表【" + pageIndex + "】中的各个作品详细信息链接";
	}

	@Override
	public void run() {
		logger.info("开始执行" + getName() + "任务");
		try {
			Document doc = TaskUtils.getDocument(url);
			//解析出列表中各个作品的详细信息链接
			fillWorkFactories(doc);
			//添加作品详细信息链接到队列
			addWorksToManager();
			logger.info(getName() + "任务执行完成");
		} catch (TaskException e) {
			logger.error(getName() + "任务执行失败：" + e.getMessage());
		} 
	}
	
	
	private void addWorksToManager() throws TaskException{
		logger.debug("开始将列表解析出来的作品详细信息链接添加到任务工厂队列");
		int count = 0;
		for(WorkTaskFactory workFactory : workFactories){
			String workTitle = workFactory.getWorkTitle();
			WorkBean workBean = WorkFindUtils.getWorkBeanByTitle(workTitle);
			if(workBean != null){
				logger.warn("作品【" + workTitle + "】已存在，详细信息链接不添加到任务工厂队列");
				if(WorkFindUtils.existWorkActress(actressName, workTitle)){
					logger.debug("【" + workTitle + "】已在演员【" + actressName + "】的作品中，不添加关联关系");
				} else {
					logger.debug("【" + workTitle + "】不在演员【" + actressName + "】的作品中，添加关联关系");
					WorkFindUtils.saveWorkToActress(actressName, workTitle);
					logger.debug("【" + workTitle + "】到演员【" + actressName + "】关联关系添加完成");
				}
			} else {
				workFactory.setActressName(actressName);
				factoryManager.addDispatchFactory(workFactory);
				count ++;
			}
		}
		logger.debug("将列表解析出来的作品详细信息链接添加到任务工厂队列完成，共添加了" + count + "个");
	}
	
	
	private void fillWorkFactories(Document doc) throws TaskException {
		logger.debug("开始解析列表中各个作品的详细信息链接");
		workFactories = WorkFindUtils.getWorkFactorys(doc);
		logger.debug("解析列表中各个作品的详细信息链接完成，共有【" + workFactories.length + "】个作品详细信息链接");;
	}
	

}
