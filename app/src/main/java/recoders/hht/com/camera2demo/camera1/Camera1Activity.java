package recoders.hht.com.camera2demo.camera1;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

import recoders.hht.com.camera2demo.R;
import recoders.hht.com.camera2demo.camera2.Camera2Activity;

public class Camera1Activity extends AppCompatActivity implements SurfaceHolder.Callback, Handler.Callback {
    private static final String TAG = "Camera1Activity";
    /**
     * static
     */
    private static final int MSG_OPEN_CAMERA = 0X0001;
    private static final int MSG_CLOSE_CAMERA = 0X0002;
    private static final int MSG_START_PREVIEW = 0X0003;
    private static final int MSG_STOP_PREVIEW = 0x0004;
    private int mBackCamereId = -1;
    private int mFrontCameraId = -1;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private int mCurrentCameraId;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private int mPreviewWidth;
    private int mPreviewHeight;

    /**
     * 由于开启相机，预览等都是耗时的，所以使用handlerthread 执行
     * @param message
     * @return
     */

    @Override
    public boolean handleMessage(Message message) {
        Log.d(TAG, "zsr handleMessage: "+message.what);
        switch (message.what){
            case MSG_OPEN_CAMERA:
                openCamera(mCurrentCameraId);
                break;
            case MSG_CLOSE_CAMERA:
                closeCamera();
                break;
            case MSG_STOP_PREVIEW:
                 stopPreview();
                break;
            case MSG_START_PREVIEW:
                setPreviewSize();
                startPreview();
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);

        initHandleThread();
        initCamera();
        mSurfaceView = findViewById(R.id.surfaceview);
        mSurfaceView.getHolder().addCallback(this);
        mCurrentCameraId = mFrontCameraId;
        //打开摄像头
        mHandler.sendEmptyMessage(MSG_OPEN_CAMERA);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopPreview();
        closeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandleThread();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_OPEN_CAMERA);
            mHandler.removeMessages(MSG_START_PREVIEW);
            mHandler.removeMessages(MSG_CLOSE_CAMERA);
            mHandler.removeMessages(MSG_STOP_PREVIEW);
        }

    }

    private void initHandleThread() {
        mHandlerThread = new HandlerThread("Camera1");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(),this);
    }
    private void stopHandleThread(){
        if (mHandlerThread != null){
            mHandlerThread.quitSafely();
        }
        mHandlerThread = null;
        mHandler = null;
    }

    /**
     * 初始化相机
     */
    private void initCamera() {

        //1.获取相机个数
        int cameraNum = Camera.getNumberOfCameras();

        for (int i = 0; i < cameraNum; i++) {
            //2.根据个数，拿到 camera 的info信息
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i,cameraInfo);
            //3.判断方向
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                //后置摄像头
                mBackCamereId = i;
            }else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                //前置摄像头
                mFrontCameraId = i;
            }
        }

    }

    /**
     * 设置相机属性
     */
    private void configCamera(){
        /**
         * 1.通过 Camera.getParameters() 获取 Camera.Parameters 实例。
         * 2.通过 Camera.Parameters.getSupportedXXX 获取某个参数的支持情况。
         * 3.通过 Camera.Parameters.set() 方法设置参数。
         * 4.通过 Camera.setParameters() 方法将参数应用到底层。
         */

        if (mCamera != null){
            //获取相机参数
            Camera.Parameters parameters = mCamera.getParameters();
            //获取相机支持预览的尺寸的尺寸
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        }
    }

    /**
     * 设置预设尺寸
     */
    private void setPreviewSize(){
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
        }
    }

    private int getCameraDisplayOrientation(Camera.CameraInfo info, Activity activity){
        int orientation = activity.getWindow().getWindowManager().getDefaultDisplay().getOrientation();
        Log.d(TAG, "zsr 相机摄像头角度: "+orientation+" "+info.orientation);
        int degress = 0;
        switch (orientation){
            case Surface.ROTATION_0:
                degress = 0;
                break;
            case Surface.ROTATION_90:
                degress = 90;
                break;
            case Surface.ROTATION_180:
                degress = 180;
                break;
            case Surface.ROTATION_270:
                degress = 270;
                break;
        }
        int result;
        //前置
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degress) % 360;
            result = (360 - result) % 360;  // 镜像
        } else {  // 后置
            result = (info.orientation - degress + 360) % 360;
        }
        //camera.setDisplayOrientation(result);
        return result;

    }


    /**
     * 开机摄像头
     */
    private void openCamera(int cameraId) {
        if (mCamera == null) {
            mCamera = Camera.open(cameraId);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            mCamera.setDisplayOrientation(getCameraDisplayOrientation(info, this));
        }
    }

    /**
     * 关闭相机
     */
    private void closeCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开启预览
     */
    private void startPreview(){
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mCamera != null){
            mCamera.startPreview();
        }
    }

    /**
     * 停止预览
     */
    private void stopPreview(){
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;

        //开始预览
        mHandler.sendEmptyMessage(MSG_START_PREVIEW);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }


    /**
     * 切换摄像头点击类
     * @param view
     */
    public void switchC(View view) {

        if (mCurrentCameraId == mFrontCameraId){
            camreaSwitch(mBackCamereId);
        }else{
            camreaSwitch(mFrontCameraId);
        }
    }
    /**
     * 切换摄像头
     */
    private void camreaSwitch(int cameraId){
        /**
         *  停止预览
         * 关闭当前摄像头
         * 开启新的摄像头
         * 配置预览尺寸
         * 配置预览 Surface
         * 开启预览
         */
        mCurrentCameraId = cameraId;
        mHandler.sendEmptyMessage(MSG_STOP_PREVIEW);
        mHandler.sendEmptyMessage(MSG_CLOSE_CAMERA);
        mHandler.sendEmptyMessage(MSG_OPEN_CAMERA);
        mHandler.sendEmptyMessage(MSG_START_PREVIEW);
    }


}
