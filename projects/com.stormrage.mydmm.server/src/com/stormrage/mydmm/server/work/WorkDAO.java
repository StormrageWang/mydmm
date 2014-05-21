package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class WorkDAO {

	private static final String TABLE_NAME = "WORK";
	
	private static final String COLUMN_CODE = "CODE";
	private static final String COLUMN_CODE_SIMPLE= "CODE_SIMPLE";
	private static final String COLUMN_TITLE = "TITLE";
	private static final String COLUMN_TITLE_CH = "TITLE_CH";
	private static final String COLUMN_DATE = "DATE";
	private static final String COLUMN_TIME_LENGTH = "TIME_LENGTH";
	private static final String COLUMN_ACTRESS_TYPE = "ACTRESS_TYPE";
	private static final String COLUMN_URL = "URL";
	
	private static final String COLUMNS = COLUMN_CODE + ", " + COLUMN_CODE_SIMPLE + ", " + COLUMN_TITLE + ", " + COLUMN_TITLE_CH + ", " + 
			COLUMN_DATE + ", " + COLUMN_TIME_LENGTH + ", " + COLUMN_ACTRESS_TYPE + ", " + COLUMN_URL;
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMNS + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_GET_BY_TITLE = 
			"SELECT "+ COLUMNS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
	
	private static final String SQL_IF_TITLE_EXIST = 
			"SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = ?";
	
	private static WorkBean readResultSet(ResultSet rs) throws SQLException {
		WorkBean workBean = new WorkBean();
		workBean.setCode(rs.getString(1));
		workBean.setSimpleCode(rs.getString(2));
		workBean.setTitle(rs.getString(3));
		workBean.setChTitle(rs.getString(4));
		workBean.setDate(rs.getDate(5));
		workBean.setTimeLength(rs.getInt(6));
		workBean.setActressType(WorkActressType.valueof(rs.getInt(7)));
		workBean.setUrl(rs.getString(7));
		return workBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, WorkBean workBean) throws SQLException{
		ps.setString(1, workBean.getCode());
		ps.setString(2, workBean.getSimpleCode());
		ps.setString(3, workBean.getTitle());
		ps.setString(4, workBean.getChTitle());
		Date date = workBean.getDate();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		ps.setDate(5, sqlDate);
		ps.setInt(6, workBean.getTimeLength());
		ps.setInt(7, workBean.getActressType().getIndex());
		ps.setString(8, workBean.getUrl());
		
	}
	
	public static void addWork(Connection conn, WorkBean workBean) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try {
			writePreparedStatement(ps, workBean);
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static boolean existTitle(Connection conn, String title) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_IF_TITLE_EXIST);
		try{
			ps.setString(1, title);
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
	
	public static WorkBean getByTitle(Connection conn, String title) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_TITLE);
		try{
			ps.setString(1, title);
			ResultSet rs = ps.executeQuery();
			try{
				if(rs.next()){
					WorkBean workBean = readResultSet(rs);
					return workBean;
				}
				return null;
			} finally {
				rs.close();
			}
		} finally{
			ps.close();
		}
	}
	
}
