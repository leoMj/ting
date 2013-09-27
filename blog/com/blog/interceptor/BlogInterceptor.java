package com.blog.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class BlogInterceptor extends Validator {

	@Override
	protected void handleError(Controller arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validate(Controller arg0) {
		// TODO Auto-generated method stub
		//validateInteger(field, min, max, errorKey, errorMessage)
	}

}
