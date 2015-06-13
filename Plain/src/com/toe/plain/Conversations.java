package com.toe.plain;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.toe.plain.chat.XmppConnection;

public class Conversations extends Activity {
	String senderUsername, senderPassword, senderScreenName, receiverUserName,
			receiverScreenName;

	ImageView send;

	final int DO_UPDATE_TEXT = 0;
	final int DO_THAT = 1;
	public static ArrayList<ConversationsListItem> conversationNames = new ArrayList<ConversationsListItem>();
	ArrayList<ConversationsListItem> the_messages = new ArrayList<ConversationsListItem>();
	public boolean service_running = false;
	String thread_name = "admin";
	XListView list;
	ConversationsListItemAdapter adapter;
	Handler handleChange;
	EmojiconEditText emojiconEditText;
	View rootView;
	ImageView emojiButton;
	ImageView submitButton;
	EmojiconsPopup popup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversations);

		setUp();
		setUpEmojiKeyboard();
		XmppConnection.initMessages(getApplicationContext(), handleChange,
				adapter, conversationNames, the_messages, receiverScreenName);
	}

	private void setUp() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		// receiverUserName = b.getString("receiverUsername");
		receiverScreenName = b.getString("receiverScreenName");

		Toast.makeText(this, receiverScreenName + ".." + receiverScreenName,
				Toast.LENGTH_LONG).show();

		list = (XListView) findViewById(R.id.lvListItems);
		list.setPullRefreshEnable(false);
		list.setPullLoadEnable(false);

		for (Map.Entry<String, String> entry : XmppConnection.message_map
				.entrySet()) {

			if (entry.getKey().contains(receiverScreenName)) {

				the_messages.add(new ConversationsListItem(entry.getValue()));
				Log.d("the values logged", entry.getValue());
			}

		}

		adapter = new ConversationsListItemAdapter(getApplicationContext(),
				R.layout.conversations_list_item, the_messages);
		list.setAdapter(adapter);
	}

	private void setUpEmojiKeyboard() {
		// TODO Auto-generated method stub
		emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
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
				changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
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
								R.drawable.ic_action_keyboard);
					}

					else {
						emojiconEditText.setFocusableInTouchMode(true);
						emojiconEditText.requestFocus();
						popup.showAtBottomPending();
						final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.showSoftInput(emojiconEditText,
								InputMethodManager.SHOW_IMPLICIT);
						changeEmojiKeyboardIcon(emojiButton,
								R.drawable.ic_action_keyboard);
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
				// if (senderUsername != null) {
				String text = emojiconEditText.getText().toString();
				XmppConnection.sendMessage("initiate_chat", receiverScreenName,
						"hi there");
				// use receiverid = screenName+ UserName
				XmppConnection.sendMessage(XmppConnection.TYPE_NORMAL,
						receiverScreenName, text);
				emojiconEditText.setText("");
				// }
			}
		});
	}

	private void changeEmojiKeyboardIcon(ImageView iconToBeChanged,
			int drawableResourceId) {
		iconToBeChanged.setImageResource(drawableResourceId);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// handleChange = Plain.handleChange;
		// finish();
		// the_messages.clear();
		finish();
	}
}
