package com.toe.plain.activities;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconsPopup;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.tjeannin.apprate.AppRate;
import com.toe.plain.adapters.ConversationsListItemAdapter;
import com.toe.plain.adapters.ListItemAdapter;
import com.toe.plain.adapters.PlainFragmentAdapter;
import com.toe.plain.adapters.TribeDirectoryListItemAdapter;
import com.toe.plain.classes.ShimmerTextView;
import com.toe.plain.classes.XListView;
import com.toe.plain.dialogs.EditDataCustomDialog;
import com.toe.plain.dialogs.ExitCustomDialog;
import com.toe.plain.dialogs.FavouriteOptionsCustomDialog;
import com.toe.plain.dialogs.NewTribeCustomDialog;
import com.toe.plain.dialogs.ReplyOptionsCustomDialog;
import com.toe.plain.dialogs.StoryOptionsCustomDialog;
import com.toe.plain.listitems.ConversationsListItem;
import com.toe.plain.listitems.ListItem;
import com.toe.plain.listitems.TribeDirectoryListItem;
import com.viewpagerindicator.PageIndicator;

public class PlainBase extends SherlockFragmentActivity {
	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	ArrayList<ListItem> favourites;
	PlainFragmentAdapter mAdapter;
	ViewPager mPager;
	Intent i;
	PageIndicator mIndicator;
	ListItemAdapter adapter, repliesAdapter, favouritesAdapter;
	ArrayList<TribeDirectoryListItem> tribes = new ArrayList<TribeDirectoryListItem>();
	TribeDirectoryListItemAdapter tribesAdapter;
	XListView listView, repliesListView, conversationsListView, tribesListView,
			favouritesListView;
	StorageService storageService;
	String lastTribe, tribeName, tribeDescription, error;
	EditText etStory, etSearchForTag;
	Button bSearchForTag;
	SherlockFragmentActivity activity;
	ShimmerTextView tvNoListItem, tvNoReplyListItem, tvNoConversationListItem,
			tvNoTribeListItem, tvNoFavouriteListItem;
	SharedPreferences sp;
	Button bShare, bFavourite;
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();
	ArrayList<String> favouriteTimestamps = new ArrayList<String>();
	ArrayList<String> storedTags = new ArrayList<String>();
	ArrayList<String> fetchedTagStories = new ArrayList<String>();
	ArrayList<ListItem> replies = new ArrayList<ListItem>();
	ArrayList<ListItem> queryResults = new ArrayList<ListItem>();
	ArrayList<String> queryResultStories = new ArrayList<String>();
	ArrayList<Integer> queryResultLikes = new ArrayList<Integer>();
	ArrayList<String> queryResultTags = new ArrayList<String>();
	ArrayList<Boolean> queryResultAdmins = new ArrayList<Boolean>();
	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray,
			appendJsonDocArray, appendJsonIdArray, appendJsonTimesArray,
			savedHashtags;
	StoryOptionsCustomDialog socDialog;
	ReplyOptionsCustomDialog rocDialog;
	FavouriteOptionsCustomDialog fsocDialog;
	EditDataCustomDialog edcDialog;
	ExitCustomDialog ecDialog;
	AlertDialog.Builder builder;
	AppRate rate;
	boolean storyIsClean = true, nameIsClean, descriptionIsClean;
	int offset = 100, j;
	EmojiconEditText emojiconEditText, emojiconEditTextReplies;
	View rootView, rootViewReplies;
	ImageView emojiButton, emojiconButtonReplies;
	ImageView submitButton, submitButtonReplies;
	EmojiconsPopup popup, popupReplies;
	int mInterval = 20000;
	Handler mHandler;
	SwingBottomInAnimationAdapter swing;
	NewTribeCustomDialog ntcDialog;
	String senderUsername, senderPassword, receiverUsername;
	ArrayList<ConversationsListItem> conversationNames = new ArrayList<ConversationsListItem>();
	public static Handler handleChange;
	ConversationsListItemAdapter conversationsAdapter;
	ArrayList<Boolean> newTribePlains = new ArrayList<Boolean>();
	int tribesThreshold = 20;
}
