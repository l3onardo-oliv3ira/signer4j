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
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.AllowedExtensions;
import com.github.signer4j.ICertificateListUI.IA1A3ConfigSaved;
import com.github.signer4j.IFilePath;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.FilePath;
import com.github.utils4j.gui.imp.ButtonRenderer;
import com.github.utils4j.gui.imp.DefaultFileChooser;
import com.github.utils4j.gui.imp.SimpleDialog;
import com.github.utils4j.imp.Args;

import net.miginfocom.swing.MigLayout;

class CertificateInstaller extends SimpleDialog {

  private static final long serialVersionUID = 1L;

  private static final Color SELECTED = new Color(234, 248, 229);
  
  private JPanel contentPane;
  private JTable table;
  private JButton btnA1;
  private JButton btnA3;
  private CertType current = null;
  private List<IFilePath> listA1 = new ArrayList<>();
  private List<IFilePath> listA3 = new ArrayList<>();
  
  private IA1A3ConfigSaved onSaved;

  CertificateInstaller() {
    this(IA1A3ConfigSaved.NOTHING);
  }

  CertificateInstaller(IA1A3ConfigSaved onSaved) {
    super("Configuração de certificado", Config.getIcon(), true);
    this.onSaved = Args.requireNonNull(onSaved, "onSaved is null");
    Config.loadA3Paths(listA3::add);
    Config.loadA1Paths(listA1::add);
    
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 474, 249); 
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    setContentPane(contentPane);
    contentPane.setLayout(new GridLayout(1, 0, 0, 5)); 
    
    JPanel pnlStep1 = new JPanel();
    pnlStep1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.add(pnlStep1);
    pnlStep1.setLayout(new BorderLayout(0, 0));
    
    JLabel lblStep1 = new JLabel("Passo 1");
    lblStep1.setFont(new Font("Tahoma", Font.PLAIN, 20));
    pnlStep1.add(lblStep1, BorderLayout.NORTH);
    
    JPanel pngStep1Center = new JPanel();
    pnlStep1.add(pngStep1Center, BorderLayout.CENTER);
    pngStep1Center.setLayout(new BorderLayout(0, 0));
    
    JLabel lblStep1Title = new JLabel("Meu certificado é do tipo:");
    lblStep1Title.setFont(new Font("Tahoma", Font.PLAIN, 16));
    pngStep1Center.add(lblStep1Title, BorderLayout.NORTH);
    
    JPanel pnlStep1a3a1 = new JPanel();
    pnlStep1a3a1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    pngStep1Center.add(pnlStep1a3a1, BorderLayout.CENTER);
    pnlStep1a3a1.setLayout(new GridLayout(0, 2, 10, 10));
    
    btnA1 = new JButton("A1");
    btnA1.addActionListener(e -> onClickA1());
    btnA1.setIcon(new ImageIcon(Images.ICON_A1.asImage().orElse(null)));
    Cursor hands = new Cursor(Cursor.HAND_CURSOR);
    btnA1.setCursor(hands);
    pnlStep1a3a1.add(btnA1);
    
