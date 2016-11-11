package com.xiaowei.android.wht.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.beans.Area;
import com.xiaowei.android.wht.beans.RegisterInfo;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.utils.mToast;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CustomViewPager;

public class RegisterInfoActivity extends FragmentActivity {
	
	
	private TextView tvTitle;//标题
	private CustomViewPager viewPager;
	private Button btnNext;
	
	private RegisterInfoAFragment rInfoAF;
	private RegisterInfoBFragment rInfoBF;
	private RegisterInfoCFragment rInfoCF;
	private ArrayList<Fragment> fList;
	
	private int viewPagerCurrentItem;
	private static final int RESULTCODE_PHOTO_SET = 510;
	private static final int RESULTCODE_JOB_TITLE = 511;//职称 
	private static final int RESULTCODE_POST_TITLE = 512;//职务
	
	private Area area;
	private String name;//姓名
	private int sex = 1;//0；男；1女
	private String headPhoto;//头像
	private String hospital;//所属医院
	private String administrativeOffice;//所属科室
	private String job;//职称
	private String post;//职务
	//private String workPermit;//工作证
	private String executePermit;//执行证
	private String certification;//资格证
	private String goodAt;//擅长领域
	private String achievement;//学术成果
	private String acceptsThat;//社会服务
	private String attestationPhoto;//认证图片
	
