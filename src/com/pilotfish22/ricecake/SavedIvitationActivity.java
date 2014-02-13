package com.pilotfish22.ricecake;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class SavedIvitationActivity extends Activity {
	// Bitmap[] mBitmap;
	private StringImageAdapter adapter;
	private ArrayList<String> mInvitationList = new ArrayList<String>();
	private String mInvitationPath;
	private int mPosition;
	private Gallery gallery;
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savedinvitation);

		gallery = (Gallery) findViewById(R.id.gallery);
		image = (ImageView) findViewById(R.id.image);

		init();

		// 배경 선택으로 이동
		ImageButton btnBackgrounList = (ImageButton) findViewById(R.id.btnBackgrounList);
		btnBackgrounList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SelectBackgroundActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		// 삭제
		ImageButton btnDelete = (ImageButton) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mInvitationPath != null) {
					if (deleteInvitation(mInvitationPath)) {
						Toast.makeText(getApplicationContext(),
								R.string.deleted, Toast.LENGTH_LONG).show();
						image.setImageBitmap(null);
						adapter.notifyDataSetChanged();
						gallery.invalidate();

					}
				}

			}
		});

		// 공유
		ImageButton btnShare = (ImageButton) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mInvitationPath != null) {
					Uri uri = Uri.fromFile(new File(mInvitationPath));
					Intent intent = new Intent(
							android.content.Intent.ACTION_SEND);
					intent.putExtra(intent.EXTRA_STREAM, uri);
					intent.setType("image/jpg");
					startActivity(Intent.createChooser(intent, getResources()
							.getText(R.string.title_share)));
				}

			}
		});

	}

	// 초기화
	private void init() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Constant.DEFAULT_PATH;
			File file = new File(path);
			if (file.listFiles() != null) {
				for (File f : file.listFiles()) {
					mInvitationList.add(new String(path + "/" + f.getName()));
				}
			}

		}

		if (mInvitationList != null) {
			adapter = new StringImageAdapter(this, mInvitationList);
			gallery.setAdapter(adapter);

			gallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mPosition = position;
					mInvitationPath = mInvitationList.get(position).toString();
					image.setImageBitmap(BitmapFactory
							.decodeFile(mInvitationPath));
				}
			});
		}
	}// end init


	// 초대장 삭제
	private boolean deleteInvitation(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(path);
			if (file.isFile()) {
				file.delete();
				mInvitationList.remove(mPosition);
				return true;
			}
		}
		return false;
	}

}// end class
