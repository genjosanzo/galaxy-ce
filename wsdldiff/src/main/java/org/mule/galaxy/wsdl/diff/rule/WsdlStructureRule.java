package org.mule.galaxy.wsdl.diff.rule;

import com.sun.xml.xsom.parser.SchemaDocument;

import java.util.Iterator;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;

import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;

public class WsdlStructureRule implements Rule {

    public static final String MISSING_PORT_TYPE = "missing.portType";
    public static final String MISSING_OPERATION = "missing.operation";
    public static final String MISSING_BINDING = "missing.binding";
    public static final String MISSING_BINDING_OPERATION = "missing.bindingOperation";
    
    public void check(Definition original, 
                      Definition newWsdl, 
                      Set<SchemaDocument> originalSchemas,
                      Set<SchemaDocument> newSchemas, 
                      DifferenceListener listener) {

        // Check that all the original port types are still there along with
        // their operations
        for (Iterator itr = original.getPortTypes().values().iterator(); itr.hasNext();) {
            PortType pt = (PortType)itr.next();

            PortType pt2 = newWsdl.getPortType(pt.getQName());

            if (pt2 == null) {
                listener
                    .onEvent(new DifferenceEvent("PortType \"" + pt.getQName() + "\" was removed.",
                                                 MISSING_PORT_TYPE, false, false));

                continue;
            }

            for (Iterator oitr = pt.getOperations().iterator(); oitr.hasNext();) {
                Operation op = (Operation)oitr.next();

                Operation op2 = pt2.getOperation(op.getName(), op.getInput().getName(), op.getOutput()
                    .getName());
                
                if (op2 == null) {
                    listener
                    .onEvent(new DifferenceEvent("Operation \"" + op.getName() + "\" was removed." +
                                                                          pt.getQName(),
                                                                          MISSING_OPERATION, false, false));

                }
            }
        }

        for (Iterator itr = original.getBindings().values().iterator(); itr.hasNext();) {
            Binding b = (Binding) itr.next();
            
            Binding b2 = newWsdl.getBinding(b.getQName());
            
            if (b2 == null) {
                listener
                    .onEvent(new DifferenceEvent("Binding \"" + b.getQName()
                                                                          + "\" was removed.",
                                                                          MISSING_BINDING, false, false));

                continue;
            }

            for (Iterator oitr = b.getBindingOperations().iterator(); oitr.hasNext();) {
                BindingOperation op = (BindingOperation)oitr.next();

                BindingOperation op2 = b2.getBindingOperation(op.getName(), 
                                                              op.getBindingInput().getName(), 
                                                              op.getBindingOutput().getName());
                
                if (op2 == null) {
                    listener
                    .onEvent(new DifferenceEvent("Binding Operation \"" + op.getName()
                                                                          + "\" was removed from binding " +
                                                                          b.getQName(),
                                                                          MISSING_BINDING_OPERATION, false, false));

                }
            }
        }
    }
}
