package application;

import java.util.UUID;

import javafx.scene.shape.Circle;

public class Food extends Circle {
	
	private UUID id;
	private int count;
	
	public Food(double x, double y, int count) {
		setCenterX(x);
		setCenterY(y);
		setRadius(count/10);
		this.count = count;
		id = UUID.randomUUID();
	}

	public int getCount() {
		return count;
	}
	
	public void reduceCount() {
		count -= 1;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void setFoodZone() {
		if(count > 0)
			setRadius(count/10);
	}

	public UUID getUUID() {
		return id;
	}
}
