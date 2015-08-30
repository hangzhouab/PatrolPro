package com.ab.nfc;

import java.util.List;
import java.util.Map;

import com.ab.health.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NFCAdapter extends SimpleAdapter {

	public NFCAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView address = (TextView) view.findViewById(R.id.view_meal_list_name_tv);
		int[] colors = new int[]{0xff2468a2,0xff33a3dc};
		int colorPos = position % 2;
		address.setTextColor(colors[colorPos]);
//		if(position == 0 ){
//			address.setTextColor(0xff7a1723);
//		}
		
		
		return view;
	}

}
