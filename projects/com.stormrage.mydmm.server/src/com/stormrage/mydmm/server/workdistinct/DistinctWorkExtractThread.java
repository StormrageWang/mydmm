package com.stormrage.mydmm.server.workdistinct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistinctWorkExtractThread extends Thread {
	
	private static final int INTERVAL = 10000;
	private static Logger logger = LogManager.getLogger();
	
	private boolean running = true;
	
	public DistinctWorkExtractThread() {
		super();
		setName("作品番号提取线程");
	}
	
	@Override
	public void run() {
		while(running){
			logger.info("开始提取作品番号");
			try{
				WorkDistinctUtils.distinct();
				logger.info("提取作品番号成功");
				if(running){
					WorkDistinctUtils.sleep(INTERVAL);
				}
			} catch(WorkDistinctException e){
				logger.error("提取作品番号失败：" + e.getMessage(), e , e.getErrorCode());
			}
		}
	}
	
	public void stopRunning(){
		running = false;
	}
}
