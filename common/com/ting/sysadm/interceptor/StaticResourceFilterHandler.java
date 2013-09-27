package com.ting.sysadm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.ting.sysadm.config.SysConstant;

public class StaticResourceFilterHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (!target.startsWith(SysConstant.STATICPATHCSS) && !target.startsWith(SysConstant.STATICPATHJS) && !target.startsWith(SysConstant.STATICPATHDATA)&&!target.startsWith(SysConstant.STATICPATHPLUGINS)&&!target.startsWith(SysConstant.STATICPATHIMAGES)) {
			System.out.println("controller 地址："+target);
			this.nextHandler.handle(target, request, response, isHandled);
		}else
			System.out.println("过滤静态资源地址："+target);
	}
}
