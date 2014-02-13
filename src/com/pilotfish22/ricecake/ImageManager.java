package com.pilotfish22.ricecake;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ImageManager extends Activity {

	protected Uri mImageCaptureUri;

	String TAG = "ImageManager";
	public void selectedImage(){
		
		if(!isSDCardMounted())
			return;
		
		try{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");

		startActivityForResult(Intent.createChooser(intent,  getResources().getText(R.string.select_gallery)), Constant.REQ_CODE_PICK_IMAGE);

		}catch(ActivityNotFoundException e){
			Log.e(TAG,e.getMessage());
		}
	}
	

	

	// 마운트 여부
	private boolean isSDCardMounted(){	
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(getApplicationContext(), getResources().getText(R.string.toast_msg_sdcard),
					Toast.LENGTH_LONG).show();
			return false;
		}else{
			return true;
		}
	}
	

	//임시파일
	public Uri getTempUri() {
		File f = new File(Environment.getExternalStorageDirectory(),
				Constant.TEMP_PHOTO_FILE);
		try {
			f.createNewFile();
		} catch (IOException e) {
			Log.e("Log", e.getMessage());
		}

		return Uri.fromFile(f);
	}
}
