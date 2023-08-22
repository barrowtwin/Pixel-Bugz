package application.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import application.BugManager;
import application.CanvasManager;
import application.Enemy;
import application.EnemyManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CenterController implements Initializable {
	
	private final int POSITIONS = 10;
	
	@FXML private StackPane center;
	@FXML private Slider redBgSlider, greenBgSlider, blueBgSlider, redBugSlider, greenBugSlider, blueBugSlider, bgAlphaSlider, bugAlphaSlider, sizeSlider, speedSlider, focusSlider, forceSlider;
	@FXML private Canvas backgroundCanv, bugCanv, objectsCanv, pheromoneCanv;
	@FXML private Circle previewBug;
	@FXML private Rectangle previewBG;
	@FXML private VBox configWindow;
	
	private MainController controller;
	CanvasManager cm;
	BugManager bm;
	EnemyManager em;
	Enemy pickedUpEnemy;
	private List<Point2D> recentMousePositions;
	private int bgRed, bgGreen, bgBlue, bugRed, bugGreen, bugBlue, size;
	private double speed, focus, force, bgAlpha, bugAlpha;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bgRed = (int)redBgSlider.getValue();
		bgGreen = (int)greenBgSlider.getValue();
		bgBlue = (int)blueBgSlider.getValue();
		bugRed = (int)redBugSlider.getValue();
		bugGreen = (int)greenBugSlider.getValue();
		bugBlue = (int)blueBugSlider.getValue();
		bgAlpha = bgAlphaSlider.getValue();
		bugAlpha = bugAlphaSlider.getValue();
		size = (int)sizeSlider.getValue();
		speed = speedSlider.getValue();
		focus = focusSlider.getValue();
		force = forceSlider.getValue();
		previewBug.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha));
		previewBG.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
		pheromoneCanv.setOpacity(0.4);
		recentMousePositions = new ArrayList<>();
		
		redBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgRed = (int)redBgSlider.getValue();
				previewBG.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
				cm.setBgRed(bgRed);
				cm.drawBackground();
			}
		});
		
		greenBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgGreen = (int)greenBgSlider.getValue();
				previewBG.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
				cm.setBgGreen(bgGreen);
				cm.drawBackground();
			}
		});
		
		blueBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgBlue = (int)blueBgSlider.getValue();
				previewBG.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
				cm.setBgBlue(bgBlue);
				cm.drawBackground();
			}
		});
		
		redBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugRed = (int)redBugSlider.getValue();
				previewBug.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha));
				bm.setBugRed(bugRed);
				cm.drawBugz();
			}
		});
		
		greenBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugGreen = (int)greenBugSlider.getValue();
				previewBug.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha));
				bm.setBugGreen(bugGreen);
				cm.drawBugz();
			}
		});
		
		blueBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugBlue = (int)blueBugSlider.getValue();
				previewBug.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha));
				bm.setBugBlue(bugBlue);
				cm.drawBugz();
			}
		});
		
		bgAlphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgAlpha = bgAlphaSlider.getValue();
				previewBG.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
				cm.setBgAlpha(bgAlpha);
				cm.drawBackground();
			}
		});
		
		bugAlphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugAlpha = bugAlphaSlider.getValue();
				previewBug.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha));
				bm.setBugAlpha(bugAlpha);
				cm.drawBugz();
			}
		});
		
		sizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				size = (int)sizeSlider.getValue();
				bm.setBugSize(size);
				em.setEnemySize(size);
			}
		});
		
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				speed = speedSlider.getValue();
				bm.setBugSpeed(speed);
				em.setEnemySpeed(speed);
			}
		});
		
		focusSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				focus = focusSlider.getValue();
				bm.setBugFocus(focus);
				em.setEnemyFocus(focus);
			}
		});
		
		forceSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				force = forceSlider.getValue();
				bm.setBugForce(force);
				em.setEnemyForce(force);
			}
		});
	}
	
	public void setMainController(MainController controller) {
		this.controller = controller;
	}
	
	public void startGame() {
		bgRed = (int)redBgSlider.getValue();
		bgGreen = (int)greenBgSlider.getValue();
		bgBlue = (int)blueBgSlider.getValue();
		bugRed = (int)redBugSlider.getValue();
		bugGreen = (int)greenBugSlider.getValue();
		bugBlue = (int)blueBugSlider.getValue();
		bgAlpha = bgAlphaSlider.getValue();
		bugAlpha = bugAlphaSlider.getValue();
		size = (int)sizeSlider.getValue();
		speed = speedSlider.getValue();
		focus = focusSlider.getValue();
		force = forceSlider.getValue();
	}
	
	public void setupCM(CanvasManager cm) {
		this.cm = cm;
		cm.generateGracphicsContexts(backgroundCanv, bugCanv, objectsCanv, pheromoneCanv);
		cm.setBgRed(bgRed);
		cm.setBgGreen(bgGreen);
		cm.setBgBlue(bgBlue);
		cm.setBgAlpha(bgAlpha);
		cm.drawBackground();
	}
	
	public void setupBM(BugManager bm) {
		this.bm = bm;
		bm.setBugRed(bugRed);
		bm.setBugGreen(bugGreen);
		bm.setBugBlue(bugBlue);
		bm.setBugAlpha(bugAlpha);
		bm.setBugSpeed(speed);
		bm.setBugSize(size);
		bm.setBugFocus(focus);
		bm.setBugForce(force);
		bm.createStarterBugz();
	}
	
	public void setupEM(EnemyManager em) {
		this.em = em;
		em.setEnemyRed(125);
		em.setEnemyGreen(0);
		em.setEnemyBlue(0);
		em.setEnemyAlpha(1.0);
		em.setEnemySpeed(speed);
		em.setEnemySize(size);
		em.setEnemyFocus(focus);
		em.setEnemyForce(force);
	}
	
	public void updateForConfig(boolean config) {
		if(config) {
			configWindow.setDisable(false);
			configWindow.setVisible(true);
		}
		else {
			configWindow.setDisable(true);
			configWindow.setVisible(false);
		}
	}
	
	public void checkForEnemy(MouseEvent m) {
		if(controller.isAnimating()) {
			int x = (int)m.getX();
			int y = (int)m.getY();
			for(int i = 0; i < em.getEnemies().size(); i++) {
				Enemy enemy = em.getEnemies().get(i);
				double test = new Point2D(x, y).distance(enemy.getX(), enemy.getY());
				if(test <= enemy.getSize()) {
					enemy.setPickedUp(true);
					pickedUpEnemy = enemy;
					recentMousePositions.clear();
					recentMousePositions.add(new Point2D(x,y));
					break;
				}
			}
		}
	}
	
	public void dragEnemy(MouseEvent m) {
		if(pickedUpEnemy != null) {
			int x = (int)m.getX();
			int y = (int)m.getY();
			if(recentMousePositions.size() == POSITIONS) {
				recentMousePositions.remove(0);
				recentMousePositions.add(new Point2D(x, y));
			}
			else {
				recentMousePositions.add(new Point2D(x, y));
			}
			pickedUpEnemy.setX(x);
			pickedUpEnemy.setY(y);
		}
	}
	
	public void releaseEnemy(MouseEvent m) {
		if(pickedUpEnemy != null) {
			pickedUpEnemy.setPickedUp(false);
			int size = recentMousePositions.size();
			double velocityX = 0;
			double velocityY = 0;
			for(int i = size-1; i > 1; i--) {
				velocityX += recentMousePositions.get(i).getX() - recentMousePositions.get(i-1).getX();
				velocityY += recentMousePositions.get(i).getY() - recentMousePositions.get(i-1).getY();
			}
			pickedUpEnemy.setThrown(true, velocityX, velocityY);
			pickedUpEnemy = null;
		}
	}
	
	public double getCanvWidth() {
		return bugCanv.getWidth();
	}
	
	public double getCanvHeight() {
		return bugCanv.getHeight();
	}
}
