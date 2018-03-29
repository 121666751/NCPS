package com.adtec.ncps.busi.ncp.bean;

public class Book {
	private String plat_date;
	private Integer seq_no;
	private String tx_code;
	private String tx_name;
	private String tx_date;
	private String tx_seq;
	private String pay_brch;
	private String pay_acct_no;
	private String pay_acct_type;
	private String pay_acct_name;
	private String pay_cert_type;
	private String pay_cert_no;
	private String pay_phn;
	private String payee_brch;
	private String payee_acct_no;
	private String payee_acct_type;
	private String payee_acct_name;
	private String payee_cert_type;
	private String payee_cert_no;
	private String payee_area;
	private String tx_type;
	private String entr_no;
	private String chnl_no;
	private String snd_time;
	private String snd_brch;
	private String snd_brch_no;
	private String snd_acct_brch;
	private String brch_no;
	private String busi_type;
	private String teller_no;
	private String chk_act_no;
	private String oth_date;
	private String oth_seq;
	private Double tx_amt=0.00;
	private String acct_input;
	private String term_type;
	private String term_no;
	private String rp_flag;
	private String clear_date;
	private String sign_no;
	private String Product_type;
	private String order_no;
	private String ori_oth_seq;
	private Double ori_tx_amt=0.00;
	private String ori_order_no;
	private String ori_tx_date;
	private String acct_lvl;
	private String chk_stat;
	private String stat;
	private String ret_code;
	private String ret_msg;
	private String host_msg;
	private String ret_time;
	private Integer time_sec;
	private String open_brch;
	private String host_date;
	private String host_seq;
	private Double refund_amt=0.00;
	private String chk_flag;
	private String chk_msg;
	private Double amt1=0.00;
	private Double amt2=0.00;
	private String rmrk;
	private String rmrk1;
	private String rmrk2;
	public Book()
	{}
	
	public String getPlat_date() {
		return plat_date;
	}

	public void setPlat_date(String plat_date) {
		this.plat_date = plat_date;
	}

	public Integer getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(Integer seq_no) {
		this.seq_no = seq_no;
	}

	public String getTx_code() {
		return tx_code;
	}

	public void setTx_code(String tx_code) {
		this.tx_code = tx_code;
	}

	public String getTx_date() {
		return tx_date;
	}
	
	public void setTx_name(String tx_name) {
		this.tx_name = tx_name;
	}

	public String getTx_name() {
		return tx_name;
	}

	public void setTx_date(String tx_date) {
		this.tx_date = tx_date;
	}

	public String getTx_seq() {
		return tx_seq;
	}

	public void setTx_seq(String tx_seq) {
		this.tx_seq = tx_seq;
	}

	public String getTx_type() {
		return tx_type;
	}

	public void setTx_type(String tx_type) {
		this.tx_type = tx_type;
	}

	public String getEntr_no() {
		return entr_no;
	}

	public void setEntr_no(String entr_no) {
		this.entr_no = entr_no;
	}

	public String getChnl_no() {
		return chnl_no;
	}

	public void setChnl_no(String chnl_no) {
		this.chnl_no = chnl_no;
	}
	
	public String getSnd_time() {
		return snd_time;
	}

	public void setSnd_time(String snd_time) {
		this.snd_time = snd_time;
	}

	public String getSnd_brch() {
		return snd_brch;
	}

	public void setSnd_brch(String snd_brch) {
		this.snd_brch = snd_brch;
	}

	public String getSnd_brch_no() {
		return snd_brch_no;
	}

	public void setSnd_brch_no(String snd_brch_no) {
		this.snd_brch_no = snd_brch_no;
	}

	public String getSnd_acct_brch() {
		return snd_acct_brch;
	}

	public void setSnd_acct_brch(String snd_acct_brch) {
		this.snd_acct_brch = snd_acct_brch;
	}

	public String getBrch_no() {
		return brch_no;
	}

	public void setBrch_no(String brch_no) {
		this.brch_no = brch_no;
	}

