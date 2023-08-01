package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		int windowWidth = 1920;
		int windowHeight = 1080;
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());
		primaryStage.setTitle("Pixelz");
		primaryStage.setScene(scene);
		primaryStage.setWidth(windowWidth);
		primaryStage.setHeight(windowHeight);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UNDECORATED);	// Makes the app be in a borderless window
		primaryStage.show();
	}
	
	
	
	// USED FOR TESTING
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		
//		int windowWidth = 500;
//		int windowHeight = 500;
//		StackPane root = new StackPane();
//		Canvas canv = new Canvas(windowWidth, windowHeight);
//		GraphicsContext gc = canv.getGraphicsContext2D();
//		root.getChildren().add(canv);
//		Scene scene = new Scene(root);
//		
//		primaryStage.setTitle("Pixelz");
//		primaryStage.setScene(scene);
//		primaryStage.setWidth(windowWidth);
//		primaryStage.setHeight(windowHeight);
//		primaryStage.show();
//		
//		double centerX = (windowWidth/2);
//		double centerY = (windowHeight/2);
//		Point2D center = new Point2D(centerX,centerY);
//		System.out.println("CenterX: " + center.getX() + "\tCenterY: " + center.getY());
//		gc.setFill(Color.RED);
//		gc.fillOval((windowWidth/2)-150, (windowHeight/2)-150, 300, 300);
//		gc.setFill(Color.BLACK);
//		int size = 5000;
//		List<Point2D> coords = new ArrayList<Point2D>();
//		generateCoords(size, coords);
//		for(int i = 0; i < coords.size(); i++) {
//			System.out.println("Before\tX: " + coords.get(i).getX() + "\tY: " + coords.get(i).getY());
//			Point2D c = coords.get(i).add(center);
//			System.out.println("After\tX: " + coords.get(i).getX() + "\tY: " + coords.get(i).getY());
//			gc.fillOval(c.getX(), c.getY(), 2, 2);
//		}
//		
//		
//	}
//	
//	public void generateCoords(int size, List<Point2D> coords) {
//		Random rand = new Random();
//		double x = 0;
//		double y = 0;
//		double radius = 150;
//		for(int i = 0; i < size; i++) {
//			while(true) {
//				x = rand.nextDouble(radius*2) - radius;
//				y = rand.nextDouble(radius*2) - radius;
//				if((x*x)+(y*y) < radius*radius) {
//					break;
//				}
//			}
//			coords.add(new Point2D(x, y));
//		}
//	}
	
	public static void main(String[] args) {
		launch(args);
	}
}