    btnA3 = new JButton("A3");
    btnA3.addActionListener(e -> onClickA3());
    btnA3.setIcon(new ImageIcon(Images.ICON_A3.asImage().orElse(null)));
    btnA3.setCursor(hands);
    pnlStep1a3a1.add(btnA3);
  }
  
  private JPanel createStep2(CertType type) {
    JPanel pnlStep2 = new JPanel();
    pnlStep2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    pnlStep2.setLayout(new BorderLayout(0, 0));
    
    JPanel pnlStep2North = new JPanel();
    pnlStep2.add(pnlStep2North, BorderLayout.NORTH);
    pnlStep2North.setLayout(new GridLayout(2, 0, 0, 0));
    
    JLabel lblStep2 = new JLabel("Passo 2");
    lblStep2.setFont(new Font("Tahoma", Font.PLAIN, 20));
    pnlStep2North.add(lblStep2);
    
    JPanel pnlFinder = new JPanel();
    pnlFinder.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    pnlStep2North.add(pnlFinder);
    pnlFinder.setLayout(new GridLayout(0, 2, 0, 0));
    
    JLabel lblSelecionarNovoCertificado = new JLabel(type.labelTitle());
    lblSelecionarNovoCertificado.setFont(new Font("Tahoma", Font.PLAIN, 16));
    pnlFinder.add(lblSelecionarNovoCertificado);
    
    JButton btnA1Locate = new JButton("Localizar");
    btnA1Locate.addActionListener(e -> onLocate());
    pnlFinder.add(btnA1Locate);
    
    JPanel pnlStep2Center = new JPanel();
    pnlStep2.add(pnlStep2Center, BorderLayout.CENTER);
    pnlStep2Center.setLayout(new CardLayout(0, 0));
    
    JPanel pnlOkCancel = new JPanel();    
    pnlStep2.add(pnlOkCancel, BorderLayout.SOUTH);   
    pnlOkCancel.setLayout(new MigLayout("fillx", "push[][]", "[][]"));    
    JButton btnCancel = new JButton("Cancelar");
    btnCancel.addActionListener(e -> close());    
    JButton btnSave = new JButton("OK");
    btnSave.setPreferredSize(btnCancel.getPreferredSize());
    btnSave.addActionListener(e -> onSave());        
    pnlOkCancel.add(btnSave);
    pnlOkCancel.add(btnCancel);
        

    table = new JTable();    
    table.setModel(type.model);
    table.getColumnModel().getColumn(0).setPreferredWidth(370);
    table.getColumnModel().getColumn(1).setPreferredWidth(30);

    ButtonRenderer bc = new ButtonRenderer((arg) -> current.remove(table.getSelectedRow()));
    table.getColumnModel().getColumn(1).setCellRenderer(bc);
    table.getColumnModel().getColumn(1).setCellEditor(bc);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFont(new Font("Tahoma", Font.PLAIN, 13));
    table.setFillsViewportHeight(true);
    table.setFillsViewportHeight(true);
    table.setRowHeight(table.getRowHeight() + 10);
   
    TableCellRenderer renderer = (TableCellRenderer) table.getTableHeader().getDefaultRenderer();
    ((DefaultTableCellRenderer)renderer).setHorizontalAlignment(JLabel.LEFT);
    
    JScrollPane scrollPane = new JScrollPane(table);
    pnlStep2Center.add(scrollPane);
    return pnlStep2;
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
      current.save(listA1, listA3);
    }
    this.close();
    this.onSaved.call(
      unmodifiableList(listA1), 
      unmodifiableList(listA3)
    );
  }

  private void onClickA1() {
    if (CertType.A1.equals(current))
      return;
    btnA1.setBackground(SELECTED);
    btnA3.setBackground(null);
    if (current != null)
      listA3 = new ArrayList<>(current.model.entries);
    setComponent(CertType.A1, listA1);
  }

  private void onClickA3() {
    if (CertType.A3.equals(current))
      return;
    btnA3.setBackground(SELECTED);
    btnA1.setBackground(null);
    if (current != null)
      listA1 = new ArrayList<>(current.model.entries);
    setComponent(CertType.A3, listA3);
  }
  
  private void setComponent(CertType type, List<IFilePath> actual) {
    GridLayout layout = (GridLayout)contentPane.getLayout();
    if (current != null)
      contentPane.remove(contentPane.getComponentCount() - 1);
    JPanel pnlStep2 = createStep2(type);
    layout.setRows(2);
    contentPane.add(pnlStep2);
    contentPane.revalidate();
    contentPane.updateUI();
    boolean center = current == null;
    loadData(current = type, actual);
    setBounds(getX(), getY(), getWidth(),  510);
    if (center) setLocationRelativeTo(null);
  }
  
  private void loadData(CertType type, List<IFilePath> actual) {
    type.load(actual);
  }

  public static enum Action {
    REMOVER;
    
    @Override
    public String toString() {
      return "X";
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
        return "Remover";
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
        return "Localize o(s) driver(s) A3:";
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
