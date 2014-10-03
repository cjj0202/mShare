package com.example.test;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Vibrator;

public class ReceiveSocket extends AsyncTask<URL, Integer, Long> {
	private Context b;
    Socket sock;
    ServerSocket serverSock;
	
	public ReceiveSocket(Context a){
		this.b = a;
	}
	
	//Receive job is done in background AsncTask
	@Override
	protected Long doInBackground(URL... arg0) {
		// TODO Auto-generated method stubk
		    try {
		    	ReceiveActivity();
		    	} catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    }
		return null;
	}

	
	private void ReceiveActivity() throws IOException {
        int filesize=9999999;  

        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;

        // Create Server Socket, Port 1150
        
        serverSock = new ServerSocket();
        serverSock.setReuseAddress(true);
        serverSock.bind(new InetSocketAddress(1150));
        
        //Wait for client connection
        while (true) {
        
            System.out.println("Waiting for TCP Socket Client");
            sock = serverSock.accept();
            System.out.println("New connection: " + sock);

            ////Receive Socket
            byte [] byteArray  = new byte [filesize];
            InputStream inStream = sock.getInputStream();
            DataInputStream clientData = new DataInputStream(inStream);
            String fileName = clientData.readUTF();   //Read Filename
            FileOutputStream fos = new FileOutputStream("/mnt/sdcard/Recorder/" +fileName); //File Path
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = inStream.read(byteArray,0,byteArray.length);
            current = bytesRead;

            //Reading bytes from InputStream
            do {
               bytesRead =  inStream.read(byteArray, current, (byteArray.length-current));
               if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(byteArray, 0 , current);
            bos.flush();
            long end = System.currentTimeMillis();
            System.out.println(end-start);
            bos.close();
            sock.close();

            //Notification Service
            int intCurRingerMode;
            AudioManager audioManager=(AudioManager)this.b.getSystemService(Context.AUDIO_SERVICE);
            intCurRingerMode=audioManager.getRingerMode();
            
            //Only Vibrate and Play sound when Ringer is Normal Mode
            if(intCurRingerMode==AudioManager.RINGER_MODE_NORMAL) {

            //Vibrate when new file arrives
            Vibrator vibrator;
            vibrator = (Vibrator) this.b.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
            
            //Play sound when new file arrives
            MediaPlayer mp;
            mp = MediaPlayer.create(this.b, R.raw.alarm01);
            mp.setLooping(false);
            mp.start();

            }

            //In both Mode, Send notification to Notification Manager
            NotificationManager mNotificationManager = (NotificationManager) this.b.getSystemService(Context.NOTIFICATION_SERVICE);
            int icon = R.drawable.ic_launcher;
            CharSequence tickerText = "New Message!";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);
            CharSequence contentTitle = "mShare Notification";
            CharSequence contentText = "New Message from mShare!";
            notification.setLatestEventInfo(this.b, contentTitle, contentText, PendingIntent.getActivity(this.b.getApplicationContext(), 0, new Intent(), 0));
            mNotificationManager.notify(1, notification);
            
        }
    }
	
}