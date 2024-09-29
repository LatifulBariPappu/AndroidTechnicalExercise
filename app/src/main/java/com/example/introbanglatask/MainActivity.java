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
import android.widget.Toast;

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
    private static final String cache_name="cache";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        randomImageView=findViewById(R.id.randomImageView);
        Button loadButton=findViewById(R.id.loadImageButton);
        sharedPreferences=getSharedPreferences(pref_name,MODE_PRIVATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkAvailability()){
                    loadRandomImage();
                }else{
                    Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
                }
            }
        });

        String cacheImage=sharedPreferences.getString(cache_name, "");

        byte[] decodedbytes=Base64.decode(cacheImage,Base64.DEFAULT);
        Bitmap bitmapDecoded=BitmapFactory.decodeByteArray(decodedbytes,0,decodedbytes.length);
        randomImageView.setImageBitmap(bitmapDecoded);

    }

    private void loadRandomImage(){
        try {
            URL url=new URL("https://picsum.photos/300/300");
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream=httpURLConnection.getInputStream();
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            cacheImages(bitmap);
            randomImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void cacheImages(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        String encoded=Base64.encodeToString(bytes,Base64.DEFAULT);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(cache_name,encoded);
        editor.apply();

    }

    private boolean networkAvailability() {
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null&&networkInfo.isConnected();

    }
}