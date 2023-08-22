package application;

import java.util.List;
import javafx.geometry.Point2D;

public class BasicEnemy extends Enemy {
	
	private final int HEALTH = 50;				// Determines how much health this enemy has
	private final int DAMAGE = 20;				// Determines how much damage this enemy does
	private final int ATTACK_STAGES = 3;		// Determines how many stages the attack has (for animation purposes)
	private final double STAGE_DURATION = 0.3;	// Determines how long inbetween each attack stage (attack speed)
	private final int PERIMETER = 200;			// Determines how far away the enemy can detect a defender bug
	private final int ATTACK_PERIMETER = 35;	// Determines how far this enemy can attack a bug/bug home
	private final double SPEED_MODIFIER = 0.8;	// Determines how much fast this enemy moves than workers
	private final double SIZE_MODIFIER = 12.0;	// Determines how much larger the enemy is than a worker
	private final int EXPERIENCE_ON_DEATH = 5;	// Determines how much experience is given to the bug that kills this enemy
	private final int EXPERIENCE_TO_LEVEL = 5;	// Determines how much experience is needed to gain a level
	
	private double maxSpeed, size, attackProgress;
	private boolean sensedBug;
	
	public BasicEnemy(double x, double y, double bugHomeX, double bugHomeY, double bugHomeRadius, double boundsX, double boundsY, SynchronizedTrackers trackers) {
		super(x, y, bugHomeX, bugHomeY, bugHomeRadius, boundsX, boundsY, trackers);
		setHealth(HEALTH);
		setAttackStage(ATTACK_STAGES); // set initial stage so that enemy will attack very shortly after reaching first enemy
	}
	
	@Override
	public void acquireTarget() {
		if(senseBug()) {
			normalizeTarget();
		}
		else {
			double enemyRadius = size/2;
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
				normalizeTarget();
			}
		}
	}
	
	@Override
	public boolean senseBug() {
		// Guards are first priority target for this enemy
		List<Guard> guards = getGuards();
		double shortestDistance = 0;
		for(int i = 0; i < guards.size(); i++) {
			Bug guard = guards.get(i);
			if(guard.isDead()) {
				continue;
			}
			double test = new Point2D(getX(), getY()).distance(guard.getX(), guard.getY());
			if(test <= PERIMETER) {
				if(getTargetBug() == null) {
					setTargetX(guard.getX());
					setTargetY(guard.getY());
					setTargetBug(guard);
					shortestDistance = test;
					sensedBug = true;
				}
				else {
					if(test < shortestDistance) {
						setTargetX(guard.getX());
						setTargetY(guard.getY());
						setTargetBug(guard);
						shortestDistance = test;
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
		// Scouts are second priority target for this enemy
		List<Scout> scouts = getScouts();
		for(int i = 0; i < scouts.size(); i++) {
			Bug scout = scouts.get(i);
			if(scout.isDead()) {
				continue;
			}
			double test = new Point2D(getX(), getY()).distance(scout.getX(), scout.getY());
			if(test <= PERIMETER) {
				if(getTargetBug() == null) {
					setTargetX(scout.getX());
					setTargetY(scout.getY());
					setTargetBug(scout);
					shortestDistance = test;
					sensedBug = true;
				}
				else {
					if(test < shortestDistance) {
						setTargetX(scout.getX());
						setTargetY(scout.getY());
						setTargetBug(scout);
						shortestDistance = test;
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
		maxSpeed = speed * SPEED_MODIFIER;
	}

	@Override
	public double getSize() {
		return size;
	}

	@Override
	public void setSize(double size) {
		this.size = size * SIZE_MODIFIER;
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