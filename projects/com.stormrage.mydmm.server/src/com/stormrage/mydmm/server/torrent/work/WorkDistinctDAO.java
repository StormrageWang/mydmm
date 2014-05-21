package com.stormrage.mydmm.server.torrent.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stormrage.mydmm.server.work.WorkActressType;

public class WorkDistinctDAO {
	
	private static final String SQL_DISTINCT = 
			"INSERT INTO WORK_DISTINCT (CODE, TITLE, ACTRESS_TYPE, [DATE], LOCK) " + 
			"SELECT T3.CODE, T3.TITLE, T3.ACTRESS_TYPE, T3.[DATE], 0  FROM WORK T3 \n" + 
				"WHERE EXISTS(SELECT 1 FROM \n" + 
				"	(SELECT MIN(GUID) AS [GUID] FROM \n" + 
				"		(SELECT T1.GUID, T1.CODE FROM WORK T1 \n" + 
				"			WHERE NOT EXISTS(SELECT 1 FROM WORK_DISTINCT T2 WHERE T1.CODE = T2.CODE)) T3 \n" + 
				"				GROUP BY T3.CODE) T4 WHERE T4.GUID = T3.GUID)" ;
	
	private static final String SQL_GET_TOP_TWENTY = 
			"SELECT TOP 20 T1.CODE, T1.TITLE, T1.ACTRESS_TYPE, T1.[DATE] FROM WORK_DISTINCT T1 \n" + 
			"	WHERE T1.LOCK = 0 AND NOT EXISTS (SELECT 1 FROM TORRENT T2 WHERE T1.CODE = T2.WORK_CODE) \n" + 
			"	ORDER BY T1.ACTRESS_TYPE ";
	
	private static final String SQL_LOCK_TOP_TWENTY = 
			"UPDATE WORK_DISTINCT SET LOCK = 0 \n" +  
			"WHERE CODE IN \n" + 
			"(SELECT TOP 20 T1.CODE FROM WORK_DISTINCT T1 \n" + 
			"	WHERE T1.LOCK = 0 AND NOT EXISTS (SELECT 1 FROM TORRENT T2 WHERE T1.CODE = T2.WORK_CODE) \n" + 
			"	ORDER BY T1.ACTRESS_TYPE)";
	
	private static final String SQL_UNLOCK_BY_CODE = 
			"UPDATE WORK_DISTINCT SET LOCK = 0 WHERE CODE = ?";
	
	private static final String SQL_UNLOCK_ALL =
			"UPDATE WORK_DISTINCT SET LOCK = 0 ";
	
	private static WorkDistinctBean readResultSet(ResultSet rs) throws SQLException {
		WorkDistinctBean bean = new WorkDistinctBean();
		bean.setCode(rs.getString(1));
		bean.setTitle(rs.getString(2));
		bean.setActressType(WorkActressType.valueof(rs.getInt(3)));
		return bean;
	}
	
	
	public static int unLockAll(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_UNLOCK_ALL);
		try {
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static void unlockByCode(Connection conn, String code) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_UNLOCK_BY_CODE);
		try{
			ps.setString(1, code);
			ps.executeUpdate();
		} finally{
			ps.close();
		}
	}
	
	public static int lockTopTwenty(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_LOCK_TOP_TWENTY);
		try {
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	public static WorkDistinctBean[] getTopTwenty(Connection conn) throws SQLException {
		List<WorkDistinctBean> beans = new ArrayList<WorkDistinctBean>();
		PreparedStatement ps = conn.prepareStatement(SQL_GET_TOP_TWENTY);
		try{
			ResultSet rs = ps.executeQuery();
			try{
				while(rs.next()){
					beans.add(readResultSet(rs));
				}
				return beans.toArray(new WorkDistinctBean[0]);
			} finally {
				rs.close();
			}
		} finally{
			ps.close();
		}
	}
	
	public static int distinct(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(SQL_DISTINCT);
		try {
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	
}
