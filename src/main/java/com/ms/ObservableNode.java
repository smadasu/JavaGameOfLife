package com.ms;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class ObservableNode extends Rectangle implements Observable, Observer {
	
	private List<Observer> observers = null;
	private long aliveNeighbors;

	public ObservableNode(double width, double height, Paint fill) {
		super(width, height, fill);
		observers = new ArrayList<>();
	}

	@Override
	public void register(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void unregister(Observer observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		observers.stream().forEach(node->node.update(getFill()));
	}
	
	@Override
	public void update(Paint fillValue) {
		if (fillValue.equals(Color.BLACK)) {
			aliveNeighbors++;
		} else {
			aliveNeighbors--;
		}
		updateMyStatus();
	}
	
	public void updateMyStatus() {
		if (getFill().equals(Color.BLACK) && (aliveNeighbors < 2 || aliveNeighbors > 3)) {
			System.out.println("dying");
			setFill(Color.WHITE);
		} else if (aliveNeighbors == 3) {
			System.out.println("bathuking");
			setFill(Color.BLACK);
		}
	}

	public long getAliveNeighbors() {
		return aliveNeighbors;
	}

	public void setAliveNeighbors(long aliveNeighbors) {
		this.aliveNeighbors = aliveNeighbors;
	}
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Row Index = ");
		stringBuilder.append(GridPane.getRowIndex(this));
		stringBuilder.append(":Column Index = ");
		stringBuilder.append(GridPane.getColumnIndex(this));
		stringBuilder.append(":Alive Neighbors = ");
		stringBuilder.append(aliveNeighbors);
		return stringBuilder.toString();
	}
	
}
