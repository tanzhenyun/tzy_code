package com.bean;

import java.util.List;

public class HomeAreaDoc {
	private String homeareadocid;
	private String homeareaname;
	private String position;
	private String height;
	private int piccount;
	private String titleflag;
	private List<HomeAreaDtl> dtllist;

	public String getHomeareadocid() {
		return homeareadocid;
	}

	public void setHomeareadocid(String homeareadocid) {
		this.homeareadocid = homeareadocid;
	}

	public String getHomeareaname() {
		return homeareaname;
	}

	public void setHomeareaname(String homeareaname) {
		this.homeareaname = homeareaname;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public int getPiccount() {
		return piccount;
	}

	public void setPiccount(int piccount) {
		this.piccount = piccount;
	}

	public String getTitleflag() {
		return titleflag;
	}

	public void setTitleflag(String titleflag) {
		this.titleflag = titleflag;
	}

	public List<HomeAreaDtl> getDtllist() {
		return dtllist;
	}

	public void setDtllist(List<HomeAreaDtl> dtllist) {
		this.dtllist = dtllist;
	}

}
