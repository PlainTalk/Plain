package com.toe.plain;

import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class StoryOptionsCustomDialog extends Dialog {

	public FlipImageView reply, replain, share, favourite, chat;

	public StoryOptionsCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.story_options_custom_dialog);

		reply = (FlipImageView) findViewById(R.id.ivReply);
		reply.setFlipped(true);

		replain = (FlipImageView) findViewById(R.id.ivReplain);
		replain.setFlipped(true);

		share = (FlipImageView) findViewById(R.id.ivShare);
		share.setFlipped(true);

		favourite = (FlipImageView) findViewById(R.id.ivFavourite);
		favourite.setFlipped(true);

		chat = (FlipImageView) findViewById(R.id.ivChat);
		chat.setFlipped(true);
	}

}
