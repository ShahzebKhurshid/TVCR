package com.test.nfc_demo;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.test.nfc_demo.sql.SQLHelper;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

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

    private TextToSpeech speaker; // speaker for Text to Speech
    private static final String tag = "Speech"; // tag for debugging Text to Speech

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

        inputSearch.setVisibility(View.INVISIBLE); //make it invisible


        //get database instance
        helper = new SQLHelper(this);
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            Log.d("SQLiteDemo", "Create database failed");
        }

        //Initialize Text to Speech engine
        speaker = new TextToSpeech(this, this);

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
                try { // speak when click list item
                    Log.i(tag, "Add - TTS invoked.");

                    // if speaker is talking, stop it
                    if(speaker.isSpeaking()){
                        Log.i(tag, "Speaker Speaking");
                        speaker.stop();
                        // else start speech
                    } else {
                        Log.i(tag, "Speaker Not Already Speaking");
                        speak(dataList.get(position));
                    }

                } catch (Exception e) {
                    Log.e(tag, "Speaker failure" + e.getMessage());
                }

                //navigate to Detail activity
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(KEY, dataList.get(position));
                startActivity(intent);

            }
        });

        ActionBar actionBar = getSupportActionBar();          //create ActionBar object
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

    }

    // speak methods will send text to be spoken
    public void speak(String output){
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
    }

    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(tag, "Language is not available.");
            } else {
                // The TTS engine has been successfully initialized
                Log.i(tag, "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e(tag, "Could not initialize TextToSpeech.");
        }
    }

    // initialize options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

        //getMenuInflater().inflate(R.menu.menu, menu);
        //return true;
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
