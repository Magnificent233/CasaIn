package com.example.afinal.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.afinal.MainActivity;
import com.example.afinal.R;
import com.github.anastr.speedviewlib.RaySpeedometer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.afinal.MainActivity;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference sensors = root.child("CASA");
    private float humid;
    private float temp;
    private float mls;
    private DatabaseReference Switch = FirebaseDatabase.getInstance().getReference("CASA/ELETRICIDADE");
    DatabaseReference myRef;
    DatabaseReference myRef1;
    DatabaseReference myRef2;
    DatabaseReference myRef3;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = v.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                }
        });

        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        myRef = FirebaseDatabase.getInstance().getReference("CASA/LED");
        myRef1 = FirebaseDatabase.getInstance().getReference("CASA/SENSOR_MOV/VALOR");
        myRef2 = FirebaseDatabase.getInstance().getReference("CASA/DHT/humidade");
        myRef3 = FirebaseDatabase.getInstance().getReference("CASA/DHT/temperatura");
        TextView textView1 = v.findViewById(R.id.text_view99);
        TextView textView2 = v.findViewById(R.id.text_view98);
        TextView textView3 = v.findViewById(R.id.text_view97);
        TextView textView4 = v.findViewById(R.id.text_view96);
        TextView textView5 = v.findViewById(R.id.text_view95);
        TextView textView6 = v.findViewById(R.id.text_view94);
        TextView textView7 = v.findViewById(R.id.text_view93);
        TextView textView8 = v.findViewById(R.id.text_view92);
        //EditText editText = v.findViewById(R.id.editTextNumber);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Caso a luz tenha sido acesa entre as 7h e as 10h avisa que foi ligada uma luz, caso contrário, aparece a mensagem que a luz se encontra desligada.
                if (currentHourIn24Format >= 7 && currentHourIn24Format < 10 && Objects.requireNonNull(snapshot.getValue()).hashCode() == 1) {
                    textView1.setText("=> A luz foi acesa!");
                } else {
                    textView1.setText("=> A luz está desligada!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sensors.child("DHT/humidade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                humid = Float.parseFloat(dataSnapshot.getValue().toString());
                if(humid >= 70){
                    textView2.setText("=> Percentagem de humidade > 70%");
                }else{
                    textView2.setText("=> Humidade OK!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sensors.child("DHT/temperatura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                temp = Float.parseFloat(dataSnapshot.getValue().toString());
                if(temp > 25){
                    textView3.setText("=> Cuidado! Temperatura do Quarto Elevada!");
                }else{
                    textView3.setText("=> Temperatura do Quarto OK!");
                }
                // Se a temperatura exceder os 25ºC entre a 0h e as 7h então o relay automaticamente corta a energia do dispositivo.
                if(temp > 25 && currentHourIn24Format > 0 && currentHourIn24Format < 7) {
                    Switch.child("STATUS").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sensors.child("ELETRICIDADE/WATTS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String wattage = dataSnapshot.getValue().toString();
                //Caso o consumo seja superior a 2000w no instante a aplicação avisa no menu principal
                if(wattage.hashCode() > 5000){
                    textView4.setText("=> Consumo de energia muito elevado!");
                }else{
                    textView4.setText("=> Consumo de energia OK!");
                }
                //Caso o consumo seja elevado entre a 0h e as 7h então o relay automaticamente corta a energia do dispositivo.
                if(wattage.hashCode() > 2000 && currentHourIn24Format > 0 && currentHourIn24Format < 7 ){
                    Switch.child("STATUS").setValue(0);
                    textView4.setText("=> A energia foi cortada devido ao consumo elevado!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sensors.child("AGUA/FLUXO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mls = Float.parseFloat(dataSnapshot.getValue().toString());
                if(mls > 10 && currentHourIn24Format > 0 && currentHourIn24Format < 7){
                    textView5.setText("=> Consumo de água alto!!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
}