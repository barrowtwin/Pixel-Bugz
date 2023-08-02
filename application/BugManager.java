package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class BugManager {

	private List<Bug> bugs;
	private PheromoneManager pm;
	private ObjectsManager om;
	
	public BugManager(double width, double height) {
		bugs = new ArrayList<>();
		pm = new PheromoneManager(width, height);
		om = new ObjectsManager();
	}
	
	public void createBugs(int count, double width, double height, int size, double speed, String paint, double alpha) {
		Random rand = new Random();
		HashMap<UUID,Food> food = om.getFood();
		Home home = om.getHome();
		home.setCenterX(200);
		home.setCenterY(200);
		home.setRadius(75);
		for(int i = 0; i < count; i++) {
			double x = home.getCenterX() + rand.nextGaussian(0, 20);
			double y = home.getCenterY() + rand.nextGaussian(0, 20);
			Bug bug = new Bug(width, height, size, speed, paint, alpha, food, home);
			bugs.add(bug);
			bug.setX(x);
			bug.setY(y);
			int gridIndexX = (int)(x / pm.getCellWidth());
			int gridIndexY = (int)(y / pm.getCellHeight());
			bug.setGridIndexX(gridIndexX);
			bug.setGridIndexX(gridIndexY);
			bug.setFPheromone(pm.getFPheromone()[gridIndexX][gridIndexY]);
			bug.setHPheromone(pm.getHPheromone()[gridIndexX][gridIndexY]);
		}
	}
	
	public void clearBugs() {
		bugs.clear();
	}
	
	public void updateBugs(double latency) {
		double cellWidth = pm.getCellWidth();
		double cellHeight = pm.getCellHeight();
		double latencySeconds = latency/1000;
		// Anything put inside this needs to be thread safe. It will perform each iteration in parallel on 2 threads
		IntStream.range(0, bugs.size()).parallel().forEach(index -> {
			Bug bug = bugs.get(index);
			bug.updateBug(latency, latencySeconds);
			updateGridLocations(bug, cellWidth, cellHeight);	
		});
		pm.updatePheromones(latencySeconds);
	}
	
	// Checks to see if the bug has entered a new grid cell
	public void updateGridLocations(Bug bug, double cellWidth, double cellHeight) {
		int direction;
		int bugXCell = (int)(bug.getX() / cellWidth);
		int bugYCell = (int)(bug.getY() / cellHeight);
		if(bugXCell == bug.getGridIndexX()) {
			// do nothing
		}
		else if(bugXCell < bug.getGridIndexX()) {
			direction = 0;
			bug.setGridIndexX(bugXCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			checkFoodPhero(bug);
		}
		else if(bugXCell > bug.getGridIndexX()) {
			direction = 1;
			bug.setGridIndexX(bugXCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			checkFoodPhero(bug);
		}
		if(bugYCell == bug.getGridIndexY()) {
			// do nothing
		}
		else if(bugYCell < bug.getGridIndexY()) {
			direction = 2;
			bug.setGridIndexY(bugYCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			checkFoodPhero(bug);
		}
		else if(bugYCell > bug.getGridIndexY()) {
			direction = 3;
			bug.setGridIndexY(bugYCell);
			bug.setFPheromone(pm.getFPheromone()[bugXCell][bugYCell]);
			bug.setHPheromone(pm.getHPheromone()[bugXCell][bugYCell]);
			checkEntranceTime(bug, bugXCell, bugYCell, direction);
			checkFoodPhero(bug);
		}
	}
	
	public void checkFoodPhero(Bug bug) {
		if(bug.hasFood()) {
			FoodPheromone phero = pm.getFPheromone()[bug.getGridIndexX()][bug.getGridIndexY()];
			phero.setActive(true);
			phero.setEnterX(bug.getX());
			phero.setEnterY(bug.getY());
		}
	}
	
	public void checkEntranceTime(Bug bug, int bugXCell, int bugYCell, int direction) {
		HomePheromone phero = pm.getHPheromone()[bug.getGridIndexX()][bug.getGridIndexY()];
		if(!phero.isActive()) {
			phero.setActive(true);
			phero.setShortestTime(bug.getTimeAway());
			phero.setEntranceX(bug.getX());
			phero.setEntranceY(bug.getY());
		} 
		// if it passes this test, it also needs to make sure that the previous grid cell's entranceXY is not on the same cell wall
		// otherwise it will create a loop for any bug in "return home" state entering that grid cell
		else if(bug.getTimeAway() < phero.getShortestTime()) {
			switch(direction) {
				case(0):
					if((int)bug.getX() != (int)pm.getHPheromone()[bugXCell+1][bugYCell].getEntranceX()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(1):
					if((int)bug.getX() != (int)pm.getHPheromone()[bugXCell-1][bugYCell].getEntranceX()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(2):
					if((int)bug.getY() != (int)pm.getHPheromone()[bugXCell][bugYCell+1].getEntranceY()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(3):
					if((int)bug.getY() != (int)pm.getHPheromone()[bugXCell][bugYCell-1].getEntranceY()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				default:
					break;
			}
		}
	}
	
	public List<Bug> getBugs() {
		return bugs;
	}
	
	public PheromoneManager getPM() {
		return pm;
	}
	
	public ObjectsManager getOM() {
		return om;
	}
}