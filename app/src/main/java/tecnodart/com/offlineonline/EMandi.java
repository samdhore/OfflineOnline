package tecnodart.com.offlineonline;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class EMandi extends Fragment {

    ImageView wheat , rice, sugar, kerosene;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_emandi , container , false) ;
        wheat = v.findViewById(R.id.wheat);
        rice = v.findViewById(R.id.rice);
        sugar = v.findViewById(R.id.sugar);
        kerosene = v.findViewById(R.id.kerosene);

        wheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity() , MarketPriceDetails.class);
                i.putExtra("det", "wheat");
                startActivity(i);
            }
        });
        rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity() , MarketPriceDetails.class);
                i.putExtra("det", "rice");
                startActivity(i);
            }
        });
        sugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity() , MarketPriceDetails.class);
                i.putExtra("det", "sugar");
                startActivity(i);
            }
        });
        kerosene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity() , MarketPriceDetails.class);
                i.putExtra("det", "kerosene");
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("E-Mandi");
    }

}