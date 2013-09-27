package com.blog.interceptor;

import com.blog.model.Blog;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class BlogValidator extends Validator {

	@Override
	protected void handleError(Controller c) {
		c.keepModel(Blog.class);
		c.render("admin/add.jsp");
	}

	@Override
	protected void validate(Controller c) {
		//validateToken("blogToken", "msg", "上次已保存，请不要重复提交");
		//validateToken("blogToken","blogTokenError","重复提交失败");
		validateString("blog.title",true,1,50,"articleTitle","请填写标题,最大不超过50个字");
		validateString("blog.content",true,1,50000,"articleContent","请填写内容，最大不超过50000个字");
		validateString("blog.description",true,1,500,"articleDescription","请做简短描述，最大不超过500个字");
	}

}
