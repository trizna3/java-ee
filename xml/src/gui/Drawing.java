package gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class Drawing extends Canvas {

	private static final long serialVersionUID = -6417679864621884063L;
	private List<Line2D> lines = new ArrayList<Line2D>();
	
	private int width = 700;
	private int height = 700;


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (Line2D line : lines) {
        	g.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
        }
    }
    
    public void addLine(int x1, int y1, int x2, int y2) {
    	lines.add(new Line2D.Double(x1,y1,x2,y2));
    }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}