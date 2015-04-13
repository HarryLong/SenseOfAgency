import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class ClockPanel extends JPanel {
	
	public static final int _ROTATION_TIME = 2560;
	public static final double _TWO_PI = 2 * Math.PI;
	public static final double _HALF_PI = Math.PI/2;
	public static final int _HALF_TICK_LENGTH = 5;
	public static final int _TICK_TO_TEXT_PADDING = 10;
	public static final int _TICK_COUNT = 60;
	
	long _clock_hand_position;
	
	private ClockHandler _clockHandler;
	private Thread _clockHandlerThread;
	
	private Color _clockFaceColor;
	
	public ClockPanel() {
		_clockFaceColor = null;
		_clockHandler = new ClockHandler(this);
		_clockHandlerThread = new Thread(_clockHandler);
		_clockHandlerThread.start();
		reset();
	}
	
	public void startClock()
	{
		_clockHandler.start();
	}

	public void stopClock()
	{
		_clockHandler.stop();
	}
	
	public void reset()
	{
		setClockHandlePosition(0);
		_clockFaceColor = null;
		repaint();
	}
	
	private double timeToRadians(long clock_hand_position)
	{
		return (((double) (clock_hand_position%_ROTATION_TIME) / _ROTATION_TIME) * _TWO_PI);
	}
	
	protected void paintComponent(Graphics g)
	{
        super.paintComponent(g);
        
        Font font = g.getFont().deriveFont(Font.BOLD);
        
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.setFont(font);
        
		int width = getWidth();
		int height = getHeight();
		int center_x = width/2;
		int center_y = height/2;
		
		// Calculate padding
		int text_padding = g2d.getFontMetrics().stringWidth("60");
		int clock_padding = text_padding + _TICK_TO_TEXT_PADDING + _HALF_TICK_LENGTH;
		
		int radius = (Math.min(width, height)-(2*(_HALF_TICK_LENGTH+clock_padding))) / 2;
		
		int centering_x_padding = center_x - radius - clock_padding;
		int centering_y_padding = center_y - radius - clock_padding;
		
		if(_clockFaceColor == null)
			g2d.drawOval(centering_x_padding + clock_padding, centering_y_padding + clock_padding, 2*radius, 2*radius);
		else
		{
			g2d.setColor(_clockFaceColor);
			g2d.fillOval(centering_x_padding + clock_padding, centering_y_padding + clock_padding, 2*radius, 2*radius);
			g2d.setColor(Color.BLACK);
		}
		
		// Draw the ticks on the clock
	    
		double increments = _TWO_PI/_TICK_COUNT;
		int tick_idx = 0;
		int base_tick_start_x = radius - _HALF_TICK_LENGTH;
		int base_tick_end_x = base_tick_start_x + (2*_HALF_TICK_LENGTH);
		int base_tick_y	= 0;
		int x_offset = centering_x_padding + clock_padding + radius;
		int y_offset  = centering_y_padding + clock_padding + radius;

	    AffineTransform transform = new AffineTransform();
		for(double angle = -_HALF_PI; angle < 3 * _HALF_PI; angle += increments, tick_idx++)
		{
			transform.setToIdentity();
			transform.translate(x_offset, y_offset);
			transform.rotate(angle);
						
			g2d.setTransform(transform);
			g2d.drawLine(base_tick_start_x, base_tick_y, base_tick_end_x, base_tick_y);
			
			transform.setToIdentity();
			transform.translate(x_offset, y_offset);
			transform.rotate(angle + _HALF_PI);
//
			// Draw the tick numbers
			String tick_idx_str = Integer.toString(tick_idx);
			int text_width = g2d.getFontMetrics().stringWidth(tick_idx_str);
			g2d.setTransform(transform);
			g2d.drawString(Integer.toString(tick_idx), -text_width/2, -radius-_HALF_TICK_LENGTH-_TICK_TO_TEXT_PADDING);
		}
		
		// Draw the clock hand position
		int clock_hand_start_x = 0;
		int clock_hand_end_x = radius;
		int clock_hand_y = 0;
		
		transform.setToIdentity();
		transform.translate(x_offset, y_offset);
		transform.rotate((timeToRadians(_clock_hand_position) - _HALF_PI));
		g2d.setTransform(transform);
		g2d.setStroke(new BasicStroke(5));
		g2d.drawLine(clock_hand_start_x, clock_hand_y, clock_hand_end_x, clock_hand_y);		
	}
	
	public void setClockHandlePosition(int position)
	{
		_clock_hand_position = position;
		repaint();
	}
	
	public long getClockHandPosition()
	{
		return _clock_hand_position;
	}
	
	public void setClockFaceColor(Color color)
	{
		_clockFaceColor = color;
	}

	public static class ClockHandler implements Runnable
	{
		private static final int _INTERVALS = 5; //ms
		private ClockPanel _callback;
		long _start_time;		
		private boolean _paused;
		
		public ClockHandler(ClockPanel clockPanel)
		{
			_callback = clockPanel;
			_paused = true;
		}
		
		public void stop()
		{
			_paused = true;
		}

		public void start()
		{
			restart();
		}
		
		public void restart()
		{
			_start_time = System.currentTimeMillis();
			_paused = false;
		}
		
		@Override
		public void run() {
			while(true)
			{
				if(!_paused)
				{
					long elapsed_time = System.currentTimeMillis() - _start_time;

					_callback.setClockHandlePosition((int) (elapsed_time%_ROTATION_TIME));
				}
				try {
					Thread.sleep(_INTERVALS);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
