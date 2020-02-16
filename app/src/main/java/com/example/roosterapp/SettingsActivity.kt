package com.example.roosterapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import android.R.id.edit
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.webkit.JsResult
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Get klascode
        val settings = getSharedPreferences("UserInfo", 0)
        editText.setText(settings.getString("klascode", "")!!.toString())

        //Browser
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.settings.javaScriptEnabled = true
            webView.settings.supportZoom()
            webView.settings.builtInZoomControls = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webView.settings.domStorageEnabled = true;
            webView.addJavascriptInterface(this, "android")

            //nieuwe webViewClient zodat links niet in een nieuwe browser worden geopend.
            webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url);
                    return false
                }
            })
            webView.setWebChromeClient(object : WebChromeClient() {
                override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                    System.out.println(message);
                    result.confirm()
                    return true
                }
            })
            webView.webChromeClient = WebChromeClient()

        };
        val loginPage = LoginPage(webView, savedInstanceState);

        //get ICS File knop
        icsButton.setOnClickListener {
            getICSURL()
        }

        //Opslaan knop
        opsButton.setOnClickListener {
            val editor = settings.edit()
            editor.putString("klascode", editText.getText().toString())
            editor.apply()
        }

        refButton.setOnClickListener { view ->
            Snackbar.make(view, "ICS bestand downloaden", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            //Setup rooster
            val roosterUrl =
                "https://mijnrooster.hu.nl/ical?5e455329&group=false&eu=U1REXDE3NTg5MjU=&h=rhtnwFUIE9LciH4W0YOh__XvdWknZdR717tjNz_BTEU="
            val rooster = DownloadRooster(roosterUrl, applicationContext)

            //Download rooster
            val returnValue = rooster.getRooster();

            //File setup
            val fileName = "rooster.ics"
            val path = applicationContext.getFilesDir();
            val nFile = File(path, fileName)

            //Save file
            nFile.writeText(returnValue);
        }
    }

    @JavascriptInterface
    fun onData(value: String) {
        println("Event Trigerred")
        println(value)
        editText3.setText("test")
    }

    fun getICSURL(){
        val script = "javascript:" +
                "document.getElementsByClassName('GC02KLKBC4')[5].click();" +
                "document.getElementsByClassName('top-header')[0].innerHTML = '';" +
                "document.getElementsByClassName('GC02KLKBBO')[0].lastChild.click();" +
                "url = document.getElementsByClassName('gwt-TextBox-readonly')[0].value;" +
                "document.write(url);" +
                "android.onData(url);"
        webView.loadUrl(script)
    }
    
}
