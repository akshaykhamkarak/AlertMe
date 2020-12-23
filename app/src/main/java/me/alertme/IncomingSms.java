package me.alertme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;


import java.sql.Time;

import static android.content.Context.MODE_PRIVATE;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                    String sp="sharedPref";
                    SharedPreferences.Editor editor;
                    SharedPreferences sharedPreferences;
                    sharedPreferences=context.getSharedPreferences(sp,MODE_PRIVATE);
                    editor=sharedPreferences.edit();
                    int count=sharedPreferences.getInt("count",0);
                    int j;
                    boolean exit=false;
                    Log.i("CountVal-",count+"");
                    Log.i("CountVal-",sharedPreferences.getString("2","Err"));
                    boolean isOn=sharedPreferences.getBoolean("isOn",true);
                    if(isOn) {
                        for (j = 1; j < count + 1; j++) {
                            String no = sharedPreferences.getString(Integer.toString(j), "null");
                            no = no.replaceAll("\\s", "");
                            Log.i("CountVal- sp no", no);
                            Log.i("CountVal- phone", phoneNumber);
                            Log.i("CountVal- phone ++", "+91" + phoneNumber);
                            if (phoneNumber.equals("+91" + no) || phoneNumber.equals(no) || phoneNumber.equals("0" + no) || phoneNumber.equals("91" + no)) {
                                Log.i("CountVal-", "Inside if");
                                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


                                //    Uri tone=Uri.parse("android.resource://me.alertme/raw/tone.mp3");
                                MediaPlayer player = MediaPlayer.create(context.getApplicationContext(), RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION));
                                //    MediaPlayer player = MediaPlayer.create(context.getApplicationContext(), tone);

                                try {
                                    player.setVolume((float) (9 / 7.0),
                                            (float) (9 / 7.0));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                player.start();

                                audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                exit = true;
                            }
                            if (exit) {
                                break;
                            }
                        }
                    }


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
          //  Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
