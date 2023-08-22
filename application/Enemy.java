package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;

public abstract class Enemy {
	
	private List<Scout> scouts;
	private List<Guard> guards;
	private List<BloodSplatter> bloodSplatters;
	private Bug targetBug;
	private int gridIndexX, gridIndexY, health, attackStage, deathPhase, experience, level, levelsPending;
	private double x, y, directionX, directionY, velocityX, velocityY, targetX, targetY, latency, bugHomeX, bugHomeY, bugHomeRadius, focus, force,
		alpha, red, green, blue, thrownVelocityX, thrownVelocityY, flingTimer, boundsX, boundsY;
	private boolean attackingBug, attackingBugHome, pickedUp, thrown, dead;
	private SynchronizedTrackers trackers;
	
	public Enemy(double x, double y, double bugHomeX, double bugHomeY, double bugHomeRadius, double boundsX, double boundsY, SynchronizedTrackers trackers) {
		directionX = 0;
		directionY = 0;
		velocityX = 0;
		velocityY = 0;
		targetX = 0;
		targetY = 0;
		flingTimer = 1.0;
		deathPhase = 3;
		level = 1;
		this.x = x;
		this.y = y;
		this.boundsX = boundsX;
		this.boundsY = boundsY;
		this.setBugHomeX(bugHomeX);
		this.setBugHomeY(bugHomeY);
		this.setBugHomeRadius(bugHomeRadius);
		this.trackers = trackers;
		bloodSplatters = new ArrayList<>();
		createBloodSplatter();
	}
	
	public void updateEnemy(double latency) {
		this.latency = latency;
		if(dead) {
			alpha -= 0.001;
			return;
		}
		else if(pickedUp) {
			// do nothing
			return;
		}
		else if(thrown) {
			fling();
			return;
		}
		else {
			acquireTarget();
		}
		if(attackingBug) {
			attackBug();
		}
		else if(attackingBugHome) {
			attackBugHome();
		}
		else {
			move();
		}
	}
	
	// Each enemy will have its own way of aquiring a target
	public abstract void acquireTarget();
	
	public void move() {
		double tempMag;
		double desiredVelocityX = directionX * getSpeed();
		double desiredVelocityY = directionY * getSpeed();
		double desiredForceX = (desiredVelocityX - velocityX) * force;
		double desiredForceY = (desiredVelocityY - velocityY) * force;
		Point2D speedTest = new Point2D(desiredForceX,desiredForceY);
		tempMag = speedTest.magnitude();
		double accelerationX, accelerationY;
		if(tempMag > force) {
			tempMag = force/tempMag;
			accelerationX = (speedTest.getX() * tempMag);
			accelerationY = (speedTest.getY() * tempMag);
		}
		else {
			accelerationX = speedTest.getX();
			accelerationY = speedTest.getY();
		}
		double elapsedTime = latency;
		double tempVelocityX = velocityX + (accelerationX * elapsedTime);
		double tempVelocityY = velocityY + (accelerationY * elapsedTime);
		Point2D velocityTest = new Point2D(tempVelocityX,tempVelocityY);
		tempMag = velocityTest.magnitude();
		if(tempMag > getSpeed()) {
			tempMag = getSpeed()/tempMag;
			velocityX = (velocityTest.getX() * tempMag);
			velocityY = (velocityTest.getY() * tempMag);
		}
		else {
			velocityX = velocityTest.getX();
			velocityY = velocityTest.getY();
		}
		// No need to check an enemy's bounds
		x += velocityX;
		y += velocityY;
	}
	
	
	public abstract boolean senseBug();
	public abstract void attackBug();
	public abstract void attackBugHome();
	
	public void normalizeTarget() {
		directionX = targetX - x;
		directionY = targetY - y;
		Point2D direction = new Point2D(directionX, directionY);
		Point2D normalizedDirection = direction.normalize();
		directionX = normalizedDirection.getX();
		directionY = normalizedDirection.getY();
	}
	
	public void fling() {
		if(flingTimer >= 0) {
			x += thrownVelocityX;
			y += thrownVelocityY;
			// reduce the thrown velocity
			thrownVelocityX *= flingTimer;
			thrownVelocityY *= flingTimer;
			flingTimer -= latency;
			if(x < 0 || x > boundsX) {
				if(Math.abs(thrownVelocityX) >= 15) {
					dead = true;
					setDeathCoordinates();
				}
			}
			else if(y < 0 || y > boundsY) {
				if(Math.abs(thrownVelocityY) >= 15) {
					dead = true;
					setDeathCoordinates();
				}
			}
		}
		else {
			flingTimer = 1.0;
			thrown = false;
		}
	}
	
