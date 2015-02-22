package com.toe.plain;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<ListItem> {

	private ArrayList<ListItem> objects;
	private Context context;
	private Typeface font;

	public ListItemAdapter(Context context, int textViewResourceId,
			ArrayList<ListItem> objects) {
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
			v = inflater.inflate(R.layout.list_item, null);
		}

		ListItem i = objects.get(position);
		if (i != null) {
			TextView tvStory = (TextView) v.findViewById(R.id.story);
			tvStory.setTypeface(font);
			TextView tvLikes = (TextView) v.findViewById(R.id.likes);
			TextView tvTag = (TextView) v.findViewById(R.id.tag);

			if (tvStory != null) {
				tvStory.setText("\"" + i.getStory() + "\"");

				if (i.isAdmin()) {
					tvStory.setTextColor(Color.rgb(255, 0, 0));
				} else {
					tvStory.setTextColor(Color.rgb(68, 68, 68));
				}
			}

			if (tvLikes != null) {
				tvLikes.setText(i.getLikes() + "");
			}

			if (tvTag != null) {
				if (i.isAdmin()) {
					tvTag.setText("dev");
				} else {
					tvTag.setText(i.getTag().toLowerCase() + "");
				}
			}
		}
		return v;
	}
}