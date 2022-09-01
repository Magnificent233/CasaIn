package com.example.afinal.ui.Sala;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.afinal.R;
import com.github.anastr.speedviewlib.RaySpeedometer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SalashowFragment extends Fragment{
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference sensors = root.child("CASA");
    private float temp;
    private float humid;
    private Button onSwitch;
    private Button offSwitch;
    private TextView connection;
    private String switchState;
    private DatabaseReference Switch = FirebaseDatabase.getInstance().getReference("CASA/ELETRICIDADE");
    private TextView switchStateView;

    private RaySpeedometer tempView;
    private final Handler hand = new Handler();
    private Runnable timer;

    private RaySpeedometer humidView;
    private Runnable timer1;


    public SalashowFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Sala- Dashboard");

        View root = inflater.inflate(R.layout.fragment_sala, container, false);

        onSwitch = (Button)root.findViewById(R.id.button);
        onSwitch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(switchState.equals("0")){
                Switch.child("STATUS").setValue(1);
            }else {
                return;
            }
        }
    });

        connection = (TextView)root.findViewById(R.id.state);
        Switch.child("STATUS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switchState = dataSnapshot.getValue().toString();
                if (dataSnapshot.getValue().toString().equals("1")) {
                    connection.setText("Connected");
                } else {
                    connection.setText("Disconnected");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        offSwitch = (Button)root.findViewById(R.id.button2);
        offSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchState.equals("1")){
                    Switch.child("STATUS").setValue(0);
                }else {
                    return;
                }
            }
        });

     return root;
}
    @Override
    public void onStart() {
        super.onStart();
        View contents = getView();
        TextView watts;
        TextView total;
        TextView price;


        tempView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer);
        tempView.setWithTremble(false);

        humidView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer2);
        humidView.setWithTremble(false);

        watts = (TextView) contents.findViewById(R.id.wattage);
        total = (TextView) contents.findViewById(R.id.total_wattage);
        price = (TextView) contents.findViewById(R.id.estimated_price);
        switchStateView = (TextView)contents.findViewById(R.id.textView);

        // Getting Switch's State
        Switch.child("STATUS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switchState = dataSnapshot.getValue().toString();
                if(dataSnapshot.getValue().toString().equals("1")){
                    switchStateView.setText("ON");
                }else {
                    switchStateView.setText("OFF");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Getting Humidity
        sensors.child("DHT/humidade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                humid = Float.parseFloat(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Getting Temperature
        sensors.child("DHT/temperatura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                temp = Float.parseFloat(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Getting Wattage
        sensors.child("ELETRICIDADE/WATTS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String wattage = dataSnapshot.getValue().toString();
                watts.setText(wattage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    sensors.child("ELETRICIDADE/TOTAL_ENERGIA").addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String total_wattage = dataSnapshot.getValue().toString();
            total.setText(total_wattage);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    // preço estimado
    sensors.child("ELETRICIDADE/TOTAL_ENERGIA").addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Double valor = Double.parseDouble(total.getText().toString());
            Double sum = (valor/1000)*1.23*0.14;
            price.setText(String.format("%.2f €",sum));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    // Used for constantly updating the Temperature and Humidity data
    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable(){
            @Override
            public void run(){
                tempView.realSpeedTo(temp);
                hand.postDelayed(this,1000);
            }
        };
        hand.postDelayed(timer,1000);

        timer1 = new Runnable(){
            @Override
            public void run(){
                humidView.realSpeedTo(humid);
                hand.postDelayed(this,1000);
            }
        };
        hand.postDelayed(timer1,1000);
    }

    // Removing the handler which constantly updates Temp and Humid Data
    @Override
    public void onPause() {
        hand.removeCallbacks(timer);
        hand.removeCallbacks(timer1);
        super.onPause();
    }
}
