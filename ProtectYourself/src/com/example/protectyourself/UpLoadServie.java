package com.example.protectyourself;

import java.util.Arrays;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class UpLoadServie extends Service {
	static final int 				REQUEST_ACCOUNT_PICKER = 1;
	//static final int 				REQUEST_AUTHORIZATION = 2;
	static final int 				RESULT_STORE_FILE = 4;
	
	
	private static Drive 			mService;
	String path;
	SharedPreferences prefer;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	public GoogleAccountCredential credential;
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences settings = this.getSharedPreferences("eventGoogle", Context.MODE_PRIVATE);
		credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE)).setBackOff(new ExponentialBackOff())
				.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		Log.e("credential", credential.toString()+"");
		mService = getDriveService(credential);
		Log.e("mService", mService.toString()+"");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	  	saveFileToDrive();
	  	Log.e("upload service","called");
		return super.onStartCommand(intent, flags, startId);
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
				 	Log.d("upload service","save to file started");
					// Create URI from real path
					String path = Environment.getExternalStorageDirectory().toString();
					java.io.File fileContent = new java.io.File(path+"/ProtectData");
					Log.e("full path",fileContent.getPath());
					Log.e("dic","hi:"+fileContent.isDirectory());
					Log.e("file","hi:"+fileContent.isFile());
					Log.e("xsists","hi:"+fileContent.exists());
					
					java.io.File[] files = fileContent.listFiles();
					Log.e("fileLength", files.length+"");
					if(files!=null){
					 	Log.d("upload service","file is not null");
						for(java.io.File aFile : files)
						{
						 	Log.d("upload service","loop entered");
							FileContent medaiContent = new FileContent("", aFile);
							Log.e("medaiContent", medaiContent+"");
							File body = new File();
							body.setTitle(fileContent.getName());
							//com.google.api.services.drive.Drive.Files f1 = mService.files();
							//com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body,medaiContent);
							 File file = mService.files().insert(body, medaiContent).execute();
							//final File file = i1.execute();
						
							Log.e("UPploaded",file+"");
							if(file!=null){
								//stopSelf();
							 	Log.d("upload service","file is not null");
							}
							else{
							 	Log.d("upload service","not uploaded");
								//stopSelf();
								Log.e("NtUPploaded","Notuloaded");
							}
						}
					}
					stopSelf();
					
				} catch (UserRecoverableAuthIOException e) {
					
					Log.d("upload service","exception auth");
					Log.e("ex uploading file as userRecver",e.toString());
					//saveFileToDrive();
					e.printStackTrace();
				} catch (Exception e) {
				 	Log.d("upload service","other exceptions");
					Log.e("ex uploading file as",e.toString());
					e.printStackTrace();
				}
    		}
    	});
    	t.start();
    	
	}
	
	@Override
	public void onDestroy() {
		String path = Environment.getExternalStorageDirectory().toString();
		java.io.File fileCon = new java.io.File(path+"/ProtectData/");
		if(fileCon.isDirectory()){
			String[] children = fileCon.list();
			for(int i = 0;i<children.length;i++){
				new java.io.File(fileCon,children[i]).delete();
			}
		}
		fileCon.delete();
		Log.i("hello Binod", "Service onDestroy");
	}
	
	
}

