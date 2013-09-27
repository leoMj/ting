package com.feel.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

public class Share extends Model<Share> {
	public static final Share dao = new Share();

	public Page<Share> pageList(String ids, int pageNumber) {
		Page<Share> list = Share.dao.paginate(pageNumber, 10, "select s.*,u.loginName,u.faceUrl", "from share s inner join user u on s.authorId=u.id where s.authorId in("+ids+") and s.father is null order by s.creation desc");
		return list;
	}

	public Page<Share> pageList(int pageNumber) {
		Page<Share> list = Share.dao.paginate(pageNumber, 5, "select s.*,u.loginName,u.faceUrl", "from share s inner join user u on s.authorId=u.id where s.father is null order by s.creation desc");
		return list;
	}

	public List<Share> replyList(int replyId) {
		List<Share> list = Share.dao.find("select  s.*,u.loginName,u.faceUrl from share s inner join user u on s.authorId=u.id where s.father=?", replyId);
		return list;
	}
}
