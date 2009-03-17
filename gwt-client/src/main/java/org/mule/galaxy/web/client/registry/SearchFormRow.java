/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.mule.galaxy.web.rpc.SearchPredicate;

public class SearchFormRow
    extends Composite
{
    private SearchForm searchPanel;
    private ListBox     propertyList;
    private HorizontalPanel contents;
    private ListBox matchTypeList;
    private TextBox valueTextBox;
    
    public SearchFormRow(SearchForm sp) {
        super();
        
        searchPanel = sp;
        
        DockPanel dock = new DockPanel();
        dock.setStylePrimaryName("search-predicate");
        dock.setWidth("100%");
        
        propertyList = new ListBox();
        propertyList.setWidth("175px");
        propertyList.addItem("Content Type", "contentType");
        propertyList.addItem("Description", "description");
        propertyList.addItem("Document Type", "documentType");
        propertyList.addItem("Name", "name");
        propertyList.addItem("Version", "version");
        
        propertyList.setSelectedIndex(3);
        propertyList.addChangeListener(new ChangeListener() {
           public void onChange(Widget sender) {
               processTypeChange();
           }
        });
        dock.add(propertyList, DockPanel.WEST);
        
        contents = new HorizontalPanel();
        contents.setWidth("100%");
        dock.add(contents, DockPanel.CENTER);
        dock.setCellWidth(contents, "100%");
        processTypeChange();
        
        HorizontalPanel buttons = new HorizontalPanel();
        final SearchFormRow pred = this;
        Button del = new Button("-", new ClickListener() {
            public void onClick(Widget sender) {
                searchPanel.removePredicate(pred);
              }
            });
        del.setStyleName("smallButton");
        buttons.add(del);
        Button add = new Button("+", new ClickListener() {
            public void onClick(Widget sender) {
                searchPanel.addPredicate();
              }
            });
        add.setStyleName("smallButton");
        buttons.add(add);
        dock.add(buttons, DockPanel.EAST);
        
        initWidget(dock);
    }
    
    public void addPropertySet(String setName, final Map<String, String> queryProps) {
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String k1, String k2) {
                String v1 = queryProps.get(k1);
                String v2 = queryProps.get(k2);
                
                return v1.compareTo(v2);
            }
        };
        
        TreeMap<String, String> sortedQP = new TreeMap<String, String>(comparator);
        sortedQP.putAll(queryProps);
        
        propertyList.addItem("", "");
        propertyList.addItem(setName, "");
        for (Map.Entry<String, String> e : sortedQP.entrySet()) {
            propertyList.addItem(e.getValue(), e.getKey());
        }
    }
    
    //
    // When the user selects a different type of search.
    // (From the first listbox)
    //
    public void processTypeChange() {
        contents.clear();
        
        matchTypeList = new ListBox();
        matchTypeList.addItem("has value",          String.valueOf(SearchPredicate.HAS_VALUE));
        matchTypeList.addItem("has value like",     String.valueOf(SearchPredicate.LIKE));
        matchTypeList.addItem("doesn't have value", String.valueOf(SearchPredicate.DOES_NOT_HAVE_VALUE));
        contents.add(matchTypeList);
        
        valueTextBox = new TextBox();
        valueTextBox.setWidth("98%");
        contents.add(valueTextBox);
        contents.setCellWidth(valueTextBox, "100%");
    }

    public SearchPredicate getPredicate()
    {
        try {
            String property = propertyList.getValue(propertyList.getSelectedIndex());
            int matchType = Integer.parseInt(matchTypeList.getValue(matchTypeList.getSelectedIndex()));
            String value = valueTextBox.getText();
            
            if (property.equals("") || value.equals(""))
                return null;
            
            return new SearchPredicate(property, matchType, value);
        }
        catch (NumberFormatException e) 
        {
            return null;
        }
    }
    
    public void setPredicate(SearchPredicate predicate) 
    {
        selectValue(propertyList, predicate.getProperty());
        selectValue(matchTypeList, new Integer(predicate.getMatchType()).toString());
        
        valueTextBox.setText(predicate.getValue());
    }

    private void selectValue(ListBox list, String value) {
        for (int i = 0; i < list.getItemCount(); i++) {
            String val = list.getValue(i);
            
            if (value.equals(val)) {
                list.setSelectedIndex(i);
                break;
            }
        }
    }
}
