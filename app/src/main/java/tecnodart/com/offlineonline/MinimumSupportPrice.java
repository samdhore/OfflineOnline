package tecnodart.com.offlineonline;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;



public class MinimumSupportPrice extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    String  TAG="msp", address="";
    TextView mspDisplay, addressDisplay;
    int msp;
    double latitude,longitude;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mMSPRef = mRootRef.child("msp");
    DatabaseReference mPriceRef=mMSPRef.child("price");
    DatabaseReference mAddressRef=mMSPRef.child("address");
    DatabaseReference mRegionRef;
    DatabaseReference mCommodityRef;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        }
        View v = inflater.inflate(R.layout.activity_minimum_support_price , container , false) ;
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity()
        );
        spinner=v.findViewById(R.id.commoditySpinner);
        spinner.setOnItemSelectedListener(this);
        mspDisplay=v.findViewById(R.id.mspDisplay);
        mspDisplay.setText("");
        addressDisplay=v.findViewById(R.id.addressTextView);
        addressDisplay.setText("");
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(),
                R.array.commodity_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        Log.d(TAG,"view created");
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Minimum Support Price");
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.d(TAG, "item selected");
        String commodity=(String)parent.getItemAtPosition(pos);
        mCommodityRef=mPriceRef.child(commodity);
        mCommodityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                msp=dataSnapshot.getValue(Integer.class);
                Log.d(TAG, Integer.toString(msp));
                mspDisplay.setText("₹"+msp+"/quintal");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Prompt the user for permission.
        getLocationPermission();
        getDeviceLocation();
        if(latitude>28.00)
        {
            mRegionRef=mAddressRef.child("north");
        }
        else if(latitude>14.00)
        {
            if(longitude<80.00)
            {
                mRegionRef=mAddressRef.child("west");
            }
            else
            {
                mRegionRef=mAddressRef.child("east");
            }
        }
        else
        {
            mRegionRef=mAddressRef.child("south");
        }
        mRegionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                address=dataSnapshot.getValue(String.class);
                addressDisplay.setText(address);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            latitude=mLastKnownLocation.getLatitude();
                            longitude=mLastKnownLocation.getLongitude();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");


                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


}