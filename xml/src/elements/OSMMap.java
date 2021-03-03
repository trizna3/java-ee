package elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSMMap {

	private double minX; // longitude
	private double minY; // latitude
	private double maxX;
	private double maxY;
	
	private Map<Long,OSMNode> nodes;
	private List<OSMWay> ways;
	
	public OSMMap(double minX, double minY, double maxX, double maxY, List<OSMNode> nodes, List<OSMWay> ways) {
		this.setMinX(minX);
		this.setMinY(minY);
		this.setMaxX(maxX);
		this.setMaxY(maxY);
		this.setWays(ways);
		for (OSMNode node : nodes) {
			getNodes().put(node.getId(), node);
		}
	}

	public OSMNode getNodeById(Long nodeId) {
		return getNodes().get(nodeId);
	}

	public List<OSMWay> getWays() {
		if (ways == null) {
			ways = new ArrayList<OSMWay>();
		}
		return ways;
	}
	
	public void setWays(List<OSMWay> ways) {
		this.ways = ways;
	}

	public Map<Long,OSMNode> getNodes() {
		if (nodes == null) {
			nodes = new HashMap<Long, OSMNode>();
		}
		return nodes;
	}

	public void setNodes(Map<Long,OSMNode> nodes) {
		this.nodes = nodes;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}
}
