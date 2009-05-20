package org.mule.galaxy.impl.artifact;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UploadServiceCleaner implements Job, ApplicationContextAware {
    
    public static final String UPLOAD_SERVICE = "uploadService";
    private transient ApplicationContext context;

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        UploadService svc = (UploadService)context.getBean(UPLOAD_SERVICE);
        
        svc.clean();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
