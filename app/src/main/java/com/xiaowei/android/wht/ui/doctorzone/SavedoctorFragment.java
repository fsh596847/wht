package com.xiaowei.android.wht.ui.doctorzone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaowei.android.wht.R;


public class SavedoctorFragment extends BaseFragment {


    public static SavedoctorFragment newInstance() {
        SavedoctorFragment f = new SavedoctorFragment();

        return f;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);

        return view;
    }

    @Override
    protected void init(View container, Bundle savedInstanceState) {

    }

    @Override
    protected void setAdapter() {

    }

    @Override
    protected void setListener() {

    }


}
