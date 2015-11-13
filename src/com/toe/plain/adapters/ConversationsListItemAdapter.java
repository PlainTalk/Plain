package com.toe.plain.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toe.plain.R;
import com.toe.plain.listitems.ConversationsListItem;

public class ConversationsListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<ConversationsListItem> {

	private ArrayList<ConversationsListItem> objects;
	private Context context;
	private Typeface font;

	public ConversationsListItemAdapter(Context context,
			int textViewResourceId, ArrayList<ConversationsListItem> objects) {
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
			v = inflater.inflate(R.layout.conversations_list_item, null);
		}

		ConversationsListItem i = objects.get(position);
		if (i != null) {
			TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
			tvScreenName.setTypeface(font);

			if (tvScreenName != null) {
				tvScreenName.setText(i.getName());
			}
		}
		return v;
	}
}