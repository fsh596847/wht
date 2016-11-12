package com.xiaowei.android.wht.ui.doctorzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaowei.android.wht.R;


public class DoctorZoneFragment extends BaseFragment {


    View view;

    public DoctorZoneFragment() {

    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_doctor, container, false);
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
