package engenoid.tessocrtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import engenoid.tessocrtest.Core.CameraEngine;
import engenoid.tessocrtest.Core.ExtraViews.FocusBoxView;
import engenoid.tessocrtest.Core.Imaging.Tools;
import engenoid.tessocrtest.Core.TessTool.TessAsyncEngine;


public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,
        Camera.PictureCallback, Camera.ShutterCallback {

    static final String TAG = "DBG_" + MainActivity.class.getName();

    Button shutterButton;
    Button focusButton;
    FocusBoxView focusBox;
    private static final int CAMERA_REQUEST=1888;
    private static final int MY_CAMERA_PERMISSION_CODE=100;

    SurfaceView cameraFrame;
    CameraEngine cameraEngine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);

            }
            else
            {
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
            }
        }


        write_permission();

        Button retry = (Button) findViewById(R.id.retryButton);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraEngine!=null && cameraEngine.isOn()){
                    cameraEngine.stop();
                    cameraEngine.start();
                }

            }
        });
    }


    public void write_permission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);

            } else {

                Toast.makeText(this,"Permission already Granted",Toast.LENGTH_LONG).show();

            }
        }


    }






    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        switch (requestCode) {



            case 100:

                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Camera granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAMERA_REQUEST);



                }
                else{
                    Toast.makeText(this,"camera denied",Toast.LENGTH_LONG).show();
                }

                break;


            case 10:

                if (grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();

                break;



            case 20:

                if ( grantResults.length>0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();

                break;

        }

    }



    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==CAMERA_REQUEST && resultCode == Activity.RESULT_OK){

            Toast.makeText(this,"Camera Open",Toast.LENGTH_LONG).show();

        }
    }







    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }

        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();

        Log.d(TAG, "Camera engine started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        shutterButton = (Button) findViewById(R.id.shutter_button);
        focusBox = (FocusBoxView) findViewById(R.id.focus_box);
        focusButton = (Button) findViewById(R.id.focus_button);

        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v == shutterButton){
            if(cameraEngine != null && cameraEngine.isOn()){
                cameraEngine.takeShot(this, this, this);
            }
        }

        if(v == focusButton){
            if(cameraEngine!=null && cameraEngine.isOn()){
                cameraEngine.requestFocus();
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.d(TAG, "Picture taken");

        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }

        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());

        Log.d(TAG, "Got bitmap");

        Log.d(TAG, "Initialization of TessBaseApi");

        new TessAsyncEngine().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

    }

    @Override
    public void onShutter() {

    }

}