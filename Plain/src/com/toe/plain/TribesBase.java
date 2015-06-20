package com.toe.plain;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconsPopup;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

public class TribesBase extends SherlockFragmentActivity {
	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	Intent i;
	TribesListItemAdapter adapter;
	XListView listView;
	StorageService storageService;
	String hashtag, error;
	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray;
	ShimmerTextView tvNoListItem;
	StoryOptionsCustomDialog socDialog;
	SherlockFragmentActivity activity;
	ArrayList<String> storedTags;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	ArrayList<String> favouriteTimestamps = new ArrayList<String>();
	SharedPreferences sp;
	EditDataCustomDialog edcDialog;
	TribeListCustomDialog tlcDialog;
	boolean storyIsClean = true;
	ArrayList<String> savedHashtags;
	EmojiconEditText emojiconEditText;
	View rootView;
	ImageView emojiButton;
	ImageView submitButton;
	EmojiconsPopup popup;
}
