package com.example.dixmaxupdater;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Updater extends AppCompatActivity implements  Runnable {

    TextView txtlog;
    Button btnupdate;
    ProgressBar progressbar;
    String code = "";
    String id = "";
    String hash = "";
    String download_url = "";
    Context context;

    public Updater(TextView txtlog, Button btnupdate, ProgressBar progressbar, Context context) {
        this.txtlog = txtlog;
        this.btnupdate = btnupdate;
        this.progressbar = progressbar;
        this.context = context;
    }



    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("https://blog.peegshare.com/posts/actualizar-dixmax-en-android-2").get();

            Elements elements = doc.select("a");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(Element e : elements) {
                        if (e.attr("href").matches(".*peegshare.com/drive.*")) {
                            txtlog.append("\nConnecting to url:\n" + e.attr("href") + "\n");
                            code =  e.attr("href").substring(e.attr("href").lastIndexOf('/') +1, e.attr("href").length());
                            //txtlog.append(code);


                        }

                    }

                    Thread aux = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url("https://peegshare.com/secure/drive/shareable-links/" + code + "?withEntries=true")
                                    .build();

                            try (Response response = client.newCall(request).execute()) {
                                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                                Headers responseHeaders = response.headers();
                                for (int i = 0; i < responseHeaders.size(); i++) {
                                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }

                                String jsonString = response.body().string();

                                JSONObject obj = new JSONObject(jsonString);

                                id = obj.getJSONObject("link").getString("id");

                                Log.d("ID", id);

                                hash = obj.getJSONObject("link").getJSONObject("entry").getString("hash");

                                Log.d("HASH", hash);

                                download_url = "https://peegshare.com/secure/uploads/download?hashes=" + hash + "&shareable_link=" + id;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        txtlog.append("\nCreated download url\n " + download_url);
                                    }
                                });

                                AsyncDownloader downloader = new AsyncDownloader(progressbar, context);
                                downloader.doInBackground();

                            } catch (IOException | JSONException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    });
                    aux.start();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
