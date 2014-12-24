package pl.bmaraszek;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface GameObject {
	
	public void drawImage(Graphics g);

	public void setRectangle(Rectangle rectangle);

	public Rectangle getRectangle();
	
}
