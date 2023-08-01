package application;

import java.util.UUID;

import javafx.scene.shape.Circle;

public class Food {
	
	private UUID id;
	private Circle area;
	private int count;
	
	public Food(double x, double y, int count) {
		area = new Circle(x, y, count/10);
		this.setCount(count);
		id = UUID.randomUUID();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Circle getArea() {
		return area;
	}

	public void setArea() {
		area.setRadius(count/10);
	}

	public UUID getId() {
		return id;
	}
}
