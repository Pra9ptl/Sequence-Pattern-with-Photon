package com.example.sequencefromparticle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {

    // MARK: Debug info
    private final String TAG="Jarvis";

    TextView seq_lbl,in_lbl;
    // MARK: Particle Account Info
    private final String PARTICLE_USERNAME = "patelpranav1313@gmail.com";
    private final String PARTICLE_PASSWORD = "$Patel14";

    // MARK: Particle device-specific info
    private final String DEVICE_ID = "17002f000f47363333343437";

    // MARK: Particle Publish / Subscribe variables
    private long subscriptionId;

    // MARK: Particle device
    private ParticleDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this.getApplicationContext());

        // 2. Setup your device variable
        getDeviceFromCloud();

        seq_lbl = (TextView) findViewById(R.id.sequenceLabel);
        in_lbl = (TextView) findViewById(R.id.inputLabel);
    }

    /**
     * Custom function to connect to the Particle Cloud and get the device
     */
    public void getDeviceFromCloud() {
        // This function runs in the background
        // It tries to connect to the Particle Cloud and get your device
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                mDevice = particleCloud.getDevice(DEVICE_ID);
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    public void getUserInput(View view){
        Log.d("TAG", "You Pressed!!");
        if (mDevice == null) {
            Log.d("TAG", "cannot find device");
            return;
        }
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToDeviceEvents("Result",DEVICE_ID,
                        // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                Log.d("Result", event.dataPayload);
                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        in_lbl.setText("" + event.dataPayload);
                                    }
                                }));
                            }

                            public void onEventError(Exception e) {
                                Log.e(TAG, "Event error: ", e);
                            }
                        });
                return -1;
            }
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got data from Cloud");
            }
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    public void startGame(String status) {

        Log.d(TAG, "Command to send to particle: " + status);


        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                // 2. build a list and put the r,g,b into the list
                List<String> functionParameters = new ArrayList<String>();
                functionParameters.add(status);

                // 3. send the command to the particle
                try {
                    mDevice.callFunction("sequence", functionParameters);
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    e.printStackTrace();
                }


                return -1;
            }

            @Override
            public void onSuccess(Object o) {

                Log.d(TAG, "Sent status command to device.");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });

    }

    public void getSequenceString(View view) {
        Log.d("TAG", "You Pressed!!");
        if (mDevice == null) {
            Log.d("TAG", "cannot find device");
            return;
        }
        startGame("start");
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents(
                        "sequenceString",  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        seq_lbl.setText("" + event.dataPayload);
                                    }
                                }));
                            }

                            public void onEventError(Exception e) {
                                Log.e(TAG, "Event error: ", e);
                            }
                        });
                return -1;
            }
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got data from Cloud");
            }
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }


}
