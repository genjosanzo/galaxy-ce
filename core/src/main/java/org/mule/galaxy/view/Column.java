package org.mule.galaxy.view;

public class Column {

    private String name;
    private ColumnEvaluator evaluator;
    
    public Column() {
        super();
    }

    public Column(String name, ColumnEvaluator evaluator) {
        this.name = name;
        this.evaluator = evaluator;
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

}
