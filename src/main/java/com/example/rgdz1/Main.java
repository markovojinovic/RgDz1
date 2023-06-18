package com.example.rgdz1;

import com.example.code.objects.Helipad;
import com.example.code.objects.Package;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	private static final double WINDOW_WIDTH  = 750;
	private static final double WINDOW_HEIGHT = 750;
	
	private static final double HELICOPTER_WIDTH          = 0.03 * Main.WINDOW_WIDTH;
	private static final double HELICOPTER_HEIGHT         = 0.07 * Main.WINDOW_HEIGHT;
	private static final double HELICOPTER_SPEED_STEP     = 5;
	private static final double HELICOPTER_DIRECTION_STEP = 5;
	private static final double HELICOPTER_DAMP           = 0.995;
	
	private static final double HELIPAD_WIDTH  = 0.1 * Main.WINDOW_WIDTH;
	private static final double HELIPAD_HEIGHT = 0.1 * Main.WINDOW_HEIGHT;
	
	private static final double PACKAGE_WIDTH  = 0.02 * Main.WINDOW_WIDTH;
	private static final double PACKAGE_HEIGHT = 0.02 * Main.WINDOW_HEIGHT;
	
	@Override
	public void start ( Stage stage ) throws IOException {
		Group root = new Group ( );
	
		Helicopter helicopter = new Helicopter ( Main.HELICOPTER_WIDTH, Main.HELICOPTER_HEIGHT );
		
		Helipad helipad = new Helipad ( Main.HELIPAD_WIDTH, Main.HELIPAD_HEIGHT );
		helipad.getTransforms ( ).addAll (
				new Translate ( -Main.HELIPAD_WIDTH / 2, - Main.HELIPAD_HEIGHT / 2 )
		);
		
		Translate package0Position = new Translate (
				-Main.PACKAGE_WIDTH / 2 + Main.WINDOW_WIDTH / 3,
				-Main.PACKAGE_HEIGHT / 2 - Main.WINDOW_HEIGHT / 3
		);
		Translate package1Position = new Translate (
				-Main.PACKAGE_WIDTH / 2 - Main.WINDOW_WIDTH / 3,
				-Main.PACKAGE_HEIGHT / 2 - Main.WINDOW_HEIGHT / 3
		);
		Translate package2Position = new Translate (
				-Main.PACKAGE_WIDTH / 2 + Main.WINDOW_WIDTH / 3,
				Main.PACKAGE_HEIGHT / 2 + Main.WINDOW_HEIGHT / 3
		);
		Translate package3Position = new Translate (
				-Main.PACKAGE_WIDTH / 2 - Main.WINDOW_WIDTH / 3,
				Main.PACKAGE_HEIGHT / 2 + Main.WINDOW_HEIGHT / 3
		);
		Package packages[] = {
				new Package ( Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package0Position ),
				new Package ( Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package1Position ),
				new Package ( Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package2Position ),
				new Package ( Main.PACKAGE_WIDTH, Main.PACKAGE_HEIGHT, package3Position )
		};
		
		root.getChildren ( ).addAll ( helipad, helicopter );
		root.getChildren ( ).addAll ( packages );
		
		root.getTransforms ( ).addAll (
				new Translate ( Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT / 2 )
		);
		Scene scene = new Scene ( root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT );
		
		scene.addEventHandler ( KeyEvent.KEY_PRESSED, event -> {
			if ( event.getCode ( ).equals ( KeyCode.UP ) ) {
				helicopter.changeSpeed ( Main.HELICOPTER_SPEED_STEP );
			} else if ( event.getCode ( ).equals ( KeyCode.DOWN ) ) {
				helicopter.changeSpeed ( -Main.HELICOPTER_SPEED_STEP );
			}
			
			if ( event.getCode ( ).equals ( KeyCode.LEFT ) ) {
				helicopter.rotate (
						-Main.HELICOPTER_DIRECTION_STEP,
						0,
						Main.WINDOW_WIDTH,
						0,
						Main.WINDOW_HEIGHT
				);
			} else if ( event.getCode ( ).equals ( KeyCode.RIGHT ) ) {
				helicopter.rotate (
						Main.HELICOPTER_DIRECTION_STEP,
						0,
						Main.WINDOW_WIDTH,
						0,
						Main.WINDOW_HEIGHT
				);
			}
		} );
		
		MyTimer.IUpdatable helicopterWrapper = ds -> {
			helicopter.update (
					ds,
					Main.HELICOPTER_DAMP,
					0,
					Main.WINDOW_WIDTH,
					0,
					Main.WINDOW_HEIGHT
			);
			
			for ( int i = 0; i < packages.length; ++i ) {
				if ( packages[i] != null && packages[i].handleCollision ( helicopter.getBoundsInParent ( ) ) ) {
					root.getChildren ( ).remove ( packages[i] );
					
					packages[i] = null;
				}
			}
		};
		
		MyTimer myTimer = new MyTimer ( helicopterWrapper );
		myTimer.start ( );
		
		scene.setFill ( Color.GREEN );
		stage.setTitle ( "Helicopter" );
		stage.setScene ( scene );
		stage.setResizable ( false );
		stage.show ( );
	}
	
	public static void main ( String[] args ) {
		launch ( );
	}
}