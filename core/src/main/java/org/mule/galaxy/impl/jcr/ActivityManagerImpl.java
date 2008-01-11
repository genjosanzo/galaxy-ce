package org.mule.galaxy.impl.jcr;

import java.util.Collection;
import java.util.Date;

import org.mule.galaxy.Activity;
import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class ActivityManagerImpl extends AbstractReflectionDao<Activity> implements ActivityManager {

    public ActivityManagerImpl() throws Exception {
        super(Activity.class, "activities", true);
    }

    public Collection<Activity> getActivities(Date from, Date to, String user, EventType eventType) {
        // TODO Auto-generated method stub
        return null;
    }

    public void logActivity(String user, String activity, EventType eventType) {
        // TODO Auto-generated method stub
        
    }
    
}
