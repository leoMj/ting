package com.ting.sysadm.model;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.plugin.activerecord.Model;

public class User extends Model<User> {
	public static final User dao = new User();

	public User login(String loginName, String password) {
		Pattern pattern = Pattern.compile("^/w+([-.]/w+)*@/w+([-]/w+)*/.(/w+([-]/w+)*/.)*[a-z]{2,3}$");
		Matcher matcher = pattern.matcher(loginName);
		User user = null;
		if (matcher.matches()) {// email登录
			user = User.dao.findFirst("select *from user where email=? and password=?", loginName, password);
		} else {
			user = User.dao.findFirst("select *from user where loginName=? and password=?", loginName, password);
		}
		return user;
	}
	//最新用户
	public List<User> findeNewUser(int number){
		List<User> listUser = User.dao.find("select *from user order by createTime desc limit ?",number);
		return listUser;
	}
	public void saveUser(){
		this.set("createTime", new Date());
		this.save();
	}
	public boolean isUniqueLogin(String loginName){
		User user = User.dao.findFirst("select *from user where loginName=?",loginName);
		return user==null;
	}
	public boolean isUniqueEmail(String email){
		User user = User.dao.findFirst("select *from user where email=?",email);
		return user==null;
	}
	public User findUniqueUserByLogin(String loginName){
		User user = User.dao.findFirst("select *from user where loginName=?",loginName);
		return user;
	}
	public List<User> getFansByAuthorId(int authorId){
		List<User> list = User.dao.find("select u.* from fans f,user u where f.fansId=u.id and f.authorId=? order by f.createtime limit 0,20", authorId);
		return list;
	}
	public List<User> getCarerByAuthorId(int authorId){
		List<User> list = User.dao.find("select u.* from fans f,user u where f.authorId=u.id and f.fansId=? order by f.createtime limit 0,20", authorId);
		return list;
	}
}
