package com.stormrage.mydmm.server.work.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.picture.PictureBean;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.workfind.WorkPageType;

/**
 * 获取作品信息的需要分发的请求任务
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkTask implements IDispatchTask {

	private static Logger logger = LogManager.getLogger();
	private ActressBean actressBean;
	private WorkBean workBean;
	private PictureBean simpleCoverBean, fullCoverBean;
	private PictureBean[] previewPictureBeans = new PictureBean[0];
	
	public WorkTask(ActressBean actressBean, WorkBean workBean) {
		this.actressBean = actressBean;
		this.workBean = workBean;
	}
	
	@Override
	public String getName() {
		return "获取作品【" + workBean.getTitle() + "】详细信息";
	}

	@Override
	public void run() {
		//更新url
		try {
			logger.info("开始获取作品【" + workBean.getTitle() + "】信息");
			String url = workBean.getUrl();
			url = TaskUtils.decode(url);
			workBean.setUrl(url);
			//打开连接
			Document doc = TaskUtils.getDocument(url);
			//获取名称信息
			String fullTitle = doc.select("h1").first().html();
			workBean.setFullTitle(fullTitle);
			//基本信息
			if(workBean.getPageType() == WorkPageType.ANIMATION){
				WorkUtils.fullWorkByAnimationPage(workBean, doc);
			} else if(workBean.getPageType() == WorkPageType.MAIL_ORDER) {
				WorkUtils.fullWorkByMailOrderPage(workBean, doc);
			} else {
				throw new TaskException("不支持从作品【" + workBean.getTitle() + "】的页面中获取信息", TaskErrorCode.TASK_ANALYTICS_GET);
			}
			//封面信息
			Element coverDiv = doc.select("#sample-video").first();
			//小图
			Element simpleCoverLink = coverDiv.select("a").first();
			String simpleCoverUrl = simpleCoverLink.attr("href");
			simpleCoverBean = new PictureBean();
			simpleCoverBean.setGuid(Guid.newGuid());
			simpleCoverBean.setUrl(simpleCoverUrl);
			workBean.setSimpleCoverGuid(simpleCoverBean.getGuid());
			//大图
			Element fullCoverImg = coverDiv.select("img").first();
			String fullCoverUrl = fullCoverImg.attr("src");
			fullCoverBean = new PictureBean();
			fullCoverBean.setGuid(Guid.newGuid());
			fullCoverBean.setUrl(fullCoverUrl);
			workBean.setFullCoverGuid(fullCoverBean.getGuid());
			//预览信息
			Element pictureDiv = doc.select("#sample-image-block").first();
			if(pictureDiv != null){
				Elements pictureLinks = pictureDiv.select("a");
				previewPictureBeans = new PictureBean[pictureLinks.size()];
				int i = 0;
				for(Element pictureLink : pictureLinks){
					String pictureUrl = pictureLink.children().first().attr("src");
					PictureBean pictureBean = new PictureBean();
					pictureBean.setGuid(Guid.newGuid());
					pictureBean.setUrl(pictureUrl);
					previewPictureBeans[i] = pictureBean;
				}
			}
			//添加演员信息中
			logger.info("获取作品【" + workBean.getTitle() + "】信息成功：" + workBean.getDescription());
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}

}
