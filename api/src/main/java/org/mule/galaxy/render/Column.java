package org.mule.galaxy.render;

public class Column {

    private String name;
    private ColumnEvaluator evaluator;
    private boolean summary = true;
    private boolean detail = true;
    
    public Column() {
        super();
    }

    public Column(String name, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    public Column(String name, boolean summary, boolean detail, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
        this.summary = summary;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ColumnEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summaryOnly) {
        this.summary = summaryOnly;
    }

    public boolean isDetail() {
        return detail;
    }

    public void setDetail(boolean detailOnly) {
        this.detail = detailOnly;
    }

}
