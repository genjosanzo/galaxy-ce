 package org.mule.galaxy.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class Query {
    List<Restriction> restrictions = new LinkedList<Restriction>();
    boolean searchLatestVersionOnly = true;
    List<Class> selectTypes;
    private String groupBy;
    private String fromId;
    private boolean fomRecursive;
    private String fromPath;
    private int start = -1;
    private int maxResults = Integer.MAX_VALUE;
    
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public Query() {
	super();
        this.selectTypes = Collections.emptyList();
    }
    
    public Query(OpRestriction restriction) {
        restrictions.add(restriction);
        this.selectTypes = Collections.emptyList();
    }
    
    public Query(OpRestriction restriction, 
	         Class... selectType) {
	this.selectTypes = Arrays.asList(selectType);
        restrictions.add(restriction);
    }
    
    public Query(Class... selectType) {
        this.selectTypes = Arrays.asList(selectType);
    }

    public Query add(Restriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public List<Class> getSelectTypes() {
        return selectTypes;
    }

    public void setSelectTypes(Class... selectType) {
	this.selectTypes = Arrays.asList(selectType);
    }
    
    public void setSelectTypes(List<Class> selectTypes) {
	this.selectTypes = selectTypes;
    }

    public Query orderBy(String field) {
        this.groupBy = field;
        return this;
    }
    
    public Query fromId(String workspace, boolean workspaceSearchRecursive) {
        this.fromId = workspace;
        this.fomRecursive = workspaceSearchRecursive;
        return this;
    }
    
    public Query fromId(String workspace) {
        this.fromId = workspace;
        return this;
    }
    
    public Query fromPath(String workspace, boolean workspaceSearchRecursive) {
        this.fromPath = workspace;
        this.fomRecursive = workspaceSearchRecursive;
        return this;
    }
    
    public Query fromPath(String workspace) {
        this.fromPath = workspace;
        return this;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getFromId() {
        return fromId;
    }

    public String getFromPath() {
        return fromPath;
    }

    public boolean isFromRecursive() {
        return fomRecursive;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("select ");
        
        if (selectTypes != null && selectTypes.size() > 0) {
            boolean first = true;
            for (Class t : selectTypes) {
                if (!first) {
            		sb.append(", ");
                }
                
                if (t.equals(Artifact.class)) {
                    sb.append("artifact");
                } else if (t.equals(ArtifactVersion.class)) {
                    sb.append("artifactVersion");
                } else if (t.equals(Entry.class)) {
                    sb.append("entry");
                } else if (t.equals(EntryVersion.class)) {
                    sb.append("entryVersion");
                } else {
                    sb.append("item");
                }
                
                first = false;
            }
            sb.append(" ");
        } else {
            sb.append("item ");
        }
        
        if (fromId != null) {
            sb.append("from '@")
                .append(fromId)
                .append("' ");
        }
        
        if (fromPath != null) {
            sb.append("from '")
              .append(fromPath)
              .append("' ");
        }
        
        if (fomRecursive) {
            sb.append("recursive ");
        }
        
        sb.append("where ");
        
        boolean first = true;
        
        for (Restriction r : restrictions) {
            if (first) first = false;
            else sb.append(" and ");
            
            r.toString(sb);
        }

        return sb.toString();
    }
    
    public static Query fromString(String queryString) throws QueryException {
        List<String> tokens = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < queryString.length(); i++) {
            char c = queryString.charAt(i);
            switch (c) {
            case ' ':
                if (start != i) {
                    tokens.add(queryString.substring(start, i));
                }
                start = i + 1;
                break;
            case ',':
                if (start != i) {
                    tokens.add(queryString.substring(start, i));
                }
                start = i + 1;
                break;
            case '!':
                if ('=' == queryString.charAt(i+1)) {
                    tokens.add("!=");
                    start = i+2;
                    i++;
                }
                break;
            case '=':
                 tokens.add("=");
                 start = i+1;
                 break;
            case '<':
                if (queryString.charAt(i+1) == '=') {
                    i++;
                    tokens.add("<=");
                } else {
                    tokens.add("<");
                }
                start = i+1;  
                break;
            }
        }
        
        if (start != queryString.length()) {
            tokens.add(queryString.substring(start));
        }

        Iterator<String> itr = tokens.iterator(); 
        if (!itr.hasNext()) {
            throw new QueryException(new Message("EMPTY_QUERY_STRING", BundleUtils.getBundle(Query.class)));
        }
        
        if (!itr.next().toLowerCase().equals("select")){
            throw new QueryException(new Message("EXPECTED_SELECT", BundleUtils.getBundle(Query.class)));
        }
        
        if (!itr.hasNext()) {
            throw new QueryException(new Message("EXPECTED_SELECT_TYPE", BundleUtils.getBundle(Query.class)));
        }
        
        List<Class> selectTypes = new ArrayList<Class>();
        String selectType;
        do {
            selectType = itr.next();
            
            if (selectType.equals("from") || selectType.equals("where")) {
        	break;
            }
            
            if (selectType.equals("artifact")) {
        	selectTypes.add(Artifact.class);
            } else if (selectType.equals("artifactVersion")) {
        	selectTypes.add(ArtifactVersion.class);
            } else if (selectType.equals("entry")) {
        	selectTypes.add(Entry.class);
            } else if (selectType.equals("entryVersion")) {
        	selectTypes.add(EntryVersion.class);
            } else if (selectType.equals("workspace")) {
        	selectTypes.add(Workspace.class);
            } else if (selectType.equals("*")) {
        	selectTypes.add(Item.class);
            } else {
                throw new QueryException(new Message("UNKNOWN_SELECT_TYPE", BundleUtils.getBundle(Query.class), selectType));
            }
        } while (itr.hasNext());
        
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
        q.setSelectTypes(selectTypes);
        
        if (!itr.hasNext()){
            return q;
        }
        
        String next = selectType;
        if ("from".equals(next.toLowerCase())) {
            if (!itr.hasNext()) throw new QueryException(new Message("EXPECTED_FROM", BundleUtils.getBundle(Query.class)));
            
            String workspace = dequote(itr.next(), itr);
            
            if (itr.hasNext()) {
                next = itr.next();

                boolean recursive = false;
                if (next.toLowerCase().equals("recursive")) {
                    recursive = true;
                    
                    if (itr.hasNext()) {
                        next = itr.next();
                    } else {
                        next = null;
                    }
                }
                
                if (workspace.startsWith("@")) {
                    q.fromId(workspace.substring(1), recursive);
                } else {
                    q.fromPath(workspace, recursive);
                }
            }
        }

        
        if (next != null && !next.toLowerCase().equals("where")) {
            throw new QueryException(new Message("EXPECTED_WHERE_BUT_FOUND", BundleUtils.getBundle(Query.class), next));
        }
        
        boolean firstRestriction = true;
        while (itr.hasNext()) {
            if (firstRestriction) {
                firstRestriction = false;
            } else {
                if (!itr.hasNext()) {
                    throw new QueryException(new Message("EXPECTED_AND", BundleUtils.getBundle(Query.class)));
                }
                String t = itr.next();
                if (!"and".equals(t.toLowerCase())) {
                    throw new QueryException(new Message("EXPECTED_AND", BundleUtils.getBundle(Query.class)));
                }
            }
            
            String left = itr.next();
            
            if (!itr.hasNext()) {
                throw new QueryException(new Message("EXPECTED_COMPARATOR", BundleUtils.getBundle(Query.class)));
            }
            
            String compare = itr.next();
            
            if (!itr.hasNext()) {
                throw new QueryException(new Message("EXPECTED_RIGHT", BundleUtils.getBundle(Query.class)));
            }
            
            OpRestriction r = null;
            if (compare.equals("=")) {
                r = OpRestriction.eq(left, dequote(itr.next(), itr));
            } else if (compare.equals("like")) {
                r = OpRestriction.like(left, dequote(itr.next(), itr));
            } else if (compare.equals("!=")) {
                r = OpRestriction.not(OpRestriction.eq(left, dequote(itr.next(), itr)));
            } else if (compare.equals("in")) {
                if (!itr.hasNext()) {
                    throw new QueryException(new Message("EXPECTED_IN_TOKEN", BundleUtils.getBundle(Query.class)));
                }
                
                ArrayList<String> in = new ArrayList<String>();
                String first = itr.next();
                boolean end = false;
                if (first.startsWith("(")) {
                    if (first.endsWith(")")) {
                        end = true;
                        first = first.substring(1, first.length() - 1);
                    } else {
                        first = first.substring(1);
                    }
                        
                    if (first.endsWith(",")) {
                        first = first.substring(0, first.length() - 1);
                    }
                    in.add(dequote(first, itr));
                } else {
                    throw new QueryException(new Message("EXPECTED_IN_LEFT_PARENS", BundleUtils.getBundle(Query.class), first));
                }
                
                while (!end && itr.hasNext()) {
                    String nextIn = itr.next();
                    if (nextIn.endsWith(")")) {
                        in.add(dequote(nextIn.substring(0, nextIn.length()-1), itr));
                        break;
                    } else {
                        in.add(dequote(nextIn, itr));
                    }
                }
                r = OpRestriction.in(left, in);
            } else {
                new QueryException(new Message("UNKNOWN_COMPARATOR", BundleUtils.getBundle(Query.class), left));
            }
            
            q.add(r);
        }
        return q;
    }
    private static String dequote(String s, Iterator itr) {
        if (s.startsWith("'")) {
            s = dequote2(s, "'", itr);
        } else if (s.startsWith("\"")) {
            s = dequote2(s, "\"", itr);
        }
        return s;
    }

    private static String dequote2(String s, String quote, Iterator itr) {
        if (!s.endsWith(quote)) {
            StringBuilder sb = new StringBuilder();
            sb.append(s.substring(1));
            while (itr.hasNext()) {
                sb.append(" ");
                String next = (String) itr.next();
                
                if (next.endsWith(quote)) {
                    sb.append(next.substring(0, next.length()-1));
                    break;
                } else {
                    sb.append(next);
                }
            }
            return sb.toString();
        } else {
            return s.substring(1, s.length()-1);
        }
    }



}
