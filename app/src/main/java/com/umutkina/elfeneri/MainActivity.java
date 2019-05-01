package com.umutkina.elfeneri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.umutkina.elfeneri.pushnotif.ContentDetailActivity;
import com.umutkina.elfeneri.pushnotif.WebViewActivity;
import com.umutkina.elfeneri.pushnotif.YoutubePlayerActivity;
import com.umutkina.elfeneri.pushnotif.modals.Notification;
import com.umutkina.elfeneri.pushnotif.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    private String TAG = "com.umutkina.uzuntesbihat menuactivity";

    //flag to detect flash is on or off
    private boolean isLighOn = false;

    private Camera camera;

    private Button button;

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            isLighOn=false;
            button.setText("Aç");
            button.setBackgroundResource(R.drawable.rounded_counter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        button = (Button) findViewById(R.id.buttonFlashlight);

        Context context = this;
        PackageManager pm = context.getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }

        camera = Camera.open();
        final Camera.Parameters p = camera.getParameters();

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isLighOn) {

                    Log.i("info", "torch is turn off!");

                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.stopPreview();
                    isLighOn = false;
                    button.setText(getString(R.string.open));
                    button.setBackgroundResource(R.drawable.rounded_counter);

                } else {
                    button.setBackgroundResource(R.drawable.rounded_open);
                    button.setText(getString(R.string.close));
                    Log.i("info", "torch is turn on!");

                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(p);
                    camera.startPreview();
                    isLighOn = true;

                }

            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Map<String, String> stringStringMap = new HashMap<>();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));

                    stringStringMap.put(key,value.toString());
                }


                sendNotification(stringStringMap);

            }
        }


    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

        }
        return "";
    }


    private void sendNotification(Map<String, String> data) {
        Intent intent = null;
        if (data.size() > 0&&data.get("type")!=null) {

            Log.d(TAG, "data.size() > 0 " + data);

            String type = data.get("type");
            String videoUrl = data.get("videoUrl");
            String webUrl = data.get("webUrl");
            String message = data.get("message");
            String link = data.get("linkUrl");
            String imageUrl = data.get("imageUrl");
            String title = data.get("title");
            String buttonText = data.get("buttonText");
            String id = System.currentTimeMillis() + "";

            Notification notification= new Notification();

            notification.setMessage(message);
            notification.setImageUrl(imageUrl);
            notification.setLink(link);
            notification.setType(type);
            notification.setVideoUrl(videoUrl);
            notification.setWebUrl(webUrl);
            notification.setId(id);
            notification.setTitle(title);
            notification.setButtonText(buttonText);



            Utils.saveNotification(notification,this);
            switch (type) {
                case "video":
                    intent = YoutubePlayerActivity.newIntent(this, videoUrl);
                    break;

                case "content":
                    intent = ContentDetailActivity.newIntent(this, notification);

                    break;

                case "web":

                    intent= WebViewActivity.newIntent(this,webUrl);

                    break;


            }
            this.startActivity(intent);
        }


    }
}