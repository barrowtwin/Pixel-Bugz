package application;

import java.util.List;
import javafx.geometry.Point2D;

public class Worker extends Bug {
	
	private final int HEALTH = 1;
	private final int MAX_ENERGY = 35;	// Determines how long a bug stays out of its home
	private final int PERIMETER = 100;	// Determines how far away the bug can detect food
	private final int EXPERIENCE_ON_DEATH = 5;	// Determines how much experience is given to the enemy that kills this bug
	private final int EXPERIENCE_TO_LEVEL = 50;	// Determines how much experience is needed to gain a level
	private final int EXPERIENCE_FOR_FOOD = 3;	// Determines how much experience is gained for bringing food home
	
	private double maxSpeed, size;

	public Worker(double boundX, double boundY, List<Food> food, List<Enemy> enemies, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		super(boundX, boundY, food, enemies, homeX, homeY, homeRadius, trackers);
		setHealth(HEALTH);
	}

	@Override
	public void acquireTarget() {
		if(hasFood()) {
			// set target to home or home trail
			getFPheromone().setActive(true);
			senseHome();
			normalizeTarget();
		}
		else if(getEnergy() <= 0) {
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
				tempX = getRandom().nextGaussian();
				tempY = getRandom().nextGaussian();
				if((tempX*tempX)+(tempY*tempY) < 1) {
					break;
				}
			}
			setDirectionX(getDirectionX() + (tempX * getFocus()));
			setDirectionY(getDirectionY() + (tempY * getFocus()));
		}
	}
	
	public boolean senseFood() {
		List<Food> food = getFood();
		for(int i = 0; i < food.size(); i++) {
			Food foodZone = food.get(i);
			double test = new Point2D(getX(), getY()).distance(foodZone.getCenterX(), foodZone.getCenterY());
			if(test <= foodZone.getRadius()) {
				food.get(i).reduceCount();
				setVelocityX(0);
				setVelocityY(0);
				setTargetX(getHomeX());
				setTargetY(getHomeY());
				setHasFood(true);
				return true;
			}
			else if(test <= PERIMETER + foodZone.getRadius()) {
				setTargetX(foodZone.getCenterX());
				setTargetY(foodZone.getCenterY());
				return true;
			}
		}
		if(getFPheromone().isActive()) {
			setTargetX(getFPheromone().getEnterX());
			setTargetY(getFPheromone().getEnterY());
			return true;
		}
		return false;
	}

	@Override
	public double getSpeed() {
		return maxSpeed;
	}

	@Override
	public void setSpeed(double speed) {
		maxSpeed = speed;
	}

	@Override
	public double getSize() {
		return size;
	}

	@Override
	public void setSize(double size) {
		this.size = size;
		if(getLevel() > 1) {
			increaseSize();
		}
	}
	
	@Override
	public void increaseSize() {
		double sizeIncrease = (getLevel() * 0.1) + 1;
		size *= sizeIncrease;
	}

	@Override
	public int getPerimeter() {
		return PERIMETER;
	}

	@Override
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	@Override
	public void attack() {
		// Will never use this method but has to create it due to abstract method in Bug
	}
	
	@Override
	public void resetHealth() {
		setHealth(HEALTH);
	}
	
	@Override
	public int getMaxHealth() {
		return HEALTH;
	}
	
	@Override
	public int getMaxAttackStages() {
		return 0;
	}
	
	@Override
	public int getExperienceOnDeath() {
		return EXPERIENCE_ON_DEATH;
	}

	@Override
	public int getExperienceToLevel() {
		return EXPERIENCE_TO_LEVEL;
	}
	
	@Override
	public int getExperienceForFood() {
		return EXPERIENCE_FOR_FOOD;
	}
}
