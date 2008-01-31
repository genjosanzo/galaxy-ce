/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api;

import org.mule.galaxy.api.security.User;

import java.io.Serializable;
import java.util.Calendar;

/**
 * TODO
 */

public class Activity implements Serializable, Identifiable
{
    private String id;
    private User user;
    private ActivityManager.EventType eventType;
    private Calendar date;
    private String message;

    public Activity(User user, ActivityManager.EventType eventType, Calendar date, String message)
    {
        super();
        this.user = user;
        this.eventType = eventType;
        this.date = date;
        this.message = message;
    }

    public Activity()
    {
        super();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public ActivityManager.EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(ActivityManager.EventType eventType)
    {
        this.eventType = eventType;
    }

    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar date)
    {
        this.date = date;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

}
