package com.example.afinal.ui.WC;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WCViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WCViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("WC");
    }

    public LiveData<String> getText() {
        return mText;
    }
}