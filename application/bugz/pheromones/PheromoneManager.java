package application.bugz.pheromones;

public class PheromoneManager {
	
	private HomePheromone[][] hPheromones;
	private FoodPheromone[][] fPheromones;
	private int xCells, yCells;
	private double cellWidth, cellHeight;
	
	public PheromoneManager(double width, double height) {
		xCells = 30;
		yCells = 18;
		cellWidth = width/xCells;
		cellHeight = height/yCells;
		hPheromones = new HomePheromone[xCells][yCells];
		fPheromones = new FoodPheromone[xCells][yCells];
		for(int i = 0; i < xCells; i++) {
			for(int j = 0; j < yCells; j++) {
				hPheromones[i][j] = new HomePheromone(cellWidth, cellHeight);
				fPheromones[i][j] = new FoodPheromone(cellWidth, cellHeight);
			}
		}
	}
	
	public void updatePheromones(double latency) {
		for(int i = 0; i < xCells; i++) {
			for(int j = 0; j < yCells; j++) {
				if(fPheromones[i][j].isActive())
					fPheromones[i][j].setDuration(fPheromones[i][j].getDuration() - latency);
			}
		}
	}
	
	public HomePheromone[][] getHPheromone() {
		return hPheromones;
	}
	
	public FoodPheromone[][] getFPheromone() {
		return fPheromones;
	}

	public double getCellWidth() {
		return cellWidth;
	}

	public double getCellHeight() {
		return cellHeight;
	}
	
	public int getXCells() {
		return xCells;
	}

	public int getYCells() {
		return yCells;
	}
}
