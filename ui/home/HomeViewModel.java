package com.example.afinal.ui.home;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Date;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        mText = new MutableLiveData<>();
        if(currentHourIn24Format >= 0 && currentHourIn24Format < 12){
            mText.setValue("Olá!");
        }else if(currentHourIn24Format >= 12 && currentHourIn24Format < 18){
            mText.setValue("Boa tarde!");
        }else if(currentHourIn24Format >= 18 && currentHourIn24Format < 24){
            mText.setValue("Boa noite!");
        }

    }

    public LiveData<String> getText() {
        return mText;
    }

}


