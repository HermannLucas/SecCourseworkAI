package SecCourseworkAI;
import robocode.*;
import robocode.util.Utils;
import java.util.Random;
import java.util.ArrayList;
//import java.util.ListIterator;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math;

public class Lazy_learnning_robot extends AdvancedRobot
{
	public boolean explore = true;
	public boolean greedy = false;
	public int rounds = 0;
	public final int exRounds = 100;
	public final double alpha = 0.1;
	public int[][] qMatrix = new int[8*8*8*8*8][3];
	public ArrayList<Integer> statesArray = new ArrayList<Integer>();
	public int reward = 0;
	public Random random = new Random();
	public double bulletPower;
	
	public void run() {
	// Used to intanciate the Look up table but to demanding in ressources to be used in robocode
	// Commented as this bot relies on lazy implementation
	/*	for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				for (int c = 0; c < 8; c++) {
					for (int d = 0; d < 8; d++) {
						for (int e = 0; e < 8; e++) {
							statesArray.add(a + b * 10 + c * 100 + d * 1000 + e * 10000);
						}
					}
				}
			}
		}
		ListIterator<Integer> iter = statesArray.listIterator();
		while (iter.hasNext()) {
			int z = iter.nextIndex();
			qMatrix[z][0] = 0;
			qMatrix[z][1] = 0;
			qMatrix[z][2] = 0;
		}*/
	
	/*
	|	From git load()
	|	Called to load the prvious look up table
	*/
	try {
		load();
	} catch (IOException e) {
		e.printStackTrace();	
	}
		setTurnRadarRight(999999);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (explore) {
			System.out.format("Exploring round n%d out of %d%n", rounds, exRounds);
			
			// Generating our actual state
			int state = makeState(e);
			
			// If the actual state doesn't already exist
			// Instanciate it
			if(!statesArray.contains(state)) {
				statesArray.add(state);
				qMatrix[statesArray.indexOf(state)][0] = 0;
				qMatrix[statesArray.indexOf(state)][1] = 0;
				qMatrix[statesArray.indexOf(state)][2] = 0;
			}
			
			// Perform a random action	
			int action = random.nextInt(3);
			doAction(action, e);
			
			// Collects the reward and add it to the appropriate cell
			qMatrix[statesArray.indexOf(state)][action] += reward;

			rounds++;
			if (rounds == exRounds) {
				System.out.println("Time to strike !");
				explore = false;
				greedy = true;
			}
		}
		
		if (greedy) {
		
			// Generating our actual state
			int state = makeState(e);
			
			// If the actual state doesn't already exist
			// Instanciate it
			if(!statesArray.contains(state)) {				
				statesArray.add(state);
				qMatrix[statesArray.indexOf(state)][0] = 0;
				qMatrix[statesArray.indexOf(state)][1] = 0;
				qMatrix[statesArray.indexOf(state)][2] = 0;
			}
			
			// Get the action with the biggest reward for our current state
			int max = 0;
			int nextAction = random.nextInt(3);
			for (int i = 0; i < 3; i++) {
				if (qMatrix[statesArray.indexOf(state)][i] > max) {
					max = qMatrix[statesArray.indexOf(state)][i];
					nextAction = i;
				}
			}
			
			// Perform the action and update the reward values
			doAction(nextAction, e);
			qMatrix[statesArray.indexOf(state)][nextAction] += reward;
		}
	}
	
	public void onBulletHit(BulletHitEvent e){
		reward = 2;
	}
	
	public void onBulletHitBullet(BulletHitBulletEvent e) {
		reward = 1;
	}
	
	public void onBulletMissed(BulletMissedEvent e) {
		reward = 0;
	}
	
	// Reused from git
	// Transform an exact position to an approximated one
	public int simplify_loc (double pos) {
		int simple_pos = 0;
		if((pos > 0) && (pos <= 100)){
			simple_pos = 0;
		}
		else if((pos > 100) && (pos <= 200)){
			simple_pos = 1;
		}
		else if((pos > 200) && (pos <= 300)){
			simple_pos = 2;
		}
		else if((pos > 300) && (pos <= 400)){
			simple_pos = 3;
		}
		else if((pos > 400) && (pos <= 500)){
			simple_pos = 4;
		}
		else if((pos > 500) && (pos <= 600)){
			simple_pos = 5;
		}
		else if((pos > 600) && (pos <= 700)){
			simple_pos = 6;
		}
		else if((pos > 700) && (pos <= 800)){
			simple_pos = 7;
		}
		return simple_pos;
	}
	
	// Reused from git
	// Same principle used previously but for the distance of the ennemy
	public int simplify_dist(double distance) {
		int qdistancetoenemy = 0;
		if((distance > 0) && (distance <= 200)){
			qdistancetoenemy = 0;
		}
		else if((distance > 200) && (distance <= 400)){
			qdistancetoenemy = 1;
		}
		else if((distance > 400) && (distance <= 600)){
			qdistancetoenemy = 2;
		}
		else if((distance > 600) && (distance <= 800)){
			qdistancetoenemy = 3;
		}
		else if((distance > 800) && (distance <= 1000)){
			qdistancetoenemy = 4;
		}
		else if((distance > 1000) && (distance <= 1200)){
			qdistancetoenemy = 5;
		}
		else if((distance > 1200) && (distance <= 1400)){
			qdistancetoenemy = 6;
		}
		else if((distance > 1400) && (distance <= 1600)){
			qdistancetoenemy = 7;
		}
		return qdistancetoenemy;
	}
	
