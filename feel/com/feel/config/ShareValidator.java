package com.feel.config;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.validate.Validator;

public class ShareValidator extends Validator {

	@Override
	protected void handleError(Controller action) {
		// TODO Auto-generated method stub
		action.renderText("2");
	}

	@Override
	protected void validate(Controller action) {
		validateRequiredString("share.content", "contentError", "发布内容不能空");
		String content = action.getPara("share.content");
		int c = StringUtils.length(content);
		System.out.println(c+"=====================");
		if(StringUtils.length(content)>153){
			addError("contentError", "长度限制，不能超过150");
		}
	}

}
