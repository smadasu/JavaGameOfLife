package com.ms;
	
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static final Image ICON_48 = new Image(Main.class.getResourceAsStream("/icon-48x48.png"));
	
	@Override
	public void start(Stage primaryStage) {
	    // create a grid with some sample data.
	    GridPane grid = new GridPane();
	    for(int row=0; row < 20; row++) {
	    	Label[] labels = new Label[20];
	    	for(int column=0; column < 20; column++) {
	    		labels[column] = new Label();
	    	}
	    	grid.addRow(row, labels);
	    }
	    for (Node n: grid.getChildren()) {
	      if (n instanceof Control) {
	        Control control = (Control) n;
	        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	        control.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
	      }
	      if (n instanceof Pane) {
	        Pane pane = (Pane) n;
	        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	        pane.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
	      }
	    }
	    grid.setStyle("-fx-background-color: palegreen; -fx-padding: 2; -fx-hgap: 2; -fx-vgap: 2;");
	    grid.setSnapToPixel(false);

	    ColumnConstraints oneThird = new ColumnConstraints();
	    oneThird.setPercentWidth(100/20.0);
	    oneThird.setHalignment(HPos.CENTER);
	    for(int column=0; column < 20; column++) {
	    	grid.getColumnConstraints().add(oneThird);
	    }
	    RowConstraints oneHalf = new RowConstraints();
	    oneHalf.setPercentHeight(100/20.0);
	    oneHalf.setValignment(VPos.CENTER);
	    for(int row=0; row < 20; row++) {
	    	grid.getRowConstraints().add(oneHalf);
	    }
	    
	    StackPane layout = new StackPane();
	    layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
	    layout.getChildren().addAll(grid);
	    primaryStage.setScene(new Scene(layout, 600, 400));
	    primaryStage.show();
	    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
