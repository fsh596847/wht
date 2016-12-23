package com.xiaowei.android.wht.ui.patient;

import com.xiaowei.android.wht.ui.ExpertKnownActivity;
import com.xiaowei.android.wht.ui.HealthActivity;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.HealthLore;
import com.xiaowei.android.wht.beans.MeetingNoticeDoctor;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.service.DataService4Patient;
import com.xiaowei.android.wht.ui.WebHealthActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.ListViewInScrollView;

@SuppressLint("NewApi")
public class FragmentPatientHomepage extends Fragment
    implements OnPageChangeListener, View.OnClickListener {
  DisplayMetrics dm;
  RelativeLayout ad_div;
  TextView my_sick_log, my_doctor, my_2_hospital, my_invite;

  //专家
  private ListViewInScrollView lvExpert;//专家ListView
  private List<HealthLore> listExpert;//专家List
  private MyExpertAdapter expertAdapter;

  private ScrollView scrollView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    dm = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    View view = inflater.inflate(R.layout.patient_fragment_homepage, container, false);
    init(view);
    return view;
  }

  private void init(View view) {
    ad_div = (RelativeLayout) view.findViewById(R.id.ad_div);
    //广告图的大小是16（长）:9（宽），所以做此调整
    LinearLayout.LayoutParams layout =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            9 * dm.widthPixels / 16);
    ad_div.setLayoutParams(layout);
    //宣传栏初始化
    group = (ViewGroup) view.findViewById(R.id.viewGroup_homepage);
    viewPager = (ViewPager) view.findViewById(R.id.viewPager_homepage);

    my_sick_log = (TextView) view.findViewById(R.id.my_sick_log);
    my_sick_log.setOnClickListener(this);

    my_doctor = (TextView) view.findViewById(R.id.my_doctor);
    my_doctor.setOnClickListener(this);

    my_2_hospital = (TextView) view.findViewById(R.id.my_2_hospital);
    my_2_hospital.setOnClickListener(this);

    my_invite = (TextView) view.findViewById(R.id.my_invite);
    my_invite.setOnClickListener(this);
    view.findViewById(R.id.btn_health).setOnClickListener(this);
    getImageInfo();

    //健康知识 初始化s
    initExpert(view);

    //搜索栏 初始化
    initSearch(view);
  }

  private void initSearch(View view) {
    // TODO Auto-generated method stub

  }

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          if (!isDestroy) {
            selectItems += 1;
            viewPager.setCurrentItem(selectItems);
            setImageBackground(selectItems % mImageViews.length);
          }
          break;

        case 1:
          expertAdapter.setList(listExpert);
          Util.setListViewHeightBasedOnChildren(lvExpert);
          scrollView.smoothScrollTo(0, y);
          break;
      }
    }
  };

  private void initExpert(View view) {
    listExpert = new ArrayList<HealthLore>();
    scrollView = (ScrollView) view.findViewById(R.id.scrollView_homepg);
    lvExpert = (ListViewInScrollView) view.findViewById(R.id.listview_patient_homepage);
    expertAdapter = new MyExpertAdapter(getActivity(), lvExpert);
    lvExpert.setAdapter(expertAdapter);
    //专家头像
    expertAdapter.setOnImageLoadListener(new SyncImageLoaderListview.OnImageLoadListener() {

      @Override
      public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead, Integer index) {
        //listExpert.get(t).setDrawable(drawable);
      }

      @Override
      public void onError(Integer t) {
      }
    });
    lvExpert.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        startActivity(new Intent(getActivity(), WebHealthActivity.class)
            .putExtra("url",
                listExpert.get(arg2).getDetailurl() + "?id=" + listExpert.get(arg2).getId()));
        getActivity().overridePendingTransition(R.anim.in_right, 0);
      }
    });

    p = 1;
    page = 10;
    success = true;
    isQuery = true;
    isFinish = false;
    scrollView.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_MOVE:
            int scrollY = v.getScrollY();
            int height = v.getHeight();
            int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
            if (scrollY == 0) {
              //System.out.println("滑动到了顶端 view.getScrollY()="+scrollY);
            }
            y = scrollY + height;
            if (y == scrollViewMeasuredHeight) {
              //System.out.println("滑动到了底部 scrollY="+scrollY);
              if (success && isQuery && !isFinish && curr == 0) {
                curr++;
                p += 1;
                getListExpert(p, page);
              }
            } else {
              curr = 0;
            }

            break;
        }
        return false;
      }
    });

    getListExpert(p, page);
  }

  //分页刷新
  private int p;
  private int page;
  private boolean success;//是否更新成功
  private boolean isQuery;//是否正在更新
  private boolean isFinish;
  int curr = 0;
  int y = 0;

  /**
   * get expert list
   */
  private void getListExpert(final int p, final int pagesize) {
    isQuery = false;
    reload("正在努力加载……");
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.queryHealthLore(getActivity(), p, pagesize);
          mLog.d("http", "s：" + s);
          if (!HlpUtils.isEmpty(s)) {
            HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              success = true;
              if (hr.isSuccess()) {
                if (!isDestroy) {
                  if (hr.getTotalpage() == p) {
                    isFinish = true;
                  }
                  List<HealthLore> list =
                      JSON.parseArray(hr.getData().toString(), HealthLore.class);
                  listExpert.addAll(list);
                  y = scrollView.getScrollY();
                  handler.sendEmptyMessage(1);
                }
              } else {
                //								listExpert.clear();
                //								handler.sendEmptyMessage(1);
              }
            } else {
            }
          } else {
            success = false;
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
        isQuery = true;
      }
    }).start();
  }

  public interface OnClickDetailsListener {
    void onClickDetailsListener(int position);
  }

  private class MyExpertAdapter extends BaseAdapter {

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case 0:
            notifyDataSetChanged();
            break;

          default:
            break;
        }
      }
    };

    List<HealthLore> list = new ArrayList<HealthLore>();
    ListView mListView;
    private Drawable[] drawables;

    private LayoutInflater mInflater = null;
    private Context mContext;

    SyncImageLoaderListview.OnImageLoadListener mImageLoadListener;

    private MyExpertAdapter(Context context, ListView lvExpert) {
      this.mInflater = LayoutInflater.from(context);
      mContext = context;
      mListView = lvExpert;
      mListView.setOnScrollListener(onScrollListener);
    }

    private void setList(List<HealthLore> list) {
      if (list != null) {
        if (list.size() > 0) {
          this.drawables = new Drawable[list.size()];
        }
        this.list = list;
        notifyDataSetChanged();
      }
    }

    private void setOnImageLoadListener(
        SyncImageLoaderListview.OnImageLoadListener mImageLoadListener) {
      this.mImageLoadListener = mImageLoadListener;
    }

    @Override
    public int getCount() {
      return list.size();
    }

    @Override
    public Object getItem(int arg0) {

      return list.get(arg0);
    }

    @Override
    public long getItemId(int position) {

      return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
      ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.item_health, null);
        holder.ivImg = (ImageView) convertView.findViewById(R.id.imageView_item_health);
        holder.tvTitle = (TextView) convertView.findViewById(R.id.textView_item_health);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      if (getCount() > 0) {
        HealthLore expert = list.get(position);
        String title = expert.getTitle();
        if (title != null) {
          holder.tvTitle.setText(title);
        }

        Drawable d = drawables[position];
        if (d != null) {
          holder.ivImg.setImageDrawable(d);
        } else {
          holder.ivImg.setImageResource(R.drawable.loading_img);
          syncImageLoader.loadImage(mContext, position, expert.getImg(), imageLoadListener, null,
              0);
        }
      }

      return convertView;
    }

    SyncImageLoaderListview.OnImageLoadListener imageLoadListener =
        new SyncImageLoaderListview.OnImageLoadListener() {

          @Override
          public void onImageLoad(Integer t, Drawable drawable, CircularImage ivHead,
              Integer index) {
            drawables[t] = drawable;
            handler.sendEmptyMessage(0);
            //mImageLoadListener.onImageLoad(t, drawable, ivHead,0);
          }

          @Override
          public void onError(Integer t) {
          }
        };

    SyncImageLoaderListview syncImageLoader = new SyncImageLoaderListview();

    public void loadImage() {
      int start = mListView.getFirstVisiblePosition();
      int end = mListView.getLastVisiblePosition();
      if (end >= getCount()) {
        end = getCount() - 1;
      }
      syncImageLoader.setLoadLimit(start, end);
      syncImageLoader.unlock();
    }

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
          case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
            syncImageLoader.lock();
            break;
          case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
            loadImage();
            break;
          case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            syncImageLoader.lock();
            break;

          default:
            break;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    };

    class ViewHolder {
      private ImageView ivImg;
      private TextView tvTitle;
    }
  }

  @SuppressLint("NewApi")
  private void initPublicity(int length) {
    if (length <= 0) {
      return;
    }

    //imgIdArray = new Drawable[length];
    for (int i = 0; i < length; i++) {
      imgIdArray[i] = getResources().getDrawable(R.drawable.h_vp1);
    }
    tips = new ImageView[imgIdArray.length];

    //将点点加入到ViewGroup中
    tips = new ImageView[imgIdArray.length];
    for (int i = 0; i < tips.length; i++) {
      ImageView imageView = new ImageView(getActivity());
      imageView.setLayoutParams(new LayoutParams(10, 10));
      tips[i] = imageView;
      if (i == 0) {
        tips[i].setBackgroundResource(R.drawable.round_hb);
      } else {
        tips[i].setBackgroundResource(R.drawable.round_hg);
      }

      LinearLayout.LayoutParams layoutParams =
          new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
              LayoutParams.WRAP_CONTENT));
      layoutParams.leftMargin = 5;
      layoutParams.rightMargin = 5;
      group.addView(imageView, layoutParams);
    }

    //将图片装载到数组中
    mImageViews = new ImageView[imgIdArray.length];
    for (int i = 0; i < mImageViews.length; i++) {
      ImageView imageView = new ImageView(getActivity());
      mImageViews[i] = imageView;
      imageView.setBackground(imgIdArray[i]);
    }

    //设置Adapter
    viewPager.setAdapter(new MyAdapter());
    //设置监听，主要是设置点点的背景
    viewPager.setOnPageChangeListener(this);
    //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
    viewPager.setCurrentItem((mImageViews.length) * 100);
  }

  private ViewPager viewPager;
  private ImageView[] tips;
  private ImageView[] mImageViews;
  private Drawable[] imgIdArray;
  private ViewGroup group;

  public class MyAdapter extends PagerAdapter {

    @Override
    public int getCount() {
      return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
      return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      //Warning：不要在这里调用removeView
      //			((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      //对ViewPager页号求模取出View列表中要显示的项
      position %= mImageViews.length;
      if (position < 0) {
        position = mImageViews.length + position;
      }
      ImageView view = mImageViews[position];
      //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
      ViewParent vp = view.getParent();
      if (vp != null) {
        ViewGroup parent = (ViewGroup) vp;
        parent.removeView(view);
      }
      container.addView(view);
      //add listeners here if necessary
      return view;

      //			((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);
      //			return mImageViews[position % mImageViews.length];
    }
  }

  @Override
  public void onPageScrollStateChanged(int arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPageSelected(int arg0) {
    setImageBackground(arg0 % mImageViews.length);
    selectItems = arg0;
    Log.d("wht", "arg0:" + arg0);
  }

  private int selectItems = 0;

  private void setImageBackground(int selectItems) {
    Log.d("wht", "selectItems:" + selectItems);
    //this.selectItems = selectItems;
    for (int i = 0; i < tips.length; i++) {
      if (i == selectItems) {
        tips[i].setBackgroundResource(R.drawable.round_hb);
      } else {
        tips[i].setBackgroundResource(R.drawable.round_hg);
      }
    }
  }

  boolean isDestroy = false;

  private void stopPlay() {
    new Thread(new Runnable() {

      @Override
      public void run() {

        while (!isDestroy) {
          try {
            Thread.sleep(2000);
            handler.sendEmptyMessage(0);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  @Override
  public void onStart() {
    super.onStart();
    isDestroy = false;
    stopPlay();
  }

  @Override
  public void onStop() {
    super.onStop();
    isDestroy = true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  //	private void queryDoctorNoAudit(final String mobile, final String id){
  //		if(isQueryDoctorNoAuditING){
  //			return;
  //		}
  //		isQueryDoctorNoAuditING = true;
  //		reload("正在努力加载……");
  //		new Thread(new Runnable() {
  //
  //			@Override
  //			public void run() {
  //
  //				try {
  //					String s = DataService.queryDoctorNoAudit(getActivity(), mobile, id);
  //					//mLog.d("http", "s:"+s);
  //					if (!HlpUtils.isEmpty(s)){
  //						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
  //						if (hr != null){
  //							if  (hr.isSuccess()){
  //								RegisterInfo.setInstance(JSON.parseObject(hr.getData().toString(), RegisterInfo.class));
  //								new SpData(getActivity())
  //								.setIntValue(SpData.KeyApprovestate, RegisterInfo.getInstance().getApprovestate());
  //								if(RegisterInfo.getInstance().getApprovestate() == 0){
  //									startActivity(new Intent(getActivity(),PatientAddAtivity.class));
  //								}
  //								else{
  //									startActivity(new Intent(getActivity(), RegisterInfoActivity.class));
  //								}
  //							}else{
  //							}
  //						}else{
  //							getActivity().runOnUiThread(new Runnable(){
  //								@Override
  //								public void run() {
  //									Toast.makeText(getActivity(), "请求失败，请重试！", Toast.LENGTH_SHORT).show();
  //								}
  //							});
  //						}
  //					}else{
  //						getActivity().runOnUiThread(new Runnable(){
  //							@Override
  //							public void run() {
  //								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
  //							}
  //						});
  //					}
  //				}catch (Exception he) {
  //					he.printStackTrace();
  //				}
  //				closeLoadingDialog();
  //				isQueryDoctorNoAuditING = false;
  //			}
  //		}).start();
  //	}
  //	boolean isQueryDoctorNoAuditING = false;

  /**
   * 获取后台图片信息
   */
  private void getImageInfo() {
    reload("正在努力加载……");
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService4Patient.getHomeImgUrls(getActivity());
          if (!HlpUtils.isEmpty(s)) {
            HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                final List<MeetingNoticeDoctor> listHomeImg =
                    JSON.parseArray(hr.getData().toString(), MeetingNoticeDoctor.class);
                if (!isDestroy && listHomeImg != null) {
                  //先显示默认图片
                  imgIdArray = new Drawable[listHomeImg.size()];
                  getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      initPublicity(listHomeImg.size());
                    }
                  });
                  //获取后台图片
                  for (int i = 0; i < listHomeImg.size(); i++) {
                    getImage(listHomeImg.get(i).getImgUrl(), i);
                  }
                }
              } else {
              }
            } else {
            }
          } else {
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
      }
    }).start();
  }

  /**
   * 获取后台图片
   */
  private void getImage(final String path, final int mImageViewsIndex) {

    new Thread(new Runnable() {

      @Override
      public void run() {

        byte[] data;
        try {

          data = DataService.getImage(path);
          if (data != null && !isDestroy) {
            final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
            imgIdArray[mImageViewsIndex] = new BitmapDrawable(getResources(), mBitmap);
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                mImageViews[mImageViewsIndex].setBackground(imgIdArray[mImageViewsIndex]);
              }
            });
          } else if (!isDestroy) {
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                //mLog.d("http", msg)
                //mToast.showToast(getActivity(), "Image error!");
              }
            });
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(getActivity(), text);
    }
    if (!loadingDialog.isShowing()) {
      loadingDialog.show();
    }
  }

  private Dialog loadingDialog = null;

  private void closeLoadingDialog() {
    if (null != loadingDialog) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.my_sick_log:
        toSickLog();
        break;
      case R.id.my_doctor:
        toMyDoctor();
        break;
      case R.id.my_2_hospital:
        toReferral();
        break;
      case R.id.my_invite:
        toMyInvite();
        break;
      case R.id.btn_health:
        Intent intent = new Intent(getActivity(), HealthActivity.class);
        startActivity(intent);
        break;
      default:
        break;
    }
  }

  private void toMyInvite() {
    Intent it = new Intent(getActivity(), PatientMyInviteActivity.class);
    startActivity(it);
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  private void toReferral() {
    Intent it = new Intent(getActivity(), PatientMyReferralActivity.class);
    startActivity(it);
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  private void toMyDoctor() {
    //ExpertKnownActivity PatientMyDoctorActivity
    Intent it = new Intent(getActivity(), ExpertKnownActivity.class);
    startActivity(it);
    getActivity().overridePendingTransition(R.anim.in_right, 0);
  }

  /**
   * 牛皮癣
   */
  private void toSickLog() {
    //Intent it = new Intent(getActivity(),PatientSickRecordActivity.class);
    //startActivity(it);
    //getActivity().overridePendingTransition(R.anim.in_right,0);
    Intent intent = new Intent(getActivity(), PsoraActivity.class);
    startActivity(intent);
  }

  public void healthClick(View view) {
    Intent intent = new Intent(getActivity(), HealthActivity.class);
    startActivity(intent);
  }
}
