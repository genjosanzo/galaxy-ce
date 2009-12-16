package org.mule.galaxy.repository.client.util;

import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.event.client.RowHighlightEvent;
import com.google.gwt.gen2.table.event.client.RowHighlightHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.RowUnhighlightEvent;
import com.google.gwt.gen2.table.event.client.RowUnhighlightHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class StylizedSortableGrid extends SortableGrid {

    public StylizedSortableGrid(int rows, int columns) {
        super(rows, columns);

        setCellSpacing(0);
        setCellPadding(4);

        addRowHighlightHandler(new RowHighlightHandler() {
            public void onRowHighlight(RowHighlightEvent event) {
                int idx = event.getValue().getRowIndex();
                if (idx == 0)
                    return;
                getRowFormatter().setStyleName(idx, "SortableGrid-selectedRow");
            }
        });
        addRowUnhighlightHandler(new RowUnhighlightHandler() {
            public void onRowUnhighlight(RowUnhighlightEvent event) {
                int idx = event.getValue().getRowIndex();
                if (idx == 0 || getSelectedRows().contains(idx))
                    return;

                getRowFormatter().setStyleName(idx, "");
            }
        });
        addRowSelectionHandler(new RowSelectionHandler() {

            public void onRowSelection(RowSelectionEvent event) {
                for (Row row : event.getDeselectedRows()) {
                    int idx = row.getRowIndex();
                    if (idx == 0)
                        return;

                    getRowFormatter().setStyleName(idx, "");
                }

                for (Row row : event.getSelectedRows()) {
                    int idx = row.getRowIndex();
                    if (idx == 0)
                        return;

                    getRowFormatter().setStyleName(idx, "SortableGrid-selectedRow");
                }
            }
        });

        setColumnSorter(new ColumnSorter() {

            @Override
            public void onSortColumn(SortableGrid grid, ColumnSortList sortList,
                                     SortableGrid.ColumnSorterCallback callback) {
                // Get the primary column and sort order
                int column = sortList.getPrimaryColumn();
                boolean ascending = sortList.isPrimaryAscending();

                // Apply the default quicksort algorithm
                SelectionGridCellFormatter formatter = grid.getSelectionGridCellFormatter();
                Element[] tdElems = new Element[grid.getRowCount()];
                for (int i = 0; i < tdElems.length; i++) {
                    tdElems[i] = formatter.getElement(i, column);
                }
                
                Element[] nonHeaders = new Element[tdElems.length-1];
                
                for (int i = 1; i < tdElems.length; i++) {
                    nonHeaders[i-1] = tdElems[i];
                }
                
                quicksort(nonHeaders, 0, nonHeaders.length - 1);

                for (int i = 1; i < tdElems.length; i++) {
                    tdElems[i] = nonHeaders[i-1];
                }
                
                // Convert tdElems to trElems, reversing if needed
                Element[] trElems = new Element[tdElems.length];
                trElems[0] = DOM.getParent(tdElems[0]);
                if (ascending) {
                    for (int i = 1; i < tdElems.length; i++) {
                        trElems[i] = DOM.getParent(tdElems[i]);
                    }
                } else {
                    int maxElem = tdElems.length - 1;
                    for (int i = 1; i <= maxElem; i++) {
                        trElems[i] = DOM.getParent(tdElems[maxElem - i + 1]);
                    }
                }

                // Use the callback to complete the sorting
                callback.onSortingComplete(trElems);
            }

            /**
             * Recursive quicksort algorithm.
             * 
             * @param tdElems
             *            an array of row elements
             * @param start
             *            the start index to sort
             * @param end
             *            the last index to sort
             */
            private void quicksort(Element[] tdElems, int start, int end) {
                // No need to sort
                if (start >= end) {
                    return;
                }

                // Sort this set
                int i = start + 1;
                int k = end;
                String pivot = DOM.getInnerText(tdElems[start]);
                while (k >= i) {
                    if (DOM.getInnerText(tdElems[i]).compareTo(pivot) < 0) {
                        // Move i until the value is great than the pivot
                        i++;
                    } else if (k == i) {
                        // Don't swap if equal
                        k--;
                    } else if (DOM.getInnerText(tdElems[k]).compareTo(pivot) < 0) {
                        // Swap the elements at k and i
                        Element tr = tdElems[i];
                        tdElems[i] = tdElems[k];
                        tdElems[k] = tr;
                        i++;
                        k--;
                    } else {
                        // Decrement k
                        k--;
                    }
                }

                // Swap k and pivot
                if (k != start) {
                    Element tr = tdElems[k];
                    tdElems[k] = tdElems[start];
                    tdElems[start] = tr;
                }

                // Sort the subsets
                quicksort(tdElems, start, k - 1);
                quicksort(tdElems, k + 1, end);
            }
        });

    }

}
