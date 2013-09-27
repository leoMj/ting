package com.ting.sysadm.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.ting.sysadm.model.SysFile;
import com.ting.sysadm.tag.BlogUtils;
import com.ting.sysadm.tag.ImageUntils;

public class CommonController extends Controller {
	public void deleteFile(){
		String path = getPara("filePath");
		boolean isS = SysFile.dao.deleteByPath(path);
		if(isS){//物理删除文件
			File f = new File(JFinal.me().getServletContext().getRealPath("/")+"data"+path);
			File newFile = new File(JFinal.me().getServletContext().getRealPath("/")+"data"+path.substring(0,path.lastIndexOf("."))+"_2"+path.substring(path.lastIndexOf(".")));
			/**删除一张原始大小的图片**/
			if(f.exists())
				f.delete();
			if(newFile.exists())
				newFile.delete();
		}
		renderText(isS?"1":"0");
	}
	public void upload() {
		ServletContext servletContext = JFinal.me().getServletContext();
		// 文件保存目录路径
		String savePath = servletContext.getRealPath("/") + "data/";
		// 文件保存目录URL
		String saveUrl = "/";
		// 压缩后图片路径
		String newSaveUrl = "";
		// 定义允许上传的文件扩展名
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		// 最大文件大小
		long maxSize = 200000;

		if (!ServletFileUpload.isMultipartContent(this.getRequest())) {
			renderJson(BlogUtils.getError("请选择文件。"));
			return;
		}
		// 检查目录
		java.io.File uploadDir = new java.io.File(savePath);
		if (!uploadDir.isDirectory()) {
			renderJson(BlogUtils.getError("上传目录不存在。"));
			return;
		}
		// 检查目录写权限
		if (!uploadDir.canWrite()) {
			renderJson(BlogUtils.getError("上传目录没有写权限。"));
			return;
		}
		String imageScan = getPara("imageScan");
		String dirName = getPara("dir");
		if (dirName == null) {
			dirName = "image";
		}
		if (!extMap.containsKey(dirName)) {
			renderJson(BlogUtils.getError("目录名不正确。"));
			return;
		}
		// 创建文件夹
		savePath += dirName + "/";
		saveUrl += dirName + "/";
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

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List items = null;
		try {
			items = upload.parseRequest(this.getRequest());
		} catch (Exception e) {
			renderJson(BlogUtils.getError(e.getMessage()));
			return;
		}
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			String fileName = item.getName();
			long fileSize = item.getSize();
			if (!item.isFormField()) {
				// 检查文件大小
				if (item.getSize() > maxSize) {
					renderJson(BlogUtils.getError("上传文件大小超过限制"));
					return;
				}
				// 检查扩展名
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if (!Arrays.<String> asList(extMap.get(dirName).split(",")).contains(fileExt)) {
					renderJson(BlogUtils.getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
					return;
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
				saveUrl += newFileName;
				try {
					java.io.File uploadedFile = new java.io.File(savePath, newFileName);
					item.write(uploadedFile);
					BufferedImage bis = ImageIO.read(uploadedFile);
					BufferedImage bid = null;
					int newHeight = 32;
					int newWidth = 32;
					if (imageScan == null) {
						double bli = (double) bis.getWidth() / (double) bis.getHeight();
						newHeight = (int) Math.round(90 / bli);
						newWidth = 90;
					}
					bid = new ImageUntils().imageZoomOut(bis, newWidth, newHeight);
					newSaveUrl = saveUrl.substring(0, saveUrl.lastIndexOf(".")) + "_2" + saveUrl.substring(saveUrl.lastIndexOf("."));
					System.out.println(servletContext.getRealPath("/") + "data" + saveUrl.substring(0, saveUrl.lastIndexOf(".")) + "_2" + saveUrl.substring(saveUrl.lastIndexOf(".")));
					File newFile = new File(servletContext.getRealPath("/") + "data" + newSaveUrl);
					ImageIO.write(bid, "jpeg", newFile);
					// 同步数据库
					try {
						SysFile sFile = new SysFile();
						sFile.set("fileName", fileName).set("fileSize", fileSize).set("filePath", saveUrl);
						sFile.save();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					renderJson(BlogUtils.getError(e.getMessage()));
					return;
				}
				JSONObject obj = new JSONObject();
				obj.put("error", 0);
				obj.put("url", saveUrl);
				obj.put("name", fileName);
				obj.put("newSaveUrl", newSaveUrl);
				renderJson(obj.toJSONString());
				return;
			}
		}

	}
}
