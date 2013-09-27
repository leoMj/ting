package com.ting.sysadm.tag;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StringKit;

/**
 * 
 * @author leo
 * 
 */
public class BlogUtils {
	/**格式化时间，返回距现在时间的差值，例如 返回：2小时前，2天前 。。。。**/
	@SuppressWarnings("deprecation")
	public static String formatBlogDate(Date date){
		Date nowTime = new Date();
		/**现在时间与传入的时间毫秒相减**/
		long odd = nowTime.getTime()-date.getTime();
		/**获取小时**/
		long hour = odd/1000/60/60;
		if(hour==0){
			return ((odd/1000/60)+"分钟前");
		}else if(1<=hour&&hour<nowTime.getHours()){
			return hour+"小时前";
		}else if(nowTime.getHours()<=hour&&hour<(24+nowTime.getHours())){
			return "昨天("+date.getHours()+":"+date.getMinutes()+")";
		}else{
			return (hour-nowTime.getHours())/24+"天前";
		}
	}
	/**编码反html内容**/
	public static String escapeHtml(String htmlContent){
		if(StringUtils.isNotBlank(htmlContent))
			return StringEscapeUtils.unescapeHtml(htmlContent);
		return "";
	}
	/**编码反html内容**/
	public static String unescapeHtml(String htmlContent){
		if(StringUtils.isNotBlank(htmlContent))
			return StringEscapeUtils.escapeHtml(htmlContent);
		return "";
	}
	/**分享心情 特定方法,替换表情 空格 换行 **/
	public static String replaceNewspace(String content){
		if(StringUtils.isNotBlank(content))
			content = content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>").replaceAll(" ", "&nbsp;");
			content = faceReplace(content);
		return content;
	}
	public static String faceReplace(String str){
		String path = JFinal.me().getContextPath();
		Pattern p= Pattern.compile("\\[\\d*\\]");//3代表出现三个数字的地方，这个根据实际变换
		String r="";
		String index="";
		Matcher m = p.matcher(str);
		while(m.find()){
			r=m.group();
			index = r.substring(r.indexOf("[")+1, r.indexOf("]"));
			str = str.replace(r, "<img  align='absmiddle' src='"+path+"/images/"+index+".gif'/>");
		}
		return str;
	}
	public static String getSourceImage(String path){//根据小图获取大图路径
		if(StringKit.notBlank(path)){
			return path.substring(0,path.lastIndexOf("."))+"_2"+path.substring(path.lastIndexOf("."));
		}else{
			return path;
		}
	}
	/**使用URLEncoder.encode 后，字符串中的空格会变成+，用此方法把+替换成%20 即编码的空格**/
	public static String decodeStr(String str)throws Exception{
		if(StringKit.isBlank(str))
			return str;
		String decodeS = URLEncoder.encode(str, "UTF-8");
		decodeS=decodeS.replaceAll("\\+", "%20");
		return decodeS;
	}
	public static void pushDataToEhcache(){
		List<String> list = new ArrayList<String>();
		list.add("leogen");list.add("leogen2");list.add("leogen3");
		CacheManager manager = CacheManager.create();
		Cache cache = manager.getCache("dataPush");
		Element element = new Element("bList", list);
		cache.put(element);
	}
	public static void getDataFromEhcache(String key){
//		CacheManager.g
//		CacheManager manager = CacheManager.create();
//		Cache cache = manager.getCache("dataPush");
//		cache.get(key);
	}
	public static String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		try {
			obj.put("message", BlogUtils.decodeStr(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj.toJSONString();
	}
	public static String getSubString(String target,int startIndex,int endIndex){
		if(StringKit.isBlank(target))
			return target;
		if(StringUtils.length(target)>endIndex){
			return target.substring(0,endIndex);
		}
		return target;
	}
}
