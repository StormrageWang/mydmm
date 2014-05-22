package com.stormrage.mydmm.server.actress.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.actress.ActressDAO;
import com.stormrage.mydmm.server.actress.ActressPictureBean;
import com.stormrage.mydmm.server.actress.ActressPictureDAO;
import com.stormrage.mydmm.server.actress.ActressUtils;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.WorkTaskManagerInstance;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.workfind.task.WorkFindTaskFactory;

/**
 * 从获取演员信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private DispatchTaskFactoryManager factoryManager = WorkTaskManagerInstance.getInstance();
	private String url;
	private ActressBean actressBean;
	private ActressPictureBean coverPictureBean;
	private WorkFindTaskFactory[] workFindFactories;
	
	public ActressTask(String url){
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取演员信息";
	}

	@Override
	public void run() {
		logger.info("开始执行任务");
		actressBean = new ActressBean();
		actressBean.setUrl(url);
		try {
			//打开连接
			Document doc = TaskUtils.getDocument(url);
			fillActressBean(doc);
			fillWorkFindFactories(doc);
			saveActress();
			addWorkFindsToManager();
			logger.info("任务执行完成");
		} catch (TaskException e) {
			logger.error("任务执行失败：" + e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	private void addWorkFindsToManager(){
		logger.debug("开始将列表解析出来的作品列表链接添加到任务工厂队列");
		int count = 0;
		for(WorkFindTaskFactory workFindTaskFactory : workFindFactories){
			workFindTaskFactory.setActressName(actressBean.getName());
			factoryManager.addDispatchFactory(workFindTaskFactory);
			count++;
		}
		logger.debug("作品列表链接任务接到任务队列完成，共添加了" + count + "个");
	}
	
	private void fillWorkFindFactories(Document doc) throws TaskException {
		logger.debug("开始解析列表中各个作品的详细信息链接");
		workFindFactories = ActressUtils.getWorkFindFactories(doc);
		//添加当前页
		int currentIndex = ActressUtils.getCurrentIndex(doc);
		workFindFactories[workFindFactories.length - 1] = 
				new WorkFindTaskFactory(actressBean.getName(), currentIndex, url);
		logger.debug("解析列表中各个作品的详细信息链接完成，共有【" + workFindFactories.length + "】个作品详细信息链接");
	}
	
	private void fillActressBean(Document doc) throws TaskException {
		logger.debug("开始解析演员详细信息");
		//解析出演员的名称
		String name = ActressUtils.getName(doc);
		actressBean.setName(name);
		//解析出全称
		String fullName = ActressUtils.getFullName(doc);
		actressBean.setFullName(fullName);
		//解析出演员的图片
		coverPictureBean = ActressUtils.getCoverPicture(doc);
		coverPictureBean.setActressName(actressBean.getName());
		logger.debug("解析演员详细信息完成");
	}
	
	private void saveActress() throws TaskException {
		logger.debug("开始保存演员【" + actressBean.getName() + "】的信息");
		ActressBean oldActressBean = ActressUtils.getActressBeanByName(actressBean.getName());
		if(oldActressBean != null){
			logger.warn("演员【" + actressBean.getName() + "】的信息已存在，不保存");
		} else {
			saveActressAndPicture();
			logger.debug("保存演员【" + actressBean.getName() + "】的信息完成");
		}
	}
	
	private void saveActressAndPicture() throws TaskException {
		logger.debug("开始保存演员【" + actressBean.getName() + "】信息");
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				conn.setAutoCommit(false);
				logger.debug("开始保存演员【" + actressBean.getName() + "】");
				ActressDAO.addActress(conn, actressBean);
				logger.debug("开始保存演员【" + actressBean.getName() + "】的封面");
				ActressPictureDAO.addPictures(conn, new ActressPictureBean[]{coverPictureBean});
				conn.commit();
				logger.debug("保存演员【" + actressBean.getName() + "】信息完成");
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("保存演员【" + actressBean.getName() + "】信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}

}
