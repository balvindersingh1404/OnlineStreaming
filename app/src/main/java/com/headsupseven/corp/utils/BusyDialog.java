package com.headsupseven.corp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.headsupseven.corp.R;


public class BusyDialog {

	private final Dialog dialog;
	private TextView title_text;

	
	public BusyDialog(Context c, boolean cancelable, String title) {
		dialog = new Dialog(c, android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// here we set layout of progress dialog
		dialog.setContentView(R.layout.custom_progress_dialog);
		dialog.setCancelable(cancelable);
		title_text = (TextView) dialog.findViewById(R.id.title_text);
		title_text.setText(title);
		ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progress);
		progress.setIndeterminate(true);
		progress.setIndeterminateDrawable(c.getResources().getDrawable(R.drawable.custom_progress));
	}

	public void show() {
		try {
			dialog.show();
		} catch (Exception e) {

		}
	}

	public void dismiss() {
		dialog.cancel();
	}

	public boolean isShowing() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}
}
