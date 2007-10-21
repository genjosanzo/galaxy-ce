package org.mule.galaxy.query;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Query {
    List<Restriction> restrictions = new LinkedList<Restriction>();
    boolean searchLatestVersionOnly = true;
    Class<?> selectType;
    
    public Query(Class selectType, Restriction restriction) {
        this.selectType = selectType;
        restrictions.add(restriction);
    }
    
    public Query add(Restriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public Class<?> getSelectType() {
        return selectType;
    }

    public void setSelectType(Class<?> selectType) {
        this.selectType = selectType;
    }

}
