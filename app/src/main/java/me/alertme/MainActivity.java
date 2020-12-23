package me.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    ListView contacts;
    Button btnAdd,btnSettings;
    String sp="sharedPref";
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    Vector<String> names,nos;
    Set<String> nam;
    Integer count;
    ArrayAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contacts=findViewById(R.id.listFav);
        btnAdd=findViewById(R.id.btnAddFav);
        btnSettings=findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                if(btnSettings.getText().toString().equals("ON"))
                {                editor.putBoolean("isOn",false);
                                editor.commit();
                                btnSettings.setText("OFF");
                }else
                {
                    editor.putBoolean("isOn",true);
                    editor.commit();
                    btnSettings.setText("ON");
                }

            }
        });





        sharedPreferences=getSharedPreferences(sp,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        count=sharedPreferences.getInt("count",0);
        Log.e("Count",""+count);
        names=new Vector<>();
        nos=new Vector<>();
        int i;
        for(i=0;i<count;i++)
        {
            int j=i+1;
            names.add(sharedPreferences.getString("n"+j,"Person"));
            nos.add(sharedPreferences.getString(""+j,"0"));
        }

        adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, names);

        contacts.setAdapter(adapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 1);
            }
        });



        contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);
                names.remove(pos);
                int i;
                i=pos+1;
                editor.remove("n"+i);
                editor.remove(""+i);
                editor.putInt("count",count-1);
                count-=1;
                editor.commit();
                int j;

                for (i=0;i<names.size();i++)
                {
                    j=i+1;
                    editor.putString("n"+j,names.get(i));
                    editor.putString(Integer.toString(j),nos.get(i));

                }
                editor.commit();

                adapter.notifyDataSetChanged();
                contacts.invalidateViews();
                contacts.refreshDrawableState();

                return true;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Cursor cursor = null;
                    try {
                        String phoneNo = null;
                        String name = null;

                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNo = cursor.getString(phoneIndex);
                        name = cursor.getString(nameIndex);
//                        Vector<String> names=new Vector<>();

  //                      editor.putStringSet("s", (Set<String>) names);
                        int i=count+1;
                        editor.putString("n"+i,name);
                        editor.putString(Integer.toString(i),phoneNo);
                        editor.putInt("count",i);
                        editor.commit();
                        count+=1;
                        names.add(name);
                        adapter.notifyDataSetChanged();
                        contacts.invalidateViews();
                        contacts.refreshDrawableState();
                      //  Log.e("Name and Contact number is",i+"-"+name+","+phoneNo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("Failed", "Not able to pick contact");
        }
    }
}
