package com.xiaowei.android.wht.ui.doctorzone.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import rx.Subscriber;

/**
 * Created by AIS on 2016/5/12 0012.
 */
public class ProgressSubscriber extends Subscriber<String> {

  public SubscriberOnNextListener mSubscriberOnNextListener;
  public Context context;
  private String TAG = ProgressSubscriber.class.getSimpleName();
  private ProgressDialog pd;
  private boolean cancelable;
  private boolean isShowProgress;
  private boolean mCancelCallOnBackPressed;

  public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context,
      boolean isShowProgress) {
    this.mSubscriberOnNextListener = mSubscriberOnNextListener;
    this.context = context;
    this.isShowProgress = isShowProgress;
  }

  public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context,
      boolean cancelable, boolean isShowProgress) {
    this.mSubscriberOnNextListener = mSubscriberOnNextListener;
    this.context = context;
    this.cancelable = cancelable;
    this.isShowProgress = isShowProgress;
  }

  public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener) {
    this.mSubscriberOnNextListener = mSubscriberOnNextListener;
  }

  /**
   * 订阅开始时调用 显示ProgressDialog
   */
  @Override public void onStart() {

    showProgressDialog();
  }

  private void showProgressDialog() {
    if (pd == null && isShowProgress) {
      pd = new ProgressDialog(context);
      pd.setCancelable(cancelable);
      if (cancelable) {
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialogInterface) {
            if (!isUnsubscribed()) {
              unsubscribe();
            }
          }
        });
      }
      if (!pd.isShowing()) {
        pd.show();
      }
    }
  }

  /**
   * 完成，隐藏ProgressDialog
   */
  @Override public void onCompleted() {
    if (!this.isUnsubscribed()) {
      this.unsubscribe();
    }
    dismissProgressDialog();
  }

  /**
   * 对错误进行统一处理 隐藏ProgressDialog
   */
  @Override public void onError(Throwable e) {
    dismissProgressDialog();
    mSubscriberOnNextListener.onError(e.getMessage());
  }

  private void dismissProgressDialog() {
    if (pd != null && isShowProgress) {
      pd.dismiss();
      pd = null;
      context = null;
    }
  }

  /**
   * 将onNext方法中的返回结果交给Activity或Fragment自己处理
   *
   * @param o 创建Subscriber时的泛型类型
   */
  @Override public void onNext(String o) {
    if (mSubscriberOnNextListener != null) {
      mSubscriberOnNextListener.onNext(o);
    }
  }

  /**
   * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
   */
  //@Override public void onCancelProgress() {
  //  if (!this.isUnsubscribed()) {
  //    this.unsubscribe();
  //  }
  //}
}