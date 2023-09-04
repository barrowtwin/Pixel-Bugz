package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class EnemyManager {
	
	private final int ENEMY_SPAWN_TIME = 2000;
	
	private List<Enemy> enemies;
	private List<Scout> scouts;
	private List<Guard> guards;
	private double bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, enemyRed, enemyGreen, enemyBlue, enemyAlpha, enemySpeed, enemyFocus, enemyForce, enemySpawnTimer;
	private int enemySize, menuEnemyCount;
	private Random rand;
	private SynchronizedTrackers trackers;
	
	public EnemyManager(double bugHomeX, double bugHomeY, double bugHomeRadius, double canvasWidth, double canvasHeight, List<Scout> scouts, List<Guard> guards, SynchronizedTrackers trackers) {
		this.bugHomeX = bugHomeX;
		this.bugHomeY = bugHomeY;
		this.bugHomeRadius = bugHomeRadius;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		rand = new Random();
		enemySpawnTimer = 0;
		enemies = new ArrayList<Enemy>();
		this.scouts = scouts;
		this.guards = guards;
		this.trackers = trackers;
	}
	
	public void createEnemy() {
		double x = 0;
		double y = 0;
		double distance = 100;
		int side = rand.nextInt(4);
		switch(side) {
			case 0:
				x = rand.nextDouble() * canvasWidth;
				y = -distance;
				break;
			case 1:
				x = rand.nextDouble() * canvasWidth;
				y = canvasHeight + distance;
				break;
			case 2:
				x = -distance;
				y = rand.nextDouble() * canvasHeight;
				break;
			case 3:
				x = canvasWidth + distance;
				y = rand.nextDouble() * canvasHeight;
		}
		Enemy enemy = new BasicEnemy(x, y, bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, trackers);
		enemy.setRed(enemyRed);
		enemy.setGreen(enemyGreen);
		enemy.setBlue(enemyBlue);
		enemy.setAlpha(enemyAlpha);
		enemy.setSpeed(enemySpeed);
		enemy.setSize(enemySize);
		enemy.setFocus(enemyFocus);
		enemy.setForce(enemyForce);
		enemy.setDefenders(scouts, guards);
		enemy.acquireTarget();
		enemies.add(enemy);
	}
	
	public void createMenuEnemy() {
		double x = 0;
		double y = 0;
		double distance = 100;
		int side = rand.nextInt(4);
		switch(side) {
			case 0:
				x = rand.nextDouble() * canvasWidth;
				y = -distance;
				break;
			case 1:
				x = rand.nextDouble() * canvasWidth;
				y = canvasHeight + distance;
				break;
			case 2:
				x = -distance;
				y = rand.nextDouble() * canvasHeight;
				break;
			case 3:
				x = canvasWidth + distance;
				y = rand.nextDouble() * canvasHeight;
		}
		Enemy enemy = new MenuEnemy(x, y, bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, trackers);
		enemy.setRed(enemyRed);
		enemy.setGreen(enemyGreen);
		enemy.setBlue(enemyBlue);
		enemy.setAlpha(enemyAlpha);
		enemy.setSpeed(enemySpeed);
		enemy.setSize(enemySize);
		enemy.setFocus(enemyFocus);
		enemy.setForce(enemyForce);
		enemy.setDefenders(scouts, guards);
		enemies.add(enemy);
	}
	
	public void updateEnemies(double latency) {
		enemySpawnTimer += latency;
		if(enemySpawnTimer >= ENEMY_SPAWN_TIME) {
			createEnemy();
			enemySpawnTimer = 0;
		}
		IntStream.range(0, enemies.size()).parallel().forEach(index -> {
			Enemy enemy = enemies.get(index);
			enemy.updateEnemy(latency);
		});
		removeDead();
	}
	
	public void menuUpdateEnemies(double latency) {
		enemySpawnTimer += latency;
		if(enemySpawnTimer >= ENEMY_SPAWN_TIME && menuEnemyCount < 3) {
			createMenuEnemy();
			enemySpawnTimer = 0;
			menuEnemyCount++;
		}
		IntStream.range(0, enemies.size()).parallel().forEach(index -> {
			Enemy enemy = enemies.get(index);
			enemy.updateEnemy(latency);
		});
		removeDead();
	}
	
	public void removeDead() {
		Iterator<Enemy> iterator = enemies.iterator();
	    while (iterator.hasNext()) {
	    	Enemy enemy = iterator.next();
	    	if(enemy.isDead()) {
	    		if(enemy.getAlpha() <= 0.05) {
	    			iterator.remove();
	    		}
	    	}
	    }
	}

	public double getBugHomeX() {
		return bugHomeX;
	}

	public double getBugHomeY() {
		return bugHomeY;
	}

	public double getBugHomeRadius() {
		return bugHomeRadius;
	}

	public double getCanvasWidth() {
		return canvasWidth;
	}

	public double getCanvasHeight() {
		return canvasHeight;
	}

	public double getEnemyRed() {
		return enemyRed;
	}

	public void setEnemyRed(double enemyRed) {
		this.enemyRed = enemyRed;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setRed(enemyRed);
		}
	}

	public double getEnemyGreen() {
		return enemyGreen;
	}

	public void setEnemyGreen(double enemyGreen) {
		this.enemyGreen = enemyGreen;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setGreen(enemyGreen);
		}
	}

	public double getEnemyBlue() {
		return enemyBlue;
	}

	public void setEnemyBlue(double enemyBlue) {
		this.enemyBlue = enemyBlue;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setBlue(enemyBlue);
		}
	}

	public double getEnemyAlpha() {
		return enemyAlpha;
	}

	public void setEnemyAlpha(double enemyAlpha) {
		this.enemyAlpha = enemyAlpha;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setAlpha(enemyAlpha);
		}
	}

	public double getEnemySpeed() {
		return enemySpeed;
	}

	public void setEnemySpeed(double enemySpeed) {
		this.enemySpeed = enemySpeed;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setSpeed(enemySpeed);
		}
	}

	public double getEnemyFocus() {
		return enemyFocus;
	}

	public void setEnemyFocus(double enemyFocus) {
		this.enemyFocus = enemyFocus;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setFocus(enemyFocus);
		}
	}

	public double getEnemyForce() {
		return enemyForce;
	}

	public void setEnemyForce(double enemyForce) {
		this.enemyForce = enemyForce;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setForce(enemyForce);
		}
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public int getEnemySize() {
		return enemySize;
	}

	public void setEnemySize(int enemySize) {
		this.enemySize = enemySize;
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			enemy.setSize(enemySize);
		}
	}
}
