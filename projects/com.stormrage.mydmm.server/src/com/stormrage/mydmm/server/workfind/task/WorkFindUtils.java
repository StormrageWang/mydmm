package com.stormrage.mydmm.server.workfind.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.actress.ActressBean;
import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.work.WorkActressDAO;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.WorkDAO;
import com.stormrage.mydmm.server.work.task.WorkTaskFactory;

public class WorkFindUtils {

	public static WorkTaskFactory[] getWorkFactorys(Document doc) throws TaskException {
		Element workTable = doc.select("#mu table").get(13);
		Elements workTrs = workTable.select("tr");
		Iterator<Element> workTrIterator = workTrs.iterator();
		//跳过第一行
		workTrIterator.next();
		int workCount = workTrs.size() - 1;
		int currentCount = 0;
		WorkTaskFactory[] workFactories = new WorkTaskFactory[workCount];
		while(workTrIterator.hasNext()){
			Element workTr = workTrIterator.next();
			Element titleLink = workTr.child(0).select("a").first();
			String workTitle = titleLink.html();
			Element workLink = null;
			for(int i = 1; i <= 6; i++){
				workLink = workTr.child(i).select("a").first();
				if(workLink != null){
					break;
				}
			}
			if(workLink == null){
				throw new TaskException("解析作品链接失败，没有找到第【" + currentCount  +"】行的作品【" + workTitle +"】的详细信息链接", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
			}
			String workUrl = workLink.attr("href");
			workUrl = TaskUtils.decode(workUrl);
			workUrl = TaskUtils.addHostUrl(workUrl);
			workFactories[currentCount] = new WorkTaskFactory(null, workTitle, currentCount, workUrl);
			currentCount++;
		}
		if(currentCount != workCount){
			throw new TaskException("解析作品链接失败，应解析出【" + workCount + "】，只解析出了【" + currentCount +  " 】", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
		}
		return workFactories;
	}
	
	public static WorkBean getWorkBeanByTitle(String workTitle) throws TaskException{
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkDAO.getByTitle(conn, workTitle);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("判断作品【" + workTitle + "】是否存在时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	public static void saveWorkToActress(String actressName, String workTitle) throws TaskException{
		ActressBean actressBean = new ActressBean();
		actressBean.setName(actressName);
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				WorkActressDAO.addWorkActress(conn, workTitle, new ActressBean[]{actressBean});
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("添加作品【" + workTitle + "】到演员【" + actressName + "】的作品中时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	public static boolean existWorkActress(String actressName, String workTitle) throws TaskException {
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkActressDAO.existWorkActress(conn, workTitle, actressName);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TaskException("判断【" + workTitle + "】是否在演员【" + actressName + "】的作品中时操作数据库出错", e, TaskErrorCode.TASK_ANALYTICS_DATABASE);
		}
	}
	
	public static void main(String[] args) {
		//使用代理设置
		TaskUtils.useProxy("127.0.0.1", "8087");
		try {
			Document doc = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http://actress.dmm.co.jp/-/detail/=/actress_id=21597/search=one/page=1/&b=0");
			WorkTaskFactory[] factories = getWorkFactorys(doc);
			System.out.println(factories.length);
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}
	
}
