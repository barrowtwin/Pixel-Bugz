package application.canvas;

import java.util.List;
import java.util.Random;

import application.SynchronizedTrackers;
import application.bugz.Bug;
import application.bugz.pheromones.FoodPheromone;
import application.bugz.pheromones.HomePheromone;
import application.enemy.Enemy;
import application.objects.BloodSplatter;
import application.objects.Food;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CanvasManager {
	
	private GraphicsContext backgroundGC, bugGC;
	private List<List<? extends Bug>> bugz;
	private List<List<? extends Enemy>> enemies;
	private List<Food> food;
	private HomePheromone[][] hPheromones;
	private FoodPheromone[][] fPheromones;
	private SynchronizedTrackers trackers;
	private double bgAlpha, bugAlpha, enemyAlpha, canvasWidth, canvasHeight, colonyX, colonyY, colonyRadius, gridCellWidth, gridCellHeight;
	private int bgRed, bgGreen, bgBlue, bugRed, bugGreen, bugBlue, enemyRed, enemyGreen, enemyBlue, xGridCells, yGridCells;
	private boolean drawHPhero, drawFPhero, drawGrid, fadeLightComplete;
	private Random rand;
	private Light.Point environmentLight;
	private Lighting environmentLighting;
	private Light.Point backLight;
	private Lighting backLighting;
	private Color bugColor, enemyColor, bgColor;

	public CanvasManager() {
		rand = new Random();
	}
	
	public void setupCM(double colonyX, double colonyY, double colonyRadius, List<List<? extends Bug>> bugz, List<List<? extends Enemy>> enemies, List<Food> food, 
			SynchronizedTrackers trackers, HomePheromone[][] hPheromones, FoodPheromone[][] fPheromones, double gridCellWidth, double gridCellHeight, int xGridCells, int yGridCells) {
		this.colonyX = colonyX;
		this.colonyY = colonyY;
		this.colonyRadius = colonyRadius;
		this.bugz = bugz;
		this.enemies = enemies;
		this.food = food;
		this.trackers = trackers;
		this.hPheromones = hPheromones;
		this.fPheromones = fPheromones;
		this.gridCellWidth = gridCellWidth;
		this.gridCellHeight = gridCellHeight;
		this.xGridCells = xGridCells;
		this.yGridCells = yGridCells;
		createLights();
	}
	
	private void createLights() {
		environmentLight = new Light.Point();
		environmentLight.setX(colonyX);
		environmentLight.setY(colonyY);
		environmentLight.setZ(300);
		environmentLighting = new Lighting();
		environmentLighting.setLight(environmentLight);
		environmentLighting.setBumpInput(null);
		backLight = new Light.Point();
		backLight.setX(colonyX);
		backLight.setY(colonyY);
		backLight.setZ(75);
		backLighting = new Lighting();
		backLighting.setLight(backLight);
        backLighting.setBumpInput(null);
		environmentLight.setColor(Color.WHITE);
		backLight.setColor(Color.WHITE);
	}
	
	// Called after the creation of the canvas manager by the main controller
	public void generateGracphicsContexts(Canvas backgroundCanv, Canvas bugCanv) {
		canvasWidth = bugCanv.getWidth();
		canvasHeight = bugCanv.getHeight();
		backgroundGC = backgroundCanv.getGraphicsContext2D();
		bugGC = bugCanv.getGraphicsContext2D();
		bugGC.setTextAlign(TextAlignment.CENTER);
		bugGC.setFont(Font.font("System", FontWeight.BOLD, 26));
	}
	
	// Used to draw in the game view
	public void draw() {
		bugGC.clearRect(0, 0, canvasWidth, canvasHeight);
		backgroundGC.clearRect(0, 0, canvasWidth, canvasHeight);
		drawBackground();
		drawPheromones();
		drawFood();
		drawColony();
		drawBugz();
		drawEnemies();
		drawColonyHealth();
		lightPlayingField();
	}
	
	// Used to draw in the main menu
	public void menuDraw() {
		bugGC.clearRect(0, 0, canvasWidth, canvasHeight);
		backgroundGC.clearRect(0, 0, canvasWidth, canvasHeight);
		drawBackground();
		drawColony();
		drawBugz();
		drawEnemies();
		lightPlayingField();
	}
	
	public void drawBackground() {
		backgroundGC.setFill(bgColor);
		backgroundGC.fillRect(0, 0, canvasWidth, canvasHeight);
	}
	
	public void drawColony() {
		double colonyWidth = colonyRadius*2;
		bugGC.setFill(Color.WHITE);
		bugGC.fillOval(colonyX-colonyRadius, colonyY-colonyRadius, colonyWidth, colonyWidth);
	}
	
	public void drawBugz() {
		bugGC.setFill(bugColor);
		for(int i = 0; i < bugz.size(); i++) {
			List<? extends Bug> bugGroup = bugz.get(i);
			for(int j = 0; j < bugGroup.size(); j++) {
				Bug bug = bugGroup.get(j);
				if(bug.isDead()) {
					drawBugDeath(bug);
					continue;
				}
				else if(!bug.isHome()) {
					drawBug(bug, i);
				}
			}
		}
	}
	
	public void drawBug(Bug bug, int bugType) {
		double x = bug.getX();
		double y = bug.getY();
		double bugSize = bug.getSize();
		double bugOffset = 0;
		// The number assigned to each bugType is determined by the order that each bug group is placed into the bugz array list
		switch(bugType) {
			case(0): // MenuBug
				bugOffset = bugSize/2;
				bugGC.fillOval(x-bugOffset, y-bugOffset, bugSize, bugSize);
				break;
			case(1): // Worker
				bugOffset = bugSize/2;
				bugGC.fillOval(x-bugOffset, y-bugOffset, bugSize, bugSize);
				if(bug.hasFood()) {
					bugGC.setFill(Paint.valueOf("#ffff00"));
					bugGC.fillOval(x-bugSize, y-bugSize, bugSize*2, bugSize*2);
					bugGC.setFill(bugColor);
				}
				break;
			case(2): // Scout
				bugOffset = bugSize * Math.sqrt(3) / 2.0;
				double x1 = x - bugSize / 2;
				double y1 = y + bugOffset / 2;
				double x2 = x + bugSize / 2;
				double y2 = y + bugOffset / 2;
				double x3 = x;
				double y3 = y - bugOffset / 2;
				if(bug.isAttacking()) {
					drawBugAttack(bug,x,y);
				}
				bugGC.fillPolygon(new double[] {x1,x2,x3}, new double[] {y1,y2,y3}, 3);
				// Draw health bar above bug if not at full health
				if(bug.getHealth() < bug.getMaxHealth()) {
					drawBugHealth(bug, x, y, bugSize);
				}
				break;
			case(3): // Guard
				if(bug.isAttacking()) {
					drawBugAttack(bug,x,y);
				}
				bugGC.fillRect(x - bugSize, y - bugSize, bugSize*2, bugSize*2);
				if(bug.getHealth() < bug.getMaxHealth()) {
					drawBugHealth(bug, x, y, bugSize);
				}
				break;
			default:
				break;
		}
	}
	
	public void drawBugAttack(Bug bug, double x, double y) {
		if(bug.getAttackStage() == bug.getMaxAttackStages()) {
			Enemy enemy = bug.getTargetEnemy();
			bugGC.setStroke(Color.RED);
			bugGC.setLineWidth(4);
			bugGC.strokeLine(x, y, enemy.getX(), enemy.getY());
		}
	}
	
	public void drawEnemies() {
		bugGC.setFill(enemyColor);
		for(int i = 0; i < enemies.size(); i++) {
			List<? extends Enemy> enemyGroup = enemies.get(i);
			for(int j = 0; j < enemyGroup.size(); j++) {
				Enemy enemy = enemyGroup.get(j);
				double enemyX = enemy.getX();
				double enemyY = enemy.getY();
				// If the enemy is dead, create a blood splatter effect on background canvas
				if(enemy.isDead()) {
					drawEnemyDeath(enemy);
					continue;
				}
				// If the enemy is not picked up, draw them on bug canvas normally
				else if(!enemy.isPickedUp()) {
					double enemySize = enemy.getSize();
					double enemyOffset = enemySize/2;
					// if enemy is attacking bug, draw attack animation before drawing enemy so that enemy is ontop of animation
					if(enemy.isAttackingBug()) {
						if(enemy.getAttackStage() == enemy.getMaxAttackStages()) {
							Bug bug = enemy.getTargetBug();
							bugGC.setStroke(Color.RED);
							bugGC.setLineWidth(4);
							bugGC.strokeLine(enemyX, enemyY, bug.getX(), bug.getY());
							
						}
					}
					// if enemy is attacking bug home, draw attack animation before drawing enemy so that enemy is ontop of animation
					if(enemy.isAttackingBugHome()) {
						if(enemy.getAttackStage() == enemy.getMaxAttackStages()) {
							bugGC.setStroke(Color.RED);
							bugGC.setLineWidth(4);
							bugGC.strokeLine(enemyX, enemyY, colonyX, colonyY);
						}
					}
					drawEnemy(enemy, enemySize, enemyOffset);
					// Draw health bar above bug if not at full health
					if(enemy.getHealth() < enemy.getMaxHealth()) {
						drawEnemyHealth(enemy, enemyX, enemyY, enemySize);
						bugGC.setFill(enemyColor);
					}
				}
				// But if the enemy is picked up, draw them slightly larger to make it look like they are closer to player
				else {
					double enemySize = enemy.getSize()*1.3;
					double enemyOffset = enemySize/2;
					drawEnemy(enemy, enemySize, enemyOffset);
				}
			}
		}
	}
	
	public void drawEnemy(Enemy enemy, double enemySize, double enemyOffset) {
		bugGC.fillOval(enemy.getX()-enemyOffset, enemy.getY()-enemyOffset, enemySize, enemySize);
	}
	
	public void drawColonyHealth() {
		bugGC.setFill(Color.rgb(20,20,20,1.0));
		bugGC.fillText(Integer.toString(trackers.getColonyHealth()), colonyX, colonyY+7, colonyRadius*2);
	}
	
	public void drawPheromones() {
		if(drawHPhero || drawFPhero || drawGrid) {
			double x = 0;
			double y = 0;
			for(int i = 0; i < xGridCells; i++) {
				for(int j = 0; j < yGridCells; j++) {
					boolean fPhero = fPheromones[i][j].isActive();
					boolean hPhero = hPheromones[i][j].isActive();
					if(drawGrid) {
						bugGC.setStroke(Paint.valueOf("#FFFFFF"));
						bugGC.strokeRect(x, y, gridCellWidth, gridCellHeight);
					}
					if(fPhero && hPhero && drawHPhero && drawFPhero) {
						bugGC.setFill(Color.rgb(150, 0, 150));
						bugGC.fillRect(x, y, gridCellWidth, gridCellHeight);
					}
					else if(hPhero && drawHPhero) {
						bugGC.setFill(Color.rgb(0, 0, 150));
						bugGC.fillRect(x, y, gridCellWidth, gridCellHeight);
					}
					else if(fPhero && drawFPhero) {
						bugGC.setFill(Color.rgb(150, 0, 0));
						bugGC.fillRect(x, y, gridCellWidth, gridCellHeight);
					}
					else
						bugGC.setFill(Color.TRANSPARENT);
					y += gridCellHeight;
				}
				x += gridCellWidth;
				y = 0;
			}
		}
	}
	
	public void drawFood() {
		for(int i = 0; i < food.size(); i++) {
			Circle foodZone = food.get(i);
			double radius = foodZone.getRadius();
			bugGC.setFill(Color.WHITE);
			bugGC.fillOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
			bugGC.setStroke(Paint.valueOf("#000000"));
			bugGC.strokeOval(foodZone.getCenterX()-radius,foodZone.getCenterY()-radius,radius*2,radius*2);
		}
	}
	
	public void lightPlayingField() {
		bugGC.applyEffect(environmentLighting);
		backgroundGC.applyEffect(backLighting);
	}
	
	private void drawBugHealth(Bug bug, double bugX, double bugY, double bugSize) {
		double barWidth = bugSize * 2.5;
		double barHeight = 4;
		double barX = bugX - barWidth / 2;
		double barY = bugY - bugSize - barHeight - 3;
		double percentHealth = (double)bug.getHealth() / (double)bug.getMaxHealth();
		bugGC.setFill(Color.GREEN);
		bugGC.fillRect(barX, barY, barWidth * percentHealth, barHeight);
	}
	
	private void drawEnemyHealth(Enemy enemy, double enemyX, double enemyY, double enemySize) {
		double barWidth = enemySize * 2.5;
		double barHeight = 4;
		double barX = enemyX - barWidth / 2;
		double barY = enemyY - enemySize - barHeight - 3;
		double percentHealth = (double)enemy.getHealth() / (double)enemy.getMaxHealth();
		bugGC.setFill(Color.GREEN);
		bugGC.fillRect(barX, barY, barWidth * percentHealth, barHeight);
	}
	
	private void drawBugDeath(Bug bug) {
		int deathPhase = bug.getDeathPhase();
		// create random circles around the area of death to simulate blood splatter
		backgroundGC.setFill(Color.rgb(bugRed, bugGreen, bugBlue, bug.getAlpha()));
		List<BloodSplatter> bloodSplatter = bug.getBloodSplatters();
		for(int i = 0; i < bloodSplatter.size()/deathPhase; i++) {
			BloodSplatter bs = bloodSplatter.get(i);
			backgroundGC.fillOval(bs.getX(), bs.getY(), bs.getRadius(), bs.getRadius());
		}
		if(deathPhase > 1) {
			bug.setDeathPhase(deathPhase - 1);
		}
	}
	
	private void drawEnemyDeath(Enemy enemy) {
		int deathPhase = enemy.getDeathPhase();
		// create random circles around the area of death to simulate blood splatter
		backgroundGC.setFill(Color.rgb(enemyRed, enemyGreen, enemyBlue, enemy.getAlpha()));
		List<BloodSplatter> bloodSplatter = enemy.getBloodSplatters();
		for(int i = 0; i < bloodSplatter.size()/deathPhase; i++) {
			BloodSplatter bs = bloodSplatter.get(i);
			backgroundGC.fillOval(bs.getX(), bs.getY(), bs.getRadius(), bs.getRadius());
		}
		if(deathPhase > 1) {
			enemy.setDeathPhase(deathPhase - 1);
		}
	}
	
	/*
	private void applyLevelGlow(Bug bug) {
		
	}
	*/
	
	public void killColony() {
		bugGC.setFill(bugColor);
		for(int j = (int)colonyRadius, width = 1; j >= 1; j--, width++) {
			double x = rand.nextGaussian(colonyX, Math.sqrt(j*j*j));
			double y = rand.nextGaussian(colonyY, Math.sqrt(j*j*j));
			bugGC.fillOval(x, y, width, width);
		}
	}
	
	public void extinguishLight() {
		backLight.setZ(backLight.getZ() - 1.0);
		environmentLight.setZ(environmentLight.getZ() - 4.0);
		if(backLight.getZ() <= 0 && environmentLight.getZ() <= 0) {
			fadeLightComplete = true;
		}
	}
	
	public void setBgAlpha(double alpha) {
		bgAlpha = alpha;
		bgColor = Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha);
	}
	
	public void setBgRed(int red) {
		bgRed = red;
		bgColor = Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha);
	}
	
	public void setBgGreen(int green) {
		bgGreen = green;
		bgColor = Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha);
	}
	
	public void setBgBlue(int blue) {
		bgBlue = blue;
		bgColor = Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha);
	}
	
	public void setBugAlpha(double alpha) {
		bugAlpha = alpha;
		bugColor = Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha);
	}
	
	public void setBugRed(int red) {
		bugRed = red;
		bugColor = Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha);
	}
	
	public void setBugGreen(int green) {
		bugGreen = green;
		bugColor = Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha);
	}
	
	public void setBugBlue(int blue) {
		bugBlue = blue;
		bugColor = Color.rgb(bugRed, bugGreen, bugBlue, bugAlpha);
	}
	
	public void setEnemyAlpha(double alpha) {
		enemyAlpha = alpha;
		enemyColor = Color.rgb(enemyRed, enemyGreen, enemyBlue, enemyAlpha);
	}
	
	public void setEnemyRed(int red) {
		enemyRed = red;
		enemyColor = Color.rgb(enemyRed, enemyGreen, enemyBlue, enemyAlpha);
	}
	
	public void setEnemyGreen(int green) {
		enemyGreen = green;
		enemyColor = Color.rgb(enemyRed, enemyGreen, enemyBlue, enemyAlpha);
	}
	
	public void setEnemyBlue(int blue) {
		enemyBlue = blue;
		enemyColor = Color.rgb(enemyRed, enemyGreen, enemyBlue, enemyAlpha);
	}
	
	public double getCanvasWidth() {
		return canvasWidth;
	}
	
	public double getCanvasHeight() {
		return canvasHeight;
	}

	public boolean isDrawHPheros() {
		return drawHPhero;
	}

	public void setDrawHPheros(boolean drawHPhero) {
		this.drawHPhero = drawHPhero;
	}

	public boolean isDrawFPheros() {
		return drawFPhero;
	}

	public void setDrawFPheros(boolean drawFPhero) {
		this.drawFPhero = drawFPhero;
	}

	public boolean isDrawGrid() {
		return drawGrid;
	}

	public void setDrawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}
	
	public boolean getFadeLightStatus() {
		return fadeLightComplete;
	}
}