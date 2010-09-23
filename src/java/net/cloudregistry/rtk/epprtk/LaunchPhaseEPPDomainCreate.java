package net.cloudregistry.rtk.epprtk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.openrtk.idl.epprtk.epp_ExtMessage;
import org.openrtk.idl.epprtk.epp_ExtResultValue;
import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_MessageQueue;
import org.openrtk.idl.epprtk.epp_Response;
import org.openrtk.idl.epprtk.epp_Result;
import org.openrtk.idl.epprtk.epp_ResultValue;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainCreate;

public class LaunchPhaseEPPDomainCreate extends EPPDomainCreate {

	protected void prepareExtensionElement(Document doc, Element command,
			epp_Extension[] extensions) throws epp_XMLException {
		LaunchPhaseEPPBase.prepareExtensionElement(doc, command, extensions);
	}

	
	/**
	 * 
	 * @return the DOM Element corresponding to the EPP response extension
	 * @throws epp_XMLException
	 */
	public Element getExtension() throws epp_XMLException
	{
        String method_name = "getExtension()";
        Element epp_node;
		try {
			epp_node = getDocumentElement();
		} catch (Exception e) {
            debug(DEBUG_LEVEL_ONE,method_name, e);
            throw new epp_XMLException("unable to parse xml ["+e.getClass().getName()+"] ["+e.getMessage()+"]");
		}
		return LaunchPhaseEPPBase.getExtension(epp_node);
	}

	
	public LaunchPhaseExtension getResponseLaunchPhaseExtension() throws epp_XMLException {
		return LaunchPhaseEPPBase.getLaunchPhaseExtension(getExtension(), LaunchPhaseExtension.CREATE_DATA_TAG);
	}

	
	public String getApplicationID() throws epp_XMLException {
		LaunchPhaseExtension launchphaseResponse = getResponseLaunchPhaseExtension();
		if (launchphaseResponse == null) {
			return null;
		}
		
		return launchphaseResponse.getApplicationID();
	}
}