	public String getBusi_type() {
		return busi_type;
	}

	public void setBusi_type(String busi_type) {
		this.busi_type = busi_type;
	}

	public String getTeller_no() {
		return teller_no;
	}

	public void setTeller_no(String teller_no) {
		this.teller_no = teller_no;
	}

	public String getChk_act_no() {
		return chk_act_no;
	}

	public void setChk_act_no(String chk_act_no) {
		this.chk_act_no = chk_act_no;
	}

	public String getOth_date() {
		return oth_date;
	}

	public void setOth_date(String oth_date) {
		this.oth_date = oth_date;
	}

	public String getOth_seq() {
		return oth_seq;
	}

	public void setOth_seq(String oth_seq) {
		this.oth_seq = oth_seq;
	}

	public Double getTx_amt() {
		return tx_amt;
	}

	public void setTx_amt(Double tx_amt) {
		this.tx_amt = tx_amt;
	}

	public String getAcct_input() {
		return acct_input;
	}

	public void setAcct_input(String acct_input) {
		this.acct_input = acct_input;
	}

	public String getTerm_type() {
		return term_type;
	}

	public void setTerm_type(String term_type) {
		this.term_type = term_type;
	}

	public String getTerm_no() {
		return term_no;
	}

	public void setTerm_no(String term_no) {
		this.term_no = term_no;
	}

	public String getRp_flag() {
		return rp_flag;
	}

	public void setRp_flag(String rp_flag) {
		this.rp_flag = rp_flag;
	}

	public String getClear_date() {
		return clear_date;
	}

	public void setClear_date(String clear_date) {
		this.clear_date = clear_date;
	}

	public String getSign_no() {
		return sign_no;
	}

	public void setSign_no(String sign_no) {
		this.sign_no = sign_no;
	}

	public String getProduct_type() {
		return Product_type;
	}

	public void setProduct_type(String product_type) {
		Product_type = product_type;
	}

	public String getPay_brch() {
		return pay_brch;
	}

	public void setPay_brch(String pay_brch) {
		this.pay_brch = pay_brch;
	}

	public String getPay_acct_no() {
		return pay_acct_no;
	}

	public void setPay_acct_no(String pay_acct_no) {
		this.pay_acct_no = pay_acct_no;
	}

	public String getPay_acct_type() {
		return pay_acct_type;
	}

	public void setPay_acct_type(String pay_acct_type) {
		this.pay_acct_type = pay_acct_type;
	}

	public String getPay_acct_name() {
		return pay_acct_name;
	}

	public void setPay_acct_name(String pay_acct_name) {
		this.pay_acct_name = pay_acct_name;
	}

	public String getPay_cert_type() {
		return pay_cert_type;
	}

	public void setPay_cert_type(String pay_cert_type) {
		this.pay_cert_type = pay_cert_type;
	}

	public String getPay_cert_no() {
		return pay_cert_no;
	}

	public void setPay_cert_no(String pay_cert_no) {
		this.pay_cert_no = pay_cert_no;
	}

	public String getPay_phn() {
		return pay_phn;
	}

	public void setPay_phn(String pay_phn) {
		this.pay_phn = pay_phn;
	}

	public String getPayee_brch() {
		return payee_brch;
	}

	public void setPayee_brch(String payee_brch) {
		this.payee_brch = payee_brch;
	}

	public String getPayee_acct_no() {
		return payee_acct_no;
	}

	public void setPayee_acct_no(String payee_acct_no) {
		this.payee_acct_no = payee_acct_no;
	}

	public String getPayee_acct_type() {
		return payee_acct_type;
	}

	public void setPayee_acct_type(String payee_acct_type) {
		this.payee_acct_type = payee_acct_type;
	}

	
	public String getPayee_acct_name() {
		return payee_acct_name;
	}

	public void setPayee_acct_name(String payee_acct_name) {
		this.payee_acct_name = payee_acct_name;
	}
	
