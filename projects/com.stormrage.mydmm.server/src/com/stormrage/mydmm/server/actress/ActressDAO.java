package com.stormrage.mydmm.server.actress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ActressDAO {

	public static final String TABLE_NAME = "ACTRESS";
	public static final String COLUMN_GUID = "GUID";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_NAME_FULL = "NAME_FULL";
	public static final String COLUMN_PICTURE_GUID = "PICTURE_GUID";
	public static final String COLUMN_URL = "URL";
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_GUID + ", "  + COLUMN_NAME + ", " + 
					COLUMN_NAME_FULL + ", " + COLUMN_PICTURE_GUID + ", " + COLUMN_URL + ") VALUES (?,?,?,?,?)"; 

	private static final String SQL_GET_BY_GUID = 
			"SELECT " + COLUMN_GUID + ", "  + COLUMN_NAME + ", " + COLUMN_NAME_FULL + ", " + COLUMN_PICTURE_GUID + ", " + COLUMN_URL + 
					" FROM " + TABLE_NAME + " WHERE " + COLUMN_GUID  + " =?";
	
	private static final String SQL_GET_BY_NAME = 
			"SELECT " + COLUMN_GUID + ", "  + COLUMN_NAME + ", " + COLUMN_NAME_FULL + ", " + COLUMN_PICTURE_GUID + ", " + COLUMN_URL + 
					" FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME  + " =?";
	
	private static ActressBean readResultSet(ResultSet rs) throws SQLException {
		ActressBean actressBean = new ActressBean();
		actressBean.setGuid(rs.getString(1));
		actressBean.setName(rs.getString(2));
		actressBean.setFullName(rs.getString(3));
		actressBean.setPictureGuid(rs.getString(4));
		actressBean.setUrl(rs.getString(5));
		return actressBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, ActressBean actressBean) throws SQLException{
		ps.setString(1, actressBean.getGuid());
		ps.setString(2, actressBean.getName());
		ps.setString(3, actressBean.getFullName());
		ps.setString(4, actressBean.getPictureGuid());
		ps.setString(5, actressBean.getUrl());
	}
	
	/**
	 * 添加演员
	 * @param conn
	 * @param actressBean
	 * @throws SQLException
	 */
	public static void addActress(Connection conn, ActressBean actressBean) throws SQLException{
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try {
			writePreparedStatement(ps, actressBean);
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	/**
	 * 根据guid获取演员信息
	 * @param conn
	 * @param guid
	 * @return
	 * @throws SQLException
	 */
	public static ActressBean getByGuid(Connection conn, String guid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_GUID);
		try{
			ps.setString(1, guid);
			ResultSet rs = ps.executeQuery();
			try{
				if(rs.next()){
					ActressBean actressBean = readResultSet(rs);
					return actressBean;
				}
				return null;
			} finally {
				rs.close();
			}
		} finally{
			
		}
	}
	
	/**
	 * 根据名称获取演员信息
	 * @param conn
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public static ActressBean getByName(Connection conn, String name) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_GET_BY_NAME);
		try{
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			try{
				if(rs.next()){
					ActressBean actressBean = readResultSet(rs);
					return actressBean;
				}
				return null;
			} finally {
				rs.close();
			}
		} finally{
			
		}
	}
	
}
