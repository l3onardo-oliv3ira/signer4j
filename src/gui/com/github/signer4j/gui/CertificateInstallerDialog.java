/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.gui;

import static java.util.Collections.unmodifiableList;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.signer4j.AllowedExtensions;
import com.github.signer4j.ICertificateListUI.IA1A3ConfigSavedCallback;
import com.github.signer4j.IFilePath;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.FilePath;
import com.github.utils4j.gui.imp.ButtonRenderer;
import com.github.utils4j.gui.imp.DefaultFileChooser;
import com.github.utils4j.gui.imp.SimpleDialog;
import com.github.utils4j.imp.Args;

import net.miginfocom.swing.MigLayout;

class CertificateInstallerDialog extends SimpleDialog {

  private static final long serialVersionUID = 1L;
  
  private static final Dimension MININUM_SIZE = new Dimension(474, 249);
  
  private static final Color SELECTED = new Color(234, 248, 229);
  
  private JPanel contentPane;
  private JTable table;
  private JButton a1Button;
  private JButton a3Button;
  private CertType current = null;
  private List<IFilePath> a1List = new ArrayList<>();
  private List<IFilePath> a3List = new ArrayList<>();
  
  private final IA1A3ConfigSavedCallback savedCallback;

  CertificateInstallerDialog() {
    this(IA1A3ConfigSavedCallback.NOTHING);
  }

  CertificateInstallerDialog(IA1A3ConfigSavedCallback savedCallback) {
    super("Configuração de certificado", Config.getIcon(), true);
    this.savedCallback = Args.requireNonNull(savedCallback, "onSaved is null");
    Config.loadA3Paths(a3List::add);
    Config.loadA1Paths(a1List::add);
    setup();
  }

  private void setup() {
    setupLayout();
    setupFrame();
  }

