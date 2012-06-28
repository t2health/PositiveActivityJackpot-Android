package org.t2health.paj.classes;

public class SharedPref {

	public static boolean getIsEulaAccepted() {
		return Global.sharedPref.getBoolean("eula_accepted", false);
	}

	public static void setIsEulaAccepted(boolean enabled) {
		Global.sharedPref.edit().putBoolean("eula_accepted", enabled).commit();
	}

	public static boolean getCoached() {
		return Global.sharedPref.getBoolean("been_coached", false);
	}

	public static void setCoached(boolean enabled) {
		Global.sharedPref.edit().putBoolean("been_coached", enabled).commit();
	}
	
	public static boolean getSendAnnonData() {
		return Global.sharedPref.getBoolean("send_anon_data", true);
	}
	
	public static void setSendAnnonData(boolean enabled) {
		Global.sharedPref.edit().putBoolean("send_anon_data", enabled).commit();
	}
	
	public static boolean getAudioOn() {
		return Global.sharedPref.getBoolean("audio_on", true);
	}

	public static void setAudioOn(boolean enabled) {
		Global.sharedPref.edit().putBoolean("audio_on", enabled).commit();
	}
	
	public static boolean getVibrationOn() {
		return Global.sharedPref.getBoolean("vibration_enabled", true);
	}
	
	public static void setVibrationOn(boolean enabled) {
		Global.sharedPref.edit().putBoolean("vibration_enabled", enabled).commit();
	}

	public static boolean getCameraOn() {
		return Global.sharedPref.getBoolean("camera_on", true);
	}

	public static void setCameraOn(boolean enabled) {
		Global.sharedPref.edit().putBoolean("camera_on", enabled).commit();
	}
	
	public static boolean getTipsOn(String key) {
		return Global.sharedPref.getBoolean("tips_" + key, true);
	}

	public static void setTipsOn(String key, boolean enabled) {
		Global.sharedPref.edit().putBoolean("tips_" + key, enabled).commit();
	}
	
	public static boolean getLearnAudio() {
		return Global.sharedPref.getBoolean("learn_audio", true);
	}

	public static void setLearnAudio(boolean enabled) {
		Global.sharedPref.edit().putBoolean("learn_audio", enabled).commit();
	}
	
	public static boolean getCoachAudio() {
		return Global.sharedPref.getBoolean("coach_audio", true);
	}

	public static void setCoachAudio(boolean enabled) {
		Global.sharedPref.edit().putBoolean("coach_audio", enabled).commit();
	}
}
