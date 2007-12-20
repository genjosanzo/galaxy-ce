package org.mule.galaxy.query;

import java.util.LinkedList;
import java.util.List;

public class Query {
    List<Restriction> restrictions = new LinkedList<Restriction>();
    boolean searchLatestVersionOnly = true;
    Class<?> selectType;
    private String groupBy;
    
    public Query(Class selectType, Restriction restriction) {
        this.selectType = selectType;
        restrictions.add(restriction);
    }
    
    public Query(Class selectType) {
        this.selectType = selectType;
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

    public Query orderBy(String field) {
        this.groupBy = field;
        return this;
    }
}
