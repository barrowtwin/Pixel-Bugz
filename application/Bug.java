package application;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Bug {
	
	private double x, y, directionX, directionY, velocityX, velocityY, desiredVelocityX, desiredVelocityY, destinationX, destinationY, 
		boundsX, boundsY, maxSpeed, focus, alpha, desiredForceX, desiredForceY, force, accelerationX, accelerationY, targetX, targetY, timeAway,
		traveledX, traveledY, energy, latencySeconds;
	private int size, perimeter, gridIndexX, gridIndexY;
	private boolean explore, collect, returnHome, hasFood, stateChanged, tracked;
	private Random rand;
	private String paint;
	private Circle home;
	private HashMap<UUID,Food> food;
	private UUID foodID;
	
	public Bug(double boundX, double boundY, int bugSize, double speed, String bugPaint, double bugAlpha, HashMap<UUID,Food> food, Circle home) {
		x = 0;
		y = 0;
		destinationX = 0;
		destinationY = 0;
		directionX = 0;
		directionY = 0;
		velocityX = 0;
		velocityY = 0;
		force = 2;
		alpha = bugAlpha;
		paint = bugPaint;
		focus = 0.2;
		maxSpeed = speed;
		size = bugSize;
		rand = new Random();
		boundsX = boundX;
		boundsY = boundY;
		targetX = 0;
		targetY = 0;
		perimeter = 40;
		this.home = home;
		this.food = food;
		timeAway = 0;
		hasFood = false;
		energy = rand.nextDouble(20000, 60000);
		stateChanged = false;
		
		explore = true;
		collect = false;
		returnHome = false;
	}
	
	public boolean updateBug(double latency, double latencySeconds, FoodPheromone fPhero) {
		this.latencySeconds = latencySeconds;
		stateChanged = false;
		timeAway = timeAway + latency;
		if(!collect && fPhero.isActive() && !hasFood) {
			collect = true;
			explore = false;
			returnHome = false;
			stateChanged = true;
			velocityX = 0;
			velocityY = 0;
		}
		if(explore) {
			explore();
			// checks the list of food objects to see if the bug's detection area and each food object's area are intersecting
			food.forEach((key,value) -> {
				Food foodZone = food.get(key);
				double test = new Point2D(x, y).distance(foodZone.getArea().getCenterX(), foodZone.getArea().getCenterY());
				if(test <= perimeter + foodZone.getArea().getRadius()*1.5) {
					explore = false;
					collect = true;
					targetX = foodZone.getArea().getCenterX();
					targetY = foodZone.getArea().getCenterY();
					foodID = key;
					stateChanged = true;
					//System.out.println("bug smells food");
				}
			});
		}
		else if(collect)
			collect();
		else if(returnHome)
			returnHome();
		
		if(timeAway >= energy && !returnHome) {
			explore = false;
			collect = false;
			returnHome = true;
			targetX = home.getCenterX();
			targetY = home.getCenterY();
			velocityX = 0;
			velocityY = 0;
			stateChanged = true;
			//System.out.println("bug is exhausted, returning home");
		}
		
		return stateChanged;
	}
	
	private void explore() {	
		// create a direction for the bug to go, making sure the direction is inside the circle around the bug
		// the direction is a x increase/decrease along with a y increase/decrease
		double tempX, tempY;
		while(true) {
			tempX = rand.nextGaussian();
			tempY = rand.nextGaussian();
			if((tempX*tempX)+(tempY*tempY) < 1) {
				break;
			}
		}
		directionX = directionX + (tempX * focus);
		directionY = directionY + (tempY * focus);
		Point2D direction = new Point2D(directionX, directionY);
		Point2D normalizedDirection = direction.normalize();
		directionX = normalizedDirection.getX();
		directionY = normalizedDirection.getY();
		
		move();
	}
	
	public void collect() {
		directionX = targetX - x;
		directionY = targetY - y;
		Point2D direction = new Point2D(directionX, directionY);
		Point2D normalizedDirection = direction.normalize();
		directionX = normalizedDirection.getX();
		directionY = normalizedDirection.getY();
		
		move();
		
		// test to see if the bug has reached food
		if(food.containsKey(foodID)) {
			Food targetFood = food.get(foodID);
			Circle foodZone = food.get(foodID).getArea();
			double test = new Point2D(x, y).distance(foodZone.getCenterX(), foodZone.getCenterY());
			if(test <= foodZone.getRadius()) {
				//System.out.println("Reached food! Returning home.");
				explore = false;
				collect = false;
				returnHome = true;
				hasFood = true;
				velocityX = 0;
				velocityY = 0;
				stateChanged = true;
				targetFood.setCount(targetFood.getCount()-1);
				targetFood.setArea();
			}
		}
	}
	
	public void returnHome() {
		directionX = targetX - x;
		directionY = targetY - y;
		Point2D direction = new Point2D(directionX, directionY);
		Point2D normalizedDirection = direction.normalize();
		directionX = normalizedDirection.getX();
		directionY = normalizedDirection.getY();
		
		double tempX, tempY;
		while(true) {
			tempX = rand.nextGaussian();
			tempY = rand.nextGaussian();
			if((tempX*tempX)+(tempY*tempY) < 1) {
				break;
			}
		}
		directionX = directionX + (tempX * focus);
		directionY = directionY + (tempY * focus);
		Point2D direction2 = new Point2D(directionX, directionY);
		Point2D normalizedDirection2 = direction2.normalize();
		directionX = normalizedDirection2.getX();
		directionY = normalizedDirection2.getY();
		
		move();
		
		// test to see if the bug has reached home
		double test = new Point2D(x, y).distance(home.getCenterX(), home.getCenterY());
		if(test <= home.getRadius()/2) {
			//System.out.println("Reached home!");
			explore = true;
			collect = false;
			returnHome = false;
			timeAway = 0;
			velocityX = 0;
			velocityY = 0;
			stateChanged = true;
			hasFood = false;
			energy = rand.nextDouble(20000, 60000);
		}
		
		if(tracked) {
			System.out.println("targetX: " + targetX + "\ttargetY: " + targetY + "\thas food: " + hasFood);
		}
	}
	
	public void move() {
		double tempMag;
		desiredVelocityX = directionX * maxSpeed;
		desiredVelocityY = directionY * maxSpeed;
		desiredForceX = (desiredVelocityX - velocityX) * force*3;
		desiredForceY = (desiredVelocityY - velocityY) * force*3;
		Point2D speedTest = new Point2D(desiredForceX,desiredForceY);
		tempMag = speedTest.magnitude();
		if(tempMag > force*3) {
			tempMag = force/tempMag;
			accelerationX = (speedTest.getX() * tempMag)/1;
			accelerationY = (speedTest.getY() * tempMag)/1;
		}
		else {
			accelerationX = speedTest.getX()/1;
			accelerationY = speedTest.getY()/1;
		}
		double elapsedTime = latencySeconds;
		double tempVelocityX = velocityX + (accelerationX * elapsedTime);
		double tempVelocityY = velocityY + (accelerationY * elapsedTime);
		Point2D velocityTest = new Point2D(tempVelocityX,tempVelocityY);
		tempMag = velocityTest.magnitude();
		if(tempMag > maxSpeed) {
			tempMag = maxSpeed/tempMag;
			velocityX = (velocityTest.getX() * tempMag);
			velocityY = (velocityTest.getY() * tempMag);
		}
		else {
			velocityX = velocityTest.getX();
			velocityY = velocityTest.getY();
		}
		
		destinationX = x + velocityX;
		destinationY = y + velocityY;
		checkBounds();
	}
	
	public void checkBounds() {
		if(destinationX > 0 && destinationX < boundsX) {
			x = destinationX;
			traveledX += velocityX;
		}
		else {
			directionX *= -1;
			velocityX = 0;
		}
		if(destinationY > 0 && destinationY < boundsY) {
			y = destinationY;
			traveledY += velocityY;
		}
		else {
			directionY *= -1;
			velocityY = 0;
		}
	}

	public void setSpeed(double speed) {
		maxSpeed = speed;
	}
	
	public double getFocus() {
		return focus;
	}
	
	public void setFocus(double focus) {
		this.focus = focus;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public String getPaint() {
		return paint;
	}

	public void setPaint(String paint) {
		this.paint = paint;
	}
	
	public double[] getVelocity() {
		double[] d = {velocityX, velocityY};
		return d;
	}
	
	public double getForce() {
		return force;
	}
	
	public void setForce(double force) {
		this.force = force;
	}
	
	public double getTimeAway() {
		return timeAway;
	}

	public double getTraveledY() {
		return traveledY;
	}
	
	public void setTraveledY(double distance) {
		traveledY = distance;
	}

	public double getTraveledX() {
		return traveledX;
	}
	
	public void setTraveledX(double distance) {
		traveledX = distance;
	}

	public int getGridIndexX() {
		return gridIndexX;
	}

	public void setGridIndexX(int gridIndexX) {
		this.gridIndexX = gridIndexX;
	}

	public int getGridIndexY() {
		return gridIndexY;
	}

	public void setGridIndexY(int gridIndexY) {
		this.gridIndexY = gridIndexY;
	}
	
	public boolean isExploring() {
		return explore;
	}
	
	public boolean isReturning() {
		return returnHome;
	}
	
	public boolean isCollecting() {
		return collect;
	}
	
	public void setCollecting(boolean collecting) {
		collect = collecting;
	}
	
	public void setExploring(boolean exploring) {
		explore = exploring;
	}
	
	public void setReturning(boolean returning) {
		returnHome = returning;
	}
	
	public double getTargetX() {
		return targetX;
	}
	
	public void setTargetX(double x) {
		targetX = x;
	}
	
	public double getTargetY() {
		return targetY;
	}
	
	public void setTargetY(double y) {
		targetY = y;
	}

	public boolean hasFood() {
		return hasFood;
	}

	public void setHasFood(boolean hasFood) {
		this.hasFood = hasFood;
	}
	
	public UUID getFoodID() {
		return foodID;
	}

	public boolean isTracked() {
		return tracked;
	}

	public void setTracked(boolean tracked) {
		this.tracked = tracked;
	}
}
