/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ab.health.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.ab.health.R;



/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
@SuppressLint("NewApi") public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private Button tongbu;
    private TextView mConnectionState;
    private TextView mDataField,title;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic,mPedoMeterCharac,mTimeCharac;
    private UUID UUID_PEDO_METER_MEASUREMENT = UUID.fromString(SampleGattAttributes.PEDO_METER_MEASUREMENT);
    private UUID UUID_PEDO_METER_SERVER = UUID.fromString(SampleGattAttributes.PEDO_METER_SERVER);
    private UUID UUID_PEDO_METER_TIME = UUID.fromString(SampleGattAttributes.PEDO_METER_TIME);
    private String mData;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private byte [] time;
    private boolean blueServerReady = false;
    private ImageView infoOperatingIV;
    private Animation operatingAnim;
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {                
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	String mData2 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            	mData = mData2.substring(3, 5);
            	if(!mData.equals("FF")){
            		displayData(intent.getIntExtra("day",0),intent.getIntExtra("hour", 0));
            	}
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                             
                        	new Thread(new Runnable() {
                				
                				@Override
                				public void run() {
                					for(int i = 0; i<1; i++){
                					
	                					try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO 自动生成的 catch 块
											e.printStackTrace();
										}
	                					mBluetoothLeService.readCharacteristic(characteristic);                					
                					}
                				}
                				
                			}).start();
	                            
                        	
                        }
                 
                        return true;
                    }
                    return false;
                }
    };

    //tongbu shouhuan de shijian
    private void initTime(){
    	Calendar now = Calendar.getInstance();
    	int year = now.get(Calendar.YEAR) - 2000;
    	int month = now.get(Calendar.MONTH);
    	int day = now.get(Calendar.DAY_OF_MONTH)-1;
    	int hour = now.get(Calendar.HOUR_OF_DAY);
    	int minute = now.get(Calendar.MINUTE);
    	int second = now.get(Calendar.SECOND);    
    	byte [] temp = {(byte)year,(byte)month,(byte)day,(byte)hour,(byte)minute,(byte)second};
    	time = temp;   
    }
    
    
  

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_tongbu);
        mData = "DC";
     //   db2();
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        
        title = (TextView) findViewById(R.id.act_recordCal_title_tv);
        infoOperatingIV = (ImageView)findViewById(R.id.tongbubeijing);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);  
        LinearInterpolator lin = new LinearInterpolator();  
        operatingAnim.setInterpolator(lin);  
       
        
       
        
        mDataField = (TextView) findViewById(R.id.textView1);
        
        Button back = (Button) findViewById(R.id.act_recordCal_back_btn);
        
        back.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
        
        tongbu = (Button) findViewById(R.id.tongbu);
        tongbu.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if( blueServerReady == false ){
					if(mConnectionState.getText().toString().equals( "已断开")){
						Toast.makeText(getApplicationContext(), "手环已断开，请返回重新连接（如果一直断开，请关闭蓝牙重新打开）", Toast.LENGTH_SHORT).show();					
						return;
					}
					Toast.makeText(getApplicationContext(), "正在获取手环服务，请稍后（如果一直获取中，请关闭蓝牙重新打开）", Toast.LENGTH_SHORT).show();
					return;
				}
				if(mConnectionState.getText().toString().equals( "已断开")){
					Toast.makeText(getApplicationContext(), "手环已断开，请返回重新连接（如果一直断开，请关闭蓝牙重新打开）", Toast.LENGTH_SHORT).show();					
					return;
				}else{
					if (operatingAnim != null) {  
				            infoOperatingIV.startAnimation(operatingAnim);  
				    }  
					
					infoOperatingIV.setVisibility(View.VISIBLE);
					
					new Thread(new Runnable() {
        				
        				@Override
        				public void run() { 
        					for(int i = 0; i<5000; i++){
        					
            					try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
            				
            					if(mData.equals("FF")){
            						finish();
            						Intent intent = new Intent(DeviceControlActivity.this, BluetoothResult.class);
            						startActivity(intent);
            						break;
            					}
            					if(!mBluetoothLeService.readCharacteristic(mPedoMeterCharac)){
            						Toast.makeText(getApplicationContext(), "连接中断，请重新同步", Toast.LENGTH_SHORT).show();
            						return;
            					}
        					}
        				}
        			}).start();
				}
              }   
        });
        
        // Sets up UI references.

        
       
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        Intent gattServiceIntent = new Intent(this, com.ab.health.ble.BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(int day,int hour) {
       	
       title.setText("正同步" + String.valueOf(day) + "日"+ hour +"时数据");
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;       
        for (BluetoothGattService gattService : gattServices) {
          
        	Log.i("DisplayGattService", gattService.getUuid().toString());
            if(UUID_PEDO_METER_SERVER.equals(gattService.getUuid()))
            {
            	Log.i("yichang", "before");
            	mPedoMeterCharac = gattService.getCharacteristic(UUID_PEDO_METER_MEASUREMENT);
            	
            	mTimeCharac = gattService.getCharacteristic(UUID_PEDO_METER_TIME);
            	if(mPedoMeterCharac == null || mTimeCharac == null){
            		Log.i("yichang", "连接失败");
            		Toast.makeText(getApplicationContext(), "连接失败，请重新连接!", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	Log.i("displayPedoCharac", mPedoMeterCharac.toString());
            	initTime();
            	mTimeCharac.setValue(time);
            	mBluetoothLeService.writeCharacteristic(mTimeCharac);
            	blueServerReady = true;
            	Log.i("yichang", "获取到了");
            	return ;
            }
          
      //  	Toast.makeText(getApplicationContext(), "获取手环服务异常，请关闭蓝牙再打开一次!", Toast.LENGTH_SHORT).show();
            
        }
    }

    
    
  /* private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();	
          
            
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mPedoMeterCharac = charas.get(0);
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }
*/
    
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
