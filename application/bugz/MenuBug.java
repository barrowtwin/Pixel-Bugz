package application.bugz;

import application.SynchronizedTrackers;

public class MenuBug extends Bug {
	
	private final int HEALTH = 1;
	private final int MAX_ENERGY = 35;			// Determines how long a bug stays out of its home
	private final int PERIMETER = 100;			// Determines how far away the bug can detect food
	private final double SPEED = 50;			// Determines the speed of the bug
	private final double SIZE =  3.0;			// Determines the size of the bug
	private final int EXPERIENCE_ON_DEATH = 5;	// Determines how much experience is given to the enemy that kills this bug
	private final int EXPERIENCE_TO_LEVEL = 50;	// Determines how much experience is needed to gain a level
	private final int EXPERIENCE_FOR_FOOD = 3;	// Determines how much experience is gained for bringing food home
	
	private double maxSpeed, size;

	public MenuBug(double boundX, double boundY, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		super(boundX, boundY, homeX, homeY, homeRadius, trackers);
		setHealth(HEALTH);
		setSize(SIZE);
		setSpeed(SPEED);
	}

	@Override
	public void acquireTarget() {
		if(getEnergy() <= 0) {
			// set target to home or home trail
			senseHome();
			createDirection();
		}
		else {
			// set a random direction to move in
			double randomDirectionX = getRandom().nextDouble() * (2 * maxSpeed) - maxSpeed;
			double randomDirectionY = getRandom().nextDouble() * (2 * maxSpeed) - maxSpeed;
			double nudgeX = (getVelocityX() - randomDirectionX) * getForce();
			double nudgeY = (getVelocityY() - randomDirectionY) * getForce();
			setDirectionX(randomDirectionX + nudgeX);
			setDirectionY(randomDirectionY + nudgeY);
		}
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
