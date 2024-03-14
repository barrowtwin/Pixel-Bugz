package application.bugz.colony;
import application.SynchronizedTrackers;
import application.bugz.BugManager;

public abstract class Colony {
	
	SynchronizedTrackers trackers;
	private BugManager bm;
	private double x, y, radius;
	
	public Colony(double width, double height, double homeRadius) {
		x = width/2;
		y = height/2;
		radius = homeRadius;
		trackers = new SynchronizedTrackers();
		bm = new BugManager(width, height, x, y, radius, trackers);
	}
	
	public void update(double latency) {
		bm.update(latency);
	}
	
	public void menuUpdate(double latency) {
		bm.menuUpdate(latency);
	}
	
	public void setupColony(int menuBugzCount, int workersCount, int guardsCount, int scoutsCount) {
		bm.createStarterBugz(menuBugzCount, workersCount, guardsCount, scoutsCount);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getRadius() {
		return radius;
	}

	public SynchronizedTrackers getTrackers() {
		return trackers;
	}

	public BugManager getBm() {
		return bm;
	}
}