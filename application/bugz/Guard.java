package application.bugz;

import java.util.List;

import application.SynchronizedTrackers;
import application.enemy.Enemy;
import javafx.geometry.Point2D;

public class Guard extends Bug {
	
	private final int HEALTH = 500;				// Determines how much health this bug has
	private final int DAMAGE = 5;				// Determines how much damage this bug does
	private final int ATTACK_STAGES = 3;		// Determines how many stages the attack has (for animation purposes)
	private final double STAGE_DURATION = 0.2;	// Determines how long inbetween each attack stage (attack speed)
	private final int MAX_ENERGY = 70;			// Determines how long a bug stays out of its home
	private final int PERIMETER = 400;			// Determines how far away the bug can detect enemies
	private final int ATTACK_PERIMETER = 50;	// Determines how far away the bug can attack enemies
	private final double SPEED = 40;			// Determines the speed of the bug
	private final double SIZE =  8.0;			// Determines the size of the bug
	private final int EXPERIENCE_ON_DEATH = 5;	// Determines how much experience is given to the enemy that kills this bug
	private final int EXPERIENCE_TO_LEVEL = 50;	// Determines how much experience is needed to gain a level
	
	private double maxSpeed, size, attackProgress;
	private boolean sensedEnemy;
	
	public Guard(double boundX, double boundY, List<List<? extends Enemy>> enemies, double homeX, double homeY, double homeRadius, SynchronizedTrackers trackers) {
		super(boundX, boundY, homeX, homeY, homeRadius, trackers);
		setHealth(HEALTH);
		setAttackStage(ATTACK_STAGES); // set stage so that enemy will attack very shortly after reaching attack range
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
		else if(senseEnemy()) {
			// set target to enemy	
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
	
	public boolean senseEnemy() {
		double shortestDistance = PERIMETER;
		List<List<? extends Enemy>> enemies = getEnemies();
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> enemyGroup = enemies.get(i);
			for(int j = 0; j < enemyGroup.size(); j++) {
				Enemy enemy = enemyGroup.get(j);
				if(enemy.isPickedUp() || enemy.getX() < 0 || enemy.getX() > getBoundsX() || enemy.getY() < 0 || enemy.getY() > getBoundsY() || enemy.isDead() || enemy.isThrown()) {
					continue;
				}
				double test = new Point2D(getX(), getY()).distance(enemy.getX(), enemy.getY());
				if(test <= PERIMETER) {
					if(getTargetEnemy() == null) {
						setTargetX(enemy.getX());
						setTargetY(enemy.getY());
						setTargetEnemy(enemy);
						shortestDistance = test;
						sensedEnemy = true;
					}
					else {
						if(test < shortestDistance) {
							setTargetX(enemy.getX());
							setTargetY(enemy.getY());
							setTargetEnemy(enemy);
							shortestDistance = test;
						}
					}
				}
			}
		}
		if(sensedEnemy) {
			sensedEnemy = false;
			if(shortestDistance < ATTACK_PERIMETER) {
				setVelocityX(0);
				setVelocityY(0);
				setAttacking(true);
			}
			else {
				setAttacking(false);
			}
			return true;
		}
		setTargetEnemy(null);
		setAttackStage(ATTACK_STAGES);
		setAttacking(false);
		return false;
	}
	
	@Override
	public void attack() {
		attackProgress += getLatency();
		// increment the attack stages so animations can be created for each stage of the attack
		if(attackProgress >= STAGE_DURATION) {
			if(getAttackStage() == ATTACK_STAGES) {
				setAttackStage(0);
				getTargetEnemy().reduceHealth(DAMAGE, this);
			}
			else {
				setAttackStage(getAttackStage() + 1);
			}
			attackProgress = 0;
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
	public void resetHealth() {
		setHealth(HEALTH);
	}
	
	@Override
	public int getMaxHealth() {
		return HEALTH;
	}
	
	@Override
	public int getMaxAttackStages() {
		return ATTACK_STAGES;
	}

	@Override
	public int getExperienceOnDeath() {
		return EXPERIENCE_ON_DEATH;
	}

	@Override
	public int getExperienceToLevel() {
		return EXPERIENCE_TO_LEVEL;
	}
}
