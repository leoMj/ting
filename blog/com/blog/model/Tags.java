package com.blog.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.ehcache.CacheKit;

public class Tags extends Model<Tags>{
	 private static String TAGS_LIST="tagsList";
	 public static final Tags dao = new Tags();
	 public List<Tags> listAll(){
		 List<Tags> tagsList = Tags.dao.findByCache(TAGS_LIST, "frontTagsList", "select distinct(title) from tags where title!=''");
		 return tagsList;
	 }
	 
	 public void saveTags(String title){
		// this.set("createTime", new Date());
		 Tags tags = Tags.dao.findFirst("select *from tags where title=?", title);
		 if(tags==null){
			 tags = new Tags();
			 tags.set("title", title);
			 tags.save();
		 }
		 //清空tagslist缓存
		 removeAllTagsCache();
	 }
	 
	 public void removeAllTagsCache() {
	        CacheKit.removeAll(TAGS_LIST);
     }
	 
}
