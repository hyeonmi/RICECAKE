package com.pilotfish22.ricecake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class IntegerImageAdapter extends ArrayAdapter<Integer> {
	private Integer[] list;
	private ImageView imageView;
	int imageSize = 0; 
	private Context context;
	public IntegerImageAdapter(Context context, Integer[] list, int imageSize) {
		super(context, 0, list);
		this.list = list;
		this.context=context;
		this.imageSize =imageSize; 
				//context.getWindowManager().getDefaultDisplay().getWidth() / 2;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(new GridView.LayoutParams(imageSize,
					imageSize));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		} else {
			imageView = (ImageView) convertView;
		}

		BitmapFactory.Options bo = new BitmapFactory.Options();
		bo.inSampleSize = 2;
		Bitmap bmp = BitmapFactory.decodeResource( context.getResources(),
				list[position], bo);
		imageView.setImageBitmap(bmp);
		// imageView.setBackgroundResource(mBackground);

		return imageView;

	}
}
