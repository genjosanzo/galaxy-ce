package org.mule.galaxy.wsi.wsdl.soap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.mule.galaxy.wsi.wsdl.AbstractWsdlRule;
import org.mule.galaxy.wsi.wsdl.AssertionResult;
import org.mule.galaxy.wsi.wsdl.ValidationResult;

import org.w3c.dom.Document;

/**
 * R2706: A wsdl:binding in a DESCRIPTION MUST use the value of "literal" for the 
 * use attribute in all soapbind:body, soapbind:fault, soapbind:header and 
 * soapbind:headerfault elements.
 * 
 */
public class OperationSignatureRule extends AbstractWsdlRule {

    public OperationSignatureRule() throws Exception {
        super("R2710");
    }

    public ValidationResult validate(Document document, Definition def) {
        
        ValidationResult result = new ValidationResult();
        
        AssertionResult ar = new AssertionResult(getName(), true);
        if (def == null || def.getBindings() == null) {
            return result;
        }
        
        for (Iterator itr = def.getBindings().values().iterator(); itr.hasNext();) {
            Binding binding = (Binding) itr.next();
            
            SOAPBinding soapBinding = getSOAPBinding(binding);
            
            if (soapBinding != null) {
                if (soapBinding.getStyle().equals("document")) {
                    checkSignatures(binding, ar);
                }
            }
        }
        
        if (ar.getMessages().size() > 0) {
            result.addAssertionResult(ar);
        }
        
        return result;
    }

    private void checkSignatures(Binding binding, AssertionResult ar) {
        
        Map<Set<Param>, String> sig2op = new HashMap<Set<Param>, String>();
        
        for (Iterator ops = binding.getBindingOperations().iterator(); ops.hasNext();) {
            BindingOperation bop = (BindingOperation) ops.next();
            Operation op = bop.getOperation();
            
            Input input = op.getInput();
            if (input != null) {
                Message msg = input.getMessage();
                
                Set<Param> params = new HashSet<Param>();
                for (Iterator parts = msg.getParts().values().iterator(); parts.hasNext();) {
                    Part part = (Part) parts.next();
                    
                    if (part.getTypeName() != null) {
                        params.add(new Param(false, part.getTypeName()));
                    } else {
                        params.add(new Param(true, part.getElementName()));
                    }
                }
                
                String opName = sig2op.get(params);
                if (opName != null) {
                    ar.addMessage("Binding " + binding.getQName() + " has an operation with a duplicate signature: " + opName);
                } else {
                    sig2op.put(params, op.getName());
                }
            }
        }
    }

    private SOAPBinding getSOAPBinding(Binding binding) {
        for (Iterator itr = binding.getExtensibilityElements().iterator(); itr.hasNext();) {
            Object o = itr.next();
            
            if (o instanceof SOAPBinding) {
                return (SOAPBinding) o;
            }
        }
        return null;
    }

    private final class Param {
        private boolean element;
        private QName name;
        
        public Param(boolean element, QName name) {
            super();
            this.element = element;
            this.name = name;
        }
        public boolean isElement() {
            return element;
        }
        public QName getName() {
            return name;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (element ? 1231 : 1237);
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Param other = (Param)obj;
            if (element != other.element)
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
        
    }


}
