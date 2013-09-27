package com.ting.sysadm.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.render.CaptchaRender;
import com.ting.sysadm.config.SysConstant;
/**
 * 
 * @author aGen
 * 系统获取验证码类
 */
public class CodeController extends Controller {
	public void getCode() throws ServletException, IOException {
		BufferedImage checkedImage = new BufferedImage(50, 20, BufferedImage.TYPE_INT_RGB);
		// 得到该图片的绘图对象
		Graphics graphics = checkedImage.getGraphics();
		Random random = new Random();
		Color color = new Color(180, 180, 180);
		graphics.setColor(color);
		// 填充整个图片的颜色
		graphics.fillRect(0, 0, 50, 20);
		graphics.setFont(new Font("Arial", Font.BOLD, 18));
		// 向图片中输出数字和字母
		StringBuffer codeString = new StringBuffer();
		char[] optionalChars = "ABCDEFGHJKLMNPQRSTUVWXYZ123456789".toCharArray();
		int index, len = optionalChars.length;
		for (int i = 0; i < 4; i++) {
			index = random.nextInt(len);
			graphics.setColor(new Color(random.nextInt(88), random.nextInt(188), random.nextInt(255)));
			graphics.drawString(StringUtils.EMPTY + optionalChars[index], (i * 11) + 3, 18);// 写什么数字，在图片
			codeString.append(optionalChars[index]);
		}
		ImageIO.write(checkedImage, "PNG", this.getResponse().getOutputStream());
		this.getSession().setAttribute("code", codeString.toString());
	}
	public void code(){
		 CaptchaRender img = new CaptchaRender(SysConstant.RANDOM_CODE_KEY);
		 render(img);
	}
}