	public String getPayee_cert_type() {
		return payee_cert_type;
	}

	public void setPayee_cert_type(String payee_cert_type) {
		this.payee_cert_type = payee_cert_type;
	}
	
	public String getPayee_cert_no() {
		return payee_cert_no;
	}

	public void setPayee_cert_no(String payee_cert_no) {
		this.payee_cert_no = payee_cert_no;
	}

	public String getPayee_area() {
		return payee_area;
	}

	public void setPayee_area(String payee_area) {
		this.payee_area = payee_area;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getOri_oth_seq() {
		return ori_oth_seq;
	}

	public void setOri_oth_seq(String ori_oth_seq) {
		this.ori_oth_seq = ori_oth_seq;
	}

	public Double getOri_tx_amt() {
		return ori_tx_amt;
	}

	public void setOri_tx_amt(Double ori_tx_amt) {
		this.ori_tx_amt = ori_tx_amt;
	}

	public String getOri_order_no() {
		return ori_order_no;
	}

	public void setOri_order_no(String ori_order_no) {
		this.ori_order_no = ori_order_no;
	}

	public String getOri_tx_date() {
		return ori_tx_date;
	}

	public void setOri_tx_date(String ori_tx_date) {
		this.ori_tx_date = ori_tx_date;
	}

	public String getAcct_lvl() {
		return acct_lvl;
	}

	public void setAcct_lvl(String acct_lvl) {
		this.acct_lvl = acct_lvl;
	}

	public String getChk_stat() {
		return chk_stat;
	}

	public void setChk_stat(String chk_stat) {
		this.chk_stat = chk_stat;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getRet_code() {
		return ret_code;
	}

	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}

	public String getRet_msg() {
		return ret_msg;
	}

	public void setRet_msg(String ret_msg) {
		this.ret_msg = ret_msg;
	}
	
	public String getHost_msg() {
		return host_msg;
	}
	
	public void setHost_msg(String host_msg) {
		this.host_msg = host_msg;
	}

	public String getRet_time() {
		return ret_time;
	}

	public void setTime_sec(Integer time_sec) {
		this.time_sec = time_sec;
	}
	
	public Integer getTime_sec() {
		return time_sec;
	}

	public void setRet_time(String ret_time) {
		this.ret_time = ret_time;
	}
	
	public String getOpen_brch() {
		return open_brch;
	}

	public void setOpen_brch(String open_brch) {
		this.open_brch = open_brch;
	}

	public String getHost_date() {
		return host_date;
	}

	public void setHost_date(String host_date) {
		this.host_date = host_date;
	}

	public String getHost_seq() {
		return host_seq;
	}

	public void setHost_seq(String host_seq) {
		this.host_seq = host_seq;
	}

	public Double getRefund_amt() {
		return refund_amt;
	}

	public void setRefund_amt(Double refund_amt) {
		this.refund_amt = refund_amt;
	}

	public String getChk_flag() {
		return chk_flag;
	}

	public void setChk_flag(String chk_flag) {
		this.chk_flag = chk_flag;
	}

	public String getChk_msg() {
		return chk_msg;
	}

	public void setChk_msg(String chk_msg) {
		this.chk_msg = chk_msg;
	}
	
	public Double getAmt1() {
		return amt1;
	}

	public void setAmt1(Double amt1) {
		this.amt1 = amt1;
	}

	public Double getAmt2() {
		return amt2;
	}

	public void setAmt2(Double amt2) {
		this.amt2 = amt2;
	}

	public String getRmrk() {
		return rmrk;
	}

	public void setRmrk(String rmrk) {
		this.rmrk = rmrk;
	}

	public String getRmrk1() {
		return rmrk1;
	}

	public void setRmrk1(String rmrk1) {
		this.rmrk1 = rmrk1;
	}

	public String getRmrk2() {
		return rmrk2;
	}

	public void setRmrk2(String rmrk2) {
		this.rmrk2 = rmrk2;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
