package tecnodart.com.offlineonline;


import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;
import java.util.List;


public class MinimumSupportPrice extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    String  TAG="msp", address="", sms;
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
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
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
        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        Log.d(TAG,"view created");
        return v;
    }
    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.READ_SMS);
        int getLocation=ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if(getLocation!=PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
        if(isOnline()) {
            Log.d(TAG,"online execution");
            Toast.makeText(this.getContext(), "You are connected to Internet", Toast.LENGTH_SHORT).show();

            mCommodityRef = mPriceRef.child(commodity);
            mCommodityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    msp = dataSnapshot.getValue(Integer.class);
                    Log.d(TAG, Integer.toString(msp));
                    mspDisplay.setText("₹" + msp + "/quintal");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // Prompt the user for permission.
            getLocationPermission();
            getDeviceLocation();
            if (latitude > 28.00) {
                mRegionRef = mAddressRef.child("north");
            } else if (latitude > 14.00) {
                if (longitude < 80.00) {
                    mRegionRef = mAddressRef.child("west");
                } else {
                    mRegionRef = mAddressRef.child("east");
                }
            } else {
                mRegionRef = mAddressRef.child("south");
            }
            mRegionRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    address = dataSnapshot.getValue(String.class);
                    addressDisplay.setText(address);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Log.d(TAG,"else executed");
            // Prompt the user for permission.
            getLocationPermission();
            getLocationOffline();
            sms=smsCreator(commodity,latitude,longitude);
            sendSMS("7028499108", sms);
            Log.d(TAG,"control back in else");
            Toast.makeText(this.getContext(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }
    //---sends an SMS message to another device---
    @SuppressWarnings("deprecation")
    private void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("message",message);
       // Log.v("i",Integer.toString(i));
        Log.d(TAG,"sendSMS executed");
        PendingIntent pi = PendingIntent.getActivity(this.getContext(), 0,
                new Intent(this.getContext(),Dummy.class), 0);

        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber, null, message, pi, null);


    }
    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    private void getLocationOffline()
    {
        Log.d(TAG,"getLocationOffline exectued!");
        LocationManager lm;
        Log.d(TAG,"#1");
        lm = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG,"#2");
        Location net_loc = null;
        Log.d(TAG,"#3");
        try {
            Log.d(TAG,"#4");
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(net_loc==null)
            {
                Log.d(TAG,"net_loc is null");
            }
            Log.d(TAG,"#5");
            latitude=net_loc.getLatitude();
            Log.d(TAG,"#6");
            longitude=net_loc.getLongitude();
            Log.d(TAG,"#7");
        }catch(SecurityException s)
        {
            Log.d(TAG,"Permission Denied");
        }
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.d(TAG,"getDeviceLocation() executed");

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
        Log.d(TAG,"getLocationPermission executed");
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
    private String smsCreator(String commodity,Double latitude,Double longitude)
    {
        String sms;
        sms="#ubi#"+commodity+"#"+latitude+"#"+longitude;
        return sms;
    }


}