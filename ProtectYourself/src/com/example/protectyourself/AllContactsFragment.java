package com.example.protectyourself;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.nirhart.parallaxscroll.views.ParallaxListView;

public class AllContactsFragment extends Fragment {
	public AllContactsFragment mContext = this;
	public ArrayList<String> nameArr;
	private ArrayList<String> numberArr;
	public ArrayList<String> numbers;
	public EditText searchText;
	private ListAdapter ad;
	public Cursor cursor;
	public LatLng userLocation;
	public LocationListener locatlist;
	public ArrayList<ListModel> models;
	MyBoardcastReceiver keyReceiver;
	IntentFilter intentFilter;
	
	public static AllContactsFragment newInstance(String content) {
        AllContactsFragment fragment = new AllContactsFragment();
        return fragment;
    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.all_contacts, container, false);
		numbers = new ArrayList<String>();
		PackageManager pm = AllContactsFragment.this.getActivity().getPackageManager();
		ComponentName componentName = new ComponentName(AllContactsFragment.this.getActivity(),MyBoardcastReceiver.class);
		pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
		keyReceiver = new MyBoardcastReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
//		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

		
		nameArr = new ArrayList<String>();
		numberArr = new ArrayList<String>();
		ContentResolver cr = mContext.getActivity().getContentResolver(); // Activity/Application
															// android.content.Context
		cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
				null, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						String contactNumber = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						String name = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

						numberArr.add(contactNumber);
						nameArr.add(name);
						break;
					}
					pCur.close();
				}

			} while (cursor.moveToNext());
		}
		
		models = new ArrayList<ListModel>();
		for (int i = 0; i < numberArr.size(); i++) {
			models.add(new ListModel(nameArr.get(i), numberArr.get(i)));
		}

		ad = new ListAdapter(mContext.getActivity(), models);

		ParallaxListView listDiscovery = (ParallaxListView) view.findViewById(R.id.listDiscovery);
		listDiscovery.setAdapter(ad);
		// listDiscovery.setOnItemClickListener(new OnItemClickListener() {
		// @SuppressLint("NewApi")
		// @Override
		// public void onItemClick(AdapterView<?> parent, View v,
		// int position, long id) {
		//
		// }
		// });

		searchText = (EditText) view.findViewById(R.id.searchText);
		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				int textlength = cs.length();
				ArrayList<ListModel> tempArrayList = new ArrayList<ListModel>();
				for (ListModel c : models) {
					if (textlength <= c.getName().length()) {
						if (c.getName().toLowerCase()
								.contains(cs.toString().toLowerCase())) {
							tempArrayList.add(c);
						}
					}
				}
				ParallaxListView listDiscovery = (ParallaxListView) getView().findViewById(R.id.listDiscovery);
				ad = new ListAdapter(AllContactsFragment.this.getActivity(), tempArrayList);
				listDiscovery.setAdapter(ad);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

 	    return view;
	}
	
	@Override
	public void onResume() {
		getActivity().registerReceiver(keyReceiver, intentFilter);
		super.onResume();
	}
	
	
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(keyReceiver);
		super.onPause();
	}

	}
	