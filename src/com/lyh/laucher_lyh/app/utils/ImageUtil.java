package com.lyh.laucher_lyh.app.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class ImageUtil {
	
	public static Bitmap drawableToBitmap(Drawable drawable) throws Exception {       

        Bitmap bitmap = Bitmap.createBitmap(
        		drawable.getIntrinsicWidth(),
        		drawable.getIntrinsicHeight(),
        		drawable.getOpacity() != PixelFormat.OPAQUE ?
        				Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
	}

}
