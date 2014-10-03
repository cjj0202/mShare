package com.example.test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class Fragment3 extends Fragment implements SurfaceHolder.Callback {
	public static final String ARG_SECTION_NUMBER = "section_number";
	private Button recordButton;
	private Button stopButton;
	private MediaRecorder mRecorder;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private File file = null;
	
	public Fragment3() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment3,
				container, false);

		//Use SurfaceView and Holder to set preview screen for video recording
	    mSurfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView1);
	    mHolder = mSurfaceView.getHolder();
	    mHolder.addCallback(this);
	    mHolder.getSurface();
		
		recordButton = (Button) rootView.findViewById(R.id.button1);
		stopButton = (Button) rootView.findViewById(R.id.button2);
		
		recordButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			//Open Camera and set the preview display turning right 90 degree(Default orientation is horizontal)
				mCamera = Camera.open();
				mCamera.setDisplayOrientation(90);
				mCamera.unlock();  //Video Recording must have this mCamera.unlock()

				//Open Surface Preview, Initiate MediaRecorder and Create video file
				try {
					initRecorder(mHolder.getSurface());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				recordButton.setText("Recording");
				Toast.makeText(getActivity().getApplicationContext(), "Recording Now-" +
						"File is located at"+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			}
		});
		
		stopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Stop MediaRecorder and release; Stop and lock Camera to avoid conflication next time
					mRecorder.stop();
					mRecorder.reset();
					mRecorder.release();
					mRecorder = null;
					mCamera.stopPreview();
					mCamera.lock();
					mCamera.release();
					mCamera = null;
					recordButton.setText("Record");
					Toast.makeText(getActivity().getApplicationContext(), "Record Finish", Toast.LENGTH_LONG).show();
			}
		});
		return rootView;
       }

		//Create file, Media Recorder and set parameters
	private void initRecorder(Surface surface) throws IllegalStateException, IOException {
					
			file = new File("/mnt/sdcard/Recorder/"
					+ new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CANADA)) + ".mp4");

			if(mRecorder == null)  

			mRecorder = new MediaRecorder();
			mRecorder.setPreviewDisplay(surface);
			mRecorder.setCamera(mCamera);

			mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		    mRecorder.setVideoEncodingBitRate(512 * 1000);
		    mRecorder.setVideoFrameRate(30);
		    mRecorder.setVideoSize(640, 480);
		    mRecorder.setOutputFile(file.getAbsolutePath());
	        mRecorder.prepare();
	        mRecorder.start();
		    
	}
				
	//Normally open camera and surfaceview is written in surfaceCreated and surfaceChange
	//But in this project both Video and Photo are using surfaceview and Camera
	//So it will crash if both initialize when application start
	//Thus I configure them DO NOTHING in surfaceCreated/Changed, Surfaceview/Camera is only opened when click on certain button
		
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    if (mCamera != null) {
	    	mCamera.stopPreview();
	    	mCamera.setPreviewCallback(null);
			mCamera.lock();
			mCamera.release();
			mCamera = null;
	    }
	}
}
	
