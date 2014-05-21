package com.stormrage.mydmm.server.actress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ActressDAO {

	private static final String TABLE_NAME = "ACTRESS";
	
	private static final String COLUMN_NAME = "NAME";
	private static final String COLUMN_NAME_FULL = "NAME_FULL";
	private static final String COLUMN_URL = "URL";
	
	private static final String COLUMNS = COLUMN_NAME + ", " + COLUMN_NAME_FULL + ", " + COLUMN_URL;
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMNS + ") VALUES (?, ?, ?)"; 

	private static final String SQL_GET_BY_NAME = 
			"SELECT "  + COLUMNS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME  + " = ?";
	
	
	private static ActressBean readResultSet(ResultSet rs) throws SQLException {
		ActressBean actressBean = new ActressBean();
		actressBean.setName(rs.getString(1));
		actressBean.setFullName(rs.getString(2));
		actressBean.setUrl(rs.getString(3));
		return actressBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, ActressBean actressBean) throws SQLException{
		ps.setString(1, actressBean.getName());
		ps.setString(2, actressBean.getFullName());
		ps.setString(3, actressBean.getUrl());
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
	 * 根据姓名获取演员信息
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
			ps.close();
		}
	}
	
}
