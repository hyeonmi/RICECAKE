package com.pilotfish22.ricecake;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class StringImageAdapter extends ArrayAdapter<String> {
	ArrayList<String> list;

	public StringImageAdapter(Context context, ArrayList<String> list) {
		super(context, 0, list);
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(new Gallery.LayoutParams(100,
					140));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(1, 0, 1, 0);
		} else {
			imageView = (ImageView) convertView;
		}
		BitmapFactory.Options bo = new BitmapFactory.Options();
		bo.inSampleSize=4;
		Bitmap bmp = BitmapFactory.decodeFile(list.get(position),bo);
		imageView.setImageBitmap(bmp);
		
		
		return imageView;

	}
}
