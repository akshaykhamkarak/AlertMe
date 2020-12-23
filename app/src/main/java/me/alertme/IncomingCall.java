package me.alertme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class IncomingCall extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
private static     MediaPlayer player;
private  static     AudioManager audio;

    private static AssetFileDescriptor afd;
    public void play(boolean stop)
    {
        if(stop)
        {player.stop();}
        if (!stop){player.start();}
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("intent " , intent.getAction().toString());

       player = MediaPlayer.create(context.getApplicationContext(), R.raw.tone);

        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        try {
            player.setVolume((float) (9 / 7.0),
                    (float) (9 / 7.0));
            Log.i("volumeme",audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0+" wo 7.0"+audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
                //audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
               // play(true);
                //player.stop();
                //try {
                //  Thread.sleep(5000);
                //} catch (InterruptedException e) {
                //   e.printStackTrace();
                //}
                //player.stop();

                audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;

            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
                String phoneNumber = number;

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
                if(isOn){
                for(j=1;j<count+1;j++)
                {
                    String no=sharedPreferences.getString(Integer.toString(j),"null");
                    no=no.replaceAll("\\s","");
                    Log.i("CountVal- sp no",no);
                    Log.i("CountVal- phone",phoneNumber);
                    Log.i("CountVal- phone ++","+91"+phoneNumber);
                    if(phoneNumber.equals("+91"+no)||phoneNumber.equals(no)||phoneNumber.equals("0"+no)||phoneNumber.equals("91"+no))
                    {
                        Log.i("CountVal-","Inside if");
                         audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        // play(false);
                     /*   if(!player.isPlaying()) {
                            try {
                                player.prepare();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player.start();
                        }*/
                         //try {
                        //  Thread.sleep(5000);
                        //} catch (InterruptedException e) {
                        //   e.printStackTrace();
                        //}
                        //player.stop();

                         //audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        Log.i("CountVal-","silent");
                       // exit=true;
                    }
                    // if(exit){break;}
                }}
            }

            onCallStateChanged(context, state, number);
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                String phoneNumber = number;

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
                for(j=1;j<count+1;j++)
                {
                    String no=sharedPreferences.getString(Integer.toString(j),"null");
                    no=no.replaceAll("\\s","");
                    Log.i("CountVal- sp no",no);
                    Log.i("CountVal- phone",phoneNumber);
                    Log.i("CountVal- phone ++","+91"+phoneNumber);
                    if(phoneNumber.equals("+91"+no)||phoneNumber.equals(no)||phoneNumber.equals("0"+no)||phoneNumber.equals("91"+no))
                    {
                        Log.i("CountVal-","Inside if");
                       // audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                     //   player.start();
                        //try {
                         //  Thread.sleep(5000);
                        //} catch (InterruptedException e) {
                         //   e.printStackTrace();
                        //}
                        //player.stop();

                       // audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                       Log.i("CountVal-","silent");
                        exit=true;
                    }
                   // if(exit){break;}
                }
                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                //player.stop();
                try {
                    player.stop();
                }catch (Exception e){}
                Log.i("OFFFHOOK","Inside offhook");
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();

                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    Log.i("OFFFHOOK","Inside IDLE");
                    //Ring but no pickup-  a miss
                   // player.setVolume(0,0);
                  //  player.stop();
                //    player.release();

                    Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();


                }
                else if(isIncoming){

                    Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
                }
                else{

                    Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();

                }

                break;
        }
        lastState = state;
    }
}