package dev.milos.messagesexample;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    IntentFilter intentFilter;

    Button btnSendSMS, btnSendIntentSMS;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //-prikazuje primljeni SMS u TextView pogledu -
            TextView SMSes = (TextView) findViewById(R.id.textView1);
            SMSes.setText(intent.getExtras().getString("sms"));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //-filter prijema SMS poruka
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        //---registrovanje primaoca---
        registerReceiver(intentReceiver, intentFilter);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnSendIntentSMS = (Button) findViewById(R.id.btnSendIntentSMS);

        btnSendIntentSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSMSIntentClick(v);
            }
        });

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS("0691931996", "Jel radi ova metoda uopste?");
            }
        });

        registerReceiver(intentReceiver, intentFilter);

    }

    @Override
    public void onResume() {
        super.onResume();

        //---registrovanje primaoca---
        registerReceiver(intentReceiver, intentFilter);

        //---kreira BroadcastReceiver kada je SMS poslat---
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case AppCompatActivity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS prosleđen",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generička greška",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Nema usluge",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio isključen",
                                Toast.LENGTH_SHORT).show();
                        break;
                }

                //---kreira BroadcastReceiver kada SMS dostavljen---
                smsDeliveredReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case AppCompatActivity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS dostavljen",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case AppCompatActivity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "SMS nije dostavljen",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };

                //---registruje dva BroadcastReceiver - a---
                registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
                registerReceiver(smsSentReceiver, new IntentFilter(SENT));
            }


        };
    }


//    @Override
//    protected void onPause() {
//
//
//        //---odjavljuje primaoca---
//        unregisterReceiver(intentReceiver);
//
//        //---odjavljuje dva BroadcastReceiver-a---
//        unregisterReceiver(smsSentReceiver);
//        unregisterReceiver(smsDeliveredReceiver);
//
//        super.onPause();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //---odjavljivanje primaoca---
        unregisterReceiver(intentReceiver);
    }

    public void onClick(View v) {
        sendSMS("0691931996", "Jel radi ova metoda uopste?");
    }

    public void onSMSIntentClick (View v) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address", "0691931996");
        i.putExtra("sms_body", "Pozdravni SMS - primer!");
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);
    }

    //Šalje poruku drugom uređaju”-
    private void sendSMS(String phoneNumber, String message){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);

        }else{

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        }

    }







}
