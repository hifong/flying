package com.flying.common.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.flying.common.cache.Cache;
import com.flying.common.cache.HashMapCache;
import com.flying.common.des.DESPlus;
import com.flying.common.log.Logger;
import com.flying.common.util.StringUtils;

import sun.misc.BASE64Decoder;

public class AppRandomCodeServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(AppRandomCodeServlet.class);
	private static final long serialVersionUID = 2581121699946039859L;
	private DESPlus des;
	private BASE64Decoder base64Decoder = new BASE64Decoder();
	Cache<byte[]> cache = new HashMapCache<byte[]>(AppRandomCodeServlet.class.getName());

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		String despluskey = this.getInitParameter("despluskey");
		try {
			des = new DESPlus(despluskey, "DES/ECB/NoPadding");
		} catch (Exception e) {
			logger.error("AppRandomCodeServlet:init()", e);
		}
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		String param1 = request.getParameter("param1");
		String code = request.getParameter("param2");// uri.substring(uri.lastIndexOf("/")+1,
														// uri.lastIndexOf(".")==-1?uri.length():uri.lastIndexOf("."));

		if (StringUtils.isBlank(param1) || StringUtils.isBlank(code)) {
			logger.error("param1[" + param1 + "] or param2[" + code + "] is null!");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		byte[] data = cache.get(code);
		if (data != null && data.length > 0) {
			ServletOutputStream sos = response.getOutputStream();
			IOUtils.write(data, sos);
			sos.flush();
			return;
		}

		String width = this.getServletConfig().getInitParameter("width");
		String height = this.getServletConfig().getInitParameter("height");

		byte[] bytes = base64Decoder.decodeBuffer(code);
		byte[] codeBytes = des.decrypt(bytes);
		BufferedImage image = createRandomImage(width != null ? Integer.valueOf(width) : 60, height != null ? Integer.valueOf(height) : 25, new String(
						codeBytes, 0, 4));

		// 将图像输出到Servlet输出流中。
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpeg", bos);
			data = bos.toByteArray();
			cache.put(code, data);
			ServletOutputStream sos = response.getOutputStream();
			IOUtils.write(data, sos);
			sos.flush();
		} catch (IOException e) {
			logger.error("创建图形验证码失败!", e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private BufferedImage createRandomImage(int width, int height, String randcode) {
		// 定义图像buffer
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D gd = buffImg.createGraphics();
		// 创建一个随机数生成器类
		Random random = new Random(new Date().getTime());
		// 将图像填充为白色
		gd.setColor(Color.decode("#DFFFDF"));
		gd.fillRect(0, 0, width, height);
		// 创建字体，字体的大小应该根据图片的高度来定。
		// Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
		Font font = new Font("Times New Roman", Font.BOLD, height * 2 / 3);
		gd.setFont(font);
		// 画边框
		// gd.setColor(Color.BLACK);
		// gd.drawRect(0, 0, width - 1, height - 1);
		// gd.setColor(Color.YELLOW);
		// gd.drawRect(1, 1, width - 1, height - 1);
		// gd.drawRect(0, 0, width - 2, height - 2);

		// 随机产生3条干扰线，使图象中的认证码不易被其它程序探测到,干扰线太明显则会导致用户无法看清验证码
		String[] color = { "#061D7B", "#4E8C50", "#516B10", "#3E5781", "#2E5781", "#190710", "#31D222", "#3A3506", "#4FDA28", "#46CA7F", "#1D1D1F", "#6F3F29" };
		// gd.setColor(Color.decode("#7AFEC6"));
		for (int i = 0; i < 8; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = width / 2 + random.nextInt(width);
			int yl = height / 2 + random.nextInt(height);
			gd.setColor(Color.decode(color[random.nextInt(999) % color.length]));
			gd.drawLine(x, y, xl, yl);
		}

		// randomCode用于保存随机产生的验证码。
		StringBuffer randomCode = new StringBuffer();

		// 随机产生codeCount数字的验证码。
		int count = randcode.length();
		for (int i = 0; i < count; i++) {
			// 得到随机产生的验证码数字。
			// String strRand =
			// String.valueOf(codeSequence[random.nextInt(36)]);
			String strRand = randcode.substring(i, i + 1);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			// red = random.nextInt(255);
			// green = random.nextInt(255);
			// blue = random.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中。
			gd.setColor(Color.decode(color[random.nextInt(999) % color.length]));
			// gd.drawString(strRand, (i * xx) + xx / 2, codeY);
			//
			gd.drawString(strRand, ((width - 10 - count) / count + 1) * i + 5, height * 7 / 10);

			// 将产生的四个随机数组合在一起。
			randomCode.append(strRand);
		}
		return buffImg;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			processRequest(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

}
