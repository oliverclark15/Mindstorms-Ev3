/**
 * UltrasonicLocalisationData.java
 */

package ca.mcgill.ecse211.capturetheflag;

import lejos.hardware.Sound;

/**
 * The UltrasonoicLocationData class is used to process date from the
 * ultrasonic poller to detect edges
 * 
 * @author Michael Vaquier
 *
 */

public class UltrasonicLocalisationData {
	
	private Localisation localisation;
	
	private boolean fallingEdge = false;
	private boolean foundFirstEdge = false;
	
	private int lastData = -1;
	
	private static final int EDGE_THRESHOLD = 50;
	
	/**
	 * Constructs an UltrasonicLocalisationData object.
	 */
	public UltrasonicLocalisationData() {
	}
	
	/**
	 * Processes the data coming from the ultrasonic poller and sends an interrupt to the localisation class when
	 * an edge is found. The process looks for falling and rising edges at the same time and sticks with one
	 * once the first edge is found.
	 * @param newVal  New value read from the Ultrasonic sensor
	 */
	public void processData(int newVal) {
		if (lastData < 0) {
			lastData = newVal;
		} else {
			if (!foundFirstEdge) {
				if (lastData >= EDGE_THRESHOLD && newVal <= EDGE_THRESHOLD) {
					fallingEdge = true;
					foundFirstEdge = true;
					localisation.setFallingEdge(fallingEdge);
					lastData = -1;
					localisation.resumeThread();
					threadWait();
				} else if (lastData <= EDGE_THRESHOLD && newVal >= EDGE_THRESHOLD) {
					fallingEdge = false;
					foundFirstEdge = true;
					localisation.setFallingEdge(fallingEdge);
					lastData = -1;
					localisation.resumeThread();
					threadWait();
				}
			} else {
				if (fallingEdge && lastData >= EDGE_THRESHOLD && newVal <= EDGE_THRESHOLD) {
					localisation.resumeThread();
					foundFirstEdge = false;
					threadWait();
				} else if (!fallingEdge && lastData <= EDGE_THRESHOLD && newVal >= EDGE_THRESHOLD) {
					localisation.resumeThread();
					foundFirstEdge = false;
					threadWait();
				}
			}
			lastData = newVal;
		}
	}
	
	/**
	 * Sets the localisation association to the instance.
	 * @param localisation  Association to the new Localisation
	 */
	public void setLocalisation(Localisation localisation) {
		this.localisation = localisation;
	}
	
	/**
	 * Puts the thread to sleep for one second.
	 */
	private void threadWait() {
		Sound.beep();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

}
