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
import com.toe.plain.listitems.ChatListItem;

public class ChatListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<ChatListItem> {

	private ArrayList<ChatListItem> objects;
	private Context context;
	private Typeface font;

	public ChatListItemAdapter(Context context, int textViewResourceId,
			ArrayList<ChatListItem> objects) {
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
			v = inflater.inflate(R.layout.chat_list_item, null);
		}

		ChatListItem i = objects.get(position);
		if (i != null) {
			TextView tvMessage = (TextView) v.findViewById(R.id.tvMessage);
			tvMessage.setTypeface(font);

			if (tvMessage != null) {
				tvMessage.setText(i.getMessage());
				if (i.getDirection() == 0) {
					tvMessage.setBackgroundResource(R.drawable.bubble_a);
				} else if (i.getDirection() == 1) {
					tvMessage.setBackgroundResource(R.drawable.bubble_b);
				}
			}
		}
		return v;
	}
}