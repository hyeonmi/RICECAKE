package com.pilotfish22.ricecake;

import android.os.Environment;

public class Constant {
	public final static int REQ_CODE_PICK_IMAGE=1;
	public final static int CROP_FROM_CAMERA=2;
	
	public final static String INTENT_EXTR_IMAGEID = "IMAGEID";
	public final static String INTENT_EXTR_IMAGE_TYPE="IMAGETYPE";
	
	public final static String TEMP_PHOTO_FILE = "temp.png";
	public final static String SAVE_INVITATION_FILE = "invitation.png";
	
	public final static String DEFAULT_PATH = Environment.getExternalStorageDirectory().toString()+"/ricecake/invitation";

	public final static int DIALOG_BACKBUTTON_ID = 1000;	

}
