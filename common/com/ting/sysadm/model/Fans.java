package com.ting.sysadm.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Fans extends Model<Fans> {
	public static final Fans dao = new Fans();
	public boolean isExistsFans(int authorId,int fansId){
		Fans f = Fans.dao.findFirst("select * from fans where authorId=? and fansId=?", authorId,fansId);
		if(f!=null)
			return true;
		return false;
	}
	public long getFansNumber(int authorId){
		List<Fans> list = Fans.dao.find("select count(*) as number from fans where authorId=?", authorId);
		return list.get(0)!=null?list.get(0).getLong("number"):0;
	}
	public List<Fans> getFansByAuthorId(int authorId){
		List<Fans> list = Fans.dao.find("select fansId from fans where authorId=?", authorId);
		return list;
	}
}
