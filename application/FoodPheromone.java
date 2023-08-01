package application;

public class FoodPheromone {
	
	private double duration;
	private boolean active;
	private double enterX, enterY;
	
	public FoodPheromone() {
		duration = 20;
		active = false;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
		if(this.duration < 0) {
			active = false;
			this.duration = 20;
		}
	}

	public double getEnterX() {
		return enterX;
	}

	public void setEnterX(double enterX) {
		this.enterX = enterX;
	}

	public double getEnterY() {
		return enterY;
	}

	public void setEnterY(double enterY) {
		this.enterY = enterY;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
