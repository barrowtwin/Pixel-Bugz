package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TopController implements Initializable {

	@FXML private StackPane top;
	@FXML private TextFlow title;
	@FXML private Label latencyText;
	@FXML private Button closeButton, configButton;
	
	private MainController controller;
	
	private Image exitImg, configImg;
	private ImageView exitView, configView;
	private Text text1, text2, text3, text4, text5;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		text1 = new Text();
		text2 = new Text();
		text3 = new Text();
		text4 = new Text();
		text5 = new Text();
		
		exitImg = new Image("application/resources/x.png");
		exitView = new ImageView(exitImg);
		configImg = new Image("application/resources/cog.png");
		configView = new ImageView(configImg);
		
		exitView.setFitHeight(closeButton.getHeight());
		exitView.setFitWidth(closeButton.getWidth());
		exitView.setScaleX(0.17);
		exitView.setScaleY(0.17);
		configView.setFitHeight(configButton.getHeight());
		configView.setFitWidth(configButton.getWidth());
		configView.setScaleX(0.17);
		configView.setScaleY(0.17);
		closeButton.setGraphic(exitView);
		configButton.setGraphic(configView);
		closeButton.setOpacity(0.04);
		configButton.setOpacity(0.04);
		
		text1.setText("Pixel ");
		text1.setFill(Paint.valueOf("#ffffff"));
		text1.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text2.setText("B");
		text2.setFill(Paint.valueOf("#8a2724"));
		text2.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text3.setText("u");
		text3.setFill(Paint.valueOf("#248a27"));
		text3.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text4.setText("g");
		text4.setFill(Paint.valueOf("#24358a"));
		text4.setFont(Font.font("System", FontPosture.ITALIC, 32));
		text5.setText("z");
		text5.setFill(Paint.valueOf("ffffff"));
		text5.setFont(Font.font("System", FontPosture.ITALIC, 32));
		title.getChildren().addAll(text1, text2, text3, text4, text5);
	}
	
	public void setMainController(MainController controller) {
		this.controller = controller;
	}
	
	public void startGame() {
		
	}
	
	public void updateUI(double latency) {
		latencyText.setText("" + (latency*1000) + "ms");
	}
	
	public void close() {
		System.out.println("Click close button");
		javafx.application.Platform.exit();
	}
	
	public void config() {
		controller.config();
	}
}
