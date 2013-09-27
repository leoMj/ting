package com.ting.sysadm.model;

import com.jfinal.plugin.activerecord.Model;

public class SysFile extends Model<SysFile>{
	public static final SysFile dao = new SysFile();
	public boolean deleteByPath(String filePath){
		SysFile file = SysFile.dao.findFirst("select *from file where filePath=?",filePath);
		return file.delete();
	}
}
