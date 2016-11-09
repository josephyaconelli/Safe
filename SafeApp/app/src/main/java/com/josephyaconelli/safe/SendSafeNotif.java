package com.josephyaconelli.safe;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendSafeNotif extends AppCompatActivity {

    private Button sendBtn;
    private AutoCompleteTextView contactEntry;
    private ListView contactList;
    private TextView contact;

    private ArrayList<Map<String, String>> peopleList;
    private SimpleAdapter contactAdapter;

    //for now
    private final String num = "15555215554";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_safe_notif);


        //setup elements
        sendBtn = (Button) findViewById(R.id.sendButton);
        contactEntry = (AutoCompleteTextView) findViewById(R.id.contactEntry);
        contactList = (ListView) findViewById(R.id.contactList);

        peopleList = new ArrayList<>();
        PopulatePeopleList();

        contactAdapter = new SimpleAdapter( this,
                                            peopleList,
                                            R.layout.contact,
                                            new String[] {"Name", "Phone", "Type"},
                                            new int[] {R.id.ccontName, R.id.ccontNo, R.id.ccontType}
        );

        contactEntry.setAdapter(contactAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sayImSafe();
            }
        });

    }

    private void sayImSafe(){

        SmsManager sms = SmsManager.getDefault();
        boolean smsSent = false;

        try{
            sms.sendTextMessage(num, null, "I'm Safe! :)", null, null);
            Toast.makeText(this, "Sms Successfully Sent! :)", Toast.LENGTH_LONG).show();
            smsSent = true;
        }catch(Exception e){
            Toast.makeText(this, "Sms Failed! :(", Toast.LENGTH_LONG).show();
        }

        if(!smsSent){
            //then send via another means (twillio or via internet)
        }

    }

    private void PopulatePeopleList(){

        peopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while(people.moveToNext()){
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else if (numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if (numberType.equals("2"))
                        NamePhoneType.put("Type", "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    peopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        startManagingCursor(people);
    }

}
