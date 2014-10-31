// 打开文件的Activity
// Leo @ 2010/10/05

package com.pmmq.bookreader;

import java.io.File;

import com.pmmq.bookreader.util.Logger;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

@SuppressLint("SdCardPath") public class OpenFileActivity extends Activity {

	private String TAG = "OpenFileActivity";
	
	private final static String mFileExt = "txt";	// 只处理txt格式的文件 
	public final static int TXT_FILE = 0;			// 文本文件的标志
	public final static int FOLDER = 1;				// 文件夹的标志
	
//	private File mCurrentDirectory = new File("/sdcard");	// 根目录
	private String mDir = "/sdcard";
	
	private ListView mlvFileList;							// 显示文件选项的容器
	private FileListAdapter mAdapter;						// 文件列表适配器
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openfile);
		
       /* boolean sdCardExist = Environment.getExternalStorageState()   
                            .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
        if(sdCardExist){                               
        	mCurrentDirectory = Environment.getExternalStorageDirectory();//获取跟目录 
        }else{
        	Toast.makeText(this, getString(R.string.insertsdcard), Toast.LENGTH_LONG).show();
        	finish();
        }*/
        
		mlvFileList = (ListView)findViewById(R.id.openfile_listview_file_select);
		mAdapter = new FileListAdapter(this);
		mlvFileList.setAdapter(mAdapter);
		
		// 设置点到ListView项时的监听函数
		ListView.OnItemClickListener itemClick = new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int itemType = mAdapter.getItemType((int)id);
				if (itemType == FOLDER){								// 是文件夹
					/*String s = mAdapter.getItem((int)id).name;
					if (s.equals("..")){
						mPath = mCurrentDirectory.getParent();
						} else {
							mPath = mCurrentDirectory.getPath() + "/" + s + "/";
							}
					
					mCurrentDirectory = new File(mPath);*/
					mDir = mAdapter.getItem((int)id).path;
					getListFile();
					} else {											// 是文件
						Bundle bundle = new Bundle();

						Logger.d(TAG, "FILE_PATH:" + mAdapter.getItem((int)id).path + "/" 
								+ mAdapter.getItem((int)id).name);
						
						bundle.putString("FILE_PATH", mAdapter.getItem((int)id).path + "/" 
								+ mAdapter.getItem((int)id).name);
						Intent mIntent = new Intent();
						mIntent.putExtras(bundle);
						setResult(RESULT_OK, mIntent);
						OpenFileActivity.this.finish();
						}
			}
		};
		
		getListFile();
		mlvFileList.setOnItemClickListener(itemClick);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(TAG, "onResume");
		MobclickAgent.onResume(this);
	}
    
    @Override
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
	
	private void getListFile() {
		mAdapter.clearItems();
		mAdapter.notifyDataSetChanged();
		mlvFileList.postInvalidate();
		
		/*List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;*/
		File f = new File(mDir);
		File[] files = f.listFiles();

		if (!mDir.equals("/sdcard")) {
			FileListItem item = new FileListItem();
			item.name = "..";
			item.type = FOLDER;
			item.path = f.getParent();
			mAdapter.addItem(item);
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()){
					FileListItem item = new FileListItem();
					item.name = files[i].getName();
					item.type = FOLDER;
					item.path = files[i].getPath();
					mAdapter.addItem(item);
				} else {
					if (checkExt(files[i].getName().toLowerCase())) {
						FileListItem item = new FileListItem();
						item.name = files[i].getName();
						item.type = TXT_FILE;
						mAdapter.addItem(item);
					}
				}
			}
		}
		mAdapter.notifyDataSetChanged();
		mlvFileList.postInvalidate();
	}
	/*
	private void ListFile(File aDirectory) {
		mAdapter.clearItems();
		mAdapter.notifyDataSetChanged();
		mlvFileList.postInvalidate();
		
		if (!aDirectory.getPath().equals("/sdcard")){
			FileListItem item = new FileListItem();
			item.name = "..";
			item.type = FOLDER;
			mAdapter.addItem(item);
			}
		
		File[] files = aDirectory.listFiles();
		
		Logger.d(TAG, "ListFile:" + aDirectory.toString());
//		Logger.d(TAG, "ListFile:" + aDirectory.listFiles().toString());
		if (files != null) {
		for(File f : aDirectory.listFiles()){
			if (f.isDirectory()){
				FileListItem item = new FileListItem();
				item.name = f.getName();
				item.type = FOLDER;
				mAdapter.addItem(item);
			} else {
				if (checkExt(f.getName().toLowerCase())) {
					FileListItem item = new FileListItem();
					item.name = f.getName();
					item.type = TXT_FILE;
					mAdapter.addItem(item);
					}
			}
		}
		}
		mAdapter.notifyDataSetChanged();
		mlvFileList.postInvalidate();
	}*/
	
	private boolean checkExt(String itemName) {
		if (itemName.endsWith(mFileExt))
			return true;
		return false;
	}
}
