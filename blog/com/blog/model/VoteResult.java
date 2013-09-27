package com.blog.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class VoteResult extends Model<VoteResult> {
	public static final VoteResult dao = new VoteResult();
	//一天只能投票一次
	public VoteResult findByIpAndUser(String ip,int userId,int voteId){
		VoteResult vr = VoteResult.dao.findFirst("select id from voteresult where ((unix_timestamp(NOW())-unix_timestamp(createTime))/60/60/24)<1 and voteId=? and userId=? and ip=?",voteId,userId,ip);
		return vr;
	}
	public VoteResult findByIp(String ip,int voteId){
		VoteResult vr = VoteResult.dao.findFirst("select id from voteresult where ((unix_timestamp(NOW())-unix_timestamp(createTime))/60/60/24)<1 and voteId=? and ip=?",voteId,ip);
		return vr;
	}
	public List<VoteResult> findByVoteOptionId(int voteId){
		List<VoteResult> lvr = VoteResult.dao.find("select *from voteresult where voteId=?", voteId);
		return lvr;
	}
}
