package application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javafx.scene.shape.Circle;

public class ObjectsManager {
	
	private HashMap<UUID, Food> food;
	private Circle home;
	private int homeRadius;
	
	public ObjectsManager(double homeX, double homeY) {
		homeRadius = 50;
		home = new Circle(homeX, homeY, homeRadius);
		food = new HashMap<>();
		createFood(1200,600, 3000);
	}
	
	public void updateFood() {		
		Iterator<HashMap.Entry<UUID,Food>> iterator = food.entrySet().iterator();
	    while (iterator.hasNext()) {
	    	HashMap.Entry<UUID,Food> entry = iterator.next();
	        if (entry.getValue().getCount() <= 0)
	            iterator.remove();
	    }
	}
	
	public void createFood(double x, double y, int count) {
		Food newFood = new Food(x,y,count);
		food.put(newFood.getId(), newFood);
	}

	public HashMap<UUID,Food> getFood() {
		return food;
	}

	public Circle getHome() {
		return home;
	}

	public void setHome(Circle home) {
		this.home = home;
	}
}
