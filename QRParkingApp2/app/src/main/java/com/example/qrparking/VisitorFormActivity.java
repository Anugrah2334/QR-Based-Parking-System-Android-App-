package com.example.qrparking;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class VisitorFormActivity extends AppCompatActivity {

    private EditText etName, etDate, etPurpose;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_form);

        // Initialize views
        etName = findViewById(R.id.et_visitor_name);
        etDate = findViewById(R.id.et_visitor_date);
        etPurpose = findViewById(R.id.et_visitor_purpose);
        btnDone = findViewById(R.id.btn_visitor_done);

        // Save button click
        btnDone.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String purpose = etPurpose.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || purpose.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                saveVisitorDataToExcel(name, date, purpose);
                etName.setText("");
                etDate.setText("");
                etPurpose.setText("");
            }
        });
    }

    private void saveVisitorDataToExcel(String name, String date, String purpose) {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, "VisitorRecords.xlsx");

        Workbook workbook;
        Sheet sheet;

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
            } catch (Exception e) {
                e.printStackTrace();
                workbook = new XSSFWorkbook(); // fallback
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        sheet = workbook.getSheet("Visitors");
        if (sheet == null) {
            sheet = workbook.createSheet("Visitors");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Date");
            headerRow.createCell(2).setCellValue("Purpose");
        }

        int rowCount = sheet.getLastRowNum();
        Row row = sheet.createRow(rowCount + 1);
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(date);
        row.createCell(2).setCellValue(purpose);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            Toast.makeText(this, "Visitor details saved in Documents!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save visitor details.", Toast.LENGTH_SHORT).show();
        }
    }

}
