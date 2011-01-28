package org.mule.galaxy.wsdl.diff.rule;

import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.wsdl.diff.DifferenceListener;

import com.sun.xml.xsom.parser.SchemaDocument;

public interface Rule {

    void check(Definition original, 
               Definition newWsdl, 
               Set<SchemaDocument> originalSchemas,
               Set<SchemaDocument> newSchemas, 
               DifferenceListener listener);

}
