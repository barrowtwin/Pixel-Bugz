package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javafx.geometry.Point2D;

public class ObjectsManager {
	
	private final int FOOD_SPAWN_TIME = 150;
	private List<Food> food;
	private Random rand;
	private double timer;
	private double canvasWidth, canvasHeight;
	
	public ObjectsManager(double width, double height) {
		canvasWidth = width;
		canvasHeight = height;
		food = new ArrayList<>();
		rand = new Random();
		timer = 145;
	}
	
	public void updateFood(double latency) {
		timer += latency;
		if(timer >= FOOD_SPAWN_TIME) {
			double x, y, distance;
			do {
				x = rand.nextDouble(-canvasWidth+50, canvasWidth-50);
				y = rand.nextDouble(-canvasHeight+50, canvasHeight-50);
				x += canvasWidth/2;
				y += canvasHeight/2;
				Point2D p1 = new Point2D(canvasWidth/2, canvasHeight/2);
				Point2D p2 = new Point2D(x, y);
				distance = p1.distance(p2);
			} while(x < 50 || x > (canvasWidth-50) || y < 50 || y > (canvasHeight-50) || distance < 400);
			
			int foodSize = (int)rand.nextGaussian(500, Math.sqrt(100));
			createFood(x,y,foodSize);
			timer = 0;
		}
		
		Iterator<Food> iterator = food.iterator();
	    while (iterator.hasNext()) {
	    	Food entry = iterator.next();
	        if (entry.getCount() <= 0) {
	        	iterator.remove();
	        }
	        else
	        	entry.setFoodZone();
	    }
	}
	
	public void createFood(double x, double y, int count) {
		Food newFood = new Food(x,y,count);
		food.add(newFood);
	}

	public List<Food> getFood() {
		return food;
	}
	
	public void setCanvasWidth(double canvWidth) {
		canvasWidth = canvWidth;
	}
	
	public void setCanvasHeight(double canvHeight) {
		canvasHeight = canvHeight;
	}
}
