package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Controller implements Initializable {
	@FXML
	private Slider redBgSlider, greenBgSlider, blueBgSlider, redBugSlider, greenBugSlider, blueBugSlider, bgAlphaSlider, bugAlphaSlider, sizeSlider, speedSlider, focusSlider, forceSlider;
	@FXML
	private Canvas backgroundCanv, bugCanv, directionCanv, objectsCanv, pheromoneCanv;
	@FXML
	private Button closeButton, configButton, animationButton, createBugsButton, followBugButton;
	@FXML
	private TextField numBugsField, bugIndexField;
	@FXML
	private TextFlow title;
	@FXML
	private Text x, y, latencyText;
	
	private Text text1, text2, text3, text4, text5;
	private int redBg, greenBg, blueBg, redBug, greenBug, blueBug, animationTracker, numBugs, size, trackedBug;
	private double speed, focus, force, bgAlpha, bugAlpha, latency, start, stop;
	private boolean trackBug, animating;
	private String bgPaint, bugPaint;
	private Image exitImg;
	private ImageView exitView;
	private Image configImg;
	private ImageView configView;
	
	GraphicsContext backgroundGC, bugGC, directionGC, objectsGC, pheromoneGC;
	BugManager bm;
	CanvasManager cm;
	ObjectsManager om;
	AnimationTimer timer;
	
	
	public Controller() {
		redBgSlider = new Slider();
		greenBgSlider = new Slider();
		blueBgSlider = new Slider();
		redBugSlider = new Slider(0,255,255);
		greenBugSlider = new Slider(0,255,255);
		blueBugSlider = new Slider(0,255,255);
		bgAlphaSlider = new Slider(0,1,0.25);
		bugAlphaSlider = new Slider(0,1,0.25);
		sizeSlider = new Slider(0,10,3);
		speedSlider = new Slider(0,3,1.5);
		focusSlider = new Slider(0,100,20);
		forceSlider = new Slider(0,2,1);
		
		text1 = new Text();
		text2 = new Text();
		text3 = new Text();
		text4 = new Text();
		text5 = new Text();
		latencyText = new Text();
		title = new TextFlow();
		
		numBugsField = new TextField();
		bugIndexField = new TextField();
		
		backgroundCanv = new Canvas(1512,812);
		bugCanv = new Canvas(1512,812);
		objectsCanv = new Canvas(1512,812);
		pheromoneCanv = new Canvas(1512,812);
		directionCanv = new Canvas(100,100);
		
		createBugsButton = new Button();
		followBugButton = new Button();
		closeButton = new Button();
		configButton = new Button();
		animationButton = new Button();
		
		exitImg = new Image("application/resources/x.png");
		exitView = new ImageView(exitImg);
		configImg = new Image("application/resources/cog.png");
		configView = new ImageView(configImg);
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		redBg = (int)redBgSlider.getValue();
		greenBg = (int)greenBgSlider.getValue();
		blueBg = (int)blueBgSlider.getValue();
		redBug = (int)redBugSlider.getValue();
		greenBug = (int)greenBugSlider.getValue();
		blueBug = (int)blueBugSlider.getValue();
		bgAlpha = bgAlphaSlider.getValue();
		bugAlpha = bugAlphaSlider.getValue();
		bgPaint = "" + toHex(redBg) + "" + toHex(greenBg) + "" + toHex(blueBg);
		bugPaint = "" + toHex(redBug) + "" + toHex(greenBug) + "" + toHex(blueBug);
		animationButton.setStyle("-fx-background-color: #2e8a24;");
		
		backgroundGC = backgroundCanv.getGraphicsContext2D();
		bugGC = bugCanv.getGraphicsContext2D();
		directionGC = directionCanv.getGraphicsContext2D();
		objectsGC = objectsCanv.getGraphicsContext2D();
		pheromoneGC = pheromoneCanv.getGraphicsContext2D();
		
		// Manager Creation
		om = new ObjectsManager(200,200);
		bm = new BugManager(om.getFood(), bugCanv.getWidth(), bugCanv.getHeight(), om.getHome());
		cm = new CanvasManager(backgroundGC, bugGC, directionGC, objectsGC, pheromoneGC, bm.getBugs(), om.getFood(), bgPaint, bgAlpha, bm.getPM(), om.getHome());
		cm.drawBackground();
		cm.drawFood();
		cm.drawHome();

		exitView.setFitHeight(closeButton.getHeight());
		exitView.setFitWidth(closeButton.getWidth());
		exitView.setScaleX(0.17);
		exitView.setScaleY(0.17);
		configView.setFitHeight(configButton.getHeight());
		configView.setFitWidth(configButton.getWidth());
		configView.setScaleX(0.17);
		configView.setScaleY(0.17);
		closeButton.setGraphic(exitView);
		configButton.setGraphic(configView);
		closeButton.setOpacity(0.04);
		configButton.setOpacity(0.04);
		
		text1.setText("Pixel ");
		text1.setFill(Paint.valueOf("#ffffff"));
		text1.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text2.setText("B");
		text2.setFill(Paint.valueOf("#8a2724"));
		text2.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text3.setText("u");
		text3.setFill(Paint.valueOf("#248a27"));
		text3.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text4.setText("g");
		text4.setFill(Paint.valueOf("#24358a"));
		text4.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text5.setText("z");
		text5.setFill(Paint.valueOf("ffffff"));
		text5.setFont(Font.font("System", FontPosture.ITALIC, 32));
		title.getChildren().addAll(text1, text2, text3, text4, text5);
		
		animating = false;
		latency = 5;
		trackBug = false;
		trackedBug = 0;
		animationTracker = 0;		
		size = (int)sizeSlider.getValue();
		speed = speedSlider.getValue();
		focus = focusSlider.getValue();
		force = forceSlider.getValue();
		
		redBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				redBg = (int)redBgSlider.getValue();
				bgPaint = "" + toHex(redBg) + "" + toHex(greenBg) + "" + toHex(blueBg);
				cm.setBgColor(bgPaint);
				if(!animating)
					cm.drawBackground();
			}
		});
		
		greenBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				greenBg = (int)greenBgSlider.getValue();
				bgPaint = "" + toHex(redBg) + "" + toHex(greenBg) + "" + toHex(blueBg);
				cm.setBgColor(bgPaint);
				if(!animating)
					cm.drawBackground();
			}
		});
		
		blueBgSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				blueBg = (int)blueBgSlider.getValue();
				bgPaint = "" + toHex(redBg) + "" + toHex(greenBg) + "" + toHex(blueBg);
				cm.setBgColor(bgPaint);
				if(!animating)
					cm.drawBackground();
			}
		});
		
		redBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				redBug = (int)redBugSlider.getValue();
				bugPaint = "" + toHex(redBug) + "" + toHex(greenBug) + "" + toHex(blueBug);
				for(int i = 0; i < bm.getBugs().size(); i++) {
					bm.getBugs().get(i).setPaint(bugPaint);
				}
				if(!animating)
					cm.drawBugs();
			}
		});
		
		greenBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				greenBug = (int)greenBugSlider.getValue();
				bugPaint = "" + toHex(redBug) + "" + toHex(greenBug) + "" + toHex(blueBug);
				for(int i = 0; i < bm.getBugs().size(); i++) {
					bm.getBugs().get(i).setPaint(bugPaint);
				}
				if(!animating)
					cm.drawBugs();
			}
		});
		
		blueBugSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				blueBug = (int)blueBugSlider.getValue();
				bugPaint = "" + toHex(redBug) + "" + toHex(greenBug) + "" + toHex(blueBug);
				for(int i = 0; i < bm.getBugs().size(); i++) {
					bm.getBugs().get(i).setPaint(bugPaint);
				}
				if(!animating)
					cm.drawBugs();
			}
		});
		
		bgAlphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgAlpha = bgAlphaSlider.getValue();
				cm.setBgAlpha(bgAlpha);
				if(!animating)
					cm.drawBackground();
			}
		});
		
		bugAlphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugAlpha = bugAlphaSlider.getValue();
				for(int i = 0; i < bm.getBugs().size(); i++) {
					bm.getBugs().get(i).setAlpha(bugAlpha);
				}
				if(!animating)
					cm.drawBugs();
			}
		});
		
		sizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				size = (int)sizeSlider.getValue();
				if(bm.getBugs().isEmpty())
					return;
				else {
					for(int i = 0; i < bm.getBugs().size(); i ++ ) {
						bm.getBugs().get(i).setSize(size);
					}
				}
				if(!animating)
					cm.drawBugs();
			}
		});
		
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				speed = speedSlider.getValue();
				if(bm.getBugs().isEmpty())
					return;
				else {
					for(int i = 0; i < bm.getBugs().size(); i ++ ) {
						bm.getBugs().get(i).setSpeed(speed);
					}
				}
			}
		});
		
		focusSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				focus = focusSlider.getValue();
				if(bm.getBugs().isEmpty())
					return;
				else {
					for(int i = 0; i < bm.getBugs().size(); i ++ ) {
						bm.getBugs().get(i).setFocus(focus);
					}
				}
			}
		});
		
		forceSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				force = forceSlider.getValue();
				if(bm.getBugs().isEmpty())
					return;
				else {
					for(int i = 0; i < bm.getBugs().size(); i ++ ) {
						bm.getBugs().get(i).setForce(force);
					}
				}
			}
		});
		
		//cm.drawPheromones();
	}
	
	public void trackBug() {
		if(!bm.getBugs().isEmpty()) {
			bm.getBugs().get(trackedBug).setTracked(false);
			trackedBug = Integer.parseInt(bugIndexField.getText());
			trackBug = true;
			bm.getBugs().get(trackedBug).setTracked(true);
		}
	}
	
	public void trackBugUpdate() {
		cm.trackBug(trackedBug);
		x.setText("X: " + (float)bm.getBugs().get(trackedBug).getX());
		y.setText("Y: " + (float)bm.getBugs().get(trackedBug).getY());
		
//		System.out.println("exploring: " + bm.getBugs().get(trackedBug).isExploring() + 
//				"\tcollecting: " + bm.getBugs().get(trackedBug).isCollecting() +
//				"\treturning: " + bm.getBugs().get(trackedBug).isReturning());
	}
	
	public void createBugs(ActionEvent e) {
		numBugs = Integer.parseInt(numBugsField.getText());
		bm.clearBugs();
		bm.createBugs(numBugs, bugCanv.getWidth(), bugCanv.getHeight(), size, speed, bugPaint, bugAlpha);
		cm.drawBugs();
	}
	
	public void animate() {
		if(timer == null) {
			timer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					update();
				}
			};
			stop = System.currentTimeMillis();
			timer.start();
			animationTracker++;
			animationButton.setText("STOP");
			animationButton.setStyle("-fx-background-color: #8a2724;");
			animating = true;
			System.out.println("Started Animation");
		}
		else if(animationTracker == 0) {
			stop = System.currentTimeMillis();
			timer.start();
			animationTracker++;
			animationButton.setText("STOP");
			animationButton.setStyle("-fx-background-color: #8a2724;");
			animating = true;
			System.out.println("Started Animation");
		}
		else {
			timer.stop();
			animationTracker = 0;
			animationButton.setText("START");
			animationButton.setStyle("-fx-background-color: #2e8a24;");
			animating = false;
			System.out.println("Stopped Animation");
		}
	}
	
	public void update() {
		start = System.currentTimeMillis();
		latency = start - stop;
		// 6.94 because that should mean 144 max fps
		if(latency >= 6.94) {
			cm.drawBackground();
			bugUpdate();
			objectsUpdate();
			if(trackBug)
				trackBugUpdate();
			latencyText.setText("" + latency + "ms");
			stop = System.currentTimeMillis();
		}
	}
	
	public void objectsUpdate() {
		om.updateFood();
		cm.drawFood();
		cm.drawHome();
	}
	
	public void bugUpdate() {
		bm.updateBugs(latency);
		cm.drawBugs();
		cm.drawPheromones();
	}
	
	public void close() {
		System.out.println("Click close button");
		Stage stage = (Stage) closeButton.getScene().getWindow();
	    stage.close();
	}
	
	public String toHex(int decimal) {
		int rem;
		String hex = "";
		char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		while(decimal > 0) {
			rem = decimal % 16;
			hex = hexChars[rem] + hex;
			decimal = decimal / 16;
		}
		if(hex.length() == 2)
			return hex;
		else {
			for(int i = hex.length(); i < 2; i++) {
				hex = "0" + hex;
			}
			return hex;
		}
	}
}
