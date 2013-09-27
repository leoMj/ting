package com.blog.model;

import com.jfinal.plugin.activerecord.Model;

public class Visitor extends Model<Visitor> {
	public static final Visitor dao = new Visitor();
	//通过ip 文章id 找到访问记录，如果存在则判断刚刚访问 5分钟内视为无效 不增加访问记录
	public Visitor findByIpAndArticleId(String ip,int articleId){
		Visitor visitor = Visitor.dao.findFirst("select id from visitor where ((unix_timestamp(NOW())-unix_timestamp(createTime))/60)<5 and articleId=? and ip=?",articleId,ip);
		return visitor;
	}
	//5分钟内视为无效 不增加访问记录
	public Visitor findByIpAndArticleIdAndAuthorId(String ip,int articleId,int authorId){
		Visitor visitor = Visitor.dao.findFirst("select id from visitor where ((unix_timestamp(NOW())-unix_timestamp(createTime))/60)<5 and authorId=? and articleId=? and ip=?", authorId,articleId,ip);
		return visitor;
	}
}
