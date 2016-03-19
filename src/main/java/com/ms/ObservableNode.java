package com.ms;

import java.util.ArrayList;
import java.util.List;

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
		for(Observer node : neighbors) {
			node.transformedFill();
		}
	}
	
	@Override
	public Paint transformedFill() {
		long aliveNeighbors = getAliveNeighbors();
		Paint fill = getFill();
		if (fill.equals(Color.BLACK)) {
			if (aliveNeighbors < 2 || aliveNeighbors > 3) {
				return Color.WHITE;	
			}
		} else if ((fill.equals(Color.WHITE)) && aliveNeighbors == 3) {
			return Color.BLACK;		
		}
		return fill;
	}
	
	public int getAliveNeighbors() {
		int count = 0;
		for(Observer node : neighbors) {
			if (((Rectangle) node).getFill().equals(Color.BLACK)) {
				count++;
			}
		}
		return count;
	}

	public List<Observer> getNeighbors() {
		return neighbors;
	}
	
}
