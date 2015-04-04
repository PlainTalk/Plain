package com.toe.plain;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TribeListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<TribeListItem> {

	private ArrayList<TribeListItem> objects;
	private Context context;
	private Typeface font;

	public TribeListItemAdapter(Context context, int textViewResourceId,
			ArrayList<TribeListItem> objects) {
		super(objects);
		this.objects = objects;
		this.context = context;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		font = Typeface.createFromAsset(context.getAssets(),
				context.getString(R.string.list_font));

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.tribe_list_item, null);
		}

		TribeListItem i = objects.get(position);
		if (i != null) {
			TextView tvTribe = (TextView) v.findViewById(R.id.tvTribe);
			tvTribe.setTypeface(font);

			if (tvTribe != null) {
				tvTribe.setText(i.getTribe());
			}

		}
		return v;
	}
}