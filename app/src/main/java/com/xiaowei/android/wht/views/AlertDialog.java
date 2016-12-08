package com.xiaowei.android.wht.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utis.ImageUtils;

public class AlertDialog implements MultiActionClickableSpan.OnTextClickedListener {
  private Context context;
  private Dialog dialog;
  private LinearLayout lLayout_bg;
  private TextView txt_title;
  private TextView txt_msg;
  private MultiActionTextView multiActionTextView;
  private ImageView iv_barcode;
  private Button btn_neg;
  private Button btn_pos;
  private ImageView img_line;
  private EditText price;
  private final Display display;
  private boolean showTitle = false;
  private boolean showMsg = false;
  private boolean showBarcode = false;
  private boolean showPosBtn = false;
  private boolean showNegBtn = false;

  public AlertDialog(Context context) {
    this.context = context;
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    display = windowManager.getDefaultDisplay();
  }

  public AlertDialog builder() {
    // 获取Dialog布局
    View view = LayoutInflater.from(context).inflate(R.layout.view_alertdialog, null);

    // 获取自定义Dialog布局中的控件
    lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
    txt_title = (TextView) view.findViewById(R.id.txt_title);
    multiActionTextView = (MultiActionTextView) view.findViewById(R.id.multitv);
    price = (EditText) view.findViewById(R.id.price);
    txt_title.setVisibility(View.GONE);
    txt_msg = (TextView) view.findViewById(R.id.txt_msg);
    txt_msg.setVisibility(View.GONE);
    iv_barcode = (ImageView) view.findViewById(R.id.barcode_iv);
    iv_barcode.setVisibility(View.GONE);
    btn_neg = (Button) view.findViewById(R.id.btn_neg);
    btn_neg.setVisibility(View.GONE);
    btn_pos = (Button) view.findViewById(R.id.btn_pos);
    btn_pos.setVisibility(View.GONE);
    img_line = (ImageView) view.findViewById(R.id.img_line);
    img_line.setVisibility(View.GONE);

    // 定义Dialog布局和参数
    dialog = new Dialog(context, R.style.AlertDialogStyle);
    dialog.setContentView(view);

    // 调整dialog背景大小
    lLayout_bg.setLayoutParams(
        new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

    return this;
  }

  public AlertDialog setTitle(String title) {
    showTitle = true;
    txt_title.setText(title);
    return this;
  }

  public AlertDialog setMsg(String msg) {
    showMsg = true;
    txt_msg.setGravity(Gravity.CENTER);
    txt_msg.setText(msg);
    return this;
  }

  public String getPrice() {
    String mny = price.getText().toString();

    return mny;
  }

  public AlertDialog setMultiActionTextView(String msg, int start, int end) {
    final int colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
    MultiActionClickableSpan action1 =
        new MultiActionClickableSpan(start, end, colorPrimary, true, false, this);
    multiActionTextView.setText(msg, action1);
    return this;
  }

  public AlertDialog setMsgAlignLeft(String msg) {
    showMsg = true;
    txt_msg.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    txt_msg.setText(msg);
    return this;
  }

  public AlertDialog setBarcode(Bitmap bitmap) {
    showBarcode = true;
    ImageUtils.setImageBitmap(iv_barcode, bitmap);
    return this;
  }

  public AlertDialog setCancelable(boolean cancel) {
    dialog.setCancelable(cancel);
    return this;
  }

  public AlertDialog setCanceledOnTouchOutside(boolean cancel) {
    dialog.setCanceledOnTouchOutside(cancel);
    return this;
  }

  public AlertDialog setPositiveButton(String text) {
    showPosBtn = true;
    if (TextUtils.isEmpty(text)) {
      btn_pos.setText("确定");
    } else {
      btn_pos.setText(text);
    }
    btn_pos.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    return this;
  }

  public AlertDialog setPositiveButton(String text, final OnClickListener listener) {
    showPosBtn = true;
    if (TextUtils.isEmpty(text)) {
      btn_pos.setText("确定");
    } else {
      btn_pos.setText(text);
    }
    btn_pos.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        listener.onClick(v);
        dialog.dismiss();
      }
    });
    return this;
  }

  public AlertDialog setNegativeButton(String text, final OnClickListener listener) {
    showNegBtn = true;
    if (TextUtils.isEmpty(text)) {
      btn_neg.setText("取消");
    } else {
      btn_neg.setText(text);
    }
    btn_neg.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) {
          listener.onClick(v);
        }
        dialog.dismiss();
      }
    });
    return this;
  }

  public AlertDialog setNegativeButton(String text) {
    showNegBtn = true;
    if (TextUtils.isEmpty(text)) {
      btn_neg.setText("取消");
    } else {
      btn_neg.setText(text);
    }
    btn_neg.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    return this;
  }

  private void setLayout() {
    if (!showTitle && !showMsg) {
      txt_title.setText("提示");
      txt_title.setVisibility(View.VISIBLE);
    }

    if (showTitle) {
      txt_title.setVisibility(View.VISIBLE);
    }

    if (showMsg) {
      txt_msg.setVisibility(View.VISIBLE);
    }

    if (showBarcode) {
      iv_barcode.setVisibility(View.VISIBLE);
    }

    if (!showPosBtn && !showNegBtn) {
      //btn_pos.setText("确定");
      //btn_pos.setVisibility(View.VISIBLE);
      //btn_pos.setBackgroundResource(R.drawable.alert_dialog_single_selector);
      //btn_pos.setOnClickListener(new OnClickListener() {
      //  @Override public void onClick(View v) {
      //    dialog.dismiss();
      //  }
      //});

      btn_pos.setVisibility(View.GONE);
      btn_neg.setVisibility(View.GONE);
      img_line.setVisibility(View.GONE);
    }

    if (showPosBtn && showNegBtn) {
      btn_pos.setVisibility(View.VISIBLE);
      btn_pos.setBackgroundResource(R.drawable.alert_dialog_right_selector);
      btn_neg.setVisibility(View.VISIBLE);
      btn_neg.setBackgroundResource(R.drawable.alert_dialog_left_selector);
      img_line.setVisibility(View.VISIBLE);
    }

    if (showPosBtn && !showNegBtn) {
      btn_pos.setVisibility(View.VISIBLE);
      btn_pos.setBackgroundResource(R.drawable.alert_dialog_single_selector);
    }

    if (!showPosBtn && showNegBtn) {
      btn_neg.setVisibility(View.VISIBLE);
      btn_neg.setBackgroundResource(R.drawable.alert_dialog_single_selector);
    }
  }

  public void show() {
    setLayout();
    dialog.show();
  }

  public void dismiss() {
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  CallPayType callPayType;

  public interface CallPayType {
    void callback();
  }

  public void setCallback(CallPayType callPayType) {
    this.callPayType = callPayType;
  }

  @Override public void onTextClicked(View view, MultiActionClickableSpan span) {
    String text = ((TextView) view).getText().toString();
    Toast.makeText(context, text.substring(span.getStart(), span.getEnd()),
        Toast.LENGTH_SHORT).show();
    if (callPayType != null) {
      callPayType.callback();
    }
  }
}