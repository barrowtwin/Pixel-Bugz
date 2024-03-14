package application.bugz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import application.SynchronizedTrackers;
import application.bugz.pheromones.FoodPheromone;
import application.bugz.pheromones.HomePheromone;
import application.enemy.Enemy;
import application.objects.BloodSplatter;
import application.objects.Food;
import javafx.geometry.Point2D;

public abstract class Bug {
	
	private final double FOCUS = 0.1;
	private final int FORCE = 2;
	
	private HomePheromone hPhero;
	private FoodPheromone fPhero;
	private Random rand;
	private List<Food> food;
	private List<List<? extends Enemy>> enemies;
	private List<BloodSplatter> bloodSplatters;
	private Enemy targetEnemy;
	private SynchronizedTrackers trackers;
	
	private int gridIndexX, gridIndexY, health, attackStage, deathPhase, experience, level, levelsPending;
	private boolean inHome, queueRelease, fullEnergy, hasFood, dead, attacking;
	private double x, y, boundsX, boundsY, alpha, homeX, homeY, homeRadius, latency, timeAway, energy, directionX, directionY,
	velocityX, velocityY, finalVelocityX, finalVelocityY, targetX, targetY;
	
	public Bug(double boundX, double boundY, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		timeAway = 0;
		deathPhase = 3;
		rand = new Random();
		boundsX = boundX;
		boundsY = boundY;
		level = 1;
		alpha = 1.0;
		this.homeX = homeX;
		this.homeY = homeY;
		this.homeRadius = homeRadius;
		fullEnergy = true;
		hasFood = false;
		inHome = true;
		this.trackers = trackers;
		trackers.increaseBugzInHome();
		bloodSplatters = new ArrayList<>();
		createBloodSplatter();
	}
	
	public void updateBug(double latency) {
		this.latency = latency;
		if(dead) {
			alpha -= latency/5;
		}
		// If not in home, update time away from home, decrease energy, find a target, then move to or attack that target
		else if(!inHome) {
			timeAway += latency;
			if(energy > 0)
				energy -= latency;
			if(attacking) {
				attack();
			}
			else {
				move();
			}
			acquireTarget();
		}
		// If bug is home increase energy over time and, if full energy, queue for release from home
		else {
			if(energy < getMaxEnergy()) {
				energy += latency*3;
			}
			else if(energy >= getMaxEnergy() && !queueRelease) {
				fullEnergy = true;
			}
		}
	}
	
	// Each bug should have its own method of aquiring targets
	public abstract void acquireTarget();
	
	// Each bug may or may not have to implement this method
	public abstract void attack();
	
	private void move() {
		double deltaX = directionX - velocityX;
		double deltaY = directionY - velocityY;
		velocityX += deltaX * FOCUS;
		velocityY += deltaY * FOCUS;
		normalizeVelocity();
		finalVelocityX = velocityX * latency;
		finalVelocityY = velocityY * latency;
		double destinationX = x + finalVelocityX;
		double destinationY = y + finalVelocityY;
		checkBounds(destinationX, destinationY);
	}
	
	private void checkBounds(double destinationX, double destinationY) {
		if(destinationX < 0 || destinationX > boundsX) {
			velocityX = 0;
		}
		else {
			x = destinationX;
		}
		if(destinationY < 0 || destinationY > boundsY) {
			velocityY = 0;
		}
		else {
			y = destinationY;
		}
	}
	
	public void createDirection() {
		directionX = targetX - x;
		directionY = targetY - y;
		normalizeDirection();
		double nudgeX = (velocityX - directionX) / getForce();
		double nudgeY = (velocityY - directionY) / getForce();
		directionX += nudgeX;
		directionY += nudgeY;
	}
	
	private void normalizeVelocity() {
		double magnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
		if(magnitude > getSpeed()) {
			velocityX = (velocityX / magnitude) * getSpeed();
			velocityY = (velocityY / magnitude) * getSpeed();
		}
	}
	
	private void normalizeDirection() {
		double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
		if(magnitude > getSpeed()) {
			directionX = (directionX / magnitude) * getSpeed();
			directionY = (directionY / magnitude) * getSpeed();
		}
		else {
			double scaleFactor = getSpeed() / magnitude;
			directionX *= scaleFactor;
			directionY *= scaleFactor;
		}
	}
	
	public void senseHome() {
		double test = new Point2D(getX(), getY()).distance(homeX, homeY);
		// Reached home and will enter home if energy is depleted or will resume behavior
		if(test <= homeRadius) {
			if(hasFood) {
				hasFood = false;
				trackers.increaseCurrentFood();		// current
				trackers.increaseFoodCollected();	// total
				increaseExperience(getExperienceForFood());
			}
			velocityX = 0;
			velocityY = 0;
			timeAway = 0;
			if(energy <= 0) {
				x = homeX;
				y = homeY;
				inHome = true;
				trackers.increaseBugzInHome();
			}
		}
		// Now sees home and will lock on and go straight for home
		else if(test <= homeRadius + getPerimeter()) {
			targetX = homeX;
			targetY = homeY;
		} 
		// Looking for home but has not seen it yet, will continue to follow home pheromones
		else {
			targetX = hPhero.getEntranceX();
			targetY = hPhero.getEntranceY();
		}
	}
	
