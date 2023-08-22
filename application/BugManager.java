package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class BugManager {

	private final double EXIT_CREATION_TIME = 200.0;
	private final double WORKER_SPAWN_TIME = 0.5;
	private final double SCOUT_SPAWN_TIME = 10.0;
	private final double GUARD_SPAWN_TIME = 20.0;
	private final double QUEEN_SPAWN_TIME = 30.0;
	private final double WORKER_COST = 1.0;
	private final double SCOUT_COST = 100.0;
	private final double GUARD_COST = 150.0;
	private final double QUEEN_COST = 500.0;
	
	private ObjectsManager om;
	private PheromoneManager pm;
	private SynchronizedTrackers trackers;
	private Random rand;
	private List<Enemy> enemies;
	private List<Worker> workers;
	private List<Scout> scouts; 
	private List<Guard> guards;
	private List<Bug> bugzToRelease;
	private int bugSize, queens, colonyExits, spawnerSelector, workerCounter, bugRed, bugGreen, bugBlue, scoutDeaths, guardDeaths, workerDeaths;
	private double homeX, homeY, homeRadius, canvasWidth, canvasHeight, releaseTime, exitTimer, bugAlpha, bugSpeed, bugFocus, bugForce,
		workerSpawnTimer, scoutSpawnTimer, guardSpawnTimer, queenSpawnTimer, exitCreationTimer, gridCellWidth, gridCellHeight, latency;
	private boolean creatingExit;
	
	public BugManager(double width, double height, ObjectsManager om, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		workers = new ArrayList<>();
		scouts = new ArrayList<>();
		guards = new ArrayList<>();
		bugzToRelease = new ArrayList<>();
		pm = new PheromoneManager(width, height);
		this.om = om;
		this.homeX = homeX;
		this.homeY = homeY;
		this.homeRadius = homeRadius;
		canvasWidth = width;
		canvasHeight = height;
		releaseTime = 0.2;
		queens = 1;
		colonyExits = 1;
		exitTimer = 0;
		spawnerSelector = 0;
		this.trackers = trackers;
		workerCounter = 0;
		rand = new Random();
		gridCellWidth = pm.getCellWidth();
		gridCellHeight = pm.getCellHeight();
	}
	
	// This is the method that is called by the animation timer each frame
	public void update(double latency) {
		this.latency = latency;
		createBugz();
		releaseBugz();
		createExit();
		updateBugz();
		
		// Updates active pheromone timers
		pm.updatePheromones(latency);
	}
	
	public void createBugz() {
		// The controller sets the value of the spawnerSelector variable, telling this switch which method to call
		switch(spawnerSelector) {
			case(0):
				break;
			case(1):
				createWorker();
				break;
			case(2):
				createScout();
				break;
			case(3):
				createGuard();
				break;
			case(4):
				createQueen();
				break;
			default:
				break;
		}
	}
	
	// Called during creation of colony to give player starter bugz
	public void createStarterBugz() {
		for(int i = 0; i < 50; i++) {
			Worker bug = new Worker(canvasWidth, canvasHeight, om.getFood(), enemies, homeX, homeY, homeRadius, trackers);
			workers.add(bug);
			bug.setRed(bugRed);
			bug.setGreen(bugGreen);
			bug.setBlue(bugBlue);
			bug.setAlpha(bugAlpha);
			bug.setSpeed(bugSpeed);
			bug.setSize(bugSize);
			bug.setFocus(bugFocus);
			bug.setForce(bugForce);
			bug.setEnergy();
			bug.setX(homeX);
			bug.setY(homeY);
		}
	}
	
	public void createWorker() {
		double counter = latency * queens;
		double progress = counter / WORKER_SPAWN_TIME;
		double cost = progress * WORKER_COST;
		if(trackers.getCurrentFood() > cost) {
			workerSpawnTimer += counter;
			trackers.decreaseCurrentFood(cost);
			while(workerSpawnTimer >= WORKER_SPAWN_TIME) {
				workerCounter++;
				workerSpawnTimer = workerSpawnTimer - WORKER_SPAWN_TIME;
				Worker bug = new Worker(canvasWidth, canvasHeight, om.getFood(), enemies, homeX, homeY, homeRadius, trackers);
				workers.add(bug);
				bug.setRed(bugRed);
				bug.setGreen(bugGreen);
				bug.setBlue(bugBlue);
				bug.setAlpha(bugAlpha);
				bug.setSpeed(bugSpeed);
				bug.setSize(bugSize);
				bug.setFocus(bugFocus);
				bug.setForce(bugForce);
				bug.setEnergy();
				bug.setX(homeX);
				bug.setY(homeY);
			}
			if(workerCounter >= 10) {
				workerCounter = 0;
			}
		}
	}
	
	public void createScout() {
		double counter = latency * queens;
		double progress = counter / SCOUT_SPAWN_TIME;
		double cost = progress * SCOUT_COST;
		if(trackers.getCurrentFood() > cost) {
			scoutSpawnTimer += counter;
			trackers.decreaseCurrentFood(cost);
			while(scoutSpawnTimer >= SCOUT_SPAWN_TIME) {
				scoutSpawnTimer = scoutSpawnTimer - SCOUT_SPAWN_TIME;
				Scout bug = new Scout(canvasWidth, canvasHeight, om.getFood(), enemies, homeX, homeY, homeRadius, trackers);
				scouts.add(bug);
				bug.setRed(bugRed);
				bug.setGreen(bugGreen);
				bug.setBlue(bugBlue);
				bug.setAlpha(bugAlpha);
				bug.setSpeed(bugSpeed);
				bug.setSize(bugSize);
				bug.setFocus(bugFocus);
				bug.setForce(bugForce);
				bug.setEnergy();
				bug.setX(homeX);
				bug.setY(homeY);
			}
		}
	}
	
	public void createGuard() {
		double counter = latency * queens;
		double progress = counter / GUARD_SPAWN_TIME;
		double cost = progress * GUARD_COST;
		if(trackers.getCurrentFood() > cost) {
			guardSpawnTimer += counter;
			trackers.decreaseCurrentFood(cost);
			while(guardSpawnTimer >= GUARD_SPAWN_TIME) {
				guardSpawnTimer = guardSpawnTimer - GUARD_SPAWN_TIME;
				Guard bug = new Guard(canvasWidth, canvasHeight, om.getFood(), enemies, homeX, homeY, homeRadius, trackers);
				guards.add(bug);
				bug.setRed(bugRed);
				bug.setGreen(bugGreen);
				bug.setBlue(bugBlue);
				bug.setAlpha(bugAlpha);
				bug.setSpeed(bugSpeed);
				bug.setSize(bugSize);
				bug.setFocus(bugFocus);
				bug.setForce(bugForce);
				bug.setEnergy();
				bug.setX(homeX);
				bug.setY(homeY);
			}
		}
	}
	
	public void createQueen() {
		double counter = latency * queens;
		double progress = counter / QUEEN_SPAWN_TIME;
		double cost = progress * QUEEN_COST;
		if(trackers.getCurrentFood() > cost) {
			queenSpawnTimer += counter;
			trackers.decreaseCurrentFood(cost);
			while(queenSpawnTimer >= QUEEN_SPAWN_TIME) {
				queenSpawnTimer = queenSpawnTimer - QUEEN_SPAWN_TIME;
				queens++;
			}
		}
	}
	
	public void clearBugz() {
		workers.clear();
		scouts.clear();
		guards.clear();
		queens = 0;
	}
	
	// Counts a timer up and when it reaches a certain time, it will set the booleans inside the bug to release it from home
	// Each bug is held in the bugzToRelease array list until it is released by the timer.
	// This is to simulate bugz being held up by having too few exits out of the colony
	public void releaseBugz() {
		exitTimer += latency;
		for(int i = 0; i < colonyExits; i++) {
			if(exitTimer >= releaseTime && !bugzToRelease.isEmpty()) {
				Bug bug = bugzToRelease.get(0);
				double angle = rand.nextDouble() * 2 * Math.PI;
				double newX = bug.getX() + homeRadius * Math.cos(angle);
				double newY = bug.getY() + homeRadius * Math.sin(angle);
				bug.setX(newX);
				bug.setY(newY);
				trackers.decreaseBugzInHome();
				bug.setIsHome(false);
				bug.setQueueRelease(false);
				bug.setFullEnergy(false);
				bug.resetHealth();
				placeInGrid(bug, gridCellWidth, gridCellHeight);
				bugzToRelease.remove(0);
			}
		}
		if(exitTimer >= releaseTime) {
			exitTimer = exitTimer - releaseTime;
		}
	}
	
	// If it has been toggled on to create a new exit for the colony, this will use bug in bugzToRelease to create the exit
	// The more bugz that are in bugzToRelease, the faster the creation of the exit happens
	public void createExit() {
		if(creatingExit) {
			exitCreationTimer += latency * bugzToRelease.size() / EXIT_CREATION_TIME;
			if(exitCreationTimer >= EXIT_CREATION_TIME) {
				exitCreationTimer -= EXIT_CREATION_TIME;
				colonyExits++;
			}
		}
	}
	
	// This is where each bug is updated with behavior and grid information
	// Anything put inside this needs to be thread safe. It will perform each iteration in parallel on 2 threads
	public void updateBugz() {
		// Updates workers first (no specific reason)
		IntStream.range(0, workers.size()).parallel().forEach(index -> {
			Bug bug = workers.get(index);
			bug.updateBug(latency);
			updateGridLocations(bug);
			if(bug.getHealth() <= 0) {
				increaseWorkerDeaths();
			}
			if(bug.isFullEnergy() && bug.isHome() && !bug.isQueueRelease()) {
				bug.setQueueRelease(true);
				addToBugzToRelease(bug); // synchronized
			}
		});
		// Updates scouts next
		IntStream.range(0, scouts.size()).parallel().forEach(index -> {
			Bug bug = scouts.get(index);
			bug.updateBug(latency);
			updateGridLocations(bug);
			if(bug.getHealth() <= 0) {
				increaseScoutDeaths();
			}
			if(bug.isFullEnergy() && bug.isHome() && !bug.isQueueRelease()) {
				bug.setQueueRelease(true);
				addToBugzToRelease(bug); // synchronized
			}
		});
		// Updates guards next
		IntStream.range(0, guards.size()).parallel().forEach(index -> {
			Bug bug = guards.get(index);
			bug.updateBug(latency);
			updateGridLocations(bug);
			if(bug.getHealth() <= 0) {
				increaseGuardDeaths();
			}
			if(bug.isFullEnergy() && bug.isHome() && !bug.isQueueRelease()) {
				bug.setQueueRelease(true);
				addToBugzToRelease(bug); // synchronized
			}
		});
		// Check if any bugs died, and if so, see if it is time to remove them
		if(workerDeaths > 0) {
			removeDeadWorkers();
		}
		if(scoutDeaths > 0) {
			removeDeadScouts();
		}
		if(guardDeaths > 0) {
			removeDeadGuards();
		}
		// Reset the death counters for next update
		workerDeaths = 0;
		scoutDeaths = 0;
		guardDeaths = 0;
	}
	
	// Iterates through the workers to see if their dead boolean is true
	// If yes, then check if alpha is 0 and remove if so
	// If bug is dead, alpha is reduced each frame in bug update
	public void removeDeadWorkers() {
		Iterator<Worker> iterator = workers.iterator();
	    while (iterator.hasNext()) {
	    	Worker bug = iterator.next();
	    	if(bug.isDead()) {
	    		if(bug.getAlpha() <= 0.0) {
	    			iterator.remove();
	    		}
	    	}
	    }
	}
	
	// Iterates through the workers to see if their dead boolean is true
	// If yes, then check if alpha is 0 and remove if so
	// If bug is dead, alpha is reduced each frame in bug update
	public void removeDeadScouts() {
		Iterator<Scout> iterator = scouts.iterator();
	    while (iterator.hasNext()) {
	    	Scout bug = iterator.next();
	    	if(bug.isDead()) {
	    		if(bug.getAlpha() <= 0.0) {
	    			iterator.remove();
	    		}
	    	}
	    }
	}
	
	// Iterates through the workers to see if their dead boolean is true
	// If yes, then check if alpha is 0 and remove if so
	// If bug is dead, alpha is reduced each frame in bug update
	public void removeDeadGuards() {
		Iterator<Guard> iterator = guards.iterator();
	    while (iterator.hasNext()) {
	    	Guard bug = iterator.next();
	    	if(bug.isDead()) {
	    		if(bug.getAlpha() <= 0.0) {
	    			iterator.remove();
	    		}
	    	}
	    }
	}
	
	// Assigns grid indexs to the given bug which is used in bug behaviors
	public void placeInGrid(Bug bug, double cellWidth, double cellHeight) {
		int bugXCell = (int)(bug.getX() / cellWidth);
		int bugYCell = (int)(bug.getY() / cellHeight);
		bug.setGridIndexX(bugXCell);
		bug.setGridIndexY(bugYCell);
		bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
		bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
	}
	
	// Checks and updates grid information for given bug
	public void updateGridLocations(Bug bug) {
		int direction;
		int bugXCell = (int)(bug.getX() / gridCellWidth);
		int bugYCell = (int)(bug.getY() / gridCellHeight);
		boolean changedCell = false;
		/*
		 * Check to see if bug changed cell on x-axis
		 * if bug didn't change cell, then do nothing
		 * else if bug did change cell, update bug's grid indexes and check pheromones of new grid
		 */
		if(bugXCell == bug.getGridIndexX()) {
			// do nothing
		}
		else if(bugXCell < bug.getGridIndexX()) {
			direction = 0;
			bug.setGridIndexX(bugXCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			changedCell = true;
		}
		else if(bugXCell > bug.getGridIndexX()) {
			direction = 1;
			bug.setGridIndexX(bugXCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			changedCell = true;
		}
		
		/*
		 * Check to see if bug changed cell on y-axis
		 * if bug didn't change cell, then do nothing
		 * else if bug did change cell, update bug's grid indexes and check pheromones of new grid
		 */
		if(bugYCell == bug.getGridIndexY()) {
			// do nothing
		}
		else if(bugYCell < bug.getGridIndexY()) {
			direction = 2;
			bug.setGridIndexY(bugYCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			changedCell = true;
		}
		else if(bugYCell > bug.getGridIndexY()) {
			direction = 3;
			bug.setGridIndexY(bugYCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			changedCell = true;
		}
		
		if(changedCell) {
			if(bug.hasFood()) {
				FoodPheromone phero = bug.getFPheromone();
				phero.setActive(true);
				phero.setEnterX(bug.getX() - bug.getVelocityX());
				phero.setEnterY(bug.getY() - bug.getVelocityY());
			}
		}
	}
	
	// Checks to see if the given bug has reach the grid cell in the shortest amount of time since leaving home
	// If yes, then updates home pheromone. Which is used by the bug for returning home using the best known route back.
	public void checkEntranceTime(Bug bug, int bugXCell, int bugYCell, int direction) {
		HomePheromone phero = bug.getHPheromone();
		if(!phero.isActive()) {
			phero.setActive(true);
			phero.setShortestTime(bug.getTimeAway());
			phero.setEntranceX(bug.getX() - bug.getVelocityX());
			phero.setEntranceY(bug.getY() - bug.getVelocityY());
		} 
		// if it passes this test, it also needs to make sure that the previous grid cell's entranceXY is not on the same cell wall
		// otherwise it will create a loop for any bug in "return home" state entering that grid cell
		else if(phero.checkShortestTime(bug.getTimeAway())) {
			switch(direction) {
				case(0):
					if((int)bug.getX() != (int)pm.getHPheromone()[bugXCell+1][bugYCell].getEntranceX()) {
						phero.setEntranceX(bug.getX() - bug.getVelocityX());
						phero.setEntranceY(bug.getY() - bug.getVelocityY());
					}
					break;
				case(1):
					if((int)bug.getX() != (int)pm.getHPheromone()[bugXCell-1][bugYCell].getEntranceX()) {
						phero.setEntranceX(bug.getX() - bug.getVelocityX());
						phero.setEntranceY(bug.getY() - bug.getVelocityY());
					}
					break;
				case(2):
					if((int)bug.getY() != (int)pm.getHPheromone()[bugXCell][bugYCell+1].getEntranceY()) {
						phero.setEntranceX(bug.getX() - bug.getVelocityX());
						phero.setEntranceY(bug.getY() - bug.getVelocityY());
					}
					break;
				case(3):
					if((int)bug.getY() != (int)pm.getHPheromone()[bugXCell][bugYCell-1].getEntranceY()) {
						phero.setEntranceX(bug.getX() - bug.getVelocityX());
						phero.setEntranceY(bug.getY() - bug.getVelocityY());
					}
					break;
				default:
					break;
			}
		}
	}
	
	public synchronized void addToBugzToRelease(Bug bug) {
		bugzToRelease.add(bug);
	}
	
	public List<Worker> getWorkers() {
		return workers;
	}
	
	public List<Scout> getScouts() {
		return scouts;
	}
	
	public List<Guard> getGuards() {
		return guards;
	}
	
	public int getColonyExits() {
		return colonyExits;
	}
	
	public PheromoneManager getPM() {
		return pm;
	}
	
	public ObjectsManager getOM() {
		return om;
	}

	public int getBugRed() {
		return bugRed;
	}

	public void setBugRed(int bugRed) {
		this.bugRed = bugRed;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setRed(bugRed);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setRed(bugRed);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setRed(bugRed);
		}
	}

	public int getBugGreen() {
		return bugGreen;
	}

	public void setBugGreen(int bugGreen) {
		this.bugGreen = bugGreen;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setGreen(bugGreen);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setGreen(bugGreen);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setGreen(bugGreen);
		}
	}

	public int getBugBlue() {
		return bugBlue;
	}

	public void setBugBlue(int bugBlue) {
		this.bugBlue = bugBlue;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setBlue(bugBlue);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setBlue(bugBlue);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setBlue(bugBlue);
		}
	}

	public double getBugAlpha() {
		return bugAlpha;
	}

	public void setBugAlpha(double bugAlpha) {
		this.bugAlpha = bugAlpha;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setAlpha(bugAlpha);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setAlpha(bugAlpha);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setAlpha(bugAlpha);
		}
	}

	public int getBugSize() {
		return bugSize;
	}

	public void setBugSize(int bugSize) {
		this.bugSize = bugSize;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setSize(bugSize);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setSize(bugSize);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setSize(bugSize);
		}
	}

	public double getBugSpeed() {
		return bugSpeed;
	}

	public void setBugSpeed(double bugSpeed) {
		this.bugSpeed = bugSpeed;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setSpeed(bugSpeed);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setSpeed(bugSpeed);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setSpeed(bugSpeed);
		}
	}

	public double getBugFocus() {
		return bugFocus;
	}

	public void setBugFocus(double bugFocus) {
		this.bugFocus = bugFocus;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setFocus(bugFocus);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setFocus(bugFocus);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setFocus(bugFocus);
		}
	}

	public double getBugForce() {
		return bugForce;
	}

	public void setBugForce(double bugForce) {
		this.bugForce = bugForce;
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			bug.setForce(bugForce);
		}
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			bug.setForce(bugForce);
		}
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			bug.setForce(bugForce);
		}
	}
	
	public int getBugzToReleaseSize() {
		return bugzToRelease.size();
	}

	public int getQueens() {
		return queens;
	}

	public void setQueens(int queens) {
		this.queens = queens;
	}

	public double getWorkerCost() {
		return WORKER_COST;
	}

	public double getScoutCost() {
		return SCOUT_COST;
	}

	public double getGuardCost() {
		return GUARD_COST;
	}

	public double getQueenCost() {
		return QUEEN_COST;
	}
	
	public void setSpawnerSelector(int value) {
		spawnerSelector = value;
	}
	
	public int getSpawnerSelector() {
		return spawnerSelector;
	}
	
	public int getWorkerCounter() {
		return workerCounter;
	}
	
	public double getScoutSpawnTimer() {
		return scoutSpawnTimer;
	}
	
	public double getScoutSpawnTime() {
		return SCOUT_SPAWN_TIME;
	}
	
	public double getGuardSpawnTimer() {
		return guardSpawnTimer;
	}
	
	public double getGuardSpawnTime() {
		return GUARD_SPAWN_TIME;
	}
	
	public double getQueenSpawnTimer() {
		return queenSpawnTimer;
	}
	
	public double getQueenSpawnTime() {
		return QUEEN_SPAWN_TIME;
	}
	
	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public synchronized void increaseWorkerDeaths() {
		workerDeaths++;
	}
	
	public synchronized void increaseScoutDeaths() {
		scoutDeaths++;
	}
	
	public synchronized void increaseGuardDeaths() {
		guardDeaths++;
	}

	public boolean isCreatingExit() {
		return creatingExit;
	}

	public void setCreatingExit(boolean creatingExit) {
		this.creatingExit = creatingExit;
	}
	
	public double getExitCreationTimer() {
		return exitCreationTimer;
	}
	
	public double getExitCreationTime() {
		return EXIT_CREATION_TIME;
	}
}