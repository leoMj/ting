package com.blog.action;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.blog.interceptor.BlogValidator;
import com.blog.model.Blog;
import com.blog.model.Tags;
import com.blog.model.Visitor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Page;
import com.ting.sysadm.interceptor.LoginInterceptor;
import com.ting.sysadm.model.User;
import com.ting.sysadm.tag.BlogUtils;
import com.ting.sysadm.utils.HtmlFilter;

public class BlogController extends Controller{
	@Before(LoginInterceptor.class)
	public void add(){
		List<Tags> tagsList = Tags.dao.listAll();
		String tags = "";
		for(Tags t:tagsList){
			if(tags.equals("")){
				tags+="\""+t.get("title")+"\"";
			}else{
				tags+=",\""+t.get("title")+"\"";
			}
		}
		setAttr("tags",tags);
		renderJsp("admin/add.jsp");
	}
	//前台展示博客详细信息
	public void detail(){
		int id = getParaToInt(0);
		Blog blog = Blog.dao.getBlog(id);
		System.out.println(blog.getStr("tags"));
		//搜索同类文章listBlogByTag
		Page<Blog> listBlog=Blog.dao.listBlogByTag(blog.getStr("tags"),id);
		
		setAttr("blog",blog);
		setAttr("blogList", listBlog);
		renderJsp("front/detail.jsp");
	}
	//后台修改博客
	public void update(){
		int id = getParaToInt(0);
		Blog blog = Blog.dao.getBlog(id);
		List<Tags> tagsList = Tags.dao.listAll();
		String tags = "";
		for(Tags t:tagsList){
			if(tags.equals("")){
				tags+="\""+t.get("title")+"\"";
			}else{
				tags+=",\""+t.get("title")+"\"";
			}
		}
		setAttr("tags",tags);
		setAttr("blog",blog);
		renderJsp("admin/edit.jsp");
	}
	public void comment(){
		//评论id
		int commentId = getParaToInt(0);
		Blog blog = Blog.dao.findByCommentId(commentId);
		setAttr("commentId",commentId);
		setAttr("blog",blog);
		renderJsp("front/detail.jsp");
	}
	//标签搜索页面
	public void tag(){
		try{
		String tag = URLDecoder.decode(getPara(0),"UTF-8");
		int number=getParaToInt(1,1);
		Page<Blog> bList = Blog.dao.listByTag(number, tag);
		setAttr("blogList", bList);
		setAttr("tag",tag);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		renderJsp("front/tag.jsp");
	}
	public void tags(){
		List<Tags> listTag = Tags.dao.listAll();
		setAttr("listTag", listTag);
		renderJsp("front/tags.jsp");
	}
	@Before({LoginInterceptor.class,BlogValidator.class})
	public void save(){
		//createToken("blogToken", 2*60); //过期时间设置为30分钟
		Blog blog = getModel(Blog.class);
		//替换尖括号 防止xss注入攻击
		String description = blog.get("description");
		if(StringUtils.isNotBlank(description)){
			String s = StringEscapeUtils.escapeHtml(description);
			System.out.println(s);
			description=description.replaceAll("<", "〈").replaceAll(">", "〉");
			blog.set("modifyTime", new Date());
			blog.set("description", description);
		}else{//如果没有输入简短描述
			description = HtmlFilter.getText(blog.getStr("content"));
			if(StringKit.notBlank(description)){
				description=BlogUtils.getSubString(description, 0, 200);
				blog.set("description", description);
			}
		}
		blog.saveBlog();
		String tags = blog.getStr("tags");
		if(StringUtils.isNotBlank(tags)){
			for(String t:tags.split(",")){
				new Tags().saveTags(t);
			}
		}
		forwardAction("/");
	}
	@Before({LoginInterceptor.class,BlogValidator.class})
	public void edit(){
		//createToken("blogToken", 2*60); //过期时间设置为30分钟
		Blog blog = getModel(Blog.class);
		Blog b = Blog.dao.findById(blog.getInt("id"));
		blog.set("authorId", b.getInt("authorId"));
		blog.set("viewCount", b.getLong("viewCount"));
		blog.set("commentCount", b.getLong("commentCount"));
		blog.set("createTime", b.getTimestamp("createTime"));
		//替换尖括号 防止xss注入攻击
		String description = blog.get("description");
		if(StringUtils.isNotBlank(description)){
			description=description.replaceAll("<", "〈").replaceAll(">", "〉");
			blog.set("description", description);
		}else{//如果没有输入简短描述
			description = HtmlFilter.getText(blog.getStr("content"));
			if(StringKit.notBlank(description)){
				description=BlogUtils.getSubString(description, 0, 200);
				blog.set("description", description);
			}
		}
		blog.editBlog();
		String tags = blog.getStr("tags");
		if(StringUtils.isNotBlank(tags)){
			for(String t:tags.split(",")){
				Tags tag = new Tags();
				tag.set("title", t);
				tag.save();
			}
		}
		forwardAction("/");
	}
	/**
	 * 如果登录状态 则判断同一用户 同一ip 同一文章  在5分钟内是否访问过
	 * 如果非登录状态 则判断 同一ip 同一文章 在5分钟内是否访问过
	 */
	public void viewCount(){
		/*访问增加记录*/
		String ip = this.getRequest().getRemoteAddr();
		int id = getParaToInt(0);
		Object o = this.getSession().getAttribute("user");
		int flag=0;
		Visitor visitor = new Visitor();
		if(o!=null&&!reasonableVisitorUser(ip,id,((User)o).getInt("id"))){
			visitor.set("articleId", id);
			visitor.set("authorId", ((User)o).getInt("id"));
			visitor.set("ip", ip);
			visitor.set("createTime", new Date());
			visitor.save();
			//更新blog中的viewCount字段
			Blog blog = Blog.dao.getBlog(id);
			blog.set("viewCount", blog.getLong("viewCount")+1);
			blog.update();
			flag=1;
			System.out.println("新增访问记录成功...");
		}else if(!reasonableVisitorUser(ip,id)){
			visitor.set("articleId", id);
			visitor.set("ip", ip);
			visitor.set("createTime", new Date());
			visitor.save();
			//更新blog中的viewCount字段
			Blog blog = Blog.dao.getBlog(id);
			blog.set("viewCount",blog.getLong("viewCount")+1);
			blog.update();
			flag=1;
			System.out.println("新增访问记录成功...");
		}
		renderText(flag+"");
	}
	private boolean reasonableVisitorUser(String ip,int...ids){
		boolean isVisitor=false;
		if(ids.length>1){//访问者 注册用户
			int articleId = ids[0];
			int authorId = ids[1];
			Visitor visitor = Visitor.dao.findByIpAndArticleIdAndAuthorId(ip, articleId, authorId);
			//刚刚访问过
			if(null!=visitor)
				isVisitor=true;
		}else{//访问者 匿名
			int articleId = ids[0];
			Visitor visitor = Visitor.dao.findByIpAndArticleId(ip, articleId);
			//刚刚访问过
			if(null!=visitor)
				isVisitor=true;
		}
		return isVisitor;
	} 
	public void list(){
	    User user = this.getSessionAttr("user");
		Page<Blog> list = null;
		if(user!=null){
			list = Blog.dao.findBlogListByLoginName(user.getStr("loginName"),getParaToInt(0, 1));
			setAttr("blogList",list);
		}
		renderJsp("admin/list.jsp");
	}
	public void delete(){
		int id = getParaToInt(0);
		if(id!=0)
			Blog.dao.delete(id);
		forwardAction("/blog/list");
	}
	public void ting(){
		renderJsp("/index.jsp");
	}
}
