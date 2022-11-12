package com.example.covid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class support_java extends AppCompatActivity {


    Button btn4;
    Button btn5;
    Button btn6;
    String[] nums={"","","","",""};
    String numsa="";
    TextView textView1,textView2,textView3,textView4;


    WebView webview1;
    //private String url = "https://covid19m.kdca.go.kr/KP/epq/35700092A";


    @Override
    protected void onCreate(@Nullable Bundle savedIntenceStae) {
        super.onCreate(savedIntenceStae);
        setContentView(R.layout.support);

        textView1 = findViewById(R.id.supview1);
        textView2 = findViewById(R.id.supview2);
        textView3 = findViewById(R.id.supview3);
        textView4 = findViewById(R.id.supview4);

        final Bundle bundle = new Bundle();

        btn4 = findViewById(R.id.imageView4);
        btn5 = findViewById(R.id.imageView5);
        btn6 = findViewById(R.id.covid_document);

        new Thread(){
            @Override
            public void run() {
                Document doc;
                try {
                    doc = Jsoup.connect("https://search.naver.com/search.naver?where=nexearch&sm=tab_etc&qvt=0&query=%EC%BD%94%EB%A1%9C%EB%82%9819").get();

                    for(int i = 1; i < 5; i++){
                        Element contents = doc.select(".info_0"+i).first();                 //복권 번호 6개 가져오기
                        nums[i] = " "+contents.text();
                        bundle.putString("numbers"+i, nums[i]);
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


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
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://covid19m.kdca.go.kr/KP/35700092"));
                startActivity(intent);
            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            textView1.setText(bundle.getString("numbers1"));
            textView2.setText(bundle.getString("numbers2"));
            textView3.setText(bundle.getString("numbers3"));
            textView4.setText(bundle.getString("numbers4"));
        }
    };
}

