package com.example.protectyourself;

import java.io.File;

import com.google.android.gms.nearby.bootstrap.request.StartScanRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver  {
  public static SharedPreferences shared;
  @Override
  public void onPasswordChanged(Context ctxt, Intent intent) {
    DevicePolicyManager mgr=
        (DevicePolicyManager)ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
    int msgId;

    if (mgr.isActivePasswordSufficient()) {
      msgId=R.string.compliant;
    }
    else {
      msgId=R.string.not_compliant;
    }

    Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onPasswordFailed(Context ctxt, Intent intent) {
	  Log.e("password","failed");
	  //File dira = new File(Environment.getExternalStorageDirectory() + "/ProtectData"); 
		/* if (dira.isDirectory()) {
			 Log.e("sabeen","sabeen");
		         String[] children = dira.list();
		         for (int j = 0; j < children.length; j++) {
		             new File(dira, children[j]).delete();
		         }
		     }


	  SharedPreferences sp = ctxt.getSharedPreferences("forCount",
				Context.MODE_PRIVATE);
		
		Integer i = sp.getInt("count", 0);
		Editor editor = sp.edit();
		editor.putInt("count", i + 1);

		editor.commit();
		Log.e("i",i.toString());
		if(i>3){
		//add the google drive and summit all the file
			intent = new Intent(ctxt.getApplicationContext(),GoogleDriveActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctxt.startActivity(intent);

			editor.putInt("count", 0);
			editor.commit();

		}
		 Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG).show();*/
	  
	  SharedPreferences sp = ctxt.getSharedPreferences("forCount",
				Context.MODE_PRIVATE);
		
		Integer i = sp.getInt("count", 0);
		Editor editor = sp.edit();
		editor.putInt("count", i + 1);
		editor.commit();
		Log.e("i",i.toString());
		if(i>3){
		//add the google drive and summit all the file
			ctxt.startService(new Intent(ctxt,UpLoadServie.class));
			editor.putInt("count", 0);
			editor.apply();
		}
		
		//deleteDirectory(dira);
  }

  @Override
  public void onPasswordSucceeded(Context ctxt, Intent intent) {
	  SharedPreferences sp = ctxt.getSharedPreferences("forCount",Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("count", 0);
		editor.commit();
		Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG).show();
  }
  public static boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      if (files == null) {
	          return true;
	      }
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
}