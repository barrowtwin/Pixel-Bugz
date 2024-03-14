package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.SynchronizedTrackers;
import application.bugz.Bug;
import application.bugz.BugManager;
import application.bugz.colony.Colony;
import application.bugz.colony.MenuColony;
import application.canvas.CanvasManager;
import application.enemy.Enemy;
import application.enemy.EnemyManager;
import application.objects.ObjectsManager;
import application.save.GameData;
import application.settings.GamePreferences;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainMenuController implements Initializable {
	
	private final int SHAPE_SIZE = 180;

	@FXML private HBox root;
	@FXML private StackPane canvasPane, menuPane;
	@FXML private VBox optionsVBox, colonySelectionVBox, colorsVBox, statsVBox;
	@FXML private TextFlow title;
	@FXML private Button bugzButton, colorsButton, closeButton, minimizeButton, optionsButton, statsButton, rightArrowButton, leftArrowButton;
	@FXML private Canvas menuBackgroundCanvas, menuBugCanvas;
	@FXML private Label colonyName, startLabel;
	@FXML private Circle fireColonyShape;
	@FXML private Rectangle carpenterColonyShape, crazyColonyShape;
	@FXML private Polygon pharoahColonyShape;
	@FXML private Slider bugzRedSlider, bugzGreenSlider, bugzBlueSlider, enemyRedSlider, enemyGreenSlider, enemyBlueSlider, bgRedSlider, bgGreenSlider, bgBlueSlider;
	@FXML private Text colonyInfo, bugzRedValue, bugzGreenValue, bugzBlueValue, enemyRedValue, enemyGreenValue, enemyBlueValue, bgRedValue, bgGreenValue, bgBlueValue;
	@FXML private ChoiceBox<String> displayBox, resolutionBox, difficultyBox, screenBox;
	
	private Stage stage;
	private AnimationTimer menuAnimationTimer;
	private Colony menuColony;
	private BugManager bm;
	private CanvasManager cm;
	private EnemyManager em;
	private ObjectsManager om;
	private SynchronizedTrackers trackers;
	private Text text1, text2, text3, text4, text5;
	private TranslateTransition translateMenu;
	private List<Screen> screens;
	private ChangeListener<Boolean> minimizeListener;
	private long lastFrameTime;
	private double screenWidth, screenHeight, canvasWidth, canvasHeight, startGameTimer, xOffset, yOffset;
	private int colonyIndex, bugzRed, bugzGreen, bugzBlue, enemyRed, enemyGreen, enemyBlue, bgRed, bgGreen, bgBlue, resolutionWidth, resolutionHeight;
	private boolean startGame, translatingMenu;
	
	// Game data object
	private GameData gameData;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colonyIndex = 0;
		startGameTimer = 0.0;
		screens = Screen.getScreens();
		setUISizes();
		setComboBoxValues();
		getStoredSettings();
		setSliderValues();
		hideShapes();
		loadColonyInformation(colonyIndex);
		setTitle();
		createColonyShapes();
		setButtonGraphics();
		bugz();
		createMenuColony();
		createManagers();
		lastFrameTime = System.nanoTime();
		animateMenu();
		prepareCanvases();
		createListeners();
	}
	
    public void startGame() throws IOException {
    	menuAnimationTimer.stop();
    	menuAnimationTimer = null;
    	menuColony = null;
    	// Load the FXML and gain access to its controller for setup, then place into scene
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("application/fxml/GameView.fxml"));
        Parent root = loader.load();
        GameController gameController = loader.getController();
        setupController(gameController);
        stage.setScene(new Scene(root));
        root.requestFocus();
    }
    
    public void startGameAnimation(MouseEvent m) {
    	List<List<? extends Bug>> bugz = bm.getBugz();
    	for(int i = 0; i < bugz.size(); i++) {
    		List<? extends Bug> bugGroup = bugz.get(i);
    		for(int j = 0; j < bugGroup.size(); j++) {
    			bugGroup.get(j).setEnergy(-500);
    			bugGroup.get(j).setSpeed(200);
    		}
    	}
    	List<List<? extends Enemy>> enemies = em.getEnemies();
    	for(int i = 0; i < enemies.size(); i++) {
    		List<? extends Enemy> enemyGroup = enemies.get(i);
    		for(int j = 0; j < enemyGroup.size(); j++) {
    			enemyGroup.get(j).setDead();
    		}
    	}
    	bm.clearBugzToRelease();
    	startGame = true;
    	TranslateTransition translateTitle = new TranslateTransition();
    	translateTitle.setNode(title);
    	translateTitle.setToY(-300);
    	translateTitle.setDuration(Duration.seconds(1));
    	FadeTransition fadeStart = new FadeTransition();
    	fadeStart.setNode(startLabel);
    	fadeStart.setToValue(0.0);
    	fadeStart.setFromValue(1.0);
    	fadeStart.setDuration(Duration.seconds(1));
    	translateTitle.play();
    	fadeStart.play();
    	translateMenu = new TranslateTransition();
    	translateMenu.setNode(menuPane);
    	translateMenu.setToX(-700);
    	translateMenu.setDuration(Duration.millis(300));
    	translateMenu.setOnFinished(event -> {
    		try {
				startGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	});
    }
	
	public void createMenuColony() {
		int menuBugzCount = 100;
		menuColony = new MenuColony(canvasWidth, canvasHeight, 60);
		bm = menuColony.getBm();
		trackers = menuColony.getTrackers();
		om = new ObjectsManager(canvasWidth, canvasHeight);
		bm.setFood(om.getFood());
		bm.setQueens(1);
		bm.setExits(1);
		menuColony.setupColony(menuBugzCount,0,0,0);
	}
	
	public void createManagers() {
		em = new EnemyManager(menuColony.getX(), menuColony.getY(), menuColony.getRadius(), canvasWidth, canvasHeight);
		em.setDefenders(menuColony.getBm().getDefenders());
		em.createMenuEnemies();
		em.setTrackers(trackers);
		cm = new CanvasManager();
		cm.setupCM(menuColony.getX(), menuColony.getY(), menuColony.getRadius(), bm.getBugz(), em.getEnemies(), om.getFood(), trackers, 
				bm.getPM().getHPheromone(), bm.getPM().getFPheromone(), bm.getPM().getCellWidth(), bm.getPM().getCellHeight(), bm.getPM().getXCells(), bm.getPM().getYCells());
		cm.setBgRed(bgRed);
		cm.setBgGreen(bgGreen);
		cm.setBgBlue(bgBlue);
		cm.setBgAlpha(1.0);
		cm.setBugRed(bugzRed);
		cm.setBugGreen(bugzGreen);
		cm.setBugBlue(bugzBlue);
		cm.setBugAlpha(0.9);
		cm.setEnemyRed(enemyRed);
		cm.setEnemyGreen(enemyGreen);
		cm.setEnemyBlue(enemyBlue);
		cm.setEnemyAlpha(1.0);
	}
	
	public void animateMenu() {
		menuAnimationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				menuUpdate(now);
			}
		};
		menuAnimationTimer.start();
	}
	
	public void prepareCanvases() {
		menuBackgroundCanvas.setVisible(true);
		menuBugCanvas.setVisible(true);
		cm.generateGracphicsContexts(menuBackgroundCanvas, menuBugCanvas);
		cm.drawBackground();
		cm.menuDraw();
	}
	
	public void menuUpdate(long now) {
		double latency = (now - lastFrameTime) / 1_000_000_000.0;
		menuColony.menuUpdate(latency);
		em.menuUpdate(latency);
		cm.menuDraw();
		// If game has been started, keep checking to see if all the bugz have returned home or 6 seconds have passed
		// When one of those requirements are met, fade the light of the menu colony and slide the menu over
		if(startGame) {
			startGameTimer += latency;
			if(menuColony.getTrackers().getBugzInHome() == bm.getMenuBugz().size() || startGameTimer >= 6.0) {
				cm.extinguishLight();
				if(cm.getFadeLightStatus() && !translatingMenu) {
					translateMenu.play();
					translatingMenu = true;
				}
			}
		}
		lastFrameTime = now;
	}
	
	private SVGPath createIconSVG(String svgPathString, double size) {
		SVGPath svgPath = new SVGPath();
		svgPath.setContent(svgPathString);
		svgPath.setScaleX(size / 24);
		svgPath.setScaleY(size / 24);
		svgPath.setStyle("-fx-fill: WHITE;");
        return svgPath;
    }
	
	public void setTitle() {
		text1 = new Text();
		text2 = new Text();
		text3 = new Text();
		text4 = new Text();
		text5 = new Text();
		text1.setText("Pixel ");
		text1.setFill(Paint.valueOf("#ffffff"));
		text1.setFont(Font.font("System", FontPosture.ITALIC, 76));
		text2.setText("B");
		text2.setFill(Paint.valueOf("#8a2724"));
		text2.setFont(Font.font("System", FontPosture.ITALIC, 76));
		text3.setText("u");
		text3.setFill(Paint.valueOf("#248a27"));
		text3.setFont(Font.font("System", FontPosture.ITALIC, 76));
		text4.setText("g");
		text4.setFill(Paint.valueOf("#24358a"));
		text4.setFont(Font.font("System", FontPosture.ITALIC, 76));
		text5.setText("z");
		text5.setFill(Paint.valueOf("ffffff"));
		text5.setFont(Font.font("System", FontPosture.ITALIC, 76));
		title.getChildren().addAll(text1, text2, text3, text4, text5);
	}
	
	public void setButtonGraphics() {
		String closePath = "M 13.7851 49.5742 L 42.2382 49.5742 C 47.1366 49.5742 49.5743 47.1367 49.5743 42.3086 L 49.5743 13.6914 C 49.5743 8.8633 47.1366 6.4258 42.2382 6.4258 L 13.7851 6.4258 C 8.9101 6.4258 6.4257 8.8398 6.4257 13.6914 L 6.4257 42.3086 C 6.4257 47.1602 8.9101 49.5742 13.7851 49.5742 Z M 19.6210 38.3711 C 18.5195 38.3711 17.6523 37.4805 17.6523 36.3789 C 17.6523 35.8398 17.8632 35.3711 18.2382 35.0196 L 25.2460 27.9883 L 18.2382 20.9571 C 17.8632 20.6289 17.6523 20.1367 17.6523 19.5977 C 17.6523 18.5196 18.5195 17.6523 19.6210 17.6523 C 20.1601 17.6523 20.6288 17.8633 20.9804 18.2383 L 28.0117 25.2461 L 35.0898 18.2149 C 35.4882 17.7930 35.9335 17.6055 36.4492 17.6055 C 37.5273 17.6055 38.4179 18.4961 38.4179 19.5742 C 38.4179 20.1133 38.2539 20.5586 37.8320 20.9336 L 30.8007 27.9883 L 37.8085 34.9727 C 38.1835 35.3477 38.3944 35.8164 38.3944 36.3789 C 38.3944 37.4805 37.5039 38.3711 36.4257 38.3711 C 35.8632 38.3711 35.3710 38.1367 35.0195 37.7852 L 28.0117 30.7539 L 21.0273 37.7852 C 20.6757 38.1602 20.1601 38.3711 19.6210 38.3711 Z";
		String minimizePath = "M 13.7851 49.5742 L 42.2382 49.5742 C 47.1366 49.5742 49.5743 47.1367 49.5743 42.3086 L 49.5743 13.6914 C 49.5743 8.8633 47.1366 6.4258 42.2382 6.4258 L 13.7851 6.4258 C 8.9101 6.4258 6.4257 8.8398 6.4257 13.6914 L 6.4257 42.3086 C 6.4257 47.1602 8.9101 49.5742 13.7851 49.5742 Z M 18.1210 30.0508 C 16.7382 30.0508 15.7773 29.3242 15.7773 28.0118 C 15.7773 26.6758 16.6913 25.9258 18.1210 25.9258 L 37.9257 25.9258 C 39.3554 25.9258 40.2460 26.6758 40.2460 28.0118 C 40.2460 29.3242 39.3085 30.0508 37.9257 30.0508 Z";
		String optionsPath = "M16.014 8.86v1.44a.587.587 0 0 1-.468.556l-1.182.204a.463.463 0 0 1-.114.006 5.902 5.902 0 0 1-.634 1.528.455.455 0 0 1 .078.084l.691.98a.586.586 0 0 1-.062.725l-1.02 1.02a.586.586 0 0 1-.724.061l-.98-.69a.444.444 0 0 1-.085-.078 5.908 5.908 0 0 1-1.544.637.502.502 0 0 1 0 .175l-.182 1.053a.667.667 0 0 1-.633.532h-1.31a.667.667 0 0 1-.633-.532l-.182-1.053a.495.495 0 0 1 0-.175 5.908 5.908 0 0 1-1.544-.637.444.444 0 0 1-.085.077l-.98.691a.586.586 0 0 1-.725-.062l-1.02-1.02a.586.586 0 0 1-.061-.723l.691-.98a.454.454 0 0 1 .077-.085 5.901 5.901 0 0 1-.633-1.528.466.466 0 0 1-.114-.006l-1.182-.204a.586.586 0 0 1-.468-.556V8.86a.586.586 0 0 1 .468-.556L2.636 8.1a.437.437 0 0 1 .114-.005 5.912 5.912 0 0 1 .633-1.528.466.466 0 0 1-.077-.085l-.691-.98a.587.587 0 0 1 .061-.724l1.02-1.02a.587.587 0 0 1 .725-.062l.98.691a.444.444 0 0 1 .085.078 5.903 5.903 0 0 1 1.528-.634.433.433 0 0 1 .005-.114l.204-1.182a.586.586 0 0 1 .556-.468h1.442a.586.586 0 0 1 .556.468l.204 1.182a.448.448 0 0 1 .005.114 5.908 5.908 0 0 1 1.528.634.444.444 0 0 1 .085-.078l.98-.691a.586.586 0 0 1 .724.062l1.02 1.02a.586.586 0 0 1 .062.724l-.691.98a.467.467 0 0 1-.078.085 5.902 5.902 0 0 1 .634 1.528.434.434 0 0 1 .114.005l1.182.204a.587.587 0 0 1 .468.556zm-4.955.72a2.559 2.559 0 1 0-2.56 2.56 2.559 2.559 0 0 0 2.56-2.56z";
		String statsPath = "M 44.7242 49.2514 L 51.3650 49.2514 C 54.4401 49.2514 56 47.7807 56 44.8615 L 56 12.4603 C 56 9.5411 54.4401 8.0480 51.3650 8.0480 L 44.7242 8.0480 C 41.6715 8.0480 40.0892 9.5411 40.0892 12.4603 L 40.0892 44.8615 C 40.0892 47.7807 41.6715 49.2514 44.7242 49.2514 Z M 24.6686 49.2514 L 31.3093 49.2514 C 34.3845 49.2514 35.9667 47.7807 35.9667 44.8615 L 35.9667 18.9673 C 35.9667 16.0480 34.3845 14.5550 31.3093 14.5550 L 24.6686 14.5550 C 21.6157 14.5550 20.0558 16.0480 20.0558 18.9673 L 20.0558 44.8615 C 20.0558 47.7807 21.6157 49.2514 24.6686 49.2514 Z M 4.6351 49.2514 L 11.2758 49.2514 C 14.3510 49.2514 15.9109 47.7807 15.9109 44.8615 L 15.9109 25.4520 C 15.9109 22.5327 14.3510 21.0397 11.2758 21.0397 L 4.6351 21.0397 C 1.5822 21.0397 0 22.5327 0 25.4520 L 0 44.8615 C 0 47.7807 1.5822 49.2514 4.6351 49.2514 Z";
		String rightArrowPath = "M 13.7851 49.5742 L 42.2382 49.5742 C 47.1366 49.5742 49.5743 47.1367 49.5743 42.3086 L 49.5743 13.6914 C 49.5743 8.8633 47.1366 6.4258 42.2382 6.4258 L 13.7851 6.4258 C 8.9101 6.4258 6.4257 8.8398 6.4257 13.6914 L 6.4257 42.3086 C 6.4257 47.1602 8.9101 49.5742 13.7851 49.5742 Z M 42.2851 27.9414 C 42.2851 28.5508 42.0507 29.0196 41.5351 29.5352 L 32.5585 38.4883 C 32.2070 38.8164 31.7382 39.0508 31.1757 39.0508 C 30.0742 39.0508 29.2070 38.2071 29.2070 37.1055 C 29.2070 36.5196 29.4648 36.0274 29.8163 35.6758 L 33.1210 32.4414 L 36.3085 29.7461 L 30.6601 29.9571 L 16.9257 29.9571 C 15.7304 29.9571 14.9335 29.1133 14.9335 27.9414 C 14.9335 26.7696 15.7304 25.9258 16.9257 25.9258 L 30.6601 25.9258 L 36.2851 26.1602 L 33.1210 23.4883 L 29.8163 20.2071 C 29.4882 19.8789 29.2070 19.3633 29.2070 18.8008 C 29.2070 17.6992 30.0742 16.8555 31.1757 16.8555 C 31.7382 16.8555 32.2070 17.0430 32.5585 17.3945 L 41.5351 26.3711 C 42.0742 26.9102 42.2851 27.3789 42.2851 27.9414 Z";
		String leftArrowPath = "M 13.7851 49.5742 L 42.2382 49.5742 C 47.1366 49.5742 49.5743 47.1367 49.5743 42.3086 L 49.5743 13.6914 C 49.5743 8.8633 47.1366 6.4258 42.2382 6.4258 L 13.7851 6.4258 C 8.9101 6.4258 6.4257 8.8398 6.4257 13.6914 L 6.4257 42.3086 C 6.4257 47.1602 8.9101 49.5742 13.7851 49.5742 Z M 14.9335 27.9414 C 14.9335 27.3789 15.1210 26.9102 15.6601 26.3711 L 24.6366 17.3945 C 25.0117 17.0430 25.4804 16.8555 26.0429 16.8555 C 27.1444 16.8555 28.0117 17.6992 28.0117 18.8008 C 28.0117 19.3633 27.7304 19.8789 27.4023 20.2071 L 24.0976 23.4883 L 20.9101 26.1602 L 26.5585 25.9258 L 40.2929 25.9258 C 41.4648 25.9258 42.2617 26.7696 42.2851 27.9414 C 42.2851 29.1133 41.4882 29.9571 40.2929 29.9571 L 26.5585 29.9571 L 20.9101 29.7461 L 24.0976 32.4414 L 27.4023 35.6758 C 27.7539 36.0274 28.0117 36.5196 28.0117 37.1055 C 28.0117 38.2071 27.1444 39.0508 26.0429 39.0508 C 25.4804 39.0508 25.0117 38.8164 24.6366 38.4883 L 15.6601 29.5352 C 15.1444 29.0196 14.9335 28.5508 14.9335 27.9414 Z";
		String bugPath = "M 27.9999 51.9062 C 41.0546 51.9062 51.9063 41.0547 51.9063 28.0000 C 51.9063 14.9219 41.0312 4.0938 27.9765 4.0938 C 14.8983 4.0938 4.0937 14.9219 4.0937 28.0000 C 4.0937 41.0547 14.9218 51.9062 27.9999 51.9062 Z M 27.9999 42.5547 C 24.3436 42.5547 21.1562 38.6172 21.1562 34.3984 C 21.1562 33.7187 21.2733 33.1797 21.5077 32.7109 C 21.1562 32.8047 20.7577 32.8984 20.3827 32.9922 C 19.5155 33.2266 19.2343 33.6016 19.3514 34.375 L 20.3124 40.5859 C 20.4062 41.3125 19.9609 41.7578 19.3749 41.7578 C 18.7421 41.7578 18.4609 41.3359 18.3436 40.7266 L 17.2890 34.0234 C 17.0546 32.5000 17.6640 31.6797 19.3280 31.2813 L 25.0234 29.9453 L 25.0234 29.8047 C 24.4609 29.5469 24.0858 28.9844 23.9218 28.4219 L 20.1014 27.8594 C 18.4843 27.625 17.7577 26.7344 17.7577 25.1406 L 17.7577 20.6875 C 17.7577 20.0547 18.1327 19.7031 18.7421 19.7031 C 19.3514 19.7031 19.7030 20.0781 19.7030 20.6875 L 19.7030 24.7422 C 19.7030 25.6562 20.1952 25.9609 21.0390 26.0781 L 23.8514 26.4766 C 23.9921 26.0781 24.2499 25.7266 24.6249 25.4922 L 24.6249 25.3516 C 22.4218 24.9531 21.3202 23.6406 21.3202 21.6484 C 21.3202 20.0078 21.9296 18.4609 22.9843 17.2891 L 22.8905 17.2187 C 21.5546 16.4687 21.4140 14.9922 22.2343 13.5625 L 23.0077 12.2969 C 23.2421 11.875 23.5234 11.6640 23.9452 11.6640 C 24.4374 11.6640 24.8358 12.0391 24.8358 12.5547 C 24.8358 12.7891 24.8124 12.9062 24.6483 13.1640 L 23.9687 14.2187 C 23.5468 14.8984 23.5702 15.4844 24.0155 15.9062 C 24.1327 16.0000 24.2030 16.0469 24.3202 16.1172 C 25.3749 15.3906 26.6405 14.9687 27.9999 14.9687 C 29.6171 14.9687 30.7890 15.5078 31.7265 16.1172 C 31.7968 16.0703 31.9140 16.0000 32.0077 15.9062 C 32.4530 15.4844 32.4765 14.8984 32.0780 14.2187 L 31.3749 13.1640 C 31.2343 12.9062 31.2109 12.7891 31.2109 12.5547 C 31.2109 12.0391 31.5858 11.6640 32.1249 11.6640 C 32.4999 11.6640 32.7812 11.875 33.0155 12.2969 L 33.7890 13.5625 C 34.6093 14.9922 34.3983 16.3281 33.1562 17.2187 L 33.0155 17.3360 C 34.0468 18.4844 34.6796 20.0078 34.6796 21.6484 C 34.6796 23.6406 33.5780 24.9531 31.3749 25.3516 L 31.3749 25.4922 C 31.7265 25.7266 31.9843 26.0781 32.1249 26.4766 L 34.9609 26.0781 C 35.8046 25.9609 36.2733 25.6562 36.2733 24.7422 L 36.2733 20.6875 C 36.2733 20.0781 36.6483 19.7031 37.2577 19.7031 C 37.8436 19.7031 38.2421 20.0547 38.2421 20.6875 L 38.2421 25.1406 C 38.2421 26.7344 37.4921 27.625 35.8983 27.8594 L 32.0780 28.4219 C 31.9140 28.9844 31.5155 29.5469 30.9765 29.8047 L 30.9765 29.9453 L 36.6483 31.2813 C 38.3124 31.6797 38.9218 32.5000 38.6874 34.0234 L 37.6327 40.7266 C 37.5155 41.3359 37.2577 41.7578 36.6014 41.7578 C 36.0155 41.7578 35.5702 41.3125 35.6874 40.5859 L 36.6249 34.375 C 36.7655 33.6016 36.4609 33.2266 35.5936 32.9922 C 35.2421 32.8984 34.8436 32.8047 34.4687 32.7109 C 34.7030 33.1797 34.8202 33.7187 34.8202 34.3984 C 34.8202 38.6172 31.6327 42.5547 27.9999 42.5547 Z";
		String colorsPath = "M 19.3164 22.7383 L 34.9726 38.3945 C 36.7305 40.1523 38.5586 39.8711 40.1992 38.2305 L 51.8242 26.6523 C 53.4649 25.0117 53.7226 23.1836 51.9649 21.4258 L 49.5975 19.0820 C 46.8087 21.5664 38.9805 27.1445 37.7617 25.9258 C 37.3633 25.5274 37.6914 25.1758 37.9258 24.9414 C 40.5039 22.2930 43.9492 18.0742 44.3946 13.8320 L 32.0430 1.4805 C 30.5899 .4 28.4336 .8 27.2617 1.7383 C 24.8946 5.0898 24.9414 10.6914 19.3164 17.3945 C 17.7695 19.2695 17.5117 20.9336 19.3164 22.7383 Z M 5.4883 52.8320 C 8.5821 55.8789 12.4726 55.9726 15.2383 53.2070 C 17.4180 51.0508 19.6914 45.2617 21.2617 42.5664 L 27.8242 49.1055 C 29.6992 50.9805 31.7852 51.0274 33.5430 49.2695 L 35.0195 47.7695 C 36.7773 46.0117 36.7305 43.9258 34.8789 42.0508 L 16.2930 23.4648 C 14.4180 21.5664 12.3555 21.5195 10.5742 23.3242 L 9.0977 24.8008 C 7.2930 26.5820 7.3399 28.6211 9.2383 30.5195 L 15.7539 37.0586 C 13.0821 38.6289 7.2695 40.9258 5.1133 43.0820 C 2.2774 45.8945 2.4180 49.8086 5.4883 52.8320 Z M 8.6758 49.5273 C 7.9024 48.7305 7.8789 47.4648 8.6758 46.6680 C 9.4961 45.8711 10.7617 45.8711 11.5586 46.6680 C 12.3555 47.4883 12.3555 48.7070 11.5586 49.5273 C 10.7617 50.3476 9.4961 50.3711 8.6758 49.5273 Z";
		bugzButton.setGraphic(createIconSVG(bugPath, 20));
		colorsButton.setGraphic(createIconSVG(colorsPath,17));
		closeButton.setGraphic(createIconSVG(closePath, 20));
		minimizeButton.setGraphic(createIconSVG(minimizePath, 20));
		optionsButton.setGraphic(createIconSVG(optionsPath, 55));
		statsButton.setGraphic(createIconSVG(statsPath, 15));
		rightArrowButton.setGraphic(createIconSVG(rightArrowPath,20));
		leftArrowButton.setGraphic(createIconSVG(leftArrowPath,20));
	}
	
	public void createColonyShapes() {
		fireColonyShape.setCenterX(0);
		fireColonyShape.setCenterY(0);
		fireColonyShape.setRadius(SHAPE_SIZE/2);
		fireColonyShape.setFill(Color.rgb(255, 255, 255, 1.0));
		
		carpenterColonyShape.setX(0);
		carpenterColonyShape.setY(0);
		carpenterColonyShape.setWidth(SHAPE_SIZE);
		carpenterColonyShape.setHeight(SHAPE_SIZE);
		carpenterColonyShape.setFill(Color.rgb(255, 255, 255, 1.0));
		
		double triangleHeight = SHAPE_SIZE * Math.sqrt(3) / 2.0;
        double centerX = SHAPE_SIZE / 2.0;
        double centerY = triangleHeight;
        double x1 = centerX - SHAPE_SIZE / 2.0;
        double y1 = centerY + triangleHeight / 2.0;
        double x2 = centerX + SHAPE_SIZE / 2.0;
        double y2 = centerY + triangleHeight / 2.0;
        double x3 = centerX;
        double y3 = centerY - triangleHeight / 2.0;
        pharoahColonyShape.getPoints().setAll(x1, y1, x2, y2, x3, y3);
        pharoahColonyShape.setFill(Color.rgb(255, 255, 255, 1.0));
		
        crazyColonyShape.setX(0);
        crazyColonyShape.setY(0);
        crazyColonyShape.setWidth(SHAPE_SIZE);
        crazyColonyShape.setHeight(SHAPE_SIZE);
        crazyColonyShape.setFill(Color.rgb(255, 255, 255, 1.0));
		Rotate rotate = new Rotate(45, SHAPE_SIZE / 2, SHAPE_SIZE / 2);
		crazyColonyShape.getTransforms().add(rotate);
	}
	
	@FXML
	public void lookRight() {
		if(colonyIndex < 3) {
			colonyIndex++;
		}
		else {
			colonyIndex = 0;
		}
		loadColonyInformation(colonyIndex);
	}
	
	@FXML
	public void lookLeft() {
		if(colonyIndex > 0) {
			colonyIndex--;
		}
		else {
			colonyIndex = 3;
		}
		loadColonyInformation(colonyIndex);
	}
	
	public void loadColonyInformation(int index) {
		switch(index) {
			case(0):
				hideShapes();
				fireColonyShape.setOpacity(1.0);
				colonyName.setText("Fire Colony");
				colonyInfo.setText("Here is a description of the Fire colony. Any special features unique to this colony will be listed here");
				break;
			case(1):
				hideShapes();
				carpenterColonyShape.setOpacity(1.0);
				colonyName.setText("Carpenter Colony");
				colonyInfo.setText("Here is a description of the Carpenter colony. Any special features unique to this colony will be listed here");
				break;
			case(2):
				hideShapes();
				pharoahColonyShape.setOpacity(1.0);
				colonyName.setText("Pharoah Colony");
				colonyInfo.setText("Here is a description of the Pharoah colony. Any special features unique to this colony will be listed here");
				break;
			case(3):
				hideShapes();
				crazyColonyShape.setOpacity(1.0);
				colonyName.setText("Crazy Colony");
				colonyInfo.setText("Here is a description of the Crazy colony. Any special features unique to this colony will be listed here");
				break;
			default:
				break;
		}
	}
	
	public void hideShapes() {
		fireColonyShape.setOpacity(0.0);
		carpenterColonyShape.setOpacity(0.0);
		pharoahColonyShape.setOpacity(0.0);
		crazyColonyShape.setOpacity(0.0);
	}
	
	public void hideGrids() {
		colorsVBox.setVisible(false);
		colorsVBox.setMouseTransparent(true);
		colonySelectionVBox.setVisible(false);
		colonySelectionVBox.setMouseTransparent(true);
		optionsVBox.setVisible(false);
		optionsVBox.setMouseTransparent(true);
		statsVBox.setVisible(false);
		statsVBox.setMouseTransparent(true);
	}
	
	public void clearButtons() {
		optionsButton.setStyle("-fx-background-color: transparent;");
		statsButton.setStyle("-fx-background-color: transparent;");
		bugzButton.setStyle("-fx-background-color: transparent;");
		colorsButton.setStyle("-fx-background-color: transparent;");
		optionsButton.getGraphic().setStyle("-fx-fill: WHITE;");
		statsButton.getGraphic().setStyle("-fx-fill: WHITE;");
		bugzButton.getGraphic().setStyle("-fx-fill: WHITE;");
		colorsButton.getGraphic().setStyle("-fx-fill: WHITE;");
	}
	
	@FXML
	public void close() {
		menuAnimationTimer.stop();
    	menuAnimationTimer = null;
    	menuColony = null;
		Platform.exit();
	}
	
	@FXML
	public void minimize() {
		stage.setIconified(true);
	}
	
	@FXML
	public void options() {
		hideGrids();
		clearButtons();
		optionsButton.setStyle("-fx-background-color: WHITE;");
		optionsButton.getGraphic().setStyle("-fx-fill: #050505;");
		optionsVBox.setVisible(true);
		optionsVBox.setMouseTransparent(false);
	}
	
	@FXML
	public void stats() {
		hideGrids();
		clearButtons();
		statsButton.setStyle("-fx-background-color: WHITE;");
		statsButton.getGraphic().setStyle("-fx-fill: #050505;");
		statsVBox.setVisible(true);
		statsVBox.setMouseTransparent(false);
	}
	
	@FXML
	public void colors() {
		hideGrids();
		clearButtons();
		colorsButton.setStyle("-fx-background-color: WHITE;");
		colorsButton.getGraphic().setStyle("-fx-fill: #050505;");
		colorsVBox.setVisible(true);
		colorsVBox.setMouseTransparent(false);
	}
	
	@FXML
	public void bugz() {
		hideGrids();
		clearButtons();
		bugzButton.setStyle("-fx-background-color: WHITE;");
		bugzButton.getGraphic().setStyle("-fx-fill: #050505;");
		colonySelectionVBox.setVisible(true);
		colonySelectionVBox.setMouseTransparent(false);
	}
	
	public void setSliderValues() {
		bugzRedSlider.setValue(bugzRed);
		bugzGreenSlider.setValue(bugzGreen);
		bugzBlueSlider.setValue(bugzBlue);
		bugzRedValue.setText("" + bugzRed);
		bugzGreenValue.setText("" + bugzGreen);
		bugzBlueValue.setText("" + bugzBlue);
		
		enemyRedSlider.setValue(enemyRed);
		enemyGreenSlider.setValue(enemyGreen);
		enemyBlueSlider.setValue(enemyBlue);
		enemyRedValue.setText("" + enemyRed);
		enemyGreenValue.setText("" + enemyGreen);
		enemyBlueValue.setText("" + enemyBlue);
		
		bgRedSlider.setValue(bgRed);
		bgGreenSlider.setValue(bgGreen);
		bgBlueSlider.setValue(bgBlue);
		bgRedValue.setText("" + bgRed);
		bgGreenValue.setText("" + bgGreen);
		bgBlueValue.setText("" + bgBlue);
	}
	
	public void createListeners() {
		bugzRedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugzRed = (int)bugzRedSlider.getValue();
				cm.setBugRed(bugzRed);
				GamePreferences.setBugRed(bugzRed);
				bugzRedValue.setText("" + bugzRed);
			}
		});
		
		bugzGreenSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugzGreen = (int)bugzGreenSlider.getValue();
				cm.setBugGreen(bugzGreen);
				GamePreferences.setBugGreen(bugzGreen);
				bugzGreenValue.setText("" + bugzGreen);
			}
		});
		
		bugzBlueSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bugzBlue = (int)bugzBlueSlider.getValue();
				cm.setBugBlue(bugzBlue);
				GamePreferences.setBugBlue(bugzBlue);
				bugzBlueValue.setText("" + bugzBlue);
			}
		});
		
		enemyRedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				enemyRed = (int)enemyRedSlider.getValue();
				cm.setEnemyRed(enemyRed);
				GamePreferences.setEnemyRed(enemyRed);
				enemyRedValue.setText("" + enemyRed);
			}
		});
		
		enemyGreenSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				enemyGreen = (int)enemyGreenSlider.getValue();
				cm.setEnemyGreen(enemyGreen);
				GamePreferences.setEnemyGreen(enemyGreen);
				enemyGreenValue.setText("" + enemyGreen);
			}
		});
		
		enemyBlueSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				enemyBlue = (int)enemyBlueSlider.getValue();
				cm.setEnemyBlue(enemyBlue);
				GamePreferences.setEnemyBlue(enemyBlue);
				enemyBlueValue.setText("" + enemyBlue);
			}
		});
		
		
		bgRedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgRed = (int)bgRedSlider.getValue();
				cm.setBgRed(bgRed);
				GamePreferences.setBgRed(bgRed);
				bgRedValue.setText("" + bgRed);
			}
		});
		
		bgGreenSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgGreen = (int)bgGreenSlider.getValue();
				cm.setBgGreen(bgGreen);
				GamePreferences.setBgGreen(bgGreen);
				bgGreenValue.setText("" + bgGreen);
			}
		});
		
		bgBlueSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				bgBlue = (int)bgBlueSlider.getValue();
				cm.setBgBlue(bgBlue);
				GamePreferences.setBgBlue(bgBlue);
				bgBlueValue.setText("" + bgBlue);
			}
		});
		
		resolutionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    String selectedResolution = newValue;
		    String[] parts = selectedResolution.split("x");
		    if(parts.length == 2) {
		    	int width = Integer.parseInt(parts[0]);
		    	int height = Integer.parseInt(parts[1]);
		    	GamePreferences.setResolution(width, height);
		    }
		});
		
		difficultyBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    String selectedDifficulty = newValue;
		    if(selectedDifficulty.equals("Easy")) {
		    	GamePreferences.setDifficulty(1);
		    }
		    else {
		    	GamePreferences.setDifficulty(2);
		    }
		});
		
		displayBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    String selectedDisplay = newValue;
		    if(selectedDisplay.equals("Fullscreen")) {
		    	GamePreferences.setFullScreen(true);
		    	stage.setFullScreen(true);
		    }
		    else if(selectedDisplay.equals("Borderless Windowed")) {
		    	GamePreferences.setStageStyle(StageStyle.UNDECORATED);
		    	GamePreferences.setFullScreen(false);
		    	stage.setFullScreen(false);
		    }
		    else {
		    	GamePreferences.setStageStyle(StageStyle.DECORATED);
		    	GamePreferences.setFullScreen(false);
		    	stage.setFullScreen(false);
		    }
		});
		
		screenBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    String screenNumber =  newValue.substring(newValue.length() - 1);
		    int number = Integer.parseInt(screenNumber);
		    GamePreferences.setScreen(number);
		    Rectangle2D screenBounds = screens.get(number-1).getBounds();
		    stage.setX(screenBounds.getMinX());
		    stage.setY(screenBounds.getMinY());
		    stage.centerOnScreen();
		});
	}
	
	public void setComboBoxValues() {
		// Display Box
		String display1 = "Fullscreen";
		String display2 = "Borderless Windowed";
		displayBox.getItems().addAll(display1, display2);
		if(GamePreferences.isFullScreen()) {
			displayBox.setValue(display1);
		}
		else {
			displayBox.setValue(display2);
		}
		
		// Resolution Box
		// These are temp values until a better method for setting this up is worked out
		int resolution4k = 2160;
		int resolution1440p = 1440;
		int resolution1080p = 1080;
		if(screenHeight == resolution4k) {
			resolutionBox.getItems().addAll("3840x2160", "2560x1440", "1920x1080", "1280x720");
		}
		else if(screenHeight == resolution1440p) {
			resolutionBox.getItems().addAll("2560x1440", "1920x1080", "1280x720");
		}
		else if(screenHeight == resolution1080p) {
			resolutionBox.getItems().addAll("1920x1080", "1280x720");
		}
		else {
			resolutionBox.getItems().addAll("1280x720");
		}
		resolutionBox.setValue("" + resolutionWidth + "x" + resolutionHeight);
		
		// Screen Box
		if(screens.size() == 1) {
			screenBox.getItems().add("Screen 1");
			screenBox.setValue("Screen 1");
			screenBox.setDisable(true);
			resolutionBox.setOpacity(0.5);
		}
		else {
			for(int i = 0, screen = 1; i < screens.size(); i++, screen++) {
				screenBox.getItems().add("Screen " + screen);
			}
			screenBox.setValue("Screen " + GamePreferences.getScreen());
		}
		
		// Difficulty Box
		String difficulty1 = "Easy";
		String difficulty2 = "Not Easy";
		difficultyBox.getItems().addAll(difficulty1, difficulty2);
		int currentDifficulty = GamePreferences.getDifficulty();
		if(currentDifficulty == 1) {
			difficultyBox.setValue(difficulty1);
		}
		else {
			difficultyBox.setValue(difficulty2);
		}
	}
	
	public void getStoredSettings() {
		// retrieve stored settings from file then update all settings variables
		bugzRed = GamePreferences.getBugRed();
		bugzGreen = GamePreferences.getBugGreen();
		bugzBlue = GamePreferences.getBugBlue();
		
		bgRed = GamePreferences.getBgRed();
		bgGreen = GamePreferences.getBgGreen();
		bgBlue = GamePreferences.getBgBlue();
		
		enemyRed = GamePreferences.getEnemyRed();
		enemyGreen = GamePreferences.getEnemyGreen();
		enemyBlue = GamePreferences.getEnemyBlue();
	}
	
	public void populateStats() {
		
	}
	
	public void setUISizes() {
		Screen screen = Screen.getPrimary();
		Rectangle2D screenBounds = screen.getBounds();
		screenWidth = screenBounds.getWidth();
		screenHeight = screenBounds.getHeight();
		resolutionWidth = GamePreferences.getResolutionWidth();
		resolutionHeight = GamePreferences.getResolutionHeight();
		if(resolutionHeight != 1080) {
			double scaleX = resolutionWidth/1920.0;
			double scaleY = resolutionHeight/1080.0;
			root.setScaleX(scaleX);
			root.setScaleY(scaleY);
		}
		canvasWidth = menuBugCanvas.getWidth();
		canvasHeight = menuBugCanvas.getHeight();
	}
	
	@FXML
	public void pressedMenuBar(MouseEvent m) {
		xOffset = m.getSceneX();
		yOffset = m.getSceneY();
	}
	
	@FXML
	public void clickedMenuBar(MouseEvent m) {
		if(m.getClickCount() == 2) {
			stage.centerOnScreen();
		}
	}
	
	@FXML
	public void draggedMenuBar(MouseEvent m) {
		stage.setX(m.getScreenX() - xOffset);
		stage.setY(m.getScreenY() - yOffset);
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		stage = primaryStage;
	}
	
	public Stage getPrimaryStage() {
		return stage;
	}
	
	public void setupController(GameController controller) {
		controller.setPrimaryStage(stage);
		stage.iconifiedProperty().removeListener(minimizeListener);
		controller.setupStage(stage);
	}
	
	public void setupStage(Stage stage) {
		// Makes a listener that will stop/start the menu animation when the program gets minimized and attaches it to the stage to listen for minimizing
		minimizeListener = (observable, oldValue, newValue) -> {
			if(newValue) {
				menuAnimationTimer.stop();
			}
			else {
				menuAnimationTimer.start();
				root.requestFocus();
			}
		};
		stage.iconifiedProperty().addListener(minimizeListener);
	}
	
	public void setupGameData(GameData data) {
		gameData = data;
		populateStats();
	}

	public double getWindowWidth() {
		return screenWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.screenWidth = windowWidth;
	}

	public double getWindowHeight() {
		return screenHeight;
	}

	public void setWindowHeight(double windowHeight) {
		this.screenHeight = windowHeight;
	}

	public int getResolutionWidth() {
		return resolutionWidth;
	}

	public void setResolutionWidth(int resolutionWidth) {
		this.resolutionWidth = resolutionWidth;
	}

	public int getResolutionHeight() {
		return resolutionHeight;
	}

	public void setResolutionHeight(int resolutionHeight) {
		this.resolutionHeight = resolutionHeight;
	}
}
