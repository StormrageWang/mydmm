package com.stormrage.mydmm.server.work.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.picture.PictureBean;
import com.stormrage.mydmm.server.picture.PictureDAO;
import com.stormrage.mydmm.server.picture.PictureType;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.task.status.ITaskFinishListener;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.work.WorkActressDAO;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.WorkDAO;
import com.stormrage.mydmm.server.work.WorkPreviewDAO;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取作品信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private PictureBean simpleCoverBean, fullCoverBean;
	private PictureBean[] previewPictureBeans = new PictureBean[0];
	private String actressGuid;
	private String workTitle;
	private WorkPageType pageType;
	private String url;
	private WorkBean workBean;
	private ITaskFinishListener finishListener;
	
	public WorkTask(String actressGuid, String workTitle, WorkPageType pageType, String url) {
		this.actressGuid = actressGuid;
		this.workTitle = workTitle;
		this.pageType = pageType;
		this.url = url;
	}
	
	@Override
	public String getName() {
		return "获取作品【" + workTitle + "】详细信息";
	}

	@Override
	public void run() {
		logger.info("开始执行获取作品信息任务");
		workBean = new WorkBean();
		workBean.setGuid(Guid.newGuid());
		workBean.setTitle(workTitle);
		workBean.setPageType(pageType);
		workBean.setUrl(url);
		try {
			Document doc = TaskUtils.getDocument(url);
			//基本信息
			fillBaseInfoByDocument(doc);
			//封面信息
			Element coverDiv = doc.select("#sample-video").first();
			fillCoverByDiv(coverDiv);
			//预览信息
			Element previewDiv = doc.select("#sample-image-block").first();
			fillPreviewByDiv(previewDiv);
			//添加演员信息中
			saveWork();
			finish();
			logger.info("获取作品信息任务执行完成");
		} catch (TaskException e) {
			logger.error("获取作品信息任务执行失败：" + e.getMessage(), e);
			finish();
		}
	}
	
	private void finish(){
		if(finishListener != null){
			finishListener.finish();
		}
	}
	
	public void setFinishListener(ITaskFinishListener finishListener) {
		this.finishListener = finishListener;
	}
	
	private void fillBaseInfoByDocument(Document doc) throws TaskException {
		//获取名称信息：完整名称，日期，时长，完整番号，番号
		logger.debug("开始解析作品的基本信息");
		String fullTitle = doc.select("h1").first().html();
		workBean.setFullTitle(fullTitle);
		if(workBean.getPageType() == WorkPageType.ANIMATION){
			WorkUtils.fullWorkByAnimationPage(workBean, doc);
		} else if(workBean.getPageType() == WorkPageType.MAIL_ORDER) {
			WorkUtils.fullWorkByMailOrderPage(workBean, doc);
		} else if(workBean.getPageType() == WorkPageType.SINGLE_RENT){
			WorkUtils.fullWorkBySingleRental(workBean, doc);
		} else {
			throw new TaskException("不支持从作品【" + workBean.getTitle() + "】的页面中获取信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		logger.debug("解析作品的基本信息完成");
	}
	
	private void fillCoverByDiv(Element coverDiv){
		logger.debug("开始解析作品的封面信息");
		//小图
		Element simpleCoverImage = coverDiv.select("img").first();
		String simpleCoverUrl = simpleCoverImage.attr("src");
		simpleCoverBean = new PictureBean();
		simpleCoverBean.setGuid(Guid.newGuid());
		simpleCoverBean.setUrl(simpleCoverUrl);
		simpleCoverBean.setType(PictureType.GENERAL);
		workBean.setCoverGuid(simpleCoverBean.getGuid());
		//大图
		Element fullCoverLink =  coverDiv.select("a").first();
		if(fullCoverLink == null){
			logger.warn("无法解析出作品的封面大图");
			return;
		}
		String fullCoverUrl = fullCoverLink.attr("href");
		fullCoverBean = new PictureBean();
		fullCoverBean.setGuid(Guid.newGuid());
		fullCoverBean.setUrl(fullCoverUrl);
		fullCoverBean.setType(PictureType.BIG);
		workBean.setFullCoverGuid(fullCoverBean.getGuid());
		logger.debug("解析作品的封面信息完成");
	}
	
	private void fillPreviewByDiv(Element previewDiv){
		logger.debug("开始解析作品的预览信息");
		if(previewDiv != null){
			Elements pictureLinks = previewDiv.select("a");
			previewPictureBeans = new PictureBean[pictureLinks.size() * 2];
			int i = 0;
			for(Element pictureLink : pictureLinks){
				//小图
				String smallUrl = pictureLink.children().first().attr("src");
				PictureBean smallBean = new PictureBean();
				smallBean.setGuid(Guid.newGuid());
				smallBean.setUrl(smallUrl);
				smallBean.setType(PictureType.SMALL);
				previewPictureBeans[i] = smallBean;
				i++;
				//对应的大图
				PictureBean bigBean = new PictureBean();
				String bugUrl = WorkUtils.getFullPictureUrl(smallUrl);
				bigBean.setGuid(Guid.newGuid());
				bigBean.setUrl(bugUrl);
				bigBean.setType(PictureType.BIG);
				previewPictureBeans[i] = bigBean;
				i++;
			}
		}
		logger.debug("解析作品的预览信息完成");
	}
	
	private void saveWork() throws TaskException {
		logger.debug("开始保存作品信息");
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				conn.setAutoCommit(false);
				PictureBean[] coverBeans = new PictureBean[0];
				if(fullCoverBean == null){
					coverBeans = new PictureBean[]{simpleCoverBean};
				} else {
					coverBeans = new PictureBean[]{simpleCoverBean, fullCoverBean};
				}
				PictureDAO.addPictures(conn, coverBeans);
				WorkDAO.addWork(conn, workBean);
				PictureDAO.addPictures(conn, previewPictureBeans);
				WorkPreviewDAO.addWorkPreviews(conn, workBean.getGuid(), previewPictureBeans);
				ActressBean actressBean = new ActressBean();
				actressBean.setGuid(actressGuid);
				WorkActressDAO.addWorkActress(conn, workBean.getGuid(), new ActressBean[]{actressBean});
				conn.commit();
			} finally {
				conn.close();
			}
		} catch(SQLException e){
			throw new TaskException("保存演员信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
		logger.debug("作品信息保存完成");
	}
		
}
