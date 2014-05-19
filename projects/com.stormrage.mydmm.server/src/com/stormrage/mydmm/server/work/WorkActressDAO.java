package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.stormrage.mydmm.server.actress.ActressBean;

public class WorkActressDAO {

	public static final String TABLE_NAME = "WORK_ACTRESS";
	public static final String COLUMN_WORK_GUID = "WORK_GUID";
	public static final String COLUMN_ACTRESS_GUID = "ACTRESS_GUID";
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_WORK_GUID + ", " + COLUMN_ACTRESS_GUID + ") VALUES(?,?)";
	
	public static void addWorkActress(Connection conn, String workGuid, ActressBean[] actressBeans) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try{
			for(ActressBean actressBean : actressBeans){
				ps.setString(1, workGuid);
				ps.setString(2, actressBean.getGuid());
				ps.addBatch();
			}
		} finally {
			ps.close();
		}
	}
}
