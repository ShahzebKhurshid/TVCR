package com.test.nfc_demo;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.test.nfc_demo.sql.SQLHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String URL = "url";
    private static final String ADDRESS = "address";
    private static final int CODE = 1001;

    // listview
    private ListView listView;

    // Adapter for listview
    ArrayAdapter<String> adapter;

    // Search EditText
    EditText inputSearch;


    // ArrayList data for Listview
    ArrayList<String> dataList = new ArrayList<>();

    //Db related data
    private SQLiteDatabase db;
    private SQLHelper helper;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //initialize views
        listView = findViewById(R.id.list_view);
        inputSearch = findViewById(R.id.inputSearch);


        //get database instance
        helper = new SQLHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("SQLiteDemo", "Create database failed");
        }


        // Adding items to listview
        dataList.addAll(getDataList());
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.contact_name, dataList);
        //set adapter to listview
        listView.setAdapter(adapter);

        // callback for edit text
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainActivity.this.adapter.getFilter().filter(cs); // this does the filtering
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // no action needed
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // no action needed
            }
        });

        //handle clicks of items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //navigate to Detail activity
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(KEY, dataList.get(position));
                startActivity(intent);

            }
        });

    }


    // initialize options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // add contact or search for contact when clicked in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivityForResult(intent, CODE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //Callback that we get from Adding new contact
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK) {
            dataList.clear();
            dataList.addAll(getDataList());
            adapter.notifyDataSetChanged();
        }
    }

    //Method used to get data
    private ArrayList<String> getDataList() {
        //query database
        ArrayList<String> contactList = helper.getContactNamesList();
        if (!contactList.isEmpty()) {
            return contactList;
        } else {
            return new ArrayList<String>();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Toast.makeText(getApplicationContext(), "NFC discovered", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            Toast.makeText(getApplicationContext(), "NFC discovered", Toast.LENGTH_SHORT).show();
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            }
            if (messages[0] != null) {
                String result = "";
                byte[] payload = messages[0].getRecords()[0].getPayload();
                // this assumes that we get back am SOH followed by host/code
                for (int b = 1; b < payload.length; b++) { // skip SOH
                    result += (char) payload[b];
                }
                Log.d(TAG, result);
                String[] strArr = result.split("FN:");
                result = strArr[1];//removes the first 2 lines of vcard

                strArr = result.split("ADR:");
                String name = strArr[0];//gets the name
                result = strArr[1];

                strArr = result.split("TEL:");
                String address = strArr[0];//gets the address
                result = strArr[1];

                strArr = result.split("EMAIL:");
                String phone = strArr[0];//gets the telephone
                result = strArr[1];

                strArr = result.split("URL:");
                String email = strArr[0];//gets the email
                result = strArr[1];

                strArr = result.split("END:");
                String url = strArr[0];//gets the email
                result = strArr[1];

                address = address.replaceAll(";", "");

                Intent activityIntent = new Intent(MainActivity.this, AddActivity.class);
                activityIntent.putExtra(NAME, name);
                activityIntent.putExtra(PHONE, phone);
                activityIntent.putExtra(EMAIL, email);
                activityIntent.putExtra(ADDRESS, address);
                activityIntent.putExtra(URL, url);
                startActivityForResult(activityIntent, CODE);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}
