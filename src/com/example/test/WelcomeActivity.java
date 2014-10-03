package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

/**
 * 
 * Welcome Activity
 */
public class WelcomeActivity extends Activity {  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_welcome);  
  
        
		 //millisInFuture:the seconds from start() to the end of onFinish()
		 //countDownInterval:the interval time of receiving onTick(long)
		
        new CountDownTimer(5000, 1000) {  
            @Override  
            public void onTick(long millisUntilFinished) {  
            }  
  
            @Override  
            public void onFinish() {  
                Intent intent = new Intent(WelcomeActivity.this, Instruction.class);  
                startActivity(intent);  
                WelcomeActivity.this.finish();  
            }  
        }.start();  
    }  
  
}  


