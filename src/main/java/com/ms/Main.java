package com.ms;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import javafx.application.Application;
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

public class Main extends Application {

	private static final int NUMBER_OF_ROWS = 20;
	private static final int NUMBER_OF_COLUMNS = 20;

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
		primaryStage.show();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			run.set(true);
			//System.out.println("run = " + run.get());
			while(run.get()) {
				grid.getChildren().parallelStream().forEach(node -> applyBackground(node, grid));
			}
		});
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
				boolean onFlag = ((Math.round(Math.random() * 100) % 2) == 0);
				Rectangle rectangle = new Rectangle(10, 10, onFlag ? Color.BLACK : Color.WHITE);
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
		button1.setOnAction(e -> run.set(true));
		ToggleButton button2 = new ToggleButton("Pause");
		button2.setOnAction(e -> run.set(false));

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
		int currentRow = ind / NUMBER_OF_ROWS;
		int currentColumn = ind - (ind / NUMBER_OF_ROWS) * NUMBER_OF_COLUMNS;
		int[] neighbors = { (currentRow - 1) * NUMBER_OF_ROWS + currentColumn - 1, (currentRow - 1) * NUMBER_OF_ROWS + currentColumn,
			(currentRow - 1) * NUMBER_OF_ROWS + currentColumn + 1, currentRow * NUMBER_OF_ROWS + currentColumn - 1,
			currentRow * NUMBER_OF_ROWS + currentColumn + 1, (currentRow + 1) * NUMBER_OF_ROWS + currentColumn - 1,
			(currentRow + 1) * NUMBER_OF_ROWS + currentColumn, (currentRow + 1) * NUMBER_OF_ROWS + currentColumn + 1 };
		long aliveNeighbors = Arrays.stream(neighbors).filter(index -> {
			if (index < 0 || index > (NUMBER_OF_ROWS * NUMBER_OF_COLUMNS - 1)) {
				return false;
			}
			Rectangle rectangle = (Rectangle) children.get(index);
			return rectangle.getFill().equals(Color.BLACK);
		}).count();
		Rectangle currentRectangle = (Rectangle) node;
		if (currentRectangle.getFill().equals(Color.BLACK) && (aliveNeighbors < 2 || aliveNeighbors > 3)) {
			//System.out.println("aliveNeighbors = " + aliveNeighbors);
			//System.out.println("dying");
			currentRectangle.setFill(Color.WHITE);
		} else if (aliveNeighbors == 3) {
			//System.out.println("bathuking");
			currentRectangle.setFill(Color.BLACK);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
