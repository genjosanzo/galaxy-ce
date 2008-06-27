/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.netboot;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * TODO
 */
public class NetbootConfig
{

    private static final String NL = System.getProperty("line.separator");

    private String httpScheme;
    private String host;
    private int port;
    private String apiUrl;
    private String username;
    private String password;
    private String[] workspaces;
    private String netBootWorkspace;
    private boolean debug;
    private boolean clean;
    private String muleHome;

    private String startupArgs[];

    public NetbootConfig(String[] args)
    {
        startupArgs = args;
    }

    public NetbootConfig(String[] args, Properties properties)
    {
        this(args);
        setHttpScheme("http"); // TODO https is not yet supported
        setHost(properties.getProperty("galaxy.host", "localhost"));
        setPort(Integer.valueOf(properties.getProperty("galaxy.port", "8080")));
        setApiUrl(properties.getProperty("galaxy.apiUrl", "/api/registry"));
        setUsername(properties.getProperty("galaxy.username", "admin"));
        setPassword(properties.getProperty("galaxy.password", "admin"));

        setNetBootWorkspace(properties.getProperty("galaxy.netboot.workspace", "Mule"));
        setDebug(Boolean.valueOf(properties.getProperty("galaxy.debug", "false")).booleanValue());
        setClean(Boolean.valueOf(properties.getProperty("galaxy.clean", "false")).booleanValue());

        setMuleHome(lookupMuleHome());

        String s = properties.getProperty("galaxy.app.workspaces");
        if (s != null)
        {
            setWorkspacesAsString(s);
        }
    }

    private String lookupMuleHome()
    {
        File muleHome = null;
        String muleHomeVar = System.getProperty("mule.home");

        if (muleHomeVar != null && !muleHomeVar.trim().equals("") && !muleHomeVar.equals("%MULE_HOME%"))
        {
            try
            {
                muleHome = new File(muleHomeVar).getCanonicalFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (muleHome == null || !muleHome.exists() || !muleHome.isDirectory())
        {
            System.err.println("Either MULE_HOME is not set or does not contain a valid directory.");
            return null;
        }
        return muleHome.getAbsolutePath();
    }


    public boolean isFullyConfigured()
    {
        try
        {
            validate();
        }
        catch (IllegalStateException e)
        {
            return false;
        }
        return true;
    }

    public void validate()
    {
        StringBuffer buf = new StringBuffer();
        if (muleHome == null)
        {
            buf.append("Mule Home (mule.home) must be set" + NL);
        }
        if (host == null)
        {
            buf.append("A hostname must be set" + NL);
        }
        if (port == 0)
        {
            buf.append("A valid port number must be set" + NL);
        }
        if (username == null)
        {
            buf.append("A valid user name must be set" + NL);
        }
        if (password == null)
        {
            buf.append("A password must be set for the user" + NL);
        }
        if (netBootWorkspace == null)
        {
            buf.append("A netboot workspace must be set" + NL);
        }
        if (workspaces == null || workspaces.length == 0)
        {
            buf.append("You must set at least one configuration workspace" + NL);
        }

        if (buf.length() > 0)
        {
            throw new IllegalStateException("Netboot config not valid:" + NL + buf.toString());
        }
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl)
    {
        this.apiUrl = apiUrl;
    }

    public boolean isClean()
    {
        return clean;
    }

    public void setClean(boolean clean)
    {
        this.clean = clean;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHttpScheme()
    {
        return httpScheme;
    }

    public void setHttpScheme(String httpScheme)
    {
        this.httpScheme = httpScheme;
    }

    public String getMuleHome()
    {
        return muleHome;
    }

    public void setMuleHome(String muleHome)
    {
        this.muleHome = muleHome;
    }

    public String getNetBootWorkspace()
    {
        return netBootWorkspace;
    }

    public void setNetBootWorkspace(String netBootWorkspace)
    {
        this.netBootWorkspace = netBootWorkspace;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String[] getWorkspaces()
    {
        return workspaces;
    }

    public void setWorkspaces(String[] workspaces)
    {
        this.workspaces = workspaces;
    }

    public String getWorkspacesAsString()
    {
        if (workspaces == null)
        {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < workspaces.length; i++)
        {
            buf.append(workspaces[i]).append(",");

        }
        return buf.toString().substring(0, buf.length() - 1);
    }

    public void setWorkspacesAsString(String workspaces)
    {
        StringTokenizer tokenizer = new StringTokenizer(workspaces, ",");
        // filter duplicates
        Set ws = new LinkedHashSet();
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
        {
            ws.add(tokenizer.nextToken().trim());
        }

        this.workspaces = (String[]) ws.toArray(new String[ws.size()]);
    }

    public String[] getStartupArgs()
    {
        return startupArgs;
    }
}
