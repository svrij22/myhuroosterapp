package com.example.roosterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import biweekly.component.VEvent;

public class RoosterAdapter extends ArrayAdapter<VEvent> {

    private static final String TAG = "RoosterAdapter";

    private Context mContext;
    private int mResource;
    private int mResource2;

    public RoosterAdapter(Context context, int resource,int resource2, ArrayList<VEvent> events){
        super(context, resource, events);
        mContext = context;
        mResource = resource;
        mResource2 = resource2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Divider?
        int cResource = (!getItem(position).getDescription().getValue().contains("divider")) ? mResource : mResource2;

        LayoutInflater  inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(cResource, parent, false);


        if (cResource == mResource){
            //Krijg informatie van vak
            long dataRead1 = getItem(position).getDateStart().getValue().getTime();
            Date date1 = new Date(dataRead1);
            String startDate = String.format("%d:%d",date1.getHours(),date1.getMinutes());

            long dataRead2 = getItem(position).getDateEnd().getValue().getTime();
            Date date2 = new Date(dataRead2);
            String endDateDate = String.format("%d:%d",date2.getHours(),date2.getMinutes());
            startDate += "\n" + endDateDate;

            //Create date
            Format f = new SimpleDateFormat("dd-MMM");
            String date4 = f.format(date1);

            //Get summary and location
            String data2 = getItem(position).getSummary().getValue().toString().replace(" - ","\n");
            String data3 = getItem(position).getLocation().getValue().toString();

            //Put data in textviews
            TextView tvData1 = (TextView) convertView.findViewById(R.id.textView1);
            TextView tvData2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView tvData3 = (TextView) convertView.findViewById(R.id.textView3);
            TextView tvData4 = (TextView) convertView.findViewById(R.id.textView4);
            tvData1.setText(startDate);
            tvData2.setText(data2);
            tvData3.setText(data3);
            tvData4.setText(date4);
        }else{
            //Get summary
            String data5 = getItem(position).getSummary().getValue();

            //
            TextView tvData1 = (TextView) convertView.findViewById(R.id.textView2);
            tvData1.setText(data5);
        }

        return convertView;
    }
}
