package com.afzal;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class VedioService extends IntentService {
    // Constructor
    public VedioService() {
        // Call superclass constructor with
        // the name of the worker thread
        super("ExampleIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("VideoIntentService", "Task in progress");

       VedioClient mVedioClient = new VedioClient(new VedioClient.OnMessageReceived()
        {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message)
            {
                try
                {
                    if(message!=null)
                    {
                        Log.d("esp-wifi", "Return Message from Socket::::: >>>>> "+message);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        },"192.168.254.1");
        mVedioClient.run();
    }
}
