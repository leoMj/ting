package com.ting.sysadm.interceptor;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class CharsetFilterHandler extends Handler {
	private String defaultEncode = "UTF-8";
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		// TODO Auto-generated method stub
		try {
			request.setCharacterEncoding(defaultEncode);
			response.setCharacterEncoding(defaultEncode);
			Handler h = this.nextHandler;
			h.handle(target, request, response, isHandled);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
