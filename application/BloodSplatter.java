package application;

public class BloodSplatter {
	
	private double x, y;
	private int radius;
	
	public BloodSplatter(double xOffset, double yOffset, int radius) {
		this.x = xOffset;
		this.y = yOffset;
		this.radius = radius;
	}

	public double getX() {
		return x;
	}
	
	public void setX(double xOffset) {
		x = xOffset + x;
	}

	public double getY() {
		return y;
	}
	
	public void setY(double yOffset) {
		y = yOffset + y;
	}

	public int getRadius() {
		return radius;
	}
}
