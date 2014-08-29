package com.artifex.mupdfdemo;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.xianzhi.office.R;
import com.xianzhi.office.appPDF;
import com.xianzhi.office.writeEmailActivity;

public class ChoosePDFActivity extends Activity{
	private File mDirectory;
	private Map<String, Integer> mPositions = new HashMap<String, Integer>();
	private File mParent;
	private File[] mDirs;
	private File[] mFiles;
	private Handler mHandler;
	private Runnable mUpdateFiles;
	private ChoosePDFAdapter adapter;
	private PopupWindow mPopupWindow;
	public static Thread saveThread;
	private ListView fileListView;
	private TextView pathText;
	private ImageButton submit_btn;
	private ImageButton goback_btn;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHandler.post(mUpdateFiles);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		mPositions.put(mDirectory.getAbsolutePath(),
				fileListView.getFirstVisiblePosition());
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String storageState = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(storageState)
				&& !Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.no_media_warning);
			builder.setMessage(R.string.no_media_hint);
			AlertDialog alert = builder.create();
			alert.setButton(AlertDialog.BUTTON_POSITIVE,
					getString(R.string.dismiss), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}
		goback_btn = (ImageButton) findViewById(R.id.goback_btn);
		goback_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		pathText = (TextView) findViewById(R.id.path_text);
		String path = appPDF.appPath;
		pathText.setText(path);
		if (mDirectory == null)
			mDirectory = new File(path);
		fileListView = (ListView) findViewById(R.id.filelistView);
		// Create a list adapter...
		adapter = new ChoosePDFAdapter(getLayoutInflater());
		fileListView.setAdapter(adapter);

		// ...that is updated dynamically when files are scanned
		mHandler = new Handler();
		mUpdateFiles = new Runnable() {
			public void run() {
				adapter.clear();
				Resources res = getResources();
				String appName = res.getString(R.string.app_name);
				String version = res.getString(R.string.version);
				String title = res.getString(R.string.picker_title_App_Ver_Dir);
				setTitle(String.format(title, appName, version, mDirectory));

				mParent = mDirectory.getParentFile();

				mDirs = mDirectory.listFiles(new FileFilter() {

					public boolean accept(File file) {
						return file.isDirectory();
					}
				});
				if (mDirs == null)
					mDirs = new File[0];

				mFiles = mDirectory.listFiles(new FileFilter() {

					public boolean accept(File file) {
						if (file.isDirectory())
							return false;
						String fname = file.getName().toLowerCase();
						if (fname.endsWith(".amr"))
							return false;
						
						 /* if (fname.endsWith(".xps")) return true; if
						 * (fname.endsWith(".cbz")) return true; if
						 * (fname.endsWith(".png")) return true; if
						 * (fname.endsWith(".jpe")) return true; if
						 * (fname.endsWith(".jpeg")) return true; if
						 * (fname.endsWith(".jpg")) return true; if
						 * (fname.endsWith(".jfif")) return true; if
						 * (fname.endsWith(".jfif-tbnl")) return true; if
						 * (fname.endsWith(".tif")) return true; if
						 * (fname.endsWith(".tiff")) return true;
						 */
						return true;
					}
				});
				if (mFiles == null)
					mFiles = new File[0];

				Arrays.sort(mFiles, new Comparator<File>() {
					public int compare(File arg0, File arg1) {
						return arg0.getName().compareToIgnoreCase(
								arg1.getName());
					}
				});

				Arrays.sort(mDirs, new Comparator<File>() {
					public int compare(File arg0, File arg1) {
						return arg0.getName().compareToIgnoreCase(
								arg1.getName());
					}
				});

				// if (mParent != null){
				// adapter.add(new ChoosePDFItem(ChoosePDFItem.Type.PARENT,
				// getString(R.string.parent_directory),null));
				// }
				for (File f : mDirs) {
					long time = f.lastModified();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					adapter.add(new ChoosePDFItem(ChoosePDFItem.Type.DIR, f
							.getName(), cal.getTime().toLocaleString()));
				}
				for (File f : mFiles) {
					long time = f.lastModified();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					adapter.add(new ChoosePDFItem(ChoosePDFItem.Type.DOC, f
							.getName(), cal.getTime().toLocaleString()));
				}
				pathText.setText(mDirectory.getAbsolutePath());
				lastPosition();
			}
		};

		// Start initial file scan...
		mHandler.post(mUpdateFiles);

		// ...and observe the directory and scan files upon changes.
		FileObserver observer = new FileObserver(mDirectory.getPath(),
				FileObserver.CREATE | FileObserver.DELETE) {
			public void onEvent(int event, String path) {
				mHandler.post(mUpdateFiles);
			}
		};
		submit_btn = (ImageButton) findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPositions.put(mDirectory.getAbsolutePath(),
						fileListView.getFirstVisiblePosition());
				if (mParent != null) {
					mDirectory = mParent;
					mHandler.post(mUpdateFiles);
				}
			}
		});
		observer.startWatching();
		fileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				mPositions.put(mDirectory.getAbsolutePath(),
						fileListView.getFirstVisiblePosition());
				if (position < mDirs.length) {
					mDirectory = mDirs[position];
					mHandler.post(mUpdateFiles);
					return;
				}
				position -= mDirs.length;
				appPDF.path = mFiles[position].getAbsolutePath();
//					Uri uri = Uri.parse(appPDF.path);
//					Intent intent = new Intent(ChoosePDFActivity.this,
//							MuPDFActivity.class);
//					intent.setAction(Intent.ACTION_VIEW);
//					intent.setData(uri);
					// startActivity(intent);
				Intent intent = new Intent(ChoosePDFActivity.this,
						writeEmailActivity.class);
				String pathString = appPDF.path;
				intent.putExtra("path", pathString);
				setResult(writeEmailActivity.Result_attachment, intent);
				finish();
				 /*else {
					try {
						OpenFiles.OpenFile(getApplicationContext(), new File(
								appPDF.path));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(), "打开文件失败",
								Toast.LENGTH_LONG).show();
					}
				}*/
			}
		});
		fileListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView name = (TextView) arg1.findViewById(R.id.name);
				initPopupwin(arg1, mDirectory.getAbsolutePath() + "/"
						+ name.getText().toString());
				return false;
			}
		});
	}

	private void initPopupwin(final View cell_view, String path) {

		final File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			return;
		}

		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View popunwindwow = mLayoutInflater.inflate(
				R.layout.popupwin_delete_detail, null);
		Button delete_btn = (Button) popunwindwow.findViewById(R.id.delete_btn);

		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		delete_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				file.delete();
				mHandler.post(mUpdateFiles);
				mPopupWindow.dismiss();
			}
		});
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		// mPopupWindow.setAnimationStyle(R.style.AnimationFade);
		mPopupWindow.showAsDropDown(cell_view);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			}
		});
	}

	private void lastPosition() {
		String p = mDirectory.getAbsolutePath();
		if (mPositions.containsKey(p))
			fileListView.setSelection(mPositions.get(p));
	}


	
}
