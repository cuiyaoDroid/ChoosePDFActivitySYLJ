package com.xianzhi.tool.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.xianzhi.office.R;
import com.xianzhi.tool.view.wheel.OnWheelChangedListener;
import com.xianzhi.tool.view.wheel.WheelView;


@SuppressLint("ViewConstructor")
public class SelectDoublePopupWindow extends PopupWindow {

	private Button positiveButton, negativeButton;
	private View mMenuView;
	private EditText textview;
	private HashMap<String,ArrayList<String>> valuelist;
	private Context context;
	public SelectDoublePopupWindow(Activity context, EditText textview,
			HashMap<String,ArrayList<String>>valuelist) {
		super(context);
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwin_double_wheel, null);
		this.valuelist=valuelist;
		this.textview = textview;
		/**
		 * 按时间添加策略view
		 */
		Iterator<String> iter_list=valuelist.keySet().iterator();
		String[] department = new String[valuelist.size()];
		int i=0;
		while (iter_list.hasNext()) {
			department[i] = iter_list.next();
			i++;
		}
		initWheel(mMenuView, R.id.passw_1, department);
		setOnChangedListener(mMenuView, R.id.passw_1);
		ArrayList<String> array=new ArrayList<String>();
		if(valuelist.size()>0){
			array=valuelist.get(getWheelValue(mMenuView, R.id.passw_1));
		}
		name = new String[array.size()];
		int j=0;
		for (String str_name:array) {
			name[j] = str_name;
			j++;
		}
		initWheel(mMenuView, R.id.passw_2, name);
		// 设置按钮监听
		positiveButton = (Button) mMenuView.findViewById(R.id.positiveButton);
		negativeButton = (Button) mMenuView.findViewById(R.id.negativeButton);
		positiveButton.setText(R.string.be_sure);
		negativeButton.setText(R.string.cancal);
		positiveButton.setOnClickListener(itemsOnClick);
		negativeButton.setOnClickListener(itemsOnClick);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			dismiss();
			switch (v.getId()) {
			case R.id.positiveButton:
				int index=getWheel(mMenuView, R.id.passw_2).getCurrentItem();
				if(name!=null){
					if(name.length>index)
					textview.setText(name[index]);
				}
				break;
			case R.id.negativeButton:
				break;
			default:
				break;
			}
		}
	};
	private String[] name;
	/**
	 * @param view
	 * @param id
	 */
	private void setOnChangedListener(View view, int id) {
		getWheel(view, id).addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String value = ((myWheelAdapter)wheel.getViewAdapter()).getItemText(wheel.getCurrentItem()).toString();
				ArrayList<String> array=valuelist.get(value);
				name = new String[array.size()];
				int j=0;
				for (String str_name:array) {
					name[j] = str_name;
					j++;
				}
				initWheel(mMenuView, R.id.passw_2, name);
			}
		});
	}

	private String getWheelValue(View view, int id) {
		if(getWheel(view, id).getViewAdapter().getItemsCount()>0){
			return ((myWheelAdapter)getWheel(view, id).getViewAdapter()).getItemText(getWheel(view, id).getCurrentItem()).toString();
		}
		return "";
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id 我添加的
	 * 
	 */
	private WheelView getWheel(View view, int id) {
		return (WheelView) view.findViewById(id);
	}
	private void initWheel(View view, int id, String[] strContents) {
		WheelView wheel = getWheel(view, id);
		wheel.setViewAdapter(new myWheelAdapter(context, strContents));
		wheel.setCurrentItem(0);
		wheel.setVisibleItems(5);
//		wheel.setCyclic(false);
//		wheel.setInterpolator(new AnticipateOvershootInterpolator());
	}
	/**
     * Adapter for countries
     */
    private class myWheelAdapter extends AbstractWheelTextAdapter {
        /**
         * Constructor
         */
    	private String[] strContents;
        protected myWheelAdapter(Context context,String[] strContents) {
        	super(context, R.layout.cell_wheel_text, NO_RESOURCE);
            setItemTextResource(R.id.name);
            this.strContents=strContents;
        }
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }
        
        @Override
        public int getItemsCount() {
            return strContents.length;
        }
        
        @Override
        protected CharSequence getItemText(int index) {
        	if (index >= 0 && index < getItemsCount()) {
    			return strContents[index].split("<")[0];
    		}
			return null;
        }
    }
}
