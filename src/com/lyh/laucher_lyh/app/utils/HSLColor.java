package com.lyh.laucher_lyh.app.utils;

/*
 * 
 * @(#)HSLColor.java
 *
 * $Date$
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */



/**
 *  This class interfaces with colors in terms of Hue, Saturation and Luminance.
 *  <P>This is copied from Rob Camick's class here:
 *  <br>http://tips4java.wordpress.com/2009/07/05/hsl-color/
 *  
 * @see <a href="http://tips4java.wordpress.com/2009/07/05/hsl-color/">HSL Color</a>
 */
public class HSLColor
{
	/**
	 *  Convert a RGB Color to it corresponding HSL values.
	 *
	 * @param color the color to convert.
	 * @param dest the optional array to store the result in.
	 *  @return an array containing the 3 HSL values.
	 */
	public static float[] fromRGB(int r1,int g1,int b1,float[] dest)
	{
		//  Get RGB values in the range 0 - 1

		float r = (r1)/255f;
		float g = (g1)/255f;
		float b = (b1)/255f;

		//	Minimum and Maximum RGB values are used in the HSL calculations

		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		//  Calculate the Hue

		float h = 0;

		if (max == min)
			h = 0;
		else if (max == r)
			h = (( (g - b) / (max - min) / 6f) + 1) % 1;
		else if (max == g)
			h = ( (b - r) / (max - min) / 6f) + 1f/3f;
		else if (max == b)
			h = ( (r - g) / (max - min) / 6f) + 2f/3f;

		//  Calculate the Luminance

		float l = (max + min) / 2;

		//  Calculate the Saturation

		float s = 0;

		if (max == min)
			s = 0;
		else if (l <= .5f)
			s = (max - min) / (max + min);
		else
			s = (max - min) / (2 - max - min);

		if(dest==null)
			dest = new float[3];
		dest[0] = h;
		dest[1] = s;
		dest[2] = l;
		
		return dest;
	}

	/**
	 *  Convert HSL values to a RGB Color with a default alpha value of 1.
	 *  H (Hue) is specified as degrees in the range 0 - 1.
	 *  S (Saturation) is specified as a percentage in the range 0 - 1.
	 *  L (Luminance) is specified as a percentage in the range 0 - 1.
	 *
	 *  @param hsl an array containing the 3 HSL values
	 */
	public static int toRGB(float[] hsl)
	{
		return toRGB(hsl, 1.0f);
	}

	/**
	 *  Convert HSL values to a RGB Color.
	 *  H (Hue) is specified as degrees in the range 0 - 1.
	 *  S (Saturation) is specified as a percentage in the range 0 - 1.
	 *  L (Luminance) is specified as a percentage in the range 0 - 1.
	 *
	 *  @param hsl    an array containing the 3 HSL values
	 *  @param alpha  the alpha value between 0 - 1
	 */
	public static int toRGB(float[] hsl, float alpha)
	{
		return toRGB(hsl[0], hsl[1], hsl[2], alpha);
	}

	/**
	 *  Convert HSL values to a RGB Color with a default alpha value of 1.
	 *
	 *  @param h Hue is specified as degrees in the range 0 - 1.
	 *  @param s Saturation is specified as a percentage in the range 0 - 1.
	 *  @param l Luminance is specified as a percentage in the range 0 - 1.
	 */
	public static int toRGB(float h, float s, float l)
	{
		return toRGB(h, s, l, 1.0f);
	}

	/**
	 *  Convert HSL values to a RGB Color.
	 *
	 *  @param h Hue is specified as degrees in the range 0 - 1.
	 *  @param s Saturation is specified as a percentage in the range 0 - 1.
	 *  @param l Luminance is specified as a percentage in the range 0 - 1.
	 *  @param alpha  the alpha value between 0 - 1
	 */
	public static int toRGB(float h, float s, float l, float alpha)
	{
		if (s <0.0f || s > 1.0f)
		{
			String message = "Color parameter outside of expected range - Saturation ("+s+")";
			throw new IllegalArgumentException( message );
		}

		if (l <0.0f || l > 1.0f)
		{
			String message = "Color parameter outside of expected range - Luminance ("+l+")";
			throw new IllegalArgumentException( message );
		}

		if (alpha <0.0f || alpha > 1.0f)
		{
			String message = "Color parameter outside of expected range - Alpha ("+alpha+")";
			throw new IllegalArgumentException( message );
		}


		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		int r = (int)(255*Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f))));
		int g = (int)(255*Math.max(0, HueToRGB(p, q, h)));
		int b = (int)(255*Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f))));

		int alphaInt = (int)(255*alpha);
		
		return (alphaInt << 24) + ( r << 16 ) + ( g << 8 ) + (b);
	}

	private static float HueToRGB(float p, float q, float h)
	{
		if (h < 0) h += 1;

		if (h > 1 ) h -= 1;

		if (6 * h < 1)
		{
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1 )
		{
			return  q;
		}

		if (3 * h < 2)
		{
			return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
		}

   		return p;
	}
}
