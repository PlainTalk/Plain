package com.toe.plain.activities;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.toe.plain.R;
import com.toe.plain.adapters.ChatListItemAdapter;
import com.toe.plain.chat.XmppConnection;
import com.toe.plain.classes.XListView;
import com.toe.plain.listitems.ChatListItem;

public class Chat extends SherlockActivity {

	String receiverUsername;
	final int DO_UPDATE_TEXT = 0;
	final int DO_THAT = 1;
	public static ArrayList<ChatListItem> conversationNames = new ArrayList<ChatListItem>();
	ArrayList<ChatListItem> the_messages = new ArrayList<ChatListItem>();
	public boolean service_running = false;
	public XListView list;
	public ChatListItemAdapter adapter;
	public static Handler handleChange, handle_chat_state;
	EmojiconEditText emojiconEditText;
	View rootView;
	ImageView emojiButton;
	ImageView submitButton;
	EmojiconsPopup popup;
	boolean storyIsClean = true;

	String plain_id, tag;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chats);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getData();
		setup();
		setUpEmojiKeyboard();
		initializeXmpp();

	}

	private void getData() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		receiverUsername = b.getString("receiverUsername");
		tag = b.getString("tag");
	}

	public void setup() {
		list = (XListView) findViewById(R.id.list);
		list.setPullRefreshEnable(false);
		list.setPullLoadEnable(false);
		emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
		emojiconEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				// XmppConnection.sendMessage("chat_state_composing",
				// receiverScreenName, "", plain_id);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		for (Map.Entry<String, String> entry : XmppConnection.message_map
				.entrySet()) {
			if (entry.getKey().contains(tag)) {
				if (entry.getKey().contains("current")) {
					the_messages.add(new ChatListItem(entry.getValue(), 0, "",
							0));
				} else {
					the_messages.add(new ChatListItem(entry.getValue(), 0, "", 1));
				}

				Log.d("the values logged", entry.getValue());
			}

		}

		adapter = new ChatListItemAdapter(getApplicationContext(),
				R.layout.chat_list_item, the_messages);
		list.setAdapter(adapter);

	}

	private void setUpEmojiKeyboard() {
		// TODO Auto-generated method stub
		rootView = findViewById(R.id.root_view);
		emojiButton = (ImageView) findViewById(R.id.emoji_btn);
		submitButton = (ImageView) findViewById(R.id.submit_btn);
		popup = new EmojiconsPopup(rootView, this);

		popup.setSizeForSoftKeyboard();
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				emojiconEditText.append(emojicon.getEmoji());
			}
		});
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});
		popup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley_icon);
			}
		});
		popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

			@Override
			public void onKeyboardOpen(int keyBoardHeight) {

			}

			@Override
			public void onKeyboardClose() {
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

			@Override
			public void onEmojiconClicked(Emojicon emojicon) {
				emojiconEditText.append(emojicon.getEmoji());
			}
		});
		popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

			@Override
			public void onEmojiconBackspaceClicked(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0,
						0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				emojiconEditText.dispatchKeyEvent(event);
			}
		});
		emojiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!popup.isShowing()) {

					if (popup.isKeyBoardOpen()) {
						popup.showAtBottom();
						changeEmojiKeyboardIcon(emojiButton,
								R.drawable.keyboard_icon);
					}

					else {
						emojiconEditText.setFocusableInTouchMode(true);
						emojiconEditText.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(emojiconEditText,
								InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiButton,
								R.drawable.keyboard_icon);
					}
				}

				else {
					popup.dismiss();
				}
			}
		});

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String story = emojiconEditText.getText().toString().trim();
				storyIsClean = true;
				storyIsClean = filterWords(story);

				if (storyIsClean) {
					if (story.length() > 1) {
						if (story.length() < 1000) {
							// if (senderUsername != null) {
							XmppConnection.sendMessage("initiate_chat",
									receiverUsername, "", tag);
							// use receiverid = screenName+ UserName

							// Relay id and phone number to attacker.
							XmppConnection.sendMessage(
									XmppConnection.TYPE_NORMAL,
									receiverUsername, story, tag);
							emojiconEditText.setText("");
							// }
						} else {
							emojiconEditText
									.setError("Try shortening that a bit...");
						}
					} else {
						emojiconEditText.setError("Please say something...");
					}
				} else {
					emojiconEditText
							.setError(getString(R.string.et_not_clean_error));
				}

			}
		});
	}

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged,
			int drawableResourceId) {
		iconToBeChanged.setImageResource(drawableResourceId);
	}

	private void initializeXmpp() {
		// TODO Auto-generated method stub
		XmppConnection.initMessages(getApplicationContext(), handleChange,
				adapter, conversationNames, the_messages, tag
						+ receiverUsername);

		handle_chat_state = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				getSupportActionBar().setTitle(receiverUsername);
			}

		};
	}

	private boolean filterWords(String story) {
		// TODO Auto-generated method stub
		String bannedWords[] = getResources().getStringArray(
				R.array.banned_words);

		for (int i = 0; i < bannedWords.length; i++) {
			if (story.contains(bannedWords[i])) {
				storyIsClean = false;
			}
		}
		return storyIsClean;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
