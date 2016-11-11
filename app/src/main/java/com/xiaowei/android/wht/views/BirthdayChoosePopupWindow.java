package com.xiaowei.android.wht.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaowei.android.wht.R;
import com.xiaowei.android.wht.utils.mLog;
import com.xiaowei.android.wht.views.PickerView.onSelectListener;

@SuppressLint("ViewConstructor")
public class BirthdayChoosePopupWindow extends PopupWindow {
	
	private PickerView pvYear;
	private PickerView pvMonth;
	private PickerView pvDay;
	private TextView tvCancel;
	private TextView tvOk;
	
	private int maxYear;
	private int minYear;
	/**
	 * 选择的日期
	 */
	private int yearSelect,monthSelect,daySelect;
	
	private CallBack callBack;
	
	public BirthdayChoosePopupWindow(Activity context){
		
		View customView = context.getLayoutInflater().inflate(R.layout.popupwindow_birthday_choose, null, false); 
		
		setContentView(customView);
		
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setAnimationStyle(R.style.AnimationFade);  
		
		initDatas();
		initViews(customView);
		initListeners(customView);
		initPickerView();
	}
	
	private void initDatas() {
		Calendar c = Calendar.getInstance();
		maxYear = c.get(Calendar.YEAR);
		minYear = maxYear - 130;
		monthSelect = 1;
        daySelect = 1;
	}

	private void initListeners(View customView) {
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.dismiss();
				}
			}
		});
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
				if(callBack != null){
					callBack.select(yearSelect, monthSelect, daySelect);
				}
			}
		});
		
		pvYear.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				yearSelect = Integer.parseInt(text);
				daySelect = daySelect <= getDays(yearSelect, monthSelect) ? daySelect : getDays(yearSelect, monthSelect);
				List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
				pvDay.setData(dayData);
				mLog.d("birthday", "yearSelect:"+yearSelect);
			}
		});
		
		pvMonth.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				monthSelect = Integer.parseInt(text);
				daySelect = daySelect <= getDays(yearSelect, monthSelect) ? daySelect : getDays(yearSelect, monthSelect);
				List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
				pvDay.setData(dayData);
				mLog.d("birthday", "monthSelect:"+monthSelect);
			}
		});
		
		pvDay.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				daySelect = Integer.parseInt(text);
				mLog.d("birthday", "daySelect:"+daySelect);
			}
		});
	}
	
	private void initPickerView() {
		yearSelect = maxYear-30;
		List<String> yearData = getYearData(yearSelect);
		List<String> monthData = getMonthData(monthSelect);
		List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
		pvYear.setData(yearData);
		pvMonth.setData(monthData);
		pvDay.setData(dayData);
	}
	
	private void initViews(View customView) {
		
		pvYear = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_year);
		pvMonth = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_month);
		pvDay = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_day);
		tvCancel = (TextView) customView.findViewById(R.id.tv_birthday_choose_cancel);
		tvOk = (TextView) customView.findViewById(R.id.tv_birthday_choose_ok);
		
	}
	
	/**
	 * 
	 * @param year 要显示的当前（或选择）年份
	 * @return
	 */
	private List<String> getYearData(int year){
		List<String> yearData = new ArrayList<String>();
		for (int i = maxYear; i >= minYear; i--)
		{
			yearData.add(String.valueOf(i));
		}
		if(year - (maxYear-(maxYear-minYear)/2) < 0){
			for (int i = 1; i <= (maxYear-(maxYear-minYear)/2) - year; i++) {
				String head = yearData.get(0);
				yearData.remove(0);
				yearData.add(head);
			}
		}else if(year - (maxYear-(maxYear-minYear)/2) > 0){
			for (int i = 1; i <= year - (maxYear-(maxYear-minYear)/2); i++) {
				String tail = yearData.get(yearData.size() - 1);
				yearData.remove(yearData.size() - 1);
				yearData.add(0, tail);
			}
		}
		return yearData;
	}

	/**
	 * view月的list
	 * @param month 要显示的当前（或选择）月份
	 * @return
	 */
	private List<String> getMonthData(int month){
		List<String> monthData = new ArrayList<String>();
		for (int i = 1; i <= 12; i++)
		{
			monthData.add(String.valueOf(i));
		}
		if(month-1 - 6 > 0){
			for (int i = 1; i <= month-1 - 6; i++) {
				String head = monthData.get(0);
				monthData.remove(0);
				monthData.add(head);
			}
		}else if(month-1 - 6 < 0){
			for (int i = 1; i <= 6 - month+1; i++) {
				String tail = monthData.get(monthData.size() - 1);
				monthData.remove(monthData.size() - 1);
				monthData.add(0, tail);
			}
		}
		return monthData;
	}

	/**
	 * view日的list
	 * @param year
	 * @param month 
	 * @param day 要显示的当前（或选择）日
	 * @return
	 */
	private List<String> getDayData(int year, int month, int day){
		List<String> dayData = new ArrayList<String>();
		for (int i = 1; i <= getDays(year, month); i++)
		{
			dayData.add(String.valueOf(i));
		}
		if(day-1 - getDays(year, month)/2 > 0){
			for (int i = 1; i <= day-1 - getDays(year, month)/2; i++) {
				String head = dayData.get(0);
				dayData.remove(0);
				dayData.add(head);
			}
		}else if(day-1 - getDays(year, month)/2 < 0){
			for (int i = 1; i <= getDays(year, month)/2 - day+1; i++) {
				String tail = dayData.get(dayData.size() - 1);
				dayData.remove(dayData.size() - 1);
				dayData.add(0, tail);
			}
		}
		return dayData;
	}

	/**
	 * 某年某月的天数
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDays(int year, int month){
		int days = 31;
		if(month == 4 || month == 6 || month == 9 || month == 11){
			days = 30;
		}
		else if(month == 2){
			if (year % 4 == 0 && year % 100!=0||year%400==0) {  
				days = 29;
			} else{
				days = 28;
			}
		}
		return days;
	}
	
	public void setCallBack(CallBack callBack){
		this.callBack = callBack;
	}
	
	public interface CallBack {  
	    void dismiss(); 
	    void select(int yearSelect, int monthSelect, int daySelect);
	} 
	
	/**
	 * minYear ~ maxYear
	 * @param maxYear 
	 * @param minYear
	 */
	public void setYearMaxMin(int maxYear, int minYear) {
		if(maxYear > 1000 && minYear > 1000 && maxYear > minYear+10){
			this.maxYear = maxYear;
			this.minYear = minYear;
			initPickerView();
		}
	}

}
