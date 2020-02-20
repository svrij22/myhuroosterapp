package com.example.roosterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.io.File
import android.text.Editable
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi


class SettingsActivity : AppCompatActivity() {

    var ICSUrl = ""
    var fileHTML = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Get klascode
        val settings = getSharedPreferences("UserInfo", 0)
        editText.setText(settings.getString("klascode", "")!!.toString())
        editText3.setText(settings.getString("icsurl", "")!!.toString())
        editText2.setText(settings.getString("ww", "")!!.toString())
        editText4.setText(settings.getString("email", "")!!.toString())

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
                    //System.out.println(message);
                    result.confirm()
                    return true
                }
            })
            webView.webChromeClient = WebChromeClient()
        };

        //Navigate naar rooster
        webView.loadUrl("https://mijnrooster.hu.nl/schedule?requireLogin=true");
        webView.visibility = WebView.GONE

        //get ICS File knop
        icsButton.setOnClickListener {
            //Lees HTML af van pagina
            getHTML()
            //Set status
            textView8.text = "Status:" + checkIngelogd()
            if (checkIngelogd() == "Ingelogd" || checkIngelogd() == "Link opgehaald"){
                getICSURL()
                editText3.text = ICSUrl.toEditable()
            }
        }

        //Opslaan knop
        opsButton.setOnClickListener {
            val editor = settings.edit()
            editor.putString("klascode", editText.getText().toString())
            editor.putString("icsurl", editText3.getText().toString())
            editor.putString("ww", editText2.getText().toString())
            editor.putString("email", editText4.getText().toString())
            editor.apply()
        }

        //Log uit knop
        buttonlu.setOnClickListener {
            webView.clearHistory();
            webView.clearFormData();
            webView.clearCache(true);
            CookieManager.getInstance().removeAllCookie();
            webView.loadUrl("https://mijnrooster.hu.nl/schedule?requireLogin=true");
        }

        //Login knop
        button4.setOnClickListener {
            //webView.visibility = WebView.VISIBLE

            //Probeer in te loggen
            if (checkIngelogd() == "Inloggen vereist")
                setUserDataWebView()
            //Lees HTML af van pagina
            getHTML()
            //Set status
            textView8.text = "Status:" + checkIngelogd()
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

    // 2 Javascript called functies
    // Een is voor HTML en een is om de ICS file url te setten.

    @JavascriptInterface
    fun onData(value: String) {
        println("Event Triggered")
        ICSUrl = value
    }

    @JavascriptInterface
    fun onGetHTML(value: String) {
        println("Event Triggered")
        fileHTML = value
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

    fun setUserDataWebView(){
        val script = "javascript:" +
                "document.getElementById('userNameInput').value = '" + editText4.text + "';" +
                "document.getElementById('passwordInput').value = '" + editText2.text + "';" +
                "document.getElementById('submitButton').click();"
        webView.loadUrl(script)
    }

    fun getHTML(){
        //Voer een javascript functie uit die via een JavaScriptInterface de HTML meegeeft
        val script = "javascript:" +
                "android.onGetHTML(document.body.innerHTML);"
        webView.loadUrl(script)
    }

    fun checkIngelogd(): String{
        //Check of op de rooster pagina of dat de link al in opgehaald.
        if (fileHTML.contains("Uitloggen"))
                return "Ingelogd"
        if (fileHTML.contains("hu.nl/ical"))
            return "Link opgehaald"
        if (fileHTML.contains("loginForm"))
            return "Inloggen vereist"
        if (fileHTML.contains("errorText"))
            return "Error bij inloggen"

        return  "Uitgelogd"
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
