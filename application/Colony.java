package application;

import javafx.scene.shape.Circle;

public class Colony extends Circle {
	
	SynchronizedTrackers trackers;
	private BugManager bm;
	
	// creation of a colony only requires the width and height of the canvas they are placed on
	public Colony(double width, double height, ObjectsManager om) {
		setCenterX(width/2);
		setCenterY(height/2);
		setRadius(35);
		trackers = new SynchronizedTrackers();
		bm = new BugManager(width, height, om, getCenterX(), getCenterY(), getRadius(), trackers);
	}
	
	public void update(double latency) {
		bm.update(latency);
	}

	public SynchronizedTrackers getTrackers() {
		return trackers;
	}

	public BugManager getBm() {
		return bm;
	}
}