package com.ting.sysadm.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StringKit;
import com.ting.sysadm.model.SysFile;
import com.ting.sysadm.tag.BlogUtils;
import com.ting.sysadm.tag.ImageUntils;

public class DownLoadUrl extends Controller {
	
	public void downLoadImage(){
		int error = 0;
		String path = getPara("imageUrl");
		if(!validateImage(path)){
			renderJson(getError("INCORRECT FILE FORMAT,SHOULD BE LIKE 'GIF,JPG,JPEG,PNG,BMP'"));
			return;
		}
		JSONObject obj = new JSONObject();
		InputStream inputStream = null;
		FileOutputStream outStream = null;
		String filePath = "";
		String fileName = "";
		String newFilePath="";
		long fileSize=0;
		try{
		URL url = new URL(path);
		String extType = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
		fileName = path.substring(path.lastIndexOf("/")+1);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		if (conn.getResponseCode() == 200) {
			inputStream = conn.getInputStream();
			//fileSize = inputStream.available();
			filePath = createImage(extType);
			File file = new File(JFinal.me().getServletContext().getRealPath("/") + "data"+filePath);
			outStream = new FileOutputStream(file);
			int len = -1;
			byte[] b = new byte[1024];
			while((len = inputStream.read(b)) != -1){
				outStream.write(b, 0, len);
			}
			fileSize = file.length();
			//文件大小验证
			if(fileSize>100000){
				file.deleteOnExit();
				//renderJson(getError("file size invalid,less than 100KB"));
				renderJson(getError(BlogUtils.decodeStr("文件大小不超过100KB")));
				return;
			}
			//图片缩略
			try{
				BufferedImage bis = ImageIO.read(file);
				int width = bis.getWidth();
				int height = bis.getHeight();
				BufferedImage bid = null;
				double bli = (double)width/(double)height;
				int newHeight = (int)Math.round(90/bli);
				bid = new ImageUntils().imageZoomOut(bis,90,newHeight);
				//System.out.println(JFinal.me().getServletContext().getRealPath("/") + "data"+filePath.substring(0,filePath.lastIndexOf("."))+"_2"+filePath.substring(filePath.lastIndexOf(".")));
				newFilePath = filePath.substring(0,filePath.lastIndexOf("."))+"_2"+filePath.substring(filePath.lastIndexOf("."));
				File newFile = new File(JFinal.me().getServletContext().getRealPath("/") + "data"+newFilePath);
				ImageIO.write(bid,"jpeg",newFile);
			}catch (Exception e) {
				e.printStackTrace();
			}
			//缩放完毕
			outStream.flush();
		}
		}catch (Exception e) {
			error = 1;
			e.printStackTrace();
		}finally{
			try {
				if(outStream!=null)
					outStream.close();
				if(inputStream!=null)
					inputStream.close();
			} catch (IOException e) {
				error=1;
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		if(error==0){//保存文件信息到数据库
			SysFile sFile = new SysFile();
			sFile.set("fileName", fileName).set("fileSize", fileSize).set("filePath", filePath);
			sFile.save();
		}
		obj.put("error", error);
		obj.put("filePath", filePath);
		obj.put("newFilePath", newFilePath);
		obj.put("fileName", fileName);
		renderJson(obj.toJSONString());
	}
	private String createImage(String fileExt){
		String savePath = JFinal.me().getServletContext().getRealPath("/") + "data/";
		String saveUrl  = "/";
		savePath += "image" + "/";
		saveUrl += "image" + "/";
		java.io.File saveDirFile = new java.io.File(savePath);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "/";
		saveUrl += ymd + "/";
		java.io.File dirFile = new java.io.File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
		saveUrl+=newFileName;
		return saveUrl;
	}
	private boolean validateImage(String path){
		boolean reV=false;
		String extTarget = ",gif,jpg,jpeg,png,bmp,";
		if(StringKit.notBlank(path)){
			String extType = path.substring(path.lastIndexOf(".")+1).toLowerCase();
			if(extTarget.contains(","+extType+","))
				reV=true;
		}
		return reV;
	}
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}
}
