package com.stormrage.mydmm.server.actress.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.actress.ActressDAO;
import com.stormrage.mydmm.server.actress.ActressPictureBean;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.utils.StringUtils;
import com.stormrage.mydmm.server.workfind.task.WorkFindTaskFactory;


/**
 * 演员相关的工具类
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class ActressUtils {

	private static final String WORK_COUNT_PREFIX = "全";
	private static final String WORK_COUNT_SUFFIX = "件";
	private static final String PAGE_CAPACITY_PREFIX = "&nbsp;";
	private static final String PAGE_CAPACITY_SUFFIX = "件";
	
	private static final String NAME_PREFIX = "&gt;";
	
	private static final String LINK_TITLE_PRE = "前へ";
	private static final String LINK_TITLE_NEXT = "次へ";	
	private static final int PAGE_CAPACITY = 50;
	
	/**
	 * 
	 * 处理 如“全91件中 50件を表示”，返回91
	 * @param fullDescStr
	 * @return
	 * @throws TaskException
	 */
	public static int getTotalWorkCount(String fullDescStr) throws TaskException {
		int prtfixIndex = fullDescStr.indexOf(WORK_COUNT_PREFIX);
		int suffixIndex = fullDescStr.indexOf(WORK_COUNT_SUFFIX, prtfixIndex);
		String countStr = fullDescStr.substring(prtfixIndex + WORK_COUNT_PREFIX.length(), suffixIndex);
		try{
			return Integer.valueOf(countStr);
		} catch(NumberFormatException e){
			throw new TaskException("总作品数【" + countStr + "】无法转为数字", TaskErrorCode.TASK_ANALYTICS_GET);
		}
	}
	
	/**
	 * 
	 * 处理 如“全91件中 50件を表示”，返回50
	 * @param fullDescStr
	 * @return
	 * @throws TaskException
	 */
	public static int getCurrentWorkCount(String fullDescStr) throws TaskException {
		int prtfixIndex = fullDescStr.indexOf(PAGE_CAPACITY_PREFIX);
		int suffixIndex = fullDescStr.indexOf(PAGE_CAPACITY_SUFFIX, prtfixIndex);
		String capacityStr = fullDescStr.substring(prtfixIndex + PAGE_CAPACITY_PREFIX.length(), suffixIndex);
		try{
			return Integer.valueOf(capacityStr);
		} catch(NumberFormatException e){
			throw new TaskException("每页数【" + capacityStr + "】无法转为数字", TaskErrorCode.TASK_ANALYTICS_GET);
		}
	}
	
	public static String getName(Document doc) {
		Element nameTd = doc.select("td.pankuzu").first();
		Node nameNode = nameTd.childNode(nameTd.childNodeSize() - 1);
		String name = nameNode.outerHtml();
		name = name.replace(NAME_PREFIX, "");
		name = StringUtils.trim(name);
		return name;
	}
	
	public static String getFullName(Document doc) {
		Element element = doc.select("h1").first();
		return element.html();
	}
	
	/**
	 * 获取需添加的作品列表链接，最后一个空给当前页
	 * @param doc
	 * @return
	 * @throws TaskException
	 */
	public static WorkFindTaskFactory[] getWorkFindFactories(Document doc) throws TaskException {
		Elements tables = doc.select("#mu table");
		Element workFindTable = tables.get(12);
		Elements findTds = workFindTable.select("td");
		//获取总页数
		Element headerTd = findTds.get(0);
		Node fullDescNode = headerTd.childNode(0);
		String fullDescStr =  fullDescNode.outerHtml();
		int workCount = ActressUtils.getTotalWorkCount(fullDescStr);
		int pageCount = workCount / PAGE_CAPACITY;
		if(workCount % PAGE_CAPACITY != 0){
			pageCount = pageCount + 1;
		}
		//获取获取作品的链接
		WorkFindTaskFactory[] workFindFactories = new WorkFindTaskFactory[pageCount];
		//删除当前页
		pageCount = pageCount - 1;
		int currentCount = 0;
		Element findTd = findTds.get(1);
		Elements workFindUrlElements = findTd.select("a");
		for(Element workFindLink : workFindUrlElements){
			String workFindUrl = workFindLink.attr("href");
			String indexStr = workFindLink.html();
			if(isTextLink(indexStr)){
				continue;
			}
			int index = getIndexByStr(indexStr);
			workFindUrl = TaskUtils.decode(workFindUrl);
			workFindUrl = TaskUtils.addHostUrl(workFindUrl);
			workFindFactories[currentCount] = new WorkFindTaskFactory(null, index, workFindUrl);
			currentCount++;
		}
		if(currentCount != pageCount){
			throw new TaskException("作品列表链接解析错误 ，除去当前列表应添加【" + pageCount + "】个，只添加了【" + currentCount +  "】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
		}
		return workFindFactories;
	}
	
	
	
	private static boolean isTextLink(String linkStr){
		if(LINK_TITLE_PRE.equals(linkStr)
				||LINK_TITLE_NEXT.equals(linkStr)){
			return true;
		}
		return false;
	}
	
	public static int getCurrentIndex(Document doc) throws TaskException {
		Element selectedSpan = doc.select("span.selected").first();
		String currentIndexStr = selectedSpan.html();
		return getIndexByStr(currentIndexStr);
	}
	
	private static int getIndexByStr(String indexStr) throws TaskException {
		try {
			return Integer.valueOf(indexStr);
		} catch (NumberFormatException e){
			throw new TaskException("无法获取作品列表链接的序号", e, TaskErrorCode.TASK_ANALYTICS_GET);
		}
	}
	
	public static ActressPictureBean getCoverPicture(Document doc) throws TaskException {
		//解析出演员的图片
		Elements tables = doc.select("#mu table");
		Element actressPictureTable = tables.get(2);
		Element actressPicture = actressPictureTable.select("img").first();
		String pictureUrl = actressPicture.attr("src");
		pictureUrl = TaskUtils.decode(pictureUrl);
		ActressPictureBean pictureBean = new ActressPictureBean();
		pictureBean.setGuid(Guid.newGuid());
		pictureBean.setUrl(pictureUrl);
		return pictureBean;
	}
	
	
	public static ActressBean getActressBeanByName(String actressName) throws TaskException{
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return ActressDAO.getByName(conn, actressName);
			} finally {
				conn.close();
			}
		}catch(SQLException e){
			throw new TaskException("获取演员【" + actressName + "】信息时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	public static void main(String[] args) {
		//使用代理设置
		TaskUtils.useProxy("127.0.0.1", "8087");
		try {
			Document doc = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http://actress.dmm.co.jp/-/detail/=/actress_id=21597/search=one/page=1/&b=0");
			System.out.println(getName(doc));
			System.out.println(getFullName(doc));
			ActressPictureBean coverBean = getCoverPicture(doc);
			System.out.println(coverBean.getUrl());
			WorkFindTaskFactory[] factories = getWorkFindFactories(doc);
			System.out.println(factories.length);
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}
}
