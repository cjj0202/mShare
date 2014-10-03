package com.example.test;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Fragment1 extends Fragment implements SensorEventListener {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private SensorManager mSensorManager;
	private SendSocket sSocket;
	private ReceiveSocket rSocket;
	public String selectedImagePath;
    private ListView mListView;
    private String[] listView_array;
    private ArrayList<String> arrayList;
    private Button button1;
    private String name;
    private String receiverName = null;
    private EditText edt;

    
	public Fragment1() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment1, container, false);

		//Bind button & edittext with xml
        button1 = (Button) rootView.findViewById(R.id.button1);
        edt = (EditText) rootView.findViewById(R.id.editText1);

        //Get System Service and set SensorManager for Light Sensor
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
        		SensorManager.SENSOR_DELAY_GAME);
        
        button1.setOnClickListener(new OnClickListener()  //Set Receiver Name button
        {
        	@Override
        	public void onClick(View v)
        	{
      	      name = edt.getText().toString();
      	      receiverName = name;
      	      Toast.makeText(getActivity(), "Receiver Name: "+name,Toast.LENGTH_LONG).show();
        	}
        });
        
        //Turn on Receive Socket to listen new file
        Context mContext = getActivity();
		rSocket = new ReceiveSocket(mContext);  // Pass Context to ReceiveSocket for Notification service
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			rSocket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			rSocket.execute();
		}
   
		//List View to list SD card files
		arrayList = new ArrayList<String>();
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recorder";
        File sdCardRoot = new File(path) ;
        
        for (File f : sdCardRoot.listFiles()) {
            if (f.isFile()){
                String name = f.getName();
                arrayList.add(name);
            }
        }
        
        listView_array = new String[arrayList.size()];
        for(int i=0 ; i<arrayList.size();i++) {
            listView_array[i] = arrayList.get(i);
        }

        mListView=(ListView)rootView.findViewById(R.id.listView1);
        
        // Add a string array in the list by setAdapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listView_array);
        mListView.setAdapter(arrayAdapter);
        //mListView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1 , listView_array));

        //Short Click filename to use SendSocket sending file
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            	System.out.println("receiverName:"+receiverName);

            	//Get file name and path
                String url = arrayAdapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
	            selectedImagePath = "/mnt/sdcard/Recorder/"+url;
	            System.out.println(selectedImagePath);
	            
	            if(receiverName!=null)
	            {
	            //Create SendSocket to send this file; Pass Receiver Name and File Path to SendSocket
				sSocket = new SendSocket();
				sSocket.sendPath(selectedImagePath);
				sSocket.sendReceiver(receiverName);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					sSocket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					sSocket.execute();
				}
				Toast.makeText(getActivity().getApplicationContext(), "File Sent", Toast.LENGTH_LONG).show();
	            }
	            else
	            {
	            Toast.makeText(getActivity().getApplicationContext(), "Set Receiver First!", Toast.LENGTH_LONG).show();
	            }
            }
        });
        
        //Long Click filename to open the file to preview
        mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				//Get file type and path
				String url = arrayAdapter.getItem(arg2);
				Intent intent = new Intent();  
				intent.setAction(Intent.ACTION_VIEW);  
				Uri fileUri = Uri.parse("file://mnt//sdcard//Recorder//"+url);  
				String filetype = url.substring(url.length()-3,url.length());
				
				//Use system relative method to open audio/video/picture
				if (filetype.equals("amr")){
					intent.setDataAndType(fileUri, "audio/*");
				}
				if (filetype.equals("mp4")){
					intent.setDataAndType(fileUri, "video/*");
					}
				if (filetype.equals("jpg")){
					intent.setDataAndType(fileUri, "image/*");
					}
				startActivity(intent);  
				return false;
			}
        });

		return rootView;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	//Get Light Sensor value
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] values = event.values;
		int sensorType = event.sensor.getType();
		switch (sensorType)
		{
		case Sensor.TYPE_LIGHT:
			if (values[0]<225){
				SetRingMode(AudioManager.RINGER_MODE_SILENT);  //Set to Silent Mode
			}
			else
				SetRingMode(AudioManager.RINGER_MODE_NORMAL);  //Set back to Normal Mode
			break;
		}
	}
	
    private void SetRingMode(int mode)
    {
        try
        {
            AudioManager audioManager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            if(audioManager!=null)
            {
                audioManager.setRingerMode(mode);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
