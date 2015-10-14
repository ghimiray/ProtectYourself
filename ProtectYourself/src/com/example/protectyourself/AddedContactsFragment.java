package com.example.protectyourself;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nirhart.parallaxscroll.views.ParallaxListView;

public class AddedContactsFragment extends Fragment {
	public AddedContactsFragment mContext = this;
	public ArrayList<String> nameArr;
	
	public ArrayList<String> numbers;
	public EditText searchText;
	private ListAdapterForAdded ad;
	public Cursor cursor;
		public LatLng userLocation;
	public LocationListener locatlist;
	public ArrayList<ListModel> models;
	public List<Contact> contactsFr;
	
	public static AddedContactsFragment newInstance(String content) {
        AddedContactsFragment fragment = new AddedContactsFragment();
        return fragment;
    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.added_contacts,
    	        container, false);
    
    	contactsFr = ((MainActivity)getActivity()).contacts;

	if(!contactsFr.isEmpty()){

		
		models = new ArrayList<ListModel>();
		
		for (Contact cn: contactsFr) {
			models.add(new ListModel(cn.getName(), cn.getPhoneNumber()));
		}

		ad = new ListAdapterForAdded(mContext.getActivity(), models);

		
		ParallaxListView listDiscovery = (ParallaxListView) view.findViewById(R.id.listDiscovery);
		listDiscovery.setAdapter(ad);
		}else{
			Toast.makeText(getActivity().getBaseContext(), "There are no added Contacts", Toast.LENGTH_LONG).show();
		}
//		// listDiscovery.setOnItemClickListener(new OnItemClickListener() {
//		// @SuppressLint("NewApi")
//		// @Override
//		// public void onItemClick(AdapterView<?> parent, View v,
//		// int position, long id) {
//		//
//		// }
//		// });

				
	 	    return view;
	}



	
}
	