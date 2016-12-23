package com.xiaowei.android.wht.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.beans.HealthLore;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;
import com.xiaowei.android.wht.utils.Util;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.SyncImageLoaderListview;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.CircularImage;
import com.xiaowei.android.wht.views.ListViewInScrollView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HIPAA on 2016/12/23. 健康知识
 */

public class HealthActivity extends BaseActivity {

  private ListViewInScrollView lvExpert;
  private List<HealthLore> listExpert;//专家List
  private MyExpertAdapter expertAdapter;
  private ScrollView scrollView;

  @Override protected void setContentView() {
    setContentView(R.layout.activity_health);
  }

  @Override public void init(Bundle savedInstanceState) {
    listExpert = new ArrayList<HealthLore>();
    scrollView = (ScrollView) findViewById(R.id.scrollView_homepg);
    lvExpert = (ListViewInScrollView) findViewById(R.id.listview_patient_homepage);
    expertAdapter = new MyExpertAdapter(activity, lvExpert);
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
  }

  @Override public void setListener() {
    p = 1;
    page = 10;
    success = true;
    isQuery = true;
    isFinish = false;
    scrollView.setOnTouchListener(new View.OnTouchListener() {

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

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {

        case 1:
          expertAdapter.setList(listExpert);
          Util.setListViewHeightBasedOnChildren(lvExpert);
          scrollView.smoothScrollTo(0, y);
          break;
      }
    }
  };

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(activity, text);
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

  /**
   * get expert list
   *
   * @param detpname
   * @param areaid
   */
  //分页刷新
  private int p;
  private int page;
  private boolean success;//是否更新成功
  private boolean isQuery;//是否正在更新
  private boolean isFinish;
  int curr = 0;
  int y = 0;

  private void getListExpert(final int p, final int pagesize) {
    reload("正在努力加载……");
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.queryHealthLore(activity, p, pagesize);
          mLog.d("http", "s：" + s);
          if (!HlpUtils.isEmpty(s)) {
            HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              success = true;
              if (hr.isSuccess()) {
                if (hr.getTotalpage() == p) {
                  isFinish = true;
                }
                List<HealthLore> list = JSON.parseArray(hr.getData().toString(), HealthLore.class);
                listExpert.addAll(list);
                y = scrollView.getScrollY();
                handler.sendEmptyMessage(1);
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
      MyExpertAdapter.ViewHolder holder;
      if (convertView == null) {
        holder = new MyExpertAdapter.ViewHolder();
        convertView = mInflater.inflate(R.layout.item_health, null);
        holder.ivImg = (ImageView) convertView.findViewById(R.id.imageView_item_health);
        holder.tvTitle = (TextView) convertView.findViewById(R.id.textView_item_health);
        convertView.setTag(holder);
      } else {
        holder = (MyExpertAdapter.ViewHolder) convertView.getTag();
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
}
