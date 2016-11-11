package com.xiaowei.android.wht.model;

import java.io.Serializable;

public class HttpResult implements Serializable {
	private static final long serialVersionUID = -1948042386850953730L;
	public static final int ERROR = -9999;
	public static final int Fail = 0;
	public static final int SUCCESS = 1;
	public static final Integer statusOK = 1;
	public static final Integer statusFail = 0;
	public static final Integer statusNotRegister = 2;
	public static final Integer statusException = -9999;
	Integer status;// 1-服务器返回处理成功，0-服务器返回失败结果，2-用户不存在，-9999 表示App处理出错
	String msg;// 提示信息
	Object data;// 交互数据
	Integer totalpage = 0;//数据的总页数
	double totalmny ;
	String filename;
	int totalRows;//未读信息数
	
	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isSuccess(){
		return status != null && 1==status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(Integer totalpage) {
		this.totalpage = totalpage;
	}

	public double getTotalmny() {
		return totalmny;
	}

	public void setTotalmny(double totalmny) {
		this.totalmny = totalmny;
	}

}
