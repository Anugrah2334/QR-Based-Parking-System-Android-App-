package com.example.qrparking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class RegisterVehicleActivity extends AppCompatActivity {

    private ListView listView;
    private EditText searchBar;
    private ArrayList<String> allUsersList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_users_list);

        listView = findViewById(R.id.registered_users_list);
        searchBar = findViewById(R.id.search_bar);

        // Ask for permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            loadRegisteredUsers();
        }

        // Handle search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Permission granted
                loadRegisteredUsers();
            } else {
                // ❌ Permission denied
                Toast.makeText(this, "Permission denied. Cannot read Excel file.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadRegisteredUsers() {
        allUsersList.clear();

        try {
            File excelFile = new File(getExternalFilesDir(null), "RegisteredUsers.xlsx");
            if (!excelFile.exists()) {
                Toast.makeText(this, "RegisteredUsers.xlsx not found!", Toast.LENGTH_LONG).show();
                return;
            }

            FileInputStream fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start at 1 to skip header
                Row row = sheet.getRow(i);
                if (row != null) {
                    String name = row.getCell(0).toString().trim();
                    String vehicle = row.getCell(1).toString().trim();
                    String serial = row.getCell(2).toString().trim();

                    String display = "Name: " + name + ", Vehicle: " + vehicle + ", Serial: " + serial;
                    allUsersList.add(display);
                }
            }

            workbook.close();
            fis.close();

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allUsersList);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading Excel file", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterList(String query) {
        ArrayList<String> filtered = new ArrayList<>();
        for (String user : allUsersList) {
            if (user.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(user);
            }
        }

        if (filtered.isEmpty()) {
            filtered.add("Not Registered");
        }

        adapter.clear();
        adapter.addAll(filtered);
        adapter.notifyDataSetChanged();
    }
}
