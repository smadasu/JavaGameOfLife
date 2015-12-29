package com.ms;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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
import rx.Observable;
import rx.Subscriber;

public class Main extends Application {

	private static final int NUMBER_OF_ROWS = 50;
	private static final int NUMBER_OF_COLUMNS = 50;

	private AtomicBoolean run = new AtomicBoolean(false);
	private GridPane grid;


	@Override
	public void start(Stage primaryStage) throws InterruptedException {
		VBox vBox = new VBox();
		vBox.setSpacing(20);
		// create a grid with some sample data.
		grid = createGrid();

		StackPane layout = new StackPane();
		layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
		HBox buttonBar = createSegmentedButtonBar();
		vBox.getChildren().addAll(grid, buttonBar);
		layout.getChildren().add(vBox);
		primaryStage.setScene(new Scene(layout, 400, 450));
		primaryStage.show();
//		ExecutorService executorService = Executors.newFixedThreadPool(5);
//		IntStream.range(0,5).forEach(e->executorService.submit(new Processor()));
//		executorService.shutdown();
//		executorService.awaitTermination(1, TimeUnit.DAYS);
		//t1.start();
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
		Random random = new Random();
		List<Integer> randomNumbers = random.ints(0, 50)
			.limit(5)
			.boxed()
			.collect(Collectors.toList());
//		System.out.println("randomNumbers = " + randomNumbers);
		IntStream.range(0, NUMBER_OF_ROWS).forEach(row -> {
			IntStream.range(0, NUMBER_OF_COLUMNS).forEach(column -> {
				boolean onFlag = (random.nextInt(2) == 1 && randomNumbers.contains(row)) ? randomNumbers.contains(column) : false;
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
		button1.setOnAction(e -> {
			Observable.from(grid.getChildren())
								.subscribe(new Subscriber<Node>() {

									@Override
									public void onCompleted() {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onError(Throwable e) {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onNext(Node node) {
										applyBackground(node);
									}
									
								});
		});//grid.getChildren().stream().forEach(node->applyBackground(node,grid)));
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
		final int currentRow = GridPane.getRowIndex(node);
		int currentColumn = GridPane.getColumnIndex(node);
		long aliveNeighbors = children.stream()
					.filter(child->{
						Rectangle neighbor = (Rectangle)child;
						int childRow = GridPane.getRowIndex(neighbor);
						int childColumn = GridPane.getColumnIndex(neighbor);
						return ((childRow - currentRow) == -1 || (childRow - currentRow) == 1
								|| (childColumn - currentColumn) == -1 || (childRow - currentRow) == 1 &&
								neighbor.getFill().equals(Color.BLACK));
					})
					.count();
		
		System.out.println("aliveNeighbors = " + aliveNeighbors);
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
