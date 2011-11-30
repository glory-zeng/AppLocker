/**
 * 
 */
package com.glory.droid.applocker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author gy.zeng
 * 
 */
public class PwdUI extends Activity implements OnClickListener {

	private static int[] digitBtnIds = { R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3, R.id.digit4, R.id.digit5, R.id.digit6,
			R.id.digit7, R.id.digit8, R.id.digit9 };
	private static long[] VIBRATOR_PATTERN = { 0, 50 }; // OFF/ON/OFF/ON...

	private Button submitBtn;
	private Button clearBtn;
	private View digitPad;
	private View passPad;
	private TextView userPwdTv;
	private TextView lcd;
	private Vibrator vibrator;

	private Animation shakeAnim;
	private Animation slideAnim;
	private Animation pushUpAnim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_enter_ui);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		lcd = (TextView) findViewById(R.id.lcd);

		clearBtn = (Button) findViewById(R.id.clear);
		clearBtn.setOnClickListener(this);

		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
		digitPad = findViewById(R.id.pad);
		passPad = findViewById(R.id.passpad);
		userPwdTv = (TextView) findViewById(R.id.userpass);
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		slideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
		slideAnim.setFillBefore(true);
		slideAnim.setFillAfter(true);
		pushUpAnim = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
		pushUpAnim.setFillAfter(true);
		pushUpAnim.setFillBefore(true);

		for (int id : digitBtnIds) {
			findViewById(id).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.clear) {
			userPwdTv.setText("");
		} else if (v.getId() == R.id.submit) {
			if (validatePwd()) {
				submit();
			} else {
				lcd.setText(getString(R.string.passwd_wrong));
				lcd.setTextColor(Color.parseColor("#B72C22"));
				userPwdTv.setText("");
				userPwdTv.startAnimation(shakeAnim);
			}
		} else if (v.getId() == R.id.clear) {
			clearPasswd();
		} else {
			// vibrate();
			String orig = userPwdTv.getText().toString();
			if (orig.length() > 7) {
				return;
			}
			String enterText = ((Button) v).getText().toString().trim();
			userPwdTv.setText(userPwdTv.getText().toString() + enterText);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AnimThread animThread = new AnimThread();
		passPad.postDelayed(animThread, 10L);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean validatePwd() {
		return false;
	}

	private void submit() {

	}

	private void clearPasswd() {
		Log.i("Lock", "clearPasswd");
		userPwdTv.setText("");
	}

	protected void vibrate() {
		vibrator.vibrate(VIBRATOR_PATTERN, -1);
	}

	class AnimThread implements Runnable {

		@Override
		public void run() {
			passPad.startAnimation(slideAnim);
			digitPad.startAnimation(pushUpAnim);
		}
	}
}
