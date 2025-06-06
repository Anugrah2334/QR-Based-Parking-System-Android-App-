package com.example.qrparking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etVehicle, etPhone;
    Button btnSubmit, btnPrint;
    ImageView qrImage;
    TextView txtName, txtPlate;
    LinearLayout printLayout;
    File excelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Views
        etName = findViewById(R.id.et_name);
        etVehicle = findViewById(R.id.et_vehicle);
        etPhone = findViewById(R.id.et_phone);
        btnSubmit = findViewById(R.id.btn_submit);
        btnPrint = findViewById(R.id.btnPrint);
        qrImage = findViewById(R.id.qr_image);
        txtName = findViewById(R.id.txtName);
        txtPlate = findViewById(R.id.txtPlate);
        printLayout = findViewById(R.id.printLayout);

        // Excel file location
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "GoPark");
        if (!dir.exists()) dir.mkdirs();
        excelFile = new File(dir, "RegisteredUsers.xlsx");

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String plate = etVehicle.getText().toString().trim();
            String empId = etPhone.getText().toString().trim();

            if (name.isEmpty() || plate.isEmpty() || empId.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String data = name + "\n" + plate + "\n" + empId;

            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
                qrImage.setImageBitmap(bitmap);
                qrImage.setVisibility(View.VISIBLE);

                txtName.setText("Name: " + name);
                txtPlate.setText("Plate: " + plate);
                printLayout.setVisibility(View.VISIBLE);
                btnPrint.setVisibility(View.VISIBLE);

                // Save to Excel
                saveToExcel(name, plate, empId);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        btnPrint.setOnClickListener(v -> saveLayoutAsImage(printLayout));
    }

    private void saveLayoutAsImage(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GoPark");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "qr_details_" + System.currentTimeMillis() + ".png";
            File file = new File(dir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            Toast.makeText(this, "Saved to Gallery:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToExcel(String name, String vehicle, String empId) {
        XSSFWorkbook workbook;
        XSSFSheet sheet;

        try {
            if (excelFile.exists()) {
                FileInputStream fis = new FileInputStream(excelFile);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Registered Users");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Name");
                header.createCell(1).setCellValue("Vehicle Number");
                header.createCell(2).setCellValue("Employee ID");
            }

            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);
            row.createCell(0).setCellValue(name);
            row.createCell(1).setCellValue(vehicle);
            row.createCell(2).setCellValue(empId);

            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.close();

            workbook.close();
            Toast.makeText(this, "Saved to Excel:\n" + excelFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving to Excel", Toast.LENGTH_SHORT).show();
        }
    }
}
