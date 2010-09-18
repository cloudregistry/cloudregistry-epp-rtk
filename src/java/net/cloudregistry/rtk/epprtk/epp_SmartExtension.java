/**
 * 
 */
package net.cloudregistry.rtk.epprtk;

import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface adds the fromDOM and toDOM methods.
 * 
 * @author wil
 *
 */
public interface epp_SmartExtension extends epp_Extension {
	/**
	 * Serializes the internal representation of this object into a DOM Node
	 * @param doc DOM Document to use for creating nodes
	 * @return
	 * @throws epp_XMLException
	 */
	public Node toDOM(Document doc) throws epp_XMLException;
	
	/**
	 * Deserializes an XML representation and populate the internal state of this object. 
	 * @param el the EPP extension element
	 * @throws epp_XMLException
	 */
	public void fromDOM(Element el) throws epp_XMLException;
}
