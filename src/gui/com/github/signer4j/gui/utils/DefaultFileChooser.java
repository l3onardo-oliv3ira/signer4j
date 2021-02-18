package com.github.signer4j.gui.utils;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class DefaultFileChooser extends JFileChooser {
  private static final long serialVersionUID = 1L;
  
  static {
    UIManager.put("FileChooser.lookInLabelMnemonic", new Integer(69));
    UIManager.put("FileChooser.lookInLabelText", "Pesquisar em");
    UIManager.put("FileChooser.saveInLabelText", "Salvar em");
    UIManager.put("FileChooser.openButtonToolTipText", "Abrir");
    UIManager.put("FileChooser.openButtonAccessibleName", "Abrir");
    UIManager.put("FileChooser.openButtonText", "Abrir");
    UIManager.put("FileChooser.fileNameLabelMnemonic", new Integer(78));
    UIManager.put("FileChooser.fileNameLabelText", "Nome do arquivo");
    UIManager.put("FileChooser.filesOfTypeLabelMnemonic", new Integer(84));
    UIManager.put("FileChooser.filesOfTypeLabelText", "Arquivos do Tipo");
    UIManager.put("FileChooser.upFolderToolTipText", "Um n\u00edvel acima");
    UIManager.put("FileChooser.upFolderAccessibleName", "Um n\u00edvel acima");
    UIManager.put("FileChooser.homeFolderToolTipText", "Desktop");
    UIManager.put("FileChooser.homeFolderAccessibleName", "Desktop");
    UIManager.put("FileChooser.newFolderToolTipText", "Criar nova pasta");
    UIManager.put("FileChooser.newFolderAccessibleName", "Criar nova pasta");
    UIManager.put("FileChooser.listViewButtonToolTipText", "Lista");
    UIManager.put("FileChooser.listViewButtonAccessibleName", "Lista");
    UIManager.put("FileChooser.detailsViewButtonToolTipText", "Detalhes");
    UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Detalhes");
    UIManager.put("FileChooser.fileNameHeaderText", "Nome");
    UIManager.put("FileChooser.fileSizeHeaderText", "Tamanho");
    UIManager.put("FileChooser.fileTypeHeaderText", "Tipo");
    UIManager.put("FileChooser.fileDateHeaderText", "Data");
    UIManager.put("FileChooser.fileAttrHeaderText", "Atributos");
  }

  @Override
  protected JDialog createDialog(final Component parent) throws HeadlessException {
    final JDialog dialog = super.createDialog(parent);
    dialog.setLocationByPlatform(true);
    dialog.setAlwaysOnTop(true);
    return dialog;
  }
}
