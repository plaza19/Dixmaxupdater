package com.example.dixmaxupdater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView txtlog;
    Button btnupdate;
    Updater updater;
    ProgressBar progressbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtlog = findViewById(R.id.txtlog);
        btnupdate = findViewById(R.id.btnupdate);
        progressbar = findViewById(R.id.downloadprogress);

        updater = new Updater(txtlog, btnupdate, progressbar, this);



        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(updater);
                t.start();
            }
        });



    }
}

