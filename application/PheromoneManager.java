package application;

import javafx.scene.shape.Rectangle;

public class PheromoneManager {
	
	private HomePheromone[][] hPheromones;
	private FoodPheromone[][] fPheromones;
	private Rectangle[][] grid;
	private int xGrids, yGrids;
	private double gridSectionWidth, gridSectionHeight;
	
	public PheromoneManager(double width, double height) {
		xGrids = 30;
		yGrids = 18;
		gridSectionWidth = width/xGrids;
		gridSectionHeight = height/yGrids;
		grid = new Rectangle[xGrids][yGrids];
		hPheromones = new HomePheromone[xGrids][yGrids];
		fPheromones = new FoodPheromone[xGrids][yGrids];
		
		double canvX = 0;
		double canvY = 0;
		for(int i = 0; i < xGrids; i++) {
			for(int j = 0; j < yGrids; j++) {
				hPheromones[i][j] = new HomePheromone();
				fPheromones[i][j] = new FoodPheromone();
				grid[i][j] = new Rectangle(canvX, canvY, gridSectionWidth, gridSectionHeight);
				canvY += gridSectionHeight;
			}
			canvX += gridSectionWidth;
			canvY = 0;
		}
	}
	
	public void updatePheromones(double latency) {
		for(int i = 0; i < xGrids; i++) {
			for(int j = 0; j < yGrids; j++) {
				if(fPheromones[i][j].isActive())
					fPheromones[i][j].setDuration(fPheromones[i][j].getDuration() - latency);
			}
		}
	}
	
	public HomePheromone getHPheromone(int xIndex, int yIndex) {
		return hPheromones[xIndex][yIndex];
	}
	
	public FoodPheromone getFPheromone(int xIndex, int yIndex) {
		return fPheromones[xIndex][yIndex];
	}
	
	public double getGridSectionWidth() {
		return gridSectionWidth;
	}

	public void setGridSectionWidth(double gridSectionWidth) {
		this.gridSectionWidth = gridSectionWidth;
	}

	public double getGridSectionHeight() {
		return gridSectionHeight;
	}

	public void setGridSectionHeight(double gridSectionHeight) {
		this.gridSectionHeight = gridSectionHeight;
	}
	
	public int getxGrids() {
		return xGrids;
	}
	
	public int getyGrids() {
		return yGrids;
	}
	
	public int findxGrid(double x) {
		int left = 0;
		int right = xGrids - 1;
		int mid = left + (right - left) / 2;
		while(left < right) {
			if(x >= grid[mid][0].getX() && x <= (grid[mid][0].getX() + gridSectionWidth))
				break;
			else if(x < grid[mid][0].getX()) {
				right = mid - 1;
				mid = left + (right - left) / 2;
			}
			else {
				left = mid + 1;
				mid = left + (right - left) / 2;
			}
		}
		return mid;
	}
	
	public int findyGrid(double y) {
		int left = 0;
		int right = yGrids - 1;
		int mid = left + (right - left) / 2;
		while(left < right) {
			if(y >= grid[0][mid].getY() && y <= (grid[0][mid].getY() + gridSectionHeight))
				break;
			else if(y < grid[0][mid].getY()) {
				right = mid - 1;
				mid = left + (right - left) / 2;
			}
			else {
				left = mid + 1;
				mid = left + (right - left) / 2;
			}
		}
		return mid;
	}
}
