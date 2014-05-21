package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.stormrage.mydmm.server.actress.ActressBean;

public class WorkActressDAO {

	public static final String TABLE_NAME = "WORK_ACTRESS";
	
	public static final String COLUMN_WORK_CODE = "WORK_CODE";
	public static final String COLUMN_ACTRESS_NAME = "ACTRESS_NAME";
	
	private static final String COLUMNS = COLUMN_WORK_CODE + ", " + COLUMN_ACTRESS_NAME;
			
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMNS + ") VALUES(?,?)";
	
	private static final String SQL_IS_WORK_ACTRESS_EXIST = 
			"SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_ACTRESS_NAME + " = ? AND " + COLUMN_ACTRESS_NAME + " = ?";
	
	public static void addWorkActress(Connection conn, String workCode, ActressBean[] actressBeans) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try{
			for(ActressBean actressBean : actressBeans){
				ps.setString(1, workCode);
				ps.setString(2, actressBean.getName());
				ps.addBatch();
			}
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static boolean existWorkActress(Connection conn, String workCode, String actressName) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_IS_WORK_ACTRESS_EXIST);
		try{
			ps.setString(1, workCode);
			ps.setString(2, actressName);
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
			ps.close();
		}
	}
	
}
