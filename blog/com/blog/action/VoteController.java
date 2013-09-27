package com.blog.action;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.blog.model.VoteOption;
import com.blog.model.VoteResult;
import com.jfinal.core.Controller;
import com.ting.sysadm.model.User;

public class VoteController extends Controller {
	public void go(){
		String jspName = getPara(0);
		renderJsp("vote/"+jspName+".jsp");
	}
	//新增vote
	public void add(){
		VoteOption voteOption = getModel(VoteOption.class);
		try {
			Date startTime = DateUtils.parseDate(getPara("startTime"), new String[]{"MM/dd/yyyy HH:mm:ss"});
			Date endTime = DateUtils.parseDate(getPara("endTime"), new String[]{"MM/dd/yyyy HH:mm:ss"});
			voteOption.set("startTime", startTime);
			voteOption.set("endTime", endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		voteOption.save();
	}
	//用户vote
	public void vote(){
		int voteOptionId = getParaToInt(0);
		VoteOption vp = VoteOption.dao.findById(voteOptionId);
		setAttr("voteOption", vp);
		renderJsp("vote/vote.jsp");
	}
	//用户投票 
	public void vp(){
		VoteResult vr = getModel(VoteResult.class);
		String ip = this.getRequest().getRemoteAddr();
		Object u = this.getSession().getAttribute("user");
		Map<String,Object> map = new HashMap<String,Object>();
		String error="投票成功";
		if(u!=null){
			if(null==VoteResult.dao.findByIpAndUser(ip, ((User)u).getInt("id"), vr.getInt("voteId"))){
				vr.set("createTime", new Date());
				vr.set("ip", ip);
				vr.save();
			}else{
				error="您已投过 票";
			}
		}else if(null==VoteResult.dao.findByIp(ip, vr.getInt("voteId"))){
			vr.set("createTime", new Date());
			vr.set("ip", ip);
			vr.save();
		}else{
			error="您已投过 票";
		}
		List<VoteResult> vrList = VoteResult.dao.findByVoteOptionId(vr.getInt("voteId"));
		map.put("vrList", vrList);
		map.put("error", error);
		renderJson(map);
	}
	//获取投票结果
	public void vr(){
		int voteId = getParaToInt(0);
		renderJson(VoteResult.dao.findByVoteOptionId(voteId));
	}
}
