package com.blog.model;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class Blog extends Model<Blog>{
	 private static String BLOG_LIST="blogList";
	 private static String BLOG_LIST_ADMIN="blogListAdmin";
	 private static String BLOG_LIST_TAG="blogListTag";
	 private static String BLOG="blog";
	 public static final Blog dao = new Blog();
	 //新增
	 public void saveBlog(){
		 this.set("createTime", new Date());
		 this.set("viewCount", 0);
		 this.set("commentCount", 0);
		 this.save();
		 removeBlogCache(this);
		 removeAllBlogPageCache();
	 }
	 //修改
	 public void editBlog(){
		this.update();
		removeBlogCache(this);
		loadBlog(this);
	 }
	 //首页博文 列表显示
	 public Page<Blog> list(int pageNumber){
		 Page<Blog> listPage = Blog.dao.paginateByCache(BLOG_LIST, BLOG_LIST+"-"+pageNumber, pageNumber, 15, "select b.*,u.loginName,u.faceUrl", "from blog b inner join user u on b.authorId=u.id order by b.createTime desc");
		 loadBlogPage(listPage);
		 return listPage;
	 }
	 public Page<Blog> listBlogByTag(String tags,int blogId){
		 Page<Blog> listPage = Blog.dao.paginateByCache(BLOG_LIST, BLOG_LIST+blogId, 1, 15, "select b.*",  "from blog b where fSearch('"+tags+"',b.tags)>0 and b.id!="+blogId);
		 loadBlogPage(listPage);
		 return listPage;
	 }
	 //根据标签搜索文章
	 
	//根据标签查找
	 public Page<Blog> listByTag(int pageNumber,String tag){
		 Page<Blog> listPage = Blog.dao.paginate(pageNumber, 15, "select b.*,u.loginName,u.faceUrl", "from blog b inner join user u on b.authorId=u.id and locate('"+tag+"',b.tags)>0 order by b.createTime desc");
		 return listPage;
	 }
	 //获取有封面的博文
	 public List<Blog> listCoverBlog(){
		 return Blog.dao.find("select *from blog where cover is not null limit 8");
	 }
	//根据用户名 找到博文 ，用户首页使用
	public Page<Blog> findBlogListByLoginName(String loginName,int pageNumber){
		Page<Blog> listPage = Blog.dao.paginateByCache(BLOG_LIST, BLOG_LIST+"-"+loginName+"-"+pageNumber, pageNumber, 15, "select b.*", "from blog b inner join user u on b.authorId=u.id where u.loginName=? order by b.createTime", loginName);
		loadBlogPage(listPage);
		 return listPage;
	}
	//根据评论id 获取博文
	public Blog findByCommentId(int commentId){
		Blog blog = Blog.dao.findFirst("select b.* from blog b inner join comment c on b.id=c.articleId where c.id=?", commentId);
		return blog;
	}
	//阅读排行
	public List<Blog> findBlogListForTop(int number){
		List<Blog> listBlog = Blog.dao.find("select *from blog order by viewCount desc limit 0,?",number);
		return listBlog;
	}
	//阅读排行根据用户
	public List<Blog> findUserBlogListForTop(int userId,int number){
		List<Blog> listBlog = Blog.dao.find("select *from blog b inner join user u on b.authorId=u.id where u.id=?  order by b.viewCount desc limit 0,?",userId,number);
		return listBlog;
	}
	 public void delete(int id){
		 Blog.dao.deleteBlog(id);
		 removeAllBlogPageCache();
	 }
	 public void commentCount(int blogId){
		 Blog blog = this.findById(blogId);
		 long commentNum = blog.getLong("commentCount");
		 blog.set("commentCount", commentNum++);
		 blog.update();
		 CacheKit.remove(BLOG, BLOG+blogId);
	 }
	 //获取用户博文数量
	 public long findBlogNum(String loginName){
		List<Blog> l= Blog.dao.find("select count(*) as number from blog b,user u where b.authorId=u.id and u.loginName=?", loginName);
		return l.get(0)!=null?l.get(0).getLong("number"):0;
	 }
	 //查找文章时，连同用户名一起查出
	 public Blog getBlog(int id){
		 	final int BLOG_ID=id;
	        return CacheKit.get(BLOG, BLOG+BLOG_ID, new IDataLoader() {
	            @Override
	            public Object load() {
	                return dao.findFirst("select b.*,u.loginName,u.faceUrl from blog b inner join user u on b.authorId=u.id where b.id=?",BLOG_ID);
	            }
	        });
	    }
	 //单个对象装载到缓存
	 private void loadBlogPage(Page<Blog> blogPage) {
        List<Blog> blogList = blogPage.getList();
        for(int i = 0; i < blogList.size(); i++){
        	getBlog(blogList.get(i).getInt("id"));
        	//blogList.set(i, blog);
        }
	 }
	 //获取博文归档根据用户id
	 public List<Blog> blogPigeonry(int authorId){
		List<Blog> blogList = Blog.dao.find("select DATE_FORMAT(createTime,'%Y年%m月') as createTime,count(id) as number from blog where authorId=? GROUP BY DATE_FORMAT(createTime,'%Y-%m')",authorId);
		return blogList;
	 } 
	 private void loadBlog(Blog blog){
		 CacheKit.put(BLOG, BLOG+blog.getInt("id"), blog);
	 }
	 private void removeAllBlogPageCache() {
	        CacheKit.removeAll(BLOG_LIST);
	 }
	 private void removeBlogCache(Blog blog){
		 CacheKit.remove(BLOG, BLOG+blog.getInt("id"));
	 }
	 public void deleteBlog(int id){
		 this.deleteById(id);
	 }
	 
}

