package elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OSMWay extends OSMElement {

	private List<Long> nodeIds;
	private List<OSMTag> tags;
	
	public static final String ELEMENT_NAME = "way";
	
	public static final String NODE = "nd";
	public static final String NODE_REF = "ref";
	public static final String TAG = "tag";
	public static final String TAG_K = "k";
	public static final String TAG_V = "v";
	
	public OSMWay(Node xmlNodeRaw) {
		super(xmlNodeRaw);
		try {
			Element nodeElement = (Element) xmlNodeRaw;
			
			NodeList nl = nodeElement.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (NODE.equals(nl.item(i).getNodeName())) {
					getNodes().add(parseNodeId((Element)nl.item(i)));
					continue;
				}
				if (TAG.equals(nl.item(i).getNodeName())) {
					getTags().add(parseTag((Element)nl.item(i)));
					continue;
				}
			}
			
		} catch (Exception e) {
			System.err.print("Error parsing " + this.getClass().toString());
			e.printStackTrace();
		}
	}
	
	public OSMWay(long id, int version, Date timestamp, int changeset, int uid, String user) {
		super(id, version, timestamp, changeset, uid, user);
	}
	
	private Long parseNodeId(Element nodeIdRaw) throws Exception {
		return Long.valueOf(nodeIdRaw.getAttributeNode(NODE_REF).getValue());
	}
	
	private OSMTag parseTag(Element tagRaw) {
		String k = tagRaw.getAttributeNode(TAG_K).getValue();
		String v = tagRaw.getAttributeNode(TAG_V).getValue();
		return new OSMTag(k,v);
	}
	
	public List<Long> getNodes() {
		if (nodeIds == null) {
			nodeIds = new ArrayList<Long>();
		}
		return nodeIds;
	}

	public void setNodes(List<Long> nodes) {
		this.nodeIds = nodes;
	}

	public List<OSMTag> getTags() {
		if (tags == null) {
			tags = new ArrayList<OSMTag>();
		}
		return tags;
	}

	public void setTags(List<OSMTag> tags) {
		this.tags = tags;
	}
}
