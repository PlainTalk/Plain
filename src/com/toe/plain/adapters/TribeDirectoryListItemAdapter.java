package com.toe.plain.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.toe.plain.R;
import com.toe.plain.listitems.TribeDirectoryListItem;

public class TribeDirectoryListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<TribeDirectoryListItem> {

	private ArrayList<TribeDirectoryListItem> objects;
	private Context context;
	private Typeface font;

	public TribeDirectoryListItemAdapter(Context context,
			int textViewResourceId, ArrayList<TribeDirectoryListItem> objects) {
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

		TribeDirectoryListItem i = objects.get(position);
		if (i != null) {
			TextView tvName = (TextView) v.findViewById(R.id.tvName);
			tvName.setTypeface(font);
			TextView tvDescription = (TextView) v
					.findViewById(R.id.tvDescription);
			tvDescription.setTypeface(font);
			TextView tvLikes = (TextView) v.findViewById(R.id.tvLikes);
			TextView tvLikesTitle = (TextView) v
					.findViewById(R.id.tvLikesTitle);
			ImageView ivNotification = (ImageView) v
					.findViewById(R.id.ivNotification);

			if (tvName != null) {
				tvName.setText(i.getName());
			}
			if (tvDescription != null) {
				tvDescription.setText(i.getDescription());
			}
			if (tvLikes != null) {
				tvLikes.setText(i.getLikes() + "");
			}
			if (tvLikesTitle != null) {
				if (i.getLikes() == 1) {
					tvLikesTitle.setText("like");
				} else {
					tvLikesTitle.setText("likes");
				}
			}

			if (ivNotification != null) {
				boolean hasNotification = i.hasNewTribePlains();
				if (hasNotification) {
					ivNotification.setVisibility(View.VISIBLE);
				} else {
					ivNotification.setVisibility(View.INVISIBLE);
				}
			}
		}
		return v;
	}
}