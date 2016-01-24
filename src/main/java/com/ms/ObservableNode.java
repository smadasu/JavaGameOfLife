package com.ms;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class ObservableNode extends Rectangle implements Observable, Observer {
	
	private List<Observer> neighbors = null;

	public ObservableNode(double width, double height, Paint fill) {
		super(width, height, fill);
		neighbors = new ArrayList<>();
	}

	@Override
	public void register(Observer observer) {
		neighbors.add(observer);
	}

	@Override
	public void unregister(Observer observer) {
		neighbors.remove(observer);
	}

	@Override
	public void notifyObservers() {
		neighbors.stream().forEach(node->node.update(getFill()));
	}
	
	@Override
	public void update(Paint fillValue) {
		long aliveNeighbors = getAliveNeighbors();
		if (getFill().equals(Color.BLACK) && (aliveNeighbors < 2 || aliveNeighbors > 3)) {
			setFill(Color.WHITE);
		} else if (aliveNeighbors == 3) {
			setFill(Color.BLACK);
		}
	}
	
	public long getAliveNeighbors() {
		return neighbors.stream()
										  .filter(neighbor->((Rectangle) neighbor).getFill().equals(Color.BLACK))
										  .count();
	}
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Row Index = ");
		stringBuilder.append(GridPane.getRowIndex(this));
		stringBuilder.append(":Column Index = ");
		stringBuilder.append(GridPane.getColumnIndex(this));
		stringBuilder.append(":Alive Neighbors = ");
		stringBuilder.append(getAliveNeighbors());
		return stringBuilder.toString();
	}
	
}
