package com.flashlightapplication;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	//Grab the camera object
	private Camera camera;
	//Set a boolean for whether the flash is on or off
	private boolean isFlashOn;
	//Check whether the phone has a flash
	private boolean hasFlash;
	Parameters params;
	Button flashButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		isFlashOn = false;
		
		flashButton = (Button) findViewById(R.id.flashlightButton);

		//Must next check whether the user has a flashlight on their device
		hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if(!hasFlash)
		{
			//Show an alert dialog yelling at the user if they do not have a camera in their phone?
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
			alert.setTitle("Error Message");
			alert.setMessage("Your device does not support this feature :(");
			alert.show();
		}

		//get the camera instance
		//getCamera();
		//Change the button image
		toggleButtonImage();

		//Set a click listener for the toggle button
		flashButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Hitting this");
				if(isFlashOn)
				{
					//If the flash is on turn it off
					turnFlashOff();
				}
				else
				{
					//If the flash is off turn it on
					turnFlashOn();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//Get the camera instance
	private void getCamera()
	{
		if(camera == null)
		{
			try
			{
				//Get the camera instance and parameters
				camera = Camera.open();
				params = camera.getParameters();
				System.out.println("camera is set");
			}
			catch(RuntimeException e)
			{
				System.out.println("Could not get the camera instance");
			}
		}
	}

	private void turnFlashOn()
	{
		if(!isFlashOn)
		{
			if(camera == null || params ==  null)
			{
				return;
			}
			params = camera.getParameters();
			//Set the flash mode to turn on the flash as a flashlight (Torch)
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			//Start the preview so that the flash will be able to start
			camera.startPreview();
			isFlashOn = true;
			//The newer phones like the nexus 5 need to cling to a surfacetexture in order to use the camera instance
			try {
				camera.setPreviewTexture(new SurfaceTexture(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Toggle the button image so that it is on or off
			toggleButtonImage();
		}
	}

	private void turnFlashOff()
	{
		if(isFlashOn)
		{
			if (camera == null || params == null) {
				return;
			}
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;		
			toggleButtonImage();
		}
		//playSound();
	}

	private void toggleButtonImage(){
		if(isFlashOn){
			//flashButton.setBackgroundResource(R.drawable.psyinverse);
			flashButton.setBackgroundResource(R.drawable.switchoff);
		}else{
			flashButton.setBackgroundResource(R.drawable.switchon);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		turnFlashOff();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if(hasFlash)
			turnFlashOn();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// on starting the app get the camera params
		getCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// on stop release the camera
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
}
