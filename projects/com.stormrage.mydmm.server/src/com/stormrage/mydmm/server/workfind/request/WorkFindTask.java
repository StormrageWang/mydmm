package com.stormrage.mydmm.server.workfind.request;

import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;
import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.request.RequestUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.work.request.WorkFactory;
import com.stormrage.mydmm.server.workfind.WorkFindBean;
import com.stormrage.mydmm.server.workfind.WorkPageType;

public class WorkFindTask implements IDispatchTask {

	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	private WorkFindBean workFindBean;
	
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
			String url = workFindBean.getUrl();
			url = RequestUtils.decode(url);
			workFindBean.setUrl(url);
			//打开连接
			Document doc = RequestUtils.getDocument(url);
			Elements tables = doc.select("#mu table");
			//解析出作品链接
			Element workTable = tables.get(13);
			Elements workTrs = workTable.select("tr");
			Iterator<Element> workTrIterator = workTrs.iterator();
			//跳过第一行
			if(workTrIterator.hasNext()){
				workTrIterator.next();
			} else {
				throw new RequestException("作品列表没有表头", RequestErrorCode.WEB_ANALYTICS_GET);
			}
			int workCount = workTrs.size() - 1;
			int i = 0;
			WorkFactory[] workFactories = new WorkFactory[workCount];
			while(workTrIterator.hasNext()){
				Element workTr = workTrIterator.next();
				Element titleLink = workTr.child(0).select("a").first();
				String workTitle = titleLink.html();
				workFindBean.setWorkTitle(workTitle);
				Element animationLink = workTr.child(1).select("a").first();
				Element mailOrderLink = workTr.child(4).select("a").first();
				if(animationLink != null){
					String workUrl = animationLink.attr("href");
					workUrl = RequestUtils.decode(workUrl);
					workUrl = RequestUtils.addHostUrl(workUrl);
					workFindBean.setWorkUrl(workUrl);
					workFindBean.setPageType(WorkPageType.ANIMATION);
					workFactories[i] = new WorkFactory(workFindBean);
					i++;
				} else if(mailOrderLink != null){
					String workUrl = mailOrderLink.attr("href");
					workUrl = RequestUtils.decode(workUrl);
					workUrl = RequestUtils.addHostUrl(workUrl);
					workFindBean.setWorkUrl(workUrl);
					workFindBean.setPageType(WorkPageType.MAIL_ORDER);
					workFactories[i] = new WorkFactory(workFindBean);
					i++;
				} else {
					throw new RequestException("不支持找到作品【" + workTitle + "】的详细信息页面", RequestErrorCode.WEB_ANALYTICS_GET);
				}
			}
			if(i != workCount){
				throw new RequestException("获取作品列表任务未添加完，应添加【" + workCount + "】，只添加了【" + i +  " 】", RequestErrorCode.WEB_ANALYTICS_UNMATCH);
			}
			for(WorkFactory workFactory : workFactories){
				factoryManager.addDispatchFactory(workFactory);
			}
		} catch (RequestException e) {
			e.printStackTrace();
		} 
	}

}
