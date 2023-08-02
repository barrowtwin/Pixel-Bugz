package application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class ObjectsManager {
	
	private HashMap<UUID, Food> food;
	private Home home;
	
	public ObjectsManager() {
		home = new Home();
		food = new HashMap<>();
		createFood(1200,600, 1500);
	}
	
	public void updateFood() {		
		Iterator<HashMap.Entry<UUID,Food>> iterator = food.entrySet().iterator();
	    while (iterator.hasNext()) {
	    	HashMap.Entry<UUID,Food> entry = iterator.next();
	        if (entry.getValue().getCount() <= 0)
	            iterator.remove();
	        else
	        	entry.getValue().setFoodZone();
	    }
	}
	
	public void createFood(double x, double y, int count) {
		Food newFood = new Food(x,y,count);
		food.put(newFood.getUUID(), newFood);
	}

	public HashMap<UUID,Food> getFood() {
		return food;
	}

	public Home getHome() {
		return home;
	}
}
