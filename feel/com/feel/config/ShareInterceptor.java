package com.feel.config;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

public class ShareInterceptor implements Interceptor {
	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController();
		if (controller.getSessionAttr("user") != null) {
			ai.invoke();
		} else {
			controller.setAttr("msg", "对不起，请您先登录");
			controller.renderJavascript("alert('对不起，请您先登录')");
		}
	}
}
