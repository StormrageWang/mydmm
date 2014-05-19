package com.stormrage.mydmm.server.picture;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PictureDAO {

	public static final String TABLE_NAME = "PICTURE";
	public static final String COLUMN_GUID = "GUID";
	public static final String COLUMN_TYPE = "TYPE";
	public static final String COLUMN_URL = "URL";
	public static final String COLUMN_DATA = "DATA";
	
	private static final String SQL_INSERT_INFO = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_GUID + ", " + COLUMN_TYPE + ", " + COLUMN_URL + ") VALUES(?,?,?)";

	private static final String SQL_UPDATE_DATA_BY_GUID = 
			"UPDATE " + TABLE_NAME + " SET " + COLUMN_DATA + " = ? WHERE " + COLUMN_GUID + " = ?";
	
	private static final String SQL_GET_INFO_BY_GUID = 
			"SELECT " + COLUMN_GUID + ", " + COLUMN_TYPE  + ", " + COLUMN_URL + " FROM " + TABLE_NAME + " WHERE " + COLUMN_GUID + " =?"; 
	
	private static PictureBean readResultSet(ResultSet rs) throws SQLException {
		PictureBean pictureBean = new PictureBean();
		pictureBean.setGuid(rs.getString(1));
		pictureBean.setType(PictureType.valueof(rs.getInt(2)));
		pictureBean.setUrl(rs.getString(3));
		return pictureBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, PictureBean pictureBean) throws SQLException{
		ps.setString(1, pictureBean.getGuid());
		ps.setInt(2, pictureBean.getType().getIndex());
		ps.setString(3, pictureBean.getUrl());
	}
	
	/**
	 * 批量添加图片
	 * @param conn
	 * @param pictureBeans
	 * @throws SQLException
	 */
	public static void addPictures(Connection conn, PictureBean[] pictureBeans) throws SQLException{
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT_INFO);
		try {
			for(PictureBean pictureBean : pictureBeans){
				writePreparedStatement(ps, pictureBean);
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			ps.close();
		}
	}
	
	/**
	 * 更新图片数据
	 * @param conn
	 * @param guid
	 * @param input
	 * @throws SQLException
	 */
	public static void updateDataByGuid(Connection conn, String guid, InputStream input) throws SQLException{
		PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_DATA_BY_GUID);
		try {
			ps.setBinaryStream(1, input);
			ps.setString(2, guid);
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static PictureBean getByGuid(Connection conn, String guid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_GET_INFO_BY_GUID);
		try{
			ps.setString(1, guid);
			ResultSet rs = ps.executeQuery();
			try{
				if(rs.next()){
					PictureBean pictureBean = readResultSet(rs);
					return pictureBean;
				}
				return null;
			} finally {
				rs.close();
			}
		} finally{
			
		}
	}
	
}
