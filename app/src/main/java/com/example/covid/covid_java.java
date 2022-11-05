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


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            Intent intent = new Intent(covid_java.this, cvd.class);
            startActivity(intent);
        } else if (view.getId() == R.id.button2) {
            Intent intent = new Intent(covid_java.this, cvd.class);
            startActivity(intent);
        }
    }
}


