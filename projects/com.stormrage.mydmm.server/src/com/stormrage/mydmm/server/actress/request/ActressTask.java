package com.stormrage.mydmm.server.actress.request;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.request.RequestErrorCode;
import com.stormrage.mydmm.server.request.RequestException;
import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.request.RequestUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.dispatch.IDispatchTask;
import com.stormrage.mydmm.server.workfind.request.WorkFindTaskFactory;

public class ActressTask implements IDispatchTask {

	private final static String JP_LEFT_BRACKET = "（";
	private final static String JP_RIGHT_BRACKET = "）";
	
	private ActressBean actressBean;
	private Set<String> workFindUrlSet = new HashSet<String>(10);
	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	
	public ActressTask(ActressBean actressBean){
		this.actressBean = actressBean;
	}
	
	@Override
	public String getName() {
		return "演员信息获取任务";
	}

	@Override
	public void run() {
		try {
			//更新url
			String url = actressBean.getUrl();
			url = RequestUtils.decode(url);
			actressBean.setUrl(url);
			//打开连接
			Document doc = RequestUtils.getDocument(url);
			Elements tables = doc.select("#mu table");
			//解析出演员的名称
			Element actressNameTable = tables.get(1);
			Element actressNameH = actressNameTable.select("h1").first();
			String nameHtml = actressNameH.html();
			if(nameHtml.contains("（")){
				String name = nameHtml.substring(0, nameHtml.indexOf(JP_LEFT_BRACKET));
				String jpName = nameHtml.substring(nameHtml.indexOf(JP_LEFT_BRACKET) + 1, nameHtml.indexOf(JP_RIGHT_BRACKET));
				actressBean.setName(name);
				actressBean.setJpName(jpName);
			} else {
				actressBean.setName(nameHtml);
			}
			actressBean.setJpName(nameHtml);
			//解析出演员的图片
			Element actressPictureTable = tables.get(2);
			Element actressPicture = actressPictureTable.select("img").first();
			String pictureUrl = actressPicture.attr("src");
			pictureUrl = RequestUtils.decode(pictureUrl);
			actressBean.setPictureUrl(pictureUrl);
			
			//解析出需要分析的作品发现链接
			Element workFindTable = tables.get(12);
			Elements findTds = workFindTable.select("td");
			//获取总页数
			Element headerTd = findTds.get(0);
			Node fullDescNode = headerTd.childNode(0);
			String fullDescStr =  fullDescNode.outerHtml();
			int workCount = ActressUtils.getWorkCount(fullDescStr);
			int pageCapacity = ActressUtils.getPageCapacity(fullDescStr);
			int pageCount = workCount / pageCapacity;
			if(workCount % pageCapacity != 0){
				pageCount = pageCount + 1;
			}
			//获取获取作品的链接
			WorkFindTaskFactory[] workFindFactories= new WorkFindTaskFactory[pageCount];
			url = RequestUtils.addHostUrl(url);
			workFindUrlSet.add(url);
			workFindFactories[0] = new WorkFindTaskFactory(actressBean, url);
			int i = 1;
			Element findTd = findTds.get(1);
			Elements workFindUrlElements = findTd.select("a");
			for(Element workFindUrlElement : workFindUrlElements){
				String workFindUrl = workFindUrlElement.attr("href");
				workFindUrl = RequestUtils.decode(workFindUrl);
				workFindUrl = RequestUtils.addHostUrl(workFindUrl);
				if(!workFindUrlSet.contains(workFindUrl)){
					workFindUrlSet.add(workFindUrl);
					workFindFactories[i] = new WorkFindTaskFactory(actressBean, workFindUrl);
					i++;
				}
			}
			if(i != pageCount){
				throw new RequestException("获取作品列表任务未添加完，应添加【" + pageCount + "】，只添加了【" + i +  " 】", RequestErrorCode.WEB_ANALYTICS_UNMATCH);
			}
			for(WorkFindTaskFactory workFindTaskFactory : workFindFactories){
				factoryManager.addDispatchFactory(workFindTaskFactory);
			}
		} catch (RequestException e) {
			e.printStackTrace();
		} 
	}

}