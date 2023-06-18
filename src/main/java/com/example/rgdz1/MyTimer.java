package com.example.rgdz1;

import javafx.animation.AnimationTimer;

public class MyTimer extends AnimationTimer {
	public static interface IUpdatable {
		public void update ( double ds );
	}
	
	private long previous;
	private IUpdatable updatables[];
	public MyTimer ( IUpdatable ...updatables ) {
		this.updatables = new IUpdatable[updatables.length];
		for ( int i = 0; i < this.updatables.length; ++i ) {
			this.updatables[i] = updatables[i];
		}
	}
	
	@Override public void handle ( long now ) {
		if ( this.previous == 0 ) {
			this.previous = now;
		}
		
		double ds = ( now - this.previous ) / 1e9;
		this.previous = now;
		
		for ( int i = 0; i < this.updatables.length; ++i ) {
			this.updatables[i].update ( ds );
		}
	}
}
