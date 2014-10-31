package com.pmmq.bookreader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import com.pmmq.bookreader.model.EBook;

import android.content.Context;

public class CopyFileFromAssets {
	private String TAG = "CopyFileFromAssets";
	/**
	  * 
	  * @param myContext
	  * @param ASSETS_NAME 要复制的文件名
	  * @param savePath 要保存的路径
	  * @param saveName 复制后的文件名
	  *  testCopy(Context context)是一个测试例子。
	  */
	 
	 public static int copy(Context myContext, String ASSETS_NAME,
			 String savePath, String saveName) {
		 String filename = savePath + "/" + saveName;

		 File dir = new File(savePath);
		 // 如果目录不中存在，创建这个目录
		 if (!dir.exists()){
			 dir.mkdir();
		 }
		 try {
			 if (!(new File(filename)).exists()) {
				 InputStream is = myContext.getResources().getAssets().open(ASSETS_NAME);
				 FileOutputStream fos = new FileOutputStream(filename);
				 byte[] buffer = new byte[7168];
				 int count = 0;
				 while ((count = is.read(buffer)) > 0) {
					 fos.write(buffer, 0, count);
				 }
				 fos.close();
				 is.close();
				 return 1;
			 }
			 return 0;
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return 0;
	 }
	 
	 public void testCopy(Context context) {
		 String path = context.getFilesDir().getAbsolutePath();
		 Logger.d(TAG, "testCopy path = " + path);

	     String name = "test.txt";
	     int result = CopyFileFromAssets.copy(context, name, path, name);
	     if(result == 1){
		     EBook book = new EBook();
		     book.setPath(path + "/" + name);
		     book.setBookName("绝世唐门");
		     book.setAuth("唐家三少");
		     book.setImportDate(new Date());
		     book.setProgress(0);
		     book.setImportType("推荐书目");
		     book.save();
	     }
	 }
}
