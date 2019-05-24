package recoders.hht.com.camera2demo.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;

import java.util.concurrent.TimeUnit;

import recoders.hht.com.camera2demo.R;

public class Camera2Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "Camera2Activity";
    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        mTextureView = findViewById(R.id.textureview);

        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String s : cameraIdList) {
                //相机信息类，比如闪光灯、AE模式等
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(s);
                //判断是否支持 camera2 的功能
                Integer level = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                /**
                 * CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL;
                 * LEGACY：向后兼容的级别，处于该级别的设备意味着它只支持 Camera1 的功能，不具备任何 Camera2 高级特性。
                 * LIMITED：除了支持 Camera1 的基础功能之外，还支持部分 Camera2 高级特性的级别。
                 * FULL：支持所有 Camera2 的高级特性。
                 * LEVEL_3：新增更多 Camera2 高级特性，例如 YUV 数据的后处理等。
                 */

                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {

                }
                Log.d(TAG, "zsr onCreate: " + s + " " + level + " " + facing);

                //支持的分辨率
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] outputSizes = map.getOutputSizes(TextureView.class);
                for (Size outputSize : outputSizes) {
                    Log.d(TAG, "zsr onCreate: "+outputSize.getWidth()+" "+outputSize.getHeight());

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "zsr onCreate: "+e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()){

        }else{
            mTextureView.setSurfaceTextureListener(this);
        }

    }

    private void openCamera(int width,int height){
        int mCameraId = setpriviewSize(width,height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置预设大小
     * @param width
     * @param height
     */
    private int  setpriviewSize(int width, int height) {
        int frontId = -1;
        int backId = -1;
        int externdId = -1;
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = manager.getCameraIdList();
            for (String s : cameraIdList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(s);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null ){
                    //前置
                    if (facing == CameraCharacteristics.LENS_FACING_FRONT){
                        frontId = facing;
                    }else if (facing == CameraCharacteristics.LENS_FACING_BACK){ //后置
                        backId = facing;
                    }else{ //外接摄像头
                        externdId = facing;
                    }
                }

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null){
                    continue;
                }
                Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                Log.d(TAG, "zsr setpriviewSize: "+level);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frontId;

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
    private CameraDevice mCameraDevice;
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
          //  mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
          //  createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
          //  mCameraOpenCloseLock.release();
            cameraDevice.close();
          //  mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
           // mCameraOpenCloseLock.release();
            cameraDevice.close();
         //   mCameraDevice = null;

        }

    };
}
