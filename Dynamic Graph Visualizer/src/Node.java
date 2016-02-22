import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Node {
	private static final int RADIUS = 10;
	private static final double SPEED_MULTIPLIER = 5.0;
	private static final double SPEED_CONNECTED = 0.005;
	private static final double OPTIMAL_CONNECTED_DISTANCE = 40;

	private ArrayList<Node> connections;

	private Vector2D position;
	private Vector2D velocity;

	public Node(Vector2D pos) {
		position = pos;
		velocity = new Vector2D();
		connections = new ArrayList<Node>();
	}

	public void calculateVelocity(Rectangle bounds,
			ArrayList<Node> otherNodes) {
		Vector2D newVelocity = new Vector2D();

		double leftWall = position.getX() - bounds.x;
		double rightWall = position.getX() - bounds.x - bounds.width;
		double topWall = position.getY() - bounds.y;
		double bottomWall = position.getY() - bounds.y - bounds.height;

		newVelocity.addToThis(new Vector2D(
				1 / (leftWall * leftWall) - 1 / (rightWall * rightWall),
				1 / (topWall * topWall) - 1 / (bottomWall * bottomWall)));

		for (int n = 0; n < otherNodes.size(); n++) {
			if (otherNodes.get(n) != this && otherNodes.get(n) != null) {
				Vector2D difference = position
						.subtract(otherNodes.get(n).position);
				if (!connections.contains(otherNodes.get(n))) {
					double dist = difference.getLength();
					difference.normalize();
					difference.multiplyBy(1.0 / (dist * dist));
					difference.multiplyBy(SPEED_MULTIPLIER);
				} else {
					difference = difference.getNormalized().multiply(OPTIMAL_CONNECTED_DISTANCE).subtract(difference);
					difference.normalize();
					difference.multiplyBy(SPEED_CONNECTED);
				}
				newVelocity.addToThis(difference);
			}
		}

		velocity = newVelocity.multiply(SPEED_MULTIPLIER);
	}

	public void update() {
		position.addToThis(velocity);
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval((int) position.getX() - RADIUS,
				(int) position.getY() - RADIUS, RADIUS * 2, RADIUS * 2);
		for (int n = 0; n < connections.size(); n++) {
			g.setColor(Color.WHITE);
			g.drawLine((int) position.getX(), (int) position.getY(),
					(int) connections.get(n).position.getX(),
					(int) connections.get(n).position.getY());
		}
	}

	public boolean contains(Point point) {
		return position.subtract(new Vector2D(point)).getLength() < RADIUS;
	}

	public void addConnection(Node node) {
		connections.add(node);
	}

	public void removeConnection(Node node) {
		if (connections.contains(node)) {
			connections.remove(node);
		}
	}

	public boolean hasConnection(Node node) {
		return connections.contains(node);
	}
	
	public int numConnections() {
		return connections.size();
	}

	public Node getConnection(int b) {
		return connections.get(b);
	}
}