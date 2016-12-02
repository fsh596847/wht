package com.xiaowei.android.wht.ui.doctorzone.bean;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HIPAA on 2016/11/23.
 */
@Setter @Getter
public class CirCleBean extends BaseBean<List<CirCleBean.CirCleItemBean>> {
  @Setter @Getter
  public class CirCleItemBean implements Serializable {
    private String groupname;
    private String id;
    private String types;
    private boolean isCheck;
  }
}
