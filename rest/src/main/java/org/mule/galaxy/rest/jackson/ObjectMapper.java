package org.mule.galaxy.rest.jackson;

import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class ObjectMapper extends org.codehaus.jackson.map.ObjectMapper {

    public ObjectMapper() {
        super();
        getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
    }

}
