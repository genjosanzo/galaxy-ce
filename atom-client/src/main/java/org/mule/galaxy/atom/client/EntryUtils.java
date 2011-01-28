package org.mule.galaxy.atom.client;

import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.i18n.text.CharUtils.Profile;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.mule.galaxy.Registry;
import org.mule.galaxy.workspace.WorkspaceManager;

public class EntryUtils {

    public static String getElementAttribute(Entry entry, String elName, String attName, String defaultValue) {
        Element element = entry.getExtension(new QName(AtomWorkspaceManager.NAMESPACE, elName));
        if (element != null) {
            String val = element.getAttributeValue(attName);
            if (val != null) {
                return val;
            }
        }
        return defaultValue;
    }

    public static Calendar getElementAttributeAsCalendar(Entry entry, String elName, String attName, Calendar defaultValue) {
        Element element = entry.getExtension(new QName(AtomWorkspaceManager.NAMESPACE, elName));
        if (element == null) {
            return defaultValue;
        }
        
        String val = element.getAttributeValue(attName);
        if (val == null) {
            return defaultValue;
        }
        
        Date parse = AtomDate.parse(val);
        Calendar c = Calendar.getInstance();
        c.setTime(parse);
        return c;
    }

    public static void setElementAttribute(Entry entry, String elName, String attName, String value) {
        // TODO Auto-generated method stub
        
    }

    public static String getId(WorkspaceManager workspaceManager, String path) {
        return workspaceManager.getId() + Registry.WORKSPACE_MANAGER_SEPARATOR + 
            UrlEncoding.encode(path, Profile.PATHNODELIMS.filter());
    }
}
