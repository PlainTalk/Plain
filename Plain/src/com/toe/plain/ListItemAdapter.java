package com.toe.plain;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<ListItem> {

	private ArrayList<ListItem> objects;
	private Context context;

	public ListItemAdapter(Context context, int textViewResourceId,
			ArrayList<ListItem> objects) {
		super(objects);
		this.objects = objects;
		this.context = context;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_item, null);
		}

		ListItem i = objects.get(position);
		if (i != null) {
			TextView tvPlain = (TextView) v.findViewById(R.id.plain);
			TextView tvLikes = (TextView) v.findViewById(R.id.likes);

			if (tvPlain != null) {
				tvPlain.setText("\"" + i.getPlain() + "\"");
			}

			if (tvLikes != null) {
				tvLikes.setText(i.getLikes() + "");
			}
		}
		return v;
	}
}