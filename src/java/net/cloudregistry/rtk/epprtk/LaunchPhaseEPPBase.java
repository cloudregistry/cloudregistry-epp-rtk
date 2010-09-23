package net.cloudregistry.rtk.epprtk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.xerces.parsers.DOMParser;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.tucows.oxrs.epprtk.rtk.xml.EPPXMLErrors;


public class LaunchPhaseEPPBase
{

	/**
	 * This utility method is meant to be used by subclasses of EPPXMLBase to 
	 * override its prepareExtensionElement method.
	 * This implementation passes the document to the extension's toDOM()
	 * method instead of fiddling with serializing and parsing it again,
	 * which, under some JDK / DOM implementation / unknown voodoo magic causes
	 * the namespace declarations to be lost.
	 * 
	 * Because we're trying to make minimal changes to the core epp rtk,
	 * we test for classes that implement the epp_SmartExtension interface
	 * (which provides the toDOM() method) and fall back to the original method
	 * for all other extension classes.
	 */
	public static void prepareExtensionElement(Document doc, Element command,
			epp_Extension[] extensions) throws epp_XMLException {
		if (extensions != null) {
			Element extension_element = doc.createElement("extension");
			for (int count = 0; count < extensions.length; count++) {
				epp_Extension extension = extensions[count];

				if (extension instanceof epp_SmartExtension) {
					Node extNode = ((LaunchPhaseExtension) extension).toDOM(doc);
					extension_element.appendChild(extNode);
					continue;
				}

				String extension_string = extension.toXML();

				if (extension_string != null && extension_string.length() != 0) {
					try {
						Node extension_node = getExtensionNode(extension_string);
						if (extension_node != null) {
							extension_node = doc.importNode(extension_node,
									true);
							extension_element.appendChild(extension_node);
						} else {
							// if the extension node was null, then it wasn't
							// valid XML or
							// it is just a string (eg "hellothere") and this
							// can be included as a raw string in the extension
							// tag.
							extension_element.appendChild(doc
									.createTextNode(extension_string));
						}
					} catch (Exception xcp) {
						throw new epp_XMLException("error in extension XML ["
								+ xcp.getClass().getName() + "] ["
								+ xcp.getMessage() + "]");
					}
				}
			}
			command.appendChild(extension_element);
		}
	}

	
	
    protected static Node getExtensionNode(String extension_string) throws IOException, SAXException
    {
        DOMParser parser = new DOMParser();
        EPPXMLErrors errors = new EPPXMLErrors();
        parser.setErrorHandler(errors);
        parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
        parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
        parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);
        parser.setFeature("http://xml.org/sax/features/namespaces", true);

        parser.parse(new InputSource(new ByteArrayInputStream(extension_string.getBytes())));
        Document document = parser.getDocument();

        if (!document.isSupported("Traversal", "2.0")) throw new RuntimeException("This DOM Document does not support Traversal");

        Node extension_node = (Node) document.getDocumentElement();
        return extension_node;
    }


	
	/**
	 * 
	 * @return the DOM Element corresponding to the EPP response extension
	 * @throws epp_XMLException
	 */
	public static Element getExtension(Element eppEl) throws epp_XMLException
	{
        Node response_node = eppEl.getElementsByTagName("response").item(0);
        NodeList extension_nodes = ((Element)response_node).getElementsByTagName("extension");
        if ( extension_nodes.getLength() == 0 ) {
            return null;
        }
        else {
            return (Element)extension_nodes.item(0);
        }
	}

	
	public static LaunchPhaseExtension getLaunchPhaseExtension(Element extension, String containerTag) throws epp_XMLException {
		if (extension == null) {
			return null;
		}
		LaunchPhaseExtension launchphaseResponse = new LaunchPhaseExtension(containerTag);
		launchphaseResponse.fromDOM(extension);
		return launchphaseResponse;
	}


}
