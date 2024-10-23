package com.example.threadsdecomunicaciOscar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.widget.Toast;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import java.net.*;
import java.io.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

class GetIp implements Runnable{

    Context context;

    public GetIp(Context context){
        this.context = context;
    }


    @Override
    public void run() {
        StringBuilder result = new StringBuilder(); 
        // Tasques en background (xarxa)
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("https://api.myip.com/");
            urlConnection    = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("GetIp", "Response code: " + responseCode);
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }   
        final String finalResult = result.toString();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(finalResult);
                    String ip = jsonObject.getString("ip");
                    String country = jsonObject.getString("country");
                    String text = "IP: " + ip + "\nCountry: " + country;
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
 
            }
        });
    }
}

public class MainActivity extends AppCompatActivity {

    ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        executor = Executors.newSingleThreadExecutor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetIp getIp = new GetIp(MainActivity.this);
                executor.execute(getIp);
            }
        });
    }
}
