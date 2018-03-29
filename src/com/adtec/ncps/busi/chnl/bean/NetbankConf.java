package com.adtec.ncps.busi.chnl.bean;

import java.io.Serializable;

public class NetbankConf implements Serializable {
    private String CODE;

    private String TYPE;

    private String TYPE_SEQ;

    private String NEXTTO;

    private String CODE_DESC;

    private String TYPE_FROM;

    private String TYPE_TO;

    private String TYPE_EXPLAIN;

    private String TYPE_FILE;

    private String TYPE_ROOT;

    private String TYPE_CONTENTS;

    private String TYPE_DATATYPE;

    private String TYPE_PURPOSE;

    private String TYPE_FILEBEGIN;

    private String TYPE_CONTENTSCONT;

    private String TYPE_ISNULL;

    private String TYPE_EXPR;

    private String TYPE_CHECK;

    private String BK1;

    private String BK2;

    private String BK3;

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE == null ? null : CODE.trim();
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE == null ? null : TYPE.trim();
    }

    public String getTYPE_SEQ() {
        return TYPE_SEQ;
    }

    public void setTYPE_SEQ(String TYPE_SEQ) {
        this.TYPE_SEQ = TYPE_SEQ == null ? null : TYPE_SEQ.trim();
    }

    public String getNEXTTO() {
        return NEXTTO;
    }

    public void setNEXTTO(String NEXTTO) {
        this.NEXTTO = NEXTTO == null ? null : NEXTTO.trim();
    }

    public String getCODE_DESC() {
        return CODE_DESC;
    }

    public void setCODE_DESC(String CODE_DESC) {
        this.CODE_DESC = CODE_DESC == null ? null : CODE_DESC.trim();
    }

    public String getTYPE_FROM() {
        return TYPE_FROM;
    }

    public void setTYPE_FROM(String TYPE_FROM) {
        this.TYPE_FROM = TYPE_FROM == null ? null : TYPE_FROM.trim();
    }

    public String getTYPE_TO() {
        return TYPE_TO;
    }

    public void setTYPE_TO(String TYPE_TO) {
        this.TYPE_TO = TYPE_TO == null ? null : TYPE_TO.trim();
    }

    public String getTYPE_EXPLAIN() {
        return TYPE_EXPLAIN;
    }

    public void setTYPE_EXPLAIN(String TYPE_EXPLAIN) {
        this.TYPE_EXPLAIN = TYPE_EXPLAIN == null ? null : TYPE_EXPLAIN.trim();
    }

    public String getTYPE_FILE() {
        return TYPE_FILE;
    }

    public void setTYPE_FILE(String TYPE_FILE) {
        this.TYPE_FILE = TYPE_FILE == null ? null : TYPE_FILE.trim();
    }

    public String getTYPE_ROOT() {
        return TYPE_ROOT;
    }

    public void setTYPE_ROOT(String TYPE_ROOT) {
        this.TYPE_ROOT = TYPE_ROOT == null ? null : TYPE_ROOT.trim();
    }

    public String getTYPE_CONTENTS() {
        return TYPE_CONTENTS;
    }

    public void setTYPE_CONTENTS(String TYPE_CONTENTS) {
        this.TYPE_CONTENTS = TYPE_CONTENTS == null ? null : TYPE_CONTENTS.trim();
    }

    public String getTYPE_DATATYPE() {
        return TYPE_DATATYPE;
    }

    public void setTYPE_DATATYPE(String TYPE_DATATYPE) {
        this.TYPE_DATATYPE = TYPE_DATATYPE == null ? null : TYPE_DATATYPE.trim();
    }

    public String getTYPE_PURPOSE() {
        return TYPE_PURPOSE;
    }

    public void setTYPE_PURPOSE(String TYPE_PURPOSE) {
        this.TYPE_PURPOSE = TYPE_PURPOSE == null ? null : TYPE_PURPOSE.trim();
    }

    public String getTYPE_FILEBEGIN() {
        return TYPE_FILEBEGIN;
    }

    public void setTYPE_FILEBEGIN(String TYPE_FILEBEGIN) {
        this.TYPE_FILEBEGIN = TYPE_FILEBEGIN == null ? null : TYPE_FILEBEGIN.trim();
    }

    public String getTYPE_CONTENTSCONT() {
        return TYPE_CONTENTSCONT;
    }

    public void setTYPE_CONTENTSCONT(String TYPE_CONTENTSCONT) {
        this.TYPE_CONTENTSCONT = TYPE_CONTENTSCONT == null ? null : TYPE_CONTENTSCONT.trim();
    }

    public String getTYPE_ISNULL() {
        return TYPE_ISNULL;
    }

    public void setTYPE_ISNULL(String TYPE_ISNULL) {
        this.TYPE_ISNULL = TYPE_ISNULL == null ? null : TYPE_ISNULL.trim();
    }

    public String getTYPE_EXPR() {
        return TYPE_EXPR;
    }

    public void setTYPE_EXPR(String TYPE_EXPR) {
        this.TYPE_EXPR = TYPE_EXPR == null ? null : TYPE_EXPR.trim();
    }

    public String getTYPE_CHECK() {
        return TYPE_CHECK;
    }

    public void setTYPE_CHECK(String TYPE_CHECK) {
        this.TYPE_CHECK = TYPE_CHECK == null ? null : TYPE_CHECK.trim();
    }

    public String getBK1() {
        return BK1;
    }

    public void setBK1(String BK1) {
        this.BK1 = BK1 == null ? null : BK1.trim();
    }

    public String getBK2() {
        return BK2;
    }

    public void setBK2(String BK2) {
        this.BK2 = BK2 == null ? null : BK2.trim();
    }

    public String getBK3() {
        return BK3;
    }

    public void setBK3(String BK3) {
        this.BK3 = BK3 == null ? null : BK3.trim();
    }

	@Override
	public String toString() {
		return "T_NETBANK_CONF [CODE=" + CODE + ", TYPE=" + TYPE + ", TYPE_SEQ=" + TYPE_SEQ + ", NEXTTO=" + NEXTTO
				+ ", CODE_DESC=" + CODE_DESC + ", TYPE_FROM=" + TYPE_FROM + ", TYPE_TO=" + TYPE_TO + ", TYPE_EXPLAIN="
				+ TYPE_EXPLAIN + ", TYPE_FILE=" + TYPE_FILE + ", TYPE_ROOT=" + TYPE_ROOT + ", TYPE_CONTENTS="
				+ TYPE_CONTENTS + ", TYPE_DATATYPE=" + TYPE_DATATYPE + ", TYPE_PURPOSE=" + TYPE_PURPOSE
				+ ", TYPE_FILEBEGIN=" + TYPE_FILEBEGIN + ", TYPE_CONTENTSCONT=" + TYPE_CONTENTSCONT + ", TYPE_ISNULL="
				+ TYPE_ISNULL + ", TYPE_EXPR=" + TYPE_EXPR + ", TYPE_CHECK=" + TYPE_CHECK + ", BK1=" + BK1 + ", BK2="
				+ BK2 + ", BK3=" + BK3 + "]";
	}

	public NetbankConf(String cODE, String tYPE, String tYPE_SEQ, String nEXTTO, String cODE_DESC, String tYPE_FROM,
			String tYPE_TO, String tYPE_EXPLAIN, String tYPE_FILE, String tYPE_ROOT, String tYPE_CONTENTS,
			String tYPE_DATATYPE, String tYPE_PURPOSE, String tYPE_FILEBEGIN, String tYPE_CONTENTSCONT,
			String tYPE_ISNULL, String tYPE_EXPR, String tYPE_CHECK, String bK1, String bK2, String bK3) {
		super();
		CODE = cODE;
		TYPE = tYPE;
		TYPE_SEQ = tYPE_SEQ;
		NEXTTO = nEXTTO;
		CODE_DESC = cODE_DESC;
		TYPE_FROM = tYPE_FROM;
		TYPE_TO = tYPE_TO;
		TYPE_EXPLAIN = tYPE_EXPLAIN;
		TYPE_FILE = tYPE_FILE;
		TYPE_ROOT = tYPE_ROOT;
		TYPE_CONTENTS = tYPE_CONTENTS;
		TYPE_DATATYPE = tYPE_DATATYPE;
		TYPE_PURPOSE = tYPE_PURPOSE;
		TYPE_FILEBEGIN = tYPE_FILEBEGIN;
		TYPE_CONTENTSCONT = tYPE_CONTENTSCONT;
		TYPE_ISNULL = tYPE_ISNULL;
		TYPE_EXPR = tYPE_EXPR;
		TYPE_CHECK = tYPE_CHECK;
		BK1 = bK1;
		BK2 = bK2;
		BK3 = bK3;
	}

	public NetbankConf() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}