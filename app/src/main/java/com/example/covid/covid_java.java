package com.example.covid;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class covid_java extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedIntenceStae) {
        super.onCreate(savedIntenceStae);
        setContentView(R.layout.covid);

        Button btn7 = (Button) findViewById(R.id.button);
        btn7.setOnClickListener(this);
        Button btn8 = (Button) findViewById(R.id.button2);
        btn8.setOnClickListener(this);
        Button btn9 = (Button) findViewById(R.id.button3);
        btn9.setOnClickListener(this);
        Button btn10 = (Button) findViewById(R.id.button4);
        btn10.setOnClickListener(this);
        Button btn11 = (Button) findViewById(R.id.button5);
        btn11.setOnClickListener(this);
        Button btn12 = (Button) findViewById(R.id.button6);
        btn12.setOnClickListener(this);
        Button btn13 = (Button) findViewById(R.id.button7);
        btn13.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            Intent intent = new Intent(covid_java.this, cvd.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button2) {
            Intent intent = new Intent(covid_java.this, cvd2.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button3) {
            Intent intent = new Intent(covid_java.this, cvd3.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button4) {
            Intent intent = new Intent(covid_java.this, cvd4.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button5) {
            Intent intent = new Intent(covid_java.this, cvd5.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button6) {
            Intent intent = new Intent(covid_java.this, cvd6.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button7) {
            Intent intent = new Intent(covid_java.this, cvd7.class);
            startActivity(intent);

        }



    }
}


