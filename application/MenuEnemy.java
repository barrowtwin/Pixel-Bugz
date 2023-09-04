package application;

import java.util.Random;

public class MenuEnemy extends Enemy {
	
	private final double SPEED_MODIFIER = 0.8;	// Determines how much fast this enemy moves than workers
	private final double SIZE_MODIFIER = 12.0;	// Determines how much larger the enemy is than a worker
	
	private Random rand;
	private double maxSpeed, size, boundsX, boundsY;
	private boolean sensedBug;

	public MenuEnemy(double x, double y, double bugHomeX, double bugHomeY, double bugHomeRadius, double boundsX,
			double boundsY, SynchronizedTrackers trackers) {
		super(x, y, bugHomeX, bugHomeY, bugHomeRadius, boundsX, boundsY, trackers);
		this.boundsX = boundsX;
		this.boundsY = boundsY;
		rand = new Random();
	}

	@Override
	public void acquireTarget() {
		// set a random direction to move in
		double randomDirectionX = rand.nextDouble() * (2 * maxSpeed) - maxSpeed;
		double randomDirectionY = rand.nextDouble() * (2 * maxSpeed) - maxSpeed;
		double nudgeX = (getVelocityX() - randomDirectionX) * getForce();
		double nudgeY = (getVelocityY() - randomDirectionY) * getForce();
		setDirectionX(randomDirectionX + nudgeX);
		setDirectionY(randomDirectionY + nudgeY);
		// Make sure the enemy stays on screen becuase no checkbounds method like bugz
		if(getX() < 0) {
			if(getDirectionX() < 0) {
				setDirectionX(-getDirectionX());
			}
		}
		else if(getX() > boundsX) {
			if(getDirectionX() > 0) {
				setDirectionX(-getDirectionX());
			}
		}
		
		if(getY() < 0) {
			if(getDirectionY() < 0) {
				setDirectionY(-getDirectionY());
			}
		}
		else if(getY() > boundsY) {
			if(getDirectionY() > 0) {
				setDirectionY(-getDirectionY());
			}
		}
	}

	@Override
	public boolean senseBug() {
		return sensedBug;
	}

	@Override
	public void attackBug() {
		
	}

	@Override
	public void attackBugHome() {
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxAttackStages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getExperienceOnDeath() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getExperienceToLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
