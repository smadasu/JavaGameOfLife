package com.ms;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.animation.Transition;
import javafx.animation.Animation;
import javafx.util.Duration;

public class Main extends Application {

	private static final int NUMBER_OF_ROWS = 30;
	private static final int NUMBER_OF_COLUMNS = 30;

	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		VBox vBox = new VBox();
		vBox.setSpacing(20);
		GridPane grid = createGrid();
		StackPane layout = new StackPane();
		layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
		HBox buttonBar = createSegmentedButtonBar(grid);
		vBox.getChildren().addAll(grid, buttonBar);
		layout.getChildren().add(vBox);
		primaryStage.setScene(new Scene(layout, 400, 450));
		primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
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
								((ObservableNode)node).register((Observer) children.get(neighborIndex));
							}
						});
					});
				});
			}
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
		List<Integer> randomNumbers = random.ints(0, 50)
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
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
		ToggleButton button1 = new ToggleButton("Start");		
		button1.setOnAction(e -> {

			IntStream.range(0, 10).forEach(index -> {
				scheduledThreadPool.scheduleAtFixedRate(() -> {
					grid.getChildren().stream().forEach(node->	 ((ObservableNode)node).notifyObservers());
				}, 1, 100, TimeUnit.MILLISECONDS);
			});
			
		});
		ToggleButton button2 = new ToggleButton("Pause");
		button2.setOnAction(e -> {
			scheduledThreadPool.shutdownNow();
		});

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

	public static void main(String[] args) throws Exception {
		launch(args);		
	}
}
