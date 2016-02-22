import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DGVPanel extends JPanel
		implements MouseListener, MouseMotionListener, KeyListener {
	
	private long lastUpdate;
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private boolean makingEdge = false;
	private Node startingNode;
	
	private boolean simulating = true;

	public DGVPanel() {
		lastUpdate = -1;
		
		setPreferredSize(new Dimension(1024, 768));

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
		
		new Thread() {
			public void run() {
				updateLoop();
			}
		}.start();
	}
	
	public void updateLoop() {
		for (;;) {
			if (simulating) {
				for (int n = 0; n < nodes.size(); n++) {
					if (nodes.get(n) != null) {
						nodes.get(n).calculateVelocity(this.getBounds(), nodes);
					}
				}
				for (int n = 0; n < nodes.size(); n++) {
					if (nodes.get(n) != null) {
						nodes.get(n).update();
					}
				}
			}
			
			if (lastUpdate != -1) {
				long diff = System.currentTimeMillis() - lastUpdate;
				try {
					Thread.sleep(Math.max(0, (1000 / 60) - diff));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				lastUpdate = System.currentTimeMillis();
			}
			repaint(0);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		for (int n = 0; n < nodes.size(); n++) {
			if (nodes.get(n) != null) {
				nodes.get(n).draw(g);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			simulating = !simulating;
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			makingEdge = false;
			nodes.clear();
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		boolean nodeClicked = false;
		for (int n = 0; n < nodes.size(); n++) {
			if (nodes.get(n).contains(e.getPoint())) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					nodeClicked = true;
					if (!makingEdge) {
						startingNode = nodes.get(n);
						makingEdge = true;
					} else {
						nodes.get(n).addConnection(startingNode);
						startingNode.addConnection(nodes.get(n));
						makingEdge = false;
						startingNode = null;
					}
					break;
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					nodeClicked = true;
					for (int i = 0; i < nodes.size(); i++) {
						nodes.get(i).removeConnection(nodes.get(n));
					}
					nodes.remove(n);
				}
			}
		}
		if (!nodeClicked) {
			nodes.add(new Node(new Vector2D(e.getPoint())));
			if (makingEdge) {
				nodes.get(nodes.size() - 1).addConnection(startingNode);
				startingNode.addConnection(nodes.get(nodes.size() - 1));
				makingEdge = false;
				startingNode = null;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}