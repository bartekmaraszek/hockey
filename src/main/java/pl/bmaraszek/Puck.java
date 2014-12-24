package pl.bmaraszek;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import pl.bmaraszek.Hockey.puckType;

public class Puck implements GameObject, Movable {

	private Component rink;
	private Image image;
	private int maxVelocity;
	private Random random;
	private Point location, velocity;
	private Rectangle rectangle;

	public Puck(Image image, puckType type, int maxVelocity, Component rink) {
		this.rink = rink;
		this.image = image;
		this.maxVelocity = maxVelocity;

		random = new Random(System.currentTimeMillis());

		if (type == puckType.NORMAL) {
			location = new Point(300 + (Math.abs(random.nextInt()) % 300),
					200 + (Math.abs(100 + random.nextInt()) % 100));

			this.setVelocity(new Point(random.nextInt() % maxVelocity, random.nextInt() % maxVelocity));

			while (velocity.x == 0) {
				velocity.x = random.nextInt(maxVelocity / 2) - maxVelocity / 2;
			}

		} else if (type == puckType.PLAYER) {
			location = new Point(10, ((Hockey) rink).backgroundImage.getHeight(rink) / 2);
			this.velocity = new Point(0, 0);

		} else if (type == puckType.COMPUTER) {
			location = new Point(((Hockey) rink).backgroundImage.getWidth(rink) - 70, // TODO: -70 WTF?!
					((Hockey) rink).backgroundImage.getHeight(rink) / 2);
			this.velocity = new Point(0, 0);

		}

		this.setRectangle(new Rectangle(location.x, location.y, image.getWidth(rink), image.getHeight(rink)));

	}

	public void move(int y) {
		rectangle.y = y;
	}
	
	public void move(Point point){
		rectangle.translate(point.x, point.y);
	}

	public void move() {
		Point position = new Point(rectangle.x, rectangle.y);

		if (random.nextInt(100) <= 1) {
			velocity.x += random.nextInt() % maxVelocity;

			velocity.x = Math.min(velocity.x, maxVelocity);
			velocity.x = Math.max(velocity.x, -maxVelocity);

			while (velocity.x == 0) {
				velocity.x = random.nextInt(maxVelocity / 2) - maxVelocity / 2;
			}

			velocity.y += random.nextInt() % maxVelocity / 2;

			velocity.y = Math.min(velocity.y, maxVelocity / 2);
			velocity.y = Math.max(velocity.y, -(maxVelocity / 2));
		}

		position.x += velocity.x;
		position.y += velocity.y;

		this.rectangle = new Rectangle(position.x, position.y, image.getWidth(rink), image.getHeight(rink));
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setVelocity(Point velocity) {
		if(Math.abs(velocity.x) <= maxVelocity && Math.abs(velocity.y) <= maxVelocity)
		this.velocity = velocity;
	}

	public void reverseXVelocity() {
		this.velocity.x = -this.velocity.x;
	}

	public void reverseYVelocity() {
		this.velocity.y = -this.velocity.y;
	}

	public void reverseVelocity() {
		reverseXVelocity();
		reverseYVelocity();
	}

	public Point getVelocity() {
		return velocity;
	}

	@Override
	public void drawImage(Graphics g) {
		g.drawImage(image, getRectangle().x, getRectangle().y, rink);
	}

	@Override
	public void setXVelocity(int xVel) {
		if (Math.abs(velocity.x) <= maxVelocity) {
			velocity.x = xVel;
		}
	}

	@Override
	public void setYVelocity(int yVel) {
		if (Math.abs(velocity.y) <= maxVelocity) {
			velocity.y = yVel;
		}
	}

	@Override
	public int getXVelocity() {
		return velocity.x;
	}

	@Override
	public int getYVelocity() {
		return velocity.y;
	}
}
