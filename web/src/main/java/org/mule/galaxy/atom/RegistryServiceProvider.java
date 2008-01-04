package org.mule.galaxy.atom;

import org.apache.abdera.protocol.Request;
import org.apache.abdera.protocol.server.CollectionProvider;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.WorkspaceInfo;
import org.apache.abdera.protocol.server.impl.DefaultTarget;
import org.apache.abdera.protocol.server.impl.ServiceProvider;

public class RegistryServiceProvider extends ServiceProvider {

    private CollectionProvider artifactVersionProvider;
    
    @Override
    public Target resolve(Request request) {
        RequestContext ctx = (RequestContext) request;
        Target t = super.resolve(request);
        
        if (ctx.getTargetPath().endsWith(".atom")) {
            return new DefaultTarget(TargetType.TYPE_ENTRY, ctx);
        } else {
            if (t.getType().equals(TargetType.TYPE_MEDIA)) {
                if ("history".equals(ctx.getParameter("view"))) {
                    ctx.setAttribute(Scope.REQUEST, 
                                     COLLECTION_PROVIDER_ATTRIBUTE, 
                                     artifactVersionProvider);
                    
                    return new DefaultTarget(TargetType.TYPE_COLLECTION, ctx);
                }
            }
            
            return t;
        }
        
        
    }

    public CollectionProvider getArtifactVersionProvider() {
        return artifactVersionProvider;
    }

    public void setArtifactVersionProvider(CollectionProvider artifactVersionProvider) {
        this.artifactVersionProvider = artifactVersionProvider;
    }

}
