package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class BugManager {

	private List<Bug> bugs;
	private HashMap<UUID,Food> food;
	private Circle home;
	private PheromoneManager pm;
	
	public BugManager(HashMap<UUID,Food> food, double width, double height, Circle home) {
		bugs = new ArrayList<>();
		this.food = food;
		this.home = home;
		pm = new PheromoneManager(width, height);
	}
	
	public void createBugs(int count, double width, double height, int size, double speed, String paint, double alpha) {
		Random rand = new Random();
		for(int i = 0; i < count; i++) {
			double x = home.getCenterX() + rand.nextGaussian(0, 20);
			double y = home.getCenterY() + rand.nextGaussian(0, 20);
			Bug bug = new Bug(width, height, size, speed, paint, alpha, food, home);
			bugs.add(bug);
			bug.setX(x);
			bug.setY(y);
			int xGrid = pm.findxGrid(bug.getX());
			int yGrid = pm.findyGrid(bug.getY());
			bug.setGridIndexX(xGrid);
			bug.setGridIndexY(yGrid);
			pm.getHPheromone(xGrid, yGrid).setEntranceX(home.getCenterX());
			pm.getHPheromone(xGrid, yGrid).setEntranceY(home.getCenterY());
			bug.setTraveledX(bug.getX() % pm.getGridSectionWidth());
			bug.setTraveledY(bug.getY() % pm.getGridSectionHeight());
		}
	}
	
	public void clearBugs() {
		bugs.clear();
	}
	
	public void updateBugs(double latency) {
		double xGridWidth = pm.getGridSectionWidth();
		double yGridHeight = pm.getGridSectionHeight();
		double latencySeconds = latency/1000;
		
		// Anything put inside this needs to be thread safe. It will perform each iteration in parallel on 2 threads
		IntStream.range(0, bugs.size()).parallel().forEach(index -> {
			Bug bug = bugs.get(index);
			if(bug.updateBug(latency, latencySeconds, pm.getFPheromone(bug.getGridIndexX(), bug.getGridIndexY()))) {
				changedState(bug); // need to change
			}
			
			// these if statements are checks to see if the bug has entered a new grid cell
			// LEFT X check first, checks if the bug changed grid cell to a new grid cell on the left
			// the direction variable is passed to the changedGrid method to help figure out shortest paths
			int direction;
			if(bug.getTraveledX() < 0) {
				bug.setTraveledX(bug.getX() % xGridWidth);
				bug.setGridIndexX(bug.getGridIndexX() - 1);
				direction = 0;
				changedGrid(bug,direction);
			} 
			// RIGHT X check next if previous failed, checks if the bug changed grid cell to a new grid cell on the right
			else if(bug.getTraveledX() > xGridWidth) {
				bug.setTraveledX(bug.getX() % xGridWidth);
				bug.setGridIndexX(bug.getGridIndexX() + 1);
				direction = 1;
				changedGrid(bug,direction);
			}
			// TOP Y check next, checks if the bug changed grid cell to a new grid cell on the top
			if(bug.getTraveledY() < 0) {
				bug.setTraveledY(bug.getY() % yGridHeight);
				bug.setGridIndexY(bug.getGridIndexY() - 1);
				direction = 2;
				changedGrid(bug,direction);
			} 
			// BOTTOM Y check next if previous failed, checks if the bug changed grid cell to a new grid cell on the bottom
			else if(bug.getTraveledY() > yGridHeight) {
				bug.setTraveledY(bug.getY() % yGridHeight);
				bug.setGridIndexY(bug.getGridIndexY() + 1);
				direction = 3;
				changedGrid(bug,direction);
			}
		});
		pm.updatePheromones(latencySeconds);
	}
	
	public void changedState(Bug bug) {
		if(bug.isExploring())
			checkForFoodPhero(bug);
		else if(bug.isReturning()) {
			updatePathToHome(bug);
		}
		else if(bug.isCollecting()) {
			updatePathToFood(bug);
		}
	}
	
	public void changedGrid(Bug bug, int direction) {
		// if bug is exploring and changes a grid cell, check to see if it needs to update the fastestTime variable to help map quickest path to that cell
		// then check to see if there is a food phero in this cell and change bug state if true
		if(bug.isExploring()) {
			checkEntranceTime(bug,direction);
			checkForFoodPhero(bug);
		} 
		// if previous check failed, then if bug is returning home, update its path home so that the bug can get a new target x,y location to help guide it back
		// if the bug has food, make sure to check the food pheromone grid and update it's active state as well as enterX and enterY of that phero
		else if(bug.isReturning()) {
			updatePathToHome(bug);
		} else if(bug.isCollecting()) {
			checkEntranceTime(bug,direction);
			updatePathToFood(bug);
		}
	}
	
	/*
	 * directions: 
	 * 0 - +1 to x grid
	 * 1 - -1 to x grid
	 * 2 - +1 to y grid
	 * 3 - -1 to y grid
	 */
	public void checkEntranceTime(Bug bug, int direction) {
		HomePheromone phero = pm.getHPheromone(bug.getGridIndexX(), bug.getGridIndexY());
		if(!phero.isActive()) {
			phero.setActive(true);
			phero.setShortestTime(bug.getTimeAway());
			phero.setEntranceX(bug.getX());
			phero.setEntranceY(bug.getY());
		} 
		// if it passes this test, it also needs to make sure that the previous grid cell's entranceXY is not on the same cell wall
		// otherwise it will create a loop for any bug in "return home" state entering that grid cell
		else if(bug.getTimeAway() < phero.getShortestTime()) {
			int bugXIndex;
			int bugYIndex;
			switch(direction) {
				case(0):
					bugXIndex = bug.getGridIndexX() + 1;
					bugYIndex = bug.getGridIndexY();
					if((int)bug.getX() != (int)pm.getHPheromone(bugXIndex, bugYIndex).getEntranceX()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(1):
					bugXIndex = bug.getGridIndexX() - 1;
					bugYIndex = bug.getGridIndexY();
					if((int)bug.getX() != (int)pm.getHPheromone(bugXIndex, bugYIndex).getEntranceX()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(2):
					bugXIndex = bug.getGridIndexX();
					bugYIndex = bug.getGridIndexY() + 1;
					if((int)bug.getY() != (int)pm.getHPheromone(bugXIndex, bugYIndex).getEntranceY()) {
						phero.setShortestTime(bug.getTimeAway());
						phero.setEntranceX(bug.getX());
						phero.setEntranceY(bug.getY());
					}
					break;
				case(3):
					bugXIndex = bug.getGridIndexX();
					bugYIndex = bug.getGridIndexY() - 1;
					if((int)bug.getY() != (int)pm.getHPheromone(bugXIndex, bugYIndex).getEntranceY()) {
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
	
	public void updatePathToFood(Bug bug) {
		FoodPheromone fPhero = pm.getFPheromone(bug.getGridIndexX(), bug.getGridIndexY());
		if(food.containsKey(bug.getFoodID())) {
			Circle foodZone = food.get(bug.getFoodID()).getArea();
			double test = new Point2D(bug.getX(), bug.getY()).distance(foodZone.getCenterX(), foodZone.getCenterY());
			if(test <= foodZone.getRadius()*3) {
				//System.out.println("Sensed home! Locked on!");
				bug.setTargetX(foodZone.getCenterX());
				bug.setTargetY(foodZone.getCenterY());
			}
		}
		else if(fPhero.isActive()) {
			bug.setTargetX(fPhero.getEnterX());
			bug.setTargetY(fPhero.getEnterY());
		} else if(!fPhero.isActive()) {
			bug.setCollecting(false);
			bug.setExploring(true);
			bug.setReturning(false);
		}
	}
	
	public void updatePathToHome(Bug bug) {
		HomePheromone hPhero = pm.getHPheromone(bug.getGridIndexX(), bug.getGridIndexY());
		FoodPheromone fPhero = pm.getFPheromone(bug.getGridIndexX(), bug.getGridIndexY());
		double test = new Point2D(bug.getX(), bug.getY()).distance(home.getCenterX(), home.getCenterY());
		if(test <= home.getRadius()*4) {
			//System.out.println("Sensed home! Locked on!");
			bug.setTargetX(home.getCenterX());
			bug.setTargetY(home.getCenterY());
		} 
		else if(hPhero.isActive()){
			bug.setTargetX(hPhero.getEntranceX());
			bug.setTargetY(hPhero.getEntranceY());
		}
		else {
			bug.setTargetX(home.getCenterX());
			bug.setTargetY(home.getCenterY());
		}
		if(bug.hasFood()) {
			if(!fPhero.isActive()) {
				fPhero.setActive(true);
				fPhero.setEnterX(bug.getX());
				fPhero.setEnterY(bug.getY());
			}
			else
				fPhero.setDuration(20);
		}
	}
	
	public void checkForFoodPhero(Bug bug) {
		FoodPheromone phero = pm.getFPheromone(bug.getGridIndexX(), bug.getGridIndexY());
		if(phero.isActive()) {
			bug.setCollecting(true);
			bug.setExploring(false);
			bug.setReturning(false);
			bug.setTargetX(phero.getEnterX());
			bug.setTargetY(phero.getEnterY());
			//System.out.println("Sensed food pheromone! Checking it out!");
		}
		else if(!phero.isActive() && bug.hasFood()) {
			phero.setActive(true);
			phero.setEnterX(bug.getX());
			phero.setEnterY(bug.getY());
		}
	}
	
	public List<Bug> getBugs() {
		return bugs;
	}
	
	public PheromoneManager getPM() {
		return pm;
	}
}