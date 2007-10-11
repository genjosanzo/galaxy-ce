package org.mule.galaxy.wsdl.diff.rule;

import com.sun.xml.xsom.parser.SchemaDocument;

import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.wsdl.diff.DifferenceListener;

public interface Rule {

    void check(Definition original, 
               Definition newWsdl, 
               Set<SchemaDocument> originalSchemas,
               Set<SchemaDocument> newSchemas, 
               DifferenceListener listener);

}
