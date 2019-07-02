package com.bean;

import java.util.List;

public class ShopInfo {
	private String shopid;
	private String shopname;
	private String companyid;
	private String companyname;
	private String addressid;
	private String address;
	private String tel;
	private String attachmentid;
	private String picname;
	// 图片文件夹地址
	private String folderpath;
	private List<WholeSPInfo> wholelist;

	// 经度
	private String longitude;
	// 纬度
	private String latitude;

	private List infolist;

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

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getAddressid() {
		return addressid;
	}

	public void setAddressid(String addressid) {
		this.addressid = addressid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getPicname() {
		return picname;
	}

	public void setPicname(String picname) {
		this.picname = picname;
	}

	public String getAttachmentid() {
		return attachmentid;
	}

	public void setAttachmentid(String attachmentid) {
		this.attachmentid = attachmentid;
	}

	public String getFolderpath() {
		return folderpath;
	}

	public void setFolderpath(String folderpath) {
		this.folderpath = folderpath;
	}

	public List getInfolist() {
		return infolist;
	}

	public void setInfolist(List infolist) {
		this.infolist = infolist;
	}

	public List<WholeSPInfo> getWholelist() {
		return wholelist;
	}

	public void setWholelist(List<WholeSPInfo> wholelist) {
		this.wholelist = wholelist;
	}

}
