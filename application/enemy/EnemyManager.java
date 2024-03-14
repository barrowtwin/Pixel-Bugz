package application.enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import application.SynchronizedTrackers;
import application.bugz.Bug;

public class EnemyManager {
	
	private final double LEVEL_DURATION = 300.0;
	private final int STAGES = 3;
	
	private List<List<? extends Enemy>> enemies;
	private List<List<? extends Bug>> defenders;
	private List<Enemy> menuEnemies;
	private List<Enemy> basicEnemies;
	private List<Integer> enemyList;
	private double bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, enemySpeed, enemySpawnTimer, spawnInterval, stageDuration;
	private int enemySize, stage, enemyListIndex;
	private Random rand;
	private SynchronizedTrackers trackers;
	
	public EnemyManager(double bugHomeX, double bugHomeY, double bugHomeRadius, double canvasWidth, double canvasHeight) {
		this.bugHomeX = bugHomeX;
		this.bugHomeY = bugHomeY;
		this.bugHomeRadius = bugHomeRadius;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		rand = new Random();
		enemySpawnTimer = 0;
		spawnInterval = 2.0;
		menuEnemies = new ArrayList<>();
		basicEnemies = new ArrayList<>();
		enemies = new ArrayList<>();
		enemies.add(menuEnemies);
		enemies.add(basicEnemies);
		enemyList = new ArrayList<>();
		stage = 1;
		stageDuration = LEVEL_DURATION/STAGES;
		setEnemyRatios();
		enemyListIndex = 0;
	}
	
	private void setEnemyRatios() {
		for(int i = 0; i < 3000; i++) {
			enemyList.add(2);
		}
	}
	
	public void createEnemy(int enemyType) {
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
				break;
			default:
				break;
		}
		Enemy enemy;
		switch(enemyType) {
			case (1):
				enemy = new MenuEnemy(x, y, bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, trackers);
				enemy.setDefenders(defenders);
				enemy.acquireTarget();
				menuEnemies.add(enemy);
				break;
			case (2):
				enemy = new BasicEnemy(x, y, bugHomeX, bugHomeY, bugHomeRadius, canvasWidth, canvasHeight, trackers);
				enemy.setDefenders(defenders);
				enemy.acquireTarget();
				basicEnemies.add(enemy);
				break;
			default:
				break;
		}
	}
	
	public void update(double latency, double gameTime) {
		enemySpawnTimer += latency;
		while(enemySpawnTimer > spawnInterval) {
			createEnemy(enemyList.get(enemyListIndex));
			enemyListIndex++;
			enemySpawnTimer -= spawnInterval;
		}
		if(gameTime/stage >= stageDuration) {
			stage++;
			spawnInterval *= 0.75;
		}
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> enemyGroup = enemies.get(i);
			IntStream.range(0, enemyGroup.size()).parallel().forEach(index -> {
				Enemy enemy = enemyGroup.get(index);
				enemy.updateEnemy(latency);
			});
		}
		removeDead();
	}
	
	public void menuUpdate(double latency) {
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> enemyGroup = enemies.get(i);
			IntStream.range(0, enemyGroup.size()).parallel().forEach(index -> {
				Enemy enemy = enemyGroup.get(index);
				enemy.updateEnemy(latency);
			});
		}
		removeDead();
	}
	
	public void createMenuEnemies() {
		for(int i = 0; i < 3; i++) {
			createEnemy(1);
		}
	}
	
	public void removeDead() {
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> e = enemies.get(i);
			Iterator<? extends Enemy> iterator = e.iterator();
		    while (iterator.hasNext()) {
		    	Enemy enemy = iterator.next();
		    	if(enemy.isDead()) {
		    		if(enemy.getAlpha() <= 0.05) {
		    			iterator.remove();
		    		}
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

	public double getEnemySpeed() {
		return enemySpeed;
	}

	public void setEnemySpeed(double enemySpeed) {
		this.enemySpeed = enemySpeed;
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> e = enemies.get(i);
			for(int j = 0; j < e.size(); j++) {
				Enemy enemy = e.get(j);
				enemy.setSpeed(enemySpeed);
			}
		}
	}

	public List<List<? extends Enemy>> getEnemies() {
		return enemies;
	}

	public int getEnemySize() {
		return enemySize;
	}

	public void setEnemySize(int enemySize) {
		this.enemySize = enemySize;
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> e = enemies.get(i);
			for(int j = 0; j < e.size(); j++) {
				Enemy enemy = e.get(j);
				enemy.setSize(enemySize);
			}
		}
	}
	
	public void setTrackers(SynchronizedTrackers trackers) {
		this.trackers = trackers;
	}

	public void setDefenders(List<List<? extends Bug>> defenders) {
		this.defenders = defenders;
	}
}
