package com.xiaowei.android.wht.ui.mycenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.SpData;
import com.xiaowei.android.wht.service.DataService;
import com.xiaowei.android.wht.ui.doctorzone.BaseActivity;
import com.xiaowei.android.wht.ui.doctorzone.bean.CirCleBean;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.utis.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircleOrgnizeFragment extends BaseActivity {
  private ListView listView;
  private MyListViewAdapter adapter;

  List<CirCleBean.CirCleItemBean> list = new ArrayList<>();

  @Override protected void setContentView() {
    setContentView(R.layout.fragment_circle);
  }

  @Override public void init(Bundle savedInstanceState) {
    String groupIds = getIntent().getStringExtra(MyInfoActivity.INTENT_ORGNIZE_KEY);
    Set<String> sH = new HashSet<>();
    if (!TextUtils.isEmpty(groupIds)) {
      String[] groupId = groupIds.split(",");
      for (String s : groupId) {
        sH.add(s);
      }
    }
    listView = (ListView) findViewById(R.id.listview);
    adapter = new MyListViewAdapter(CircleOrgnizeFragment.this, list, sH);
    listView.setAdapter(adapter);
  }

  @Override public void setListener() {
    queryCompanyledoc();
  }

  public static final String KEY_INTENT_LIST_ORGINZE = "orginze_key_list";

  public void submitClick(View view) {
    Intent intent = new Intent();
    intent.putExtra(KEY_INTENT_LIST_ORGINZE, (Serializable) list);
    setResult(RESULT_OK, intent);
    finish();
  }

  private void queryCompanyledoc() {
    reload("正在努力加载……");
    SpData sp = new SpData(CircleOrgnizeFragment.this);
    final String phone = sp.getStringValue(SpData.keyPhoneUser, null);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String s = DataService.queryCompanydoc(CircleOrgnizeFragment.this, phone);
          Gson gson = new Gson();
          final CirCleBean cirCleBean = gson.fromJson(s, CirCleBean.class);
          mLog.d("queryCircledoc", "data:" + s);
          CircleOrgnizeFragment.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              closeLoadingDialog();
              if (cirCleBean.getStatus() == 1) {
                list = cirCleBean.getData();
                adapter.setList(cirCleBean.getData());
              }
            }
          });
        } catch (Exception he) {
          closeLoadingDialog();
          he.printStackTrace();
        }
      }
    }).start();
  }

  private void reload(String text) {
    if (loadingDialog == null) {
      loadingDialog = Utils.createLoadingDialog(CircleOrgnizeFragment.this, text);
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
    List<CirCleBean.CirCleItemBean> list = new ArrayList<>();
    private Set<String> sH;
    private LayoutInflater mInflater = null;

    private MyListViewAdapter(Context context, List<CirCleBean.CirCleItemBean> list,
        Set<String> sH) {
      this.mInflater = LayoutInflater.from(context);
      this.sH = sH;
      if (list != null) {
        this.list = list;
      }
    }

    private void setList(List<CirCleBean.CirCleItemBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup arg2) {
      ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.adapter_circle, null);
        holder.tvText = (TextView) convertView.findViewById(R.id.tv_item_job_listview);
        holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      if (getCount() > 0) {
        final CirCleBean.CirCleItemBean detail = list.get(position);
        if (sH.contains(detail.getId())) {
          holder.checkBox.setChecked(true);
          detail.setCheck(true);
        } else {
          if (detail.isCheck()) {
            holder.checkBox.setChecked(true);
          } else {
            holder.checkBox.setChecked(false);
          }
        }
        //holder.checkBox.setChecked(detail.isCheck());
        holder.tvText.setText(detail.getGroupname());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            list.get(position).setCheck(!detail.isCheck());
            sH.remove(list.get(position).getId());
            notifyDataSetChanged();
          }
        });
      }

      return convertView;
    }

    class ViewHolder {
      public TextView tvText;
      public CheckBox checkBox;
    }
  }
}
