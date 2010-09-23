package net.cloudregistry.rtk.epprtk;

import java.util.*;
import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;
import org.w3c.dom.Element;


public class LaunchPhaseTest {
    private static String USAGE = "Usage: net.cloudregistry.rtk.epprtk.LaunchPhaseTest epp_host_name epp_host_port epp_client_id epp_password epp_domain_name epp_contact_id";
    
	public static void main(String args[]) {
		epp_Command command_data = null;
		epp_AuthInfo domain_auth_info = null;
		try {
			if (args.length < 6) {
				System.err.println(USAGE);
				System.exit(1);
			}

			String epp_host_name = args[0];
			String epp_host_port_string = args[1];
			String epp_client_id = args[2];
			String epp_password = args[3];
			String epp_domain_name = args[4];
			String epp_contact_id = args[5];
			int epp_host_port = Integer.parseInt(epp_host_port_string);
			EPPClient epp_client = new EPPClient(epp_host_name,epp_host_port,
					epp_client_id, epp_password);

			epp_client.setLang("en");

			System.out.println("Connecting to the EPP Server and getting the greeting");
			epp_Greeting greeting = epp_client.connectAndGetGreeting();

			System.out.println("greeting's server: [" + greeting.m_server_id + "]");
			System.out.println();

			String client_trid = getClientTrid(epp_client_id);
			epp_client.login(client_trid);
			String applicationID = null;
			try {
				// ***************************
				// Domain Create
				// ***************************
				System.out.println("Creating the Domain Create command");
				
				LaunchPhaseCreateDomainWrapper wrapper = new LaunchPhaseCreateDomainWrapper();
				wrapper.setDomainName(epp_domain_name);
				wrapper.setPeriodYears(2);
				wrapper.setRegistrantContactID(epp_contact_id);
				wrapper.addNameServer("ns1.host.tld");
				wrapper.addNameServer("ns2.host.tld");
				wrapper.addContact(epp_DomainContactType.ADMIN, epp_contact_id);
				wrapper.addContact(epp_DomainContactType.BILLING, epp_contact_id);
				wrapper.addContact(epp_DomainContactType.TECH, epp_contact_id);
				wrapper.setAuthInfoPassword("abc123456");
				wrapper.setTrademarkName("ACME Kitchen Products");
				wrapper.setTrademarkEntitlement("owner");
				wrapper.setTrademarkLocality("US");
				wrapper.setTrademarkNumber("23985-3985-239899-22");
				wrapper.setPhase("sr");
				LaunchPhaseEPPDomainCreate dc = wrapper.toEPPDomainCreate(getClientTrid(epp_client_id));
				System.out.println("epp create domain command = \n" + dc.toXML());

				// Now ask the EPPClient to process the request and retrieve
				// a response from the server.
				dc = (LaunchPhaseEPPDomainCreate) epp_client.processAction(dc);
				
				epp_DomainCreateRsp domain_create_response = dc.getResponseData();
				epp_Response response = domain_create_response.getRsp();
				epp_Result[] results = response.getResults();
				System.out.println("DomainCreate results: [" + results[0].m_code + "] [" + results[0].m_msg + "]");

				// The application_id is returned on a successful domain creation.
				applicationID = dc.getApplicationID(); // save it for info domain later
				System.out.println("ApplicationID: " + applicationID);
				System.out.println("DomainCreate results: domain name ["
						+ domain_create_response.m_name + "] exp date ["
						+ domain_create_response.m_expiration_date + "]");
			} catch (epp_XMLException xcp) {
				// Either the request was missing some required data in
				// validation before sending to the server, or the server's
				// response was either unparsable or missing some required data.
				System.err.println("epp_XMLException! [" + xcp.m_error_message
						+ "]");
			} catch (epp_Exception xcp) {
				// The EPP Server has responded with an error code with
				// some optional messages to describe the error.
				System.err.println("epp_Exception!");
				epp_Result[] results = xcp.m_details;
				// We're taking advantage epp_Result's toString() here
				// for debugging. Take a look at the javadocs for
				// the full list of attributes in the class.
				System.err.println("\tresult: [" + results[0] + "]");
			} catch (Exception xcp) {
				// Other unexpected exceptions
				System.err.println("Domain Create failed! ["
						+ xcp.getClass().getName() + "] [" + xcp.getMessage()
						+ "]");
				xcp.printStackTrace();
			}

            try
            {
                // ***************************
                // Domain Info
                // ***************************
                System.out.println("Creating the Domain Info command");
                epp_DomainInfoReq domain_info_request = new epp_DomainInfoReq();

                command_data = new epp_Command();
                // The client trid is optional by EPP.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests.
                // Some registries make it mandatory and unique per session.
                command_data.m_client_trid = getClientTrid(epp_client_id);
                domain_info_request.m_cmd = command_data;

                // The only domain-specific parameter is the domain name itself.
                domain_info_request.m_name = epp_domain_name;
                
                LaunchPhaseExtension launchphase = new LaunchPhaseExtension("info");
                launchphase.setApplicationID(applicationID);
                launchphase.setPhase("sr");
                domain_info_request.m_cmd.m_extensions = new epp_Extension[1];
                domain_info_request.m_cmd.m_extensions[0] = launchphase;

                LaunchPhaseEPPDomainInfo domain_info = new LaunchPhaseEPPDomainInfo();
                domain_info.setRequestData(domain_info_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_info = (LaunchPhaseEPPDomainInfo) epp_client.processAction(domain_info);
                // or, alternatively, this method can be used...
                //domain_info.fromXML(epp_client.processXML(domain_info.toXML()));

                epp_DomainInfoRsp domain_info_response = domain_info.getResponseData();
                epp_Response response = domain_info_response.m_rsp;
                epp_Result[] results = response.m_results;

                // You can also save the authorization information from an info where
                // the calling registrar is the sponsoring client for the
                // object.
                domain_auth_info = domain_info_response.m_auth_info;
                
                System.out.println("DomainInfo results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
                // The Info command returns some standard information like
                // the current sponsoring client id, the creator client id,
                // the create time and the last update time.
                // For a Domain Info, the domain's nameservers, hosts, status
                // last transfer client id, last transfer date,
                // expiration date and domain ROID are returned.
                System.out.println("DomainInfo results: clID ["+domain_info_response.m_client_id+"] crID ["+domain_info_response.m_created_by+"]");
                System.out.println("DomainInfo results: crDate ["+domain_info_response.m_created_date+"] upDate ["+domain_info_response.m_updated_date+"]");
                System.out.println("DomainInfo results: exDate ["+domain_info_response.m_expiration_date+"]");
                if ( domain_auth_info != null )
                {
                    System.out.println("Domain's authID ["+domain_auth_info.m_value+"]");
                }
                
            	LaunchPhaseExtension launchphaseExt = domain_info.getResponseLaunchPhaseExtension();
                if (launchphaseExt != null) {
                    System.out.println("DomainInfo results: trademark_name [" + launchphaseExt.getTrademarkName() + "]");
                    System.out.println("DomainInfo results: trademark_number [" + launchphaseExt.getTrademarkNumber() + "]");
                    System.out.println("DomainInfo results: trademark_locality [" + launchphaseExt.getTrademarkLocality() + "]");
                    System.out.println("DomainInfo results: trademark_entitlement [" + launchphaseExt.getTrademarkEntitlement() + "]");
                    System.out.println("DomainInfo results: pvrc [" + launchphaseExt.getPVRC() + "]");
                    System.out.println("DomainInfo results: phase [" + launchphaseExt.getPhase() + "]");
                }
                else {
                    System.err.println("no launch phase extension element returned from response!");
                }


            }
            catch ( epp_XMLException xcp )
            {
                // Either the request was missing some required data in
                // validation before sending to the server, or the server's
                // response was either unparsable or missing some required data.
                System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
            }
            catch ( epp_Exception xcp )
            {
                // The EPP Server has responded with an error code with
                // some optional messages to describe the error.
                System.err.println("epp_Exception!");
                epp_Result[] results = xcp.m_details;
                // We're taking advantage epp_Result's toString() here
                // for debugging.  Take a look at the javadocs for
                // the full list of attributes in the class.
                System.err.println("\tresult: ["+results[0]+"]");
            }
            catch ( Exception xcp )
            {
                // Other unexpected exceptions
                System.err.println("Domain Info failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                xcp.printStackTrace();
            }

			epp_client.logout(getClientTrid(epp_client_id));
			epp_client.disconnect();
		} catch (epp_XMLException xcp) {
			System.err.println("epp_XMLException! [" + xcp.m_error_message
					+ "]");
		} catch (epp_Exception xcp) {
			System.err.println("epp_Exception!");
			epp_Result[] results = xcp.m_details;
			System.err.println("\tresult: [" + results[0] + "]");
		} catch (Exception xcp) {
			System.err.println("Exception! [" + xcp.getClass().getName()
					+ "] [" + xcp.getMessage() + "]");
			xcp.printStackTrace();
		}
	}

    protected static String getClientTrid(String epp_client_id)
    {
        return "ABC:"+epp_client_id+":"+System.currentTimeMillis();
    }
}
