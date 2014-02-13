package com.pilotfish22.ricecake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.androidtown.iview.graphic.BitmapShape;
import org.androidtown.iview.graphic.GraphicModel;
import org.androidtown.iview.graphic.GraphicView;
import org.androidtown.iview.graphic.GraphicViewController;
import org.androidtown.iview.graphic.LabelShape;
import org.androidtown.iview.graphic.ShapeObject;
import org.androidtown.iview.graphic.ShapeObjectController;
import org.androidtown.iview.graphic.ShapeObjectControllerContext;
import org.androidtown.iview.graphic.ShapeSelection;
import org.androidtown.iview.graphic.ShapeSelectionFactory;
import org.androidtown.iview.graphic.SimplePoint;
import org.androidtown.iview.graphic.SimpleRectangle;

import com.pilotfish22.ricecake.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MakeInvitationActivity extends Activity {

	private static String TAG = "MakeInvitationAct";
	private int DIALOG_1 = 1;
	// GraphicView object ,GraphicModel object
	GraphicView view;
	GraphicModel model;

	Paint mDefaultPaint = new Paint();

	// 클릭한 오브젝트
	ShapeObject mSelectedObject;

	// 텍스트에 기본 속성
	String mSelectedText = ""; // 클릭한 글내용

	TextObjectController textController;
	BitmapObjectController bitmapController;

	boolean mIsToggle = false;
	String src="";

	// 버튼
	ImageButton btnText, btnMap, btnPhoto, btnStyle, btnColor, btnSize,
			btnEdit, btnDelete;
	ColorPicker colorPicker;
	LinearLayout makeText, fonts;
	RelativeLayout editText;
	SeekBar fontSize;
	EditText text;
	TextView font1, font2, font3, font4, font5;
	FrameLayout sublayout;
	int gone = View.GONE;
	int visibile = View.VISIBLE;

	private Uri mImageCaptureUri;
	
	private static final int REQ_CODE_PICK_IMAGE=1;
	private static final int REQ_CODE_MAP=2;
	private static final int CROP_FROM_CAMERA=3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_makeinvitation);
		sublayout = (FrameLayout) findViewById(R.id.sublayout);

		Paint layerPaint = new Paint();
		layerPaint.setAntiAlias(true);
		layerPaint.setColor(Color.BLUE);
		layerPaint.setStrokeWidth(3.0F);
		layerPaint.setStrokeCap(Cap.ROUND);
		layerPaint.setStrokeJoin(Join.ROUND);

		model = new GraphicModel();
		model.setLayerPaint(0, layerPaint);
		view = (GraphicView) findViewById(R.id.mainlayout); // xml저장된 view
		view.setGraphicModel(model);

		// 배경 이미지
		Intent intent = getIntent();
		int backgroudId = intent.getIntExtra(Constant.INTENT_EXTR_IMAGEID, 0);
		String backgroundType = intent.getStringExtra(Constant.INTENT_EXTR_IMAGE_TYPE);
		if(backgroundType.equals("res")){
			view.setBackgroundImage(getResources(), backgroudId);
		}else{
			
			Bitmap photo = null;
			Bitmap selectedImage = null;
			String path = getTempUri().getPath();
			try{
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
				bmpFactoryOptions.inJustDecodeBounds = true;
				photo = BitmapFactory.decodeFile(path, bmpFactoryOptions);
				
				int boundBoxInDp = getWindowManager()
						.getDefaultDisplay().getWidth();
				

				int w = bmpFactoryOptions.outWidth;
				int h = bmpFactoryOptions.outHeight;

				int xScale = w / boundBoxInDp;
				int yScale = h / boundBoxInDp;
				int scale = (xScale <= yScale) ? yScale : xScale;

				bmpFactoryOptions.inSampleSize=scale;
				bmpFactoryOptions.inJustDecodeBounds = false;
				selectedImage = BitmapFactory.decodeFile(path, bmpFactoryOptions);
							
			view.setBackgroundImage(selectedImage);
			}catch(Exception e){
				e.getStackTrace();
				e.getMessage();
			}
			
		}
		view.setDoubleBuffering(true);
		view.keepRatio(true);

		// 컨트롤러 세팅
		bitmapController = new BitmapObjectController();
		textController = new TextObjectController();

		// 핸들러
		HandleSelectionFactory selectionFactory = new HandleSelectionFactory();
		model.setSelectionFactory(selectionFactory);


		makeText = (LinearLayout) findViewById(R.id.maketext);
		fonts = (LinearLayout) findViewById(R.id.fonts);
		editText = (RelativeLayout) findViewById(R.id.edittext);
		fontSize = (SeekBar) findViewById(R.id.seekbar);

		colorPicker = (ColorPicker) findViewById(R.id.colorpicker);
		// 컬러 피커 선택시
		colorPicker.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (v instanceof ColorPicker) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						colorPicker.setDrawingCacheEnabled(true);
						Bitmap bm = colorPicker.getDrawingCache();
						int mSelectColor = bm.getPixel((int) event.getX(),
								(int) event.getY());
						// Log.i("hyun", String.valueOf(mSelectColor));
						LabelShape selectedLabel = (LabelShape) mSelectedObject;
						selectedLabel.setForeground(mSelectColor);
						model.removeObject(selectedLabel, false);
						model.addObject(selectedLabel, true);
						model.startRedraw();
						view.redrawAllView();
						return true;
					default:
						return false;
					}
				}
				return false;
			}
		});

		// 사진 가져오기
		btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if(!isSDCardMounted())
					return;
				
				try{
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");

				startActivityForResult(
						Intent.createChooser(intent, getResources().getText(R.string.select_gallery)),
						REQ_CODE_PICK_IMAGE);

				toggleAdd();
				}catch(ActivityNotFoundException e){
					Log.e(TAG,e.getMessage());
				}
			}
		});

		// 텍스트 추가
		btnText = (ImageButton) findViewById(R.id.btnText);
		btnText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 글꼴 기본 세팅
				mDefaultPaint.setTextSize(25);
				mDefaultPaint.setAntiAlias(true);

				LabelShape newLabel = new LabelShape(new SimplePoint(150.0F,
						35.0F), getResources().getString(
						R.string.text_add_contents));

				newLabel.setPaint(mDefaultPaint);
				newLabel.setForeground(Color.GRAY);
				model.setObjectController(newLabel, textController);
				model.addObject(newLabel, true);
				model.startRedraw();
				view.redrawAllView();

				mSelectedObject = newLabel; // 포커스 받은 객체 를 담는다.
				toggleAdd();
				addText();
			}
		});

		// 지도
		btnMap = (ImageButton) findViewById(R.id.btnMap);
		btnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(!isSDCardMounted())
					return;
				
				Intent intent = new Intent(getApplicationContext(),
						DialogMapActivity.class);

				startActivityForResult(intent, REQ_CODE_MAP);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

				toggleAdd();
			}
		});

		// 추가 버튼
		ImageButton btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleAdd();
			}
		});

		// 저장
		ImageButton btnSave = (ImageButton) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if(!isSDCardMounted())
					return;
				
				//model.setSelected(mSelectedObject, false, false);
				model.startRedraw();
				view.redrawAllView();
				view.setDrawingCacheEnabled(true);
				Bitmap bm = view.getDrawingCache();
				boolean isValue = saveBitmapToInvitation(bm);

				if (isValue) {
					//Log.i("log_save", src);
					Toast.makeText(getApplicationContext(),
							R.string.toast_msg_saved, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(),
							SavedIvitationActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.toast_msg_unsaved, Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		// 공유
		ImageButton btnShare = (ImageButton) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(!isSDCardMounted())
					return;
				
				model.startRedraw();
				view.redrawAllView();
				view.setDrawingCacheEnabled(true);
				Bitmap bm = view.getDrawingCache();
				boolean isValue = saveBitmapToInvitation(bm);
				if (isValue) {
					// Log.i("log_share", src);
					Uri uri = Uri.fromFile(new File(src));
					Intent intent = new Intent(
							android.content.Intent.ACTION_SEND);
					intent.setType("image/*");
					intent.putExtra(intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(intent, getResources()
							.getText(R.string.title_share)));
				}

			}
		});

		// 텍스트 입력 버튼
		btnEdit = (ImageButton) findViewById(R.id.btnEdit);

		// 텍스트 색 버튼
		btnColor = (ImageButton) findViewById(R.id.btnColor);

		// 텍스트 크기 버튼
		btnSize = (ImageButton) findViewById(R.id.btnSize);

		// 텍스트 스타일 버튼
		btnStyle = (ImageButton) findViewById(R.id.btnStyle);

		// seekbar
		fontSize = (SeekBar) findViewById(R.id.seekbar);
		fontSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (mSelectedObject instanceof LabelShape) {

					// 글씨 크기
					Paint p = new Paint();
					p.setTextSize(progress);
					p.setAntiAlias(true);
					LabelShape selectedLabel = (LabelShape) mSelectedObject;
					selectedLabel.setPaint(p);
					redrawLabelShpe(selectedLabel);

				}

			}
		});

		// 텍스트 스타일
		font1 = (TextView) findViewById(R.id.font1);
		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/HeummPea172.ttf");
		font1.setTypeface(face);

		font2 = (TextView) findViewById(R.id.font2);
		face = Typeface.createFromAsset(getAssets(),
				"fonts/AuctionGothic_Light.ttf");
		font2.setTypeface(face);

		font3 = (TextView) findViewById(R.id.font3);
		face = Typeface.createFromAsset(getAssets(), "fonts/NANUMPEN.TTF");
		font3.setTypeface(face);

		font4 = (TextView) findViewById(R.id.font4);
		face = Typeface.createFromAsset(getAssets(), "fonts/HYSUPM.TTF");
		font4.setTypeface(face);

		font5 = (TextView) findViewById(R.id.font5);
		face = Typeface.createFromAsset(getAssets(),
				"fonts/HeummPerfume162.ttf");
		font5.setTypeface(face);

		// 글쓴 내용 저장
		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		text = (EditText) findViewById(R.id.text);
		btnConfirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!"".equals(text.getText().toString())) {
					LabelShape selectedLabel = (LabelShape) mSelectedObject;

					model.removeObject(selectedLabel, true);
					selectedLabel.setLabel(text.getText().toString());
					model.addObject(selectedLabel, true);
					model.startRedraw();
					view.redrawAllView();

				}
			}
		});

		// 삭제 버튼
		btnDelete = (ImageButton) findViewById(R.id.btnDelete);

	}// end onCreate

	// 클릭 이벤트
	public void onClickText(View v) {
		switch (v.getId()) {
		case R.id.btnEdit:
			if (editText.getVisibility() == gone
					|| editText.getVisibility() == View.INVISIBLE) {
				editText.setVisibility(visibile);
				sublayout.setVisibility(visibile);
				colorPicker.setVisibility(gone);
				fontSize.setVisibility(gone);
				fonts.setVisibility(gone);
			} else {
				editText.setVisibility(gone);
				sublayout.setVisibility(gone);
			}
			break;

		case R.id.btnColor:
			if (colorPicker.getVisibility() == gone) {
				colorPicker.setVisibility(visibile);
				sublayout.setVisibility(visibile);
				editText.setVisibility(gone);
				fontSize.setVisibility(gone);
				fonts.setVisibility(gone);
			} else {
				colorPicker.setVisibility(gone);
				sublayout.setVisibility(gone);
			}
			break;
		case R.id.btnSize:
			if (fontSize.getVisibility() == gone) {
				fontSize.setVisibility(visibile);
				sublayout.setVisibility(visibile);
				colorPicker.setVisibility(gone);
				editText.setVisibility(gone);
				fonts.setVisibility(gone);
			} else {
				fontSize.setVisibility(gone);
				sublayout.setVisibility(gone);
			}

			break;
		case R.id.btnStyle:
			if (fonts.getVisibility() == gone) {
				fonts.setVisibility(visibile);
				sublayout.setVisibility(visibile);
				fontSize.setVisibility(gone);
				colorPicker.setVisibility(gone);
				editText.setVisibility(gone);
			} else {
				fonts.setVisibility(gone);
				sublayout.setVisibility(gone);
			}
			break;
		case R.id.btnDelete:
			if (mSelectedObject != null) {
				model.removeObject(mSelectedObject, true);
				model.startRedraw();
				view.redrawAllView();
				mSelectedObject = null;
				makeText.setVisibility(gone);
				btnDelete.setVisibility(gone);
				sublayout.setVisibility(gone);
			}
			break;
		case R.id.btnClose:
			fonts.setVisibility(gone);
			fontSize.setVisibility(gone);
			colorPicker.setVisibility(gone);
			editText.setVisibility(gone);
			makeText.setVisibility(gone);
			btnDelete.setVisibility(gone);
			break;

		}
	}

	// 객체 선택 여부 체크
	public boolean ismSelectedObject() {
		if (mSelectedObject != null) {
			return true;
		} else {
			return false;
		}
	}

	// 클릭 폰트 이벤트
	public void onClickFont(View v) {
		LabelShape selectedLabel = (LabelShape) mSelectedObject;
		switch (v.getId()) {
		case R.id.font1:
			selectedLabel.setFont(font1.getTypeface());
			break;
		case R.id.font2:
			selectedLabel.setFont(font2.getTypeface());
			break;
		case R.id.font3:
			selectedLabel.setFont(font3.getTypeface());
			break;
		case R.id.font4:
			selectedLabel.setFont(font4.getTypeface());
			break;
		case R.id.font5:
			selectedLabel.setFont(font5.getTypeface());
			break;
		}

		redrawLabelShpe(selectedLabel);
	}

	// 추가 숨기기/보이기
	public void toggleAdd() {
		if (mIsToggle) {
			btnText.setVisibility(View.GONE);
			btnPhoto.setVisibility(View.GONE);
			btnMap.setVisibility(View.GONE);
			mIsToggle = false;
		} else {
			btnText.setVisibility(View.VISIBLE);
			btnPhoto.setVisibility(View.VISIBLE);
			btnMap.setVisibility(View.VISIBLE);
			mIsToggle = true;
		}
	}

	// 텍스트 추가시
	public void addText() {
		if (makeText.getVisibility() == gone) {
			makeText.setVisibility(visibile);
			btnDelete.setVisibility(visibile);
		}

		if (editText.getVisibility() == gone) {
			editText.setVisibility(visibile);
			text.setText("");
		}

	}

	// 기존 오브젝트 지우고 다시 생성하기
	public void redrawLabelShpe(LabelShape selectedLabel) {
		model.removeObject(selectedLabel, true);
		model.addObject(selectedLabel, true);
		model.startRedraw();
		view.redrawAllView();
	}


	 // 인텐트 결과목록
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			// 사진 선택시
			case REQ_CODE_PICK_IMAGE:
				
				try {
					mImageCaptureUri = data.getData();
					
					Log.i("hyun", mImageCaptureUri.toString()); // logCat
					
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setType("image/*");
					List<ResolveInfo> list = getPackageManager()
							.queryIntentActivities(intent, 0);
					intent.setData(mImageCaptureUri);
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,  getTempUri());

					Intent i = new Intent(intent);
					ResolveInfo res = list.get(0);

					i.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));

					startActivityForResult(i, CROP_FROM_CAMERA);
					

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}

				break;

			// 지도 선택후 결과
			case REQ_CODE_MAP:
			//잘라낸 이미지를 보여줌
			case CROP_FROM_CAMERA:
				Bitmap photo = null;
				Bitmap selectedImage = null;
				String path = getTempUri().getPath();
				try{
					BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
					bmpFactoryOptions.inJustDecodeBounds = true;
					photo = BitmapFactory.decodeFile(path, bmpFactoryOptions);
					
					int boundBoxInDp = getWindowManager()
							.getDefaultDisplay().getWidth()*2/3;
					

					int w = bmpFactoryOptions.outWidth;
					int h = bmpFactoryOptions.outHeight;

					int xScale = w / boundBoxInDp;
					int yScale = h / boundBoxInDp;
					int scale = (xScale <= yScale) ? yScale : xScale;

					bmpFactoryOptions.inSampleSize=scale;
					bmpFactoryOptions.inJustDecodeBounds = false;
					selectedImage = BitmapFactory.decodeFile(path, bmpFactoryOptions);
					
					
//					selectedImage = util.setImage(photo,
//							boundBoxInDp);
					BitmapShape bitmapshape = new BitmapShape(selectedImage,
							new SimpleRectangle(100.0F, 100.0F, 100, 100));
					BitmapObjectController controller = new BitmapObjectController();
					model.addObject(bitmapshape, true);
					model.setObjectController(bitmapshape, controller);
					model.startRedraw();
					view.redrawAllView();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}finally{
					//selectedImage.recycle();
					selectedImage=null;
					//photo.recycle();
					photo=null;
					
				}
				break;
			default:
				break;
			}
		} else {
			Log.e(TAG, String.valueOf(resultCode));
		}

	}// end onActivityResult

	/*
	 * 사진 컨트롤러
	 */
	class BitmapObjectController extends ShapeObjectController {
		public static final String TAG = "BitmapObjectController";

		float oldX = 0.0f;
		float oldY = 0.0f;

		long oldTime = System.currentTimeMillis();
		long currentTime;

		float curX = 0.0f;
		float curY = 0.0f;

		float offsetX = 0.0f;
		float offsetY = 0.0f;

		boolean isSelected = false;

		public BitmapObjectController() {
			isSelected = false;
		}

		public boolean processMotionEvent(ShapeObject shape_object,
				MotionEvent event,
				ShapeObjectControllerContext obj_controller_context) {
			Log.d(TAG + "processMotionEvent()",
					"processMotionEvent() called : " + event.getX() + ", "
							+ event.getY());

			int action = event.getAction();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				model.removeObject(shape_object, false);
				model.addObject(shape_object, true);

				model.startRedraw();

				if (!isSelected) {
					model.setSelected(shape_object, true, false);
					isSelected = true;
				} else {
					isSelected = false;
				}

				shape_object.redraw();
				view.redrawAllView();

				oldX = event.getX();
				oldY = event.getY();

				// 사진
				if (shape_object instanceof BitmapShape) {

					if (makeText.getVisibility() == visibile) {
						makeText.setVisibility(gone);
					}

					if (sublayout.getVisibility() == visibile) {
						sublayout.setVisibility(gone);
					}
					// 삭제 버튼
					if (btnDelete.getVisibility() == gone) {
						btnDelete.setVisibility(visibile);
					}
				}
				// }
				mSelectedObject = shape_object;
				oldTime = currentTime;

				oldX = event.getX();
				oldY = event.getY();

				Log.d(TAG, "MotionEvent.ACTION_DOWN");

				break;
			case MotionEvent.ACTION_MOVE:
				curX = event.getX();
				curY = event.getY();

				offsetX = curX - oldX;
				offsetY = curY - oldY;

				model.startRedraw();
				shape_object.translate(offsetX, offsetY);
				shape_object.redraw();

				if (isSelected) {
					ShapeSelection selection_object = model
							.getSelection(shape_object);
					if (selection_object != null) {
						selection_object.translate(offsetX, offsetY);
						selection_object.redraw();
					}
				}

				view.redrawAllView();

				oldX = event.getX();
				oldY = event.getY();

				break;
			case MotionEvent.ACTION_UP:
				curX = event.getX();
				curY = event.getY();

				offsetX = curX - oldX;
				offsetY = curY - oldY;

				model.startRedraw();
				shape_object.translate(offsetX, offsetY);
				shape_object.redraw();

				if (!isSelected) {
					model.removeObject(shape_object, false);
					model.addObject(shape_object, true);

					model.setSelected(shape_object, false, false);
					view.removeController();
				} else {
					ShapeSelection selection_object = model
							.getSelection(shape_object);
					if (selection_object != null) {
						selection_object.translate(offsetX, offsetY);
						selection_object.redraw();
					}

					SelectionViewController viewController = new SelectionViewController();
					view.setController(viewController);
				}

				view.redrawAllView();

				Log.d(TAG, "MotionEvent.ACTION_UP");

				break;
			default:
				break;
			}

			return true;
		}

	}

	/*
	 * 글자 컨트롤러
	 */
	class TextObjectController extends ShapeObjectController {
		public static final String TAG = "TextObjectController";

		float oldX = 0.0f;
		float oldY = 0.0f;

		long oldTime = System.currentTimeMillis();
		long currentTime;

		float curX = 0.0f;
		float curY = 0.0f;

		float offsetX = 0.0f;
		float offsetY = 0.0f;

		public boolean processMotionEvent(ShapeObject shape_object,
				MotionEvent event,
				ShapeObjectControllerContext obj_controller_context) {
			Log.d(TAG + "processMotionEvent()",
					"processMotionEvent() called : " + event.getX() + ", "
							+ event.getY());

			int action = event.getAction();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				model.removeObject(shape_object, false);
				model.addObject(shape_object, true);

				model.startRedraw();

				shape_object.redraw();
				view.redrawAllView();

				oldX = event.getX();
				oldY = event.getY();

				mSelectedObject = shape_object;

				if (shape_object instanceof LabelShape) {
					LabelShape label = (LabelShape) shape_object;
					if (makeText.getVisibility() == gone) {
						makeText.setVisibility(visibile);
						btnDelete.setVisibility(visibile);
					}

					if (getResources().getString(R.string.text_add_contents)
							.equals(label.getLabel())) {
						text.setText("");
					} else {
						text.setText(label.getLabel());
					}

					fontSize.setProgress((int) label.getPaint().getTextSize());

				}

				oldTime = currentTime;

				oldX = event.getX();
				oldY = event.getY();

	
				Log.d(TAG, "MotionEvent.ACTION_DOWN");

				break;
			case MotionEvent.ACTION_MOVE:
				curX = event.getX();
				curY = event.getY();

				offsetX = curX - oldX;
				offsetY = curY - oldY;

				model.startRedraw();
				shape_object.translate(offsetX, offsetY);
				shape_object.redraw();

				view.redrawAllView();

				oldX = event.getX();
				oldY = event.getY();

				break;
			case MotionEvent.ACTION_UP:
				curX = event.getX();
				curY = event.getY();

				offsetX = curX - oldX;
				offsetY = curY - oldY;

				model.startRedraw();
				shape_object.translate(offsetX, offsetY);
				shape_object.redraw();

				model.removeObject(shape_object, false);
				model.addObject(shape_object, true);

				model.setSelected(shape_object, false, false);
				view.removeController();

				view.redrawAllView();

				Log.d(TAG, "MotionEvent.ACTION_UP");

				break;
			default:
				break;
			}

			return true;
		}

	}

	class SelectionViewController extends GraphicViewController {

		HandleObjectController handleController;

		protected void processMotionEvent(MotionEvent evt) {
			Log.d(TAG, "processMotionEvent() called.");

			dispatchToSelection(evt);
		}

		/** DispatchToSelection */
		protected boolean dispatchToSelection(MotionEvent evt) {
			Log.d(TAG, "dispatchToSelection() called.");

			ShapeSelection selectedObj = null;
			int action = evt.getAction();
			float xcoord = evt.getX();
			float ycoord = evt.getY();

			switch (action) {
			default:
				break;

			case MotionEvent.ACTION_DOWN:
				selectedObj = getGraphicModel().getSelection(
						new SimplePoint(xcoord, ycoord), getGraphicView());

				break;

			case MotionEvent.ACTION_UP:
				selectedObj = getGraphicModel().getSelection(
						new SimplePoint(xcoord, ycoord), getGraphicView());

				view.removeController();

				break;

			case MotionEvent.ACTION_MOVE:
				selectedObj = getGraphicModel().getSelection(
						new SimplePoint(xcoord, ycoord), getGraphicView());

				break;
			}

			Log.d(TAG, "selection found : " + selectedObj);

			return selectedObj != null
					&& dispatchToSelectionController(selectedObj.getObject(),
							evt);
		}

		boolean dispatchToSelectionController(ShapeObject shape_object,
				MotionEvent evt) {
			if (handleController == null) {
				handleController = new HandleObjectController();

				getGraphicModel().setObjectController(shape_object,
						handleController);

			}

			handleController.processMotionEvent(shape_object, evt,
					getGraphicView());

			Log.d(TAG, "event processed for HandleObjectController.");

			return true;
		}

	}

	/*
	 * 핸들러 컨트롤러
	 */
	class HandleObjectController extends ShapeObjectController {
		public static final String TAG = "HandleObjectController";

		float originX = 0.0f;
		float originY = 0.0f;

		float oldDistance = 0.0f;

		float oldX = 0.0f;
		float oldY = 0.0f;

		SimpleRectangle mbr = null;

		public boolean processMotionEvent(ShapeObject shape_object,
				MotionEvent event,
				ShapeObjectControllerContext obj_controller_context) {
			Log.d(TAG, "processMotionEvent() called : " + event.getX() + ", "
					+ event.getY());

			int action = event.getAction();

			switch (action) {
			case MotionEvent.ACTION_DOWN:

				oldX = event.getX();
				oldY = event.getY();

				originX = oldX;
				originY = oldY;

				break;
			case MotionEvent.ACTION_MOVE:
				float curX = event.getX();
				float curY = event.getY();

				float offsetX = curX - oldX;
				float offsetY = curY - oldY;

				model.startRedraw();

				ShapeSelection sel = model.getSelection(shape_object);
				if (sel != null) {
					model.invalidateRegion(sel);
					sel.translate(offsetX, offsetY);
					model.invalidateRegion(sel);
					sel.redraw();
				}

				model.invalidateRegion(shape_object);

				if (shape_object instanceof BitmapShape) {
					float scaleFactor = 0.0f;
					scaleFactor = 1 + offsetY / 350.0f;

					Log.d(TAG, "scale factor for current BitmapShape : "
							+ scaleFactor);
					shape_object.scale(scaleFactor, scaleFactor);
				}

				model.invalidateRegion(shape_object);
				shape_object.redraw();
				view.redrawAllView();

				mbr = shape_object.getMBR(view.getTransformer());
				Log.d(TAG, "MBR : " + mbr.x + ", " + mbr.y + ", " + mbr.width
						+ ", " + mbr.height);

				oldX = event.getX();
				oldY = event.getY();

				Log.d(TAG, "ACTION_MOVE " + offsetX + ", " + offsetY
						+ " in HandleObjectController...");

				break;
			case MotionEvent.ACTION_UP:

				ShapeSelection selection_object = model
						.getSelection(shape_object);

				model.removeObject(shape_object, false);
				model.addObject(shape_object, true);
				model.setObjectController(shape_object,
						new BitmapObjectController());

				model.startRedraw();
				if (selection_object != null) {

					model.removeObject(selection_object, true);
					model.invalidateRegion(selection_object);
				}

				model.invalidateRegion(shape_object);

				shape_object.redraw();
				view.redrawAllView();

				break;
			default:
				break;
			}

			return true;
		}

	}

	class HandleSelectionFactory implements ShapeSelectionFactory {

		public ShapeSelection makeSelection(ShapeObject shape_object) {
			SimpleRectangle mbr = shape_object.getMBR(view.getTransformer());
			SimpleRectangle newMBR = new SimpleRectangle(
					mbr.x + mbr.width - 39, mbr.y + mbr.height - 36, 49, 46);

			HandleShapeSelection selection_object = new HandleShapeSelection(
					shape_object, getResources(), R.drawable.ic_controller,
					newMBR);

			return selection_object;
		}

	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_1);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.toast_msg);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(DIALOG_1);
					}
				});

		return builder.create();
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
	
	
	
	// 사진 저장 경로 리턴
	private boolean saveBitmapToInvitation(Bitmap bitmap) {
		String status = Environment.getExternalStorageState();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHHmmss",
				Locale.getDefault());
		String currentTime = format
				.format(new Date(System.currentTimeMillis()));
		File path = new File(Constant.DEFAULT_PATH);

		FileOutputStream out = null;

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if (!path.isDirectory()) {
				path.mkdirs();
			}

			try {
				src = path.toString() + "/" + currentTime
						+ Constant.SAVE_INVITATION_FILE;

				out = new FileOutputStream(src);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

				return true;
			} catch (FileNotFoundException e) {
				Log.e(TAG, "IOException" + e.getMessage());
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					Log.e(TAG, "IOException" + e.getMessage());
				}
			}
		}
		return false;
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
