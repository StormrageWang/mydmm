package com.stormrage.mydmm.server.workdistinct;

import java.sql.Connection;
import java.sql.SQLException;

import com.stormrage.mydmm.server.ConnectionProvider;

public class WorkDistinctUtils {

	
	public static int distinct() throws WorkDistinctException {
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkDistinctDAO.distinct(conn);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new WorkDistinctException("获取作品唯一信息时操作数据库出错", e, WorkDistinctErrorCode.SQL);
		}
	}
	
	
	public static void sleep(int interval) throws WorkDistinctException {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			throw new WorkDistinctException("进程挂起出错", e, WorkDistinctErrorCode.INTERVAL);
		}
	}
}
