package com.ting.sysadm.tag;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
/**
 * 

* @ClassName: FormatStringLenTag 

* @Description: 该标签用于格式化 字符串长度

* @author chungen.zhang 

* @date 2013-1-24

*
 */
public class FormatStringBR extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	//格式化的字符串，确保页面输入的 特殊字符都完整  
	private String targetStr;
	@Override
	public int doEndTag() {
		String reStr=this.targetStr;
		if(StringUtils.isNotBlank(reStr)){
			reStr = reStr.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
		}
	try{
		JspWriter writer = pageContext.getOut();
		writer.print(reStr);
	} catch (Exception ex) {
		ex.printStackTrace();
		//log.error(ex.getMessage());		
	}
	return EVAL_PAGE;
	}
	public void setTargetStr(String targetStr) {
		this.targetStr = targetStr;
	}
	//增加get方式，目标值会改变
	public String getTargetStr() {
		return targetStr;
	}
}
