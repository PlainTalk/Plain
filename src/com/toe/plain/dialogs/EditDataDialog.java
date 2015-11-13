package com.toe.plain.dialogs;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.toe.plain.R;

public class EditDataDialog extends Dialog {

	public SherlockFragmentActivity activity;
	public Button bDone;
	public TextView tvTitle;
	public EditText etDataField;
	public String title, message, tag;
	public boolean numberInput = false;

	public EditDataDialog(SherlockFragmentActivity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_data_dialog);

		Typeface font = Typeface.createFromAsset(activity.getAssets(),
				activity.getString(R.string.list_font));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);
		tvTitle.setText(title);

		etDataField = (EditText) findViewById(R.id.etDataField);
		etDataField.setTypeface(font);
		etDataField.setText(tag);

		if (numberInput) {
			etDataField.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

		bDone = (Button) findViewById(R.id.bDone);
	}
}
