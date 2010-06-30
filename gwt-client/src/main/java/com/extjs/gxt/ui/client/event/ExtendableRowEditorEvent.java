package com.extjs.gxt.ui.client.event;

import java.util.Map;

import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.ExtendableRowEditor;

/**
 * RowEditor event type.
 */
public class ExtendableRowEditorEvent extends BoxComponentEvent {

  private ExtendableRowEditor<?> editor;
  private int rowIndex;
  private Record record;
  private Map<String, Object> changes;
  
  @SuppressWarnings("unchecked")
  public ExtendableRowEditorEvent(ExtendableRowEditor editor) {
    super(editor);
    this.editor = editor;
  }
  
  @SuppressWarnings("unchecked")
  public ExtendableRowEditorEvent(ExtendableRowEditor editor, int rowIndex) {
    this(editor);
    this.rowIndex = rowIndex;
  }

  public Map<String, Object> getChanges() {
    return changes;
  }

  public ExtendableRowEditor<?> getEditor() {
    return editor;
  }

  public Record getRecord() {
    return record;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public void setChanges(Map<String, Object> changes) {
    this.changes = changes;
  }

  public void setEditor(ExtendableRowEditor<?> editor) {
    this.editor = editor;
  }

  public void setRecord(Record record) {
    this.record = record;
  }

  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

}