	private ToggleButton tBtnAgreement;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_info);
		ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
		initViews();
		
		initListeners();
		
		initFragments();
	}

	private void initFragments() {
		viewPagerCurrentItem = 0;
		rInfoAF = new RegisterInfoAFragment();
		rInfoBF = new RegisterInfoBFragment();
		rInfoCF = new RegisterInfoCFragment();
		fList = new ArrayList<Fragment>();
		fList.add(rInfoAF);
		fList.add(rInfoBF);
		fList.add(rInfoCF);
		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fList));
		viewPager.setCurrentItem(viewPagerCurrentItem);
	}

	private void initListeners() {
		findViewById(R.id.textView_register_agreement).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(RegisterInfoActivity.this, WebAgreementActivity.class).putExtra("type", 1));
				overridePendingTransition(R.anim.in_right,0);
			}
		});
		
		//返回
		findViewById(R.id.iv_register_info_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(viewPagerCurrentItem == 1){
					viewPagerCurrentItem = 0;
					viewPager.setCurrentItem(viewPagerCurrentItem);
					tvTitle.setText("填写资料");
					btnNext.setText("下一步");
				}
				finish();
			}
		});
		
		//下一步
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				switch (viewPagerCurrentItem) {
				case 0:
					if(!getInfoA()){
						break;
					}
					viewPagerCurrentItem = 1;
					viewPager.setCurrentItem(viewPagerCurrentItem);
					tvTitle.setText("认证信息");
					btnNext.setText("提交资料");
					break;

				case 1:
					if(!getInfoB()){
						Toast.makeText(getApplicationContext(), "请完整信息", Toast.LENGTH_SHORT).show();
						break;
					}
					
					if(tBtnAgreement.isChecked()){
						uploadInfo();
					}else{
						mToast.showToast(RegisterInfoActivity.this, "未同意《转诊协议》");
					}
					
						//DataService.uploadFile(new File(path), User.getInstance().);
					
					//viewPagerCurrentItem = 2;
					//viewPager.setCurrentItem(viewPagerCurrentItem);
					//tvTitle.setText("收款方式");
					break;
					
				case 2:
					
					//startActivity(new Intent(RegisterInfoActivity.this, ApproveActivity.class).putExtra("type", 0));
					break;
				}

			}
		});
		
	}

	protected boolean getInfoA() {
		area = rInfoAF.getArea();
		name = rInfoAF.getName();
		sex = rInfoAF.getSex();
		headPhoto = rInfoAF.getHeadPhotoPath();
		hospital = rInfoAF.getHospital();
		administrativeOffice = rInfoAF.getAdministrativeOffice();
		job = rInfoAF.getJob();
		post = rInfoAF.getPost();
		//workPermit = rInfoAF.getWorkPermit();
		executePermit = rInfoAF.getExecutePermit();
		certification = rInfoAF.getCertification();
		boolean re = true;
		try {
			if(area == null || name.isEmpty() /*|| headPhoto.isEmpty()*/ || hospital.isEmpty() || administrativeOffice.isEmpty()
					|| job.isEmpty() || post.isEmpty() /*|| workPermit.isEmpty() || executePermit.isEmpty() || certification.isEmpty()*/){
				re = false;
			}
		} catch (NullPointerException e) {
			re = false;
		}
		if(!re){
			
			Toast.makeText(getApplicationContext(), "请完整信息", Toast.LENGTH_SHORT).show();
		}
		else if(executePermit.length()>0 && executePermit.length() != 15){
			Toast.makeText(getApplicationContext(), "请输入正确的15位执行医师证书编码", Toast.LENGTH_SHORT).show();
			re = false;
		}
		
		else if(certification.length()>0 && certification.length() < 23){
			Toast.makeText(getApplicationContext(), "请输入正确的医师资格证书编码", Toast.LENGTH_SHORT).show();
			re = false;
		}
		
		return re;
	}
	
	protected boolean getInfoB() {
		goodAt = rInfoBF.getGoodAt();
		achievement = rInfoBF.getAchievement();
		acceptsThat = rInfoBF.getAcceptsThat();
		attestationPhoto = rInfoBF.getPhotoPath();
		/*boolean re = true;
		try {
			if(goodAt.isEmpty() || achievement.isEmpty() || acceptsThat.isEmpty() || attestationPhoto.isEmpty()){
				re = false;
			}
		} catch (NullPointerException e) {
			re = false;
		}
		return re;*/
		return true;
	}

	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_register_info_title);
		viewPager = (CustomViewPager) findViewById(R.id.viewpager_register_info);
		btnNext = (Button) findViewById(R.id.btn_register_info);
		tBtnAgreement = (ToggleButton) findViewById(R.id.toggleButton_register);
	}
	
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
		
		private ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> fList) {
			super(fm);
			list = fList;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}
		
	}
	
	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		ApplicationTool.getInstance().activitis.remove(this);//把当前Activity移出集合中
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_PHOTO_SET:
			switch (viewPagerCurrentItem) {
			case 1:
				uploadIndex = 1;
				//fList.get(viewPagerCurrentItem).onActivityResult(requestCode, resultCode, data);
				break;
			}
			
			break;
			
		case RESULTCODE_JOB_TITLE:
			//rInfoAF.onActivityResult(requestCode, resultCode, data);
			break;
			
		case RESULTCODE_POST_TITLE:
			//rInfoAF.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}
	
	/**
	 * 上传资料
	 */
	private void uploadInfo(){
		if(isUploadING){
			return;
		}
		isUploadING = true;
		reload("正在上传资料……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				for (int i = uploadIndex; i <= 3; i++) {
					switch (i) {
					case 1:
						if(attestationPhoto != null){
							//认证图片上传失败
							if(!uploadAttestationImg(Utils.getImageToBase64Str(attestationPhoto))){
								i = 100;//跳出for循环
							}
						}
						else{
							uploadIndex = 2; 
						}
						break;

					case 2:
						if(headPhoto != null){
							//头像图片上传失败
							if(!uploadHeadImg(Utils.getImageToBase64Str(headPhoto))){
								
								i = 100;//跳出for循环
							}
						}
						else{
							uploadIndex = 3; 
						}
						break;
						
					case 3:
						//上传完成
						if(uploadHeadInfo()){
							startActivity(new Intent(RegisterInfoActivity.this, ApproveActivity.class).putExtra("type", 1));
							finish();
						}
						break;
					}
					
				}
				
				closeLoadingDialog();
				isUploadING = false;
			}
		}).start();
	}
	private int uploadIndex = 1;//当前上传步骤  1，上传认证图片；2，上传头像；3，上传资料
	boolean isUploadING = false;
	
	
	private boolean uploadHeadInfo(){
		boolean re = false;
		try {
			String s = DataService.uploadRegisterInfo(getApplicationContext(), 
					new SpData(getApplicationContext()).getStringValue(SpData.keyPhoneUser, null)
					, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), null, name
					, headPhoto==null ? RegisterInfo.getInstance().getHeadimgname()==null 
						? null : RegisterInfo.getInstance().getHeadimgname() :  headPhoto
					, hospital, area.getAreaname(), area.getId(), administrativeOffice
					, String.valueOf(sex), null, null, executePermit, certification, goodAt, achievement
					, acceptsThat, attestationPhoto,null,null,null,null,null, job, post);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(!isDestroy){
							re = true;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
						}  
					});
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		return re;
	}
	
	/**
	 * 上传头像
	 * @param iosimg
	 */
	private boolean uploadHeadImg(final String iosimg){
		boolean re = false;
		if(iosimg == null){
			return re;
		}
		try {
			String s = DataService.uploadUserImg(RegisterInfoActivity.this, 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "userlogoimg", iosimg);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(!isDestroy && hr.getFilename() != null){
							headPhoto = hr.getFilename();
							re = true;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
						}  
					});
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		return re;
	}
	
	/**
	 * 上传认证图片
	 * @param iosimg
	 */
	private boolean uploadAttestationImg(final String iosimg){
		boolean re = false;
		if(iosimg == null){
			return re;
		}
		try {
			String s = DataService.uploadUserImg(RegisterInfoActivity.this, 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), "qualifiedimg", iosimg);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(!isDestroy && hr.getFilename() != null){
							attestationPhoto = hr.getFilename();
							re = true;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), "上传失败，请重试！", Toast.LENGTH_SHORT).show();
						}  
					});
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		}catch (Exception he) {
			he.printStackTrace();
		}
		return re;
	}
	
	private void reload(String text){
		if (loadingDialog == null){
			loadingDialog = Utils.createLoadingDialog(this, text);
		}
		if (!loadingDialog.isShowing()){
			loadingDialog.show();
		}
	}
	
	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

}
