package com.stormrage.mydmm.server.actress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActressPictureDAO {

	private static final String TABLE_NAME = "ACTRESS_PICTURE";
	
	private static final String COLUMN_GUID = "GUID";
	private static final String COLUMN_ACTRESS_NAME = "NAME";
	private static final String COLUMN_URL = "URL";
	private static final String COLUMN_DATA = "DATA";
	
	private static final String COLUMNS = COLUMN_GUID + ", " +  COLUMN_ACTRESS_NAME + ", " + COLUMN_URL;
	
	private static final String SQL_INSERT_INFO = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMNS + ") VALUES (?, ?, ?)"; 
	
	
	private static ActressPictureBean readResultSet(ResultSet rs) throws SQLException {
		ActressPictureBean pictureBean = new ActressPictureBean();
		pictureBean.setGuid(rs.getString(1));
		pictureBean.setActressName(rs.getString(2));
		pictureBean.setUrl(rs.getString(3));
		return pictureBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, ActressPictureBean pictureBean) throws SQLException{
		ps.setString(1, pictureBean.getGuid());
		ps.setString(2, pictureBean.getActressName());
		ps.setString(3, pictureBean.getUrl());
	}
	
	public static void addPictures(Connection conn, ActressPictureBean[] pictureBeans) throws SQLException{
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT_INFO);
		try {
			for(ActressPictureBean pictureBean : pictureBeans){
				writePreparedStatement(ps, pictureBean);
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			ps.close();
		}
	}
	
}
