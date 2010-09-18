package net.cloudregistry.rtk.epprtk;

import org.openrtk.idl.epprtk.epp_Extension;
import org.openrtk.idl.epprtk.epp_XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.tucows.oxrs.epprtk.rtk.xml.EPPDomainInfo;

public class LaunchPhaseEPPDomainInfo extends EPPDomainInfo {

	protected void prepareExtensionElement(Document doc, Element command,
			epp_Extension[] extensions) throws epp_XMLException {
		LaunchPhaseEPPBase.prepareExtensionElement(doc, command, extensions);
	}

	
	/**
	 * 
	 * @return the DOM Element corresponding to the EPP response extension
	 * @throws epp_XMLException
	 */
	protected Element getExtension() throws epp_XMLException
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
	
}
