package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.BugManager;
import application.CanvasManager;
import application.Colony;
import application.EnemyManager;
import application.ObjectsManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class MainController implements Initializable {
	
	@FXML BorderPane mainView;
	@FXML BottomController bottomController;
	@FXML CenterController centerController;
	@FXML LeftController leftController;
	@FXML RightController rightController;
	@FXML TopController topController;
	
	private AnimationTimer timer;
	private Colony colony;
	private BugManager bm;
	private ObjectsManager om;
	private CanvasManager cm;
	private EnemyManager em;
	private double canvasWidth, canvasHeight, latency, uiUpdateTimer, start, stop;
	private boolean animating, config;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		canvasWidth = centerController.getCanvWidth();
		canvasHeight = centerController.getCanvHeight();
		centerController.setMainController(this);
		bottomController.setMainController(this);
		rightController.setMainController(this);
		topController.setMainController(this);
		latency = 5;
		uiUpdateTimer = 0;
	}
	
	public void animate() {		
		if(timer == null) {
			startGame();
			timer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					update();
				}
			};
			animating = true;
			stop = System.currentTimeMillis();
			timer.start();
			bottomController.updateForAnimation(animating);
		}
		else if(!animating) {
			animating = true;
			stop = System.currentTimeMillis();
			timer.start();
			bottomController.updateForAnimation(animating);
		}
		else {
			timer.stop();
			animating = false;
			bottomController.updateForAnimation(animating);
		}
	}
	
	public void startGame() {		
		// Create colony with all managers
		om = new ObjectsManager(canvasWidth, canvasHeight);
		colony = new Colony(canvasWidth, canvasHeight, om);
		bm = colony.getBm();
		rightController.startGame();
		centerController.startGame();
		cm = new CanvasManager(colony, om);
		centerController.setupCM(cm);
		centerController.setupBM(bm);
		bottomController.startGame();
		// Create the enemies
		em = new EnemyManager(colony.getCenterX(), colony.getCenterY(), colony.getRadius(), canvasWidth, canvasHeight, bm.getScouts(), bm.getGuards(), colony.getTrackers());
		bm.setEnemies(em.getEnemies());
		leftController.startGame();
		topController.startGame();
		centerController.setupEM(em);
		cm.setEnemyManager(em);
	}
	
	public void update() {
		start = System.currentTimeMillis();
		latency = (start - stop)/1000;
		// 0.00694 because that should mean 144 max fps
		if(latency >= 0.00694) {
			colony.update(latency);
			om.updateFood(latency);
			em.updateEnemies(latency);
			cm.draw();
			updateUI(latency);
			stop = System.currentTimeMillis();
		}
		if(colony.getTrackers().getColonyHealth() <= 0) {
			gameOver();
		}
	}
	
	// For each of these UI updates, it may be better to create a listener on the variables 
	// Then update the UI when the listener triggers because not every variable is constantly changing
	public void updateUI(double latency) {
		uiUpdateTimer += latency;
		bottomController.updateGameTimer(latency);
		if(uiUpdateTimer >= 0.5) {
			bottomController.updateUI();
			topController.updateUI(latency);
			rightController.updateUI();
			uiUpdateTimer = 0;
		}
	}
	
	public void gameOver() {
		cm.killColony();
		animate();
		timer = null;
	}
	
	public void config() {
		if(animating && !config) {
			animate();
			config = true;
			centerController.updateForConfig(config);
			bottomController.disableAnimationButton();
		}
		else if(!config) {
			config = true;
			centerController.updateForConfig(config);
			bottomController.disableAnimationButton();
		}
		else {
			config = false;
			centerController.updateForConfig(config);
			bottomController.enableAnimationButton();
		}
	}
	
	public Colony getColony() {
		return colony;
	}
	
	public CanvasManager getCanvasManager() {
		return cm;
	}
	
	public ObjectsManager getObjectsManager() {
		return om;
	}

	public boolean isAnimating() {
		return animating;
	}

	public boolean isConfig() {
		return config;
	}

	public void setConfig(boolean config) {
		this.config = config;
	}
	
	public void setCanvasWidth(double width) {
		canvasWidth = width;
	}
	
	public void setCanvasHeight(double height) {
		canvasHeight = height;
	}
}