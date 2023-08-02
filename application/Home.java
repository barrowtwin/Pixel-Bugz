package application;

import javafx.scene.shape.Circle;

public class Home extends Circle {
	
	public Home() {
		setCenterX(0);
		setCenterY(0);
		setRadius(0);
	}
	
	public void setHomeX(double x) {
		setCenterX(x);
	}
	
	public void setHomeY(double y) {
		setCenterY(y);
	}
	
	public void setHomeRadius(double radius) {
		setRadius(radius);
	}
}
