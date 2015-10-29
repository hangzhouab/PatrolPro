package com.ab.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.CalendarView.OnDateChangeListener;

public class QueryRecordActivity extends Activity {
	
	private  CalendarView calendar;
	private int year,month,day=0;
	private TextView query;
	private Button back_btn;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_patrol_query);		
		calendar = (CalendarView) findViewById(R.id.patrol_query_calendar);
		calendar.setBackgroundColor(0xffcccccc);
		calendar.setOnDateChangeListener(new OnDateChangeListener() {
			
			@Override
			public void onSelectedDayChange(CalendarView view, int y, int m,
					int d) {				
				year = y;
				month = m;
				day = d;
			}
		});
		Time t = new Time();
		t.setToNow();
		year = t.year;
		month = t.month;
		day = t.monthDay;
		
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		
		query = (TextView) findViewById(R.id.query_btn);
		query.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(QueryRecordActivity.this,PatrolQueryActivity.class);
				intent.putExtra("year", year);
				intent.putExtra("month", month);
				intent.putExtra("day", day);
				startActivity(intent);	
				
			}
		});
		
		
		
	}

}
