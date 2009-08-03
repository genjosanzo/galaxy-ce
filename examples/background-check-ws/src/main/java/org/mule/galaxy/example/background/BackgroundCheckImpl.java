package org.mule.galaxy.example.background;

import javax.jws.WebService;

import org.mule.galaxy.example.background_check.AddressType;
import org.mule.galaxy.example.background_check.BackgroundCheckPortType;
import org.mule.galaxy.example.background_check.BackgroundInformation;
import org.mule.galaxy.example.background_check.BackgroundInformationRequest;

@WebService(portName = "SoapPort",
            serviceName = "BackgroundCheckImpl",
            targetNamespace = "http://galaxy.mule.org/example/background-check/")
public class BackgroundCheckImpl implements BackgroundCheckPortType {

    public BackgroundInformation backgroundCheck(BackgroundInformationRequest req) {
        
        BackgroundInformation info = new BackgroundInformation();
        info.setFullName("Joe Schmoe");
        info.setHasCriminalRecord(true);
        info.setHasValidSocialSecurityNumber(true);
        
        AddressType add = new AddressType();
        add.setAddress1("123 Main St");
        add.setCity("Smalltown");
        add.setState("MO");
        add.setPostalCode("12345");
        info.setAddress(add);
        
        return info;
    }

}
