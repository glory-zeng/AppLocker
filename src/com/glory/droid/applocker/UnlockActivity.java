/**
 * 
 */
package com.glory.droid.applocker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author gy.zeng
 * 
 */
public class UnlockActivity extends Activity {

	private EditText pwdEt;

	private Button submitBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_enter_ui);
	}
}
