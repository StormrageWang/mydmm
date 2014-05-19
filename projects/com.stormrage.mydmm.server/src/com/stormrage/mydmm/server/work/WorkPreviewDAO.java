package com.stormrage.mydmm.server.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.stormrage.mydmm.server.picture.PictureBean;

public class WorkPreviewDAO {

	public static final String TABLE_NAME = "WORK_PREVIEW";
	public static final String COLUMN_WORK_GUID = "WORK_GUID";
	public static final String COLUMN_PICTURE_GUID = "PICTURE_GUID";
	
	private static final String SQL_INSERT = 
			"INSERT INTO " + TABLE_NAME + "(" + COLUMN_WORK_GUID + ", " + COLUMN_PICTURE_GUID + ") VALUES(?,?)";
	
	public static void addWorkPreviews(Connection conn, String workGuid, PictureBean[] pictureBeans) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_INSERT);
		try{
			for(PictureBean pictureBean : pictureBeans){
				ps.setString(1, workGuid);
				ps.setString(2, pictureBean.getGuid());
				ps.addBatch();
			}
		} finally {
			ps.close();
		}
	}
}
