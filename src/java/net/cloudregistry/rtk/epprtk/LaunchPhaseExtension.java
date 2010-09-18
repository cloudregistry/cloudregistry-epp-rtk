package net.cloudregistry.rtk.epprtk;


import java.io.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import org.openrtk.idl.epprtk.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;

/**
 * This class encapsulates the Cloud Registry EPP extension for managing
 * the launch phases of a Top Level Domain (TLD), such as "sunrise" and "land rush". 
 * 
 * @see net.cloudregistry.rtk.epprtk.LaunchPhaseTest
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo
 * @see com.tucows.oxrs.epprtk.rtk.xml.EPPDomainUpdate
 */
public class LaunchPhaseExtension extends EPPXMLBase implements epp_SmartExtension
{

	public static final String NS_LAUNCHPHASE = "http://www.cloudregistry.net/ns/launchphase-1.0";
	public static final String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
	private String tag_;
    private String tm_name_;
    private String tm_number_;
    private String tm_locality_;
    private String tm_entitlement_;
    private String pvrc_;
    private String phase_;
    private String app_id_;

    
    /**
     * Deafult constructor
     * @param tag The tag name under which the extension elements appear under. Can be "create", "creData" (for create domain response), "info", "infData" (info domain response), or "update"
     */
    public LaunchPhaseExtension (String tag) {
    	tag_ = tag;
    }


    public void setApplicationID(String app_id) { app_id_ = app_id; }
    public void setTrademarkName(String value) { tm_name_ = value; }
    public void setPVRC(String value) { pvrc_ = value; }
    public void setPhase(String value) { phase_ = value; }
    public void setTrademarkLocality(String value) { tm_locality_ = value; }
    public void setTrademarkEntitlement(String value) { tm_entitlement_ = value; }
    public void setTrademarkNumber(String value) { tm_number_ = value; }
    
