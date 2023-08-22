package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.BugManager;
import application.SynchronizedTrackers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class RightController implements Initializable {

	@FXML private StackPane right;
	@FXML private Label queenSpawnToggle, currentFood;
	@FXML private Circle workerSpawnToggle;
	@FXML private Rectangle guardSpawnToggle;
	@FXML private Polygon scoutSpawnToggle;
	@FXML private ProgressBar workerProgress, scoutProgress, guardProgress, queenProgress;
	
	private SynchronizedTrackers trackers;
	private BugManager bm;
	private MainController controller;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void setMainController(MainController controller) {
		this.controller = controller;
	}
	
	public void startGame() {
		bm = controller.getColony().getBm();
		trackers = controller.getColony().getTrackers();
		workerProgress.setProgress(0.0);
		scoutProgress.setProgress(0.0);
		guardProgress.setProgress(0.0);
		queenProgress.setProgress(0.0);
	}
	
	public void updateUI() {
		updateSpawnerProgress(bm.getSpawnerSelector(), bm);
		currentFood.setText("" + (int)trackers.getCurrentFood());
	}
	
	public void updateSpawnerProgress(int selected, BugManager bm) {
		double progress;
		switch(selected) {
			case(0):
				break;
			case(1):
				progress = bm.getWorkerCounter() / 10.0;
				workerProgress.setProgress(progress);
				break;
			case(2):
				progress = bm.getScoutSpawnTimer() / bm.getScoutSpawnTime();
				scoutProgress.setProgress(progress);
				break;
			case(3):
				progress = bm.getGuardSpawnTimer() / bm.getGuardSpawnTime();
				guardProgress.setProgress(progress);
				break;
			case(4):
				progress = bm.getQueenSpawnTimer() / bm.getQueenSpawnTime();
				queenProgress.setProgress(progress);
				break;
			default:
				break;
		}
	}
	
	public void toggleWorker() {
		int selector = bm.getSpawnerSelector();
		if(selector != 1) {
			scoutProgress.setOpacity(0.25);
			guardProgress.setOpacity(0.25);
			queenProgress.setOpacity(0.25);
			workerProgress.setOpacity(1.0);
			bm.setSpawnerSelector(1);
			
		}
		else {
			workerProgress.setOpacity(0.25);
			bm.setSpawnerSelector(0);
		}
	}
	
	public void toggleScout() {
		int selector = bm.getSpawnerSelector();
		if(selector != 2) {
			scoutProgress.setOpacity(1.0);
			guardProgress.setOpacity(0.25);
			queenProgress.setOpacity(0.25);
			workerProgress.setOpacity(0.25);
			bm.setSpawnerSelector(2);
			
		}
		else {
			scoutProgress.setOpacity(0.25);
			bm.setSpawnerSelector(0);
		}
	}
	
	public void toggleGuard() {
		int selector = bm.getSpawnerSelector();
		if(selector != 3) {
			scoutProgress.setOpacity(0.25);
			guardProgress.setOpacity(1.0);
			queenProgress.setOpacity(0.25);
			workerProgress.setOpacity(0.25);
			bm.setSpawnerSelector(3);
			
		}
		else {
			guardProgress.setOpacity(0.25);
			bm.setSpawnerSelector(0);
		}
	}
	
	public void toggleQueen() {
		int selector = bm.getSpawnerSelector();
		if(selector != 4) {
			scoutProgress.setOpacity(0.25);
			guardProgress.setOpacity(0.25);
			queenProgress.setOpacity(1.0);
			workerProgress.setOpacity(0.25);
			bm.setSpawnerSelector(4);
			
		}
		else {
			queenProgress.setOpacity(0.25);
			bm.setSpawnerSelector(0);
		}
	}
}
