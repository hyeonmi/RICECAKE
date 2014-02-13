package com.pilotfish22.ricecake;

import org.androidtown.iview.graphic.ShapeObject;
import org.androidtown.iview.graphic.ShapeSelection;
import org.androidtown.iview.graphic.SimpleRectangle;
import org.androidtown.iview.graphic.Transform2D;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class HandleShapeSelection extends ShapeSelection {
	protected final SimpleRectangle drawrect;
	private Bitmap bitmap;

	public HandleShapeSelection(ShapeObject shape_object) {
		super(shape_object);

		drawrect = new SimpleRectangle();
	}

	public HandleShapeSelection(ShapeObject shape_object, Bitmap image,
			SimpleRectangle simple_rect) {
		super(shape_object);

		drawrect = new SimpleRectangle();
		drawrect.x = simple_rect.x;
		drawrect.y = simple_rect.y;
		drawrect.width = simple_rect.width;
		drawrect.height = simple_rect.height;
		bitmap = image;
	}

	public HandleShapeSelection(ShapeObject shape_object, Resources res,
			int resId, SimpleRectangle simple_rect) {
		super(shape_object);

		drawrect = new SimpleRectangle();
		drawrect.x = simple_rect.x;
		drawrect.y = simple_rect.y;
		drawrect.width = simple_rect.width;
		drawrect.height = simple_rect.height;

		bitmap = createImage(res, resId);
	}

	/** Apply transformation */
	public void fireTransform(Transform2D transformer) {
		transformer.apply(drawrect);
		if (drawrect.width < 0)
			drawrect.width = 1;
		if (drawrect.height < 0)
			drawrect.height = 1;
	}

	/** Create image object from the input name */
	public Bitmap createImage(Resources res, int resId) {
		if (resId > 0) {
			return BitmapFactory.decodeResource(res, resId);
		} else {
			return null;
		}
	}

	/** Draw this object */
	public void draw(Canvas canvas, Transform2D transformer) {
		SimpleRectangle simple_rect = new SimpleRectangle(drawrect);
		if (transformer != null && !transformer.isIdentity()) {
			transformer.apply(simple_rect);
		} else {
			simple_rect.floor();
		}

		if (bitmap != null) {
			Rect dstRect = new Rect();
			dstRect.left = (int) simple_rect.x;
			dstRect.top = (int) simple_rect.y;
			dstRect.right = (int) (simple_rect.x + simple_rect.width);
			dstRect.bottom = (int) (simple_rect.y + simple_rect.height);

			canvas.drawBitmap(bitmap, null, dstRect, null);
		} else {
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Style.FILL);

			canvas.drawRect(simple_rect.x, simple_rect.y, simple_rect.x
					+ simple_rect.width, simple_rect.y + simple_rect.height,
					paint);
		}
	}

	/** Get the target Image */
	public Bitmap getImage() {
		return bitmap;
	}

	@Override
	public SimpleRectangle getMBR(Transform2D transformer) {
		SimpleRectangle simple_rect = new SimpleRectangle(drawrect);
		if (transformer != null && !transformer.isIdentity()) {
			transformer.apply(simple_rect);
		}

		return simple_rect;
	}

}
