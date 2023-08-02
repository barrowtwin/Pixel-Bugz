package application;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class Bug {
	
	private double x, y, directionX, directionY, velocityX, velocityY, desiredVelocityX, desiredVelocityY, destinationX, destinationY, 
		boundsX, boundsY, maxSpeed, focus, alpha, desiredForceX, desiredForceY, force, accelerationX, accelerationY, targetX, targetY, timeAway,
		energy, latencySeconds;
	private int size, gridIndexX, gridIndexY, perimeter;
	private boolean hasFood, tracked, foundFood;
	private Random rand;
	private String paint;
	private Circle home;
	private HashMap<UUID,Food> food;
	private UUID foodID;
	private HomePheromone hPhero;
	private FoodPheromone fPhero;
	
	public Bug(double boundX, double boundY, int bugSize, double speed, String bugPaint, double bugAlpha, HashMap<UUID,Food> food, Home home) {
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
		perimeter = 40;
		rand = new Random();
		boundsX = boundX;
		boundsY = boundY;
		targetX = 0;
		targetY = 0;
		this.home = home;
		this.food = food;
		timeAway = 0;
		hasFood = false;
		foundFood = false;
		energy = rand.nextDouble(30000, 70000);
	}
	
	public void updateBug(double latency, double latencySeconds) {
		this.latencySeconds = latencySeconds;
		timeAway = timeAway + latency;
		acquireTarget();
		move();
	}
	
	public void acquireTarget() {
		if(hasFood) {
			// set target to home or home trail
			fPhero.setActive(true);
			senseHome();
			normalizeTarget();
		}
		else if(timeAway >= energy) {
			// set target to home or home trail
			senseHome();
			normalizeTarget();
		}
		else if(senseFood()) {
			// set target to food or food trail	
			normalizeTarget();
		}
		else {
			// set a random target to wander toward
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
		}
	}
	
	public void move() {
		double tempMag;
		desiredVelocityX = directionX * maxSpeed;
		desiredVelocityY = directionY * maxSpeed;
		desiredForceX = (desiredVelocityX - velocityX) * force;
		desiredForceY = (desiredVelocityY - velocityY) * force;
		Point2D speedTest = new Point2D(desiredForceX,desiredForceY);
		tempMag = speedTest.magnitude();
		if(tempMag > force) {
			tempMag = force/tempMag;
			accelerationX = (speedTest.getX() * tempMag);
			accelerationY = (speedTest.getY() * tempMag);
		}
		else {
			accelerationX = speedTest.getX();
			accelerationY = speedTest.getY();
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
	
	public void normalizeTarget() {
		directionX = targetX - x;
		directionY = targetY - y;
		Point2D direction = new Point2D(directionX, directionY);
		Point2D normalizedDirection = direction.normalize();
		directionX = normalizedDirection.getX();
		directionY = normalizedDirection.getY();
	}
	
	public void checkBounds() {
		if(destinationX > 0 && destinationX < boundsX) {
			x = destinationX;
		}
		else {
			directionX *= -1;
			velocityX = 0;
		}
		if(destinationY > 0 && destinationY < boundsY) {
			y = destinationY;
		}
		else {
			directionY *= -1;
			velocityY = 0;
		}
	}
	
	public boolean senseFood() {
		food.forEach((key,value) -> {
			Food foodZone = food.get(key);
			double test = new Point2D(x, y).distance(foodZone.getCenterX(), foodZone.getCenterY());
			if(test <= foodZone.getRadius()) {
				food.get(foodID).reduceCount();
				velocityX = 0;
				velocityY = 0;
				targetX = home.getCenterX();
				targetY = home.getCenterY();
				hasFood = true;
				foundFood = true;
			}
			if(test <= perimeter + foodZone.getRadius()*1.75) {
				targetX = foodZone.getCenterX();
				targetY = foodZone.getCenterY();
				foodID = key;
				foundFood = true;
			}
		});
		if(foundFood) {
			foundFood = false;
			return true;
		}
		if(fPhero.isActive()) {
			targetX = fPhero.getEnterX();
			targetY = fPhero.getEnterY();
			return true;
		}
		return false;
	}
	
	public boolean senseFoodPhero() {
		if(fPhero.isActive()) {
			targetX = fPhero.getEnterX();
			targetY = fPhero.getEnterY();
			return true;
		}
		return false;
	}
	
	public void senseHome() {
		double test = new Point2D(x, y).distance(home.getCenterX(), home.getCenterY());
		if(test <= home.getRadius()) {
			hasFood = false;
			foundFood = false;
			timeAway = 0;
			velocityX = 0;
			velocityY = 0;
			energy = rand.nextDouble(30000, 70000);
		}
		else if(test <= home.getRadius()*3) {
			targetX = home.getCenterX();
			targetY = home.getCenterY();
		} 
		else {
			targetX = hPhero.getEntranceX();
			targetY = hPhero.getEntranceY();
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
	
	public void setHPheromone(HomePheromone hPhero) {
		this.hPhero = hPhero;
	}
	
	public HomePheromone getHPheromone() {
		return hPhero;
	}
	
	public void setFPheromone(FoodPheromone fPhero) {
		this.fPhero = fPhero;
	}
	
	public FoodPheromone getFPheromone() {
		return fPhero;
	}
}
