package com.stormrage.mydmm.server.work.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.WorkActressDAO;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.WorkDAO;
import com.stormrage.mydmm.server.work.WorkPictureBean;
import com.stormrage.mydmm.server.work.WorkPictureDAO;
import com.stormrage.mydmm.server.work.WorkUtils;

/**
 * 获取作品信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private String actressName;
	private String workTitle;
	private String url;
	private WorkBean workBean;
	private WorkPictureBean simpleCoverBean, fullCoverBean;
	private WorkPictureBean[] previewPictureBeans;
	
	public WorkTask(String actressName, String workTitle, String url) {
		this.actressName = actressName;
		this.workTitle = workTitle;
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取作品【" + workTitle + "】详细信息";
	}

	@Override
	public void run() {
		logger.info("开始执行任务");
		workBean = new WorkBean();
		workBean.setTitle(workTitle);
		workBean.setUrl(url);
		try {
			Document doc = TaskUtils.getDocument(url);
			Elements infoTables = doc.select("table");
			if(infoTables.size() < 3) {
				//打开的页面不对
				logger.warn("页面【" + url + "】未包含作品的基本信息，跳过解析该页面");
				return;
			}
			//解析作品详细信息
			fillTaskBean(doc);
			//保存作品
			saveWork();
			logger.info("任务执行完成");
		} catch(TaskException e) {
			e.printStackTrace();
			logger.error("任务执行失败：" + e.getMessage(), e);
		}
	}
	private void fillTaskBean(Document doc) throws TaskException {
		logger.debug("开始解析作品信息");
		//获取完整名称，完整番号，简单番号，日期，时长，演员类型
		logger.debug("开始解析作品的基本信息");
		WorkUtils.fullBaseInfo(workBean, doc);
		logger.debug("解析作品的基本信息完成");
		logger.debug("开始解析作品的封面图");
		simpleCoverBean = WorkUtils.getSimpleCover(doc);
		simpleCoverBean.setWorkCode(workBean.getCode());
		fullCoverBean = WorkUtils.getFullCover(doc);
		if(fullCoverBean != null){
			fullCoverBean.setWorkCode(workBean.getCode());
		}
		logger.debug("解析作品的封面图完成");
		logger.debug("开始解析作品的预览图");
		previewPictureBeans = WorkUtils.getPreviewPictures(doc);
		for(WorkPictureBean pictureBean : previewPictureBeans){
			pictureBean.setWorkCode(workBean.getCode());
		}
		logger.debug("解析作品的预览图完成");
		logger.debug("解析作品信息完成");
	}
	
	
	private void saveWork() throws TaskException {
		logger.debug("开始保存作品信息");
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				conn.setAutoCommit(false);
				//保存作品信息
				logger.debug("保存作品");
				WorkDAO.addWork(conn, workBean);
				//保存作品封面图
				logger.debug("保存作品封面图");
				WorkPictureBean[] coverBeans = new WorkPictureBean[]{simpleCoverBean, fullCoverBean};
				if(fullCoverBean == null){//大图没有
					coverBeans = new WorkPictureBean[]{simpleCoverBean};
				}
				WorkPictureDAO.addPictures(conn, coverBeans);
				//保存作品预览图
				logger.debug("保存作品预览图");
				WorkPictureDAO.addPictures(conn, previewPictureBeans);
				//保存作品与演员的对应关系
				logger.debug("保存作品与演员的对应关系");
				ActressBean actressBean = new ActressBean();
				actressBean.setName(actressName);
				WorkActressDAO.addWorkActress(conn, workBean.getCode(), new ActressBean[]{actressBean});
				conn.commit();
				logger.debug("作品信息保存完成");
			} finally {
				conn.close();
			}
		} catch(SQLException e){
			throw new TaskException("保存演员信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
		
}
