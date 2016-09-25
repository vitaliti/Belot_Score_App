package com.example.vision.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Vision on 20.9.2016 г..
 */
public class InfoScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
    }

    public void LeaveInfoActivity(View view){
        setResult(RESULT_OK);
        finish();
    }

    public void loadRules(View view) {
        Uri uriUrl = Uri.parse("https://www.pagat.com/jass/belote.html/");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
