package com.toe.plain;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoredTribeListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<StoredTribeListItem> {

	private ArrayList<StoredTribeListItem> objects;
	private Context context;
	private Typeface font;

	public StoredTribeListItemAdapter(Context context, int textViewResourceId,
			ArrayList<StoredTribeListItem> objects) {
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
			v = inflater.inflate(R.layout.stored_tribe_list_item, null);
		}

		StoredTribeListItem i = objects.get(position);
		if (i != null) {
			TextView tvName = (TextView) v.findViewById(R.id.tvName);
			tvName.setTypeface(font);

			if (tvName != null) {
				tvName.setText(i.getName());
			}
		}
		return v;
	}
}