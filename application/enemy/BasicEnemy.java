package application.enemy;

import java.util.List;

import application.SynchronizedTrackers;
import application.bugz.Bug;
import javafx.geometry.Point2D;

public class BasicEnemy extends Enemy {
	
	private final int HEALTH = 50;				// Determines how much health this enemy has
	private final int DAMAGE = 20;				// Determines how much damage this enemy does
	private final int ATTACK_STAGES = 3;		// Determines how many stages the attack has (for animation purposes)
	private final double STAGE_DURATION = 0.3;	// Determines how long inbetween each attack stage (attack speed)
	private final int PERIMETER = 200;			// Determines how far away the enemy can detect a defender bug
	private final int ATTACK_PERIMETER = 35;	// Determines how far this enemy can attack a bug/bug home
	private final double SPEED = 40;			// Determines how much fast this enemy moves than workers
	private final double SIZE = 25.0;			// Determines this size of the enemy
	private final int EXPERIENCE_ON_DEATH = 5;	// Determines how much experience is given to the bug that kills this enemy
	private final int EXPERIENCE_TO_LEVEL = 5;	// Determines how much experience is needed to gain a level
	
	private double maxSpeed, attackProgress, size;
	private boolean sensedBug;
	
	public BasicEnemy(double x, double y, double bugHomeX, double bugHomeY, double bugHomeRadius, double boundsX, double boundsY, SynchronizedTrackers trackers) {
		super(x, y, bugHomeX, bugHomeY, bugHomeRadius, boundsX, boundsY, trackers);
		setHealth(HEALTH);
		setAttackStage(ATTACK_STAGES); // set initial stage so that enemy will attack very shortly after reaching first enemy
		setSize(SIZE);
		setSpeed(SPEED);
	}
	
	@Override
	public void acquireTarget() {
		if(senseBug()) {
			createDirection();
		}
		else {
			double enemyRadius = getSize()/2;
			double test = new Point2D(getX(), getY()).distance(getBugHomeX(), getBugHomeY());
			// enemy is attacking bug home
			if(test <= getBugHomeRadius() + enemyRadius) {
				setAttackingBugHome(true);
				setVelocityX(0);
				setVelocityY(0);
			} 
			// enemy is moving toward bug home
			else {
				setAttackingBugHome(false);
				setTargetX(getBugHomeX());
				setTargetY(getBugHomeY());
				createDirection();
			}
		}
	}
	
	@Override
	public boolean senseBug() {
		double shortestDistance = PERIMETER;
		for(int i = 0; i < getDefenders().size(); i++) {
			List<? extends Bug> defenders = getDefenders().get(i);
			for(int j = 0; j < defenders.size(); j++) {
				Bug bug = defenders.get(j);
				if(bug.isDead() || bug.isHome()) {
					continue;
				}
				double test = new Point2D(getX(), getY()).distance(bug.getX(), bug.getY());
				if(test <= PERIMETER) {
					if(getTargetBug() == null) {
						setTargetX(bug.getX());
						setTargetY(bug.getY());
						setTargetBug(bug);
						shortestDistance = test;
						sensedBug = true;
					}
					else {
						sensedBug = true;
						if(test < shortestDistance) {
							setTargetX(bug.getX());
							setTargetY(bug.getY());
							setTargetBug(bug);
							shortestDistance = test;
						}
					}
				}
			}
		}
		if(sensedBug) {
			sensedBug = false;
			if(shortestDistance < ATTACK_PERIMETER) {
				setVelocityX(0);
				setVelocityY(0);
				setAttackingBug(true);
			}
			else {
				setAttackingBug(false);
			}
			return true;
		}
		setTargetBug(null);
		setAttackStage(ATTACK_STAGES);
		setAttackingBug(false);
		return false;
	}
	
	@Override
	public void attackBug() {
		attackProgress += getLatency();
		// increment the attack stages so animations can be created for each stage of the attack
		if(attackProgress >= STAGE_DURATION) {
			if(getAttackStage() == ATTACK_STAGES) {
				setAttackStage(0);
				getTargetBug().reduceHealth(DAMAGE, this);
			}
			else {
				setAttackStage(getAttackStage() + 1);
			}
			attackProgress = 0;
		}
	}
	
	@Override
	public void attackBugHome() {
		attackProgress += getLatency();
		// increment the attack stages so animations can be created for each stage of the attack
		if(attackProgress >= STAGE_DURATION) {
			if(getAttackStage() == ATTACK_STAGES) {
				setAttackStage(0);
				getTrackers().decreaseColonyHealth(DAMAGE);
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