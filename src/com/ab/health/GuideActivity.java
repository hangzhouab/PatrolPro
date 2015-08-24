package com.ab.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GuideActivity extends Activity {

	private ViewFlipper guiderViewPager;
	private LayoutInflater inflater;
	private GuideOnTouchListener guideOnTouch;
	private float pressX = 0;
	private Button btn_have_acount,btn_no_acount;
	private ButtonOnClick btnOnClick;
	private View guide1,guide2,guide3,guide4,guide5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		inflater = getLayoutInflater();
		guiderViewPager = (ViewFlipper) findViewById(R.id.guide_fragment);

		
		guide1 = inflater.inflate(R.layout.guide1_page, guiderViewPager);
		guide2 = inflater.inflate(R.layout.guide2_page, guiderViewPager);
		guide3 = inflater.inflate(R.layout.guide3_page, guiderViewPager);
		guide4 = inflater.inflate(R.layout.guide4_page, guiderViewPager);
		guide5 = inflater.inflate(R.layout.guide5_page, guiderViewPager);	
		
		Log.i("id", String.valueOf(guide1.getId()));
		btn_have_acount = (Button) findViewById(R.id.guide_have_account);
		btn_no_acount =(Button) findViewById(R.id.guide_no_account);		
		btnOnClick = new ButtonOnClick();
		btn_have_acount.setOnClickListener(btnOnClick);
		btn_no_acount.setOnClickListener(btnOnClick);
		
		guideOnTouch = new GuideOnTouchListener();
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.parent);
		rl.setOnTouchListener(guideOnTouch);

	}
	
	
	class ButtonOnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.guide_have_account:
				Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
				startActivity(intent);
				break;
			case R.id.guide_no_account:
				Intent intent2 = new Intent(GuideActivity.this, FirstUseActivity.class);
				startActivity(intent2);
				break;
			default:
				break;
			}
			
		}
		
	}

	class GuideOnTouchListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {

			switch (arg1.getAction()) {
			case MotionEvent.ACTION_DOWN:
				pressX = arg1.getRawX();				
				break;
			case MotionEvent.ACTION_UP:
				float upX = arg1.getRawX();				
				if (upX < pressX) {
					guiderViewPager.setInAnimation(getApplicationContext(),
							R.anim.push_right_in);
					guiderViewPager.setOutAnimation(getApplicationContext(),
							R.anim.push_left_out);
					guiderViewPager.showNext();
				} else {				
					guiderViewPager.setInAnimation(getApplicationContext(),
							R.anim.push_left_in);
					guiderViewPager.setOutAnimation(getApplicationContext(),
							R.anim.push_right_out);
					guiderViewPager.showPrevious();
					
				}
				break;
			default:
				break;
			}
			return true;
		}

	}

}
