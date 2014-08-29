package com.xianzhi.tool.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xianzhi.office.R;
import com.xianzhi.office.writeEmailActivity;


public class popListAdapter extends SimpleAdapter {
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
			inflater.inflate(R.layout.cell_poplist, null);
		}
		final TextView text=(TextView)result.findViewById(R.id.autoedit_text);
		text.setClickable(true);
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				writeEmailActivity.editingText.setText(text.getText().toString());
			}
		});
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
	public popListAdapter(Context context,
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
