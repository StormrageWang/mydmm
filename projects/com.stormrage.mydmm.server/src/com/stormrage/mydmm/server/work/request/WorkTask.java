package com.stormrage.mydmm.server.work.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.PictureBean;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;
import com.stormrage.mydmm.server.request.RequestUtils;
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

	private ActressBean actressBean;
	private WorkBean workBean;
	private static Logger logger = LogManager.getLogger();
	
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
			url = RequestUtils.decode(url);
			workBean.setUrl(url);
			//打开连接
			Document doc = RequestUtils.getDocument(url);
			//获取名称信息
			String fullTitle = doc.select("h1").first().html();
			workBean.setFullTitle(fullTitle);
			//基本信息
			if(workBean.getPageType() == WorkPageType.ANIMATION){
				WorkUtils.fullWorkByAnimationPage(workBean, doc);
			} else if(workBean.getPageType() == WorkPageType.MAIL_ORDER) {
				WorkUtils.fullWorkByMailOrderPage(workBean, doc);
			} else {
				throw new RequestException("不支持从作品【" + workBean.getTitle() + "】的页面中获取信息", RequestErrorCode.WEB_ANALYTICS_GET);
			}
			//封面信息
			Element coverDiv = doc.select("#sample-video").first();
			//小图
			Element simpleCoverLink = coverDiv.select("a").first();
			String simpleCoverUrl = simpleCoverLink.attr("href");
			PictureBean simpleCoverBean = new PictureBean();
			simpleCoverBean.setGuid(Guid.newGuid());
			simpleCoverBean.setUrl(simpleCoverUrl);
			workBean.setSimpleCover(simpleCoverBean);
			//大图
			Element fullCoverImg = coverDiv.select("img").first();
			String fullCoverUrl = fullCoverImg.attr("src");
			PictureBean fullCoverBean = new PictureBean();
			fullCoverBean.setGuid(Guid.newGuid());
			fullCoverBean.setUrl(fullCoverUrl);
			workBean.setFullCover(fullCoverBean);
			//预览信息
			Element pictureDiv = doc.select("#sample-image-block").first();
			if(pictureDiv != null){
				Elements pictureLinks = pictureDiv.select("a");
				PictureBean[] pictureBeans = new PictureBean[pictureLinks.size()];
				int i = 0;
				for(Element pictureLink : pictureLinks){
					String pictureUrl = pictureLink.children().first().attr("src");
					PictureBean pictureBean = new PictureBean();
					pictureBean.setGuid(Guid.newGuid());
					pictureBean.setUrl(pictureUrl);
					pictureBeans[i] = pictureBean;
				}
				workBean.setPreviewPictures(pictureBeans);
			}
			//添加演员信息中
			logger.info("获取作品【" + workBean.getTitle() + "】信息成功：" + workBean.getDescription());
			actressBean.addWorkBean(workBean);
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

}
