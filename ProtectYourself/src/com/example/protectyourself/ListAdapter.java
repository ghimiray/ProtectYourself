package com.example.protectyourself;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class ListAdapter extends ArrayAdapter<ListModel> {

	private final Context context;
	private final ArrayList<ListModel> modelsArrayList;

	public ListAdapter(Context context,
			ArrayList<ListModel> modelsArrayList) {

		super(context,R.layout.activity_main, modelsArrayList);

		this.context = context;
		this.modelsArrayList = modelsArrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 2. Get rowView from inflater

		View rowView = null;

		rowView = inflater.inflate(R.layout.list_content, parent, false);
		
		// 3. Get icon,title & counter views from the rowView
		// RelativeLayout lay = (RelativeLayout)
		// rowView.findViewById(R.id.imageLay);
		
		TextView name = (TextView) rowView
				.findViewById(R.id.nameText);
		name.setText(modelsArrayList.get(position).getName());
		
		ImageView add = (ImageView) rowView.findViewById(R.id.addView);
		add.setBackgroundResource(R.drawable.add);
		add.setTag(R.string.checka,modelsArrayList.get(position).getNumber());
		add.setTag(R.string.checkb,modelsArrayList.get(position).getName());
//		ImageButton ib = (ImageButton)rowView.findViewById(R.id.BookButtonAv);
//	
//		ib.setTag(R.string.checka,modelsArrayList.get(position).getPrice());
//		ib.setTag(R.string.checkb,modelsArrayList.get(position).getMachineName());
//		ib.setTag(R.string.checkc,modelsArrayList.get(position).getCounter());
//		ib.setTag(R.string.checkd,modelsArrayList.get(position).getUnit());

		
				

		
		return rowView;
	}

//	@Override
//	public long getItemId(int position) {
//		// return ID based on the position
//		return Long.valueOf(modelsArrayList.get(position).getNumber());
//	}
	

}