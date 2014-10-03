package com.example.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class Fragment4 extends Fragment implements SurfaceHolder.Callback {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private Camera mCamera;
	private Bitmap mBitmap; 
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private Button recordButton;
	private Button previewButton;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment4,
				container, false);

		recordButton = (Button) rootView.findViewById(R.id.button1);
		previewButton = (Button) rootView.findViewById(R.id.button2);
		mSurfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView1);		
	    mHolder = mSurfaceView.getHolder();
	    mHolder.addCallback(this);
	    mHolder.getSurface();
	    
	    //Press Preview to open camera and surface
	    previewButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				mCamera = Camera.open();
				//mCamera.unlock(); 
				//I spent 3 hours to find out this line should be commented out in Photo Camera
				//Otherwise "setPreviewDisplay" will fail, this is opposite to the setting in Video Recording

				try {
					mCamera.setPreviewDisplay(mHolder);
					System.out.println("try"+mCamera);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Set Camera Parameters
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.setPreviewSize(800, 480);
				parameters.setFocusMode("auto");
				//parameters.setPictureSize(2592, 1944);
				parameters.setPictureSize(800, 600);  //Setting smaller resolution to make raw file smaller, otherwise it will have OutOfMemory issue
				//Set preview orientation 90 degree to make it vertical
				mCamera.setParameters(parameters);
				mCamera.setDisplayOrientation(90);
				mCamera.startPreview();
				Toast.makeText(getActivity().getApplicationContext(), "Preview OK", Toast.LENGTH_LONG).show();
			}
		});
	    
	    //Take Picture can callback to save picture
		recordButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				try {
					mCamera.takePicture(null, null, pictureCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
				Toast.makeText(getActivity().getApplicationContext(), "Photo OK", Toast.LENGTH_LONG).show();
			}
		});
		
		return rootView;
	}
	
	//We need wait about 10 seconds for this method to save pictures
   public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
    	public void onPictureTaken(byte[] data, Camera camera) {
    		System.out.println("pictureCallback");
    		Log.i("cjj","onPictureTaken");
    		Toast.makeText(getActivity().getApplicationContext(), "Saving¡­¡­", Toast.LENGTH_LONG).show();
    		
    		//Use BitmapFactory.decodeByteArray() to translate raw data to Bitmap
    		mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    		
    		//Save Bitmap to file in SD card
    		File file = new File("/mnt/sdcard/Recorder/"+ new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg");
    		try {
    			file.createNewFile();
    			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
    			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);                  
    			os.flush();
    			os.close();
    			Toast.makeText(getActivity().getApplicationContext(), "Saving OK", Toast.LENGTH_LONG).show();
    			
    			//Stop Camera and preview to avoid conflict
    	    	mCamera.stopPreview();
    	    	mCamera.setPreviewCallback(null);
    			mCamera.lock();
    			mCamera.release();
    			mCamera = null;
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
   };

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
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	    if (mCamera != null) {
	    	mCamera.stopPreview();
	    	mCamera.setPreviewCallback(null);
			mCamera.lock();
			mCamera.release();
			mCamera = null;
	    }

	}

}
