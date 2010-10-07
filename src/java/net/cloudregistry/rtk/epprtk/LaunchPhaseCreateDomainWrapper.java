package net.cloudregistry.rtk.epprtk;

import java.lang.reflect.Array;
import java.util.*;
import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;
import org.w3c.dom.Element;

/**
 * A wrapper class to provide a simplified way to create sunrise applications
 * using the Launch Phase extension.
 **/
public class LaunchPhaseCreateDomainWrapper {
	protected String domainName = null;
	protected epp_DomainPeriod period = null;
	protected epp_AuthInfo authInfo = null;
	protected LaunchPhaseExtension launchPhase = null;
	protected epp_Command cmd = null;
	public List nameservers = null;
	public String registrantContactID = null;
	public List contacts = null;

	/**
	 * constructor
	 */
	public LaunchPhaseCreateDomainWrapper() {
		// initialize members
		
		this.period = new epp_DomainPeriod();
		this.period.m_unit = epp_DomainPeriodUnitType.YEAR;
		this.period.m_value = 1; // default
		
		this.nameservers = (List) new ArrayList();
		this.contacts = (List) new ArrayList();
		this.authInfo = new epp_AuthInfo();
		this.authInfo.m_type = epp_AuthInfoType.PW;
		
		this.launchPhase = new LaunchPhaseExtension(LaunchPhaseExtension.CREATE_TAG);
	}

	
	/**
	 * Given an <code>epp_DomainCreateReq</code> object, returns a new instance of
	 * this <code>LaunchPhaseCreateDomainWrapper</code> object initialized from the
	 * fields in the given object.
	 * 
	 * This is useful for integration into existing applications using EPP-RTK.
	 * @param req
	 * @return
	 */
	public static LaunchPhaseCreateDomainWrapper fromDomainCreateRequest(epp_DomainCreateReq req) {
		LaunchPhaseCreateDomainWrapper wrapper = new LaunchPhaseCreateDomainWrapper();
		wrapper.setDomainName(req.m_name);
		wrapper.setPeriod(req.m_period);
		wrapper.setNameServers(req.m_name_servers);
		wrapper.setRegistrantContactID(req.m_registrant);
		wrapper.setContacts(req.m_contacts);
		wrapper.setAuthInfo(req.getAuthInfo());
		wrapper.setCmd(req.getCmd());
		return wrapper;
	}

	public LaunchPhaseEPPDomainCreate toEPPDomainCreate(){
		if(cmd != null)
			return toEPPDomainCreate(cmd.getClientTrid());
		return toEPPDomainCreate(null);
	}

	public LaunchPhaseEPPDomainCreate toEPPDomainCreate(String clTRID) {
		epp_DomainCreateReq req = new epp_DomainCreateReq();

		//epp_Command cmd = new epp_Command();
		if(cmd == null) cmd = new epp_Command();
		cmd.m_client_trid = clTRID;
		req.m_cmd = cmd;
		req.m_name = getDomainName();
		req.m_period = getPeriod();
		req.m_name_servers = EPPXMLBase.convertListToStringArray(getNameservers());

		req.m_auth_info = getAuthInfo();
		req.m_contacts = getContactsAsArray();
		req.m_registrant = getRegistrantContactID();

		List<epp_Extension> extensions = new ArrayList<epp_Extension>();
		if(cmd.getExtensions() != null && cmd.getExtensions().length > 0)
			extensions.addAll(Arrays.asList(cmd.getExtensions()));
		extensions.add(this.launchPhase);
		cmd.setExtensions((epp_Extension[])extensions.toArray(new epp_Extension[0]));

		LaunchPhaseEPPDomainCreate domainCreate = new LaunchPhaseEPPDomainCreate();
		domainCreate.setRequestData(req);

		return domainCreate;
	}
	
	
	
	/**
	 * Returns the domain name
	 * @return
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * Sets the domain name of this 
	 * @param name
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * Returns a shallow reference to the internal <code>period</code> member.
	 * @return
	 */
	public epp_DomainPeriod getPeriod() {
		return period;
	}

	/**
	 * This sets the internal <code>period</code> member of this object using deep copy.
	 * @param period
	 */
	public void setPeriod(epp_DomainPeriod period) {
		this.period.m_unit = period.m_unit;
		this.period.m_value = period.m_value;
	}

	/**
	 * Convenience method to get the number of years in the period value.
	 * This assumes that period unit is "y".
	 * @return
	 */
	public short getPeriodYears() {
		return period.m_value;
	}

