package application;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class CanvasManager {
	
	private GraphicsContext backgroundGC, bugGC, directionGC, objectsGC, pheromoneGC;
	private BugManager bm;
	private String bgColor;
	private double bgAlpha, canvWidth, canvHeight;

	public CanvasManager(GraphicsContext backgroundGC, GraphicsContext bugGC, GraphicsContext directionGC, GraphicsContext objectsGC, GraphicsContext pheromoneGC, String bgColor, double bgAlpha, BugManager bm) {
		canvWidth = backgroundGC.getCanvas().getWidth();
		canvHeight = backgroundGC.getCanvas().getHeight();
		this.backgroundGC = backgroundGC;
		this.bugGC = bugGC;
		this.directionGC = directionGC;
		this.objectsGC = objectsGC;
		this.pheromoneGC = pheromoneGC;
		this.bgColor = bgColor;
		this.bgAlpha = bgAlpha;
		this.pheromoneGC.clearRect(0, 0, canvWidth, canvHeight);
		this.directionGC.clearRect(0, 0, directionGC.getCanvas().getWidth(), directionGC.getCanvas().getHeight());
		this.objectsGC.clearRect(0, 0, canvWidth, canvHeight);
		this.bugGC.clearRect(0, 0, canvWidth, canvHeight);
		this.bm = bm;
		backgroundGC.setFill(Paint.valueOf("#FFFFFF"));
		backgroundGC.fillRect(0, 0, canvWidth, canvHeight);
		backgroundGC.getCanvas().setOpacity(0.5);
		pheromoneGC.getCanvas().setOpacity(0.2);
	}
	
	public void drawBugs() {
		List<Bug> bugs = bm.getBugs();
		bugGC.clearRect(0, 0, canvWidth, canvHeight);
		for(int i = 0; i < bugs.size(); i++) {
			Bug bug = bugs.get(i);
			int bugSize = bug.getSize();
			bugGC.setFill(Paint.valueOf(bug.getPaint()));
			bugGC.fillOval(bug.getX()-bugSize/2, bug.getY()-bugSize/2, bugSize, bugSize);
			if(bug.hasFood()) {
				bugGC.setFill(Paint.valueOf("#ffff00"));
				bugGC.fillOval(bug.getX()-3, bug.getY()-3, 3, 3);
			}
		}
		if(!bugs.isEmpty())
			bugGC.setGlobalAlpha(bugs.get(0).getAlpha());
	}
	
	public void drawPheromones() {
		FoodPheromone[][] fPheros = bm.getPM().getFPheromone();
		HomePheromone[][] hPheros = bm.getPM().getHPheromone();
		pheromoneGC.clearRect(0, 0, canvWidth, canvHeight);
		double canvX = 0;
		double canvY = 0;
		double cellWidth = bm.getPM().getCellWidth();
		double cellHeight = bm.getPM().getCellHeight(); 
		for(int i = 0; i < bm.getPM().getXCells(); i++) {
			for(int j = 0; j < bm.getPM().getYCells(); j++) {
				boolean fPhero = fPheros[i][j].isActive();
				boolean hPhero = hPheros[i][j].isActive();
				pheromoneGC.setStroke(Paint.valueOf("#FFFFFF"));
				pheromoneGC.strokeRect(canvX, canvY, cellWidth, cellHeight);
				if(fPhero && hPhero) {
					pheromoneGC.setFill(Color.rgb(150, 0, 150, 1));
					pheromoneGC.fillRect(canvX, canvY, cellWidth, cellHeight);
//					pheromoneGC.fillOval(pm.getFPheromone(i, j).getEnterX()-2, pm.getFPheromone(i, j).getEnterY()-2, 4, 4);
//					pheromoneGC.setFill(Color.rgb(0, 0, 150, 1));
//					pheromoneGC.fillOval(pm.getHPheromone(i, j).getEntranceX()-2, pm.getHPheromone(i, j).getEntranceY()-2, 4, 4);
				}
				else if(hPhero) {
					pheromoneGC.setFill(Color.rgb(0, 0, 150, 1));
					pheromoneGC.fillRect(canvX, canvY, cellWidth, cellHeight);
					//pheromoneGC.fillOval(pm.getHPheromone(i, j).getEntranceX()-2, pm.getHPheromone(i, j).getEntranceY()-2, 4, 4);
				}
				else if(fPhero) {
					pheromoneGC.setFill(Color.rgb(150, 0, 0, 1));
					pheromoneGC.fillRect(canvX, canvY, cellWidth, cellHeight);
					//pheromoneGC.fillOval(pm.getFPheromone(i, j).getEnterX()-2, pm.getFPheromone(i, j).getEnterY()-2, 4, 4);
				}
				else
					pheromoneGC.setFill(Color.TRANSPARENT);
				canvY += cellHeight;
			}
			canvX += cellWidth;
			canvY = 0;
		}
	}
	
	public void drawFood() {
		objectsGC.clearRect(0, 0, canvWidth, canvHeight);
		HashMap<UUID, Food> food = bm.getOM().getFood();
		food.forEach((key,value) -> {
			Circle foodZone = food.get(key);
			double radius = foodZone.getRadius();
			objectsGC.setFill(Paint.valueOf("#ffff00"));
			objectsGC.fillOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
			objectsGC.setStroke(Paint.valueOf("#000000"));
			objectsGC.strokeOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
		});
	}
	
	public void drawHome() {
		Home bugHome = bm.getOM().getHome();
		double homeSize = bugHome.getRadius();
		objectsGC.setFill(Paint.valueOf("#ffffff"));
		objectsGC.fillOval(bugHome.getCenterX()-homeSize,bugHome.getCenterY()-homeSize,homeSize*2,homeSize*2);
		objectsGC.setStroke(Paint.valueOf("#000000"));
		objectsGC.strokeOval(bugHome.getCenterX()-homeSize,bugHome.getCenterY()-homeSize,homeSize*2,homeSize*2);
	}
	
	public void trackBug(int index) {
		Canvas directionCanv = directionGC.getCanvas();
		Bug bug = bm.getBugs().get(index);
		directionGC.clearRect(0, 0, directionCanv.getWidth(), directionCanv.getHeight());
		directionGC.setFill(Paint.valueOf("#ffffff"));
		directionGC.fillOval(0, 0, directionCanv.getWidth(), directionCanv.getWidth());
		directionGC.setFill(Paint.valueOf("#171717"));
		directionGC.fillOval(2, 2, directionCanv.getWidth()-4, directionCanv.getHeight()-4);
		directionGC.setFill(Paint.valueOf("#ff0000"));
		double[] direction = bug.getVelocity();
		directionGC.fillOval(50+(direction[0]*50), 50+(direction[1]*50), 3, 3);
		bugGC.setStroke(Paint.valueOf("#ffffff"));
		bugGC.strokeOval(bug.getX()-10,bug.getY()-10, 20,20);
	}
	
	public void drawBackground() {
		if(backgroundGC.getFill() != Paint.valueOf(bgColor)) {
			backgroundGC.setFill(Paint.valueOf(bgColor));
			backgroundGC.fillRect(0, 0, canvWidth, canvHeight);
			backgroundGC.getCanvas().setOpacity(bgAlpha);
		}
	}
	
	public void drawBackground(Double alpha) {
		backgroundGC.getCanvas().setOpacity(alpha);
	}
	
	public String getBgColor() {
		return bgColor;
	}
	
	public void setBgColor(String color) {
		bgColor = color;
	}
	
	public double getBgAlpha() {
		return bgAlpha;
	}
	
	public void setBgAlpha(double alpha) {
		bgAlpha = alpha;
	}
}
