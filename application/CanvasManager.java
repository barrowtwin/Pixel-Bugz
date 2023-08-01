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
	private Canvas backgroundCanv, bugCanv, directionCanv, objectsCanv, pheromoneCanv;
	private List<Bug> bugs;
	private HashMap<UUID,Food> food;
	private String bgColor;
	private double bgAlpha, canvWidth, canvHeight;
	private PheromoneManager pm;
	private Circle bugHome;

	public CanvasManager(GraphicsContext backgroundGC, GraphicsContext bugGC, GraphicsContext directionGC, GraphicsContext objectsGC, GraphicsContext pheromoneGC, 
			List<Bug> bugs, HashMap<UUID,Food> food, String bgColor, double bgAlpha, PheromoneManager pm, Circle bugHome) {
		this.backgroundGC = backgroundGC;
		this.bugGC = bugGC;
		this.directionGC = directionGC;
		this.objectsGC = objectsGC;
		this.pheromoneGC = pheromoneGC;
		canvWidth = backgroundGC.getCanvas().getWidth();
		canvHeight = backgroundGC.getCanvas().getHeight();
		this.bugs = bugs;
		this.food = food;
		this.bgColor = bgColor;
		this.bgAlpha = bgAlpha;
		backgroundCanv = backgroundGC.getCanvas();
		bugCanv = bugGC.getCanvas();
		directionCanv = directionGC.getCanvas();
		objectsCanv = objectsGC.getCanvas();
		pheromoneCanv = pheromoneGC.getCanvas();
		this.pm = pm;
		this.bugHome = bugHome;
		
		this.pheromoneGC.clearRect(0, 0, canvWidth, canvHeight);
		this.directionGC.clearRect(0, 0, directionCanv.getWidth(), directionCanv.getHeight());
		this.objectsGC.clearRect(0, 0, canvWidth, canvHeight);
		this.bugGC.clearRect(0, 0, canvWidth, canvHeight);
		this.backgroundGC.setFill(Paint.valueOf("#FFFFFF"));
		this.backgroundGC.fillRect(0, 0, canvWidth, canvHeight);
		backgroundCanv.setOpacity(0.5);
		pheromoneCanv.setOpacity(0.2);
	}
	
	public void drawBugs() {
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
		double canvX = 0;
		double canvY = 0;
		pheromoneGC.clearRect(0, 0, canvWidth, canvHeight);
		double gridSectionHeight = pm.getGridSectionHeight(); 
		double gridSectionWidth = pm.getGridSectionWidth();
		for(int i = 0; i < pm.getxGrids(); i++) {
			for(int j = 0; j < pm.getyGrids(); j++) {
				boolean fPhero = pm.getFPheromone(i, j).isActive();
				boolean hPhero = pm.getHPheromone(i, j).isActive();
				pheromoneGC.setStroke(Paint.valueOf("#FFFFFF"));
				pheromoneGC.strokeRect(canvX, canvY, gridSectionWidth, gridSectionHeight);
				if(fPhero && hPhero) {
					pheromoneGC.setFill(Color.rgb(150, 0, 150, 1));
					pheromoneGC.fillRect(canvX, canvY, gridSectionWidth, gridSectionHeight);
//					pheromoneGC.fillOval(pm.getFPheromone(i, j).getEnterX()-2, pm.getFPheromone(i, j).getEnterY()-2, 4, 4);
//					pheromoneGC.setFill(Color.rgb(0, 0, 150, 1));
//					pheromoneGC.fillOval(pm.getHPheromone(i, j).getEntranceX()-2, pm.getHPheromone(i, j).getEntranceY()-2, 4, 4);
				}
				else if(hPhero) {
					pheromoneGC.setFill(Color.rgb(0, 0, 150, 1));
					pheromoneGC.fillRect(canvX, canvY, gridSectionWidth, gridSectionHeight);
					//pheromoneGC.fillOval(pm.getHPheromone(i, j).getEntranceX()-2, pm.getHPheromone(i, j).getEntranceY()-2, 4, 4);
				}
				else if(fPhero) {
					pheromoneGC.setFill(Color.rgb(150, 0, 0, 1));
					pheromoneGC.fillRect(canvX, canvY, gridSectionWidth, gridSectionHeight);
					//pheromoneGC.fillOval(pm.getFPheromone(i, j).getEnterX()-2, pm.getFPheromone(i, j).getEnterY()-2, 4, 4);
				}
				else
					pheromoneGC.setFill(Color.TRANSPARENT);
				canvY += pm.getGridSectionHeight();
			}
			canvX += pm.getGridSectionWidth();
			canvY = 0;
		}
	}
	
	public void drawFood() {
		objectsGC.clearRect(0, 0, canvWidth, canvHeight);
		food.forEach((key,value) -> {
			Circle foodZone = food.get(key).getArea();
			double radius = foodZone.getRadius();
			objectsGC.setFill(Paint.valueOf("#ffff00"));
			objectsGC.fillOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
			objectsGC.setStroke(Paint.valueOf("#000000"));
			objectsGC.strokeOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
		});
	}
	
	public void drawHome() {
		double homeSize = bugHome.getRadius();
		objectsGC.setFill(Paint.valueOf("#ffffff"));
		objectsGC.fillOval(bugHome.getCenterX()-homeSize/2,bugHome.getCenterY()-homeSize/2,homeSize,homeSize);
		objectsGC.setStroke(Paint.valueOf("#000000"));
		objectsGC.strokeOval(bugHome.getCenterX()-homeSize/2,bugHome.getCenterY()-homeSize/2,homeSize,homeSize);
	}
	
	public void trackBug(int index) {
		directionGC.clearRect(0, 0, directionCanv.getWidth(), directionCanv.getHeight());
		directionGC.setFill(Paint.valueOf("#ffffff"));
		directionGC.fillOval(0, 0, directionCanv.getWidth(), directionCanv.getWidth());
		directionGC.setFill(Paint.valueOf("#171717"));
		directionGC.fillOval(2, 2, directionCanv.getWidth()-4, directionCanv.getHeight()-4);
		directionGC.setFill(Paint.valueOf("#ff0000"));
		double[] direction = bugs.get(index).getVelocity();
		directionGC.fillOval(50+(direction[0]*50), 50+(direction[1]*50), 3, 3);
		bugGC.setStroke(Paint.valueOf("#ffffff"));
		bugGC.strokeOval(bugs.get(index).getX()-10,bugs.get(index).getY()-10, 20,20);
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
