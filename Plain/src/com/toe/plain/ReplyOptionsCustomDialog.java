package com.toe.plain;

import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

public class ReplyOptionsCustomDialog extends Dialog {

	public FlipImageView share, favourite;

	public ReplyOptionsCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reply_options_custom_dialog);

		share = (FlipImageView) findViewById(R.id.ivShare);
		share.setFlipped(true);

		favourite = (FlipImageView) findViewById(R.id.ivFavourite);
		favourite.setFlipped(true);
	}

}
