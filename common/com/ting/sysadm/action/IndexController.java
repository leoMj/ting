package com.ting.sysadm.action;

import java.util.List;

import com.blog.model.Blog;
import com.blog.model.Comment;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.ting.sysadm.model.User;

public class IndexController extends Controller {
	public void index() {
		Page<Blog> list = Blog.dao.list(getParaToInt(0, 1));
		//List<Blog> listCoverBlog = Blog.dao.listCoverBlog();
		List<Blog> listReadBlog = Blog.dao.findBlogListForTop(8);
		List<Comment> listComment = Comment.dao.findCommentForTop(8);
		List<User> listNewUser = User.dao.findeNewUser(10);
		setAttr("blogList",list);
		//封面图片 博文
		//setAttr("coverBlog",listCoverBlog);
		//阅读排行
		setAttr("readTopBlog", listReadBlog);
		//评论排行
		setAttr("listComment",listComment);
		//最新注册用户
		setAttr("listNewUser",listNewUser);
		render("/WEB-INF/jsps/index.jsp");
	}
	public void webmq(){
		render("/WEB-INF/jsps/webmq.jsp");
	}
	public void getPage(){
		render("/WEB-INF/jsps/blog/admin/list.jsp");
	}
	public void leaveMsg() {
		render("/common/leaveMsg.html");
	}

	public void regist() {
		render("/user/regist.html");
	}

	public void toLogin() {
		render("/user/login.html");
	}

	public void test() {
		render("/common/test.html");
	}
}
