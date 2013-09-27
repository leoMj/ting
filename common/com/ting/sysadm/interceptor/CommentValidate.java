package com.ting.sysadm.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class CommentValidate extends Validator {

	@Override
	protected void handleError(Controller c) {
		// TODO Auto-generated method stub
		c.keepPara("comment.content");
		c.renderText("评论失败");
	}

	@Override
	protected void validate(Controller c) {
		// TODO Auto-generated method stub
		validateRequiredString("comment.content", "commentContentError", "评论内容不能为空");
	}
}
