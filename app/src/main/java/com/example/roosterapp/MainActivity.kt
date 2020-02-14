package com.example.roosterapp

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import biweekly.component.VEvent
import biweekly.Biweekly
import biweekly.ICalendar
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.io.File
import android.os.StrictMode
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context.MODE_PRIVATE
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.io.OutputStreamWriter
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.activity_settings.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val settings = getSharedPreferences("UserInfo", 0)

        //File setup
        val fileName = "rooster.ics"
        val path = applicationContext.getFilesDir();
        val nFile = File(path, fileName)

        //Setup rooster
        val roosterUrl = "https://mijnrooster.hu.nl/ical?5e455329&group=false&eu=U1REXDE3NTg5MjU=&h=rhtnwFUIE9LciH4W0YOh__XvdWknZdR717tjNz_BTEU="
        val rooster = DownloadRooster(roosterUrl, applicationContext)

        firstButton.setOnClickListener{ view ->

            Snackbar.make(view, "Rooster aan het laden", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            //Set datum en display datum
            rooster.setWeek()
            textView.setText(rooster.formatBeginEind());

            //klascode
            val klascode = settings.getString("klascode", "")!!.toString()

            //Read file
            if (nFile.isFile()){
                //Parse ical
                val myRooster = Biweekly.parse(nFile).first()
                val myEvents = myRooster.getEvents()

                //Laad het rooster
                rooster.LaadRooster(myEvents)
                rooster.setWeek()

                //Events van deze week
                var VDezeWeek = rooster.dezeWeek(myEvents,klascode)
                VDezeWeek = rooster.voegHeadersToe(VDezeWeek);

                //Set in ListView
                var listView = findViewById<ListView>(R.id.firstListView)
                val adapter = RoosterAdapter(this, R.layout.adapter_view_layout, R.layout.adapter_view_layout2, VDezeWeek)
                listView.adapter = adapter

            }else{
                textView.text = "Download uw rooster eerst."
            }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            rooster.switchWeek(-7);
            firstButton.callOnClick();
        }
        button3.setOnClickListener {
            rooster.switchWeek(7);
            firstButton.callOnClick();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
