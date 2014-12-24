package pl.bmaraszek;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public class Goal implements GameObject {
	
	private Image image;
	private Rectangle rectangle;
	private Component rink;

	public Goal(Image image, Point position, Component rink){
		this.image = image;
		this.rectangle = new Rectangle(position.x, position.y, image.getWidth(rink), image.getHeight(rink));
		this.rink = rink;
	}

	@Override
	public void drawImage(Graphics g) {
		g.drawImage(image, getRectangle().x, getRectangle().y, rink);
	}

	@Override
	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	@Override
	public Rectangle getRectangle() {
		return rectangle;
	}

}