	// Still the same principle for the ennemy velocity
	public int simplify_vel(double velocity) {
		int envelocity = 0;
		if ((velocity > 0) && (velocity <= 1)){
			envelocity = 0;
		}
		else if ((velocity > 1) && (velocity <= 2)){
			envelocity = 1;
		}
		else if ((velocity > 2) && (velocity <= 3)){
			envelocity = 2;
		}
		else if ((velocity > 3) && (velocity <= 4)){
			envelocity = 3;
		}
		else if ((velocity > 4) && (velocity <= 5)){
			envelocity = 4;
		}
		else if ((velocity > 5) && (velocity <= 6)){
			envelocity = 5;
		}
		else if ((velocity > 6) && (velocity <= 7)){
			envelocity = 6;
		}
		else if ((velocity > 7) && (velocity <= 8)){
			envelocity = 7;
		}
		
		return envelocity;
	}
	
	// Still the same principle for our gun bearing
	public int simplify_bear (double bearing) {
		int enbearing = 0;
		if ((bearing > 0) && (bearing <= 45)){
			enbearing = 0;
		}
		else if ((bearing > 45) && (bearing <= 90)){
			enbearing = 1;
		}
		else if ((bearing > 90) && (bearing <= 135)){
			enbearing = 2;
		}
		else if ((bearing > 135) && (bearing <= 180)){
			enbearing = 3;
		}
		else if ((bearing < -180) && (bearing >= -135)){
			enbearing = 4;
		}
		else if ((bearing < -135) && (bearing <= -90)){
			enbearing = 5;
		}
		else if ((bearing < -90) && (bearing <= -45)){
			enbearing = 6;
		}
		else if ((bearing < -45) && (bearing <= 0)){
			enbearing = 7;
		}
		
		return enbearing;
	}
	
	// List of possible actions
	public void doAction(int x, ScannedRobotEvent e) {
		switch(x) {
			case 0:
				// Align the gun to the opponnent position
				bulletPower = 1;
				setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
				setFire(bulletPower);
				break;
			case 1:
				/*
				| From Robocode : RandomTargeting
				| http://robowiki.net/wiki/RandomTargeting
				*/
				double targetAngle = getHeadingRadians() + e.getBearingRadians();

				bulletPower = Math.max(0.1,Math.random() * 3.0);
				double escapeAngle = Math.asin(8 / Rules.getBulletSpeed(bulletPower));
				double randomAimOffset = -escapeAngle + Math.random() * 2 * escapeAngle;

				double headOnTargeting = targetAngle - getGunHeadingRadians();
				setTurnGunRightRadians(Utils.normalRelativeAngle(headOnTargeting + randomAimOffset));
				setFire(bulletPower);
				break;
			case 2:
				/*
				| From Robocode : Linear targeting
				| http://robowiki.net/wiki/Linear_Targeting
				*/
				bulletPower = 3;
    			double headOnBearing = getHeadingRadians() + e.getBearingRadians();
    			double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
    			setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
    			setFire(bulletPower);
				break;
		}
	}
	
	// Method producing a unique int representing our current state
	// Data used : Enemy location (x,y), enemy velocity, my gun bearing, the distance to the ennemy
	public int makeState (ScannedRobotEvent e) {
		
		// From git
		// Used to approximate the absolute position of the enemy using our position
		double angle = Math.toRadians((getHeading() + e.getBearing() % 360));
		double enemyX = (getX() + Math.sin(angle) * e.getDistance());
		double enemyY = (getY() + Math.cos(angle) * e.getDistance());
		int enemy_x = simplify_dist(enemyX); //enemy x-position 
		int enemy_y = simplify_dist(enemyY); //enemy y-position

		// Multiply each data by a different multiple of ten
		// This permit to obtain a unique int for each possible state
		int state;
		state = simplify_loc(enemy_x) * 1;
		state += simplify_loc(enemy_y) * 10;
		state += simplify_vel(e.getVelocity()) * 100;
		state += simplify_bear(e.getBearing()) * 1000;
		state += simplify_dist(e.getDistance()) * 10000;
		
		return state;
	}
	
	/*
	|	From git save()
	|	Saves the look up table generated at the end of the round
	|	It erases the previous one	
	*/
	public void onRoundEnded(RoundEndedEvent e) {
		
		System.out.println("Saving the look up table created");
		PrintStream pStream = null;
		try {
			pStream = new PrintStream(new RobocodeFileOutputStream(getDataFile("LookUpTable.txt")));
			for (int state : statesArray) {
				pStream.printf("%d   %d   %d   %d%n",state, qMatrix[statesArray.indexOf(state)][0], qMatrix[statesArray.indexOf(state)][1], qMatrix[statesArray.indexOf(state)][2]);
			}
		} catch (IOException ioE) {
			ioE.printStackTrace();
		} finally {
			pStream.flush();
			pStream.close();
		}
	}
	
	/*
	|	From git load()
	|	Loads the data saved in the look up table
	*/
	public void load() throws IOException {

		if (getDataFile("LookUpTable.txt").exists()) {
			System.out.println("Loading the previous look up table");
			BufferedReader bReader = new BufferedReader(new FileReader(getDataFile("LookUpTable.txt")));
			String line = bReader.readLine();
			try {
    	    	while (line != null) {
    	    		String[] entry = line.split("   ");
					statesArray.add(Integer.parseInt(entry[0]));
					qMatrix[statesArray.indexOf(Integer.parseInt(entry[0]))][0] = Integer.parseInt(entry[1]);
					qMatrix[statesArray.indexOf(Integer.parseInt(entry[0]))][1] = Integer.parseInt(entry[2]);
					qMatrix[statesArray.indexOf(Integer.parseInt(entry[0]))][2] = Integer.parseInt(entry[3]);
					
    	    		line= bReader.readLine();
    	    	}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				bReader.close();
			}
		}
	}
}
