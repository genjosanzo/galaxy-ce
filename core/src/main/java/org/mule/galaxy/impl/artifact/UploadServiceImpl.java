package org.mule.galaxy.impl.artifact;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.uuid.UUID;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.GalaxyUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class UploadServiceImpl implements UploadService {
    private final class CleanCallback implements JcrCallback {
        public Object doInJcr(Session session) throws IOException, RepositoryException {
            try {
                Calendar now = GalaxyUtils.getCalendarForNow();
                now.add(Calendar.SECOND, -storagePeriod);
                
                ValueFactory factory = session.getValueFactory();
                Value dateValue = factory.createValue(now);

                String dateString = dateValue.getString();
                 
                QueryManager qm = session.getWorkspace().getQueryManager();
                QueryResult result = qm.createQuery("/jcr:root/uploads/*[@" +
                		DATE + " < xs:dateTime('" + dateString + "')]", Query.XPATH).execute();
                
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    nodes.nextNode().remove();
                }
                
                session.save();
            } catch (PathNotFoundException e) {
            } catch (ItemNotFoundException e) {
            }
            return null;
        }
    }

    protected static final String DATA = "data";
    protected static final String DATE = "date";
    
    protected String uploadNodeId;
    
    private JcrTemplate jcrTemplate;
    private Scheduler scheduler;
    private int storagePeriod = 60*60;
    
    /**
     * Start up a background thread to clean up deleted files.
     */
    public void initialize() throws Exception {
        JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node uploadNode = JcrUtil.getOrCreate(session.getRootNode(), "uploads", "galaxy:noSiblings");
                uploadNodeId = uploadNode.getUUID();
                session.save();
                return null;
            }
            
        });
        
        String jobName = "UploadService-cleaner";
        
        if (scheduler.getJobDetail(jobName, null) == null) {
            JobDetail job = new JobDetail(jobName, null, UploadServiceCleaner.class);
            job.setDurability(true);
            
            CronTrigger trigger = new CronTrigger(jobName, null, "0 0 * * * ?");
            trigger.setJobName(jobName);
            
            scheduler.scheduleJob(job, trigger);
        }
    }
    
    public String upload(final InputStream inputStream) {
        return (String) jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node uploads = session.getNodeByUUID(uploadNodeId);
                
                Node upload = uploads.addNode(UUID.randomUUID().toString());
                upload.addMixin("mix:referenceable");
                upload.setProperty(DATA , inputStream);
                upload.setProperty(DATE , GalaxyUtils.getCalendarForNow());
                session.save();
                return upload.getUUID();
            }
        });
    }

    public InputStream getFile(final String name) throws FileNotFoundException {
        InputStream is = (InputStream) jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Node upload = session.getNodeByUUID(name);
                    return upload.getProperty(DATA).getStream();
                } catch (PathNotFoundException e) {
                } catch (ItemNotFoundException e) {
                }
                // we didn't find that one
                return null;
            }
        });
        
        if (is == null) {
            throw new FileNotFoundException();
        }
        
        return is;
    }

    public void delete(final String name) {
        jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                try {
                    Node upload = session.getNodeByUUID(name);
                    upload.remove();
                    session.save();
                } catch (PathNotFoundException e) {
                } catch (ItemNotFoundException e) {
                }
                return null;
            }
        });
    }

    public void clean() {
        try {
            JcrUtil.doInTransaction(jcrTemplate.getSessionFactory(), new CleanCallback());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setStoragePeriod(int storagePeriod) {
        this.storagePeriod = storagePeriod;
    }
    
}
