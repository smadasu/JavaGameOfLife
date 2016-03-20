package com.ms;

import java.util.List;
import java.util.Map;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Main extends Application {

	private static final int NUMBER_OF_ROWS = 40;
	private static final int NUMBER_OF_COLUMNS = 40;
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
		primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, windowEvent -> {
				ObservableList<Node> children = grid.getChildren();
				children.stream().forEach(node -> {
					int rowIndex = GridPane.getRowIndex(node);
					int columnIndex = GridPane.getColumnIndex(node);
					IntStream.of(rowIndex -1, rowIndex, rowIndex + 1).forEach(rowInd -> {
						IntStream.of(columnIndex - 1, columnIndex, columnIndex + 1).forEach(columnInd -> {
							int neighborIndex = rowInd * NUMBER_OF_COLUMNS + columnInd;
							if ((rowInd == rowIndex && columnInd == columnIndex) ||
								rowInd < 0 || rowInd > (NUMBER_OF_ROWS - 1) || columnInd < 0 || columnInd > (NUMBER_OF_COLUMNS - 1)){
								//invalid cell
							} else {
								Observer neighbor = (Observer) children.get(neighborIndex);
								((ObservableNode)node).register(neighbor);
							}
						});
					});					
				});				
		});		
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
				ObservableNode rectangle = new ObservableNode(5, 5, onFlag ? Color.BLACK : Color.WHITE);	
				grid.add(rectangle, column, row);
			});
		});
		return grid;
	}

	/**
	 * Demonstrates the construction and usage of the {@link SegmentedButtonBar}
	 * @param grid 
	 * @return
	 */
	public HBox createSegmentedButtonBar(GridPane grid) {
		ToggleButton button1 = new ToggleButton("Start");		
		button1.setOnAction(e -> {
			timeline = new Timeline(new KeyFrame(
			        Duration.millis(50),
			        ae -> {
						Map<Node, Paint> collect = grid.getChildren().stream()
								.collect(Collectors.toMap(node -> node, node -> ((ObservableNode)node).transformedFill()));
						collect.entrySet().stream()
								.forEach(entry -> ((Rectangle)entry.getKey()).setFill(entry.getValue()));
			        	
			        }));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
			
		});
		ToggleGroup group = new ToggleGroup();
		
		ToggleButton button2 = new ToggleButton("Stop");		
		button2.setOnAction(e -> {
			timeline.stop();
		});
		group.getToggles().addAll(button1, button2);
		group.selectToggle(button2);

		HBox displayBox = new HBox();
		displayBox.setSpacing(20);
		displayBox.setAlignment(Pos.CENTER);

		HBox  buttonBar = new HBox();
		buttonBar.setSpacing(20);
		buttonBar.getChildren().addAll(button1, button2);

		displayBox.getChildren().addAll(buttonBar);

		return displayBox;
	}

	public static void main(String[] args) throws Exception {
		launch(args);		
		System.exit(0);
	}
}
