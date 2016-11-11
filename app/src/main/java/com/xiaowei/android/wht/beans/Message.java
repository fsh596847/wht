package com.xiaowei.android.wht.beans;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6301540838958349754L;
	
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
	Integer totalpage;//数据的总页数
	double totalmny ;
	String filename;
	private String totalRows;//查询出的总条数(未读)
	private String p;        //当前页
	//private String totalPage;//总页数
	private String ntitle;
	private int state;//0：未读 1：已读
	private String createtime;
	private String ncontent;
	private String mobile;
	private String msgtype;
	private String dataid; //查询数据所需要的ID（跟上面的消息类型一起传回后台去查询）
	private String optye;
	private String id;

	
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
	
	private String pagesize;//每页显示条数
	public String getPagesize() {
		return pagesize;
	}
	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}
	public String getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(String totalRows) {
		this.totalRows = totalRows;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getNtitle() {
		return ntitle;
	}
	public void setNtitle(String ntitle) {
		this.ntitle = ntitle;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCreatetime() {
		return createtime;
	}
	public String getCreatetimeMMddHH() {
		if(createtime.contains("-") && createtime.contains(":")){
			return createtime.substring(createtime.indexOf("-")+1,createtime.indexOf(":")+3);
		}
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getNcontent() {
		return ncontent;
	}
	public void setNcontent(String ncontent) {
		this.ncontent = ncontent;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getDataid() {
		return dataid;
	}
	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	public String getOptye() {
		return optye;
	}
	public void setOptye(String optye) {
		this.optye = optye;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