  private void setupFrame() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setFixedMinimumSize(MININUM_SIZE);
    toCenter();
  }

  private void setupLayout() {
    contentPane = new JPanel();
    contentPane.setLayout(new GridLayout(1, 0, 0, 5)); 
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.add(createStep1());
    setContentPane(contentPane);
  }

  private JPanel createStep1() {
    JPanel step1Pane = new JPanel();
    step1Pane.setLayout(new BorderLayout(0, 0));
    step1Pane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    step1Pane.add(createStep1_Title(), BorderLayout.NORTH);
    step1Pane.add(createStep1_Center(), BorderLayout.CENTER);
    return step1Pane;
  }

  private JPanel createStep1_Center() {
    JPanel step1CenterPane = new JPanel();
    step1CenterPane.setLayout(new BorderLayout(0, 0));
    step1CenterPane.add(createStep1_CertificateType(), BorderLayout.NORTH);
    step1CenterPane.add(createStep1_A1A3Pane(), BorderLayout.CENTER);
    return step1CenterPane;
  }

  private JLabel createStep1_Title() {
    JLabel step1Label = new JLabel("Passo 1");
    step1Label.setFont(new Font("Tahoma", Font.PLAIN, 20));
    return step1Label;
  }

  private JPanel createStep1_A1A3Pane() {
    JPanel step1_a1a3_Pane = new JPanel();
    step1_a1a3_Pane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    step1_a1a3_Pane.setLayout(new GridLayout(0, 2, 10, 10));
    step1_a1a3_Pane.add(createStep1_A1Button());
    step1_a1a3_Pane.add(createStep1_A3Button());
    return step1_a1a3_Pane;
  }

  private JLabel createStep1_CertificateType() {
    JLabel step1TitleLabel = new JLabel("Meu certificado é do tipo:");
    step1TitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
    return step1TitleLabel;
  }

  private JButton createStep1_A3Button() {
    a3Button = new JButton("A3");
    a3Button.addActionListener(e -> onClickA3());
    a3Button.setIcon(new ImageIcon(Images.ICON_A3.asImage()));
    a3Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return a3Button;
  }

  private JButton createStep1_A1Button() {
    a1Button = new JButton("A1");
    a1Button.addActionListener(e -> onClickA1());
    a1Button.setIcon(new ImageIcon(Images.ICON_A1.asImage()));
    a1Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return a1Button;
  }
  
  private JPanel createStep2(CertType type) {
    JPanel step2Pane = new JPanel();
    step2Pane.setLayout(new BorderLayout(0, 0));
    step2Pane.add(createStep2_NorthPane(type), BorderLayout.NORTH);
    step2Pane.add(createStep2_CenterPane(type), BorderLayout.CENTER);
    step2Pane.add(createStep2_OkCancelPane(), BorderLayout.SOUTH); 
    return step2Pane;
  }

  private JPanel createStep2_CenterPane(CertType type) {
    JPanel step2CenterPane = new JPanel();
    step2CenterPane.setLayout(new CardLayout(0, 0));
    step2CenterPane.add(createStep2_TablePane(type));
    return step2CenterPane;
  }

  private JScrollPane createStep2_TablePane(CertType type) {
    table = new JTable();    
    table.setModel(type.model);
    table.getColumnModel().getColumn(0).setPreferredWidth(340);
    table.getColumnModel().getColumn(1).setPreferredWidth(60);
    ButtonRenderer bc = new ButtonRenderer((arg) -> current.remove(table.getSelectedRow()));
    table.getColumnModel().getColumn(1).setCellRenderer(bc);
    table.getColumnModel().getColumn(1).setCellEditor(bc);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFont(new Font("Tahoma", Font.PLAIN, 13));
    table.setFillsViewportHeight(true);
    table.setFillsViewportHeight(true);
    table.setRowHeight(table.getRowHeight() + 10);
    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)
    table.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.LEFT);
    JScrollPane scrollPane = new JScrollPane(table);
    return scrollPane;
  }

  private JPanel createStep2_NorthPane(CertType type) {
    JPanel step2NorthPane = new JPanel();
    step2NorthPane.setLayout(new GridLayout(2, 0, 0, 0));
    step2NorthPane.add(createStep2_Title());
    step2NorthPane.add(createStep2_FinderPane(type));
    return step2NorthPane;
  }

  private JPanel createStep2_OkCancelPane() {
    JButton cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(e -> close());    
    JButton saveButton = new JButton("OK");
    saveButton.setPreferredSize(cancelButton.getPreferredSize());
    saveButton.addActionListener(e -> onSave());        

    JPanel okCancelPane = new JPanel();    
    okCancelPane.setLayout(new MigLayout("fillx", "push[][]", "[][]"));  
    okCancelPane.add(saveButton);
    okCancelPane.add(cancelButton);
    return okCancelPane;
  }

  private JPanel createStep2_FinderPane(CertType type) {
    JPanel finderPane = new JPanel();
    finderPane.setLayout(new GridLayout(0, 2, 0, 0));
    finderPane.add(createStep2_Title(type));
    finderPane.add(createStep2_A1LocateButton());
    return finderPane;
  }

  private JLabel createStep2_Title() {
    JLabel step2Label = new JLabel("Passo 2");
    step2Label.setFont(new Font("Tahoma", Font.PLAIN, 20));
    return step2Label;
  }

  private JButton createStep2_A1LocateButton() {
    JButton a1LocateButton = new JButton("Localizar");
    a1LocateButton.addActionListener(e -> onLocate());
    return a1LocateButton;
  }

  private JLabel createStep2_Title(CertType type) {
    JLabel certificateTile = new JLabel(type.labelTitle());
    certificateTile.setFont(new Font("Tahoma", Font.PLAIN, 16));
    return certificateTile;
  }
  
  private void onLocate() {
    DefaultFileChooser chooser = new DefaultFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
    chooser.setDialogTitle(current.chooseTitle());
    FileNameExtensionFilter filter = current.fileFilter();
    chooser.setFileFilter(filter);
    chooser.addChoosableFileFilter(filter);
    
    if (JFileChooser.CANCEL_OPTION == chooser.showOpenDialog(null))
      return;
    final File[] files = chooser.getSelectedFiles();
    if (files == null || files.length == 0)
      return;
    for(File file: files) {
      current.load(file);
    }
  }

  private void onSave() {
    if (current != null) { 
      current.save(a1List, a3List);
    }
    this.close();
    this.savedCallback.call(
      unmodifiableList(a1List), 
      unmodifiableList(a3List)
    );
  }

  private void onClickA1() {
    if (CertType.A1.equals(current))
      return;
    a1Button.setBackground(SELECTED);
    a3Button.setBackground(null);
    if (current != null)
      a3List = new ArrayList<>(current.model.entries);
    setComponent(CertType.A1, a1List);
  }

  private void onClickA3() {
    if (CertType.A3.equals(current))
      return;
    a3Button.setBackground(SELECTED);
    a1Button.setBackground(null);
    if (current != null)
      a1List = new ArrayList<>(current.model.entries);
    setComponent(CertType.A3, a3List);
  }
  
  private void setComponent(CertType type, List<IFilePath> actual) {
    GridLayout layout = (GridLayout)contentPane.getLayout();
    if (current != null)
      contentPane.remove(contentPane.getComponentCount() - 1);
    JPanel step2Pane = createStep2(type);
    layout.setRows(2);
    contentPane.add(step2Pane);
    contentPane.revalidate();
    contentPane.updateUI();
    boolean center = current == null;
    loadData(current = type, actual);
    setBounds(getX(), getY(), getWidth(),  510);
    if (center) toCenter();
  }
  
  private void loadData(CertType type, List<IFilePath> actual) {
    type.load(actual);
  }

  public static enum Action {
    REMOVER;
    
    @Override
    public String toString() {
      return "Remover";
    }
  }
  
  private static abstract class PathModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final List<IFilePath> entries;

    private final String itemPath;

    public PathModel(String itemPath) {
      this.itemPath = itemPath;
      this.entries = new LinkedList<>();
    }

    @Override
    public int getRowCount() {
      return entries.size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
      case 0:
        return String.class;
      case 2:
        return Action.class;
      default:
        return Object.class;
      }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex){
      case 0:
        return itemPath;
      case 1:
        return "Ação";
      }
      return "?";  
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      IFilePath entry = entries.get(rowIndex);
      switch (columnIndex) {
      case 0:
        return entry.getPath();
      case 1:
        return Action.REMOVER;
      }
      return "?";
    }
    
    public final void remove(int index) {
      if (index < 0 || index >= entries.size())
        return;
      entries.remove(index);
      fireTableRowsDeleted(index, index);
    }
    
    public final void add(IFilePath path) {
      if (entries.contains(path))
        return;
      int row = entries.size(); //NAO SERIA -1?
      entries.add(path);
      fireTableRowsInserted(row, row);
    }

    public void clear() {
      entries.clear();
      fireTableDataChanged();
    }

    public void load(List<IFilePath> entry) {
      clear();
      int i = 0;
      while(i < entry.size()) {
        IFilePath item = entry.get(i);
        if (!entries.contains(item)) {
          int row = entries.size();
          this.entries.add(item);
          fireTableRowsInserted(row, row);
        }
        i++;
      }
    }
  }
  
  private static class A1PathModel extends PathModel {
    private static final long serialVersionUID = 1L;

    A1PathModel(){
      super("Certificado");
    }
  }
  
  private static class A3PathModel extends PathModel {
    private static final long serialVersionUID = 1L;

    A3PathModel(){
      super("Driver");
    }
  }

  private static enum CertType {
    A1(new A1PathModel()) {
      String labelTitle() {
        return "Localize o certificado A1:";
      }

      @Override
      protected void load(List<IFilePath> actual) {
        List<IFilePath> m = new ArrayList<>();
        Config.loadA1Paths(m::add);
        m.addAll(actual);
        model.load(m);
      }

      @Override
      void save(List<IFilePath> listA1, List<IFilePath> listA3) {
        listA1.clear();
        listA1.addAll(model.entries);
        super.save(listA1, listA3);
      }

      @Override
      protected String chooseTitle() {
        return "Selecione o(s) certificado(s) A1";
      }

      @Override
      protected FileNameExtensionFilter fileFilter() {
        return AllowedExtensions.CERTIFICATES;
      }
    },
    A3(new A3PathModel()){
      @Override
      String labelTitle() {
        return "Localize o driver A3:";
      }

      @Override
      void load(List<IFilePath> actual) {
        List<IFilePath> m = new ArrayList<>();
        Config.loadA3Paths(m::add);
        m.addAll(actual);
        model.load(m);
      }
      
      @Override
      void save(List<IFilePath> listA1, List<IFilePath> listA3) {
        listA3.clear();
        listA3.addAll(model.entries);
        super.save(listA1, listA3);
      }

      @Override
      protected String chooseTitle() {
        return "Selecione o driver do dispositivo";
      }

      @Override
      protected FileNameExtensionFilter fileFilter() {
        return AllowedExtensions.LIBRARIES;
      }
    };
    
    public final PathModel model;
    
    CertType(PathModel model) {
      this.model = model;
    }

    protected abstract FileNameExtensionFilter fileFilter();

    void save(List<IFilePath> listA1, List<IFilePath> listA3) {
      Config.saveA1Paths(listA1.toArray(new IFilePath[listA1.size()]));
      Config.saveA3Paths(listA3.toArray(new IFilePath[listA3.size()]));
    }
    
    void load(File file) {
      model.add(new FilePath(file.toPath()));
    }
    
    void remove(int row) {
      if (row >= 0) {
        model.remove(row);
      }
    }
    
    abstract void load(List<IFilePath> actual);
    
    abstract String chooseTitle();
    abstract String labelTitle();
  }
}
