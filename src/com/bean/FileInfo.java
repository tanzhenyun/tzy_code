package com.bean;

import java.util.List;

public class FileInfo {
	private GoodsInfo goodsinfo;
	private List<String> filenamelist;
	private String txt;

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public List<String> getFilenamelist() {
		return filenamelist;
	}

	public void setFilenamelist(List<String> filenamelist) {
		this.filenamelist = filenamelist;
	}

	public GoodsInfo getGoodsinfo() {
		return goodsinfo;
	}

	public void setGoodsinfo(GoodsInfo goodsinfo) {
		this.goodsinfo = goodsinfo;
	}

}
