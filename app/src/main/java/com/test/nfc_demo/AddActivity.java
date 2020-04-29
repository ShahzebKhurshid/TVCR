package com.test.nfc_demo;

/**
 *  Activity for adding new contact information.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.test.nfc_demo.pojo.ContactInfo;
import com.test.nfc_demo.sql.SQLHelper;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String URL = "url";
    private static final String ADDRESS = "address";

    private SQLiteDatabase db;
    private SQLHelper helper;

    private EditText name;
    private EditText phone;
    private EditText email;
    private EditText url;
    private EditText address;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //initialize view
        name = findViewById(R.id.editName);
        phone = findViewById(R.id.editNumber);
        email = findViewById(R.id.editEmail);
        url = findViewById(R.id.editUrl);
        address = findViewById(R.id.editAddress);
        saveButton = findViewById(R.id.addSave);

        saveButton.setOnClickListener(this);

        //get database instance
        helper = new SQLHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("SQLiteDemo", "Create database failed");
        }

        name.setText(getIntent().getStringExtra(NAME));
        phone.setText(getIntent().getStringExtra(PHONE));
        email.setText(getIntent().getStringExtra(EMAIL));
        address.setText(getIntent().getStringExtra(ADDRESS));
        url.setText(getIntent().getStringExtra(URL));
    }

    // Save contact on click and add name to the home page list
    public void onClick(View v) {
        if (v.getId() == R.id.addSave) {
            // insert record
            helper.addContact(new ContactInfo(
                    capitalizeWord(name.getText().toString()), // capitalize each word in a name
                    phone.getText().toString(), email.getText().toString(),
                    url.getText().toString(), address.getText().toString()));
            showAlert();
        }
    }

    // capitalize each word
    public static String capitalizeWord(String str){
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    //on back button press to go back
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //close database
    @Override
    protected void onPause() {
        super.onPause();
        if (db != null)
            db.close();
    }

    //Show a dialog and close the activity
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Contact Added successfully");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK); //send back a positive result to parent activity as data was added
                        AddActivity.this.finish(); //line to close activity
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

}
