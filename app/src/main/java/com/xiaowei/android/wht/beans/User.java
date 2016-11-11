package com.xiaowei.android.wht.beans;

import java.io.Serializable;


public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3737898951040974618L;
	
	private String id;
	private String url;
	private String mobile;
	
//	private User() {}  
//    private static User single=null;  
//    //静态工厂方法   
//    public static User getInstance() {  
//         if (single == null) {    
//             single = new User();  
//         }    
//        return single;  
//    } 
//    
//    public static void setInstance(User user) {  
//        if (user != null) {    
//            single = user;  
//        }    
//   }
//    
//    public void clear(){
//    	single = new User();
//    }
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
