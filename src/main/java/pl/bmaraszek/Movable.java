package pl.bmaraszek;

import java.awt.Point;

public interface Movable {
	
	public void move();
	public void move(int y);
	public void move(Point point);
	
	public void setVelocity(Point velocity);
	public void setXVelocity(int xVel);
	public void setYVelocity(int yVel);
	
	public Point getVelocity();
	public int getXVelocity();
	public int getYVelocity();
	
	public void reverseVelocity();
	public void reverseXVelocity();
	public void reverseYVelocity();
	
}
