package com.test.nfc_demo;

/**
 *  Activity for the individual contact page.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.test.nfc_demo.pojo.ContactInfo;
import com.test.nfc_demo.sql.SQLHelper;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY = "key";

    private String selectedName;
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView address;
    private TextView linkedin;
    private static final int CODE = 2001;

    private SQLiteDatabase db;
    private SQLHelper helper;
    private ContactInfo info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //the selected data position from previous screen
        selectedName = getIntent().getStringExtra(KEY);

        //declare the views
        name = findViewById(R.id.name_txt);
        phone = findViewById(R.id.phone_txt);
        email = findViewById(R.id.email_txt);
        address = findViewById(R.id.address_txt);
        linkedin = findViewById(R.id.linkedin_txt);

        //set On clicks
        phone.setOnClickListener(this);
        email.setOnClickListener(this);
        address.setOnClickListener(this);
        linkedin.setOnClickListener(this);

        //get database instance
        helper = new SQLHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("SQLiteDemo", "Create database failed");
        }

        getContactList();
    }


    //on back button press to go back
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // initialize options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // add contact or search for contact when clicked in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            helper.deleteContact(new ContactInfo(
                    name.getText().toString(),
                    phone.getText().toString(), email.getText().toString(),
                    linkedin.getText().toString(), address.getText().toString()));
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivityForResult(intent, CODE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //on click of data action occurs here
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.address_txt) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+info.getAddress());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
        if (id == R.id.email_txt) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{info.getEmail()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "NFC Demo");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        if (id == R.id.linkedin_txt) {
            String url = info.getUrl();
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        }
        if (id == R.id.phone_txt) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setMessage("Complete Action Using");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Call",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent call = new Intent(Intent.ACTION_DIAL);
                            call.setData(Uri.parse("tel:" + info.getNumber()));
                            if (call.resolveActivity(getPackageManager()) != null) {
                                startActivity(call);
                            }
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    "Text",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String number = info.getNumber();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));

                            /*Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setData(Uri.parse("smsto:"));
                            intent.putExtra("sms_body", "NFC Demo");
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }*/
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private void getContactList() {
        //query database
        ArrayList<ContactInfo> contactList = helper.getContactList();
        for (ContactInfo item : contactList) {
            if (selectedName.equals(item.getName())) {
                info = item;
                break;
            }
        }
        //set data
        name.setText(info.getName());
        phone.setText(info.getNumber());
        email.setText(info.getEmail());
        address.setText(info.getAddress());
        linkedin.setText(info.getUrl());
    }

}
