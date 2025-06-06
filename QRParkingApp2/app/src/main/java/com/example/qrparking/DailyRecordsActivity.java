package com.example.qrparking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DailyRecordsActivity extends AppCompatActivity {

    private TextView recordsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_records);

        recordsTextView = findViewById(R.id.recordsTextView);

        // Load stored records from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("DailyRecords", MODE_PRIVATE);
        String records = prefs.getString("records", "No Records Found");

        recordsTextView.setText(records);
    }
}
