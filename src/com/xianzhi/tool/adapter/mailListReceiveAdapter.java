package com.xianzhi.tool.adapter;

import java.util.List;
import java.util.Map;

import com.xianzhi.office.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class mailListReceiveAdapter extends SimpleAdapter {
	// 用于动态的载入一个界面文件
	private List<Map<String, Object>> styles = null;
	private LayoutInflater inflater = null;

	public List<Map<String, Object>> getStyles() {
		return styles;
	}

	public void setStyles(List<Map<String, Object>> styles) {
		this.styles = styles;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View result = super.getView(position, convertView, parent);
		if (result != null) {
			inflater.inflate(R.layout.cell_mail_list, null);
		}
		ImageView image = (ImageView)result.findViewById(R.id.has_attachment);
		int hasattachment=(Integer)styles.get(position).get("hasattachment");
		image.setVisibility(hasattachment==1?View.VISIBLE:View.GONE);
		TextView title = (TextView)result.findViewById(R.id.mail_title);
		int readflag=(Integer)styles.get(position).get("readflag");
		TextPaint tp = title.getPaint(); 
		tp.setFakeBoldText(readflag==0?true:false);  
		return result;
	}

	public void notify_change() {
		this.notifyDataSetChanged();
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	public mailListReceiveAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		inflater = LayoutInflater.from(context);
		styles = (List<Map<String, Object>>) data;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}
}
