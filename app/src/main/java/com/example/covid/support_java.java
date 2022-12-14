package com.example.covid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class support_java extends AppCompatActivity {

    Button btn4;
    Button btn5;
    Button btn6;

    private WebView webview1;
    private String url = "https://covid19m.kdca.go.kr/KP/epq/35700092A";

    @Override
    protected void onCreate(@Nullable Bundle savedIntenceStae) {
        super.onCreate(savedIntenceStae);
        setContentView(R.layout.support);

        btn4 = findViewById(R.id.imageView4);
        btn5 = findViewById(R.id.imageView5);
        btn6 = findViewById(R.id.covid_document);



        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.iksan.go.kr/index.iksan?menuCd=DOM_000002018001002000"));
                    startActivity(intent);

            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://ncvr.kdca.go.kr/cobk/index.html"));
                startActivity(intent);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview1 = (WebView)findViewById(R.id.webview1);
                webview1.loadUrl(url);

                WebSettings webSettings = webview1.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webview1.setWebChromeClient(new WebChromeClient());

            }
        });
    }


}