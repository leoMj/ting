package com.ting.sysadm.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class BlogValidate extends Validator{

	@Override
	protected void handleError(Controller arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validate(Controller c) {
		validateToken("blogToken", "msg", "alert('上次已保存，请不要重复提交')");
		
	}
	
}
