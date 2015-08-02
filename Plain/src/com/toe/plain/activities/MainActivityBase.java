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
import com.toe.plain.dialogs.EditDataDialog;
import com.toe.plain.dialogs.ExitDialog;
import com.toe.plain.dialogs.FavouriteOptionsDialog;
import com.toe.plain.dialogs.NewTribeDialog;
import com.toe.plain.dialogs.ReplyOptionsDialog;
import com.toe.plain.dialogs.StoryOptionsDialog;
import com.toe.plain.dialogs.TribeListDialog;
import com.toe.plain.listitems.ConversationsListItem;
import com.toe.plain.listitems.ListItem;
import com.toe.plain.listitems.TribeDirectoryListItem;
import com.viewpagerindicator.PageIndicator;

public class MainActivityBase extends SherlockFragmentActivity {

	// Variables
	ViewPager mPager;
	PageIndicator mIndicator;
	Intent i;
	SherlockFragmentActivity activity;
	SharedPreferences sp;
	StorageService storageService;

	// Adapters
	PlainFragmentAdapter mAdapter;
	ListItemAdapter adapter, repliesAdapter, favouritesAdapter;
	ConversationsListItemAdapter conversationsAdapter;
	TribeDirectoryListItemAdapter tribesAdapter;
	SwingBottomInAnimationAdapter swing;

	// Views
	XListView listView, repliesListView, conversationsListView, tribesListView,
			favouritesListView;
	ShimmerTextView tvNoListItem, tvNoReplyListItem, tvNoConversationListItem,
			tvNoTribeListItem, tvNoFavouriteListItem;
	EmojiconEditText emojiconEditText, emojiconEditTextReplies;
	String lastTribe, tribeName, tribeDescription, error, senderUsername,
			senderPassword, receiverUsername, tag, plain_id;
	ImageView emojiButton, emojiconButtonReplies;
	ImageView submitButton, submitButtonReplies;
	Button bSearchForTag, bShare, bFavourite;
	EditText etStory, etSearchForTag;
	View rootView, rootViewReplies;
	EmojiconsPopup popup, popupReplies;

	// ArrayLists
	ArrayList<ListItem> stories = new ArrayList<ListItem>();
	ArrayList<ListItem> favourites = new ArrayList<ListItem>();
	ArrayList<ListItem> replies = new ArrayList<ListItem>();
	ArrayList<ListItem> queryResults = new ArrayList<ListItem>();

	ArrayList<String> jsonDocArray, jsonIdArray, jsonTimesArray,
			appendJsonDocArray, appendJsonIdArray, appendJsonTimesArray,
			savedHashtags;
	ArrayList<String> storedTags = new ArrayList<String>();
	ArrayList<String> fetchedTagStories = new ArrayList<String>();

	ArrayList<Boolean> newTribePlains = new ArrayList<Boolean>();
	ArrayList<TribeDirectoryListItem> tribes = new ArrayList<TribeDirectoryListItem>();

	ArrayList<Integer> favouriteLikes = new ArrayList<Integer>();
	ArrayList<String> favouriteTags = new ArrayList<String>();
	ArrayList<String> favouriteStories = new ArrayList<String>();
	ArrayList<String> favouriteTimestamps = new ArrayList<String>();
	ArrayList<Boolean> favouriteAdmins = new ArrayList<Boolean>();

	ArrayList<String> queryResultStories = new ArrayList<String>();
	ArrayList<Integer> queryResultLikes = new ArrayList<Integer>();
	ArrayList<String> queryResultTags = new ArrayList<String>();
	ArrayList<Boolean> queryResultAdmins = new ArrayList<Boolean>();

	ArrayList<ConversationsListItem> conversationNames = new ArrayList<ConversationsListItem>();

	// Dialogs
	StoryOptionsDialog soDialog;
	ReplyOptionsDialog roDialog;
	FavouriteOptionsDialog fsoDialog;
	EditDataDialog edDialog;
	ExitDialog eDialog;
	NewTribeDialog ntDialog;
	TribeListDialog tlDialog;

	// From AppRate
	AlertDialog.Builder builder;
	AppRate rate;

	// Constants
	boolean storyIsClean = true, nameIsClean, descriptionIsClean;
	int mInterval = 20000;
	int tribesThreshold = 20;
	int offset = 100;

	// Handlers
	Handler mHandler;
	public static Handler handleChange;

}
