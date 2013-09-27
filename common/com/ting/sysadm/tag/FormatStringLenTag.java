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
public class FormatStringLenTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	//格式化的字符串
	private String targetStr;
	//想要限制的长度，包含该长度
	private String limitLen="10";
	//使用 。。结尾（默认3个点）
	private String replaceStr="...";
	//返回对象
	private String name="row";
	@Override
	public int doEndTag() {
		String reStr=this.targetStr;
	try{
		//如果为空直接返回
		if(StringUtils.isNotBlank(this.targetStr)){
		//长度超过限制
		if(StringUtils.length(this.targetStr)>Integer.valueOf(limitLen).intValue()){
			reStr=(this.targetStr.substring(0,Integer.parseInt(this.limitLen)+1)+this.replaceStr);
		}
		JspWriter writer = pageContext.getOut();
		writer.print(reStr);
		}
	} catch (Exception ex) {
		ex.printStackTrace();
		//log.error(ex.getMessage());		
	}
	pageContext.setAttribute(name,reStr);
	return EVAL_PAGE;
	}
	public void setTargetStr(String targetStr) {
		this.targetStr = targetStr;
	}
	public void setLimitLen(String limitLen) {
		this.limitLen = limitLen;
	}
	public void setReplaceStr(String replaceStr) {
		this.replaceStr = replaceStr;
	}
	public void setName(String name) {
		this.name = name;
	}
	//增加get方式，目标值会改变
	public String getTargetStr() {
		return targetStr;
	}
}
