package com.example.test;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SendSocket extends AsyncTask<URL, Integer, Long> {
	String selectedImagePath;
	String receiver;
    Socket sock;

	@Override
	protected Long doInBackground(URL... arg0) {
		// TODO Auto-generated method stub
		sendActivity();
		return null;
	}
	
	//Pass the file path to SendSocket
	void sendPath(String path) {
		selectedImagePath = path;
		System.out.println("sendPath ok");
	}
	
	//Pass the Receiver name to SendSocket
	void sendReceiver(String name) {
		receiver = name;
		System.out.println("sendReceiver ok");
	}
	
	protected void sendActivity() {
        try {
            sock = new Socket("192.168.43.200", 1149);    //Server IP is 192.168.43.200, Port 1149 for send
            System.out.println("Connecting to Server");

            
            File myFile = new File (selectedImagePath); 
            byte [] byteArray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(byteArray,0,byteArray.length);
            OutputStream outStream = sock.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outStream);
            dos.writeUTF(receiver+"_"+myFile.getName());  //Add File name when transmitting
            System.out.println("Sending File");
            outStream.write(byteArray,0,byteArray.length);
            outStream.flush();
            sock.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
  
    
