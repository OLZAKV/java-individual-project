package MyGame2;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
 
public class MyGame2 extends JPanel
{
	private static final long serialVersionUID = -3014279135641202439L;
	private ArrayList<ColoredShape> coloredShapes = new ArrayList<ColoredShape>();
	private int WindowWidth = 800;
	private int WindowHight = 600;
	
	private int BrickWidth = 40;
	private int BrickHight = 40;
	private int BricksSparsity = 2;
	private int DensityLevel = 50;// %
	private int Scores = 0;
	
	private static Timer timer = new Timer();
	
	// Instance variables that define the current characteristics
	// of ball object.
	final int BALL_SIZE = 20;
	int ballX = (int) WindowWidth/2;
	int ballY = (int) WindowHight - BALL_SIZE * 4;

	// Ball's speed for x and y
	private float ballSpeedX = 1;
	private float ballSpeedY = -3;

	private static final int UPDATE_RATE = 6;
 
    public MyGame2()
    {
    	MouseMotionListener dl = new MouseMotionListener();
        addMouseMotionListener(dl);
    }
    
    private void CreateLevel() 
    {
    	int HStep = BrickWidth+BricksSparsity;
    	int VStep = BrickHight+BricksSparsity;
    	int HBricks = (int) (WindowWidth - BricksSparsity) / HStep;
    	int VBricks = (int) WindowHight / 2 / VStep;// 50% of top part
    	int HOffset = (int) (WindowWidth - HBricks * HStep + BricksSparsity)/2;
    	
    	// create instance of Random class
        Random rand = new Random();
        	
    	for (int _h = 0; _h < HBricks; _h++) {
    		for (int _v = 0; _v < VBricks; _v++) {
    			if (rand.nextInt(100) < DensityLevel) {
    				this.addShape(new Rectangle2D.Double(HOffset + _h * HStep, BricksSparsity + _v * VStep , BrickWidth, BrickHight), Color.GREEN);
    			}
    		}
    	}
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
 
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        for (ColoredShape cs : coloredShapes)
        {
            g2.setColor( cs.getForeground() );
            g2.fill( cs.getShape() );
        }
        
		// Draw the ball at the current ballX, ballY position
		g.setColor(Color.ORANGE);
		g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

    }
 
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(WindowWidth, WindowHight);
    }
 
    public void addShape(Shape shape, Color color)
    {
        // Convert the Shape to a GeneralPath so the Shape can be translated
        // to a new location when dragged
        ColoredShape cs = new ColoredShape(color, new GeneralPath(shape));
        coloredShapes.add( cs );
        repaint();
    }
 
    class ColoredShape
    {
        private Color foreground;
        private GeneralPath shape;
 
        public ColoredShape(Color foreground, GeneralPath shape)
        {
            this.foreground = foreground;
            this.shape = shape;
        }
 
        public Color getForeground()
        {
            return foreground;
        }
 
        public void setForeground(Color foreground)
        {
            this.foreground = foreground;
        }
 
        public GeneralPath getShape()
        {
            return shape;
        }
    }
    
    class MouseMotionListener extends MouseAdapter
    {
        private Point pressed = new Point((int) WindowWidth/2, 0 );;
        
        @Override
        public void mouseMoved(MouseEvent e) {
        	ColoredShape stick = coloredShapes.get(0);
            if (stick != null)
            {
                int deltaX = e.getX() - pressed.x;
                int deltaY = 0;
 
                stick.getShape().transform(AffineTransform.getTranslateInstance(deltaX, deltaY));
 
                pressed = e.getPoint();
            }
         }
    }
 
    private static void createAndShowGUI()
    {
    	MyGame2 MyGame2 = new MyGame2();
 
        // adding stick
        MyGame2.addShape(new Rectangle2D.Double(MyGame2.WindowWidth / 2 - MyGame2.BALL_SIZE * 4, MyGame2.WindowHight - MyGame2.BALL_SIZE * 3, MyGame2.BALL_SIZE * 8, MyGame2.BALL_SIZE), Color.CYAN);
        // creating level
        MyGame2.CreateLevel();
 
        JFrame frame = new JFrame("My first Java game");
        
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        frame.getContentPane().setCursor(blankCursor);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(MyGame2);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        
        // Helper class extends TimerTask
        TimerTask task = MyGame2.new Helper();        
		// Launch animation
        timer.schedule(task, UPDATE_RATE, UPDATE_RATE);
    }

	public void playAnimation() 
	{
		// Calculate the ball's new position
		int newX = (int) (ballX + ballSpeedX);
	    int newY = (int) (ballY + ballSpeedY);
	    boolean collided = false;

		for (int i = coloredShapes.size() - 1; i >= 0; i--)
		{
			ColoredShape cs = coloredShapes.get(i);
		    if (cs.getShape().contains( newX, newY ))
		    {
		        collided = true;

		    	if(i > 0)// collided with a brick 
		    	{
		    		//	      a
		            //  X1Y1------X2Y2
		            //   |         |
		            // d |         | b
		    		//   |         |
		    		//  X4Y4------X3Y3
		            //        c
		    		
		    		int brickX1 = cs.getShape().getBounds().x;
		            int brickY1 = cs.getShape().getBounds().y;
		            int brickX2 = brickX1 + BrickWidth;
		            int brickY2 = brickY1;
		            int brickX3 = brickX2;
		            int brickY3 = brickY1 + BrickHight;
		            int brickX4 = brickX1;
		            int brickY4 = brickY3;
		            
		            Line2D BallTrajectory = new Line2D.Double(ballX, ballY, newX, newY);
		            
		            // if ball trajectory cross a or c
		            if (BallTrajectory.intersectsLine(brickX1, brickY1, brickX2, brickY2) 
		            		|| BallTrajectory.intersectsLine(brickX3, brickY3, brickX4, brickY4))
		            {
		              ballSpeedY = -ballSpeedY;
		            }
		            
		            // if ball trajectory cross b or d
		            if (BallTrajectory.intersectsLine(brickX2, brickY2, brickX3, brickY3) 
		            		|| BallTrajectory.intersectsLine(brickX1, brickY1, brickX4, brickY4))
		            {
		              ballSpeedX = -ballSpeedX;
		            }
		    		
		    		coloredShapes.remove(i);
		    		Scores += 1;
		    		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		    		topFrame.setTitle("Score: " + Scores);
		    		break;
		  	  	}
		  	  	else// collided with a stick  
		  	  	{
		  	  		ballSpeedY = -ballSpeedY;
		  	  		if(cs.getShape().getBounds().getCenterX() + BALL_SIZE + BALL_SIZE < newX
		  	  				|| cs.getShape().getBounds().getCenterX() - BALL_SIZE - BALL_SIZE > newX)
		  	  		{
		  	  			ballSpeedX = (int) (newX - cs.getShape().getBounds().getCenterX())/BALL_SIZE; 
		  	  		}
		  	  	}
		    	if (coloredShapes.size() == 1)
		    	{
		    		this.CreateLevel();
		    		ballSpeedY *= 1.1;
		    		ballSpeedX *= 1.1;
		    	}
		    	break;
		    }
		}
		
		if (!collided)
		{
		    ballX = newX;
		    ballY = newY;
		}
	        
		// Check the new position to see if a bounce occurs;
		// If the bounce occurs, the moving direction changes
		if (ballX < 0 || ballX + BALL_SIZE > WindowWidth) 
		{
			ballSpeedX = -ballSpeedX;
		}
		if (ballY < 0 || ballY + BALL_SIZE > WindowHight) 
		{
			ballSpeedY = -ballSpeedY;
		}
		// If the ball reaches the bottom, it stops
		if (ballY + BALL_SIZE > WindowHight) 
		{
			ballSpeedY = 0;
			ballSpeedX = 0;
		}
		// Use repaint() to draw the ball in the new location
		repaint();
	}
	
	class Helper extends TimerTask 
	{
	    public void run() 
	    {
	    	playAnimation();
	    }
	}
	
    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater( () -> createAndShowGUI() );
    }
}

