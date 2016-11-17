package com.xiaowei.android.wht.views;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.xiaowei.android.wht.ApplicationTool;

/**
 * Created by HIPAA on 2016/11/17.
 */

public class TextFont extends TextView {
  private TextPaint paint;

  public TextFont(Context context) {
    super(context);
    setTypeface(ApplicationTool.getInstace().getTypeface());
  }

  public TextFont(Context context, AttributeSet attrs) {
    super(context, attrs);
    setTypeface(ApplicationTool.getInstace().getTypeface());
  }

  public TextFont(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setTypeface(ApplicationTool.getInstace().getTypeface());
  }

  // 设置粗体 其实就是将画笔变粗即可
  public void setTextblod(boolean textIsRegular) {
    if (textIsRegular) {
      paint = super.getPaint();
      paint.setFakeBoldText(true);
    }
  }
}
