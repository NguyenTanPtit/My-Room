package com.example.myroom;

public interface OnCheckPhoneNumberListener {
    void onSuccess();
    void onFailure(String error);
    void onVerifyPhoneNumber();
}
