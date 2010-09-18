package net.cloudregistry.rtk.epprtk;

import java.util.*;
import com.tucows.oxrs.epprtk.rtk.*;
import com.tucows.oxrs.epprtk.rtk.xml.*;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.domain.*;
import org.w3c.dom.Element;


public class LaunchPhaseTest {
    private static String USAGE = "Usage: net.cloudregistry.rtk.epprtk.LaunchPhaseTest epp_host_name epp_host_port epp_client_id epp_password epp_domain_name epp_contact_id";

    /**
     * Main of the example.  Performs Domain check, info, create, update, renew, transfer and delete.
    **/
    public static void main(String args[])
    {
        System.out.println("Start of the Domain example");

        epp_Command command_data = null;
        epp_AuthInfo domain_auth_info = null;
        
        try
        {
            if (args.length < 6)
            {
                System.err.println(USAGE);
                System.exit(1);
            }

            String epp_host_name = args[0];
            String epp_host_port_string = args[1];
            String epp_client_id = args[2];
            String epp_password  = args[3];
            String epp_domain_name = args[4];
            String epp_contact_id = args[5];

            int epp_host_port = Integer.parseInt(epp_host_port_string);

            EPPClient epp_client = new EPPClient(epp_host_name,
                                                 epp_host_port,
                                                 epp_client_id,
                                                 epp_password);
            
            epp_client.setLang("en");

            // The protocol used is set by the rtk.transport property
            // in etc/rtk.properties

            System.out.println("Connecting to the EPP Server and getting the greeting");
            epp_Greeting greeting = epp_client.connectAndGetGreeting();

            System.out.println("greeting's server: ["+greeting.m_server_id+"]");
            System.out.println();
	    
            String client_trid = getClientTrid(epp_client_id);
            
            System.out.println("Logging into the EPP Server");

            // XXX change me, update me
            // If epp_client.setEPPServices() or epp_client.setEPPUnspecServices() 
            // have been called, epp_client.login() uses services values set by user,
            // otherwise, epp_client.login() fills in default service values for you
            // which are contact, domain and host (pretty standard stuff).
            epp_client.login(client_trid);

            try
            {
                // ***************************
                // Domain Check
                // ***************************
                System.out.println("Creating the Domain Check command");
                epp_DomainCheckReq domain_check_request = new epp_DomainCheckReq();
                
                command_data = new epp_Command();
                // The client trid is optional by EPP.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests.
                // Some registries make it mandatory and unique per session.
                command_data.m_client_trid = getClientTrid(epp_client_id);
                domain_check_request.m_cmd = command_data;

                // The Domain Check request can accept an array of domain
                // names.  In this example, an ArrayList is used to dynamically
                // create the List of domain names and then EPPXMLBase's
                // utility method convertListToStringArray() is used
                // to convert the List to a String array.
                List domain_list = (List)new ArrayList();
                domain_list.add(epp_domain_name);
                domain_check_request.m_names = EPPXMLBase.convertListToStringArray(domain_list);
                
                EPPDomainCheck domain_check = new EPPDomainCheck();
                domain_check.setRequestData(domain_check_request);
                
                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_check = (EPPDomainCheck) epp_client.processAction(domain_check);
                // or, alternatively, this method can be used...
                //domain_check.fromXML(epp_client.processXML(domain_check.toXML()));

                epp_DomainCheckRsp domain_check_response = domain_check.getResponseData();
                epp_Response response = domain_check_response.m_rsp;
                epp_Result[] results = response.m_results;
                System.out.println("DomainCheck results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");
                // All EPP Check requests, regardless of the object being checked,
                // will return a generic epp_CheckResult array.  To find the
                // check results for a particular object, EPPXMLBase's utility
                // method getCheckResultFor() can be used.  This method returns
                // a Boolean object or null if the value was not found in the
                // epp_CheckResult array.
                epp_CheckResult[] check_results = domain_check_response.m_results;
                System.out.println("DomainCheck results: domain ["+epp_domain_name+"] available? ["+EPPXMLBase.getAvailResultFor(check_results, epp_domain_name)+"]");
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
                System.err.println("\tcode: ["+results[0].m_code+"] lang: ["+results[0].m_lang+"] msg: ["+results[0].m_msg+"]");
                if ( results[0].m_values != null && results[0].m_values.length > 0 )
                {
                    System.err.println("\tvalue: ["+results[0].m_values[0]+"]");
                }
            }
            catch ( Exception xcp )
            {
                // Other unexpected exceptions
                System.err.println("Domain Check failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
                xcp.printStackTrace();
            }


            
            String applicationID = null;
            try
            {
                // ***************************
                // Domain Create
                // ***************************
                System.out.println("Creating the Domain Create command");
                epp_DomainCreateReq domain_create_request = new epp_DomainCreateReq();
                
                command_data = new epp_Command();
                // The client trid is optional by EPP.  it's main use
                // is for registrar tracking and logging of requests,
                // especially for data creation or modification requests.
                // Some registries make it mandatory and unique per session.
                command_data.m_client_trid = getClientTrid(epp_client_id);
                domain_create_request.m_cmd = command_data;

                domain_create_request.m_name = epp_domain_name;
                // The domain's period is optional.  It is specified with
                // an object that contains the unit of measurement (years or 
                // months) and the actual period value.
                domain_create_request.m_period = new epp_DomainPeriod();
                domain_create_request.m_period.m_unit = epp_DomainPeriodUnitType.YEAR;
                domain_create_request.m_period.m_value = (short) 2;
                
                // At domain creation, if the registry requires nameservers,
                // you must specify another domain's nameserver's in the request.
                // You can't use nameserver in the same namespace as the domain,
                // but those host objects don't exist yet.
                List name_server_list = (List)new ArrayList();

                // quick little note -- it just happens that these
                // nameservers exist in many OT&E domain registries
                // out there, so they were chosen to make sure the 
                // create succeeds.  Feel free to use other values
                // in this example.
                name_server_list.add("a.cloudregistry.net");
                name_server_list.add("b.cloudregistry.net");
                domain_create_request.m_name_servers = EPPXMLBase.convertListToStringArray(name_server_list);
                                
                domain_auth_info = new epp_AuthInfo();
                domain_auth_info.m_value = "abc123456";

                // For the current spec of EPP, PW is the only allowed type
                // of auth info.  So, the type can be left null and the RTK will
                // fill in the value for you.
                domain_auth_info.m_type = epp_AuthInfoType.PW;
                domain_create_request.m_auth_info = domain_auth_info;

                // Some registries require a minimum number of contacts for a domain
                Vector contacts = new Vector();
                contacts.add(new epp_DomainContact(epp_DomainContactType.TECH, epp_contact_id));
                contacts.add(new epp_DomainContact(epp_DomainContactType.ADMIN, epp_contact_id));
                contacts.add(new epp_DomainContact(epp_DomainContactType.BILLING, epp_contact_id));
                domain_create_request.m_contacts = (epp_DomainContact[])contacts.toArray(new epp_DomainContact[1]);

                domain_create_request.m_registrant = epp_contact_id;
 
                // ***************************
                LaunchPhaseExtension launchphase = new LaunchPhaseExtension("create");
                launchphase.setTrademarkName("ACME Kitchen Products");
                launchphase.setTrademarkEntitlement("owner");
                launchphase.setTrademarkLocality("US");
                launchphase.setTrademarkNumber("23985-3985-239899-22");
                launchphase.setPhase("sr");
                
                //System.out.println("launchphase = " + launchphase.toXML());
                domain_create_request.m_cmd.m_extensions = new epp_Extension[1];
                domain_create_request.m_cmd.m_extensions[0] = launchphase;

                LaunchPhaseEPPDomainCreate domain_create = new LaunchPhaseEPPDomainCreate();
                domain_create.setRequestData(domain_create_request);
                System.out.println("epp create domain command = \n" + domain_create.toXML());

                // Now ask the EPPClient to process the request and retrieve 
                // a response from the server.
                domain_create = (LaunchPhaseEPPDomainCreate) epp_client.processAction(domain_create);
                // or, alternatively, this method can be used...
                //domain_create.fromXML(epp_client.processXML(domain_create.toXML()));

                epp_DomainCreateRsp domain_create_response = domain_create.getResponseData();
                epp_Response response = domain_create_response.getRsp();
                epp_Result[] results = response.getResults();
                System.out.println("DomainCreate results: ["+results[0].m_code+"] ["+results[0].m_msg+"]");

                Element extension = domain_create.getExtension();
                if (extension != null) {
                	LaunchPhaseExtension launchphase_resp = new LaunchPhaseExtension("creData");
                    launchphase_resp.fromDOM(extension);
                    applicationID = launchphase_resp.getApplicationID();
                    System.out.println("ApplicationID: " + applicationID);
                }
                else {
                    System.err.println("no extension element returned from response!");
                }

                // The application_id is returned on a successful domain creation.
                System.out.println("DomainCreate results: domain name ["+domain_create_response.m_name+"] exp date ["+domain_create_response.m_expiration_date+"]");
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
                System.err.println("Domain Create failed! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
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
                
                Element extension = domain_info.getExtension();
                if (extension != null) {
                	LaunchPhaseExtension launchphase_resp = new LaunchPhaseExtension("infData");
                    launchphase_resp.fromDOM(extension);
                    System.out.println("DomainInfo results: trademark_name [" + launchphase_resp.getTrademarkName() + "]");
                    System.out.println("DomainInfo results: trademark_number [" + launchphase_resp.getTrademarkNumber() + "]");
                    System.out.println("DomainInfo results: trademark_locality [" + launchphase_resp.getTrademarkLocality() + "]");
                    System.out.println("DomainInfo results: trademark_entitlement [" + launchphase_resp.getTrademarkEntitlement() + "]");
                    System.out.println("DomainInfo results: pvrc [" + launchphase_resp.getPVRC() + "]");
                    System.out.println("DomainInfo results: phase [" + launchphase_resp.getPhase() + "]");
                }
                else {
                    System.err.println("no extension element returned from response!");
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

            // All done with this session, so let's log out...
            System.out.println("Logging out from the EPP Server");
            epp_client.logout(getClientTrid(epp_client_id));

            // ... and disconnect            
            System.out.println("Disconnecting from the EPP Server");
            epp_client.disconnect();

        }
        catch ( epp_XMLException xcp )
        {
            System.err.println("epp_XMLException! ["+xcp.m_error_message+"]");
        }
        catch ( epp_Exception xcp )
        {
            System.err.println("epp_Exception!");
            epp_Result[] results = xcp.m_details;
            System.err.println("\tresult: ["+results[0]+"]");
        }
        catch ( Exception xcp )
        {
            System.err.println("Exception! ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
            xcp.printStackTrace();
        }

    }

    protected static String getClientTrid(String epp_client_id)
    {
        return "ABC:"+epp_client_id+":"+System.currentTimeMillis();
    }
}
