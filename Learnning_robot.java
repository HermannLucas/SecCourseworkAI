package 2ndCourseworkAI;import robocode.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.ListIterator;

public class Learnning_robot extends AdvancedRobot
{
	public boolean explore = true;
	public boolean greedy = false;
	public final double alpha = 0.1;
	public int[][] qMatrix = new int[8*8*8*8*8][3];
	public ArrayList<Integer> statesArray = new ArrayList<Integer>();
	public int reward = 0;
	public Random random = new Random();
	
	public void run() {
		for (int a = 0; a < 8; a++) {
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
		}
		setTurnRadarRight(999999);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		int rounds = 0;
		if (explore) {
			int action = random.nextInt(3);
			doAction(action, e);
			Bullet bullet = fireBullet(1.5);
			int state = makeState(e);
			
			// waits for the bullet to hit
			while(bullet.isActive()) {
				doNothing();
			}

			if (rounds < 500) {
				rounds++;
			} else {
				explore = false;
				greedy = true;
			}
			
			qMatrix[state][action] += reward;
		}
		
		if (greedy) {
			int state = makeState(e);
			int max = 0;
			int nextAction = random.nextInt(3);
			for (int i = 0; i < 3; i++) {
				if (qMatrix[state][i] > max) {
					max = qMatrix[state][i];
					nextAction = i;
				}
			}
			doAction(nextAction, e);
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
	
	//Reused from git
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
	
	//Reused from git
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
	
	public void doAction(int x, ScannedRobotEvent e) {
		switch(x) {
			case 0:
				// Align the gun to the opponnent position
				setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
				break;
			case 1:
				// Align the gun to the opponnent position + 10 degrees
				setTurnGunRight(getHeading() - getGunHeading() + e.getBearing() + 10);
				break;
			case 2:
				// Align the gun to the opponnent position - 10 degrees
				setTurnGunRight(getHeading() - getGunHeading() + e.getBearing() - 10);
				break;
		}
	}
	
	public int makeState (ScannedRobotEvent e) {
		
		//from git
		
		double angle = Math.toRadians((getHeading() + e.getBearing() % 360));
		double enemyX = (getX() + Math.sin(angle) * e.getDistance());
		double enemyY = (getY() + Math.cos(angle) * e.getDistance());
		int enemy_x = simplify_dist(enemyX); //enemy x-position 
		int enemy_y = simplify_dist(enemyY); //enemy y-position

		int state;
		state = simplify_loc(enemy_x) * 1;
		state += simplify_loc(enemy_y) * 10;
		state += simplify_vel(e.getVelocity()) * 100;
		state += simplify_bear(e.getBearing()) * 1000;
		state += simplify_dist(e.getDistance()) * 10000;
		
		return state;
	}
}
