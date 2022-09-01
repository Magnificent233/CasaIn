package com.example.afinal.ui.Sala;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalashowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SalashowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Sala");
    }

    public LiveData<String> getText() {
        return mText;
    }
}