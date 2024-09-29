package com.example.introbanglatask;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    ImageView randomImageView;
    SharedPreferences sharedPreferences;
    private static final String pref_name="prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        randomImageView=findViewById(R.id.randomImageView);
        Button loadButton=findViewById(R.id.loadImageButton);
        TextView textView=findViewById(R.id.textView);
        sharedPreferences=getSharedPreferences(pref_name,MODE_PRIVATE);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkAvailability()){
                    textView.setVisibility(View.GONE);
                    loadRandomImage();
                }else{
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No network.Please check your connection");
                }
            }
        });
    }


    private void loadRandomImage(){
        try {
            URL url=new URL("https://picsum.photos/300/300");
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream=httpURLConnection.getInputStream();
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            randomImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void cacheImages(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,1,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString();
    }

    private boolean networkAvailability() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null&&networkInfo.isConnected();

    }
}