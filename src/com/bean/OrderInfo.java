package com.bean;

import java.util.List;

public class OrderInfo {
	private String orderid;
	private AddressInfo addressinfo;
	private List<CartInfo> cartinfolist;

	public AddressInfo getAddressinfo() {
		return addressinfo;
	}

	public void setAddressinfo(AddressInfo addressinfo) {
		this.addressinfo = addressinfo;
	}

	public List<CartInfo> getCartinfolist() {
		return cartinfolist;
	}

	public void setCartinfolist(List<CartInfo> cartinfolist) {
		this.cartinfolist = cartinfolist;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

}
