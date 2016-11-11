package com.xiaowei.android.wht.model;

import java.io.Serializable;

import com.xiaowei.android.wht.utis.DoubleUtil;

/**
 * 订单信息
 * @author wudw
 *
 */
public class OrderInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5182515203485432653L;

	int id;// ": 1, <!-- 订单主键 -->
	String orderno;// ": "OD201507230001", <!-- 订单编号 -->
	int ownerid;// ": 13, <!-- 货主主键 -->
	int areaid;//地区主键
	String ownername;// ": 13, <!-- 货主姓名 -->
	String ownerphone;// ": 13, <!-- 货主电话 -->
	String ownerlogo;//": null,
	String ownersex;// ": 1, <!-- 货主性别-->
	int driverid;// ": 5, <!-- 车主主键 -->
	int flownum;// ": 2, <!-- 随行人数 -->
	int orderstatus;// ": 0, <!-- 订单状态 0:未接单 1：已接单 2：已送达 3：已取消-->
	public static final int OrderStatusRecving = 0;
	public static final int OrderStatusRecved = 1;
	public static final int OrderStatusDelivered = 2;
	public static final int OrderStatusCanceled = 3;
	public static final int PayStatusNotPaid = 0;
	public static final int PayStatusHasPaid = 1;
	int paystatus;// ": 0, <!-- 支付状态 0:未支付 1：已支付-->
	int invoicestatus;// ": 0, <!-- 是否已开发票 0:未开票 1：已开票-->
	int judgestatus;// ": 0, <!-- 评价状态 0:未评价 1：已评价-->
	String dbilldate;// ": "2015-07-23 20:47:21", <!-- 乘车时间 -->
	double startmny;//	起步价
	double perprice;//	每公里单价
	double mileage;//	里程
	double transmny;// ": 320, <!-- 用费 -->
	double startlat;// ": 22.622825, <!-- 起点纬度 -->
	double startlng;// ": 114.031871, <!-- 起点经度 -->
	String startlocation;// ": "东吴商厦", <!-- 终点 -->
	double endlat;// ": 22.630573, <!-- 终点纬度 -->
	double endlng;// ": 113.820149 <!-- 终点经度 -->
	String endlocation;// ": "宝安机场东", <!-- 终点 -->
	String memo;// ": null, <!-- 订单备注 -->
	String cancelman;// ": null, 					<!-- 取消方  "0":货主 "1"：车主 -->
	String cancelres;// ": null, <!-- 取消原因 -->
	int ownerjude;//": 0, 						
    int driverjude;//": 0, 						
    double casemoney;//":3<!-- 小费 -->
    Integer discountid;
    Double discountmny;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getOwnerphone() {
		return ownerphone;
	}
	public void setOwnerphone(String ownerphone) {
		this.ownerphone = ownerphone;
	}
	public String getOwnername() {
		return ownername;
	}
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	public String getOwnersex() {
		return ownersex;
	}
	public void setOwnersex(String ownersex) {
		this.ownersex = ownersex;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public int getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(int ownerid) {
		this.ownerid = ownerid;
	}
	public int getDriverid() {
		return driverid;
	}
	public void setDriverid(int driverid) {
		this.driverid = driverid;
	}
	public int getFlownum() {
		return flownum;
	}
	public void setFlownum(int flownum) {
		this.flownum = flownum;
	}
	public int getOrderstatus() {
		return orderstatus;
	}
	public void setOrderstatus(int orderstatus) {
		this.orderstatus = orderstatus;
	}
	public int getPaystatus() {
		return paystatus;
	}
	public void setPaystatus(int paystatus) {
		this.paystatus = paystatus;
	}
	public int getInvoicestatus() {
		return invoicestatus;
	}
	public void setInvoicestatus(int invoicestatus) {
		this.invoicestatus = invoicestatus;
	}
	public int getJudgestatus() {
		return judgestatus;
	}
	public void setJudgestatus(int judgestatus) {
		this.judgestatus = judgestatus;
	}
	public String getDbilldate() {
		return dbilldate;
	}
	public void setDbilldate(String dbilldate) {
		this.dbilldate = dbilldate;
	}
	public double getStartmny() {
		return startmny;
	}
	public void setStartmny(double startmny) {
		this.startmny = startmny;
	}
	public double getPerprice() {
		return perprice;
	}
	public void setPerprice(double perprice) {
		this.perprice = perprice;
	}
	public double getMileage() {
		return mileage;
	}
	public void setMileage(double mileage) {
		this.mileage = mileage;
	}
	public double getTransmny() {
		return transmny;
	}
	public void setTransmny(double transmny) {
		this.transmny = transmny;
	}
	public double getStartlat() {
		return startlat;
	}
	public void setStartlat(double startlat) {
		this.startlat = startlat;
	}
	public double getStartlng() {
		return startlng;
	}
	public void setStartlng(double startlng) {
		this.startlng = startlng;
	}
	public String getStartlocation() {
		return startlocation;
	}
	public void setStartlocation(String startlocation) {
		this.startlocation = startlocation;
	}
	public double getEndlat() {
		return endlat;
	}
	public void setEndlat(double endlat) {
		this.endlat = endlat;
	}
	public double getEndlng() {
		return endlng;
	}
	public void setEndlng(double endlng) {
		this.endlng = endlng;
	}
	public String getEndlocation() {
		return endlocation;
	}
	public void setEndlocation(String endlocation) {
		this.endlocation = endlocation;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCancelman() {
		return cancelman;
	}
	public void setCancelman(String cancelman) {
		this.cancelman = cancelman;
	}
	public String getCancelres() {
		return cancelres;
	}
	public void setCancelres(String cancelres) {
		this.cancelres = cancelres;
	}
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public int getOwnerjude() {
		return ownerjude;
	}
	public void setOwnerjude(int ownerjude) {
		this.ownerjude = ownerjude;
	}
	public int getDriverjude() {
		return driverjude;
	}
	public void setDriverjude(int driverjude) {
		this.driverjude = driverjude;
	}
	public double getCasemoney() {
		return casemoney;
	}
	public void setCasemoney(double casemoney) {
		this.casemoney = casemoney;
	}
	public String getOwnerlogo() {
		return ownerlogo;
	}
	public void setOwnerlogo(String ownerlogo) {
		this.ownerlogo = ownerlogo;
	}
	

	public String getPayStatusStr(){
		if (paystatus == 1){
			return "已支付";
		}
		if (orderstatus == OrderStatusRecving){
			return "未接单";
		}
		if (orderstatus == OrderStatusRecved){
			return "已接单";
		}
		if (orderstatus == OrderStatusDelivered){
			return "待支付";//"已送达";
		}
		if (orderstatus == OrderStatusCanceled){
			return "已取消";
		}
		return "";
	}
	public Integer getDiscountid() {
		return discountid;
	}
	public void setDiscountid(Integer discountid) {
		this.discountid = discountid;
	}
	public Double getDiscountmny() {
		return discountmny;
	}
	public void setDiscountmny(Double discountmny) {
		this.discountmny = discountmny;
	}
	public Double getFactMny(){
		Double factMny  = transmny;
		if (discountmny !=null){
			factMny = DoubleUtil.sub(transmny, discountmny);
			if (factMny<0){
				factMny =0D;
			}
		}
		return factMny;
	}
}
