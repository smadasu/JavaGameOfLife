package com.ms;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.util.Duration;

public class GameOfLife extends Application {

	public static final int NUMBER_OF_ROWS = 40;
	public static final int NUMBER_OF_COLUMNS = 40;
	private Timeline timeline;

	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		VBox vBox = new VBox();
		vBox.setSpacing(20);
		GridPane grid = createGrid();
		StackPane layout = new StackPane();
		HBox buttonBar = createSegmentedButtonBar(grid);
		vBox.getChildren().addAll(grid, buttonBar);
		layout.getChildren().add(vBox);
		primaryStage.setScene(new Scene(layout, 280, 350));
		timeline = new Timeline(new KeyFrame(Duration.millis(50), ae -> {
					grid.getChildren().stream()
							.collect(Collectors.toMap(node -> node, node -> ((GameNode)node).transformedFill()))
							.entrySet().stream()
							.filter(entry -> !((Rectangle)entry.getKey()).getFill().equals(entry.getValue()))
							.forEach(entry -> ((Rectangle)entry.getKey()).setFill(entry.getValue()));
		        	
		        }));
		timeline.setCycleCount(Animation.INDEFINITE);
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
		grid.getColumnConstraints().add(columnConstraints);
		grid.getRowConstraints().add(rowConstraints);

		Random random = new Random();
		List<Integer> randomNumbers = random.ints(0, NUMBER_OF_ROWS)
			.limit(30)
			.boxed()
			.collect(Collectors.toList());
		IntStream.range(0, NUMBER_OF_ROWS).forEach(row -> {
			IntStream.range(0, NUMBER_OF_COLUMNS).forEach(column -> {
				boolean onFlag = (random.nextInt(2) == 1 && randomNumbers.contains(row)) ? randomNumbers.contains(column) : false;
				GameNode rectangle = new GameNode(5, 5, onFlag ? Color.BLACK : Color.WHITE);	
				grid.add(rectangle, column, row);
			});
		});
		ObservableList<Node> children = grid.getChildren();
		children.parallelStream().forEach(node -> ((GameNode)node).initializeNeighbors(children));
		return grid;
	}

	public HBox createSegmentedButtonBar(GridPane grid) {
		ToggleButton button1 = new ToggleButton("Start");		
		button1.setOnAction(e -> timeline.play());		
		ToggleButton button2 = new ToggleButton("Stop");		
		button2.setOnAction(e -> timeline.stop());
		ToggleGroup group = new ToggleGroup();
		group.getToggles().addAll(button1, button2);
		group.selectToggle(button2);		
		HBox  buttonBar = new HBox();
		buttonBar.setSpacing(20);
		buttonBar.getChildren().addAll(button1, button2);
		HBox displayBox = new HBox();
		displayBox.setSpacing(20);
		displayBox.setAlignment(Pos.CENTER);
		displayBox.getChildren().addAll(buttonBar);
		return displayBox;
	}

	public static void main(String[] args) throws Exception {
		launch(args);		
		System.exit(0);
	}
}