	private void createBloodSplatter() {
		for(int i = 0; i < 2; i++) {
			for(int j = 12, width = 1; j >= 1; j--, width++) {
				double x = rand.nextGaussian(0, Math.sqrt(j*j));
				double y = rand.nextGaussian(0, Math.sqrt(j*j));
				bloodSplatters.add(new BloodSplatter(x,y,width));
			}
		}
	}
	
	// Each different type of bug moves at different speeds
	public abstract double getSpeed();
	public abstract void setSpeed(double speed);

	// Each different type of bug has a different size
	public abstract double getSize();
	public abstract void setSize(double size);
	public abstract void increaseSize();
	
	// Each different type of bug has different perimeters and max energy
	public abstract int getPerimeter();
	public abstract int getMaxEnergy();
		
	public Random getRandom() {
		return rand;
	}
	
	public double getHomeRadius() {
		return homeRadius;
	}
	
	public double getHomeX() {
		return homeX;
	}
	
	public double getHomeY() {
		return homeY;
	}
	
	public List<Food> getFood() {
		return food;
	}
	
	public double getFocus() {
		return FOCUS;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public double getForce() {
		return FORCE;
	}
	
	public double getTimeAway() {
		return timeAway;
	}
	
	public void setTimeAway(double timeAway) {
		this.timeAway = timeAway;
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

	public boolean isQueueRelease() {
		return queueRelease;
	}

	public void setQueueRelease(boolean queueRelease) {
		this.queueRelease = queueRelease;
	}
	
	public boolean isHome() {
		return inHome;
	}
	
	public void setIsHome(boolean status) {
		inHome = status;
	}

	public boolean isFullEnergy() {
		return fullEnergy;
	}
	
	public void setFullEnergy(boolean status) {
		fullEnergy = status;
	}
	
	public double getVelocityX() {
		return velocityX;
	}
	
	public void setVelocityX(double velocityX) {
		this.velocityX = velocityX;
	}
	
	public double getVelocityY() {
		return velocityY;
	}
	
	public void setVelocityY(double velocityY) {
		this.velocityY = velocityY;
	}
	
	public double getFinalVelocityX() {
		return finalVelocityX;
	}
	
	public double getFinalVelocityY() {
		return finalVelocityY;
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public double getLatency() {
		return latency;
	}
	
	public double getDirectionX() {
		return directionX;
	}
	
	public void setDirectionX(double direction) {
		directionX = direction;
	}
	
	public double getDirectionY() {
		return directionY;
	}
	
	public void setDirectionY(double direction) {
		directionY = direction;
	}
	
	public boolean hasFood() {
		return hasFood;
	}
	
	public void setHasFood(boolean status) {
		hasFood = status;
	}
	
	public void setEnergy() {
		energy = getMaxEnergy();
	}
	
	public void setEnergy(double value) {
		energy = value;
	}
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean status) {
		dead = status;
	}

	public List<List<? extends Enemy>> getEnemies() {
		return enemies;
	}

	public void setEnemies(List<List<? extends Enemy>> enemies) {
		this.enemies = enemies;
	}
	
	public double getBoundsX() {
		return boundsX;
	}
	
	public double getBoundsY() {
		return boundsY;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public synchronized void reduceHealth(int damage, Enemy enemy) {
		health -= damage;
		if(health <= 0) {
			dead = true;
			setDeathCoordinates();
			enemy.increaseExperience(getExperienceOnDeath());
		}
	}
	
	public void setDeathCoordinates() {
		double tempX = 0;
		double tempY = 0;
		if(x < 0)
			tempX = 0;
		else if(x > boundsX)
			tempX = boundsX;
		else {
			tempX = x;
		}
		// make sure the blood splatter is at the edge of the canvas on y axis
		if(y < 0)
			tempY = 0;
		else if(y > boundsY)
			tempY = boundsY;
		else {
			tempY = y;
		}
		for(int i = 0; i < bloodSplatters.size(); i++) {
			BloodSplatter bloodSplatter = bloodSplatters.get(i);
			bloodSplatter.setX(tempX);
			bloodSplatter.setY(tempY);
		}
	}
	
	public abstract void resetHealth();
	public abstract int getMaxHealth();
	public abstract int getMaxAttackStages();

	public int getAttackStage() {
		return attackStage;
	}

	public void setAttackStage(int attackStage) {
		this.attackStage = attackStage;
	}

	public Enemy getTargetEnemy() {
		return targetEnemy;
	}

	public void setTargetEnemy(Enemy targetEnemy) {
		this.targetEnemy = targetEnemy;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public List<BloodSplatter> getBloodSplatters() {
		return bloodSplatters;
	}

	public int getDeathPhase() {
		return deathPhase;
	}

	public void setDeathPhase(int deathPhase) {
		this.deathPhase = deathPhase;
	}
	
	public int getExperienceForFood() {
		return 0;
	}

	public abstract int getExperienceOnDeath();

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public abstract int getExperienceToLevel();
	
	// Increases experience by given amount and then checks if it will level the bug
	public void increaseExperience(int experience) {
		this.experience += experience;
		checkForLevel();
	}
	
	public void checkForLevel() {
		int experienceRequired = getExperienceToLevel() * level;
		if(this.experience >= experienceRequired) {
			level++;
			levelsPending++;
			this.experience -= experienceRequired;
			System.out.println("bug is now level " + level);
			increaseSize();
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getLevelsPending() {
		return levelsPending;
	}
}