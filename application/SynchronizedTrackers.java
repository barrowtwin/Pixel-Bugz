package application;

// This class is for creating variables that need to be synchronized and also need to be accessed from multiple areas of the program
// I imagine most of these variables will be used in the UI
public class SynchronizedTrackers {
	
	private int bugzInHome, totalFoodCollected, colonyHealth;
	private double currentFood;
	
	public SynchronizedTrackers() {
		colonyHealth = 100;
		bugzInHome = 0;
		totalFoodCollected = 0;
		currentFood = 500.0;
	}
	
	public synchronized void decreaseColonyHealth(int damage) {
		colonyHealth -= damage;
	}
	
	public int getColonyHealth() {
		return colonyHealth;
	}
	
	public synchronized void increaseBugzInHome() {
		bugzInHome++;
	}
	
	public synchronized void decreaseBugzInHome() {
		bugzInHome--;
	}
	
	public int getBugzInHome() {
		return bugzInHome;
	}
	
	public synchronized void increaseFoodCollected() {
		totalFoodCollected++;
	}
	
	public int getTotalFoodCollected() {
		return totalFoodCollected;
	}
	
	public synchronized void increaseCurrentFood() {
		currentFood++;
	}
	
	public synchronized void decreaseCurrentFood(double value) {
		currentFood = currentFood - value;
	}
	
	public double getCurrentFood() {
		return currentFood;
	}
}
