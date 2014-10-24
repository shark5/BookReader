package com.pmmq.bookreader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.pmmq.bookreader.model.EBook;
import com.pmmq.bookreader.util.CopyFileFromAssets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BookListActivity extends Activity {

	private String TAG = "BookListActivity";
	// 启动Activity的返回码
	final static int REQUST_CODE_OPEN_FILE = 1; 
		
	private ListView mBookListView;
	
	@SuppressWarnings("serial")
	private List<EBook> eBookList = new ArrayList<EBook>() {};		//从数据库读取的Book List数据
		
	private SimpleAdapter listItemAdapter = null;
	private ArrayList<HashMap<String, Object>> listItem = null;		//ListAdapter map 数据
	private AdapterView.AdapterContextMenuInfo menuInfo;
	private ProgressDialog mpDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        Log.d(TAG, "onCreate");
        //导入txt
        new CopyFileFromAssets().testCopy(this);

        File sdDir = null; 
        boolean sdCardExist = Environment.getExternalStorageState()   
                            .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
        if(sdCardExist){                               
          sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
        }   
        Log.d(TAG, "onCreate" + Environment.getExternalStorageDirectory().getAbsolutePath());
//        Environment.getExternalStorageDirectory().getExternalStorageDirectory();//获取SD卡根目录

        mBookListView = (ListView)findViewById(R.id.book_list);
        
        // 添加长按点击
        mBookListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

     			@Override
     			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

     				menu.add(Menu.NONE, 0, 0, "从阅读列表中删除");
     				menu.add(Menu.NONE, 1, 0, "创建快捷方式");
     			}

     		});

        mBookListView.setOnItemClickListener(new OnItemClickListener() {
     			@Override
     			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
     				try {
     					showProgressDialog("正在加载电子书...");
     					// 修改数据库中图书的最近阅读状态为1
     					String path = (String) listItem.get(arg2).get("path");
     					int id = (Integer) listItem.get(arg2).get("id");

     					File f = new File(path);
     					if (f.length() == 0) {
     						Toast.makeText(BookListActivity.this, "该文件为空文件", Toast.LENGTH_SHORT).show();
     						if (mpDialog != null) {
     							mpDialog.dismiss();
     						}
     					} else {
     						Intent intent = new Intent();
     						intent.setClass(BookListActivity.this, ReadActivity.class);
     						intent.putExtra("path", path);
     						intent.putExtra("id", id);
     						startActivity(intent);
     					}
     				} catch (SQLException e) {
     					Log.e(TAG, "list.setOnItemClickListener-> SQLException error", e);
     				} catch (Exception e) {
     					Log.e(TAG, "list.setOnItemClickListener Exception", e);
     				}
     			}
     		});
        if (listItem == null){
        	listItem = new ArrayList<HashMap<String, Object>>();
        }
		listItem.clear();
        setAdapter();
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		getDataForBookList();
	}

	/**
	 * 加载listview 的adapter
	 */
	private void setAdapter() {
		Log.d(TAG, "setAdapter");
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				R.layout.book_list_item,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "itemback", "ItemImage", "BookName", "ItemTitle", "ItemTitle1", "ItemTitle2", "progress", "LastImage" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.itemback, R.id.ItemImage, R.id.bookName, R.id.ItemTitle, R.id.ItemTitle1, R.id.ItemTitle2, R.id.progress, R.id.last });
		// 添加并且显示
		mBookListView.setAdapter(listItemAdapter);
	}
	
	private void getDataForBookList(){
		//从数据库读取数据
        eBookList = DataSupport.findAll(EBook.class);
        Log.d(TAG, eBookList.toString());
        if (listItem == null){
        	listItem = new ArrayList<HashMap<String, Object>>();
        }
		listItem.clear();
		
        for(EBook book : eBookList){
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	if (book.getId() % 2 == 0) {
				map.put("itemback", R.drawable.itemback1);
			} else {
				map.put("itemback", R.drawable.itemback1);
			}
			map.put("ItemImage", R.drawable.cover);
			map.put("BookName", book.getBookName());
			map.put("ItemTitle", book.getBookName());
			map.put("ItemTitle1", "作者：" + book.getAuth());
			map.put("LastImage", book.getImportType());
			map.put("path", book.getPath());
			map.put("progress", book.getProgress() + "%");
			map.put("id", book.getId());
			listItem.add(map);
        }
        listItemAdapter.notifyDataSetChanged();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
		case R.id.it1:
			// 进入导入图书界面
			Intent intent = new Intent();
			intent.putExtra("explorer_title",
					getString(R.string.dialog_read_from_dir));
			intent.setDataAndType(Uri.fromFile(new File("/sdcard")), "*/*");
			intent.setClass(BookListActivity.this, ExDialog.class);
			startActivityForResult(intent, REQUST_CODE_OPEN_FILE);
			break;
		case R.id.it2:
			// 进入反馈界面
			break;
		case R.id.it3:
			// 进入关于界面
			/*AboutDialog about;
			about = new AboutDialog(BookListActivity.this, R.style.FullHeightDialog);
			about.show();
			about.setMessage1(getString(R.string.about_book));
			about.setMessage2(getString(R.string.app_name));
			about.setMessage3(getString(R.string.aboue_text1));*/
			break;
		}
        return super.onOptionsItemSelected(item);
    }
    
    
    // 处理Activity返回的结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == REQUST_CODE_OPEN_FILE) {
				// 用户打开文件，返回一个完整的文件路径
				Bundle b = data.getExtras();
	            String str = b.getString("FILE_PATH");  
				Log.d(TAG, "onActivityResult str = " + str);
				EBook book = new EBook();
				book.setPath(str);
				book.setBookName(str.substring(str.lastIndexOf("/") + 1, str.length() - 4));
				book.setAuth("未知");
				book.setImportDate(new Date());
				book.setProgress(0);
				book.setImportType("本地导入");
				book.save();
			} 
			break;
		}
    }
    
    /**
	 * 长按菜单响应函数
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:// 删除文件
				// 获取点击的是哪一个文件
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			HashMap<String, Object> imap = listItem.get(menuInfo.position);
			int id = (Integer) imap.get("id");
			int rowsAffected = DataSupport.delete(EBook.class, id);
			Toast.makeText(this, getString(R.string.delsuc), Toast.LENGTH_SHORT).show();
			getDataForBookList();
			break;

		case 1:// 创建快捷方式
				// 获取点击的是哪一个文件
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			// 通过position获取图书地址
			HashMap<String, Object> imap1 = listItem.get(menuInfo.position);
			String path1 = (String) imap1.get("path");

			Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon); // 获取快捷键的图标
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, imap1.get("ItemTitle").toString() + ".txt");// 快捷方式的标题

			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);//
			// 快捷方式的图标
			ComponentName comp = new ComponentName(this.getPackageName(), "." + this.getLocalClassName());
			// 将关键字"bbb"放入intent 当重新快捷方式重新载入本页时 自动跳转到该书
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).putExtra("bbb", path1).setComponent(comp));//
			// 快捷方式的动作
			sendBroadcast(addIntent);// 发送广播

			Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
			shortcut.putExtra("duplicate", false); // 不允许重复创建
			// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer //注意:
			// ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序 // ComponentName comp
			// =
			// new ComponentName(this.getPackageName(),
			// "."+this.getLocalClassName()); //
			// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
			// Intent(Intent.ACTION_MAIN).setComponent(comp));
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).putExtra("bbb", path1).setComponent(comp));
			// 快捷方式的图标
			ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			sendBroadcast(shortcut);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public void showProgressDialog(String msg) {
		mpDialog = new ProgressDialog(BookListActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		mpDialog.setMessage(msg);
		mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
		mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
		mpDialog.show();
	}
	
	public void closeProgressDialog() {
		if (mpDialog != null) {
			mpDialog.dismiss();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		closeProgressDialog();
	}
}
