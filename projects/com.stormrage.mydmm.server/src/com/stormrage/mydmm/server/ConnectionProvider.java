package com.stormrage.mydmm.server;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class ConnectionProvider {

	private static final String JNDI_DATASOURCE_NAME = "MYDMM";
	boolean hasInit = false;
	private DataSource ds = null;
	private static ConnectionProvider provider = new ConnectionProvider();

	public ConnectionProvider()  {

	}

	protected synchronized void initDataSource() throws NamingException {
		if (hasInit == false) {
			Context initCtx = new InitialContext();
			Object o = initCtx.lookup("java:comp/env/" + JNDI_DATASOURCE_NAME);
			ds = (DataSource) o;
			hasInit = true;
		}
	}

	public static ConnectionProvider getInstance() {
		return provider;
	}

	public Connection open() throws SQLException {
		if(!hasInit){
			throw new SQLException("数据源未初始化，无法获取数据连接");
		} else {
			if(ds == null){
				throw new SQLException("数据源初始化时失败，无法获取数据连接");
			}
			return ds.getConnection();
		}
	}
}
