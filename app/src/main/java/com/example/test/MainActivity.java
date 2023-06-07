package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editTextFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextFileName = (EditText) findViewById(R.id.editTextFilename);
    }

    public void openPieActivity(View v){
        Intent intent = new Intent(this, PieChartActivity.class);
        startActivity(intent);
    }

    public void openLocationActivity(View v){
        String fileName = editTextFileName.getText().toString();
        if (!fileName.equals("")){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("FileName",fileName);
            startActivity(intent);
        }
    }
}