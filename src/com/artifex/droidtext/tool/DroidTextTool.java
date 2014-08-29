package com.artifex.droidtext.tool;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.artifex.mupdfdemo.BitmapHolder;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.xianzhi.office.appPDF;

public class DroidTextTool {
	public static void addPdfMark(String InPdfFile, String outPdfFile)
			throws Exception {

		PdfReader reader = new PdfReader(InPdfFile, "PDF".getBytes());
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
				outPdfFile));

		for (HashMap<String, Object> stampob : appPDF.stampHolderlist) {
			int page = (Integer) (stampob.get("page"));
			Bitmap mBitmap = ((BitmapHolder) stampob.get("stamp")).getBm();
			Point stamppoint = (Point) stampob.get("point");
			float f_stampscale = (Float) stampob.get("stampscale");
			float pagescale = (Float) stampob.get("pagescale");
			float ab_y = (Float) stampob.get("ab_y");
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			Image img = Image.getInstance(stream.toByteArray());
			float width = mBitmap.getWidth()*f_stampscale*0.5f/pagescale;
			float height = mBitmap.getHeight()*f_stampscale*0.5f/pagescale;
			float x = (stamppoint.x*pagescale-mBitmap.getWidth()*f_stampscale/2)*0.5f/pagescale;
			float y = (ab_y*pagescale-mBitmap.getHeight()*f_stampscale/2)*0.5f/pagescale;

			img.scaleAbsolute(width,height);
			img.setAbsolutePosition(x, y);
			PdfContentByte over = stamp.getOverContent(page + 1);
			over.addImage(img);
			
		}
		stamp.close();// ¹Ø±Õ
		reader.close();
	}
}
