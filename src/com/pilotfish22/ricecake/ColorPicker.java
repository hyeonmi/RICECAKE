package com.pilotfish22.ricecake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ColorPicker extends View {
	int mSelectColor=0;
	public ColorPicker(Context context) {
		super(context);
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w = canvas.getWidth();
		int[] colors = { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN,
				Color.BLUE, Color.MAGENTA, Color.WHITE, Color.BLACK };
		float[] pos = { 0.0f, 0.12f, 0.24f, 0.36f, 0.48f, 0.60f, 0.72f, 1.0f };

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		paint.setShader(new LinearGradient(0, 0, w, 0, colors, pos,
				TileMode.CLAMP));
		canvas.drawRect(0, 0, w, 80, paint);
	}


}
