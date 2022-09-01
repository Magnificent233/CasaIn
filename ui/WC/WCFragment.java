package com.example.afinal.ui.WC;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.afinal.R;
import com.github.anastr.speedviewlib.RaySpeedometer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class WCFragment extends Fragment {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference sensors = root.child("CASA");
    private float ml;
    private float mls;

    private RaySpeedometer mlView;
    private final Handler hand = new Handler();
    private Runnable timer;

    private RaySpeedometer mlsView;
    private Runnable timer1;


    public WCFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("CONSUMO DE ÁGUA- Dashboard");
        View root = inflater.inflate(R.layout.fragment_wc, container, false);

        return root;
    }

    @Override
    public void onStart(){
        super.onStart();
        View contents = getView();
        TextView total;
        TextView preco;

        total = (TextView) contents.findViewById(R.id.consumo_litros);
        preco = (TextView) contents.findViewById(R.id.preco);

        mlView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer);
        mlView.setWithTremble(false);

        mlsView = (RaySpeedometer) contents.findViewById(R.id.raySpeedometer2);
        mlsView.setWithTremble(false);

        // Getting Humidity
        sensors.child("AGUA/CONSUMO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ml = Float.parseFloat(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Getting Temperature
        sensors.child("AGUA/FLUXO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mls = Float.parseFloat(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sensors.child("AGUA/CONSUMO").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String consumo_total = dataSnapshot.getValue().toString();
                total.setText(consumo_total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // preço estimado
        sensors.child("AGUA/CONSUMO").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double valor = Double.parseDouble(total.getText().toString());
                Double valor_tarifa = valor/1000;
                if(valor_tarifa < 5 ){
                    Double sum = (valor_tarifa*0.396)+(valor_tarifa*0.254)+(valor_tarifa*1.238);
                    preco.setText(String.format("%.2f €",sum));
                }
                if(valor_tarifa > 6 && valor_tarifa <10){
                    Double sum = (valor_tarifa*0.396)+(valor_tarifa*0.254)+(valor_tarifa*1.238);
                    preco.setText(String.format("%.2f €",sum));
                }
                if(valor_tarifa > 11 && valor_tarifa <15){
                    Double sum = (valor_tarifa*0.396)+(valor_tarifa*0.254)+(valor_tarifa*1.238);
                    preco.setText(String.format("%.2f €",sum));
                }
                if(valor_tarifa >15) {
                    Double sum = (valor_tarifa * 0.396) + (valor_tarifa * 0.254) + (valor_tarifa * 1.238);
                    preco.setText(String.format("%.2f €", sum));
                }
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
                mlView.realSpeedTo(ml);
                hand.postDelayed(this,1000);
            }
        };
        hand.postDelayed(timer,1000);

        timer1 = new Runnable(){
            @Override
            public void run(){
                mlsView.realSpeedTo(mls);
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





