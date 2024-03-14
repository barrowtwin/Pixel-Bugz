package application.bugz.pheromones;

public class FoodPheromone {
	
	private boolean active;
	private double enterX, enterY, width, height, duration;
	
	public FoodPheromone(double width, double height) {
		duration = 30;
		active = false;
		this.width = width;
		this.height = height;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
		if(this.duration < 0) {
			active = false;
			this.duration = 30;
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

	public void setActive(boolean status) {
		active = status;
		if(active == true) {
			duration = 30;
		}
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
