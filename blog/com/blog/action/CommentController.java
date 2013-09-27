package com.blog.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.blog.model.Blog;
import com.blog.model.Comment;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.ting.sysadm.interceptor.CommentValidate;
import com.ting.sysadm.interceptor.LoginInterceptor;
import com.ting.sysadm.model.User;

public class CommentController extends Controller{
	@Before({LoginInterceptor.class,CommentValidate.class})
	public void add(){
		/*新增评论，返回评论的文章详细页面*/
		Comment comment = getModel(Comment.class);
		comment.set("createTime", new Date());
		String content = comment.getStr("content");
		User user = (User)this.getSession().getAttribute("user");
		int authorId = user.getInt("id");
		comment.set("authorId", authorId);
		//替换尖括号 防止xss注入攻击
		if(StringUtils.isNotBlank(content)){
			String s = StringEscapeUtils.escapeHtml(content);
			content=content.replaceAll("<", "〈");
			content=content.replaceAll(">", "〉");
			comment.set("content", content);
		}
		comment.saveComment();
		Blog.dao.commentCount(comment.getInt("articleId"));
		renderText("评论成功");
	}
	//获取文章的评论列表
	public void list(){
		List<Comment> cList = Comment.dao.findCommentByArticleId(getParaToInt(0));
		List<Comment> commentList = new ArrayList<Comment>();
		String content="";
		Date createTime;
		//确保格式正确返回 主要替换回车和空格
		for(Comment comment:cList){
			content = comment.getStr("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>").replaceAll(" ", "&nbsp;");
			createTime = comment.getTime("createTime");
			comment.set("createTime", DateUtils.round(createTime, Calendar.DATE));
			comment.set("content", content);
			commentList.add(comment);
		}
		setAttr("cList", commentList);
	}
	public void ajaxList() throws ParseException{
		Page<Comment> cList = Comment.dao.findPageCommentByArticle(getParaToInt(0),getParaToInt(1,1));
		List<Comment> commentList = new ArrayList<Comment>();
		Map<String,Object> map = new HashMap<String,Object>();
		cList.getTotalPage();
		String content="";
		String commentIds="";//获取回复列表
		//确保格式正确返回 主要替换回车和空格
		for(Comment comment:cList.getList()){
			commentIds+=commentIds.equals("")?comment.getInt("id"):(","+comment.getInt("id"));
			content = comment.getStr("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>").replaceAll(" ", "&nbsp;");
			comment.set("content", content);
			commentList.add(comment);
		}
		//获取当页的评论列表
		if(!commentIds.equals("")){
			List<Comment> listCommentReplay = Comment.dao.findPageCommentByParentComment(commentIds);
			map.put("rList", listCommentReplay);
		}
		map.put("cList", commentList);
		map.put("pageNumber", cList.getPageNumber());
		map.put("totalPage", cList.getTotalPage());
		map.put("pageSize", cList.getPageSize());
		renderJson(map);
	}
	//增加 支持
	public void ajaxSupport(){
		int commentId = getParaToInt(0);
		long articleId = getParaToLong(1);
		int supportNum = Comment.dao.support(commentId,articleId);
		renderText(String.valueOf(supportNum));
	}
	//增加反对
	public void ajaxOppose(){
		int commentId = getParaToInt(0);
		int articleId = getParaToInt(1);
		int opposeNum = Comment.dao.oppose(commentId,articleId);
		renderText(opposeNum+"");
	}
}
