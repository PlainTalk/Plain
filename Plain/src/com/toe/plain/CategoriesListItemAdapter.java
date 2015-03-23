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

public class CategoriesListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<CategoriesListItem> {

	private ArrayList<CategoriesListItem> objects;
	private Context context;
	private Typeface font;

	public CategoriesListItemAdapter(Context context, int textViewResourceId,
			ArrayList<CategoriesListItem> objects) {
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
			v = inflater.inflate(R.layout.category_list_item, null);
		}

		CategoriesListItem i = objects.get(position);
		if (i != null) {
			TextView tvCategoryName = (TextView) v.findViewById(R.id.category);
			TextView tvCategoryDescription = (TextView) v
					.findViewById(R.id.description);
			tvCategoryDescription.setTypeface(font);

			if (tvCategoryName != null) {
				tvCategoryName.setText(i.getCategory());
				tvCategoryName.setTextColor(Color.rgb(15, 91, 107));
			}

			if (tvCategoryDescription != null) {
				tvCategoryDescription.setText(i.getDescription());
			}

		}
		return v;
	}
}