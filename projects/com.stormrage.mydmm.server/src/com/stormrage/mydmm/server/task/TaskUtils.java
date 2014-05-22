package com.stormrage.mydmm.server.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * 任务工具类
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class TaskUtils {

	/**
	 * 编码
	 */
	private static final String ENCODE = "utf-8";
	/**
	 * 客户端类型
	 */
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36";
	/**
	 * 超时时间
	 */
	private static final int TIMEOUT = 50000;
	/**
	 * 
	 */
	private static final String DMM_HOST_URL = "http://www.unblockdmm.com";
	private static final String PRIFIX_DRIECT = "/";
	private static final int DEFAULT_RETRY_COUNT = 3;
	private static Logger logger = LogManager.getLogger();
	
	/**
	 * 解码
	 * @param url
	 * @return
	 * @throws TaskException
	 */
	public static String decode(String url) throws TaskException {
		try {
			return URLDecoder.decode(url, ENCODE);
		} catch (UnsupportedEncodingException e) {
			throw new TaskException ("请求【" + url + "】解码错误", e.fillInStackTrace(), TaskErrorCode.TASK_REQUEST_ENCODE);
		}
	}
	
	/**
	 * 解码
	 * @param url
	 * @return
	 * @throws TaskException
	 */
	public static String addHostUrl(String url) throws TaskException {
		if(url.startsWith(PRIFIX_DRIECT)){
			return DMM_HOST_URL + url;
		}
		if(url.startsWith(DMM_HOST_URL)){
			return url;
		}
		throw new TaskException("请求【" + url + "】无法识别", TaskErrorCode.TASK_ANALYTICS_UNMATCH);
	}
	
	/**
	 * 使用代理
	 * @param ip
	 * @param port
	 */
	public static void useProxy(String ip, String port){
		//使用代理设置
		Properties prop = System.getProperties();     
		//设置http访问要使用的代理服务器的地址     
		prop.setProperty("http.proxyHost", ip);     
		//设置http访问要使用的代理服务器的端口     
		prop.setProperty("http.proxyPort", port);
	}
	
	/**
	 * @param url
	 * @return
	 * @throws TaskException
	 */
	public static Document getDocument(String url) throws TaskException {
		return getDocument(url, DEFAULT_RETRY_COUNT);
	}
	
	/**
	 * @param url
	 * @param totalIndex
	 * @return
	 * @throws TaskException
	 */
	public static Document getDocument(String url, int totalIndex) throws TaskException {
		logger.debug("连接到【" + url + "】，最多进行【" + totalIndex + "】次尝试");
		return getDocumentWithRetry(url, 1, totalIndex);
	}
	
	private static Document getDocumentWithRetry(String url, int currentIndex, int totalIndex) throws TaskException {
		logger.debug("正在进行第【" + currentIndex + "】次网络连接");
		Connection conn = Jsoup.connect(url);
		conn.userAgent(USER_AGENT).timeout(TIMEOUT);
		try {
			Document document = conn.get();
			logger.debug("第【" + currentIndex + "】次网络连接成功");
			return document;
		} catch (IOException e) {
			if(currentIndex < totalIndex){
				logger.debug("第【" + currentIndex + "】次网络连接失败，进行重连");
				return getDocumentWithRetry(url, currentIndex + 1, totalIndex);
			} else {
				throw new TaskException("请求【" + url + "】连接IO错误", e.fillInStackTrace(), TaskErrorCode.TASK_REQUEST_IO);
			}
		}
	}
	
	
	/**
	 * 根据url获取二进制内容
	 * @param url
	 * @return
	 * @throws TaskException
	 */
	public static byte[] getData(String url) throws TaskException{
		byte[] data = new byte[0];
		url = decode(url);
		try{
			URL u = new URL(url);
			try {
				URLConnection conn = u.openConnection();
				try{
					InputStream inputStream = conn.getInputStream();
					try {
						data = readInputStreamToBytes(inputStream); 
					} finally {
						inputStream.close();
					}
					return data;
				} catch (IOException e) {
					throw new TaskException ("请求【" + url + "】IO错误", e.fillInStackTrace(), TaskErrorCode.TASK_REQUEST_IO);
				}
			} catch (IOException e) {
				throw new TaskException ("请求【" + url + "】打开连接错误", e.fillInStackTrace(), TaskErrorCode.TASK_REQUEST_IO);
			}
		} catch (MalformedURLException e) {
			throw new TaskException ("请求【" + url + "】格式错误", e.fillInStackTrace(), TaskErrorCode.TASK_REQUEST_EFORMAT);
		}
	}
	
	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStreamToBytes(InputStream inputStream)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try{
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			return outputStream.toByteArray();
		} finally {
			outputStream.close();
		}
	}
}
