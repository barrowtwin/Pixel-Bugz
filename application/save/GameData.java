package application.save;

import java.io.Serializable;

public class GameData implements Serializable {

	private static final long serialVersionUID = 1L;
	private int gameLevel, heroLevel, totalFoodCollected, totalEnemiesKilled;
	// Fire colony
	private int colony1TotalFoodCollected, colony1TotalEnemiesKilled, colony1TotalLevelsCompleted;
	// Fire colony
	private int colony2TotalFoodCollected, colony2TotalEnemiesKilled, colony2TotalLevelsCompleted;
	// Fire colony
	private int colony3TotalFoodCollected, colony3TotalEnemiesKilled, colony3TotalLevelsCompleted;
	// Fire colony
	private int colony4TotalFoodCollected, colony4TotalEnemiesKilled, colony4TotalLevelsCompleted;

	public GameData() {
		gameLevel = 1;
		heroLevel = 1;
		totalFoodCollected = 0;
		totalEnemiesKilled = 0;
		colony1TotalFoodCollected = 0;
		colony1TotalEnemiesKilled = 0;
		colony1TotalLevelsCompleted = 0;
		colony2TotalFoodCollected = 0;
		colony2TotalEnemiesKilled = 0;
		colony2TotalLevelsCompleted = 0;
		colony3TotalFoodCollected = 0;
		colony3TotalEnemiesKilled = 0;
		colony3TotalLevelsCompleted = 0;
		colony4TotalFoodCollected = 0;
		colony4TotalEnemiesKilled = 0;
		colony4TotalLevelsCompleted = 0;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getHeroLevel() {
		return heroLevel;
	}

	public void setHeroLevel(int heroLevel) {
		this.heroLevel = heroLevel;
	}

	public int getTotalFoodCollected() {
		return totalFoodCollected;
	}

	public void setTotalFoodCollected(int totalFoodCollected) {
		this.totalFoodCollected = totalFoodCollected;
	}

	public int getTotalEnemiesKilled() {
		return totalEnemiesKilled;
	}

	public void setTotalEnemiesKilled(int totalEnemiesKilled) {
		this.totalEnemiesKilled = totalEnemiesKilled;
	}

	public int getColony1TotalFoodCollected() {
		return colony1TotalFoodCollected;
	}

	public void setColony1TotalFoodCollected(int totalFoodCollected) {
		this.colony1TotalFoodCollected = totalFoodCollected;
	}

	public int getColony1TotalEnemiesKilled() {
		return colony1TotalEnemiesKilled;
	}

	public void setColony1TotalEnemiesKilled(int totalEnemiesKilled) {
		this.colony1TotalEnemiesKilled = totalEnemiesKilled;
	}

	public int getColony1TotalLevelsCompleted() {
		return colony1TotalLevelsCompleted;
	}

	public void setColony1TotalLevelsCompleted(int totalLevelsCompleted) {
		this.colony1TotalLevelsCompleted = totalLevelsCompleted;
	}

	public int getColony2TotalFoodCollected() {
		return colony2TotalFoodCollected;
	}

	public void setColony2TotalFoodCollected(int colony2TotalFoodCollected) {
		this.colony2TotalFoodCollected = colony2TotalFoodCollected;
	}

	public int getColony2TotalEnemiesKilled() {
		return colony2TotalEnemiesKilled;
	}

	public void setColony2TotalEnemiesKilled(int colony2TotalEnemiesKilled) {
		this.colony2TotalEnemiesKilled = colony2TotalEnemiesKilled;
	}

	public int getColony2TotalLevelsCompleted() {
		return colony2TotalLevelsCompleted;
	}

	public void setColony2TotalLevelsCompleted(int colony2TotalLevelsCompleted) {
		this.colony2TotalLevelsCompleted = colony2TotalLevelsCompleted;
	}

	public int getColony3TotalFoodCollected() {
		return colony3TotalFoodCollected;
	}

	public void setColony3TotalFoodCollected(int colony3TotalFoodCollected) {
		this.colony3TotalFoodCollected = colony3TotalFoodCollected;
	}

	public int getColony3TotalEnemiesKilled() {
		return colony3TotalEnemiesKilled;
	}

	public void setColony3TotalEnemiesKilled(int colony3TotalEnemiesKilled) {
		this.colony3TotalEnemiesKilled = colony3TotalEnemiesKilled;
	}

	public int getColony3TotalLevelsCompleted() {
		return colony3TotalLevelsCompleted;
	}

	public void setColony3TotalLevelsCompleted(int colony3TotalLevelsCompleted) {
		this.colony3TotalLevelsCompleted = colony3TotalLevelsCompleted;
	}

	public int getColony4TotalFoodCollected() {
		return colony4TotalFoodCollected;
	}

	public void setColony4TotalFoodCollected(int colony4TotalFoodCollected) {
		this.colony4TotalFoodCollected = colony4TotalFoodCollected;
	}

	public int getColony4TotalEnemiesKilled() {
		return colony4TotalEnemiesKilled;
	}

	public void setColony4TotalEnemiesKilled(int colony4TotalEnemiesKilled) {
		this.colony4TotalEnemiesKilled = colony4TotalEnemiesKilled;
	}

	public int getColony4TotalLevelsCompleted() {
		return colony4TotalLevelsCompleted;
	}

	public void setColony4TotalLevelsCompleted(int colony4TotalLevelsCompleted) {
		this.colony4TotalLevelsCompleted = colony4TotalLevelsCompleted;
	}
}