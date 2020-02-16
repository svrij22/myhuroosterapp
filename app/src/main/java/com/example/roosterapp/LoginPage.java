package com.example.roosterapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class LoginPage {
    private WebView webView;
    private Bundle savedInstanceState;

    public LoginPage(WebView webView, Bundle savedInstanceState){
        this.webView = webView;
        this.savedInstanceState = savedInstanceState;
        this.webView.loadUrl("https://mijnrooster.hu.nl/schedule?requireLogin=true");
    }
}
