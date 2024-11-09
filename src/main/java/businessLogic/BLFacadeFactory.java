package businessLogic;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;


public class BLFacadeFactory {
	public BLFacadeFactory() {
		super();
	}
	
	public BLFacade BLFacadeCreator(boolean isLocal) {
		
		ConfigXML c = ConfigXML.getInstance();
		try {
			BLFacade appFacadeInterface;
			if(isLocal) {
				DataAccess da = new DataAccess();
				appFacadeInterface = new BLFacadeImplementation(da);
			} else {
				String serviceName = "http://" + c.getBusinessLogicNode() + ":" + c.getBusinessLogicPort() + "/ws/"
						+ c.getBusinessLogicName() + "?wsdl";
	
				URL url = new URL(serviceName);
	
				QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
	
				Service service = Service.create(url, qname);
	
				appFacadeInterface = service.getPort(BLFacade.class);
			}
			return appFacadeInterface;
		} catch(Exception e) {
			System.out.println("Error in ApplicationLauncher: " + e.toString());
			return null;
		}
	}
}