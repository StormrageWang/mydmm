package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.stormrage.mydmm.server.workfind.WorkActressType;
import com.stormrage.mydmm.server.workfind.WorkPageType;


public class WorkDAO {

	public static final String TABLE_NAME = "WORK";
	
	public static final String COLUMN_GUID = "GUID";
	public static final String COLUMN_TITLE = "TITLE";
	public static final String COLUMN_TITLE_FULL = "TITLE_FULL";
	public static final String COLUMN_TITLE_CH = "TITLE_CH";
	public static final String COLUMN_CODE_FULL = "CODE_FULL";
	public static final String COLUMN_CODE = "CODE";
	public static final String COLUMN_DATE = "DATE";
	public static final String COLUMN_TIME_LENGTH = "TIME_LENGTH";
	public static final String COLUMN_COVER_FULL_GUID = "COVER_FULL_GUID";
	public static final String COLUMN_COVER_GUID = "COVER_GUID";
	public static final String COLUMN_URL = "URL";
	public static final String COLUMN_PAGE_TYPE = "PAGE_TYPE";
	public static final String COLUMN_ACTRESS_TYPE = "ACTRESS_TYPE";
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_GUID + ", " + COLUMN_TITLE + ", " + COLUMN_TITLE_FULL + ", " + COLUMN_TITLE_CH + ", " + 
					COLUMN_CODE_FULL + ", " + COLUMN_CODE + ", " + COLUMN_DATE + ", " + COLUMN_TIME_LENGTH + ", " + 
					COLUMN_COVER_FULL_GUID + ", " + COLUMN_COVER_GUID + ", " + 
					COLUMN_URL + ", " + COLUMN_PAGE_TYPE + ", " + COLUMN_ACTRESS_TYPE + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String SQL_GET_BY_TITLE = 
			"SELECT "+ COLUMN_GUID + ", " + COLUMN_TITLE + ", " + COLUMN_TITLE_FULL + ", " + COLUMN_TITLE_CH + ", " + 
					COLUMN_CODE_FULL + ", " + COLUMN_CODE + ", " + COLUMN_DATE + ", " + COLUMN_TIME_LENGTH + ", " + 
					COLUMN_COVER_FULL_GUID + ", " + COLUMN_COVER_GUID + ", " + 
					COLUMN_URL + ", " + COLUMN_PAGE_TYPE + ", " + COLUMN_ACTRESS_TYPE + 
			" FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " =?";
	
	private static final String SQL_IF_TITLE_EXIST = 
			"SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " =?";
	
	private static WorkBean readResultSet(ResultSet rs) throws SQLException {
		WorkBean workBean = new WorkBean();
		workBean.setGuid(rs.getString(1));
		workBean.setTitle(rs.getString(2));
		workBean.setFullTitle(rs.getString(3));
		workBean.setChTitle(rs.getString(4));
		workBean.setFullCode(rs.getString(5));
		workBean.setCode(rs.getString(6));
		workBean.setDate(rs.getDate(7));
		workBean.setTimeLength(rs.getInt(8));
		workBean.setFullCoverGuid(rs.getString(9));
		workBean.setCoverGuid(rs.getString(10));
		workBean.setUrl(rs.getString(11));
		workBean.setPageType(WorkPageType.valueof(rs.getInt(12)));
		workBean.setActressType(WorkActressType.valueof(rs.getInt(13)));
		return workBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, WorkBean workBean) throws SQLException{
		ps.setString(1, workBean.getGuid());
		ps.setString(2, workBean.getTitle());
		ps.setString(3, workBean.getFullTitle());
		ps.setString(4, workBean.getChTitle());
		ps.setString(5, workBean.getFullCode());
		ps.setString(6, workBean.getCode());
		Date date = workBean.getDate();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		ps.setDate(7, sqlDate);
		ps.setInt(8, workBean.getTimeLength());
		ps.setString(9, workBean.getFullCoverGuid());
		ps.setString(10, workBean.getCoverGuid());
		ps.setString(11, workBean.getUrl());
		ps.setInt(12, workBean.getPageType().getIndex());
		ps.setInt(13, workBean.getActressType().getIndex());
		
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
