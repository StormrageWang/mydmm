package com.stormrage.mydmm.server.torrent.work;

import java.sql.Connection;
import java.sql.SQLException;

import com.stormrage.mydmm.server.ConnectionProvider;
import com.stormrage.mydmm.server.torrent.TorrentErrorCode;
import com.stormrage.mydmm.server.torrent.TorrentException;

public class WorkDistinctUtils {

	
	public static int distinct() throws TorrentException{
		try{
			Connection conn = ConnectionProvider.getInstance().open();
			try{
				return WorkDistinctDAO.distinct(conn);
			} finally {
				conn.close();
			}
		} catch(SQLException e) {
			throw new TorrentException("获取作品唯一信息时，操作数据库出错", e, TorrentErrorCode.SQL);
		}
	}
}
