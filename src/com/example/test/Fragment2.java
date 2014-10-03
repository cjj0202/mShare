package com.example.test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Fragment2 extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private Button recordButton;
	private Button stopButton;
	private MediaRecorder mRecorder;

    
	public Fragment2() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment2,
				container, false);

		recordButton = (Button) rootView.findViewById(R.id.button1);
		stopButton = (Button) rootView.findViewById(R.id.button2);

		//Press Record button to start recording
		recordButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Set file name with date&time
				File file = new File("/mnt/sdcard/Recorder/"
						+ new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CANADA))
						+ ".amr");
				
				Toast.makeText(getActivity().getApplicationContext(), "Recording Now-" +
						"File is located at"+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				
				//Set MediaRecorder parameters
				mRecorder = new MediaRecorder();
				mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); 
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mRecorder.setOutputFile(file.getAbsolutePath()); 
				
				//Create record file and prepare MediaRecorder
				try {
					file.createNewFile();
					mRecorder.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mRecorder.start();
			}
		});
		
		//Stop Button to stop recording
		stopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//Stop and release MediaRecorder
				if (mRecorder != null) {
					mRecorder.stop();
					mRecorder.release();
					mRecorder = null;
					Toast.makeText(getActivity().getApplicationContext(), "Record Finish", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		return rootView;
	}
	
}
