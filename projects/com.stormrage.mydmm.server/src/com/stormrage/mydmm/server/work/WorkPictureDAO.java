package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkPictureDAO {

	private static final String TABLE_NAME = "WORK_PICTURE";
	
	private static final String COLUMN_GUID = "GUID";
	private static final String COLUMN_WORK_CODE = "WORK_CODE";
	private static final String COLUMN_TYPE = "TYPE";
	private static final String COLUMN_URL = "URL";
	private static final String COLUMN_DATA = "DATA";
	
	private static final String COLUMNS = COLUMN_GUID + ", " +  COLUMN_WORK_CODE + ", " + COLUMN_TYPE + ", " + COLUMN_URL;
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMNS + ") VALUES(?, ?, ?, ?)";
	
	private static WorkPictureBean readResultSet(ResultSet rs) throws SQLException {
		WorkPictureBean pictureBean = new WorkPictureBean();
		pictureBean.setGuid(rs.getString(1));
		pictureBean.setWorkCode(rs.getString(2));
		pictureBean.setType(WorkPictureType.valueof(rs.getInt(3)));
		pictureBean.setUrl(rs.getString(4));
		return pictureBean;
	}
	
	private static void writePreparedStatement(PreparedStatement ps, WorkPictureBean pictureBean) throws SQLException{
		ps.setString(1, pictureBean.getGuid());
		ps.setString(2, pictureBean.getWorkCode());
		ps.setInt(3, pictureBean.getType().getIndex());
		ps.setString(4, pictureBean.getUrl());
	}
	
	public static void addPictures(Connection conn, WorkPictureBean[] pictureBeans) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try{
			for(WorkPictureBean pictureBean : pictureBeans){
				writePreparedStatement(ps, pictureBean);
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			ps.close();
		}
	}
}
