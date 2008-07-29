package org.mule.galaxy.impl.render;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.render.ArtifactRenderer;
import org.mule.galaxy.render.Column;
import org.mule.galaxy.render.ColumnEvaluator;

public class CustomArtifactRenderer implements ArtifactRenderer {
    private List<Column> columns = new ArrayList<Column>();

    public CustomArtifactRenderer() {
        super();
        columns.add(new Column("Name", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact) artifact).getName();
            }
        }));
        
        columns.add(new Column("Workspace", false, true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact) artifact).getParent().getPath();
            }
        }));

        columns.add(new Column("Media Type", false, true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact) artifact).getContentType();
            }
        }));
        columns.add(new Column("Version", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact) artifact).getDefaultOrLastVersion().getVersionLabel();
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

    public boolean isSummary(int column) {
        Column col = columns.get(column);
        
        return col.isSummary();
    }

    public boolean isDetail(int column) {
        Column col = columns.get(column);
        
        return col.isDetail();
    }
    
}
