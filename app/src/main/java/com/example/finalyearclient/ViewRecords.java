package com.example.finalyearclient;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ViewRecords extends AppCompatActivity {

    String name,password;
    int version;
    DataBase dataBase;
    TableLayout tableLayout;
    Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        password = bundle.getString("password");
        version = Integer.parseInt(bundle.getString("version"));
        cache = new Cache(this);
        dataBase = new DataBase(getApplicationContext(), name,version,password);
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);

        //permanent header values
        TableRow rowHeader = new TableRow(getApplicationContext());
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"Name", "Age", "Height", "Weight", "Sex"};

        for (String c : headerText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(c);
            rowHeader.addView(tv);
        }
        tableLayout.addView(rowHeader);


        //permanent values
        TableRow row1 = new TableRow(getApplicationContext());
        row1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] colText1 = new String[0];
        try {
            colText1 = new String[]{dataBase.stringValue("name"), dataBase.stringValue("age")+"",dataBase.stringValue("height")+"",
                    dataBase.stringValue("weight")+"", dataBase.stringValue("sex")};
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        for (String text : colText1) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(text);
            row1.addView(tv);
        }
        tableLayout.addView(row1);


       //display all changing values
        TableRow rowHeader2 = new TableRow(getApplicationContext());
        rowHeader2.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        String[] headerText2 = {"Date and Time","Blood Pressure", "Respiration Rate", "Blood Oxygen", "Heart Rate"};

        for (String c : headerText2) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(30, 5, 30, 5);
            tv.setText(c);
            rowHeader2.addView(tv);
        }
        tableLayout.addView(rowHeader2);


        Cursor cursor = dataBase.getAll();
        while (cursor.moveToNext()) {

            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            byte[] ENdate_and_time= cursor.getBlob(cursor.getColumnIndex("date_and_time"));
            byte[]  ENblood_pressure= cursor.getBlob(cursor.getColumnIndex("blood_pressure"));
            byte[]  ENrespiration_rate= cursor.getBlob(cursor.getColumnIndex("respiration_rate"));
            byte[]  ENblood_oxygen= cursor.getBlob(cursor.getColumnIndex("blood_oxygen"));
            byte[]  ENheart_rate= cursor.getBlob(cursor.getColumnIndex("heart_rate"));

            String date_and_time = null;
            String blood_pressure = null;
            String respiration_rate = null;
            String blood_oxygen = null;
            String heart_rate = null;
            try {
                date_and_time = dataBase.databaseDecrypt(ENdate_and_time);
                blood_pressure = dataBase.databaseDecrypt(ENblood_pressure);
                respiration_rate = dataBase.databaseDecrypt(ENrespiration_rate);
                blood_oxygen = dataBase.databaseDecrypt(ENblood_oxygen);
                heart_rate = dataBase.databaseDecrypt(ENheart_rate);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            String[] colText = {date_and_time, blood_pressure, respiration_rate,blood_oxygen,heart_rate};

            for (String text : colText) {
                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(16);
                tv.setPadding(5, 5, 5, 5);
                tv.setText(text);
                row.addView(tv);
            }
            tableLayout.addView(row);
        }
    }
}
