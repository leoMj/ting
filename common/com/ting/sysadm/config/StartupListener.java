package com.ting.sysadm.config;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 *  StartupListener
 *
 * @author agen
 * @version 
 * @since 
 */
public class StartupListener implements ServletContextListener {

	//private static Logger log = Logger.getLogger(StartupListener.class);

	@Override
    public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		//系统根目录
		servletContext.setAttribute("contextPath", servletContext.getContextPath());
		servletContext.setAttribute("currentDater", new CurrentDater());
    }
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	//EL Expression ${applicationScope.currentDater.currentDate}
	public static class CurrentDater {
		//返回当前时间实例
		public Date getCurrentDate() {
			return new Date();
		}
	}
}