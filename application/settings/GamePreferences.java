package application.settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.stage.StageStyle;

public class GamePreferences {

	private static final Preferences prefs = Preferences.userRoot().node("application");
	
	// Can call this method to look at stored preferences if needed
	public static void export() throws BackingStoreException {
		String fileName = "exported_preferences.xml";
		try (FileOutputStream outputStream = new FileOutputStream(fileName)){
			prefs.exportNode(outputStream);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getResolutionWidth() {
		return prefs.getInt("resolutionWidth", 1920);
	}
	
	public static int getResolutionHeight() {
		return prefs.getInt("resolutionHeight", 1080);
	}
	
	public static void setResolution(double width, double height) {
		prefs.putInt("resolutionWidth", (int)width);
		prefs.putInt("resolutionHeight", (int)height);
	}

	public static StageStyle getStageStyle() {
		String style = prefs.get("stageStyle", "borderless");
		StageStyle stageStyle = StageStyle.UNDECORATED;
		if(style.equals("bordered")) {
			stageStyle = StageStyle.DECORATED;
		}
		return stageStyle;
	}

	public static void setStageStyle(StageStyle style) {
		String stageStyle = "borderless";
		if(style == StageStyle.DECORATED) {
			stageStyle = "bordered";
		}
		prefs.put("stageStyle", stageStyle);
	}
	
	public static boolean isFullScreen() {
		return prefs.getBoolean("fullscreen", false);
	}
	
	public static void setFullScreen(boolean status) {
		prefs.putBoolean("fullscreen", status);
	}
	
	public static int getScreen() {
		return prefs.getInt("screenNumber", 1);
	}
	
	public static void setScreen(int screen) {
		prefs.putInt("screenNumber", screen);
	}
	
	public static int getDifficulty() {
		return prefs.getInt("difficulty", 2);
	}
	
	public static void setDifficulty(int difficulty) {
		prefs.putInt("difficulty", difficulty);
	}
	
	public static int getBugRed() {
		return prefs.getInt("bugRed", 255);
	}
	
	public static int getBugGreen() {
		return prefs.getInt("bugGreen", 255);
	}
	
	public static int getBugBlue() {
		return prefs.getInt("bugBlue", 255);
	}
	
	public static int getBgRed() {
		return prefs.getInt("bgRed", 50);
	}
	
	public static int getBgGreen() {
		return prefs.getInt("bgGreen", 50);
	}
	
	public static int getBgBlue() {
		return prefs.getInt("bgBlue", 50);
	}
	
	public static int getEnemyRed() {
		return prefs.getInt("enemyRed", 150);
	}
	
	public static int getEnemyGreen() {
		return prefs.getInt("enemyGreen", 0);
	}
	
	public static int getEnemyBlue() {
		return prefs.getInt("enemyBlue", 0);
	}
	
	public static void setBugRed(int red) {
		prefs.putInt("bugRed", red);
	}
	
	public static void setBugGreen(int green) {
		prefs.putInt("bugGreen", green);
	}
	
	public static void setBugBlue(int blue) {
		prefs.putInt("bugBlue", blue);
	}
	
	public static void setBgRed(int red) {
		prefs.putInt("bgRed", red);
	}
	
	public static void setBgGreen(int green) {
		prefs.putInt("bgGreen", green);
	}
	
	public static void setBgBlue(int blue) {
		prefs.putInt("bgBlue", blue);
	}
	
	public static void setEnemyRed(int red) {
		prefs.putInt("enemyRed", red);
	}
	
	public static void setEnemyGreen(int green) {
		prefs.putInt("enemyGreen", green);
	}
	
	public static void setEnemyBlue(int blue) {
		prefs.putInt("enemyBlue", blue);
	}
	
	public static double getMusicVolume() {
		return prefs.getDouble("musicVolume", 100.0);
	}
	
	public static void setMusicVolume(double volume) {
		prefs.putDouble("musicVolume", volume);
	}
	
	public static double getEffectsVolume() {
		return prefs.getDouble("effectsVolume", 100.0);
	}
	
	public static void setEffectsVolume(double volume) {
		prefs.putDouble("effectsVolume", volume);
	}
}
