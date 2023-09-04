package application;

import java.util.List;
import java.util.Random;

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
	private Colony colony;
	private BugManager bm;
	private EnemyManager em;
	private ObjectsManager om;
	private double bgAlpha, canvasWidth, canvasHeight;
	private int bgRed, bgGreen, bgBlue;
	private boolean drawHPhero, drawFPhero, drawGrid, fadeLightComplete;
	private Random rand;
	private Light.Point environmentLight;
	private Lighting environmentLighting;
	private Light.Point backLight;
	private Lighting backLighting;

	public CanvasManager(Colony colony) {
		this.colony = colony;
		om = colony.getOm();
		bm = colony.getBm();
		em = colony.getEm();
		rand = new Random();
		environmentLight = new Light.Point();
		environmentLight.setX(colony.getX());
		environmentLight.setY(colony.getY());
		environmentLight.setZ(300);
		environmentLighting = new Lighting();
		environmentLighting.setLight(environmentLight);
		environmentLighting.setBumpInput(null);
		backLight = new Light.Point();
		backLight.setX(colony.getX());
		backLight.setY(colony.getY());
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
		drawBackground();
		drawPheromones();
		drawFood();
		drawBugz();
		drawEnemies();
		drawColonyHealth();
		lightPlayingField();
	}
	
	// Used to draw in the main menu
	public void menuDraw() {
		bugGC.clearRect(0, 0, canvasWidth, canvasHeight);
		drawBackground();
		drawBugz();
		drawEnemies();
		lightPlayingField();
	}
	
	public void drawBackground() {
		backgroundGC.clearRect(0, 0, canvasWidth, canvasHeight);
		backgroundGC.setFill(Color.rgb(bgRed, bgGreen, bgBlue, bgAlpha));
		backgroundGC.fillRect(0, 0, canvasWidth, canvasHeight);
	}
	
	public void drawBugz() {
		List<Worker> workers = bm.getWorkers();
		List<Scout> scouts = bm.getScouts();
		List<Guard> guards = bm.getGuards();
		double radius = colony.getRadius();
		double colonyWidth = radius*2;
		Color bugColor = Color.rgb((int)bm.getBugRed(), (int)bm.getBugGreen(), (int)bm.getBugBlue(), bm.getBugAlpha());
		// Draw the colony first
		bugGC.setFill(Color.WHITE);
		bugGC.fillOval(colony.getX()-radius, colony.getY()-radius, colonyWidth, colonyWidth);
		
		// Next draw the workers
		for(int i = 0; i < workers.size(); i++) {
			Bug bug = workers.get(i);
			double bugX = bug.getX();
			double bugY = bug.getY();
			if(bug.isDead()) {
				drawBugDeath(bug);
				continue;
			}
			else if(!bug.isHome()) {
				double bugSize = bug.getSize();
				double bugOffset = bugSize/2;
				bugGC.setFill(bugColor);
				bugGC.fillOval(bugX-bugOffset, bugY-bugOffset, bugSize, bugSize);
				if(bug.hasFood()) {
					bugGC.setFill(Paint.valueOf("#ffff00"));
					bugGC.fillOval(bugX-bugSize, bugY-bugSize, bugSize*2, bugSize*2);
				}
			}
		}
		
		// Next draw the scouts
		for(int i = 0; i < scouts.size(); i++) {
			Bug bug = scouts.get(i);
			double bugX = bug.getX();
			double bugY = bug.getY();
			if(bug.isDead()) {
				drawBugDeath(bug);
				continue;
			}
			else if(!bug.isHome()) {
				double bugSize = bug.getSize();
				double bugOffset = bugSize * Math.sqrt(3) / 2.0;
				double x1 = bugX - bugSize / 2;
				double y1 = bugY + bugOffset / 2;
				double x2 = bugX + bugSize / 2;
				double y2 = bugY + bugOffset / 2;
				double x3 = bugX;
				double y3 = bugY - bugOffset / 2;
				// if bug is attacking, draw attack animation before drawing bug so that bug is ontop of animation
				if(bug.isAttacking()) {
					if(bug.getAttackStage() == bug.getMaxAttackStages()) {
						Enemy enemy = bug.getTargetEnemy();
						bugGC.setStroke(Color.RED);
						bugGC.setLineWidth(4);
						bugGC.strokeLine(bugX, bugY, enemy.getX(), enemy.getY());
					}
				}
				if(bug.getLevelsPending() > 0) {
					applyLevelGlow(bug);
				}
				bugGC.setFill(bugColor);
				bugGC.fillPolygon(new double[] {x1,x2,x3}, new double[] {y1,y2,y3}, 3);
				// Draw health bar above bug if not at full health
				if(bug.getHealth() < bug.getMaxHealth()) {
					drawBugHealth(bug, bugX, bugY, bugSize);
				}
			}
		}
		
		// Next draw the guards
		for(int i = 0; i < guards.size(); i++) {
			Bug bug = guards.get(i);
			double bugX = bug.getX();
			double bugY = bug.getY();
			if(bug.isDead()) {
				drawBugDeath(bug);
				continue;
			}
			else if(!bug.isHome()) {
				double bugSize = bug.getSize();
				// if bug is attacking, draw attack animation before drawing bug so that bug is ontop of animation
				if(bug.isAttacking()) {
					if(bug.getAttackStage() == bug.getMaxAttackStages()) {
						Enemy enemy = bug.getTargetEnemy();
						bugGC.setStroke(Color.RED);
						bugGC.setLineWidth(4);
						bugGC.strokeLine(bugX, bugY, enemy.getX(), enemy.getY());
					}
				}
				bugGC.setFill(bugColor);
				bugGC.fillRect(bugX - bugSize, bugY - bugSize, bugSize*2, bugSize*2);
				if(bug.getHealth() < bug.getMaxHealth()) {
					drawBugHealth(bug, bugX, bugY, bugSize);
				}
			}
		}
	}
	
	public void drawEnemies() {
		Color enemyColor = Color.rgb((int)em.getEnemyRed(), (int)em.getEnemyGreen(), (int)em.getEnemyBlue(), em.getEnemyAlpha());
		bugGC.setFill(enemyColor);
		List<Enemy> enemies = em.getEnemies();
		for(int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
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
						bugGC.strokeLine(enemyX, enemyY, colony.getX(), colony.getY());
					}
				}
				bugGC.fillOval(enemy.getX()-enemyOffset, enemy.getY()-enemyOffset, enemySize, enemySize);
				// Draw health bar above bug if not at full health
				if(enemy.getHealth() < enemy.getMaxHealth()) {
					drawEnemyHealth(enemy, enemyX, enemyY, enemySize);
					bugGC.setFill(enemyColor);
				}
			}
			// But if the enemy is picked up, draw them slightly larger to make it look like they are closer to player
			else {
				double enemySize = enemy.getSize()*1.2;
				double enemyOffset = enemySize/2;
				bugGC.fillOval(enemy.getX()-enemyOffset, enemy.getY()-enemyOffset, enemySize, enemySize);
			}
		}
	}
	
	public void drawColonyHealth() {
		bugGC.setFill(Color.rgb(20,20,20,1.0));
		bugGC.fillText(Integer.toString(colony.getTrackers().getColonyHealth()), colony.getX(), colony.getY()+7, colony.getRadius()*2);
	}
	
	public void drawPheromones() {
		if(drawHPhero || drawFPhero || drawGrid) {
			PheromoneManager pm = colony.getBm().getPM();
			FoodPheromone[][] fPheros = pm.getFPheromone();
			HomePheromone[][] hPheros = pm.getHPheromone();
			double canvX = 0;
			double canvY = 0;
			double cellWidth = pm.getCellWidth();
			double cellHeight = pm.getCellHeight();
			for(int i = 0; i < pm.getXCells(); i++) {
				for(int j = 0; j < pm.getYCells(); j++) {
					boolean fPhero = fPheros[i][j].isActive();
					boolean hPhero = hPheros[i][j].isActive();
					if(drawGrid) {
						bugGC.setStroke(Paint.valueOf("#FFFFFF"));
						bugGC.strokeRect(canvX, canvY, cellWidth, cellHeight);
					}
					if(fPhero && hPhero && drawHPhero && drawFPhero) {
						bugGC.setFill(Color.rgb(150, 0, 150));
						bugGC.fillRect(canvX, canvY, cellWidth, cellHeight);
					}
					else if(hPhero && drawHPhero) {
						bugGC.setFill(Color.rgb(0, 0, 150));
						bugGC.fillRect(canvX, canvY, cellWidth, cellHeight);
					}
					else if(fPhero && drawFPhero) {
						bugGC.setFill(Color.rgb(150, 0, 0));
						bugGC.fillRect(canvX, canvY, cellWidth, cellHeight);
					}
					else
						bugGC.setFill(Color.TRANSPARENT);
					canvY += cellHeight;
				}
				canvX += cellWidth;
				canvY = 0;
			}
		}
	}
	
	public void drawFood() {
		List<Food> food = om.getFood();
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
		backgroundGC.setFill(Color.rgb((int)bug.getRed(), (int)bug.getGreen(), (int)bug.getBlue(), bug.getAlpha()));
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
		backgroundGC.setFill(Color.rgb((int)enemy.getRed(), (int)enemy.getGreen(), (int)enemy.getBlue(), enemy.getAlpha()));
		List<BloodSplatter> bloodSplatter = enemy.getBloodSplatters();
		for(int i = 0; i < bloodSplatter.size()/deathPhase; i++) {
			BloodSplatter bs = bloodSplatter.get(i);
			backgroundGC.fillOval(bs.getX(), bs.getY(), bs.getRadius(), bs.getRadius());
		}
		if(deathPhase > 1) {
			enemy.setDeathPhase(deathPhase - 1);
		}
	}
	
	private void applyLevelGlow(Bug bug) {
		
	}
	
	public void killColony() {
		bugGC.setFill(Color.rgb((int)bm.getBugRed(), (int)bm.getBugGreen(), (int)bm.getBugBlue(), (int)bm.getBugAlpha()));
		for(int j = (int)colony.getRadius(), width = 1; j >= 1; j--, width++) {
			double x = rand.nextGaussian(colony.getX(), Math.sqrt(j*j*j));
			double y = rand.nextGaussian(colony.getY(), Math.sqrt(j*j*j));
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
	
	public double getBgAlpha() {
		return bgAlpha;
	}
	
	public void setBgAlpha(double alpha) {
		bgAlpha = alpha;
	}
	
	public int getBgRed() {
		return bgRed;
	}
	
	public void setBgRed(int red) {
		bgRed = red;
	}
	
	public int getBgGreen() {
		return bgGreen;
	}
	
	public void setBgGreen(int green) {
		bgGreen = green;
	}
	
	public int getBgBlue() {
		return bgBlue;
	}
	
	public void setBgBlue(int blue) {
		bgBlue = blue;
	}
	
	public void setEnemyManager(EnemyManager em) {
		this.em = em;
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