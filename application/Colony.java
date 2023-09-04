package application;

public class Colony {
	
	SynchronizedTrackers trackers;
	private BugManager bm;
	private ObjectsManager om;
	private EnemyManager em;
	private CanvasManager cm;
	private double x, y, radius;
	
	public Colony(double width, double height, double homeRadius) {
		x = width/2;
		y = height/2;
		radius = homeRadius;
		trackers = new SynchronizedTrackers();
		om = new ObjectsManager(width, height);
		bm = new BugManager(width, height, om, x, y, radius, trackers);
		em = new EnemyManager(x, y, radius, width, height, bm.getScouts(), bm.getGuards(), trackers);
		cm = new CanvasManager(this);
		bm.setEnemies(em.getEnemies());
	}
	
	public void update(double latency) {
		bm.update(latency);
		om.updateFood(latency);
		em.updateEnemies(latency);
		cm.draw();
	}
	
	public void menuUpdate(double latency) {
		bm.menuUpdate(latency);
		em.menuUpdateEnemies(latency);
		cm.menuDraw();
	}
	
	public void setupMenuColony() {
		bm.setBugSpeed(50);
		bm.setBugSize(3);
		bm.setBugFocus(0.1);
		bm.setBugForce(2);
		bm.createMenuBugz();
	}
	
	public void setupGameColony() {
		bm.setBugSpeed(50);
		bm.setBugSize(3);
		bm.setBugFocus(0.1);
		bm.setBugForce(2);
		bm.createStarterBugz();
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

	public ObjectsManager getOm() {
		return om;
	}

	public CanvasManager getCm() {
		return cm;
	}

	public EnemyManager getEm() {
		return em;
	}
}