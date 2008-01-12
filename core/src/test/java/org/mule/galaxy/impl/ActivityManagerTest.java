package org.mule.galaxy.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.mule.galaxy.Activity;
import org.mule.galaxy.ActivityManager;
import org.mule.galaxy.ActivityManager.EventType;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ActivityManagerTest extends AbstractGalaxyTest {
    
    public void testAM() throws Exception {
        Date date1 = new Date();
        Thread.sleep(10);
        activityManager.logActivity(getAdmin(), "Did stuff", EventType.INFO);
       
       Collection<Activity> activities = activityManager.getActivities(null, null, null, null, 0, 50, true);
       assertEquals(1, activities.size());
       
       activities = activityManager.getActivities(null, null, getAdmin().getId(), null, 0, 50, true);
       assertEquals(1, activities.size());
       
       activityManager.logActivity(getAdmin(), "Did stuff2", EventType.WARNING);
       activities = activityManager.getActivities(null, null, null, EventType.INFO, 0, 50, true);
       assertEquals(1, activities.size());

       activities = activityManager.getActivities(null, null, getAdmin().getId(), EventType.INFO, 0, 50, true);
       assertEquals(1, activities.size());
       
       activities = activityManager.getActivities(date1, null, null, null, 0, 50, true);
       assertEquals(2, activities.size());

       Calendar c = Calendar.getInstance();
       c.setTime(new Date());
       c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 2);
       Date date2 = c.getTime();

       activities = activityManager.getActivities(date2, null, null, null, 0, 50, true);
       assertEquals(0, activities.size());

       activities = activityManager.getActivities(date1, date2, null, null, 0, 50, true);
       assertEquals(2, activities.size());
       
       for (int i = 0; i < 10; i++) {
           activityManager.logActivity(getAdmin(), "Did stuff3", EventType.WARNING);
       }
       
       activities = activityManager.getActivities(null, null, null, null, 0, 4, true);
       assertEquals(4, activities.size());

       activities = activityManager.getActivities(null, null, null, null, 10, 4, true);
       assertEquals(2, activities.size());
    }
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
