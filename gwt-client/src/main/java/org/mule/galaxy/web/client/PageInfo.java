package org.mule.galaxy.web.client;

public abstract class PageInfo {
    private AbstractComposite instance;
    private String name;
    private int tabIndex;
    
    public PageInfo(String name, int tabIndex) {
        super();
        this.name = name;
        this.tabIndex = tabIndex;
    }

    public PageInfo(String name) {
        this(name, -1);
    }

    public abstract void show();

    public abstract AbstractComposite createInstance();

    public final AbstractComposite getInstance() {
        if (instance != null) {
            return instance;
        }
        return (instance = createInstance());
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public String getName() {
        return name;
    }

}
