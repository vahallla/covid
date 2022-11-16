package com.example.covid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    String[] nums={"","","","",""};
    String numsa="";
    TextView textView1,textView2,textView3,textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView1 = findViewById(R.id.supview1);
        textView2 = findViewById(R.id.supview2);
        textView3 = findViewById(R.id.supview3);
        textView4 = findViewById(R.id.supview4);
        final Bundle bundle = new Bundle();
        Button btn1 = findViewById(R.id.button_move1);//버튼 1,2,3선언
        btn1.setOnClickListener(this);
        Button btn2 = findViewById(R.id.button_move2);
        btn2.setOnClickListener(this);
        Button btn3 = findViewById(R.id.button_move3);
        btn3.setOnClickListener(this);

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


    }

    @Override//버튼 클릭시 이벤트 1,2,3
    public void onClick(View view){
        if(view.getId()==R.id.button_move1){
            Intent intent = new Intent(MainActivity.this, covid_java.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.button_move2){
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        }
        else if(view.getId()==R.id.button_move3){
            Intent intent = new Intent(MainActivity.this, support_java.class);
            startActivity(intent);
        }

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




