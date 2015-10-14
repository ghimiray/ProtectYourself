package com.example.protectyourself;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;


public class MainActivity extends FragmentActivity {

	private static final String[] CONTENT = new String[] { "All Contacts","Added Contacts","WebBrowser"};
	public String[] mPlanetTitles;
	public CharSequence mDrawerTitle;
	public CharSequence mTitle;
	public DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	ProgressDialog dialog;
	NetworkInfo mWifi;
	private Context mContext = this;
	public ActionBarDrawerToggle mDrawerToggle;
	public ArrayList<String> addedNumbers;
	public static String[] numberArr;
	public DatabaseHandler db;
	public List<Contact> contacts;
	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	public GoogleAccountCredential credential;
	private static final String[] SCOPES = { "https://www.googleapis.com/auth/plus.login",
	      "https://www.googleapis.com/auth/drive"};
	public static Activity activity;
	private static Drive 			mService;
	boolean googleIdGot = false;
	private ViewPager pager;
	private TabPageIndicator indicator;
	private boolean finish;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences settings = mContext.getSharedPreferences("eventGoogle", Context.MODE_PRIVATE);
		credential = GoogleAccountCredential.usingOAuth2(mContext, Arrays.asList(SCOPES )).setBackOff(new ExponentialBackOff())
				.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		mService = getDriveService(credential);
		activity = this;
		
		String accountName = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		
		Log.e("accountName", accountName+"");
		if (settings.getBoolean(accountName,false) != true) {
			saveFileToDrive();
		}
		
