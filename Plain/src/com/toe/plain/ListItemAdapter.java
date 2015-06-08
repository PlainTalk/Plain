package com.toe.plain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ListItemAdapter extends
		com.nhaarman.listviewanimations.ArrayAdapter<ListItem> {

	private ArrayList<ListItem> objects;
	private Context context;
	private SherlockFragmentActivity activity;
	private Typeface font;
	private int timeOffset = 10800000;

	public ListItemAdapter(Context context, SherlockFragmentActivity activity,
			int textViewResourceId, ArrayList<ListItem> objects) {
		super(objects);
		this.objects = objects;
		this.context = context;
		this.activity = activity;
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
			TextView tvTimeStamp = (TextView) v.findViewById(R.id.tvTimestamp);
			FlipImageView ivLike = (FlipImageView) v.findViewById(R.id.ivLike);
			ivLike.setFlipped(true);

			if (tvStory != null) {
				tvStory.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod
						.getInstance());
				tvStory.setText(addClickablePart("\"" + i.getStory() + "\""),
						TextView.BufferType.SPANNABLE);
				tvStory.setFocusable(false);
				tvStory.setClickable(false);
				tvStory.setLongClickable(false);

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
			if (tvTimeStamp != null) {
				Date date = formatTime(i.getTimestamp());
				long time = date.getTime();

				Date curDate = currentDate();
				long now = curDate.getTime();
				if (time > now || time <= 0) {
					return null;
				}

				int timeDistance = getTimeDistanceInMilliseconds(time
						+ timeOffset);
				tvTimeStamp.setText(TimeUtils.millisToLongDHMS(timeDistance)
						+ " ago");
			}
		}
		return v;
	}

	private SpannableStringBuilder addClickablePart(String str) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(str);

		if (!str.contains("rp@")) {
			int idx1 = str.indexOf("@");
			int idx2 = 0;
			while (idx1 != -1) {
				idx2 = idx1 + 4;
				final String clickString = str.substring(idx1, idx2);
				ssb.setSpan(new TextClickableSpan(clickString), idx1, idx2, 0);
				idx1 = str.indexOf("@", idx2);
			}
		} else {
			int idx1 = str.indexOf("@", str.indexOf("@") + 1);
			int idx2 = 0;
			while (idx1 != -1) {
				idx2 = idx1 + 4;
				final String clickString = str.substring(idx1, idx2);
				ssb.setSpan(new TextClickableSpan(clickString), idx1, idx2, 0);
				idx1 = str.indexOf("@", idx2);
			}
		}

		return ssb;
	}

	class TextClickableSpan extends ClickableSpan {
		String clicked;

		public TextClickableSpan(String string) {
			// TODO Auto-generated constructor stub
			super();
			clicked = string;
		}

		public void onClick(View tv) {
			TagTextCustomDialog ttcDialog = new TagTextCustomDialog(activity);
			ttcDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			ttcDialog.collection = context.getResources().getString(
					R.string.collection_name);
			ttcDialog.tag = clicked.replaceAll("@", "");
			ttcDialog.show();
		}

		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false);
			ds.setColor(Color.rgb(12, 98, 120));
		}
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