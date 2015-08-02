package com.toe.plain.dialogs;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.toe.plain.R;

public class ExitDialog extends Dialog {

	private TextView tvTitle;
	public Button bExitNo, bExitYes;
	private SherlockFragmentActivity activity;

	public ExitDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exit_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		bExitNo = (Button) findViewById(R.id.bExitNo);
		bExitYes = (Button) findViewById(R.id.bExitYes);
	}
}
