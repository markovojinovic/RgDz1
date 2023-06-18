package com.example.rgdz1;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Helicopter extends Group {
	
	private Point2D direction;
	private Rotate rotate;
	private Translate position;
	private double speed;
	
	private Circle cockpit;
	private Rectangle tail;
	
	public Helicopter ( double width, double height ) {
		this.cockpit = new Circle ( width  / 2 );
		this.cockpit.setFill ( null );
		this.cockpit.setStroke ( Color.BLACK );
		this.cockpit.setStrokeWidth ( 3 );
		
		double tailWidth = 0.2 * width;
		double tailHeight = height - width / 2;
		this.tail = new Rectangle ( tailWidth, tailHeight );
		this.tail.setFill ( Color.BLACK );
		this.tail.getTransforms ( ).addAll (
				new Translate ( -tailWidth / 2, 0 )
		);
		
		super.getChildren ( ).addAll ( this.cockpit, this.tail );
		
		this.direction = new Point2D ( 0, -1 );
		this.rotate    = new Rotate ( 0 );
		this.position  = new Translate ( );
		
		super.getTransforms ( ).addAll (
				this.position,
				this.rotate
		);
	}
	
	private boolean isWallHit (  double left, double right, double up, double down ) {
		Bounds cockpitBounds = this.cockpit.localToScene ( this.cockpit.getBoundsInLocal ( ) );
		Bounds tailBounds    = this.tail.localToScene ( this.tail.getBoundsInLocal ( ) );
		
		double cockpitMinX = cockpitBounds.getCenterX ( ) - this.cockpit.getRadius ( );
		double cockpitMaxX = cockpitBounds.getCenterX ( ) + this.cockpit.getRadius ( );
		double cockpitMinY = cockpitBounds.getCenterY ( ) - this.cockpit.getRadius ( );
		double cockpitMaxY = cockpitBounds.getCenterY ( ) + this.cockpit.getRadius ( );
		
		boolean cockpitWallHit = cockpitMinX <= left || cockpitMaxX >= right || cockpitMinY <= up || cockpitMaxY >= down;
		
		double tailMinX = tailBounds.getMinX ( );
		double tailMaxX = tailBounds.getMaxX ( );
		double tailMinY = tailBounds.getMinY ( );
		double tailMaxY = tailBounds.getMaxY ( );
		
		boolean tailWallHit = tailMinX <= left || tailMaxX >= right || tailMinY <= up || tailMaxY >= down;
		
		
		return cockpitWallHit || tailWallHit;
	}
	
	public void rotate ( double dAngle, double left, double right, double up, double down  ) {
		double oldAngle = this.rotate.getAngle ( );
		
		double newAngle = oldAngle + dAngle;
		this.rotate.setAngle ( newAngle );
		
		if ( this.isWallHit ( left, right, up, down ) ) {
			this.rotate.setAngle ( oldAngle );
		} else {
			double magnitude = this.direction.magnitude ( );
			this.direction = new Point2D (
					magnitude * Math.sin ( Math.toRadians ( newAngle ) ),
					-magnitude * Math.cos ( Math.toRadians ( newAngle ) )
			);
		}
	}
	
	public void changeSpeed ( double dSpeed ) {
		this.speed = Math.max ( this.speed + dSpeed, 0 );
	}
	
	public void update ( double ds, double speedDamp, double left, double right, double up, double down ) {
		double oldX = this.position.getX ( );
		double oldY = this.position.getY ( );
	
		double newX = oldX + ds * this.speed * this.direction.getX ( );
		double newY = oldY + ds * this.speed * this.direction.getY ( );
		
		this.position.setX ( newX );
		this.position.setY ( newY );
		
		if ( this.isWallHit ( left, right, up, down ) ) {
			this.speed = 0;
			this.position.setX ( oldX );
			this.position.setY ( oldY );
		} else {
			this.speed *= speedDamp;
		}
	}
}
