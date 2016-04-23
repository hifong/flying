package com.flying.common.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class SimpleTextAuthImage {
	// 生成随机类
	private final static Random random = new Random();
	
	private final int SIDE_WIDTH = 10;
	
	private int width = 110;
	private int height = 40;
	private String text = "";
	
	public SimpleTextAuthImage(String text, int width, int height) {
		this.text = text;
		this.width = width;
		this.height = height;
	}

	public SimpleTextAuthImage() {

		while(text.length() < 6) {
			int r = random.nextInt(128);
			if(r >= '0' && r <= '9' || r >= 'A' && r <= 'Z' || r >= 'a' && r <= 'z') {
				char c = (char)r;
				text += String.valueOf(c);
			}
		}
	}
	
	public String getText() {
		return this.text;
	}
	
	public void output(OutputStream os) throws IOException {
		// 在内存中创建图象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// 获取图形上下文
		Graphics g = image.getGraphics();

		// 设定背景色
		g.setColor(getRandColor(150, 250));
		g.fillRect(0, 0, width, height);
		// 设定字体

		// 画边框
		// g.setColor(new Color());
		// g.drawRect(0,0,width-1,height-1);

		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		for (int i = 0; i < 80; i++) {
			g.setColor(getRandColor(100, 250));
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(50);
			int yl = random.nextInt(50);
			g.drawLine(x, y, random.nextInt(10) % 2 == 0?x + xl:x - xl, random.nextInt(10) % 2 == 0?y + yl:y - yl);
		}
		// 取随机产生的认证码(4位数字)

		for(int i=0; i< text.length(); i++) {
			int fontsize = 15 + random.nextInt(24);
			g.setFont(new Font("Times New Roman", random.nextInt(2), fontsize));
			g.setColor(new Color(60 + random.nextInt(110), 60 + random.nextInt(110), 60 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(text.substring(i, i + 1), SIDE_WIDTH + (width - SIDE_WIDTH * 2) / text.length() * i, fontsize * 6 / 7 + random.nextInt(fontsize / 3));
		}
		// 图象生效
		g.dispose();
		// 输出图象到页面
		ImageIO.write(image, "JPEG", os);
		
		os.flush();
	}

	private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
	
	public static void main(String[] args) throws Exception{
		for(int i=0; i < 50; i++)
			new SimpleTextAuthImage().output(new FileOutputStream("H:\\a\\"+i+".jpg"));
	}
}
