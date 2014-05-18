package com.stormrage.mydmm.server.work.request;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;
import com.stormrage.mydmm.server.request.RequestUtils;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.workfind.WorkPageType;

public class WorkTask implements IDispatchTask {

	private ActressBean actressBean;
	private WorkBean workBean;
	
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
			String url = workBean.getUrl();
			url = RequestUtils.decode(url);
			workBean.setUrl(url);
			//打开连接
			try {
				Document doc = RequestUtils.getDocument(url);
				//获取名称信息
				String name = doc.select("h1").first().html();
				workBean.setJpName(name);
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
				Element simpleCoverLink = coverDiv.select("a").first();
				Element fullCoverImg = coverDiv.select("img").first();
				String simpleCoverUrl = simpleCoverLink.attr("href");
				String fullCoverUrl = fullCoverImg.attr("src");
				workBean.setCoverSimpleUrl(simpleCoverUrl);
				workBean.setCoverFullUrl(fullCoverUrl);
				//预览信息
				Element pictureDiv = doc.select("#sample-image-block").first();
				if(pictureDiv != null){
					Elements pictureLinks = pictureDiv.select("a");
					String[] pictureUrls = new String[pictureLinks.size()];
					int i = 0;
					for(Element pictureLink : pictureLinks){
						String pictureUrl = pictureLink.children().first().attr("src");
						pictureUrls[i] = pictureUrl;
					}
					workBean.setPictureUrls(pictureUrls);
				}
				//添加演员信息中
				actressBean.addWorkBean(workBean);
				System.out.println(workBean.getInfo());
			} catch (RequestException e) {
				e.printStackTrace();
			}
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

}
