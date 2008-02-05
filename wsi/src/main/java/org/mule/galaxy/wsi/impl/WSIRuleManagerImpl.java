package org.mule.galaxy.wsi.impl;

import org.mule.galaxy.wsi.WSIRule;
import org.mule.galaxy.wsi.WSIRuleManager;
import org.mule.galaxy.wsi.wsdl.ImportUriRule;
import org.mule.galaxy.wsi.wsdl.NonEmptyWsdlLocationImportRule;
import org.mule.galaxy.wsi.wsdl.WsdlSchemaValidationRule;
import org.mule.galaxy.wsi.wsdl.WsdlSoapSchemaValidationRule;
import org.mule.galaxy.wsi.wsdl.soap.EmptySoapBindingTransportAttributeRule;
import org.mule.galaxy.wsi.wsdl.soap.NoEncodingRule;
import org.mule.galaxy.wsi.wsdl.soap.OperationSignatureRule;
import org.mule.galaxy.wsi.wsdl.soap.SoapHttpBindingTransportRule;
import org.mule.galaxy.wsi.wsdl.soap.StyleConsistencyRule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

public class WSIRuleManagerImpl implements WSIRuleManager {

    
    protected List<WSIRule> wsi11Rules = new ArrayList<WSIRule>();
    protected Map<String, String> number2Description = new HashMap<String, String>();
    
    public WSIRuleManagerImpl() throws Exception {
        super();

        String name = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).getClass().getName();
        if (name.startsWith("com.sun.org.apache.xerces") && isJDK5()) {
            System.err.println("WARNING: The Sun XML validator does not work correctly in Java 5.\n" +
            		       "You must endorse Xerces for the WS-I Compliance validator to fully\n" +
            		       "work. Schema validation rules will be disabled until then.");
        } else {
            wsi11Rules.add(new WsdlSchemaValidationRule());
            wsi11Rules.add(new WsdlSoapSchemaValidationRule());
        }
        wsi11Rules.add(new ImportUriRule());
        wsi11Rules.add(new NonEmptyWsdlLocationImportRule());
        
        // SOAP Binding rules
        wsi11Rules.add(new EmptySoapBindingTransportAttributeRule());
        wsi11Rules.add(new NoEncodingRule());
        wsi11Rules.add(new SoapHttpBindingTransportRule());
        wsi11Rules.add(new StyleConsistencyRule());
        wsi11Rules.add(new OperationSignatureRule());
    }   
    
    boolean isJDK5() {
        String v = System.getProperty("java.class.version", "44.0");
        
        return "49.0".equals(v);
    }

    public String getDescription(String ruleNumber) {
        String desc = number2Description.get(ruleNumber);
        if (desc == null) {
            for (WSIRule rule : wsi11Rules) {
                if (rule.getName().equals(ruleNumber)) {
                    String packageName = getPackageName(rule.getClass());
                    loadRuleDescriptions(packageName);
                    desc = number2Description.get(ruleNumber);
                }
            }
        }
        
        if (desc == null) {
            desc = "(No Description)";
        }
        
        return desc;
    }

    private void loadRuleDescriptions(String packageName) {
        packageName = packageName.replaceAll("\\.", "/");
        
        InputStream is = getClass().getResourceAsStream("/" + packageName + "/rules.properties");
        if (is == null) {
            return;
        }
        
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            number2Description.put(e.getKey().toString(), e.getValue().toString());
        }
    }
    
    static int copy(final InputStream input,
                           final OutputStream output,
                           int bufferSize)
       throws IOException {
       int avail = input.available();
       if (avail > 262144) {
           avail = 262144;
       }
       if (avail > bufferSize) {
           bufferSize = avail;
       }
       final byte[] buffer = new byte[bufferSize];
       int n = 0;
       n = input.read(buffer);
       int total = 0;
       while (-1 != n) {
           output.write(buffer, 0, n);
           total += n;
           n = input.read(buffer);
       }
       return total;
   }

    public List<WSIRule> getRules(String version) {
        if (version == WSIRuleManager.WSI_BP_1_1) {
            return wsi11Rules;
        }
        throw new UnsupportedOperationException();
    }
    
    static String getPackageName(String className) {
        int pos = className.lastIndexOf('.');
        if (pos != -1) {
            return className.substring(0, pos);
        } else {
            return "";
        }
    }
    
    public static String getPackageName(Class<?> clazz) {
        String className = clazz.getName();
        if (className.startsWith("[L")) {
            className = className.substring(2);
        }
        return getPackageName(className);
    }
    

}
