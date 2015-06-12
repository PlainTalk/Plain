package com.toe.plain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TribeDirectoryListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<TribeDirectoryListItem> {

	private ArrayList<TribeDirectoryListItem> objects;
	private Context context;
	private Typeface font;
	private int timeOffset = 10800000;

	public TribeDirectoryListItemAdapter(Context context, int textViewResourceId,
			ArrayList<TribeDirectoryListItem> objects) {
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
			TextView tvTimestamp = (TextView) v.findViewById(R.id.tvTimestamp);

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

			if (tvTimestamp != null) {
				Date date = formatTime(i.getTimestamp());
				long time = date.getTime();

				Date curDate = currentDate();
				long now = curDate.getTime();
				if (time > now || time <= 0) {
					return null;
				}

				int timeDistance = getTimeDistanceInMilliseconds(time
						+ timeOffset);
				tvTimestamp.setText("made "
						+ TimeUtils.millisToLongDHMS(timeDistance) + " ago");
			}
		}
		return v;
	}

	@SuppressWarnings("deprecation")
	private Date formatTime(String rawTime) {
		// TODO Auto-generated method stub
		String spitDate[] = rawTime.split("T");
		String sDate = spitDate[0];
		String dateSplit[] = sDate.split("-");
		String sYear = dateSplit[0];
		String sMonth = dateSplit[1];
		String sDay = dateSplit[2];

		int month = Integer.parseInt(sMonth);
		int day = Integer.parseInt(sDay);

		String splitTime[] = rawTime.split(":");
		String rawHour = splitTime[0];
		String hour = rawHour.substring(rawHour.length() - 2, rawHour.length());
		String minute = splitTime[1];

		int hourInt = Integer.parseInt(hour);

		String hourString = hourInt + "";
		if (hourString.length() == 1) {
			hourString = 0 + hourString;
		}

		Date dateObj = new Date(Integer.parseInt(sYear.substring(2)) + 100,
				month - 1, day, hourInt, Integer.parseInt(minute));
		return dateObj;
	}

	public static Date currentDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	private static int getTimeDistanceInMilliseconds(long time) {
		long timeDistance = currentDate().getTime() - time;
		return Math.round(Math.abs(timeDistance));
	}
}