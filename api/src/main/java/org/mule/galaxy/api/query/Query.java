/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api.query;

import java.util.List;

/**
 * TODO
 */

public interface Query
{
    int getStart();

    void setStart(int start);

    int getMaxResults();

    void setMaxResults(int maxResults);

    Query add(Restriction restriction);

    List<Restriction> getRestrictions();

    Class<?> getSelectType();

    void setSelectType(Class<?> selectType);

    Query orderBy(String field);

    Query workspaceId(String workspace, boolean searchWorkspaceChildren);

    Query workspaceId(String workspace);

    Query workspacePath(String workspace, boolean searchWorkspaceChildren);

    Query workspacePath(String workspace);

    String getGroupBy();

    String getWorkspaceId();

    String getWorkspacePath();

    boolean isSearchWorkspaceChildren();
}
