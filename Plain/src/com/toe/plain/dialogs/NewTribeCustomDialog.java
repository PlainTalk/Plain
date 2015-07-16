package com.toe.plain.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.toe.plain.R;

public class NewTribeCustomDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public Button bDone;
	public TextView tvTitle;
	public EditText etName, etDescription;

	public NewTribeCustomDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_tribe_custom_dialog);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		etName = (EditText) findViewById(R.id.etName);
		etDescription = (EditText) findViewById(R.id.etDescription);

		bDone = (Button) findViewById(R.id.bDone);
	}
}
