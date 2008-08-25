package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * User: mark
 * Date: Aug 25, 2008
 * Time: 11:24:44 AM
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 */
public class ScheduleForm extends AbstractAdministrationForm {
    public ScheduleForm(AdministrationPanel administrationPanel) {
        super(administrationPanel, "schedules", "Schedule was saved.", "Schedule was deleted.",
              "A Schedule with that name already exists");

    }

    protected void fetchItem(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void initializeItem(Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void initializeNewItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void addFields(FlexTable table) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
