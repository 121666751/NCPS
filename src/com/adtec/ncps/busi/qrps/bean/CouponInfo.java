package com.adtec.ncps.busi.qrps.bean;

public class CouponInfo {
    String type;
    String spnsrId;
    String offstAmt;
    String id;
    String desc;
    String addnInfo;

    /**
     * @return type
     */

    public String getType() {
	return type;
    }

    /**
     * @param type
     *            the type to set
     */

    public void setType(String type) {
	this.type = type;
    }

    /**
     * @return spnsrId
     */

    public String getSpnsrId() {
	return spnsrId;
    }

    /**
     * @param spnsrId
     *            the spnsrId to set
     */

    public void setSpnsrId(String spnsrId) {
	this.spnsrId = spnsrId;
    }

    /**
     * @return offstAmt
     */

    public String getOffstAmt() {
	return offstAmt;
    }

    /**
     * @param offstAmt
     *            the offstAmt to set
     */

    public void setOffstAmt(String offstAmt) {
	this.offstAmt = offstAmt;
    }

    /**
     * @return id
     */

    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return desc
     */

    public String getDesc() {
	return desc;
    }

    /**
     * @param desc
     *            the desc to set
     */

    public void setDesc(String desc) {
	this.desc = desc;
    }

    /**
     * @return addnInfo
     */

    public String getAddnInfo() {
	return addnInfo;
    }

    /**
     * @param addnInfo
     *            the addnInfo to set
     */

    public void setAddnInfo(String addnInfo) {
	this.addnInfo = addnInfo;
    }

    /*
     * (非 Javadoc)
     * 
     * 
     * @return
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
	return "CouponInfo [type=" + type + ", spnsrId=" + spnsrId + ", offstAmt=" + offstAmt + ", id=" + id + ", desc="
		+ desc + ", addnInfo=" + addnInfo + "]";
    }

}
