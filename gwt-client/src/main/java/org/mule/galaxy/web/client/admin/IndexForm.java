package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.util.QNameListBox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WIndex;

public class IndexForm extends AbstractAdministrationForm {

    private WIndex index;
    private TextBox mediaTypeTB;
    private TextArea xqueryExpressionTA;
    private TextBox propertyTB;
    private ListBox languageList;
    private TextBox xpathExpressionTB;
    private ListBox resultTypeLB;
    private QNameListBox docTypesLB;
    private TextBox descriptionTB;
    private TextArea groovyExpressionTA;
    
    public IndexForm(AdministrationPanel adminPanel) {
        super(adminPanel, "indexes", "Index was saved.", "Index was deleted.");
    }
    
    protected void addFields(final FlexTable table) {
        table.setText(0, 0, "Description:");
        table.setText(1, 0, "Result Type:");
        table.setText(2, 0, "Media Type:");
        table.setText(3, 0, "Document Types:");
        table.setText(4, 0, "Language:");
        
        descriptionTB = new TextBox();
        descriptionTB.setText(index.getDescription());
        table.setWidget(0, 1, descriptionTB);
        
        resultTypeLB = new ListBox();
        resultTypeLB.addItem("String");
        resultTypeLB.addItem("QName");
        table.setWidget(1, 1, resultTypeLB);
        
        if (index.getResultType() == null || index.getResultType().equals("String")) {
            resultTypeLB.setSelectedIndex(0);
        } else {
            resultTypeLB.setSelectedIndex(1);
        }
        
        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(index.getMediaType());
        table.setWidget(2, 1, mediaTypeTB);
        
        docTypesLB = new QNameListBox(index.getDocumentTypes());

        table.setWidget(3, 1, docTypesLB);
        
        languageList = new ListBox();
        languageList.addItem("XPath");
        languageList.addItem("XQuery");
//        languageList.addItem("Groovy");

        String indexer = index.getIndexer();
        if (indexer == null || indexer.equalsIgnoreCase("XPath")) {
            setupXPath(table);
        } else if (indexer.equalsIgnoreCase("Groovy")) {
            setupGroovy(table);
        }  else {
            setupXQuery(table);
        }
        
        languageList.addChangeListener(new ChangeListener() {

            public void onChange(Widget sender) {
                String value = languageList.getValue(languageList.getSelectedIndex());
                
                if (value.equalsIgnoreCase("XPath")) {
                    setupXPath(table);
                } else if (value.equalsIgnoreCase("Groovy")) {
                    setupGroovy(table);
                }  else {
                    setupXQuery(table);
                }
            }
            
        });
        
        table.setWidget(4, 1, languageList);

        styleHeaderColumn(table);
    }


    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getIndex(id, getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return "Add index";
        } else {
            return "Edit index: " + index.getDescription();
        }
    }

    protected void initializeItem(Object o) {
        this.index = (WIndex) o;
    }

    protected void initializeNewItem() {
        this.index = new WIndex();
    }

    protected void setupXQuery(FlexTable table) {
        clearConfiguration(table);
        
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
    }

    private void clearConfiguration(FlexTable table) {
        for (int i = 5; i < table.getRowCount(); i++) {
            table.removeRow(i);
        }
    }

    protected void setupGroovy(FlexTable table) {
        groovyExpressionTA = new TextArea();
        groovyExpressionTA.setVisibleLines(15);
        groovyExpressionTA.setCharacterWidth(80);
        xqueryExpressionTA.setText(index.getExpression());
        table.setText(5, 0, "Groovy Script:");
        table.setWidget(5, 1, xqueryExpressionTA);
    }

    protected void setupXPath(FlexTable table) {
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
    }

    protected void save() {
        super.save();
        
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
        
        if (!assertValid(index.getProperty(), "property name")) return;
        
        if (index.getProperty().indexOf(' ') != -1) {
            adminPanel.setMessage("The index property name cannot contain spaces.");
            setEnabled(true);
            return;
        }

        if (!assertValid(index.getDescription(), "name")) return;
        if (!assertValid(index.getExpression(), "expression")) return;

        svc.saveIndex(index, getSaveCallback());
    }

    private boolean assertValid(String value, String name) {
        if (value == null || value.equals("") ) {
            adminPanel.setMessage("You must supply an index " + name + " .");
            setEnabled(true);
            return false;
        }
        return true;
    }

}
