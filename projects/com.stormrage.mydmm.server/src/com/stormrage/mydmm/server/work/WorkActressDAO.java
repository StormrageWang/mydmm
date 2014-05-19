package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.stormrage.mydmm.server.actress.ActressBean;

public class WorkActressDAO {

	public static final String TABLE_NAME = "WORK_ACTRESS";
	public static final String COLUMN_WORK_GUID = "WORK_GUID";
	public static final String COLUMN_ACTRESS_GUID = "ACTRESS_GUID";
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_WORK_GUID + ", " + COLUMN_ACTRESS_GUID + ") VALUES(?,?)";
	
	private static final String SQL_IS_WORK_ACTRESS_EXIST = 
			"SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_WORK_GUID + " = ? AND " + COLUMN_ACTRESS_GUID + " = ?";
	
	public static void addWorkActress(Connection conn, String workGuid, ActressBean[] actressBeans) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try{
			for(ActressBean actressBean : actressBeans){
				ps.setString(1, workGuid);
				ps.setString(2, actressBean.getGuid());
				ps.addBatch();
			}
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static boolean existWorkActress(Connection conn, String workGuid, String actressGuid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_IS_WORK_ACTRESS_EXIST);
		try{
			ps.setString(1, workGuid);
			ps.setString(2, actressGuid);
			ResultSet rs = ps.executeQuery();
			try{
				if(rs.next()){
					return true;
				}
				return false;
			} finally {
				rs.close();
			}
		} finally{
			
		}
	}
	
}
