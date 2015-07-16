package com.toe.plain.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.toe.plain.R;
import com.toe.plain.classes.FlipImageView;

public class FavouriteOptionsCustomDialog extends Dialog {

	public FlipImageView share, delete;

	public FavouriteOptionsCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.favourite_options_custom_dialog);

		share = (FlipImageView) findViewById(R.id.ivShare);
		share.setFlipped(true);

		delete = (FlipImageView) findViewById(R.id.ivDelete);
		delete.setFlipped(true);
	}

}
