package com.lyh.laucher_lyh.app.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;

public class IconViewEffect {



	private float mRoundedRate = 5.5f;

	private boolean mAutoFit = true;


	private static IconViewEffect  mInstance ;
	
	public static IconViewEffect getInstance(){
		if(mInstance == null){
			synchronized (IconViewEffect.class) {
				if(mInstance == null){
					mInstance = new IconViewEffect();
				}
			}
		}
		return mInstance;
	}


	public Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}


	public Bitmap addBorderToBitmap(Bitmap src) {
		return addBorderToImage(src,src.getWidth(),src.getHeight());
	}
	
	public Bitmap addBorderToDrawable(Drawable drawable) {
		Bitmap src = drawableToBitmap(drawable);
		return addBorderToImage(src,src.getWidth(),src.getHeight());
	}
	
	public Bitmap addBorderToBitmap(Bitmap src,int width,int height) {
		return addBorderToImage(src,width,height);
	}
	
	public Bitmap addBorderToDrawable(Drawable drawable,int width,int height) {
		Bitmap src = drawableToBitmap(drawable);
		return addBorderToImage(src,width,height);
	}
	
	public Bitmap addClickedEffectToBitmap(Bitmap src){
		return addClickedEffect(src);
	}
	
	public Bitmap addClickedEffectToDrawable(Drawable drawable){
		return addClickedEffect(drawableToBitmap(drawable));
	}
	
	
	private Bitmap addClickedEffect(Bitmap src) {

		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(),
				src.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bmOut);

		Rect srcSrcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
		Rect srcDstRect = new Rect(0, 0, src.getWidth(), src.getHeight());

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		
		paint.setAlpha(0xAA);
		
		canvas.drawBitmap(src, srcSrcRect, srcDstRect, paint);
		return bmOut;
	}

	
	

	private Bitmap addBorderToImage(Bitmap src,int width,int height) {

		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		RectF outerRect = new RectF(0, 0, width, height);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		
		int[] colors = getBroderColor(src);
		
		LinearGradient mLinearGradientClamp = new LinearGradient(0, 0, 0,
				height, colors, null, TileMode.CLAMP);

		paint.setShader(mLinearGradientClamp);

		canvas.drawRoundRect(outerRect, width / mRoundedRate, height
				/ mRoundedRate, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		int sw = src.getWidth();
		int sh = src.getHeight();

		Rect srcRect = new Rect(0, 0, sw, sh);
		Rect dstRect = getDstRect(sw, sh, width, height);

		canvas.drawBitmap(src, srcRect, dstRect, paint);

		return output;
	}
private Rect getDstRect(int sw, int sh, int dw, int dh) { 
		
		float startx = 0;
		float starty = 0; 
		float endx = dw;
		float endy = dh;
		
		//绘制于框内的长和�?
		float sw1 = sw, 
			  sh1 = sh;
		
		if(sw<=dw && sh <= dh){
			if(!mAutoFit){
				sw1 = sw;
				sh1 = sh;	
			}else{
				float radio_w = sw/(float)dw;
				float radio_h = sh/(float)dh;
				
				if(radio_w>radio_h){
					sw1 = dw;
					sh1 = sw1*sh/(float)sw;
				}else{
					sh1 = dh;
					sw1 = sh1*sw/(float)sh;
				}
				
			}
		}else if(sw<=dw && sh >= dh){
			sh1 = dh;
			sw1 = sh1*sw/(float)sh;
		}else if(sw>=dw && sh<=dh){
			sw1 = dw;
			sh1 = sw1*sh/(float)sw;
		}else{
			float radio_w = sw/(float)dw;
			float radio_h = sh/(float)dh;
			
			if(radio_w>radio_h){
				sw1 = dw;
				sh1 = sw1*sh/(float)sw;
			}else{
				sh1 = dh;
				sw1 = sh1*sw/(float)sh;
			}
		}
		
		startx = (dw - sw1)/2f;
		starty = (dh - sh1)/2f;
		endx = startx + sw1;
		endy = starty + sh1; 
		
		return new Rect((int)startx, (int)starty, (int)endx, (int)endy);
	}


	private int[] getBroderColor(Bitmap src){
		
		
		int top = getCenterTopColor(src);
		int bottom = getCenterBottomColor(src);
		
		int top1;
		int bottom1;
		
		top1 = lighterColorHSL_low(top);
		bottom1 = lighterColorHSL_low(bottom);
		
		float compare = 20;
		
		if(colorDifferent(top1, top)<compare){
			top1 = lighterColorHSL_deep(top);
		}
		
		if(colorDifferent(bottom, bottom1)<compare){
			bottom1 = lighterColorHSL_deep(bottom);
		}
		
		
		int[] color =new int[]{top1,bottom1};
		return color;
		
	}
	
	/**
	 * 得到原始图片中，底部中间的第�?个完全不透明的颜�?
	 * 
	 * @return
	 */
	private int getCenterBottomColor(Bitmap src) {
		int w = src.getWidth();
		int h = src.getHeight();

		int getHeight = 30;
		int starty = h-getHeight;
		if(starty<0){
			starty = 0;
		}
		if(starty>h){
			starty = h;
		}
		
		int c = getAreaColor(src,0 ,starty,w,h);
		
		return c;
	}

	/**
	 * 得到原始图片中，顶部中间的第�?个完全不透明的颜�?
	 * 
	 * @return
	 */
	private int getCenterTopColor(Bitmap src) {
		int w = src.getWidth();
		int h = src.getHeight();
		
		int getHeight = 30;
		
		int endy = getHeight;
		if(endy>h){
			endy = h;
		}
		if(endy<0){
			endy = 0;
		}
		
		int c = getAreaColor(src,0,0,w,endy);
		
		
		return c;
	}

	private int getAreaColor(Bitmap src,int startx,int starty,int endx,int endy){
		SparseIntArray countMap = new SparseIntArray();
		
		for(int x = startx;x<endx;++x){
			for(int y = starty;y<endy;++y){
				int pixel = src.getPixel(x, y);
				if (Color.alpha(pixel) >=0xAA) {
					int c = pixel;
					if(countMap.get(c)==0){
						countMap.put(c, 1);
					}else{
						int count = countMap.get(c);
						++count;
						countMap.put(c, count);
					}
				}
			}
		}

		int selectedColor = Color.WHITE;
		int selectedColorCount = -1;
		
		for(int i =0;i<countMap.size();++i){
			int color = countMap.keyAt(i);
			int count = countMap.get(color);
			if(count > selectedColorCount){
				selectedColor = color;
				selectedColorCount = count;
			}
		}
		
		return selectedColor;
	}
	
	private int lighterColorHSL_deep(int color) {
		
		if(color == Color.WHITE){
			return color;
		}
		
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		float[] hsl = new float[3];

		HSLColor.fromRGB(r, g, b, hsl);

		hsl[2] *= 1.4;

		if (hsl[2] < 0.5) {
			hsl[2] = 0.8f;
		}

		if (hsl[2] > 1) {
			hsl[2] = 1;
		}
		
		
		hsl[1] *= 0.8;
		
		return HSLColor.toRGB(hsl);
	}
	
	private int lighterColorHSL_low(int color) {
		
		if(color == Color.WHITE){
			return color;
		}
		
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		float[] hsl = new float[3];

		HSLColor.fromRGB(r, g, b, hsl);

		hsl[2] *= 1.2;

		if (hsl[2] > 1) {
			hsl[2] = 1;
		}
		
		
		hsl[1] *= 0.8;
		
		return HSLColor.toRGB(hsl);
	}
	
	
	
	private  double colorDifferent(int r,int g,int b,int r1,int g1,int b1){
		double[] lab,
				 lab1;
		
		lab = ColorSpaceConverter.getInstance().RGBtoLAB(r, g, b);
		lab1 = ColorSpaceConverter.getInstance().RGBtoLAB(r1, g1, b1);
		
		double dl = lab[0] - lab1[0];
		double da = lab[1] - lab1[1];
		double db = lab[2] - lab1[2];
		
		return Math.sqrt(dl*dl + da*da + db*db );
	}
	
	
	private double colorDifferent(int color,int color1){
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int r1 = Color.red(color1);
		int g1 = Color.green(color1);
		int b1 = Color.blue(color1);
		
		return colorDifferent(r, g, b, r1, g1, b1);
	}

}
