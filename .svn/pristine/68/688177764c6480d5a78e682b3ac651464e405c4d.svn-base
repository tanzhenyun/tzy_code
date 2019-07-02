package com.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.util.Parameter;

/**
 * 读取图片类
 * 
 * @author KF_04
 * 
 */
@Controller
public class ReadPictureController {

	private Logger logger = Logger.getLogger(ReadPictureController.class);

	/**
	 * 读取商品图
	 * 
	 * @param request
	 * @param response
	 */
	// @RequestMapping("goodspic.do")
	// public void checkGoodsQty(HttpServletRequest request, HttpServletResponse
	// response) {
	// try {
	// String goodsid = request.getParameter("goodsid");
	// String name = request.getParameter("name");
	// String picpath = Parameter.getParameter("picpath");
	// String path = picpath + "\\ecpic\\ecgoodspic\\" + goodsid + "\\" + name;
	// FileInputStream fileInputStream = new FileInputStream(new File(path));
	// BufferedInputStream bis = new BufferedInputStream(fileInputStream);
	// byte[] buffer = new byte[1024 * 10];
	// response.reset();
	// response.setCharacterEncoding("UTF-8");
	// // 不同类型的文件对应不同的MIME类型
	// response.setContentType("image/png");
	// response.setHeader("Content-Disposition", "attachment;
	// filename=img.jpg");
	// response.setContentLength(bis.available());
	// OutputStream os = response.getOutputStream();
	// int n;
	// while ((n = bis.read(buffer)) != -1) {
	// os.write(buffer, 0, n);
	// }
	// bis.close();
	// os.flush();
	// os.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("error", e);
	// }
	// }

