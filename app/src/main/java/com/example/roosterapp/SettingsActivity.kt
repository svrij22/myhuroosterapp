package com.example.roosterapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*
import android.R.id.edit
import android.content.SharedPreferences
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settings = getSharedPreferences("UserInfo", 0)
        editText.setText(settings.getString("klascode", "")!!.toString())

        opsButton.setOnClickListener{
            val editor = settings.edit()
            editor.putString("klascode", editText.getText().toString())
            editor.apply()
        }

        refButton.setOnClickListener{view ->
            Snackbar.make(view, "ICS bestand downloaden", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            //Setup rooster
            val roosterUrl = "https://mijnrooster.hu.nl/ical?5e455329&group=false&eu=U1REXDE3NTg5MjU=&h=rhtnwFUIE9LciH4W0YOh__XvdWknZdR717tjNz_BTEU="
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
}
