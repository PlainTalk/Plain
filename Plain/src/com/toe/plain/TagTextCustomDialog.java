package com.toe.plain;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

public class TagTextCustomDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public TextView tvTag;
	public EmojiconTextView tvTagText;
	public String collection, tag, tagText, error;
	private Context context;
	private StorageService storageService;
	private ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray;
	ArrayList<ListItem> queryResults = new ArrayList<ListItem>();
	ArrayList<String> queryResultStories = new ArrayList<String>();
	ArrayList<Integer> queryResultLikes = new ArrayList<Integer>();
	ArrayList<String> queryResultTags = new ArrayList<String>();
	ArrayList<Boolean> queryResultAdmins = new ArrayList<Boolean>();

	public TagTextCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		context = activity.getApplicationContext();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tag_text_custom_dialog);

		tvTag = (TextView) findViewById(R.id.tvTag);
		tvTag.setText("@" + tag);
		tvTag.setTextColor(Color.rgb(245, 173, 30));
		tvTagText = (EmojiconTextView) findViewById(R.id.tvTagText);
		initialize(context);
		searchForTag(tag);
	}

	private void initialize(Context context) {
		// TODO Auto-generated method stub
		App42API.initialize(context, context.getString(R.string.api_key),
				context.getString(R.string.secret_key));
		storageService = App42API.buildStorageService();
	}

	protected void searchForTag(String tagQuery) {
		// TODO Auto-generated method stub
		String key = "tag";
		Query query = QueryBuilder.build(key, tagQuery, Operator.LIKE);
		HashMap<String, String> metaHeaders = new HashMap<String, String>();
		metaHeaders.put("orderByDescending", "_$createdAt");
		storageService.setOtherMetaHeaders(metaHeaders);
		storageService.findDocumentsByQuery(
				context.getString(R.string.database_name), collection, query,
				new App42CallBack() {
					public void onSuccess(Object response) {
						Storage storage = (Storage) response;
						ArrayList<Storage.JSONDocument> jsonDocList = storage
								.getJsonDocList();

						jsonDocArray = new ArrayList<String>();
						jsonIdArray = new ArrayList<String>();
						jsonTimesArray = new ArrayList<String>();

						for (int i = 0; i < jsonDocList.size(); i++) {
							jsonDocArray.add(jsonDocList.get(i).getJsonDoc());
							jsonIdArray.add(jsonDocList.get(i).getDocId());
							jsonTimesArray.add(jsonDocList.get(i)
									.getCreatedAt());
						}

						queryResultStories.clear();
						queryResultLikes.clear();
						queryResultTags.clear();
						queryResultAdmins.clear();
						queryResults.clear();

						for (int i = 0; i < jsonDocArray.size(); i++) {
							try {
								JSONObject json = new JSONObject(jsonDocArray
										.get(i));
								queryResultStories.add(json.getString("story"));
								queryResultLikes.add(json.getInt("likes"));
								queryResultTags.add(json.getString("tag"));
								queryResultAdmins.add(json.getBoolean("admin"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						for (int i = 0; i < queryResultStories.size(); i++) {
							queryResults.add(new ListItem(queryResultStories
									.get(i), queryResultLikes.get(i),
									queryResultTags.get(i), queryResultAdmins
											.get(i), jsonTimesArray.get(i)));
						}

						setResultText(queryResults);
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
						errorHandler(ex);
					}
				});
	}

	private void setResultText(final ArrayList<ListItem> queryResults) {
		// TODO Auto-generated method stub
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvTagText
						.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod
								.getInstance());
				tvTagText.setText(addClickablePart("\""
						+ queryResults.get(0).getStory() + "\""),
						TextView.BufferType.SPANNABLE);
				tvTagText.setFocusable(false);
				tvTagText.setClickable(false);
				tvTagText.setLongClickable(false);
			}
		});
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
			ttcDialog.tag = clicked.replaceAll("@", "");
			ttcDialog.show();
		}

		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false);
			ds.setColor(Color.rgb(245, 173, 30));
		}
	}

	private void errorHandler(Exception ex) {
		// TODO Auto-generated method stub
		if (ex.getMessage().contains("refused")
				|| ex.getMessage().contains("UnknownHostException")
				|| ex.getMessage().contains("SSL")
				|| ex.getMessage().contains("ConnectTimeoutException")
				|| ex.getMessage().contains("Neither")
				|| ex.getMessage().contains("Socket")) {
			error = "No internet connection :-(";
		} else if (ex.getMessage().contains("No document")) {
			error = "No plains found :-(";
		} else if (ex.getMessage().contains("UnAuthorized Access")) {
			error = "Hi, how are you? Please try again in a few minutes :-)";
		} else {
			error = ex.getMessage();
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvTagText.setText(error);
			}
		});
	}
}
