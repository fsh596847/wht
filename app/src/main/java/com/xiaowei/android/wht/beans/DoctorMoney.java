package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class DoctorMoney implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5250529404591976732L;
	
	private String accountmny;//余额
	private String takemny;//已提现
	private String tadymny;//今日收入
	private String isbandcard;
	private String withdrawmny;
	
	public String getIsbandcard() {
		return isbandcard;
	}
	public void setIsbandcard(String isbandcard) {
		this.isbandcard = isbandcard;
	}
	public String getWithdrawmny() {
		return withdrawmny;
	}
	public void setWithdrawmny(String withdrawmny) {
		this.withdrawmny = withdrawmny;
	}
	public String getAccountmny() {
		return accountmny;
	}
	public void setAccountmny(String accountmny) {
		this.accountmny = accountmny;
	}
	public String getTakemny() {
		return takemny;
	}
	public void setTakemny(String takemny) {
		this.takemny = takemny;
	}
	public String getTadymny() {
		return tadymny;
	}
	public void setTadymny(String tadymny) {
		this.tadymny = tadymny;
	}
	

}
