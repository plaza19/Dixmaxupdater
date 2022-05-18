package com.example.dixmaxupdater;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncDownloader extends AsyncTask<Void, Long, Boolean> {
        private final String URL = "https://peegshare.com/secure/uploads/download?hashes=Njc0N3xwYWRkaQ&shareable_link=89";
        ProgressBar progressbar;
        Context context;

        public AsyncDownloader(ProgressBar progressbar, Context context) {
            this.progressbar = progressbar;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient httpClient = new OkHttpClient();
            Call call = httpClient.newCall(new Request.Builder().url(URL).get().build());
            try {
                Response response = call.execute();
                if (response.code() == 200) {
                    InputStream inputStream = null;
                    try {
                        inputStream = response.body().byteStream();
                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();

                        publishProgress(0L, target);
                        while (true) {
                            int readed = inputStream.read(buff);
                            if(readed == -1){
                                break;
                            }
                            //write buff
                            downloaded += readed;
                            publishProgress(downloaded, target);
                            if (isCancelled()) {
                                return false;
                            }
                        }
                        return downloaded == target;
                    } catch (IOException ignore) {
                        return false;
                    } finally {
                        if (inputStream != null) {

                            try {
                                File file = new File(android.os.Environment.getExternalStorageDirectory(), "cacheFileAppeal.apk");
                                Log.d("hola", context.getExternalCacheDir().toString());
                                try (OutputStream output = new FileOutputStream(file)) {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = inputStream.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }

                                    output.flush();
                                } catch (FileNotFoundException fileNotFoundException) {
                                    fileNotFoundException.printStackTrace();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }


                                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                        .setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file),
                                                "application/vnd.android.package-archive");
                                promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(promptInstall);


                                inputStream.close();
                            } catch (FileNotFoundException e) {
                                // handle exception here
                            } catch (IOException e) {
                                // handle exception here
                            }



                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            progressbar.setMax(values[1].intValue());
            progressbar.setProgress(values[0].intValue());

            //textViewProgress.setText(String.format("%d / %d", values[0], values[1]));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //textViewStatus.setText(result ? "Downloaded" : "Failed");
        }

}
