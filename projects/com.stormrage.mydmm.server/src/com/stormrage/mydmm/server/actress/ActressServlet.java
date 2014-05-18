package com.stormrage.mydmm.server.actress;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stormrage.mydmm.server.actress.request.ActressTaskFactory;
import com.stormrage.mydmm.server.request.RequestFactoryManagerInstance;
import com.stormrage.mydmm.server.task.dispatch.DispatchTaskFactoryManager;
import com.stormrage.mydmm.server.utils.StringUtils;

/**
 * 演员信息相关的servlet服务
 * @author StormrageWang
 * @date 2014年5月18日 
 */
public class ActressServlet extends HttpServlet {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 6963539265640976779L;
	private static final String PARAMETER_NAME_ACTREE = "actressUrl";
	private DispatchTaskFactoryManager factoryManager = RequestFactoryManagerInstance.getInstance();
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//第一个演员
		String actressUrl = req.getParameter(PARAMETER_NAME_ACTREE);
		if(StringUtils.isEmpty(actressUrl)){
			return;
		}
		ActressTaskFactory actressFactory = new ActressTaskFactory(actressUrl);
		factoryManager.addDispatchFactory(actressFactory);
		
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
	
	
	
}