	public void createBloodSplatter() {
		Random rand = new Random();
		for(int i = 0; i < 2; i++) {
			for(int j = 12, width = 1; j >= 1; j--, width++) {
				double x = rand.nextGaussian(0, Math.sqrt(j*j));
				double y = rand.nextGaussian(0, Math.sqrt(j*j));
				bloodSplatters.add(new BloodSplatter(x,y,width));
			}
		}
	}
	
	public double getLatency() {
		return latency;
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
	
	public double getFocus() {
		return focus;
	}
	
	public void setFocus(double focus) {
		this.focus = focus;
	}
	
	public double getForce() {
		return force;
	}
	
	public void setForce(double force) {
		this.force = force;
	}
	
	// Each different type of enemy moves at different speeds
	public abstract double getSpeed();
	public abstract void setSpeed(double speed);

	// Each different type of enemy has a different size
	public abstract double getSize();
	public abstract void setSize(double size);
	public abstract void increaseSize();

	public double getBugHomeX() {
		return bugHomeX;
	}

	public void setBugHomeX(double bugHomeX) {
		this.bugHomeX = bugHomeX;
	}

	public double getBugHomeY() {
		return bugHomeY;
	}

	public void setBugHomeY(double bugHomeY) {
		this.bugHomeY = bugHomeY;
	}

	public double getBugHomeRadius() {
		return bugHomeRadius;
	}

	public void setBugHomeRadius(double bugHomeRadius) {
		this.bugHomeRadius = bugHomeRadius;
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

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getRed() {
		return red;
	}

	public void setRed(double red) {
		this.red = red;
	}

	public double getGreen() {
		return green;
	}

	public void setGreen(double green) {
		this.green = green;
	}

	public double getBlue() {
		return blue;
	}

	public void setBlue(double blue) {
		this.blue = blue;
	}
	
	// Used to store all of the "defender" type bugz that the enemy may target
	public void setDefenders(List<Scout> scouts, List<Guard> guards) {
		this.scouts = scouts;
		this.guards = guards;
	}

	public List<Scout> getScouts() {
		return scouts;
	}
	
	public List<Guard> getGuards() {
		return guards;
	}

	public boolean isAttackingBug() {
		return attackingBug;
	}

	public void setAttackingBug(boolean attacking) {
		this.attackingBug = attacking;
	}
	
	public boolean isAttackingBugHome() {
		return attackingBugHome;
	}

	public void setAttackingBugHome(boolean attacking) {
		this.attackingBugHome = attacking;
	}
	
	public boolean isPickedUp() {
		return pickedUp;
	}
	
	public void setPickedUp(boolean status) {
		pickedUp = status;
		if(status == true) {
			attackingBug = false;
		}
	}

	public boolean isThrown() {
		return thrown;
	}

	public void setThrown(boolean thrown, double velocityX, double velocityY) {
		this.thrown = thrown;
		thrownVelocityX = velocityX * 1.2;
		thrownVelocityY = velocityY * 1.2;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Bug getTargetBug() {
		return targetBug;
	}
	
	public void setTargetBug(Bug bug) {
		targetBug = bug;
	}
	
	public int getAttackStage() {
		return attackStage;
	}
	
	public void setAttackStage(int stage) {
		attackStage = stage;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public synchronized void reduceHealth(int damage, Bug bug) {
		health -= damage;
		if(health <= 0) {
			dead = true;
			setDeathCoordinates();
			bug.increaseExperience(getExperienceOnDeath());
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
	
	public abstract int getMaxHealth();
	public abstract int getMaxAttackStages();

	public SynchronizedTrackers getTrackers() {
		return trackers;
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

	public abstract int getExperienceOnDeath();

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public abstract int getExperienceToLevel();
	
	// Increases experience by given amount and then checks if it will level the enemy
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
			System.out.println("enemy is now level " + level);
			increaseSize();
		}
	}
	
	public int getLevel() {
		return level;
	}
	
	public int areLevelsPending() {
		return levelsPending;
	}
}