package com.ting.sysadm.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.blog.model.Blog;
import com.blog.model.Comment;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.render.CaptchaRender;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.ting.sysadm.config.SysConstant;
import com.ting.sysadm.interceptor.LoginInterceptor;
import com.ting.sysadm.interceptor.LoginValidate;
import com.ting.sysadm.interceptor.RegisterValidate;
import com.ting.sysadm.model.Fans;
import com.ting.sysadm.model.User;
import com.ting.sysadm.tag.BlogUtils;
import com.ting.sysadm.tag.ImageUntils;

public class UserController extends Controller {
	/**
	 * 1、验证码是否正确 2、用户名和密码是否在表user中是否有对应
	 */
	@Before(LoginValidate.class)
	public void login() {
		forwardAction("/");
	}

	@Before(RegisterValidate.class)
	public void register() {
		createToken("registerToken", 30 * 60);
		User user = getModel(User.class);
		user.saveUser();
		this.getSession().setAttribute("user", user);
		forwardAction("/");
	}

	public void toLogin() {
		Object user = this.getSessionAttr("user");
		if (user != null)
			forwardAction("/");
		else
			renderJsp("login.jsp");
	}

	public void toRegister() {
		Object user = this.getSessionAttr("user");
		renderJsp("register.jsp");
	}

	@Before(LoginInterceptor.class)
	public void editUser() {
		User user = getModel(User.class);
		user.update();
		// 图片缩放
		if (StringKit.notBlank(user.getStr("faceUrl"))) {
			try {
				File faceFile = new File(JFinal.me().getServletContext().getRealPath("/") + "data" + user.getStr("faceUrl"));
				BufferedImage bis = ImageIO.read(faceFile);
				if (bis.getWidth() == 32 && bis.getHeight() == 32) {
					BufferedImage bid = null;
					bid = new ImageUntils().imageZoomOut(bis, 32, 32);
					ImageIO.write(bid, "jpeg", faceFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CacheKit.removeAll("blogList");// 清除首页博文缓存（主要是如果更改了图片，首页不会马上见效）
		this.setSessionAttr("user", User.dao.findById(user.getInt("id")));
		renderJsp("user/editUser.jsp");
	}

	public void go() {
		String jspName = getPara(0);
		renderJsp("user/" + jspName + ".jsp");
	}

	public void loginOut() {
		Object user = getSessionAttr("user");
		if (user != null)
			removeSessionAttr("user");
		render("login.jsp");
	}

	public void admin() {
		renderJsp("admin.jsp");
	}

	public void index() {
		// 获取用户名
		String loginName = getPara(0);
		if (StringKit.isBlank(loginName))
			return;
		// 获取分页的当前页数
		int pageNumber = getParaToInt(1, 1);
		User u = User.dao.findUniqueUserByLogin(loginName);
		Page<Blog> listBlog = Blog.dao.findBlogListByLoginName(loginName, pageNumber);
		// 返回博文分页和用户
		setAttr("blogList", listBlog);
		setAttr("u", u);

		// 右侧 博文排行，评论排行
		List<Blog> listRightBlog = Blog.dao.findUserBlogListForTop(u.getInt("id"), 10);
		List<Comment> listComment = Comment.dao.findUserCommentForTop(u.getInt("id"), 10);
		setAttr("listRightBlog", listRightBlog);
		setAttr("listComment", listComment);

		// 获取关注状态
		Object obj = this.getSessionAttr("user");
		if (obj != null) {
			User user = (User) obj;
			if (Fans.dao.isExistsFans(u.getInt("id"), user.getInt("id")))
				setAttr("careStatu", 1);
		}
		renderJsp("userIndex.jsp");
	}

	public void code() {
		CaptchaRender img = new CaptchaRender(SysConstant.RANDOM_CODE_KEY);
		render(img);
	}

	/** 关注对象 **/
	@Before(LoginInterceptor.class)
	public void careAuthor() {
		int id = getParaToInt(0, 0);
		User user = (User) this.getSessionAttr("user");
		User careUser = User.dao.findById(id);
		if (careUser == null) {
			renderJson(BlogUtils.getError("0"));
		} else {
			Fans fans = new Fans();
			fans.set("authorId", id);
			fans.set("fansId", user.get("id"));
			fans.set("createtime", new Date());
			try {
				if (!Fans.dao.isExistsFans(id, (user.getInt("id")))&&user.getInt("id").intValue()!=careUser.getInt("id").intValue())
					fans.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
			long number = Fans.dao.getFansNumber(id);
			renderJson(BlogUtils.getError(String.valueOf(number)));
		}
	}

	public void careNumber() {
		try {
			int authorId = getParaToInt(0, 0);
			long number = Fans.dao.getFansNumber(authorId);
			renderText(String.valueOf(number));
		} catch (Exception e) {
			renderText("0");
		}
		return;
	}
	public void getMyFans(){//获取粉丝列表
		int authorId = getParaToInt(0, 0);
		List<User> uList = User.dao.getFansByAuthorId(authorId);
		renderJson(uList);
	}
	public void getMyCareAuthor(){//获取关注列表
		int authorId = getParaToInt(0, 0);
		List<User> uList = User.dao.getCarerByAuthorId(authorId);
		renderJson(uList);
	}
}
