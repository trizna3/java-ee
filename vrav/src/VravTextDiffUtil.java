package src;

/**
 * Computes difference in old and text to determine what changed.
 * 
 * @author Adam Trizna
 *
 */
public class VravTextDiffUtil {

	/**
	 * Computes old/new text difference.
	 * Returns {@link VravTextTransport} object with header ADD, REMOVE or REPLACE. 
	 * 
	 */
	public static VravTextTransport prepareTextTransport(String oldText, String newText) {
		if (oldText.length() == 0 && newText.length() > 0) {
			// add all
			return new VravTextTransport(VravHeader.HEADER_ADD_TEXT, 0, -1, newText);
		}
		if (newText.length() == 0 && oldText.length() > 0) {
			// remove all
			return new VravTextTransport(VravHeader.HEADER_REMOVE_TEXT, 0, oldText.length(), "");
		}
		// compute diff
		boolean firstCharMatches = oldText.charAt(0) == newText.charAt(0);
		boolean lastCharMatches = oldText.charAt(oldText.length()-1) == newText.charAt(newText.length()-1);
		
		if (!firstCharMatches && !lastCharMatches) {
			// whole text was replaced
			return new VravTextTransport(VravHeader.HEADER_REPLACE_TEXT, 0, oldText.length(), newText);
		}
		else if (!firstCharMatches && lastCharMatches) {
			// search for diff from end
			int oldIdx = oldText.length()-1;
			int newIdx = newText.length()-1;
			while (newIdx > 0 && oldIdx > 0 && newText.charAt(newIdx) == oldText.charAt(oldIdx)) {
				oldIdx --;
				newIdx --;
			}
			if (newIdx == 0) {
				// text was removed from beginning
				return new VravTextTransport(VravHeader.HEADER_REMOVE_TEXT, 0, oldIdx, "");
			} else if (oldIdx == 0) {
				// text was added from beginning
				return new VravTextTransport(VravHeader.HEADER_ADD_TEXT, 0, -1, newText.substring(0,newIdx));
			} else {
				// text was replaced at beginning
				return new VravTextTransport(VravHeader.HEADER_REPLACE_TEXT, 0, oldIdx, newText.substring(0,newIdx+1));
			}
		}
		else if (firstCharMatches && !lastCharMatches) {
			// search for diff from beginning
			int oldIdx = 0;
			int newIdx = 0;
			while (newIdx < newText.length() && oldIdx < oldText.length() && newText.charAt(newIdx) == oldText.charAt(oldIdx)) {
				oldIdx ++;
				newIdx ++;
			}
			if (newIdx == newText.length()) {
				// text was removed from end
				return new VravTextTransport(VravHeader.HEADER_REMOVE_TEXT, newIdx, oldText.length(), "");
			} else if (oldIdx == oldText.length()) {
				// text was added to the end
				return new VravTextTransport(VravHeader.HEADER_ADD_TEXT, oldIdx, -1, newText.substring(oldIdx));
			} else {
				// text was replaced at the end
				return new VravTextTransport(VravHeader.HEADER_REPLACE_TEXT, oldIdx, oldText.length(), newText.substring(oldIdx));
			}
		}
		else {
			// search for diff from both beginning and end
			int oldLower = 0;
			int newLower = 0;
			int oldUpper = oldText.length()-1;
			int newUpper = newText.length()-1;
			while (newLower < newText.length() && oldLower < oldText.length() && newText.charAt(newLower) == oldText.charAt(oldLower)) {
				oldLower ++;
				newLower ++;
			}
			while (newUpper > 0 && oldUpper > 0 && newText.charAt(newUpper) == oldText.charAt(oldUpper)) {
				oldUpper --;
				newUpper --;
			}
			
			// block of text was replaced somewhere in the middle
			return new VravTextTransport(VravHeader.HEADER_REPLACE_TEXT, oldLower, oldUpper+1, newText.substring(newLower,newUpper+1));
		}
	}
}
