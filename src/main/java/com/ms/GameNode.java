package com.ms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import static com.ms.GameOfLife.NUMBER_OF_ROWS;
import static com.ms.GameOfLife.NUMBER_OF_COLUMNS;

public class GameNode extends Rectangle {
	
	private List<Node> neighbors = null;

	public GameNode(double width, double height, Paint fill) {
		super(width, height, fill);
		neighbors = new ArrayList<>();
	}
	
	public void initializeNeighbors(ObservableList<Node> children) {
		int rowIndex = GridPane.getRowIndex(this);
		int columnIndex = GridPane.getColumnIndex(this);
		IntStream.of(rowIndex -1, rowIndex, rowIndex + 1).forEach(rowInd -> {
			IntStream.of(columnIndex - 1, columnIndex, columnIndex + 1).forEach(columnInd -> {
				int neighborIndex = rowInd * NUMBER_OF_COLUMNS + columnInd;
				if ((rowInd == rowIndex && columnInd == columnIndex) ||
					rowInd < 0 || rowInd > (NUMBER_OF_ROWS - 1) || columnInd < 0 || columnInd > (NUMBER_OF_COLUMNS - 1)){
					//invalid cell
				} else {
					neighbors.add(children.get(neighborIndex));
				}
			});
		});					
	}
	
	public Paint transformedFill() {
		long aliveNeighbors = neighbors.stream()
																.filter(node -> !((Rectangle) node).getFill().equals(Color.WHITE))
																.count();
		Paint fill = getFill();
		if (fill.equals(Color.WHITE) && (aliveNeighbors == 3)) {
			Random random = new Random();
			return Color.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
		} else if ((aliveNeighbors < 2 || aliveNeighbors > 3)){
			return Color.WHITE;
		}
		return fill;
	}
	
}
