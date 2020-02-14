package com.example.roosterapp;

import android.content.Context;
import android.widget.ListView;

import biweekly.component.VEvent;
import biweekly.property.Description;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DownloadRooster{

    private String roosterUrl;
    private List<VEvent> myEvents;
    private long firstDay,lastDay;
    private Context context;
    private String[] dagen = {"Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag", "Zondag"};
    private int minDag = -1;
    private Calendar myCal = Calendar.getInstance();

    public DownloadRooster(String Url, Context context){
        roosterUrl = Url;
        this.context = context;
    }

    public String getRooster(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(roosterUrl)
                .build();

        try{
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed to get response.";
    }

    public void LaadRooster(List<VEvent> mRooster){
        myEvents = mRooster;
    }

    public VEvent huidigeVak(List<VEvent> eventList){
        for (VEvent vak : eventList){
            long vBegin = vak.getDateStart().getValue().getTime();
            long vEind = vak.getDateEnd().getValue().getTime();
            long date = new Date().getTime();
            if (date >= vBegin && date <= vEind){
                return vak;
            }
        }
        return new VEvent();
    }

    public ArrayList<VEvent> dezeWeek(List<VEvent> eventList, String klascode){

        ArrayList<VEvent> vakWeek = new ArrayList();
        for (VEvent vak : eventList){
            long vBegin = vak.getDateStart().getValue().getTime();
            if (firstDay < vBegin && lastDay > vBegin){
                if (vak.getDescription().getValue().toString().contains(klascode)){
                    vakWeek.add(vak);
                }
            }
        }
        return vakWeek;
    }

    public void printData(VEvent vak){

        System.out.println("Lokaal " + vak.getLocation().getValue().toString());
        System.out.println("Van " + vak.getDateStart().getValue().toString());

        long var = vak.getDateStart().getValue().getTime();
        long date = new Date().getTime();

        System.out.println("Tot " + vak.getDateEnd().getValue().toString());
        String lines[] = (vak.getDescription().getValue().toString()).split("\\r?\\n");

        for (String line : lines)
        {
            if (line.contains("Doc"))
                System.out.println(line);
            if (line.contains("Gro"))
                System.out.println(line);
        }

        System.out.println("Beschrijving " + vak.getSummary().getValue().toString() + "\n");
    }

    public void setWeek(){
        Calendar cal = (Calendar)myCal.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        firstDay = cal.getTime().getTime();
        cal.add(Calendar.DAY_OF_MONTH,7);
        lastDay = cal.getTime().getTime();
    }

    public void switchWeek(int days){
        myCal.add(Calendar.DAY_OF_MONTH, days);
    }

    public String formatBeginEind(){
        Format f = new SimpleDateFormat("dd-MMM");
        return f.format(new Date(firstDay)) + " t/m " + f.format(new Date(lastDay));
    }

    public ArrayList<VEvent> voegHeadersToe(ArrayList<VEvent> eventsDW){
        ArrayList<VEvent> newEventList = new ArrayList<>();
        minDag = -1;
        for (VEvent event : eventsDW){
            int dag = event.getDateStart().getValue().getDay()-1;
            if (minDag < dag){
                VEvent dividerEvent = new VEvent();
                dividerEvent.setDescription("divider");
                dividerEvent.setSummary(dagen[dag]);
                newEventList.add(dividerEvent);
                minDag = dag;
            }
            newEventList.add(event);
        }
        return newEventList;
    }
}
