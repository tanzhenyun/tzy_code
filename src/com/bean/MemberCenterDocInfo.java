package com.bean;

import java.util.List;

public class MemberCenterDocInfo {
	private String membermechanism;
	private String memberpointdesc;
	private List<MemberCenterDtlInfo> dtllist;

	public String getMembermechanism() {
		return membermechanism;
	}

	public void setMembermechanism(String membermechanism) {
		this.membermechanism = membermechanism;
	}

	public String getMemberpointdesc() {
		return memberpointdesc;
	}

	public void setMemberpointdesc(String memberpointdesc) {
		this.memberpointdesc = memberpointdesc;
	}

	public List<MemberCenterDtlInfo> getDtllist() {
		return dtllist;
	}

	public void setDtllist(List<MemberCenterDtlInfo> dtllist) {
		this.dtllist = dtllist;
	}

}
