package com.example.protectyourself;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class GoogleDriveActivity extends Activity {

	static final int 				REQUEST_ACCOUNT_PICKER = 1;
	static final int 				REQUEST_AUTHORIZATION = 2;
	static final int 				RESULT_STORE_FILE = 4;
	private static Drive 			mService;
	String path;
	SharedPreferences prefer;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	public GoogleAccountCredential credential;
	//private static final String[] SCOPES = {"com.google"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_drive);
		
		Button button = (Button) findViewById(R.id.button1);
		SharedPreferences settings = this.getSharedPreferences("eventGoogle", Context.MODE_PRIVATE);
		credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE)).setBackOff(new ExponentialBackOff())
				.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

		mService = getDriveService(credential);
		/*mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
		Intent accountPicker = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
		startActivityForResult(accountPicker, REQUEST_ACCOUNT_PICKER);*/  
		
		//startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		
		
		
		try {
			
	        button.setOnClickListener(new View.OnClickListener() 
	        {
	            public void onClick(View v) 
	            {
	            	startActivityForResult(getIntent(), RESULT_STORE_FILE);
	            }
	        });
	        button.performClick();
			
		} catch (Exception e) {
			Log.e("print Exception", e.toString());
			e.printStackTrace();
		}
		
	}
	
	@Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) 
	{
		switch (requestCode) 
		{
			
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					//mService = getDriveService(credential);
					//account already picked
					//String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					//Log.e("accountName", accountName+"");
				} else {
					//startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
				break;
			case RESULT_STORE_FILE:
				// Save the file to Google Drive
        		saveFileToDrive();
				break;
		}
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .build();
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
					// Create URI from real path
					String path = Environment.getExternalStorageDirectory().toString();
					//String secStore = System.getenv("SECONDARY_STORAGE");
					//new File(secStore+"/ProtectData");
					java.io.File fileContent = new java.io.File(path+"/ProtectData/");
					
					Log.e("full path",fileContent.getPath());
					Log.e("dic","hi:"+fileContent.isDirectory());
					Log.e("file","hi:"+fileContent.isFile());
					Log.e("xsists","hi:"+fileContent.exists());
					
					java.io.File[] files = fileContent.listFiles();
					Log.e("fileLength", files.length+"");
					if(files!=null){
						for(java.io.File aFile : files)
						{
							FileContent medaiContent = new FileContent("", aFile);
							Log.e("medaiContent", medaiContent+"");
							File body = new File();
							body.setTitle(fileContent.getName());
							com.google.api.services.drive.Drive.Files f1 = mService.files();
							com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body,medaiContent);
							final File file = i1.execute();
							Log.e("UPploaded",file+"");
							if(file!=null){
								Log.e("file","uloaded");;
							}
							else{
								Log.e("NtUPploaded","Notuloaded");
							}
							
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									
									if (file != null) 
									{
										showToast("Uploaded: " + file.getTitle());
									}else{
										Log.e("file Not UpLoaded", "Try Again");
									}
								}
							});
						}
					}
					//fileContent.delete();
				} catch (UserRecoverableAuthIOException e) {
					Log.e("ex uploading file as userRecver",e.toString());
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
					e.printStackTrace();
				} catch (Exception e) {
					Log.e("ex uploading file as",e.toString());
					e.printStackTrace();
					showToast("Transfer ERROR: " + e.toString());
				}
    		}
    	});
    	t.start();
    	
	}

	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
}

