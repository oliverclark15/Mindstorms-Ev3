package ca.mcgill.ecse211.odometerlab;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	
	private Object lock = new Object();
	
	
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	
	private int interruptedTheta = -1; //This angle is the angle when the navigation was interrupted
	
	private NavigationController controller;
	
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private double leftRadius;
	private double rightRadius;
	private double width;


	private final int[][] points = { { 2, 1 }, { 1, 2 }, { 1, 1 }, { 2, 0 } };

	public Navigation(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double leftRadius,
		double rightRadius, double width) {
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		this.leftMotor.setAcceleration(1000);
		this.rightMotor.setAcceleration(1000);
	}

	/*
	 * This method causes the robot to travel to the absolute field location (x,
	 * y), specified in tilepoints.This method should continuously
	 * callturnTo(double theta)and thenset the motor speed to forward(straight).
	 * This will make sure that yourheading is updated until you reach your
	 * exact goal. This method will pollthe odometer for informatio
	 */
	public void travelTo(int x, int y) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
		}
		
		
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		
		double nextHeading = Math.toDegrees(Math.atan2(x - currentX, y - currentY));

		turnTo(nextHeading);
		
		
		leftMotor.setSpeed(FORWARD_SPEED);
	    rightMotor.setSpeed(FORWARD_SPEED);
	    
	    double distance = Math.sqrt(Math.pow((y - currentY),2) + Math.pow((x - currentX),2));
	    
	    leftMotor.rotate(convertDistance(leftRadius, distance), true);
	    rightMotor.rotate(convertDistance(rightRadius, distance), true);
	    
	   
	}
	
	public void turnTo(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);
	    rightMotor.setSpeed(ROTATE_SPEED);
		double currentTheta = odometer.getThetaDegrees();
		double rightRotation = theta - currentTheta;
		if (rightRotation < 0) {
			rightRotation = rightRotation + 360;
		}
		double leftRotation = currentTheta - theta;
		if (leftRotation < 0) {
			leftRotation = leftRotation + 360;
		}
		
		if (rightRotation < leftRotation) {
			leftMotor.rotate(convertAngle(leftRadius, width, rightRotation), true);
		    rightMotor.rotate(-convertAngle(rightRadius, width, rightRotation), false);
		}
		else {
			leftMotor.rotate(-convertAngle(leftRadius, width, leftRotation), true);
		    rightMotor.rotate(convertAngle(rightRadius, width, leftRotation), false);
		}
	}
	
	public void interruptNav() {
		rightMotor.stop();
		leftMotor.stop();
		
		
		leftMotor.rotate(convertAngle(leftRadius, width, 75), true); //when an object is detected, we turn to starting to follow it as a wall.
	    rightMotor.rotate(-convertAngle(rightRadius, width, 75), false);
	    
	    setInterruptedTheta((int) odometer.getThetaDegrees());
	    
	    
	}


	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public void setInterruptedTheta(int theta) {
		synchronized(lock) {
			interruptedTheta = theta;
		}
	}
	
	public int getInterruptedTheta() {
		synchronized(lock) {
			return interruptedTheta;
		}
	}
	
	public void setController(NavigationController cont) {
		this.controller = cont;
	}
	
	
}