		File folder = new File(Environment.getExternalStorageDirectory() + "/ProtectData");
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			Log.e("sabeen","directory made");
		} else {
			// Do something else on failure 
		}
		ComponentName cna=new ComponentName(this, AdminReceiver.class);
		DevicePolicyManager mgr=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

		if (mgr.isAdminActive(cna)) {
			int msgId;

			if (mgr.isActivePasswordSufficient()) {
				msgId=R.string.compliant;
			}
			else {
				msgId=R.string.not_compliant;
			}

			Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
		}
		else {
			Intent intent=
					new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cna);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					getString(R.string.device_admin_explanation));
			startActivity(intent);
		}

		overridePendingTransition(0,0);
		db = new DatabaseHandler(this);
		addedNumbers = new ArrayList<String>();
		contacts = db.getAllContacts();       
		for(Contact cn: contacts){
			addedNumbers.add(cn.getPhoneNumber());

		}

		numberArr = new String[addedNumbers.size()];
		numberArr = addedNumbers.toArray(numberArr);


		setContentView(R.layout.activity_main);
		finish = getIntent().getBooleanExtra("finish", false);
		FragmentPagerAdapter adapterFragment = new SabeenAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapterFragment);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		if(finish){
			pager.setCurrentItem(1);
		}
	}

	class SabeenAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
		public SabeenAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;

			switch (position) {
			case 0:
				f = new AllContactsFragment();
				break;

			case 1:
				f = new AddedContactsFragment();
				break;

			case 2:
				f = new WebViewActivity();
				break;

			default:
				throw new IllegalArgumentException("not this many fragments: "+ position);
			}
			// DebugUtils.debugUI("getView(): " + f.toString());
			return f;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getIconResId(int index) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	public void onAdd(View view) {

		final String number = (String) view.getTag(R.string.checka);
		final String name = (String) view.getTag(R.string.checkb);
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					//					numbers.add(number);
					//					for (int i = 0; i < numbers.size(); i++) {
					Log.e("s", number);
					//					}
					db.addContact(new Contact(name,number));
					Toast.makeText(getApplicationContext(), name+" "+number+" has been added to your list", Toast.LENGTH_LONG).show();
					finish();
					overridePendingTransition(0,0);
					startActivity(getIntent());
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Add to you Emergency list")
		.setMessage("Add this number " + number + "?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener)
		.setCancelable(false).show();

	}

	public void onDelete(View view) {
		final String number = (String) view.getTag(R.string.checka);
		final String name = (String) view.getTag(R.string.checkb);
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Log.e("s", number);
					db.deleteContact(name);
					Toast.makeText(getApplicationContext(), name+" "+number+" has been deleted from your list", Toast.LENGTH_LONG).show();
					
					finish();
					overridePendingTransition(0,0);
					Intent main = new Intent(MainActivity.this,MainActivity.class);
					main.putExtra("finish", true);
					startActivity(main);

					
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					

					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Delete from your Emergency list")
		.setMessage("Delete this number " + number + "?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener)
		.setCancelable(false).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isGooglePlayServicesAvailable()) {
			refreshResults();
		} else {
			Log.d("Google log", "Google play services required");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode != RESULT_OK) {
				isGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == RESULT_OK && data != null &&
			data.getExtras() != null) {
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = this.getSharedPreferences("eventGoogle",Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(accountName, true);
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Log.e("Google Log","Account Unspecefied");
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode != RESULT_OK) {
				
					saveFileToDrive();
			}
			break;
		}
	}

	private void chooseAccount() throws UserRecoverableAuthIOException {
		
	startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

	}

	private void refreshResults() {
		if (credential.getSelectedAccountName() == null) {
			Log.e("google calendar","choose Account");
			try {
				chooseAccount();
			} catch (UserRecoverableAuthIOException e) {
				
				e.printStackTrace();
			}
		} else {
			Log.e("google calendar","account achieved");
			if (isDeviceOnline()) {
				//startService(new Intent(this,EventGoogleService.class));
				//finish();
			} else {
				Log.e("Google Log","No network Connection Available");
			}
		}

	}

	private boolean isDeviceOnline() {
		ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	private boolean isGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		} else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
			return false;
		}
		return true;
	}

	private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
			}
		});

	}
	
	private void saveFileToDrive() 
    {
    	Thread t = new Thread(new Runnable()
    	{
    		@Override
    		public void run() 
    		{
				try 
				{
				 	Log.d("upload service","save to file started");
					// Create URI from real path
				 	File dirFile = new File(Environment.getExternalStorageDirectory(),"proteTest");
				 	if(!dirFile.exists()){
				 		dirFile.mkdirs();
				 		
				 	}
				 	 File gpxfile = new File(dirFile, "test.txt");
				       // FileWriter writer = new FileWriter(gpxfile);
				    gpxfile.createNewFile();
				    
					String path = Environment.getExternalStorageDirectory().toString();
					java.io.File fileContent = new java.io.File(path+"/proteTest");
					java.io.File[] files = fileContent.listFiles();
					Log.e("fileLength", files.length+"");
					if(files!=null){
					 	Log.d("upload service","file is not null");
						for(java.io.File aFile : files)
						{
						 	Log.d("upload service","loop entered");
							FileContent medaiContent = new FileContent("", aFile);
							Log.e("medaiContent", medaiContent+"");
							com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
							body.setTitle(fileContent.getName());
							 //File file = mService.files().insert(body, medaiContent).execute();
							com.google.api.services.drive.model.File file = mService.files().insert(body, medaiContent).execute();
							Log.e("UPploaded",file+"");
							if(file!=null){
								SharedPreferences sharedPref =getSharedPreferences("eventGoogle",Context.MODE_PRIVATE);
								String googleId = sharedPref.getString(PREF_ACCOUNT_NAME, "");
							 	
							}
							else{
							 	Log.d("upload service","not uploaded");
								//stopSelf();
								Log.e("NtUPploaded","Notuloaded");
							}
						}
					}
					
					
				} catch (UserRecoverableAuthIOException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
					Log.e("ex uploading file as userRecver",e.toString());
					e.printStackTrace();
				} catch (Exception e) {
					Log.e("ex uploading file as",e.toString());
					e.printStackTrace();
				}
    		}
    	});
    	t.start();
    	
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .build();
      }
}



