package com.example.afinal.ui.Quarto;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.afinal.MainActivity;
import com.example.afinal.R;
import com.example.afinal.ui.home.HomeFragment;
import com.github.anastr.speedviewlib.RaySpeedometer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.api.ResourceDescriptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;


public class QuartoFragment extends Fragment {
       Button btn;
       private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
       private DatabaseReference sensors = root.child("CASA");
       DatabaseReference myRef;
       DatabaseReference myRef1;
       DatabaseReference myRef2;
       private ToggleButton toggle1;
       private Button history;
       private float temp;
       private float humid;

       private RaySpeedometer tempView;
       private final Handler hand = new Handler();
       private Runnable timer;

       private RaySpeedometer humidView;
       private Runnable timer1;

       private QuartoViewModel QuartoViewModel;

       public QuartoFragment() {

       }

       @Nullable
       @Override
       public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
              View v = inflater.inflate(R.layout.fragment_quarto, container, false);

              //btn = (Button) v.findViewById(R.id.btn);
              Button history = (Button) v.findViewById(R.id.history);
              ToggleButton toggle1 = (ToggleButton) v.findViewById(R.id.toggle1);
              Button btn = (Button) v.findViewById(R.id.btn);
              toggle1.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                            myRef = FirebaseDatabase.getInstance().getReference("CASA/LED");
                            if (toggle1.isChecked()) {
                                   myRef.setValue(1);
                            } else {
                                   myRef.setValue(0);
                            }

                     }
              });

              history.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                            Intent in = new Intent(getActivity(), DatabaseData.class);
                            startActivity(in);
                     }
              });

              myRef1 = FirebaseDatabase.getInstance().getReference("CASA/SENSOR_MOV/VALOR");

              myRef1.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (Objects.requireNonNull(snapshot.getValue()).hashCode() == '1') {
                                   btn.setBackgroundColor(Color.GREEN);
                            } else {
                                   btn.setBackgroundColor(Color.RED);
                            }
                     }


                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
              });

              myRef2 = FirebaseDatabase.getInstance().getReference("CASA/LED");

              myRef2.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (Objects.requireNonNull(snapshot.getValue()).hashCode() == 1) {
                                   toggle1.setChecked(true);
                            } else {
                                   toggle1.setChecked(false);
                            }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
              });


              //LED();
              //PIR();

              return v;

       }


       // public void LED() {


       //}

       /*public void PIR() {

              myRef1 = FirebaseDatabase.getInstance().getReference("CASA/SENSOR_MOV/VALOR");

              myRef1.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (Objects.requireNonNull(snapshot.getValue()).hashCode() == '1') {
                                   btn.setBackgroundColor(getResources().getColor(R.color.green));
                            } else{
                                   btn.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                     }


                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
              });
       }*/


       @Override
       public void onStart() {
              super.onStart();
              View contents = getView();

              tempView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer);
              tempView.setWithTremble(false);

              humidView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer2);
              humidView.setWithTremble(false);

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
       }

       // Used for constantly updating the Temperature and Humidity data
       @Override
       public void onResume() {
              super.onResume();
              timer = new Runnable() {
                     @Override
                     public void run() {
                            tempView.realSpeedTo(temp);
                            hand.postDelayed(this, 1000);
                     }
              };
              hand.postDelayed(timer, 1000);

              timer1 = new Runnable() {
                     @Override
                     public void run() {
                            humidView.realSpeedTo(humid);
                            hand.postDelayed(this, 1000);
                     }
              };
              hand.postDelayed(timer1, 1000);
       }

       // Removing the handler which constantly updates Temp and Humid Data
       @Override
       public void onPause() {
              hand.removeCallbacks(timer);
              hand.removeCallbacks(timer1);
              super.onPause();
       }
}

