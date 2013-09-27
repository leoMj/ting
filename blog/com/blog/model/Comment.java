package com.blog.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;

public class Comment extends Model<Comment> {
	private static String COMMENT_LIST = "commentList";
	public static final Comment dao = new Comment();

	// 获取该片文章下的所有评论，缓存key使用 缓存名称+文章id 保存（即：COMMENT_LIST+articleId）
	public List<Comment> findCommentByArticleId(int articleId) {
		List<Comment> list = Comment.dao.findByCache(COMMENT_LIST, COMMENT_LIST + articleId, "select c.*,u.name,u.faceUrl from comment c inner join user u on c.authorId=u.id where c.articleId=? order by c.createTime desc",articleId);
		return list;
	}
	public Page<Comment> findPageCommentByArticle(int articleId,int pageNumber){
		Page<Comment> listPage = Comment.dao.paginateByCache(COMMENT_LIST, pageNumber+COMMENT_LIST + articleId, pageNumber, 15, "select c.*,u.name,u.loginName,u.faceUrl","from comment c inner join user u on c.authorId=u.id where c.articleId=? and c.father is null order by c.supportNum desc,c.createTime desc",articleId);
		return listPage;
	}
	public List<Comment> findPageCommentByParentComment(String commentIds){
		List<Comment> listComment = Comment.dao.find("select c.*,u.name,u.loginName,u.faceUrl from comment c inner join user u on c.authorId=u.id where c.father in("+commentIds+") order by c.supportNum desc,c.createTime desc");
		return listComment;
	}
	//添加评论，判断是否存在缓存并删除缓存
	public void saveComment() {
		//int articleId = this.getInt("articleId");
		this.save();
		CacheKit.removeAll(COMMENT_LIST);
	}
	//添加支持
	public int support(int commentId,long article){
		Comment c = Comment.dao.findById(commentId);
		int supportNum = c.getInt("supportNum");
		c.set("supportNum", supportNum+1);
		boolean f = c.update();
		CacheKit.removeAll(COMMENT_LIST);
		return f?(supportNum+1):supportNum;
	}
	//添加反对
	public int oppose(int commentId,int article){
		Comment c = Comment.dao.findById(commentId);
		int opposeNum = c.getInt("opposeNum");
		c.set("opposeNum", opposeNum+1);
		boolean f = c.update();
		CacheKit.removeAll(COMMENT_LIST);
		return f?(opposeNum+1):opposeNum;
	}
	//评论排行
	public List<Comment> findCommentForTop(int number){
		List<Comment> listComment = Comment.dao.find("select c.*,u.loginName,u.faceUrl from comment c inner join user u on c.authorId=u.id order by c.createTime desc limit ?", number);
		return listComment;
	}
	//用户博文的评论排行
	public List<Comment> findUserCommentForTop(int userId,int number){
		List<Comment> listComment = Comment.dao.find("select c.*,u.loginName from comment c inner join user u on c.authorId=u.id where c.authorId=? order by c.createTime desc limit ?", userId,number);
		return listComment;
	}
	//移除所有该对象缓存
	public void removeAllTagsCache() {
		CacheKit.removeAll(COMMENT_LIST);
	}

}
