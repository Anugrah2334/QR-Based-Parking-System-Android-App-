package com.example.qrparking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private Button btnScan, btnRegister;
    private TextView dailyRecordsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_main);

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        btnMenu = findViewById(R.id.btnMenu);
        btnScan = findViewById(R.id.btn_scan);
        btnRegister = findViewById(R.id.btn_register);
        dailyRecordsTextView = findViewById(R.id.dailyRecordsTextView);

        // Menu Button Click - Open Drawer
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle Sidebar Navigation Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_register) {
                startActivity(new Intent(this, RegisterVehicleActivity.class));

        } else if (id == R.id.nav_daily) {
                startActivity(new Intent(this, DailyRecordsActivity.class));
            } else if (id == R.id.nav_visitor_form) { // "Visitors" form
                startActivity(new Intent(this, VisitorFormActivity.class));
            } else if (id == R.id.nav_visitor_detail) { // "Visitor Records"
                startActivity(new Intent(this, VisitorRecordsActivity.class));
            } else if (id == R.id.nav_export) {
                startActivity(new Intent(this, ExportActivity.class));
            }
            return true;
        });

        // Scan Button Click
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        });

        // Register Button Click - Protected by login
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AuthLoginActivity.class);
            startActivity(intent);
        });

        loadDailyRecords();
    }

    private void openDailyRecords() {
        Toast.makeText(this, "Opening Daily Records", Toast.LENGTH_SHORT).show();
        loadDailyRecords();
    }

    private void loadDailyRecords() {
        SharedPreferences prefs = getSharedPreferences("DailyRecords", MODE_PRIVATE);
        String records = prefs.getString("records", "No Records Found");
        dailyRecordsTextView.setText(records);
    }
}
