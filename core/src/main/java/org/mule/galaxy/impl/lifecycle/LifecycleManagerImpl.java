package org.mule.galaxy.impl.lifecycle;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.lifecycle.TransitionException;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.LogUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LifecycleManagerImpl implements LifecycleManager {

    private Logger LOGGER = LogUtils.getL7dLogger(LifecycleManagerImpl.class);

    private List<String> lifecycleDocuments = new ArrayList<String>();
    private Map<String,Lifecycle> lifecycles = new ConcurrentHashMap<String, Lifecycle>();
    private static final String DEFAULT_LIFECYCLE = "Default";
    
    public Lifecycle getDefaultLifecycle() {
        return lifecycles.get(DEFAULT_LIFECYCLE);
    }

    public Lifecycle getLifecycle(Workspace workspace) {
        return getDefaultLifecycle();
    }

    public Collection<Lifecycle> getLifecycles() {
        return Collections.unmodifiableCollection(lifecycles.values());
    }

    public void initialize() throws Exception {
        Enumeration<URL> lifecycleUrls = getClass().getClassLoader().getResources("META-INF/galaxy-lifecycles.xml");

        while(lifecycleUrls.hasMoreElements()) {
            URL url = lifecycleUrls.nextElement();
            
            LOGGER.info("Loading lifecycles from " + url.toString());
            
            Map<String, Lifecycle> ls = buildLifecycle(url.openStream());
            lifecycles.putAll(ls);
        }
    }

    private Map<String, Lifecycle> buildLifecycle(InputStream is) throws Exception {
        Document doc = DOMUtils.readXml(is);
        Element root = doc.getDocumentElement();
        
        Map<String, Lifecycle> lifecycles = new HashMap<String, Lifecycle>(); 
        Element lifecycleEl = (Element) DOMUtils.getChild(root, "lifecycle");
        while (lifecycleEl != null) {
            String name = lifecycleEl.getAttribute("name");
            
            Lifecycle l = new Lifecycle();
            l.setName(name);
            lifecycles.put(name, l);
            
            HashMap<String, Phase> phases = new HashMap<String, Phase>();
            l.setPhases(phases);
            
            Element phaseEl = (Element) DOMUtils.getChild(lifecycleEl, "phase");
            while (phaseEl != null) {
                String phaseName = phaseEl.getAttribute("name");
                Phase phase = new Phase(l);
                phase.setName(phaseName);
                phases.put(phaseName, phase);
                
                phaseEl = (Element) DOMUtils.getNext(phaseEl);
            }
            
            // second pass to link phases
            phaseEl = (Element) DOMUtils.getChild(lifecycleEl, "phase");
            while (phaseEl != null) {
                String phaseName = phaseEl.getAttribute("name");
                String nextPhasesStr = phaseEl.getAttribute("nextPhases");
                Phase p = phases.get(phaseName);
                
                if (nextPhasesStr != null && !"".equals(nextPhasesStr)) {
                    StringTokenizer st = new StringTokenizer(nextPhasesStr, ",");
                    while (st.hasMoreTokens()) {
                        String nextPhaseName = st.nextToken().trim();
                        Phase nextPhase = phases.get(nextPhaseName);
                        
                        if (nextPhase == null) {
                            throw new Exception("Phase " + nextPhaseName + 
                                                " is not a valid transition in phase " +
                                                nextPhase + " in lifecycle " + name);
                        }
                        
                        p.getNextPhases().add(nextPhase);
                    }
                }
                phaseEl = (Element) DOMUtils.getNext(phaseEl);
            }
            
            // Set up initial phases
            String initialPhases = lifecycleEl.getAttribute("initialPhases");
            if (initialPhases != null && !"".equals(initialPhases)) {
                StringTokenizer st = new StringTokenizer(initialPhases, ",");
                while (st.hasMoreTokens()) {
                    String nextPhaseName = st.nextToken().trim();
                    Phase nextPhase = phases.get(nextPhaseName);
                    
                    if (nextPhase == null) {
                        throw new Exception("Phase " + nextPhaseName + 
                                            " is not a valid initial phase in lifecycle " + name);
                    }
                    
                    l.getInitialPhases().add(nextPhase);
                }
            } else {
                throw new Exception("Lifecycle " + name + " must have at least one initial phase!");
            }
            
            
            lifecycleEl = (Element) DOMUtils.getNext(lifecycleEl, "lifecycle", Node.ELEMENT_NODE);
        }
        return lifecycles;
    }

    public Lifecycle getLifecycle(String lifecycleName) {
        return lifecycles.get(lifecycleName);
    }

    public boolean isTransitionAllowed(Artifact a, Phase p2) {
        Phase p = a.getPhase();
        Lifecycle l = p2.getLifecycle();
        
        if (p == null) {
            return l.getInitialPhases().contains(p2);
        } else {
            return p != null && p.getNextPhases() != null && p.getNextPhases().contains(p2);
        }
    }
    
    public void transition(Artifact a, Phase p) throws TransitionException {
        if (!isTransitionAllowed(a, p)) {
            throw new TransitionException(p);
        }
        
        JcrArtifact ja =(JcrArtifact) a;
        
        ja.setPhase(p);
    }

    public List<String> getLifecycleDocuments() {
        return lifecycleDocuments;
    }

    public void setLifecycleDocuments(List<String> lifecycleDocuments) {
        this.lifecycleDocuments = lifecycleDocuments;
    }
    
}
