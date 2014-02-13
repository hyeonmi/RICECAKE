package com.pilotfish22.ricecake;

import java.util.List;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.impl.AdError;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class SelectBackgroundActivity extends ImageManager {
	private String TAG = "SelectBacground";
	private AdView adView = null;

	/* Back key 두번눌러 종료 코드 시작 */
	private static final int MSG_TIMER_EXPIRED = 1;
	private static final int BACKEY_TIMEOUT = 2000;
	private boolean mIsBackKeyPressed = false;
	private long mCurrentTimeInMillis = 0;


	/** Called when the activity is first created. */
	IntegerImageAdapter adapter;
	Integer[] mThumbIds = { 
			R.drawable.bg_10, R.drawable.bg_11, R.drawable.bg_12,
			R.drawable.bg_13, R.drawable.bg_05, R.drawable.bg_01, 
			R.drawable.bg_02, R.drawable.bg_03, R.drawable.bg_04, 
			R.drawable.bg_06, R.drawable.bg_07, R.drawable.bg_08, R.drawable.bg_09
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectbackground);

		initAdam();

		GridView gridView = (GridView) findViewById(R.id.gridview_background);
		gridView.setColumnWidth(getWindowManager().getDefaultDisplay()
				.getWidth() / 3);
		ImageButton btnInvitationList = (ImageButton) findViewById(R.id.btnInvitationList);
		ImageButton btnInvitationAdd = (ImageButton) findViewById(R.id.btnInvitationAdd);

		adapter = new IntegerImageAdapter(this, mThumbIds, getWindowManager().getDefaultDisplay().getWidth() / 2);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(getApplicationContext(),
						MakeInvitationActivity.class);

				intent.putExtra(Constant.INTENT_EXTR_IMAGE_TYPE, "res");
				intent.putExtra(Constant.INTENT_EXTR_IMAGEID, mThumbIds[position]);
				Log.i(TAG, String.valueOf(mThumbIds[position]));
				startActivity(intent);

			}
		});

		//SavedIvitationActivity 이동 
		btnInvitationList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SavedIvitationActivity.class);
				startActivity(intent);
			}
		});
		
		//사용자 이미지 추가
		btnInvitationAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedImage();
			}
		});
	}// end create
	

	// 인텐트 결과목록
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			// 사진 선택시
			case Constant.REQ_CODE_PICK_IMAGE:
				
				try {
					mImageCaptureUri = data.getData();
					
					//Log.i("hyun", mImageCaptureUri.toString()); // logCat
					
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setType("image/*");
					//TODO
					intent.putExtra("aspectX", 3);
					intent.putExtra("aspectY", 5);
					//intent.putExtra("scale",true);
					
					List<ResolveInfo> list = getPackageManager()
							.queryIntentActivities(intent, 0);
					intent.setData(mImageCaptureUri);
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,  getTempUri());

					Intent i = new Intent(intent);
					ResolveInfo res = list.get(0);

					i.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));

					startActivityForResult(i, Constant.CROP_FROM_CAMERA);
					

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}

				break;


			//잘라낸 이미지를 보여줌
			case Constant.CROP_FROM_CAMERA:					

				//TODO
				Intent intent = new Intent(getApplicationContext(),
						MakeInvitationActivity.class);
				intent.putExtra(Constant.INTENT_EXTR_IMAGE_TYPE, "url");
				intent.putExtra(Constant.INTENT_EXTR_IMAGEID, getTempUri().getPath());
				//Log.i(TAG, String.valueOf(mThumbIds[position]));
				startActivity(intent);
				
				break;
			default:
				break;
			}
		} else {
			Log.e(TAG, String.valueOf(resultCode));
		}

	}// end onActivityResult


	// 백버튼
	@Override
	public void onBackPressed() {
		if (mIsBackKeyPressed == false) {
			mIsBackKeyPressed = true;

			mCurrentTimeInMillis = System.currentTimeMillis();

			Toast.makeText(this, R.string.toast_msg_quit, Toast.LENGTH_SHORT)
					.show();
			startTimer();
		} else {
			mIsBackKeyPressed = false;

			if (System.currentTimeMillis() <= (mCurrentTimeInMillis + (BACKEY_TIMEOUT))) {
				finish();
			}
		}
	}

	private void startTimer() {
		mTimerHander.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED, BACKEY_TIMEOUT);
	}

	private Handler mTimerHander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TIMER_EXPIRED:
				mIsBackKeyPressed = false;
				break;
			}
		}
	};

	//onDestroy
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adView != null) {
			adView.destroy();
			adView = null;
		}
	}

	// Ad@m sdk 초기화 시작
	private void initAdam() {
		adView = (AdView) findViewById(R.id.adview);
		adView.setOnAdFailedListener(new OnAdFailedListener() {
			@Override
			public void OnAdFailed(AdError error, String message) {
				Log.w(TAG, message);
			}
		});
		adView.setAdCache(false);
		adView.setVisibility(View.VISIBLE);
	}

}// end class