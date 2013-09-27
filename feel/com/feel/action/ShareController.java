package com.feel.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.blog.model.Blog;
import com.blog.model.Comment;
import com.feel.config.ShareValidator;
import com.feel.model.Share;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.ting.sysadm.interceptor.LoginInterceptor;
import com.ting.sysadm.model.Fans;
import com.ting.sysadm.model.User;
import com.ting.sysadm.tag.BlogUtils;

public class ShareController extends Controller {
	@Before({LoginInterceptor.class,ShareValidator.class})
	public void save(){
		Share share = getModel(Share.class);
		User user = (User)this.getSession().getAttribute("user");
		share.set("authorId", user.get("id"));
		String content = share.get("content");
		if(StringUtils.isNotBlank(content)){
			content=content.replaceAll("<", "〈").replaceAll(">", "〉");
			share.set("content", content);
		}
		share.set("creation", new Date());
		share.set("replyNum", 0);
		boolean rb = share.save();
		if(share.getInt("father")!=null&&share.getInt("father")!=0){//修改评论的回复数量
			Share father = Share.dao.findById(share.getInt("father"));
			long replyNum = father.getLong("replyNum")==null?0:father.getLong("replyNum");
			father.set("replyNum", replyNum+1);
			father.update();
		}
		renderText(rb?"0":"1");
	}
	public void shareList(){
		int authorId = getParaToInt(0,0);
		int pageNumber = getParaToInt(1,1);
		//获取粉丝id列表和自身id
		List<Fans> fans = Fans.dao.getFansByAuthorId(authorId);
		String ids=String.valueOf(authorId);
		for(Fans f:fans){
			ids+=","+f.getInt("fansId");
		}
		Page<Share> list = Share.dao.pageList(ids, pageNumber);
		setAttr("shareList", list);
		//获取归档博文
		List<Blog> piList = Blog.dao.blogPigeonry(authorId);
		setAttr("piList", piList);
		User u = User.dao.findById(authorId);
		//返回博文分页和用户
		setAttr("u", u);
		//获取博文数量
		long blogNumber = Blog.dao.findBlogNum(u.getStr("loginName"));
		setAttr("blogNumber", blogNumber);
		//右侧 博文排行，评论排行
		List<Blog> listRightBlog = Blog.dao.findUserBlogListForTop(u.getInt("id"),10);
		List<Comment> listComment = Comment.dao.findUserCommentForTop(u.getInt("id"),10);
		setAttr("listRightBlog", listRightBlog);
		setAttr("listComment", listComment);
		
		renderJsp("front/share.jsp");
	}
	public void userShareList(){
		int pageNumber = getParaToInt(0,1);
		Page<Share> list = Share.dao.pageList(pageNumber);
		setAttr("shareList", list);
		renderJsp("front/shareList.jsp");
	}
	public void getReply(){
		int replyId = getParaToInt(0,0);
		List<Share> list = Share.dao.replyList(replyId);
		List<Share> newList = new ArrayList<Share>();
		//替换空格 换行等
		for(Share share:list){
			share.set("content", BlogUtils.replaceNewspace(share.getStr("content")));
			//此时没有图片 暂用该字段
			share.set("prepareFild", BlogUtils.formatBlogDate(share.getTimestamp("creation")));
			newList.add(share);
		}
		renderJson(newList);
	}
}
