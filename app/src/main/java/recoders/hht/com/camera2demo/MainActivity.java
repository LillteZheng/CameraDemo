package recoders.hht.com.camera2demo;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.security.Permission;

import recoders.hht.com.camera2demo.camera1.Camera1Activity;
import recoders.hht.com.camera2demo.camera2.Camera2Activity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        PermissionManager.requestPermission(this, new PermissionManager.Callback() {
            @Override
            public void permissionSuccess() {

            }

            @Override
            public void permissionFailed() {

            }
        }, Manifest.permission.CAMERA);


        PermissionManager.requestPermission(this, new PermissionManager.Callback() {
            @Override
            public void permissionSuccess() {

            }

            @Override
            public void permissionFailed() {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);



    }



    public void camera1(View view) {
        startActivity(new Intent(this, Camera1Activity.class));
    }

    public void camera2(View view) {
        startActivity(new Intent(this, Camera2Activity.class));
    }
}
