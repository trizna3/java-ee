package gui;

import javax.swing.JFrame;

import elements.OSMMap;
import parser.Parser;

public class Main {
	
	public static void main(String[] args) {
		
        Drawing d = new Drawing();
        JFrame frame = new JFrame("OSM map");
        frame.add(d);
        
        frame.pack();
        frame.setVisible(true);
        
        OSMMap map = Parser.getInstance().parseMap();
        MapPrinter.getInstance().printMap(map, d);
	}
}