	/**
	 * 读取商品图
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("goodspic.do")
	public void goodsPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// InputStream is = null;
		// BufferedImage bi = null;
		// int results[] = { 0, 0 };
		// BufferedImage tag = null;
		// OutputStream fout = null;
		// try {
		String goodsid = request.getParameter("goodsid");
		String name = request.getParameter("name");
		String picpath = Parameter.getParameter("picpath");
		String path = picpath + "\\ecpic\\ecgoodspic\\" + goodsid + "\\" + name;

		// File file = new File(path);
		// BufferedInputStream bis = new BufferedInputStream(new
		// FileInputStream(file));
		// BufferedOutputStream bos = new
		// BufferedOutputStream(response.getOutputStream());
		// response.setHeader("Content-Type", "image/jpeg");
		// byte b[] = new byte[1024];
		// int read;
		// try {
		// while ((read = bis.read(b)) != -1) {
		// bos.write(b, 0, read);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (bos != null) {
		// bos.close();
		// }
		// if (bis != null) {
		// bis.close();
		// }
		// }

		// -----------------------------
		// String fileName = file.getName();
		// String substring = fileName.substring(fileName.lastIndexOf(".") +
		// 1).toLowerCase();
		// response.setContentType("image/" + substring); // 设置返回的文件类型
		// response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
		// IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		// -----------------------------
		// BufferedInputStream bis = new BufferedInputStream(new
		// FileInputStream(file));
		// BufferedOutputStream bos = new
		// BufferedOutputStream(response.getOutputStream());
		// response.setHeader("Content-Type", "image/*");// 设置返回的文件类型
		// response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
		//
		// byte b[] = new byte[1024];
		// int read;
		// while ((read = bis.read(b)) != -1) {
		// bos.write(b, 0, read);
		// }
		// bos.close();
		// bis.close();
		// ----------------------
		OutputStream os = null;
		FileInputStream fis = null;
		os = response.getOutputStream();
		fis = new FileInputStream(path);
		byte[] buffer = new byte[1024];
		while (fis.read(buffer) != -1) {
			os.write(buffer);
		}
		os.flush();
		os.close();
		fis.close();

		// File file = new File(path);
		// if (!file.exists()) {
		// return;
		// }
		// double rate = 1; // rate是压缩比率 1为原图 0.1为最模糊
		// is = new FileInputStream(file);
		// bi = ImageIO.read(file);
		// results[0] = bi.getWidth(null); // 得到源图宽
		// results[1] = bi.getHeight(null); // 得到源图高

		// int widthdist = 0;
		// int heightdist = 0;
		// if (results == null || results[0] == 0 || results[1] == 0) {
		// return;
		// } else {
		// widthdist = (int) (results[0] * rate);
		// heightdist = (int) (results[1] * rate);
		// }
		// ImageIcon src = new ImageIcon(file.getAbsolutePath());
		// ImageIcon src = new ImageIcon(path);

		// tag = new BufferedImage(src.getIconWidth(), src.getIconHeight(),
		// BufferedImage.TYPE_INT_RGB);
		// tag.getGraphics().drawImage(src.getImage(), 0, 0, null);
		// fout = response.getOutputStream();
		// ImageIO.write(tag, "PNG", fout);
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.error("error", e);
		// } finally {
		// fout.flush();
		// fout.close();
		// is.close();
		// }
	}

	/**
	 * 读取门店图
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("shoppic.do")
	public void shopPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// InputStream is = null;
		// OutputStream fout = null;
		try {
			String attachmentid = request.getParameter("attachmentid");
			String name = request.getParameter("name");
			String picpath = Parameter.getParameter("picpath");
			String path = picpath + "\\附件存储\\" + attachmentid + "\\" + name;

			File file = new File(path);
			// if (!file.exists()) {
			// return;
			// }
			// is = new FileInputStream(file);
			// ImageIcon src = new ImageIcon(file.getAbsolutePath());
			// ImageIcon src = new ImageIcon(path);
			//
			// BufferedImage tag = new BufferedImage(src.getIconWidth(),
			// src.getIconHeight(), BufferedImage.TYPE_INT_RGB);
			// tag.getGraphics().drawImage(src.getImage(), 0, 0, null);
			// fout = response.getOutputStream();
			// ImageIO.write(tag, "PNG", fout);
			// -------------------
			// String fileName = file.getName();
			// String substring = fileName.substring(fileName.lastIndexOf(".") +
			// 1).toLowerCase();
			// response.setContentType("image/" + substring); // 设置返回的文件类型
			// response.setHeader("Access-Control-Allow-Origin", "*");//
			// 设置该图片允许跨域访问
			// IOUtils.copy(new FileInputStream(file),
			// response.getOutputStream());
			// ---------------------
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			response.setHeader("Content-Type", "image/*");
			response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
			byte b[] = new byte[1024];
			int read;
			while ((read = bis.read(b)) != -1) {
				bos.write(b, 0, read);
			}
			bos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// fout.flush();
			// fout.close();
			// is.close();
		}
	}

	/**
	 * 读取首页活动图片
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("activitypic.do")
	public void activityPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// InputStream is = null;
		// BufferedImage tag = null;
		// OutputStream fout = null;

		// try {
		String name = request.getParameter("name");
		String homeareadtlid = request.getParameter("homeareadtlid");
		String picpath = Parameter.getParameter("picpath");
		String path = picpath + "\\ecpic\\echomepic\\" + homeareadtlid + "\\" + name;

		File file = new File(path);
		// BufferedInputStream bis = new BufferedInputStream(new
		// FileInputStream(file));
		// BufferedOutputStream bos = new
		// BufferedOutputStream(response.getOutputStream());
		// response.setHeader("Content-Type", "image/jpeg");
		// byte b[] = new byte[1024];
		// int read;
		// try {
		// while ((read = bis.read(b)) != -1) {
		// bos.write(b, 0, read);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (bos != null) {
		// bos.close();
		// }
		// if (bis != null) {
		// bis.close();
		// }
		// }

		// ------------------------
		// String fileName = file.getName();
		// String substring = fileName.substring(fileName.lastIndexOf(".") +
		// 1).toLowerCase();
		// response.setContentType("image/" + substring); // 设置返回的文件类型
		// response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
		// IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		// ----------------------------
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		response.setHeader("Content-Type", "image/*");
		response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
		byte b[] = new byte[1024];
		int read;
		while ((read = bis.read(b)) != -1) {
			bos.write(b, 0, read);
		}
		bos.close();
		bis.close();
		// File file = new File(path);
		// if (!file.exists()) {
		// return;
		// }
		// is = new FileInputStream(file);
		// ImageIcon src = new ImageIcon(file.getAbsolutePath());
		// ImageIcon src = new ImageIcon(path);
		//
		// BufferedImage tag = new BufferedImage(src.getIconWidth(),
		// src.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		// tag.getGraphics().drawImage(src.getImage(), 0, 0, null);
		// fout = response.getOutputStream();
		// ImageIO.write(tag, "PNG", fout);
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// fout.flush();
		// fout.close();
		// is.close();
		// }
	}

	/**
	 * 活动图片
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("eventpic.do")
	public void eventPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// InputStream is = null;
		// OutputStream fout = null;
		try {
			String name = request.getParameter("name");
			String eventid = request.getParameter("eventid");
			String picpath = Parameter.getParameter("picpath");
			String path = picpath + "\\ecpic\\eventpic\\" + eventid + "\\" + name;

			File file = new File(path);
			// if (!file.exists()) {
			// return;
			// }
			// is = new FileInputStream(file);
			// ImageIcon src = new ImageIcon(file.getAbsolutePath());
			// ImageIcon src = new ImageIcon(path);
			//
			// BufferedImage tag = new BufferedImage(src.getIconWidth(),
			// src.getIconHeight(), BufferedImage.TYPE_INT_RGB);
			// tag.getGraphics().drawImage(src.getImage(), 0, 0, null);
			// fout = response.getOutputStream();
			// ImageIO.write(tag, "PNG", fout);
			// -------------------------
			// String fileName = file.getName();
			// String substring = fileName.substring(fileName.lastIndexOf(".") +
			// 1).toLowerCase();
			// response.setContentType("image/" + substring); // 设置返回的文件类型
			// response.setHeader("Access-Control-Allow-Origin", "*");//
			// 设置该图片允许跨域访问
			// IOUtils.copy(new FileInputStream(file),
			// response.getOutputStream());
			// ------------------------
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			response.setHeader("Content-Type", "image/*");
			response.setHeader("Access-Control-Allow-Origin", "*");// 设置该图片允许跨域访问
			byte b[] = new byte[1024];
			int read;
			while ((read = bis.read(b)) != -1) {
				bos.write(b, 0, read);
			}
			bos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// fout.flush();
			// fout.close();
			// is.close();
		}
	}

}
