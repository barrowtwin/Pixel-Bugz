package application;

public class HomePheromone {
	
	private boolean active;
	private double shortestTime, entranceX, entranceY;
	
	public HomePheromone() {
		active = false;
		shortestTime = 0;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setShortestTime(double time) {
		shortestTime = time;
	}
	
	public double getShortestTime() {
		return shortestTime;
	}

	public double getEntranceX() {
		return entranceX;
	}

	public void setEntranceX(double entranceX) {
		if(!active)
			active = true;
		this.entranceX = entranceX;
	}

	public double getEntranceY() {
		return entranceY;
	}

	public void setEntranceY(double entranceY) {
		if(!active)
			active = true;
		this.entranceY = entranceY;
	}
}
