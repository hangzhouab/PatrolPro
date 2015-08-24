package com.ab.health;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class CoreBriefActivity extends Activity {
	private ViewFlipper guiderViewPager;
	private LayoutInflater inflater;
	private GuideOnTouchListener guideOnTouch;
	private float pressX = 0;
	private Button btn_ok;
	private ButtonOnClick btnOnClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		inflater = getLayoutInflater();
		guiderViewPager = (ViewFlipper) findViewById(R.id.guide_fragment);

		
		View guide1 = inflater.inflate(R.layout.guide1_page, guiderViewPager);
		View guide2 = inflater.inflate(R.layout.guide2_page, guiderViewPager);
		View guide3 = inflater.inflate(R.layout.guide3_page, guiderViewPager);
		View guide4 = inflater.inflate(R.layout.guide4_page, guiderViewPager);
		View guide5 = inflater.inflate(R.layout.guide5_page_final, guiderViewPager);		
		btn_ok = (Button) findViewById(R.id.guide_exit);
		btnOnClick = new ButtonOnClick();
		btn_ok.setOnClickListener(btnOnClick);
		
		
		guideOnTouch = new GuideOnTouchListener();
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.parent);
		rl.setOnTouchListener(guideOnTouch);

	}
	
	
	class ButtonOnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.guide_exit:
				finish();
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
