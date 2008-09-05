 package org.mule.galaxy.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.query.OpRestriction.Operator;
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
    
    public Query fromPath(String path) {
        this.fromPath = path;
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
        Stack<String> tokens = new Stack<String>();
        int start = 0;
        for (int i = 0; i < queryString.length(); i++) {
            char c = queryString.charAt(i);
            switch (c) {
            case ' ':
            case ',':
                if (start != i) {
                    tokens.add(0, queryString.substring(start, i));
                }
                start = i + 1;
                break;
            case '!':
                if ('=' == queryString.charAt(i+1)) {
                    tokens.add(0, "!=");
                    start = i+2;
                    i++;
                }
                break;
            case '=':
            case '(':
            case ')':
        	 if (start != i) {
                     tokens.add(0, queryString.substring(start, i));
                 }
                 tokens.add(0, new String(new char[] { c } ));
                 start = i+1;
                 break;
            case '<':
                if (queryString.charAt(i+1) == '=') {
                    i++;
                    tokens.add(0, "<=");
                } else {
                    tokens.add(0, "<");
                }
                start = i+1;  
                break;
            }
        }
        
        if (start != queryString.length()) {
            tokens.add(0, queryString.substring(start));
        }

        if (tokens.isEmpty()) {
            throw new QueryException(new Message("EMPTY_QUERY_STRING", BundleUtils.getBundle(Query.class)));
        }
        
        if (!tokens.pop().toLowerCase().equals("select")){
            throw new QueryException(new Message("EXPECTED_SELECT", BundleUtils.getBundle(Query.class)));
        }
        
        if (tokens.isEmpty()) {
            throw new QueryException(new Message("EXPECTED_SELECT_TYPE", BundleUtils.getBundle(Query.class)));
        }
        
        List<Class> selectTypes = new ArrayList<Class>();
        String selectType;
        do {
            selectType = tokens.pop();
            
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
        } while (!tokens.isEmpty());
        
        org.mule.galaxy.query.Query q = new org.mule.galaxy.query.Query();
        q.setSelectTypes(selectTypes);
        
        if (tokens.isEmpty()){
            return q;
        }
        
        String next = selectType;
        if ("from".equals(next.toLowerCase())) {
            if (tokens.isEmpty()) throw new QueryException(new Message("EXPECTED_FROM", BundleUtils.getBundle(Query.class)));
            
            String workspace = dequote(tokens.pop(), tokens);
            
            boolean recursive = false;
            if (!tokens.isEmpty()) {
                next = tokens.pop();

                if (next.toLowerCase().equals("recursive")) {
                    recursive = true;
                    
                    if (!tokens.isEmpty()) {
                        next = tokens.pop();
                    } else {
                        next = null;
                    }
                }
            } else {
                next = null;
            }
            
            if (workspace.startsWith("@")) {
                q.fromId(workspace.substring(1), recursive);
            } else {
                q.fromPath(workspace, recursive);
            }
        }

        
        if (next != null && !next.toLowerCase().equals("where")) {
            throw new QueryException(new Message("EXPECTED_WHERE_BUT_FOUND", BundleUtils.getBundle(Query.class), next));
        }
        
	if (tokens.isEmpty()) {
	    return q;
	}
	
        OpRestriction r = handleSubClause(tokens, new DepthHolder());
        
        if (!tokens.isEmpty()) {
            r = getFollowOnClauses(r, tokens, new DepthHolder());
        }
        
	append(q, r);

        return q;
    }

    /**
     * Optimization to avoid unnecessary parentheses when serializing
     * @param q
     * @param r2
     */
    private static void append(org.mule.galaxy.query.Query q,
	    OpRestriction r2) {
	if (r2.getOperator().equals(Operator.AND)) {
	    q.add((OpRestriction) r2.getLeft());
	    append(q, (OpRestriction) r2.getRight());
	} else {
	    q.add(r2);
	}
    }

    private static String getJoin(Stack<String> tokens) throws QueryException {
	String join = tokens.pop();
	if (!"and".equals(join.toLowerCase()) && !"or".equals(join.toLowerCase())) {
	    throw new QueryException(new Message("EXPECTED_AND", BundleUtils.getBundle(Query.class)));
	}
	return join;
    }

    private static OpRestriction handleSubClause(Stack<String> tokens, DepthHolder holder) throws QueryException {
	boolean firstRestriction = true;
	boolean parens = false;
	
	String firstToken = tokens.peek();
	if (firstToken.equals("(")) {
	    tokens.pop();
            parens = true;
            holder.depth++;
	}

        if (!parens) {
            return buildRestriction(tokens, holder);
        }
	
        while (!tokens.isEmpty()) {
            if (firstRestriction) {
                firstRestriction = false;
            } else {
                getJoin(tokens);
            }
            
            OpRestriction r1 = handleSubClause(tokens, holder);
            
            if (tokens.isEmpty()) {
        	return r1;
            } else {
        	return getFollowOnClauses(r1, tokens, holder);
            }
        }
        return null;
    }

    private static OpRestriction getFollowOnClauses(OpRestriction r1,
	    Stack<String> tokens, DepthHolder holder) throws QueryException {
	Stack<Object[]> rightside = new Stack<Object[]>();
	rightside.add(new Object[] { null, r1 });
	int depth = holder.depth;
	while (depth <= holder.depth) {
	    String join = getJoin(tokens);
	    OpRestriction r2 = handleSubClause(tokens, holder);

	    rightside.add(new Object[] { join, r2 });

	    if (!tokens.isEmpty()) {
		String next = tokens.peek();
		while (next.equals(")")) {
		    holder.depth--;
		    tokens.pop();
		    
		    if (tokens.isEmpty()) {
			break;
		    }
		    next = tokens.peek();
		}
	    }

	    if (tokens.isEmpty()) {
		holder.depth--;
	    }
	}
	
	Object[] joinRestriction = rightside.pop();
	OpRestriction rightRestriction = (OpRestriction) joinRestriction[1];
	
	while (!rightside.isEmpty()) {
	    String join = (String) joinRestriction[0];
	    joinRestriction = rightside.pop();
	    OpRestriction leftRestriction = (OpRestriction) joinRestriction[1];

	    if ("or".equals(join)) {
		rightRestriction = OpRestriction.or(leftRestriction,
			rightRestriction);
	    } else {
		rightRestriction = OpRestriction.and(leftRestriction,
			rightRestriction);
	    }
	}
	
	return rightRestriction;
    }
  
    private static class DepthHolder {
	public int depth = 0;
    }
    
    private static OpRestriction buildRestriction(Stack<String> tokens, DepthHolder holder) throws QueryException {
	String left = tokens.pop();
	
	if (tokens.isEmpty()) {
	    throw new QueryException(new Message("EXPECTED_COMPARATOR", BundleUtils.getBundle(Query.class)));
	}
	
	String compare = tokens.pop();
	
	if (tokens.isEmpty()) {
	    throw new QueryException(new Message("EXPECTED_RIGHT", BundleUtils.getBundle(Query.class)));
	}
	
	OpRestriction r = null;
	String right = tokens.pop();

	if (compare.equals("=")) {
	    r = OpRestriction.eq(left, dequote(right, tokens));
	} else if (compare.equals("like")) {
	    r = OpRestriction.like(left, dequote(right, tokens));
	} else if (compare.equals("!=")) {
	    r = OpRestriction.not(OpRestriction.eq(left, dequote(right, tokens)));
	} else if (compare.equals("in")) {
	    ArrayList<String> in = new ArrayList<String>();
	    String first = right;

	    if (!first.equals("(")) {
		throw new QueryException(new Message("EXPECTED_IN_LEFT_PARENS", BundleUtils.getBundle(Query.class), first));
	    }
	    
	    boolean end = false;
	    while (!end && !tokens.isEmpty()) {
	        String nextIn = tokens.pop();
	        if (nextIn.equals(")")) {
	            end = true;
	            break;
	        } else {
	            in.add(dequote(nextIn, tokens));
	        }
	    }
	    r = OpRestriction.in(left, in);
	} else {
	    new QueryException(new Message("UNKNOWN_COMPARATOR", BundleUtils.getBundle(Query.class), left));
	}
	return r;
    }
    private static String dequote(String s, Stack<String> tokens) {
        if (s.startsWith("'")) {
            s = dequote2(s, "'", tokens);
        } else if (s.startsWith("\"")) {
            s = dequote2(s, "\"", tokens);
        }
        return s;
    }

    private static String dequote2(String s, String quote, Stack<String> tokens) {
        if (!s.endsWith(quote)) {
            StringBuilder sb = new StringBuilder();
            sb.append(s.substring(1));
            while (!tokens.isEmpty()) {
                sb.append(" ");
                String next = tokens.pop();
                
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
