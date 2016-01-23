package com.ms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private static final int NUMBER_OF_ROWS = 50;
	private static final int NUMBER_OF_COLUMNS = 50;

	private AtomicBoolean run = new AtomicBoolean(false);
	private GridPane grid;
	private Map<Integer, List<Integer>> neighborsMap = null;
	
	public Main() {
		neighborsMap = new HashMap<>();
		IntStream.range(0, NUMBER_OF_ROWS).forEach(rowIndex-> {
			IntStream.range(0, NUMBER_OF_COLUMNS).forEach(columnIndex -> {
				   ArrayList<Integer> neighborsList = new ArrayList<>();
				   neighborsMap.put(rowIndex * 50 + columnIndex, neighborsList);
				   IntStream.of(rowIndex -1, rowIndex, rowIndex + 1).forEach(rowInd -> {
					   IntStream.of(columnIndex - 1, columnIndex, columnIndex + 1).forEach(columnInd -> {
						   int neighborIndex = rowInd * 50 + columnInd;
						   if ((rowInd == rowIndex && columnInd == columnIndex) ||
								   rowInd < 0 || rowInd > 49 || columnInd < 0 || columnInd > 49){
							   //invalid cell
						   } else {
							   neighborsList.add(neighborIndex);
						   }
					   });
				   });				
			});
		});
	}


	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		VBox vBox = new VBox();
		vBox.setSpacing(20);
		grid = createGrid();
		StackPane layout = new StackPane();
		layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
		HBox buttonBar = createSegmentedButtonBar();
		vBox.getChildren().addAll(grid, buttonBar);
		layout.getChildren().add(vBox);
		primaryStage.setScene(new Scene(layout, 400, 450));
		primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				ObservableList<Node> children = grid.getChildren();
				children.stream().forEach(node -> {
					int currentIndex = children.indexOf(node);
					List<Integer> neighborIndexes = neighborsMap.get(currentIndex);
					long aliveNeighbors = neighborIndexes.stream()
												 .map(ind -> children.get(ind))
												 .filter(neighbor->((Rectangle) neighbor).getFill().equals(Color.BLACK))
												 .count();
					((ObservableNode)node).setAliveNeighbors(aliveNeighbors);
					if(aliveNeighbors>1)
					System.out.println(node);
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
			.limit(5)
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
	 * @return
	 */
	public HBox createSegmentedButtonBar() {
		ToggleButton button1 = new ToggleButton("Start");
		button1.setOnAction(e -> {
			grid.getChildren().stream().forEach(node->{
				Rectangle rectangle = (Rectangle)node;
				rectangle.fillProperty().addListener(new ChangeListener<Paint>() {

					@Override
					public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue) {
						System.out.println("CHANGED");
					}
				});
			});
		});
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

	private void applyBackground(Node node) {
		List<Node> children = grid.getChildren();
		int currentIndex = children.indexOf(node);
		List<Integer> neighborIndexes = neighborsMap.get(currentIndex);
		long aliveNeighbors = neighborIndexes.stream()
									 .map(neighborIndex -> children.get(neighborIndex))
									 .filter(neighborNode ->( (Shape)neighborNode).getFill().equals(Color.BLACK))
									 .count();
		System.out.println("currentIndex = " + currentIndex + " aliveNeighbors = " + aliveNeighbors);
		Rectangle currentRectangle = (Rectangle) node;
		if (currentRectangle.getFill().equals(Color.BLACK) && (aliveNeighbors < 2 || aliveNeighbors > 3)) {
			System.out.println("dying");
			currentRectangle.setFill(Color.WHITE);
		} else if (aliveNeighbors == 3) {
			System.out.println("bathuking");
			currentRectangle.setFill(Color.BLACK);
		}
	}

	public static void main(String[] args) throws Exception {
		launch(args);
		//t1.join();
	}
}
