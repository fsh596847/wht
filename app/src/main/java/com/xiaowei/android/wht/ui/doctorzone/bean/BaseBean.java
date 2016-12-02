package com.xiaowei.android.wht.ui.doctorzone.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HIPAA on 2016/11/23.
 */
@Setter @Getter
public class BaseBean<T> {
  public int status;
  public T data;
}
