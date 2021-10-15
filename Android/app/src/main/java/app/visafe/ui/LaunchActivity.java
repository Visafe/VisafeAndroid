package app.visafe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActivityNavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.visafe.R;
import app.visafe.domain.DomainVisafe;

public class LaunchActivity extends AppCompatActivity {
    public void GetUserID() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(DomainVisafe.DOMAIN_GENERATE_ID);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if  (conn.getResponseCode() == 200)
                    {
                        runOnUiThread(() -> {
                            BufferedReader br = null;
                            try {
                                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String strCurrentLine;
                            try {
                                strCurrentLine = br.readLine();
                            } catch (IOException e) {
                                strCurrentLine = "0";
                            }
                            System.out.println(strCurrentLine);
                            JSONObject reader = null;
                            String deviceId = "";
                            try {
                                reader = new JSONObject(strCurrentLine);
                                deviceId = reader.getString("deviceId");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences share = getApplicationContext().getSharedPreferences("userID", 0);
                            SharedPreferences.Editor editor = share.edit();
                            editor.putString("userID",deviceId.toLowerCase());
                            editor.apply();
                        });
                    }
                    conn.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNavigator.applyPopAnimationsToPendingTransition(this);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        SharedPreferences sharedPreferences = getSharedPreferences("userID", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        if (userID == null)
        {
            GetUserID();
        }
        setContentView(R.layout.activity_launch);

        new Handler().postDelayed(new Runnable() {

// Using handler with postDelayed called runnable run method

            @Override

            public void run() {

                Intent i = new Intent(LaunchActivity.this, MainActivity.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, 1000); // w
    }

}