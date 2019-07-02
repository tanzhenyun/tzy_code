package com.bean;

import java.util.List;

public class GoodsInfo {
	// 商品ID
	private String goodsid;
	// 商品名称
	private String goodsname;
	// 规格
	private String goodsspecs;
	// 产地
	private String prodarea;
	// 厂家ID
	private String factoryid;
	// 厂家名称
	private String factoryname;
	// 批号ID
	private String lotid;
	// 批号
	private String lotno;
	// 有效期至
	private String validdate;
	// 基本包装
	private String basepackname;
	// 基本包装数量
	private String basepackqty;
	// 包装ID
	private String goodspackid;
	// 包装名称
	private String packname;
	// 包装大小
	private String packsize;
	// 商品状态ID
	private String goodsstatusid;
	// 商品状态
	private String goodsstatus;
	// 基本包装数量
	private String goodsqty;
	// 库位数量
	private String inventoryqty;
	// 补货数量
	private String tradegoodsqty;
	// 价格
	private String price;
	// 总价
	private String amount;
	// 门店ID
	private String shopid;
	// 门店名字
	private String shopname;
	// 图片名字
	private String picname;
	// OTC标志
	private String otcflag;
	// 主药标志
	private String importflag;
	// 辅药标志
	private String assistflag;
	// 促销数量
	private String spcount;
	// 单品促销信息
	private List<SingleSPInfo> singlelist;
	// 整单促销信息
	private List<WholeSPInfo> wholelist;
	// 图片文件夹地址
	private String folderpath;

	private List<Object> list;

	public String getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public String getGoodsspecs() {
		return goodsspecs;
	}

	public void setGoodsspecs(String goodsspecs) {
		this.goodsspecs = goodsspecs;
	}

	public String getProdarea() {
		return prodarea;
	}

	public void setProdarea(String prodarea) {
		this.prodarea = prodarea;
	}

	public String getFactoryid() {
		return factoryid;
	}

	public void setFactoryid(String factoryid) {
		this.factoryid = factoryid;
	}

	public String getFactoryname() {
		return factoryname;
	}

	public void setFactoryname(String factoryname) {
		this.factoryname = factoryname;
	}

	public String getLotid() {
		return lotid;
	}

	public void setLotid(String lotid) {
		this.lotid = lotid;
	}

	public String getLotno() {
		return lotno;
	}

	public void setLotno(String lotno) {
		this.lotno = lotno;
	}

	public String getValiddate() {
		return validdate;
	}

	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}

	public String getBasepackname() {
		return basepackname;
	}

	public void setBasepackname(String basepackname) {
		this.basepackname = basepackname;
	}

	public String getBasepackqty() {
		return basepackqty;
	}

	public void setBasepackqty(String basepackqty) {
		this.basepackqty = basepackqty;
	}

	public String getGoodspackid() {
		return goodspackid;
	}

	public void setGoodspackid(String goodspackid) {
		this.goodspackid = goodspackid;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public String getPacksize() {
		return packsize;
	}

	public void setPacksize(String packsize) {
		this.packsize = packsize;
	}

	public String getGoodsstatusid() {
		return goodsstatusid;
	}

	public void setGoodsstatusid(String goodsstatusid) {
		this.goodsstatusid = goodsstatusid;
	}

	public String getGoodsstatus() {
		return goodsstatus;
	}

	public void setGoodsstatus(String goodsstatus) {
		this.goodsstatus = goodsstatus;
	}

	public String getGoodsqty() {
		return goodsqty;
	}

	public void setGoodsqty(String goodsqty) {
		this.goodsqty = goodsqty;
	}

	public String getInventoryqty() {
		return inventoryqty;
	}

	public void setInventoryqty(String inventoryqty) {
		this.inventoryqty = inventoryqty;
	}

	public String getTradegoodsqty() {
		return tradegoodsqty;
	}

	public void setTradegoodsqty(String tradegoodsqty) {
		this.tradegoodsqty = tradegoodsqty;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getShopid() {
		return shopid;
	}

	public void setShopid(String shopid) {
		this.shopid = shopid;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public String getPicname() {
		return picname;
	}

	public void setPicname(String picname) {
		this.picname = picname;
	}

	public String getOtcflag() {
		return otcflag;
	}

	public void setOtcflag(String otcflag) {
		this.otcflag = otcflag;
	}

	public String getImportflag() {
		return importflag;
	}

	public void setImportflag(String importflag) {
		this.importflag = importflag;
	}

	public String getAssistflag() {
		return assistflag;
	}

	public void setAssistflag(String assistflag) {
		this.assistflag = assistflag;
	}

	public String getSpcount() {
		return spcount;
	}

	public void setSpcount(String spcount) {
		this.spcount = spcount;
	}

	public List<SingleSPInfo> getSinglelist() {
		return singlelist;
	}

	public void setSinglelist(List<SingleSPInfo> singlelist) {
		this.singlelist = singlelist;
	}

	public List<WholeSPInfo> getWholelist() {
		return wholelist;
	}

	public void setWholelist(List<WholeSPInfo> wholelist) {
		this.wholelist = wholelist;
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public String getFolderpath() {
		return folderpath;
	}

	public void setFolderpath(String folderpath) {
		this.folderpath = folderpath;
	}

}
