package org.mule.galaxy.view;

public class Column {

    private String name;
    private ColumnEvaluator evaluator;
    private boolean summaryOnly;
    
    public Column() {
        super();
    }

    public Column(String name, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    public Column(String name, boolean summaryOnly, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
        this.summaryOnly = summaryOnly;
    }

    public Column(String name, ViewLink link, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
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

    public boolean isSummaryOnly() {
        return summaryOnly;
    }

    public void setSummaryOnly(boolean summaryOnly) {
        this.summaryOnly = summaryOnly;
    }

}
