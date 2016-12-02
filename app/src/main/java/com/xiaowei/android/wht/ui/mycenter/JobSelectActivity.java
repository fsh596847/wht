package com.xiaowei.android.wht.ui.mycenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xiaowei.android.wht.ApplicationTool;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.model.HttpResult;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.doctorzone.bean.CirCleBean;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.HlpUtils;
import com.xiaowei.android.wht.utis.Utils;
import com.xiaowei.android.wht.views.SildingFinishLayout;
import com.xiaowei.android.wht.views.SildingFinishLayout.OnSildingFinishListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin getIntent().getIntExtra("RESULTCODE", -1); 511;//职称 、512;//职务 setResult(RESULTCODE,
 *         new Intent().putExtra("result", "返回选择"));
 */
public class JobSelectActivity extends Activity {

  private TextView tvTitle;
  private ListView listView;
  private MyListViewAdapter adapter;

  List<String> list = new ArrayList<String>();

  private static final int RESULTCODE_JOB_TITLE = 511;//职称
  private static final int RESULTCODE_POST_TITLE = 512;//职务
  private static final int RESULTCODE_CIRCLE_TITLE = 513;//圈子
  private int RESULTCODE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_job_title);
    ApplicationTool.getInstance().activitis.add(this);//把当前Activity放入集合中
    RESULTCODE = getIntent().getIntExtra("RESULTCODE", -1);

    initViews();

    initListeners();

    switch (RESULTCODE) {
      case RESULTCODE_JOB_TITLE:
        tvTitle.setText("职称");
        queryTitledoc();
        break;

      case RESULTCODE_POST_TITLE:
        tvTitle.setText("职务");
        queryPostdoc();
        break;
      case RESULTCODE_CIRCLE_TITLE:
        tvTitle.setText("圈子");
        queryCircledoc();
        break;
    }

    //右滑退出
    SildingFinishLayout mSildingFinishLayout =
        (SildingFinishLayout) findViewById(R.id.sildingFinishLayout_job_title);
    mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {

      @Override
      public void onSildingFinish() {
        JobSelectActivity.this.finish();
      }
    });
    // touchView
    mSildingFinishLayout.setTouchView(listView);
  }

  private void initListeners() {
    //返回
    findViewById(R.id.iv_job_title_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        finish();
        overridePendingTransition(0, R.anim.out_right);
      }
    });

    //
    listView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        setResult(RESULTCODE, new Intent().putExtra("result", list.get(arg2)));
        finish();
      }
    });
  }

  // Press the back button in mobile phone
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, R.anim.out_right);
  }

  private void initViews() {
    tvTitle = (TextView) findViewById(R.id.tv_job_title);
    listView = (ListView) findViewById(R.id.listview_job_title);
    adapter = new MyListViewAdapter(getApplicationContext(), list);
    listView.setAdapter(adapter);
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

  private void queryPostdoc() {
    reload("正在努力加载……");
    new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          String s = DataService.queryPostdoc(JobSelectActivity.this);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                if (!isDestroy) {

                  com.alibaba.fastjson.JSONArray array = JSON.parseArray(hr.getData().toString());
                  for (int i = 0; i < array.size(); i++) {
                    list.add(array.getString(i));
                  }
                  //mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      adapter.setList(list);
                    }
                  });
                }
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getApplicationContext(), hr.getData().toString(),
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
      }
    }).start();
  }

  private void queryCircledoc() {
    reload("正在努力加载……");
    SpData sp = new SpData(getApplicationContext());
    final String phone = sp.getStringValue(SpData.keyPhoneUser, null);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String s = DataService.queryCircletdoc(JobSelectActivity.this, phone);
          mLog.d("queryCircledoc", "data:" + s);
          Gson gson = new Gson();
          final CirCleBean cirCleBean = gson.fromJson(s, CirCleBean.class);
          //mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              adapter.setList(list);
            }
          });
        } catch (
            Exception he
            )

        {
          he.printStackTrace();
        }

        closeLoadingDialog();
      }
    }

    ).

        start();
  }

  private void queryCompanyledoc() {
    reload("正在努力加载……");
    SpData sp = new SpData(getApplicationContext());
    final String phone = sp.getStringValue(SpData.keyPhoneUser, null);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String s = DataService.queryCompanydoc(JobSelectActivity.this, phone);
          Gson gson = new Gson();
          final CirCleBean cirCleBean = gson.fromJson(s, CirCleBean.class);
          mLog.d("queryCircledoc", "data:" + s);
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (cirCleBean.getStatus() == 1) {
                adapter.setList(list);
              }
            }
          });
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
      }
    }).start();
  }

  private void queryTitledoc() {
    reload("正在努力加载……");
    new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          String s = DataService.queryTitledoc(JobSelectActivity.this);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                if (!isDestroy) {

                  com.alibaba.fastjson.JSONArray array = JSON.parseArray(hr.getData().toString());
                  for (int i = 0; i < array.size(); i++) {
                    list.add(array.getString(i));
                  }
                  //mLog.d("http", "data:"+JSON.parseArray(hr.getData().toString()).toString());
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      adapter.setList(list);
                    }
                  });
                }
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getApplicationContext(), hr.getData().toString(),
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (Exception he) {
          he.printStackTrace();
        }
        closeLoadingDialog();
      }
    }).start();
  }

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(this, text);
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

  private class MyListViewAdapter extends BaseAdapter {
    List<String> list = new ArrayList<String>();

    private LayoutInflater mInflater = null;

    private MyListViewAdapter(Context context, List<String> list) {
      this.mInflater = LayoutInflater.from(context);
      if (list != null) {
        this.list = list;
      }
    }

    private void setList(List<String> list) {
      if (list != null) {
        this.list = list;
        notifyDataSetChanged();
      }
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
    public View getView(int position, View convertView, ViewGroup arg2) {
      ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.item_job_listview, null);
        holder.tvText = (TextView) convertView.findViewById(R.id.tv_item_job_listview);

        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      if (getCount() > 0) {
        String detail = list.get(position);
        holder.tvText.setText(detail);
      }

      return convertView;
    }

    class ViewHolder {
      public TextView tvText;
    }
  }
}
