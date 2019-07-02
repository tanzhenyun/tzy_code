package com.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取图片名字
 * 
 * @author KF_04
 * 
 */
public class Pic {
	
	/**
	 * 获取商品图片list
	 * 
	 * @param goodsid
	 * @return
	 */
	public List<String> getGoodsPicList(String goodsid) {
		File file = null;
		List<String> filenamelist = null;
		try {
			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\ecgoodspic\\" + goodsid;
			file = new File(pathname);
			filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int k = 0; k < fa.length; k++) {
					File fs = fa[k];
					if (fs.isDirectory()) {
						System.out.println(fs.getName() + " [目录]");
					} else {
						if (fs.getName().indexOf("jt") < 0) {
							filenamelist.add(fs.getName());
						}
					}
				}
				Collections.sort(filenamelist);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return filenamelist;
	}

	/**
	 * 获取商品图片 返回图片名字
	 * 
	 * @param goodsid
	 * @return
	 */
	public String getGoodsPic(String goodsid) {
		File file = null;
		String filename = "";
		try {
			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\ecgoodspic\\" + goodsid;
			file = new File(pathname);
			List<String> filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int k = 0; k < fa.length; k++) {
					File fs = fa[k];
					if (fs.isDirectory()) {
						System.out.println(fs.getName() + " [目录]");
					} else {
						if (fs.getName().indexOf("jt") < 0) {
							filenamelist.add(fs.getName());
						}
					}
				}
				Collections.sort(filenamelist);
				if (filenamelist.size() > 0)
					return filenamelist.get(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return filename;
	}

	/**
	 * 获取商品缩略图 返回图片名字
	 * 
	 * @param goodsid
	 * @return
	 */
	public String getThumbnailGoodsPic(String goodsid) {
		File file = null;
		String filename = "";
		try {
			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\ecgoodspic\\" + goodsid;
			file = new File(pathname);
			List<String> filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int k = 0; k < fa.length; k++) {
					File fs = fa[k];
					if (fs.isDirectory()) {
						System.out.println(fs.getName() + " [目录]");
					} else {
						filenamelist.add(fs.getName());
						if (fs.getName().indexOf("jt") > -1) {
							return fs.getName();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

	/**
	 * 获取门店图片 返回图片名字
	 * 
	 * @param attachmentid
	 * @return
	 */
	public String getShopPic(String attachmentid) {
		File file = null;
		String filename = "";
		String picpath = Parameter.getParameter("picpath");
		String pathname = picpath + "\\附件存储\\" + attachmentid;
		file = new File(pathname);
		// 递归求取目录文件个数
		File flist[] = file.listFiles();
		if (flist == null) {
		} else {
			File fa[] = file.listFiles();
			File fs = fa[0];
			if (fs.isDirectory()) {
				filename = "";
			} else {
				filename = fs.getName();
			}
		}
		return filename;
	}

	/**
	 * 获取活动图片名字
	 * 
	 * @param homeareadtlid
	 * @return
	 */
	public String getActivityPic(String homeareadtlid) {
		File file = null;
		String filename = "";
		try {
			String picpath = Parameter.getParameter("picpath");
			String pathname = picpath + "\\ecpic\\echomepic\\" + homeareadtlid;
			file = new File(pathname);
			List<String> filenamelist = new ArrayList<String>();
			// 递归求取目录文件个数
			File flist[] = file.listFiles();
			if (flist == null) {
			} else {
				file = new File(pathname);
				File fa[] = file.listFiles();
				for (int k = 0; k < fa.length; k++) {
					File fs = fa[k];
					if (fs.isDirectory()) {
						System.out.println(fs.getName() + " [目录]");
					} else {
						filenamelist.add(fs.getName());
					}
				}
				Collections.sort(filenamelist);
				if (filenamelist.size() > 0)
					return filenamelist.get(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return filename;
	}
}
