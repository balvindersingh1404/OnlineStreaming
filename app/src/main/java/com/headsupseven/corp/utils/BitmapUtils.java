package com.headsupseven.corp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class BitmapUtils {

	public static Bitmap getRescaledBitmap(final Bitmap bit, final int x,
			final int y, final boolean flag) {

		return Bitmap.createScaledBitmap(bit, x, y, true);

	}

	public static Vector<Bitmap> getBitmapFromAsset(Context con, String path)
			throws IOException {
		AssetManager am = con.getAssets();
		String[] files = am.list(path);
		Vector<Bitmap> temp = new Vector<Bitmap>();

		for (String s : files) {
			try {
				temp.addElement(BitmapFactory.decodeStream(am.open(path + "/"
						+ s)));
			} catch (Exception e) {

			}
		}

		return temp;

	}

	public static Bitmap getResizedBitmapByDeviceWidth(Bitmap src,
			int deviceWidth) {

		final int origWidth = src.getWidth();
		final int origHeight = src.getHeight();
		float scale = (float) origHeight / origWidth;

		int destHeight = Math.round(deviceWidth * scale);

		return Bitmap.createScaledBitmap(src, deviceWidth, destHeight, false);

	}

	public static Bitmap getResizedBitmapUsingMatrix(final Bitmap BitmapOrg,
			int x, int y, final boolean flag) {

		final int origHeight = BitmapOrg.getHeight();
		final int origWidth = BitmapOrg.getWidth();
		float scaleWidth;
		float scaleHeight;
		if (origWidth >= origHeight) {
			scaleWidth = (float) x / origWidth;
			scaleHeight = scaleWidth;
		} else {
			scaleHeight = (float) y / origHeight;
			scaleWidth = scaleHeight;
		}
		x = Math.round(origWidth * scaleWidth);
		y = Math.round(origHeight * scaleWidth);

		// create a matrix for the manipulation
		final Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		return Bitmap.createBitmap(BitmapOrg, 0, 0, x, y, matrix, flag);

	}

	public static byte[] getByteArrayFromBitmap(final Bitmap bitmap, int quality) {

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
		return out.toByteArray();
	}

	public static Bitmap getBitmapFromByteArray(final byte data[], int maxSize) {
		final int MAX_SIZE = maxSize;
		if (data.length > 0) {
			try {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length, options);
				final int fullWidth = options.outWidth;
				final int fullHeight = options.outHeight;
				int w = 0;
				int h = 0;
				if (fullWidth > fullHeight) {
					w = MAX_SIZE;
					h = w * fullHeight / fullWidth;
				} else {
					h = MAX_SIZE;
					w = h * fullWidth / fullHeight;
				}
				options.inJustDecodeBounds = false;
				options.inSampleSize = fullWidth / w;
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
						options);
				return bitmap;
			} catch (final Exception e) {
			}
		}
		return null;
	}

	public static InputStream getInputStreamFromBitmap(Bitmap bitmap) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		byte[] bitmapdata = out.toByteArray();
		ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
		return bs;
	}


}
