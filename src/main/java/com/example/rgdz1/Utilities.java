package com.example.rgdz1;

import java.util.Arrays;

public class Utilities {
	public static double median ( double ...values ) {
		Arrays.sort ( values );
		
		int index = values.length / 2;
		if ( values.length % 2 == 1 ) {
			return values[index];
		} else {
			return ( values[index - 1] + values[index] ) / 2;
		}
	}
}
