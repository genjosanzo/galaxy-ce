/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.spring.config;

import org.mule.galaxy.test.AbstractAtomTest;

import java.net.URL;

/**
 * TODO
 */
public class GalaxyApplicationContextTest extends AbstractAtomTest
{
    private GalaxyApplicationContext context;

    public void testMuleConfig() throws Exception
    {

        String configURL = "http://admin:admin@localhost:9002/api/registry?q=select artifact where spring.bean.id = 'TestObject1'";

//        context = new GalaxyApplicationContext(new URL(configURL));
//
//        //Assert beans
//        assertNotNull(context.getBean("TestBean1"));
//        assertNotNull(context.getBean("TestBean2"));

    }


    @Override
    protected void tearDown() throws Exception
    {
        try
        {
            if(context!=null)
            {
                context.destroy();
            }
        }
        finally
        {
            super.tearDown();
        }
    }
}