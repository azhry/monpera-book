package com.example.acer.monperabook;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Azhary Arliansyah on 19/01/2018.
 */

public class MuseumProfileActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private TextView museumProfile,
                    museumHistory,
                    museumSchedule,
                    museumTicketPrices,
                    profileHeader,
                    historyHeader,
                    scheduleHeader,
                    ticketPricesHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_profile);
        context = this;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));

        museumProfile = (TextView)findViewById(R.id.museum_profile);
        museumHistory = (TextView)findViewById(R.id.museum_history);
        museumSchedule = (TextView)findViewById(R.id.museum_schedule);
        museumTicketPrices = (TextView)findViewById(R.id.museum_ticket_prices);

        museumProfile.setText(R.string.museum_profile);
        museumHistory.setText(R.string.museum_history);

    }

}
