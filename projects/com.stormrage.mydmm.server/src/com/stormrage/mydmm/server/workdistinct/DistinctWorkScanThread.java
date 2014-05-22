package com.stormrage.mydmm.server.workdistinct;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.task.TorrentTaskManagerInstance;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;

/**
 * @author StormrageWang
 * @date 2014年5月20日
 */
public class DistinctWorkScanThread extends Thread {

	private static final int INTERVAL = 10000;
	private static Logger logger = LogManager.getLogger();
	
	private boolean running = true;
	private WorkDistinctBean[] workDistinctBeans;
	private DispatchTaskFactoryManager torrntentFactoryManager = TorrentTaskManagerInstance.getInstance();
	
	public DistinctWorkScanThread() {
		super();
		setName("作品番号扫描线程");
	}
	
	@Override
	public void run() {
		while(running){
			logger.info("开始扫描作品番号");
			try{
				updateTopTwenty();
				logger.info("扫描作品番号成功");
				if(running){
					addToTaskManager();
					logger.info("扫描作品番号成功");
				}
			} catch(WorkDistinctException e){
				logger.error("提取作品番号失败：" + e.getMessage(), e , e.getErrorCode());
			}
		}
	}
	
	public void stopRunning(){
		running = false;
	}
	
	private void addToTaskManager(){
		logger.info("开始将需要查询的作品番号添加到任务列表");
		if(workDistinctBeans == null || workDistinctBeans.length == 0){
			logger.warn("没有需要查询的作品番号， 跳过将需要查询的作品番号添加到任务列表");
		}
		for(WorkDistinctBean workDistinctBean : workDistinctBeans){
			String workCode = workDistinctBean.getCode();
			torrntentFactoryManager.addDispatchFactory(new TorrentFindTaskFactory(workCode));
		}
		logger.info("将需要查询的作品番号添加到任务列表成功");
	}
	
	private void updateTopTwenty() throws WorkDistinctException {
		logger.info("开始更新需要查询的作品番号");
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				workDistinctBeans = WorkDistinctDAO.getTopTwenty(conn);
				if(workDistinctBeans.length == 0){
					WorkDistinctDAO.unLockAll(conn);
				} else {
					//WorkDistinctDAO.lockTopTwenty(conn);
				}
				logger.info("更新需要查询的作品番号成功");
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new WorkDistinctException("新需要查询的作品番号时操作数据库出错", e, WorkDistinctErrorCode.SQL);
		}
	}
}
