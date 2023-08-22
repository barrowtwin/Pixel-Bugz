package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.BugManager;
import application.CanvasManager;
import application.Colony;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BottomController implements Initializable {

	@FXML private StackPane bottom;
	@FXML private Label bugzInHome, bugzExiting, workerCount, scoutCount, guardCount, queenCount, exitsCount, createExitLabel, gameTimer;
	@FXML private Button animationButton;
	@FXML private Circle workerToggle;
	@FXML private Rectangle guardToggle, hPheroToggle, fPheroToggle, gridToggle;
	@FXML private Polygon scoutToggle;
	@FXML private ProgressBar exitProgress;
	
	private MainController controller;
	private CanvasManager cm;
	private Colony colony;
	private boolean drawHPheros, drawFPheros, drawPherosGrid;
	private double gameTime;
	private int hours, minutes, seconds;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		drawHPheros = false;
		drawFPheros = false;
		drawPherosGrid = false;
		gameTime = 0;
		hPheroToggle.setOpacity(0.1);
		fPheroToggle.setOpacity(0.1);
		gridToggle.setOpacity(0.1);
		animationButton.setStyle("-fx-background-color: #2e8a24;");
	}
	
	public void setMainController(MainController controller) {
		this.controller = controller;
	}
	
	public void startGame() {
		cm = controller.getCanvasManager();
		colony = controller.getColony();
	}
	
	@FXML
	public void animate() {
		controller.animate();
	}
	
	public void updateForAnimation(boolean animating) {
		if(animating) {
			animationButton.setText("PAUSE");
			animationButton.setStyle("-fx-background-color: #8a2724;");
		}
		else {
			animationButton.setText("RESUME");
			animationButton.setStyle("-fx-background-color: #2e8a24;");
		}
	}
	
	public void updateUI() {
		BugManager bm = colony.getBm();
		double progress = bm.getExitCreationTimer() / bm.getExitCreationTime();
		exitProgress.setProgress(progress);
		exitsCount.setText("" + bm.getColonyExits());
		bugzInHome.setText("" + colony.getTrackers().getBugzInHome());
		bugzExiting.setText("" + bm.getBugzToReleaseSize());
		workerCount.setText("" + bm.getWorkers().size());
		scoutCount.setText("" + bm.getScouts().size());
		queenCount.setText("" + bm.getQueens());
	}
	
	public void updateGameTimer(double latency) {
		gameTime += latency;
		hours = (int) (gameTime / 3600);
		minutes = (int) ((gameTime % 3600) / 60);
		seconds = (int) (gameTime % 60);
		String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		gameTimer.setText(formattedTime);
	}
	
	public void createExit() {
		BugManager bm = colony.getBm();
		if(bm.isCreatingExit()) {
			createExitLabel.setText("Create Exit");
			bm.setCreatingExit(false);
		}
		else {
			createExitLabel.setText("Creating Exit");
			bm.setCreatingExit(true);
		}
	}
	
	// Used to toggle the view of the home pheromones on and off
	public void setDrawHPheros() {
		if(drawHPheros) {
			drawHPheros = false;
			hPheroToggle.setOpacity(0.1);
			cm.setDrawHPheros(drawHPheros);
			cm.clearPheroDraws();
		}
		else {
			drawHPheros = true;
			hPheroToggle.setOpacity(1.0);
			cm.setDrawHPheros(drawHPheros);
		}
	}
	
	// Used to toggle the view of the food pheromones on and off
	public void setDrawFPheros() {
		if(drawFPheros) {
			drawFPheros = false;
			fPheroToggle.setOpacity(0.1);
			cm.setDrawFPheros(drawFPheros);
			cm.clearPheroDraws();
		}
		else {
			drawFPheros = true;
			fPheroToggle.setOpacity(1.0);
			cm.setDrawFPheros(drawFPheros);
		}
	}
	
	// Used to toggle the view of the pheromone grid on and off
	public void setDrawPherosGrid() {
		if(drawPherosGrid) {
			drawPherosGrid = false;
			gridToggle.setOpacity(0.1);
			cm.setDrawGrid(drawPherosGrid);
			cm.clearPheroDraws();
		}
		else {
			drawPherosGrid = true;
			gridToggle.setOpacity(1.0);
			cm.setDrawGrid(drawPherosGrid);
		}
	}
	
	public void disableAnimationButton() {
		animationButton.setDisable(true);
	}
	
	public void enableAnimationButton() {
		animationButton.setDisable(false);
	}
}
