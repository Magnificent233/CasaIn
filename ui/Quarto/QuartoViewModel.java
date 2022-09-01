package com.example.afinal.ui.Quarto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuartoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QuartoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Quarto");
    }

    public LiveData<String> getText() {
        return mText;
    }
}