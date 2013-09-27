package com.ting.sysadm.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.ext.render.CaptchaRender;
import com.jfinal.validate.Validator;
import com.ting.sysadm.config.SysConstant;
import com.ting.sysadm.model.User;

public class LoginValidate extends Validator {

	@Override
	protected void handleError(Controller c) {
		c.keepPara("user.loginName");
		c.renderJsp("/WEB-INF/jsps/login.jsp");
	}

	@Override
	protected void validate(Controller c) {
		//验证 验证码是否正确
		String code = c.getPara("code");
		code = code.toUpperCase();
		//System.out.println(encrypt(code)+"="+c.getCookie(SysConstant.RANDOM_CODE_KEY));
		boolean isSucc = CaptchaRender.validate(c, code, SysConstant.RANDOM_CODE_KEY);
		if(!isSucc){
			addError("codeError", "验证码错误");
			return;
		}
		validateRequiredString("loginName", "loginNameError", "用户名不能为空");
		validateRequiredString("password", "passwordError", "密码不能为空");
		//以下是业务逻辑的验证， 本应在login的action中验证，与字段物理验证区分开，在此为方便一起验证
		String loginName = c.getPara("loginName");
		String password = c.getPara("password");
		User user = User.dao.login(loginName, password);
		if(user==null){
			addError("loginError", "用户名或密码错误");
			return;
		}
		c.setSessionAttr("user", user);
	}
}
