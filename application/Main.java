package application;

import java.util.List;

import application.controllers.MainMenuController;
import application.settings.GamePreferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	private Rectangle2D screenBounds;
	private double screenWidth, screenHeight;
	private int resolutionWidth, resolutionHeight;
	private boolean isFullScreen;
	private StageStyle stageStyle;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Get any data needed for setting the stage and scene up
		getScreenInformation();
		getGamePreferences();
		
		// Load the FXML and gain access to its controller for setup, then place into scene
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainMenu.fxml"));
		Parent root = loader.load();
		MainMenuController mainMenuController = loader.getController();
		setupController(mainMenuController, primaryStage);
		Scene scene = new Scene(root);
		
		// Place the scene into the stage and configure stage, then show
		primaryStage.setScene(scene);
		configureStage(primaryStage);
		primaryStage.show();
		
		// Request focus is important for keybindings to work
		root.requestFocus();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void getScreenInformation() {
		List<Screen> screens = Screen.getScreens();
		int preferedScreen = GamePreferences.getScreen();
		Screen screen = screens.get(preferedScreen-1);
		screenBounds = screen.getBounds();
		screenWidth = screenBounds.getWidth();
		screenHeight = screenBounds.getHeight();
	}
	
	private void getGamePreferences() {
		resolutionWidth = GamePreferences.getResolutionWidth();
		resolutionHeight = GamePreferences.getResolutionHeight();
		isFullScreen = GamePreferences.isFullScreen();
		stageStyle = GamePreferences.getStageStyle();
	}
	
	private void setupController(MainMenuController controller, Stage stage) {
		controller.setPrimaryStage(stage);
		controller.setupStage(stage);
	}
	
	public void configureStage(Stage stage) {
		stage.setTitle("Pixelz");
		stage.setResizable(false);
		stage.initStyle(stageStyle);
		if(isFullScreen) {
			stage.setFullScreen(isFullScreen);
		}
		else {
			if(resolutionHeight < screenHeight) {
				stage.setWidth(resolutionWidth);
				stage.setHeight(resolutionHeight);
			}
			else {
				stage.setWidth(screenWidth);
				stage.setHeight(screenHeight);	
			}
			stage.setX(screenBounds.getMinX());
			stage.setY(screenBounds.getMinY());
			stage.centerOnScreen();
		}
	}
}