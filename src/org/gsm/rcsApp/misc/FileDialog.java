package org.gsm.rcsApp.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.gsm.RCSDemo.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class FileDialog extends ListActivity {

	
	private static final String ITEM_KEY = "key";

	
	private static final String ITEM_IMAGE = "image";

	
	private static final String ROOT = "/mnt/sdcard";

	
	public static final String START_PATH = "START_PATH";
	public static final String FILE_DESCRIPTOR= "FILE_DESCRIPTOR";
	
	public static final String FORMAT_FILTER = "FORMAT_FILTER";

	
	public static final String RESULT_PATH = "RESULT_PATH";

	
	public static final String SELECTION_MODE = "SELECTION_MODE";

	
	public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";

	/**
	 * @uml.property  name="path"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private List<String> path = null;
	/**
	 * @uml.property  name="myPath"
	 * @uml.associationEnd  
	 */
	private TextView myPath;
	/**
	 * @uml.property  name="mFileName"
	 * @uml.associationEnd  
	 */
	private EditText mFileName;
	/**
	 * @uml.property  name="mFileDescriptor"
	 * @uml.associationEnd  
	 */
	private EditText mFileDescriptor;
	/**
	 * @uml.property  name="mList"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.util.HashMap"
	 */
	private ArrayList<HashMap<String, Object>> mList;

	/**
	 * @uml.property  name="selectButton"
	 * @uml.associationEnd  
	 */
	private Button selectButton;

	/**
	 * @uml.property  name="layoutSelect"
	 * @uml.associationEnd  
	 */
	private LinearLayout layoutSelect;
	/**
	 * @uml.property  name="layoutCreate"
	 * @uml.associationEnd  
	 */
	private LinearLayout layoutCreate;
	/**
	 * @uml.property  name="inputManager"
	 * @uml.associationEnd  
	 */
	private InputMethodManager inputManager;
	/**
	 * @uml.property  name="parentPath"
	 */
	private String parentPath;
	/**
	 * @uml.property  name="currentPath"
	 */
	private String currentPath = ROOT;

	/**
	 * @uml.property  name="selectionMode"
	 */
	private int selectionMode = SelectionMode.MODE_CREATE;

	/**
	 * @uml.property  name="formatFilter" multiplicity="(0 -1)" dimension="1"
	 */
	private String[] formatFilter = null;

	/**
	 * @uml.property  name="canSelectDir"
	 */
	private boolean canSelectDir = false;

	/**
	 * @uml.property  name="selectedFile"
	 */
	private File selectedFile;
	/**
	 * @uml.property  name="lastPositions"
	 */
	private HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();

	/**
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED, getIntent());

		setContentView(R.layout.file_dialog_main);
		myPath = (TextView) findViewById(R.id.path);
		mFileName = (EditText) findViewById(R.id.fdEditTextFile);
		mFileDescriptor = (EditText)findViewById(R.id.fdmessage);
		inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		selectButton = (Button) findViewById(R.id.fdButtonSelect);
		selectButton.setEnabled(false);
		selectButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (selectedFile != null) {
					Intent data = new Intent();
					data.putExtra(RESULT_PATH, selectedFile.getPath());
					data.putExtra(FILE_DESCRIPTOR,mFileDescriptor.getText());
					setResult(RESULT_OK, data);
					finish();
				}
			}
		});

		/*final Button newButton = (Button) findViewById(R.id.fdButtonNew);
		newButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setCreateVisible(v);

				mFileName.setText("");
				mFileName.requestFocus();
			}
		});*/

		selectionMode = getIntent().getIntExtra(SELECTION_MODE, SelectionMode.MODE_CREATE);

		formatFilter = getIntent().getStringArrayExtra(FORMAT_FILTER);

		canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);

		/*if (selectionMode == SelectionMode.MODE_OPEN) {
			newButton.setEnabled(false);
		}*/

		layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);
		layoutCreate = (LinearLayout) findViewById(R.id.fdLinearLayoutCreate);
		layoutCreate.setVisibility(View.GONE);

		final Button cancelButton = (Button) findViewById(R.id.fdButtonCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setSelectVisible(v);
			}

		});
		final Button createButton = (Button) findViewById(R.id.fdButtonCreate);
		createButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mFileName.getText().length() > 0) {
					getIntent().putExtra(RESULT_PATH, currentPath + "/" + mFileName.getText());
					getIntent().putExtra(FILE_DESCRIPTOR,mFileDescriptor.getText());
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		});

		String startPath = getIntent().getStringExtra(START_PATH);
		startPath = startPath != null ? startPath : ROOT;
		if (canSelectDir) {
			File file = new File(startPath);
			selectedFile = file;
			selectButton.setEnabled(true);
		}
		getDir(startPath);
	}

	private void getDir(String dirPath) {

		boolean useAutoSelection = dirPath.length() < currentPath.length();

		Integer position = lastPositions.get(parentPath);

		getDirImpl(dirPath);

		if (position != null && useAutoSelection) {
			getListView().setSelection(position);
		}

	}

	
	private void getDirImpl(final String dirPath) {

		currentPath = dirPath;

		final List<String> item = new ArrayList<String>();
		path = new ArrayList<String>();
		mList = new ArrayList<HashMap<String, Object>>();

		File f = new File(currentPath);
		File[] files = f.listFiles();
		if (files == null) {
			currentPath = ROOT;
			f = new File(currentPath);
			files = f.listFiles();
		}
		myPath.setText(getText(R.string.location) + ": " + currentPath);

		if (!currentPath.equals(ROOT)) {

			item.add(ROOT);
			addItem(ROOT, R.drawable.folder);
			path.add(ROOT);

			item.add("../");
			addItem("../", R.drawable.folder);
			path.add(f.getParent());
			parentPath = f.getParent();

		}

		TreeMap<String, String> dirsMap = new TreeMap<String, String>();
		TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();
		TreeMap<String, String> filesMap = new TreeMap<String, String>();
		TreeMap<String, String> filesPathMap = new TreeMap<String, String>();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				dirsMap.put(dirName, dirName);
				dirsPathMap.put(dirName, file.getPath());
			} else {
				final String fileName = file.getName();
				final String fileNameLwr = fileName.toLowerCase();
				// se ha um filtro de formatos, utiliza-o
				if (formatFilter != null) {
					boolean contains = false;
					for (int i = 0; i < formatFilter.length; i++) {
						final String formatLwr = formatFilter[i].toLowerCase();
						if (fileNameLwr.endsWith(formatLwr)) {
							contains = true;
							break;
						}
					}
					if (contains) {
						filesMap.put(fileName, fileName);
						filesPathMap.put(fileName, file.getPath());
					}
					
				} else {
					filesMap.put(fileName, fileName);
					filesPathMap.put(fileName, file.getPath());
				}
			}
		}
		item.addAll(dirsMap.tailMap("").values());
		item.addAll(filesMap.tailMap("").values());
		path.addAll(dirsPathMap.tailMap("").values());
		path.addAll(filesPathMap.tailMap("").values());

		SimpleAdapter fileList = new SimpleAdapter(this, mList, R.layout.file_dialog_row, new String[] {
				ITEM_KEY, ITEM_IMAGE }, new int[] { R.id.fdrowtext, R.id.fdrowimage });

		for (String dir : dirsMap.tailMap("").values()) {
			addItem(dir, R.drawable.folder);
		}

		for (String file : filesMap.tailMap("").values()) {
			addItem(file, R.drawable.file);
		}

		fileList.notifyDataSetChanged();

		setListAdapter(fileList);

	}

	private void addItem(String fileName, int imageId) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_KEY, fileName);
		item.put(ITEM_IMAGE, imageId);
		mList.add(item);
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));

		setSelectVisible(v);
		View selected= l.getChildAt(position);
		selected.setBackgroundColor(Color.YELLOW);
		if (file.isDirectory()) {
			selectButton.setEnabled(false);
			if (file.canRead()) {
				lastPositions.put(currentPath, position);
				getDir(path.get(position));
				if (canSelectDir) {
					selectedFile = file;
					v.setSelected(true);
					selectButton.setEnabled(true);
				}
			} else {
				new AlertDialog.Builder(this).setIcon(R.drawable.icon)
						.setTitle("[" + file.getName() + "] " + getText(R.string.cant_read_folder))
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			}
		} else {
			selectedFile = file;
			v.setSelected(true);
			selectButton.setEnabled(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			selectButton.setEnabled(false);

			if (layoutCreate.getVisibility() == View.VISIBLE) {
				layoutCreate.setVisibility(View.GONE);
				layoutSelect.setVisibility(View.VISIBLE);
			} else {
				if (!currentPath.equals(ROOT)) {
					getDir(parentPath);
				} else {
					return super.onKeyDown(keyCode, event);
				}
			}

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	
	private void setCreateVisible(View v) {
		layoutCreate.setVisibility(View.VISIBLE);
		layoutSelect.setVisibility(View.GONE);

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		selectButton.setEnabled(false);
	}

	
	private void setSelectVisible(View v) {
		layoutCreate.setVisibility(View.GONE);
		layoutSelect.setVisibility(View.VISIBLE);

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		selectButton.setEnabled(false);
	}
}
