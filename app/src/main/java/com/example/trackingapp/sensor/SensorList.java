package com.example.trackingapp.sensor;

import android.content.Context;

import com.example.trackingapp.SensorDatabaseHelper;
import com.example.trackingapp.sensor.implementation.AccessibilitySensor;
import com.example.trackingapp.sensor.implementation.AppSensor;
import com.example.trackingapp.sensor.implementation.ChargingSensor;
import com.example.trackingapp.sensor.implementation.MyAccelerometerSensor;
import com.example.trackingapp.sensor.implementation.MyGyroscopeSensor;
import com.example.trackingapp.sensor.implementation.MyLightSensor;
import com.example.trackingapp.sensor.implementation.MyProximitySensor;
import com.example.trackingapp.sensor.implementation.OrientationSensor;
import com.example.trackingapp.sensor.implementation.RingtoneVolumeSensor;
import com.example.trackingapp.sensor.implementation.ScreenOnOffSensor;
import com.example.trackingapp.sensor.implementation.ScreenOrientationSensor;
import com.example.trackingapp.sensor.implementation.StillAliveSensor;
import com.example.trackingapp.sensor.implementation.TouchSensor;
import com.example.trackingapp.sensor.implementation.WifiSensor;

import java.util.ArrayList;
import java.util.List;

public class SensorList {
		
	private SensorList() {
	}
		
	public static List<AbstractSensor> getList(Context pContext) {
		List<AbstractSensor> list  = new ArrayList<>();

		list.add(new AccessibilitySensor());
		list.add(new MyAccelerometerSensor());
		//list.add(new ActivitySensor()); // This is not longer supported by Android
		//list.add(new AirplaneModeSensor());
		list.add(new AppSensor());
		//list.add(new AudioLevelSensor());
		list.add(new ChargingSensor());
		list.add(new MyGyroscopeSensor());
		list.add(new MyLightSensor());
		list.add(new MyProximitySensor());
		list.add(new OrientationSensor());
		list.add(new RingtoneVolumeSensor());
		list.add(new ScreenOnOffSensor());
		list.add(new ScreenOrientationSensor());
		list.add(new StillAliveSensor());
		list.add(new TouchSensor());
		list.add(new WifiSensor());
		
		SensorDatabaseHelper db = new SensorDatabaseHelper(pContext);
		
		for (AbstractSensor s : list)
			db.addIfNotExists(s);
		
		for (AbstractSensor s : list)
			s.setEnabled(db.getSensorData(s));

		db.close();
			
		return list;
	}

}
