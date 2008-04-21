package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.util.QNameListBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WIndex;

public class IndexForm extends AbstractComposite {

    private AdministrationPanel adminPanel;
    private WIndex index;
    private Button save;
    private TextBox mediaTypeTB;
    private TextArea xqueryExpressionTA;
    private TextBox propertyTB;
    private ListBox languageList;
    private TextBox xpathExpressionTB;
    private ListBox resultTypeLB;
    private QNameListBox docTypesLB;
    private FlexTable table;
    private TextBox descriptionTB;
    private TextArea groovyExpressionTA;
    
    public IndexForm(AdministrationPanel adminPanel, WIndex u) {
        this (adminPanel, u, false);
    }
    
    public IndexForm(AdministrationPanel adminPanel) {
        this (adminPanel, new WIndex(), true);
    }
    
    protected IndexForm(AdministrationPanel adminPanel, WIndex idx, boolean add){
        this.adminPanel = adminPanel;
        this.index = idx;
        
        FlowPanel panel = new FlowPanel();
        String title;
        if (add) {
            title = "Add index";
        } else {
            title = "Edit index: " + idx.getDescription();
        }
        
        table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Description:");
        table.setText(1, 0, "Result Type:");
        table.setText(2, 0, "Media Type:");
        table.setText(3, 0, "Document Types:");
        table.setText(4, 0, "Language:");
        
        descriptionTB = new TextBox();
        descriptionTB.setText(idx.getDescription());
        table.setWidget(0, 1, descriptionTB);
        
        resultTypeLB = new ListBox();
        resultTypeLB.addItem("String");
        resultTypeLB.addItem("QName");
        table.setWidget(1, 1, resultTypeLB);
        
        if (idx.getResultType() == null || idx.getResultType().equals("String")) {
            resultTypeLB.setSelectedIndex(0);
        } else {
            resultTypeLB.setSelectedIndex(1);
        }
        
        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(idx.getMediaType());
        table.setWidget(2, 1, mediaTypeTB);
        
        docTypesLB = new QNameListBox(idx.getDocumentTypes());

        table.setWidget(3, 1, docTypesLB);
        
        languageList = new ListBox();
        languageList.addItem("XPath");
        languageList.addItem("XQuery");
//        languageList.addItem("Groovy");

        String indexer = idx.getIndexer();
        if (indexer == null || indexer.equalsIgnoreCase("XPath")) {
            setupXPath();
        } else if (indexer.equalsIgnoreCase("Groovy")) {
            setupGroovy();
        }  else {
            setupXQuery();
        }
        
        languageList.addChangeListener(new ChangeListener() {

            public void onChange(Widget sender) {
                String value = languageList.getValue(languageList.getSelectedIndex());
                
                if (value.equalsIgnoreCase("XPath")) {
                    setupXPath();
                } else if (value.equalsIgnoreCase("Groovy")) {
                    setupGroovy();
                }  else {
                    setupXQuery();
                }
            }
            
        });
        
        table.setWidget(4, 1, languageList);

        styleHeaderColumn(table);
        
        initWidget(panel);
    }

    private void addSaveButton(int row) {
        save = new Button("Save");
        table.setWidget(row, 1, save);
        save.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                save();
            }
        });
    }

    protected void setupXQuery() {
        clearConfiguration();
        
        propertyTB = new TextBox();
        propertyTB.setText(index.getProperty());
        table.setText(5, 0, "Property Name:");
        table.setWidget(5, 1, propertyTB);
        
        xqueryExpressionTA = new TextArea();
        xqueryExpressionTA.setVisibleLines(15);
        xqueryExpressionTA.setCharacterWidth(80);
        
        languageList.setSelectedIndex(1);
        xqueryExpressionTA.setText(index.getExpression());
        table.setText(6, 0, "XQuery Expression:");
        table.setWidget(6, 1, xqueryExpressionTA);
        
        addSaveButton(7);
    }

    private void clearConfiguration() {
        for (int i = 5; i < table.getRowCount(); i++) {
            table.removeRow(i);
        }
    }

    protected void setupGroovy() {
        groovyExpressionTA = new TextArea();
        groovyExpressionTA.setVisibleLines(15);
        groovyExpressionTA.setCharacterWidth(80);
        xqueryExpressionTA.setText(index.getExpression());
        table.setText(5, 0, "Groovy Script:");
        table.setWidget(5, 1, xqueryExpressionTA);
        
        addSaveButton(6);
    }

    protected void setupXPath() {
        propertyTB = new TextBox();
        propertyTB.setText(index.getProperty());
        table.setText(5, 0, "Property Name:");
        table.setWidget(5, 1, propertyTB);

        xpathExpressionTB = new TextBox();
        xpathExpressionTB.setVisibleLength(80);
        
        languageList.setSelectedIndex(0);
        xpathExpressionTB.setText(index.getExpression());
        table.setText(6, 0, "XPath Expression:");
        table.setWidget(6, 1, xpathExpressionTB);

        addSaveButton(7);
    }

    protected void save() {
        save.setEnabled(false);
        save.setText("Saving...");
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        index.setProperty(propertyTB.getText());
        index.setMediaType(mediaTypeTB.getText());
        index.setDescription(descriptionTB.getText());
        index.setResultType(resultTypeLB.getValue(resultTypeLB.getSelectedIndex()));
        
        String language = languageList.getValue(languageList.getSelectedIndex()).toLowerCase();
        index.setIndexer(language);
        if ("xpath".equals(language)) {
            index.setExpression(xpathExpressionTB.getText());
        } else {
            index.setExpression(xqueryExpressionTA.getText());
        }
        // TODO Groovy index
        
        
        index.setDocumentTypes(docTypesLB.getItems());
        
        // validation
        
        if (!assertValid(index.getId(), "id")) return;
        
        if (index.getId().indexOf(' ') != -1) {
            adminPanel.setMessage("The index id cannot contain spaces.");
            reenable();
            return;
        }

        if (!assertValid(index.getDescription(), "name")) return;
        if (!assertValid(index.getExpression(), "expression")) return;

        svc.saveIndex(index, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("Index was not found! " + index.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                History.newItem("indexes");
                adminPanel.setMessage("Index " + index.getDescription() + " was saved.");
            }
            
        });
    }

    private boolean assertValid(String value, String name) {
        if (value == null || value.equals("") ) {
            adminPanel.setMessage("You must supply an index " + name + " .");
            reenable();
            return false;
        }
        return true;
    }

    private void reenable() {
        save.setEnabled(true);
        save.setText("Save");
    }

}
