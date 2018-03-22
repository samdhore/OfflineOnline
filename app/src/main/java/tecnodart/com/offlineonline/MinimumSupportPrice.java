package tecnodart.com.offlineonline;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MinimumSupportPrice extends Fragment implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    String  TAG="msp";
    TextView mspDisplay;
    int msp;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mMSPRef = mRootRef.child("msp");
    DatabaseReference mCommodityRef;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_minimum_support_price , container , false) ;
        spinner=v.findViewById(R.id.commoditySpinner);
        spinner.setOnItemSelectedListener(this);
        mspDisplay=v.findViewById(R.id.mspDisplay);
        mspDisplay.setText("");
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
        mCommodityRef=mMSPRef.child(commodity);
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

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}