	public String getApplicationID() { return app_id_; }
    public String getTrademarkName() { return tm_name_; }
    public String getPVRC() { return pvrc_; }
    public String getPhase() { return phase_; }
    public String getTrademarkLocality() { return tm_locality_; }
    public String getTrademarkEntitlement() { return tm_entitlement_; }
    public String getTrademarkNumber() { return tm_number_; }

    
    /**
     * Converts the launch phase data into XML to be put into the extension
     * section of the request.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @throws org.openrtk.idl.epprtk.epp_XMLException if required data is missing
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "toXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");
        Document doc = new DocumentImpl();
        Node launchphase = toDOM(doc);
        doc.appendChild(launchphase);

        String launchphase_xml;
        try {
            launchphase_xml = createXMLSnippetFromDoc(doc);
        }
        catch (IOException xcp) {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");
        return launchphase_xml;
    }


    /**
     * Parses an XML String of launch phase data from the extension section of
     * a response from the Registry.
     * Implemented method from org.openrtk.idl.epprtk.epp_Extension interface.
     * @param A new trademark Unspec XML String to parse
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @see org.openrtk.idl.epprtk.epp_Extension
     */
    public void fromXML(String xml) throws epp_XMLException
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = xml;
        try
        {
            if ( xml_ == null ||
                 xml_.length() == 0 )
            {
                // no xml string to parse
                return;
            }

            Element extension_node = getDocumentElement();
            if ( extension_node == null ) {
                throw new epp_XMLException("unparsable or missing extension");
            }
            fromDOM(extension_node);
        }
        catch (SAXException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }
        catch (IOException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }
    }
    

    /**
     * Extract launch phase data from the given extension element
     * Implemented method from net.cloudregistry.rtk.epprtk.epp_SmartExtension interface.
     * @param the EPP "extension" DOM Element
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the extension element does not contain the expected data
     * @see net.cloudregistry.rtk.epprtk.epp_SmartExtension
     */
    public void fromDOM(Element el) throws epp_XMLException
    {
        String method_name = "fromDOM(el)";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        tm_name_ = null;
        tm_number_ = null;
        tm_locality_ = null;
        tm_entitlement_ = null;
        pvrc_ = null;
        phase_ = null;

        NodeList launchphase_node_list = el.getElementsByTagNameNS(NS_LAUNCHPHASE, tag_);
        if (launchphase_node_list.getLength() == 0) {
            throw new epp_XMLException("unparsable or missing launchphase");
        }
        if (launchphase_node_list.getLength() > 1) {
            throw new epp_XMLException("more then one launchphase element");
        }

        debug(DEBUG_LEVEL_TWO,method_name,"launchphase_node_list's node count ["+launchphase_node_list.getLength()+"]");
        Node launchphase_node = launchphase_node_list.item(0);

        NodeList detail_node_list = launchphase_node.getChildNodes();

        if ( detail_node_list.getLength() == 0 ) {
            return;
        }

        for (int count = 0; count < detail_node_list.getLength(); count++)
        {
            Node a_node = detail_node_list.item(count);

			if (a_node.getLocalName().equals("trademark_name")) {
				tm_name_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("trademark_locality")) {
				tm_locality_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("trademark_entitlement")) {
				tm_entitlement_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("trademark_number")) {
				tm_number_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("pvrc")) {
				pvrc_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("phase")) {
				phase_ = a_node.getFirstChild().getNodeValue();
			}
			else if (a_node.getLocalName().equals("application_id")) {
				app_id_ = a_node.getFirstChild().getNodeValue();
			}
        }
    }

    
    /**
     * Converts the launch phase data into a DOM Node to be appended to the extension element
     * of the command.
     * Implemented method from net.cloudregistry.rtk.epprtk.epp_SmartExtension interface.
     * @see net.cloudregistry.rtk.epprtk.epp_SmartExtension
     */
	public Node toDOM(Document doc) throws epp_XMLException
    {
        /*
        Element launchphase = doc.createElement("launchphase:" + tag_);
        launchphase.setAttribute("xmlns:launchphase", NS_LAUNCHPHASE);
        launchphase.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        launchphase.setAttribute("xsi:schemaLocation", NS_LAUNCHPHASE + " launchphase-1.0.xsd");
        */
        Element launchphase = doc.createElementNS(NS_LAUNCHPHASE, tag_);
        launchphase.setAttributeNS(NS_XMLNS, "xmlns:launchphase", NS_LAUNCHPHASE);
        launchphase.setAttributeNS(NS_XMLNS, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        launchphase.setAttributeNS(NS_XMLNS, "xmlns", NS_LAUNCHPHASE);
        launchphase.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", NS_LAUNCHPHASE + " launchphase-1.0.xsd");
        launchphase.setPrefix("launchphase");

        if (app_id_ != null) {
        	addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "application_id", app_id_);
        }
        if (tm_name_ != null) {
        	addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "trademark_name", tm_name_);
        }
        if (tm_number_ != null) {
            addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "trademark_number", tm_number_);
        }
        if (tm_locality_ != null) {
    		addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "trademark_locality", tm_locality_);
        }
        if ( tm_entitlement_ != null) {
        	addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "trademark_entitlement", tm_entitlement_);
		}
		if ( pvrc_ != null) {
			addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "pvrc", pvrc_);
		}
		if ( phase_ != null) {
			addXMLElement(doc, launchphase, NS_LAUNCHPHASE, "phase", phase_);
		}

        return launchphase;
    }

    

    /**
     * Convenience method for creating an element with the given namespace and tag_name with the given text node value.
     * This is a namespace-aware version of EPPXMLBase.addXMLElement.
     * @param doc
     * @param containing_element
     * @param ns
     * @param tag_name
     * @param value
     * @return
     */
    protected Element addXMLElement(Document doc, Element containing_element, String ns, String tag_name, String value)
    {
        Element xml_element = doc.createElementNS(ns, tag_name);
        if ( value != null && value.length() != 0 )
        {
            xml_element.appendChild( doc.createTextNode(value) );
        }
        containing_element.appendChild( xml_element );
        return xml_element;
    }

}