	/**
	 * Convenience method to set the number of years in the period value.
	 * If the unit type is not "y", this method will set it to that unit type.
	 * @return
	 */
	public void setPeriodYears(short years) {
		this.period.m_unit = epp_DomainPeriodUnitType.YEAR; // just to be sure
		this.period.m_value = years;
	}

	public void setPeriodYears(int years) {
		if (years < 0 || years > 32767) {
			throw new RuntimeException("year value " + years + "is too large");
		}
		this.period.m_unit = epp_DomainPeriodUnitType.YEAR; // just to be sure
		this.period.m_value = (short)years; // CAST!
	}


	/**
	 * @return the authInfo
	 */
	public epp_AuthInfo getAuthInfo() {
		return authInfo;
	}


	/**
	 * @param authInfo the authInfo to set
	 */
	public void setAuthInfo(epp_AuthInfo authInfo) {
		this.authInfo.m_type = authInfo.m_type;
		this.authInfo.m_value = authInfo.m_value;
	}

	public void setAuthInfoPassword(String password) {
		this.authInfo.m_type = epp_AuthInfoType.PW; // just to be explicit
		this.authInfo.m_value = password;
	}


	/**
	 * @return a shallow reference to the internal <code>nameservers</code> member.
	 */
	public List getNameservers() {
		return nameservers;
	}

	public void addNameServer(String hostName) {
		this.nameservers.add(hostName);
	}
	
	public void setNameServers(List hostNames) {
		this.nameservers = new ArrayList(hostNames);
	}
	
	public void setNameServers(String[] hostNames) {
		this.nameservers = new ArrayList();
		for (int i = 0; i < hostNames.length; i++) {
			this.addNameServer(hostNames[i]);
		}
	}
	
	/**
	 * @return the registrantContactID
	 */
	public String getRegistrantContactID() {
		return registrantContactID;
	}


	/**
	 * @param registrantContactID the registrantContactID to set
	 */
	public void setRegistrantContactID(String registrantContactID) {
		this.registrantContactID = registrantContactID;
	}


	public List getContacts() {
		return contacts;
	}

	public epp_DomainContact[] getContactsAsArray() {
		return (epp_DomainContact[])contacts.toArray(new epp_DomainContact[1]);
	}

	public void addContact(epp_DomainContact contact) {
		epp_DomainContact c = new epp_DomainContact(contact.m_type, contact.m_id);
		this.contacts.add(c);
	}

	public void addContact(epp_DomainContactType type, String id) {
		epp_DomainContact c = new epp_DomainContact(type, id);
		this.addContact(c);
	}

	public void setContacts(List newContacts) {
		this.contacts = new ArrayList();
		Iterator it = newContacts.iterator();
		while (it.hasNext()) {
			this.addContact((epp_DomainContact)it.next());
		}
	}

	public void setContacts(epp_DomainContact[] newContacts) {
		this.contacts = new ArrayList();
		for (int i = 0; i < newContacts.length; i++) {
			this.addContact(newContacts[i]);
		}
	}

	public void setCmd(epp_Command cmd){
		this.cmd = cmd;
	}

    public void setTrademarkName(String trademarkName) {
    	this.launchPhase.setTrademarkName(trademarkName);
    }
    
    public void setPVRC(String pvrc) {
    	this.launchPhase.setPVRC(pvrc);
    }
    
    public void setPhase(String phase) {
    	this.launchPhase.setPhase(phase);
    }
    
    public void setTrademarkLocality(String trademarkLocality) {
    	this.launchPhase.setTrademarkLocality(trademarkLocality);
    }
    
    public void setTrademarkEntitlement(String trademarkEntitlement) {
    	this.launchPhase.setTrademarkEntitlement(trademarkEntitlement);
    }
    
    public void setTrademarkNumber(String trademarkNumber) {
    	this.launchPhase.setTrademarkNumber(trademarkNumber);
    }
    
	public String getApplicationID() {
		return this.launchPhase.getApplicationID();
	}
	
    public String getTrademarkName() {
    	return this.launchPhase.getTrademarkName();
    }
    public String getPVRC() {
    	return this.launchPhase.getPVRC();
    }
    
    public String getPhase() {
    	return this.launchPhase.getPhase();
    }
    
    public String getTrademarkLocality() {
    	return this.launchPhase.getTrademarkLocality();
    }
    
    public String getTrademarkEntitlement() {
    	return this.launchPhase.getTrademarkEntitlement();
    }
    
    public String getTrademarkNumber() {
    	return this.launchPhase.getTrademarkNumber();
    }
}
