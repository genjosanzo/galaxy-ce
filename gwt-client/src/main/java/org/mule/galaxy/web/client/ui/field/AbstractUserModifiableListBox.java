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

package org.mule.galaxy.web.client.ui.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.web.client.ui.grid.BasicGrid;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

public abstract class AbstractUserModifiableListBox extends ContentPanel {

    private Button rmButton;
    private Button addButton;
    private TextField<String> textBox;
    private ListStore<ModelData> store;
    private BasicGrid<ModelData> grid;

    public AbstractUserModifiableListBox(Collection list, Validator validator) {
        super();
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);
        
        store = new ListStore<ModelData>();
        store.setMonitorChanges(true);

        if (list != null) {
            for (Object o : list) {
                Model model = new BaseModel();
                model.set("value", o.toString());
                
                store.add(model);
            }
        }

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig nameConfig = new ColumnConfig("value", "Name", 200);
        columns.add(nameConfig);

        ColumnConfig edit = new ColumnConfig("remove", " ", 60);
        edit.setRenderer(new GridCellRenderer<ModelData>() {

            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<ModelData> store, Grid<ModelData> grid) {
                final Image deleteImg = IconHelper.create("images/delete_config.gif").createImage();
                deleteImg.addClickHandler(new ClickHandler() {

                    public void onClick(ClickEvent arg0) {

                        deleteCurrentRow();
                    }
                });

                LayoutContainer container = new LayoutContainer();
                container.add(deleteImg);
                return container;
            }

        });
        columns.add(edit);

        grid = new BasicGrid<ModelData>(store, new ColumnModel(columns));
        grid.setColumnLines(false);
        grid.setHideHeaders(true);
        grid.setAutoExpandColumn("value");
        grid.getStore().setSortField("value");

        TableLayout layout = new TableLayout(2);
        layout.setCellSpacing(10);
        LayoutContainer addContainer = new LayoutContainer(layout);

        textBox = new RequiredTextField<String>();
        textBox.setWidth(260);
        addButton = new Button("Add", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (textBox.validate()) {
                    BaseModel model = new BaseModel();
                    model.set("value", textBox.getValue());
                    
                    store.add(model);
                    textBox.clear();
                }
            }

        });
        addContainer.add(textBox);
        addContainer.add(addButton);

        add(addContainer);
        
        ContentPanel gridPanel = new ContentPanel();
        gridPanel.setHeight(180);
        gridPanel.setScrollMode(Scroll.AUTO);
        gridPanel.setHeaderVisible(false);
        gridPanel.setBorders(false);
        gridPanel.setBodyBorder(false);
        gridPanel.add(grid);
        add(gridPanel);
    }


    private void deleteCurrentRow() {
        ModelData model = grid.getSelectionModel().getSelectedItem();
        store.remove(model);
    }
    
    protected abstract boolean isValid(String text);

    public Collection<String> getItemValues() {
        ArrayList<String> items = new ArrayList<String>();
        for (ModelData d : store.getModels()) {
            items.add((String)d.get("value"));
        }
        return items;
    }

    public boolean validate() {
        return textBox.validate();
    }

    public void setEnabled(boolean e) {
        addButton.setEnabled(e);
        rmButton.setEnabled(e);
    }

}
