package com.ms;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static final int NUMBER_OF_ROWS = 50;
	private static final int NUMBER_OF_COLUMNS = 50;
	
	private AtomicBoolean run = new AtomicBoolean();


	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		run.set(false);
		VBox vBox = new VBox();
        vBox.setSpacing(20);
		// create a grid with some sample data.
		GridPane grid = createGrid();

		StackPane layout = new StackPane();
		layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
		HBox buttonBar = createSegmentedButtonBar();
		vBox.getChildren().addAll(grid, buttonBar);
		layout.getChildren().add(vBox);
		primaryStage.setScene(new Scene(layout, 400, 450));
		new Thread(() -> {
			while (run.get()) {
				System.out.println("RUNNING");
			}
		}).start();
		primaryStage.show();
	}

	private GridPane createGrid() {
		GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: palegreen; -fx-padding: 2; -fx-hgap: 2; -fx-vgap: 2;");
		grid.setSnapToPixel(false);
		
		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setPercentWidth(100 / NUMBER_OF_COLUMNS);
		columnConstraints.setHalignment(HPos.CENTER);
		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPercentHeight(100 / NUMBER_OF_ROWS);
		rowConstraints.setValignment(VPos.CENTER);
		IntStream.range(0, NUMBER_OF_ROWS).forEach(row -> {
			IntStream.range(0, NUMBER_OF_COLUMNS).forEach(column -> {
				boolean onFlag = ((Math.round(Math.random() * 1000000) % 2) == 0);
				Rectangle rectangle = new Rectangle(5, 5, onFlag ? Color.BLACK : Color.WHITE);
				grid.add(rectangle, column, row);
			});
			grid.getColumnConstraints().add(columnConstraints);
			grid.getRowConstraints().add(rowConstraints);
		});
		return grid;
	}
    
    /**
     * Demonstrates the construction and usage of the {@link SegmentedButtonBar}
     * @return
     */
    public HBox createSegmentedButtonBar() {
        ToggleButton button1 = new ToggleButton("Start");
        button1.setOnAction(e -> 
	        Platform.runLater(() -> {
	            run.set(true);
	        })
        );
        ToggleButton button2 = new ToggleButton("Pause");
        button2.setOnAction(e -> 
	        Platform.runLater(() -> {
	        	run.set(false);
	        })
	    );
        
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(button1, button2);
        group.selectToggle(button1);
        
        HBox displayBox = new HBox();
        displayBox.setSpacing(20);
        displayBox.setAlignment(Pos.CENTER);
        
        HBox  buttonBar = new HBox();
        buttonBar.setSpacing(20);
        buttonBar.getChildren().addAll(button1, button2);
        
        displayBox.getChildren().addAll(buttonBar);
        
        return displayBox;
    }

	private void applyBackground(Node node, GridPane grid) {
		List<Node> children = grid.getChildren();
		int ind = children.indexOf(node);
		int currentRow = ind / 20;
		int currentColumn = ind - (ind / 20) * 20;
		int[] neighbors = { (currentRow - 1) * 20 + currentColumn - 1, (currentRow - 1) * 20 + currentColumn,
				(currentRow - 1) * 20 + currentColumn + 1, currentRow * 20 + currentColumn - 1,
				currentRow * 20 + currentColumn + 1, (currentRow + 1) * 20 + currentColumn - 1,
				(currentRow + 1) * 20 + currentColumn, (currentRow + 1) * 20 + currentColumn + 1 };
		long aliveNeighbors = Arrays.stream(neighbors).filter(index -> {
			if (index < 0 || index > 399) {
				return false;
			}
			Control control1 = (Control) children.get(index);
			return control1.getStyle().contains("black");
		}).count();
		Control control = (Control) node;
		String currentStyle = control.getStyle();
//		System.out.println("current style" + currentStyle);
		if (currentStyle.contains("black") && (aliveNeighbors < 2 || aliveNeighbors > 3)) {
			System.out.println("aliveNeighbors = " + aliveNeighbors);
			System.out.println("dying");
			control.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
		} else if (aliveNeighbors == 3) {
			System.out.println("bathuking");
			control.setStyle("-fx-background-color: black; -fx-alignment: center;");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
