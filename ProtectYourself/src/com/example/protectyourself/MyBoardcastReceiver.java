package com.example.protectyourself;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MyBoardcastReceiver extends BroadcastReceiver {
	private Integer i;
	public LatLng userLocation;
	public LocationListener locatlist;
	private String[] addedNumberRec;
	@Override
	public void onReceive(final Context context, Intent intent) {
		addedNumberRec = MainActivity.numberArr;
		SharedPreferences sp = context.getSharedPreferences("myPrefs",
				Context.MODE_PRIVATE);
		
		i = sp.getInt("count", 0);
		Editor editor = sp.edit();
		editor.putInt("count", i + 1);

		editor.commit();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				SharedPreferences sp = context.getSharedPreferences("myPrefs",
						Context.MODE_PRIVATE);

				Editor editor = sp.edit();
				editor.putInt("count", 0);

				editor.commit();

			}
		}, 1000 * 3);
		Log.e("count of i", i.toString());
		
		if (i > 50) {
			Log.e("sabeen","pradhan");
			editor.putInt("count", 0);

			editor.commit();
			
			

			LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria myCriteria = new Criteria();
			myCriteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = service.getBestProvider(myCriteria, true);
			Location location = service.getLastKnownLocation(provider);
			locatlist = new LocationListener() {

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String provider) {
					

				}

				@Override
				public void onProviderDisabled(String provider) {

						Toast.makeText(context, "Please enable your GPS for sending your location", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onLocationChanged(Location location) {

				}

			};

			service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000,
					10, locatlist);

			if (location != null) {
				Geocoder gcd = new Geocoder(context, Locale.getDefault());
				List<Address> addresses = null;
				try {
					addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String subLocality = "";
				String locality = "";
				String throughFare = "";
				String premises = "";
				
				String label = "users Location";
				
				if (addresses.size() > 0){ 
				subLocality = addresses.get(0).getSubLocality();
				
				locality = addresses.get(0).getLocality();
				throughFare = addresses.get(0).getThoroughfare();
				premises = addresses.get(0).getPremises();
				if(subLocality==null){
					subLocality = "";
				}else if(locality==null){
					locality = "";
				}else if(throughFare==null){
					throughFare = "";
				}
				label = locality+","+subLocality+","+throughFare;
				label = label.replaceAll(",,",",");
				label=label.replaceAll("\\s+","-");
				   }	
				Log.e("premises",label);
				
				String number[] = addedNumberRec;
				userLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				if(addedNumberRec.length!=0){
					Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					 // Vibrate for 500 milliseconds
					 v.vibrate(500);
					 
					 sendLocationSMS(number, location,label);
			
				}

			}


		}
	}

	public void sendLocationSMS(String phoneNumber[], Location currentLocation,String address) {
		SmsManager smsManager = SmsManager.getDefault();
		StringBuffer smsBody = new StringBuffer();
		smsBody.append("I am in Danger, please help!!! My location is  ");
		smsBody.append("http://maps.google.com/maps?q=");
		smsBody.append(currentLocation.getLatitude());
		smsBody.append(",");
		smsBody.append(currentLocation.getLongitude());
		smsBody.append("(");
		smsBody.append(address);
		
		smsBody.append(")");
		
		for (String number : phoneNumber) {
			smsManager.sendTextMessage(number, null, smsBody.toString(), null,
					null);
		}
		
	}
	



}
