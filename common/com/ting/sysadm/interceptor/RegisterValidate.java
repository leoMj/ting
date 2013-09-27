package com.ting.sysadm.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.ext.render.CaptchaRender;
import com.jfinal.kit.StringKit;
import com.jfinal.validate.Validator;
import com.ting.sysadm.config.SysConstant;
import com.ting.sysadm.model.User;

public class RegisterValidate extends Validator {

	@Override
	protected void handleError(Controller c) {
		c.keepModel(User.class);
		c.renderJsp("/WEB-INF/jsps/register.jsp");
	}

	/* 注册字段的验证,统一返回一个错误key */
	@Override
	protected void validate(Controller c) {
		// addError("registerError", "系统暂停用户注册，请联系邮箱（flyed@126.com）。。。。");
		try {
			validateRegex("user.loginName", "^[a-zA-Z][a-zA-Z0-9_]*$", "registerError", "用户登陆名必需用英文字母、数字或下划线");
			String loginName = c.getPara("user.loginName");
			if (loginName.length() < 4 || loginName.length() > 12) {
				addError("registerError", "用户名称4-12位合法字符");
				return;
			}

			validateEmail("user.email", "registerError", "邮箱格式不正确");
			validateRequired("user.password", "registerError", "密码不能为空");
			validateRequired("password", "registerError", "重复密码不能为空");

			String email = c.getPara("user.email");

			String code = c.getPara("code");
			code = code.toUpperCase();
			boolean isSucc = CaptchaRender.validate(c, code, SysConstant.RANDOM_CODE_KEY);
			if (!isSucc) {
				addError("registerError", "验证码错误");
				return;
			}

			String repassword = c.getPara("password");
			String password = c.getPara("user.password");
			if (StringKit.notBlank(repassword) && StringKit.notBlank(password) && !repassword.equals(password)) {
				addError("registerError", "两次密码不一致");
				return;
			}

			if (StringKit.notBlank(loginName) && !User.dao.isUniqueLogin(loginName)) {
				addError("registerError", "用户名已被注册");
				return;
			}
			if (StringKit.notBlank(email) && !User.dao.isUniqueEmail(email)) {
				addError("registerError", "邮箱已被注册");
				return;
			}
			validateToken("registerToken", "registerError", "上次已保存，请不要重复提交");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			addError("registerError", e.getMessage());
		}
	}
}
