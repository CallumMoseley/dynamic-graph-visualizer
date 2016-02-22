import javax.swing.JFrame;

public class DGVFrame extends JFrame
{
	DGVPanel p;
	
	public DGVFrame()
	{
		super("Dynamic Graph Visualizer");
		p = new DGVPanel(); 
		
		setContentPane(p);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args)
	{
		DGVFrame frame = new DGVFrame();
		frame.pack();
		frame.setVisible(true);
	}
}