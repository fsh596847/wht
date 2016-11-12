/**
 * @Description:
 * @author fsh
 * @date 2015-4-8 下午2:09:17
 * @version V1.0
 */
package com.xiaowei.android.wht.ui.doctorzone;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.view.*;


/**
 * fragment 基类
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 上下文
     */
    protected Context mContext;

    protected Activity mActivity;

    protected View mContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = createView(inflater, container, savedInstanceState);
        mContainer = view;

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(mContainer, savedInstanceState);
        setAdapter();
        setListener();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    /**
     * 为Activity设置布局
     *
     * @param savedInstanceState
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * init...
     *
     * @param container
     */
    protected abstract void init(View container, Bundle savedInstanceState);

    /**
     * 为ListView设置适配器
     */
    protected abstract void setAdapter();

    /**
     * 为组建设置监听器
     */
    protected abstract void setListener();

}
