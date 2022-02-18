package com.github.signer4j.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


public class ButtonRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
  private static final long serialVersionUID = -1L;
  
  private JButton renderButton;
  private JButton editButton;
  private String text;
  private ActionListener listener;

  public ButtonRenderer(ActionListener listener) {
    super();
    this.listener = listener;
    this.renderButton = new JButton();
    this.editButton = new JButton();
    this.editButton.setFocusPainted( false );
    this.editButton.addActionListener( this );
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (hasFocus) {
      renderButton.setForeground(table.getForeground());
      renderButton.setBackground(UIManager.getColor("Button.background"));
    }
    else if (isSelected) {
      renderButton.setForeground(table.getSelectionForeground());
      renderButton.setBackground(table.getSelectionBackground());
    }
    else {
      renderButton.setForeground(table.getForeground());
      renderButton.setBackground(UIManager.getColor("Button.background"));
    }
    renderButton.setText( (value == null) ? "" : value.toString() );
    return renderButton;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    text = (value == null) ? "" : value.toString();
    editButton.setText( text );
    return editButton;
  }

  public Object getCellEditorValue() {
    return text;
  }

  public void actionPerformed(ActionEvent e) {
    fireEditingStopped();
    listener.actionPerformed(e);
  }
}