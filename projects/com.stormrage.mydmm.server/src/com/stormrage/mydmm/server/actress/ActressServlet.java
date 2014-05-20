package com.stormrage.mydmm.server.actress;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stormrage.mydmm.server.actress.task.ActressTaskFactory;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskFactoryManagerInstance;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.task.status.ITaskFinishListener;
import com.stormrage.mydmm.server.task.status.TaskStatusManager;
import com.stormrage.mydmm.server.torrent.TorrentException;
import com.stormrage.mydmm.server.torrent.work.WorkDistinctUtils;
import com.stormrage.mydmm.server.utils.StringUtils;

/**
 * 演员信息相关的servlet服务
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressServlet extends HttpServlet {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 6963539265640976779L;
	private static final String PARAMETER_NAME_ACTREE = "actressUrl";
	private DispatchTaskFactoryManager factoryManager = TaskFactoryManagerInstance.getInstance();
	private static Logger logger = LogManager.getLogger();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//第一个演员
		String actressUrl = req.getParameter(PARAMETER_NAME_ACTREE);
		if(StringUtils.isEmpty(actressUrl)){
			return;
		}
		try {
			actressUrl = TaskUtils.decode(actressUrl);
			TaskStatusManager statusManager = new TaskStatusManager("获取演员所有信息", 1);
			statusManager.setFinishListener(new ITaskFinishListener() {
				
				@Override
				public void finish() {
					logger.info("所有演员信息获取完成");
					try {
						logger.info("开始获取作品唯一信息");
						int workCount = WorkDistinctUtils.distinct();
						logger.info("获取作品唯一信息成功，共获取【" + workCount + "】个作品");
					} catch (TorrentException e) {
						logger.info("获取作品唯一信息失败：" + e.getMessage(), e, e.getErrorCode());
					}
				}
			});
			ActressTaskFactory actressFactory = new ActressTaskFactory(actressUrl);
			factoryManager.addDispatchFactory(actressFactory);
			statusManager.addStatusProvider(actressFactory);
		} catch (TaskException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
	
	
	
}
