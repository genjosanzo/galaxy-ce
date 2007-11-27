package org.mule.galaxy.view;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.Artifact;

public class CustomArtifactView implements ArtifactTypeView {
    private List<Column> columns = new ArrayList<Column>();

    
    public CustomArtifactView() {
        super();
        columns.add(new Column("Name", new ColumnEvaluator() {
            public Object getValue(Artifact artifact) {
                return artifact.getName();
            }
        }));
        
        columns.add(new Column("Version", new ColumnEvaluator() {
            public Object getValue(Artifact artifact) {
                return artifact.getLatestVersion().getVersionLabel();
            }
        }));
        
        columns.add(new Column("Lifecycle", new ColumnEvaluator() {
            public Object getValue(Artifact artifact) {
                if (artifact.getPhase() != null)
                    return artifact.getPhase().getName();
                
                return "";
            }
        }));
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String[] getColumnNames() {
        List<String> names = new ArrayList<String>();
        for (Column c : columns) {
            names.add(c.getName());
        }
        return names.toArray(new String[names.size()]);
    }

    public String getColumnValue(Artifact row, int column) {
        Column col = columns.get(column);
        if (col == null) {
            throw new RuntimeException("Invalid Column!");
        }
        
        Object value = col.getEvaluator().getValue(row);
        if (value == null) {
            return "";
        }
        
        return value.toString();
    }

    public ViewLink getLink(Artifact row, int column) {
        return null;
    }
    
}
