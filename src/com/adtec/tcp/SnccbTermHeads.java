package com.adtec.tcp;

class SnccbTermHeads {
	String AllLen;
	String Len;
	String Fixlen;
	String SvcName;
	String body;
	// byte t;
	/* 8+5+15+1+5+9+1+19+1+4+5+()+1(0xff) */

	public String getLen() {
		return Len;
	}

	public void setLen(String len) {
		this.Len = len;
	}

	public String getAllLen() {
		return AllLen;
	}

	public void setAllLen(String AllLen) {
		this.AllLen = AllLen;
	}

	public void setFixlen(String fixlen) {
		this.Fixlen = fixlen;
	}

	public String getFixlen() {
		return Fixlen;
	}

	public void setSvcName(String svcName) {
		this.SvcName = svcName;
	}

	public String getSvcName() {
		return SvcName;
	}

}