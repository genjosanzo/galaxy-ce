package org.mule.galaxy.atom;

import java.util.Collection;

import org.apache.abdera.protocol.Resolver;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetBuilder;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.WorkspaceManager;
import org.apache.abdera.protocol.server.impl.AbstractProvider;
import org.apache.abdera.protocol.server.impl.DefaultWorkspaceManager;

public class ServiceProvider extends AbstractProvider {

    private WorkspaceManager workspaceManager;
    private Resolver<Target> targetResolver;
    
    public ServiceProvider() {
    }

    @Override
    protected TargetBuilder getTargetBuilder(RequestContext request) {
        return null;
    }

    @Override
    protected Resolver<Target> getTargetResolver(RequestContext request) {
        return targetResolver;
    }

    @Override
    protected WorkspaceManager getWorkspaceManager(RequestContext request) {
        return workspaceManager;
    }

    public Resolver<Target> getTargetResolver() {
        return targetResolver;
    }

    public void setTargetResolver(Resolver<Target> targetResolver) {
        this.targetResolver = targetResolver;
    }

    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    public void setWorkspaceManager(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

}
