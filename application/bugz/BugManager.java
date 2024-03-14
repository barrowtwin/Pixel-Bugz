package application.bugz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import application.SynchronizedTrackers;
import application.bugz.pheromones.FoodPheromone;
import application.bugz.pheromones.HomePheromone;
import application.bugz.pheromones.PheromoneManager;
import application.enemy.Enemy;
import application.objects.Food;

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
	private final double EXIT_COST = 500;
	private final double REPAIR_COST = 100;
	
	private PheromoneManager pm;
	private SynchronizedTrackers trackers;
	private Random rand;
	private List<List<? extends Enemy>> enemies;
	private List<Bug> menuBugz;
	private List<Bug> workers;
	private List<Bug> scouts; 
	private List<Bug> guards;
	private List<Bug> bugzToRelease;
	private List<List<? extends Bug>> bugz;
	private List<List<? extends Bug>> defenders;
	private List<Food> food;
	private int queens, colonyExits, spawnerSelector, instantSpawnSelector, workerCounter, scoutDeaths, guardDeaths, workerDeaths;
	private double homeX, homeY, homeRadius, canvasWidth, canvasHeight, releaseTime, exitTimer, workerSpawnTimer, scoutSpawnTimer, guardSpawnTimer, queenSpawnTimer,
		exitCreationTimer, gridCellWidth, gridCellHeight, latency;
	private boolean creatingExit, repairing;
	
	public BugManager(double width, double height, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		menuBugz = new ArrayList<>();
		workers = new ArrayList<>();
		scouts = new ArrayList<>();
		guards = new ArrayList<>();
		defenders = new ArrayList<>();
		defenders.add(scouts);
		defenders.add(guards);
		bugzToRelease = new ArrayList<>();
		// The order that the bug groups are placed into the bugz array is important for drawing the bugz
		bugz = new ArrayList<>();
		bugz.add(menuBugz);
		bugz.add(workers);
		bugz.add(scouts);
		bugz.add(guards);
		pm = new PheromoneManager(width, height);
		this.homeX = homeX;
		this.homeY = homeY;
		this.homeRadius = homeRadius;
		canvasWidth = width;
		canvasHeight = height;
		releaseTime = 0.2;
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
	
	public void menuUpdate(double latency) {
		this.latency = latency;
		releaseBugz();
		updateBugz();
		pm.updatePheromones(latency);
	}
	
	// Used for testing only
	public void instantCreateBugz() {
		// The controller sets the value of the instantSpawnSelector variable and calls this method to instantly create a bug
		switch(instantSpawnSelector) {
			case(0):
				break;
			case(1):
				Worker worker = new Worker(canvasWidth, canvasHeight, food, homeX, homeY, homeRadius, trackers);
				workers.add(worker);
				worker.setEnergy();
				worker.setX(homeX);
				worker.setY(homeY);
				break;
			case(2):
				Scout scout = new Scout(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
				scouts.add(scout);
				scout.setEnergy();
				scout.setX(homeX);
				scout.setY(homeY);
				break;
			case(3):
				Guard guard = new Guard(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
				guards.add(guard);
				guard.setEnergy();
				guard.setX(homeX);
				guard.setY(homeY);
				break;
			case(4):
				queens++;
				break;
			default:
				break;
		}
	}
	
	// Called during creation of colony to give player starter bugz
	public void createStarterBugz(int menuBugsCount, int workersCount, int guardsCount, int scoutsCount) {
		for(int i = 0; i < menuBugsCount; i++) {
			Bug bug = new MenuBug(canvasWidth, canvasHeight, homeX, homeY, homeRadius, trackers);
			menuBugz.add(bug);
			bug.setEnergy();
			bug.setX(homeX);
			bug.setY(homeY);
		}
		for(int i = 0; i < workersCount; i++) {
			Bug bug = new Worker(canvasWidth, canvasHeight, food, homeX, homeY, homeRadius, trackers);
			workers.add(bug);
			bug.setEnergy();
			bug.setX(homeX);
			bug.setY(homeY);
		}
		for(int i = 0; i < guardsCount; i++) {
			Bug bug = new Guard(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
			guards.add(bug);
			bug.setEnergy();
			bug.setX(homeX);
			bug.setY(homeY);
		}
		for(int i = 0; i < scoutsCount; i++) {
			Bug bug = new Scout(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
			scouts.add(bug);
			bug.setEnergy();
			bug.setX(homeX);
			bug.setY(homeY);
		}
	}
	
	public void createBugz() {
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
				Bug bug = new Worker(canvasWidth, canvasHeight, food, homeX, homeY, homeRadius, trackers);
				workers.add(bug);
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
				Bug bug = new Scout(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
				scouts.add(bug);
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
				Bug bug = new Guard(canvasWidth, canvasHeight, enemies, homeX, homeY, homeRadius, trackers);
				guards.add(bug);
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
				bug.acquireTarget();
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
		for(int i = 0; i < bugz.size(); i++) {
			List<? extends Bug> bugGroup = bugz.get(i);
			IntStream.range(0, bugGroup.size()).parallel().forEach(index -> {
				Bug bug = bugGroup.get(index);
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
		}
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
		Iterator<Bug> iterator = workers.iterator();
	    while (iterator.hasNext()) {
	    	Bug bug = iterator.next();
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
		Iterator<Bug> iterator = scouts.iterator();
	    while (iterator.hasNext()) {
	    	Bug bug = iterator.next();
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
		Iterator<Bug> iterator = guards.iterator();
	    while (iterator.hasNext()) {
	    	Bug bug = iterator.next();
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
		else {
			bug.setGridIndexX(bugXCell);
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
		else {
			bug.setGridIndexY(bugYCell);
			changedCell = true;
		}
		
		if(changedCell) {
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell);
			if(bug.hasFood()) {
				FoodPheromone phero = bug.getFPheromone();
				phero.setActive(true);
				phero.setEnterX(bug.getX() - bug.getFinalVelocityX());
				phero.setEnterY(bug.getY() - bug.getFinalVelocityY());
			}
		}
	}
	
	// Checks to see if the given bug has reach the grid cell in the shortest amount of time since leaving home
	// If yes, then updates home pheromone. Which is used by the bug for returning home using the best known route back.
	public void checkEntranceTime(Bug bug, int bugXCell, int bugYCell) {
		HomePheromone phero = bug.getHPheromone();
		if(!phero.isActive()) {
			phero.setActive(true);
			phero.setShortestTime(bug.getTimeAway());
			phero.setEntranceX(bug.getX() - bug.getFinalVelocityX());
			phero.setEntranceY(bug.getY() - bug.getFinalVelocityY());
		} 
		// if it passes this test, it also needs to make sure that the previous grid cell's entranceXY is not on the same cell wall
		// otherwise it will create a loop for any bug in "return home" state entering that grid cell
		else if(phero.checkShortestTime(bug.getTimeAway())) {
			phero.setEntranceX(bug.getX() - bug.getFinalVelocityX());
			phero.setEntranceY(bug.getY() - bug.getFinalVelocityY());
		}
	}
	
	public synchronized void addToBugzToRelease(Bug bug) {
		bugzToRelease.add(bug);
	}
	
	public void clearBugzToRelease() {
		bugzToRelease.clear();
	}
	
	public List<Bug> getMenuBugz() {
		return menuBugz;
	}
	
	public List<Bug> getWorkers() {
		return workers;
	}
	
	public List<Bug> getScouts() {
		return scouts;
	}
	
	public List<Bug> getGuards() {
		return guards;
	}
	
	public int getColonyExits() {
		return colonyExits;
	}
	
	public PheromoneManager getPM() {
		return pm;
	}
	
	public List<Food> getFood() {
		return food;
	}
	
	public void setFood(List<Food> food) {
		this.food = food;
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
	
	public double getExitCost() {
		return EXIT_COST;
	}
	
	public double getRepairCost() {
		return REPAIR_COST;
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
	
	public void setEnemies(List<List<? extends Enemy>> enemies) {
		this.enemies = enemies;
	}
	
	public List<List<? extends Enemy>> getEnemies() {
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
	
	public boolean isRepairing() {
		return repairing;
	}

	public void setRepairing(boolean status) {
		repairing = status;
	}
	
	public double getExitCreationTimer() {
		return exitCreationTimer;
	}
	
	public double getExitCreationTime() {
		return EXIT_CREATION_TIME;
	}
	
	public void setExits(int exits) {
		colonyExits = exits;
	}
	
	public void setInstantSpawnSelector(int value) {
		instantSpawnSelector = value;
	}
	
	public List<List<? extends Bug>> getBugz() {
		return bugz;
	}

	public List<List<? extends Bug>> getDefenders() {
		return defenders;
	}
}