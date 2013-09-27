package com.ting.sysadm.config;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.ting.sysadm.model.SysFile;
/**
 * 
 * @author agen
 *
 */
public class FileUploadController extends Controller{
	//单个文件上传
	public void singleFile(){
		String mHttpUrl=this.getRequest().getRealPath("/data");
		int maxSize = 10 * 1024 * 1024;
		//UploadFile file = getFile("fileName", mHttpUrl);
		//UploadFile file = getFile("fileName",mHttpUrl);
		UploadFile file = getFile("fileName", mHttpUrl, maxSize, "UTF-8");
		//Map map = getParaMap();
		//System.out.println(file.getSaveDirectory());
		//数据库操作
		SysFile f = new SysFile();
		//f.set("", value);
		renderText(file.getSaveDirectory());
	}
//	public String uploadFile(){
//		
//	}
	//发生错误时返回结果的封装
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}
	public void files(){
		List<UploadFile> listFiles = getFiles();
		boolean IsSuccess=true;
		renderJson("{\"text\":\"上传成功\"}");
	}
	public void deletePath(){
		String path = getPara("filePath");
		boolean isS = SysFile.dao.deleteByPath(path);
		if(isS){//物理删除文件
			File f = new File(JFinal.me().getServletContext().getRealPath("/")+"data"+path);
			File newFile = new File(JFinal.me().getServletContext().getRealPath("/")+"data"+path.substring(0,path.lastIndexOf("."))+"_2"+path.substring(path.lastIndexOf(".")));
			if(f.exists())
				f.delete();
			if(newFile.exists())
				newFile.delete();
		}
		renderText(isS?"1":"0");
	}
}
