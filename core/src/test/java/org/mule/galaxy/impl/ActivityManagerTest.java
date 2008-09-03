package org.mule.galaxy.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.mule.galaxy.activity.Activity;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ActivityManagerTest extends AbstractGalaxyTest {

    public void testAM() throws Exception {
        User user = new User();
        user.setUsername("dan");
        user.addGroup(accessControlManager.getGroupByName("Administrators"));
        userManager.create(user, "123");
        
        login("dan", "123");
        
        Date date1 = new Date();
        Thread.sleep(10);
        System.out.println("SAVING FIRST");

        activityManager.logActivity("Did stuff", EventType.INFO, user, null);

        Collection<Activity> activities = activityManager.getActivities(null, null, user.getId(), null, null, null, 0, 50, true);
        assertEquals(1, activities.size());

        activities = activityManager.getActivities(null, null, user.getId(), null, "stuff", null, 0, 50, true);
        assertEquals(1, activities.size());
        
        activities = activityManager.getActivities(null, null, user.getId(), null, null, null, 0, 50, true);
        assertEquals(1, activities.size());

        activityManager.logActivity("Did stuff2", EventType.WARNING, user, null);
        activities = activityManager.getActivities(null, null, user.getId(), null, null, EventType.INFO, 0, 50, true);
        assertEquals(1, activities.size());

        activities = activityManager.getActivities(null, null, user.getId(), null, null, EventType.INFO,
                                                   0, 50, true);
        assertEquals(1, activities.size());

        activities = activityManager.getActivities(date1, null, user.getId(), null, null, null, 0, 50, true);
        assertEquals(2, activities.size());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 2);
        Date date2 = c.getTime();

        activities = activityManager.getActivities(date2, null, user.getId(), null, null, null, 0, 50, true);
        assertEquals(0, activities.size());

        activities = activityManager.getActivities(date1, date2, user.getId(), null, null, null, 0, 50, true);
        assertEquals(2, activities.size());

        for (int i = 0; i < 10; i++) {
            activityManager.logActivity("Did stuff3", EventType.WARNING, user, null);
        }

        activities = activityManager.getActivities(null, null, user.getId(), null, null, null, 0, 4, true);
        assertEquals(4, activities.size());

        activities = activityManager.getActivities(null, null, user.getId(), null, null, null, 10, 4, true);
        assertEquals(2, activities.size());
    }

}
