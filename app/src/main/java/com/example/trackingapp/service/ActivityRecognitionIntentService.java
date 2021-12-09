package com.example.trackingapp.service;

import android.app.IntentService;
import android.content.Intent;

public class ActivityRecognitionIntentService extends IntentService {

	private static final String TAG = "org.hcilab.projects.logeverything.service.ActivityRecognitionIntentService";

	public ActivityRecognitionIntentService() {
		super("ActivityRecognitionIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		/*if (ActivityRecognitionResult.hasResult(intent)) {
			ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
			DetectedActivity mostProbableActivity = result.getMostProbableActivity();
			
			String s = CONST.dateFormat.format(System.currentTimeMillis()) + "," + getNameFromType(mostProbableActivity.getType()) + "," + mostProbableActivity.getConfidence() + "\n";
			
			Intent message = new Intent(TAG);
			message.putExtra(Intent.EXTRA_TEXT, s);
			sendBroadcast(message);
		}

		 */
	}
/*
	private String getNameFromType(int activityType) {
		switch (activityType) {
			case DetectedActivity.IN_VEHICLE:
				return "in_vehicle";
			case DetectedActivity.ON_BICYCLE:
				return "on_bicycle";
			case DetectedActivity.ON_FOOT:
				return "on_foot";
			case DetectedActivity.STILL:
				return "still";
			case DetectedActivity.UNKNOWN:
				return "unknown";
			case DetectedActivity.TILTING:
				return "tilting";
		}
		return "unknown";
	}

 */
}