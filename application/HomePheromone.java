package application;

public class HomePheromone {
	
	private boolean active;
	private double shortestTime, entranceX, entranceY, width, height;
	
	public HomePheromone(double width, double height) {
		active = false;
		shortestTime = 0;
		this.width = width;
		this.height = height;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public synchronized void setShortestTime(double time) {
		shortestTime = time;
	}
	
	public synchronized boolean checkShortestTime(double time) {
		if(shortestTime > time) {
			shortestTime = time;
			return true;
		}
		return false;
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

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
