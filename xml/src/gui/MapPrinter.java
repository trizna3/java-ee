package gui;

import elements.OSMMap;
import elements.OSMNode;
import elements.OSMWay;

public class MapPrinter {

	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	private Drawing drawing;
	
	private static final MapPrinter INSTANCE = new MapPrinter();

	public static MapPrinter getInstance() {
		return INSTANCE;
	}
	
	public void printMap(OSMMap map, Drawing drawing) {
		storeBounds(map);
		this.drawing = drawing;
		
		for (OSMWay way : map.getWays()) {
			for (int i = 0; i < way.getNodes().size() - 1; i++) {
				OSMNode node1 = map.getNodeById(way.getNodes().get(i));
				OSMNode node2 = map.getNodeById(way.getNodes().get(i+1));
				
				int x1 = transformX(node1.getLon());
				int y1 = transformY(node1.getLat());
				int x2 = transformX(node2.getLon());
				int y2 = transformY(node2.getLat());
				
				drawing.addLine(x1, y1, x2, y2);
			}
		}
		drawing.repaint();
	}
	
	private void storeBounds(OSMMap map) {
		minX = map.getMinX();
		maxX = map.getMaxX();
		minY = map.getMinY();
		maxY = map.getMaxY();
	}
	
	/**
	 * transforms x coordinate from OsmMap, to relative position in Drawing
	 * @param x
	 * @return
	 */
	private int transformX(double x) {
		double mapProportion = (x - minX) / (maxX-minX);
		return (int) (drawing.getWidth() * mapProportion);
	}
	
	/**
	 * transforms y coordinate from OsmMap, to relative position in Drawing
	 * @param y
	 * @return
	 */
	private int transformY(double y) {
		double mapProportion = (y - minY) / (maxY-minY);
		return (int) (drawing.getHeight() * mapProportion);
	}
}
