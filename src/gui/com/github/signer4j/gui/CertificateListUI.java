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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.ICertificateListUI;
import com.github.signer4j.IFilePath;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.utils4j.gui.imp.SimpleDialog;
import com.github.utils4j.imp.Args;

import net.miginfocom.swing.MigLayout;

public class CertificateListUI extends SimpleDialog implements ICertificateListUI {

  private static final long serialVersionUID = -1L;

  private static IChoice UNDEFINED_CHOICE  = () -> Optional.empty();
  
  private static final Dimension MININUM_SIZE = new Dimension(740, 287);

  private JTable table;
  
  private JButton btnOk;

  private JCheckBox chkRememberMe;
  
  private final String defaultAlias;
  
  private final IA1A3ConfigSaved onSaved;
  
  private Optional<ICertificateEntry> selectedEntry = Optional.empty();
  
  private IChoice choice = UNDEFINED_CHOICE;

  private CertificateListUI(String defaultAlias, IA1A3ConfigSaved onSaved) {
    super("Seleção de certificado", Config.getIcon(), true);
    this.defaultAlias = Args.requireNonNull(defaultAlias, "defaultAlias is null");
    this.onSaved = Args.requireNonNull(onSaved, "onSaved is null");

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setMinimumSize(MININUM_SIZE);
    setContentPane(createContentPane());
    setLocationRelativeTo(null);    
  }

  private JPanel createContentPane() {
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.add(createNorth(), BorderLayout.NORTH);
    contentPane.add(createCenter(), BorderLayout.CENTER);
    contentPane.add(createSouth(), BorderLayout.SOUTH);
    return contentPane;
  }

  private JPanel createNorth() {
    JPanel pnlNorth = new JPanel();    
    pnlNorth.setLayout(new BorderLayout(0, 0)); 
    pnlNorth.add(createCertListLabel());    
    JPanel pnlNorthEast = new JPanel();
    pnlNorthEast.setLayout(new BorderLayout(0, 0));
    pnlNorthEast.add(createConfigInstall(), BorderLayout.CENTER);
    pnlNorthEast.add(createRefresh(), BorderLayout.EAST);
    pnlNorth.add(pnlNorthEast, BorderLayout.EAST);
    return pnlNorth;
  }

  private JLabel createCertListLabel() {
    JLabel lblCertificateList = new JLabel("Certificados Disponíveis");
    lblCertificateList.setIcon(Images.CERTIFICATE.asIcon());
    lblCertificateList.setHorizontalAlignment(SwingConstants.LEFT);
    lblCertificateList.setFont(new Font("Tahoma", Font.BOLD, 15));
    return lblCertificateList;
  }

  private JLabel createRefresh() {
    JLabel lblRefresh = new JLabel("");
    lblRefresh.setVerticalAlignment(SwingConstants.BOTTOM);    
    lblRefresh.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRefresh.setIcon(Images.REFRESH.asIcon());
    lblRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblRefresh.setToolTipText("Atualiza a lista de certificados abaixo");
    lblRefresh.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        refresh();
      }
    });
    lblRefresh.setVisible(onSaved != IA1A3ConfigSaved.NOTHING);
    return lblRefresh;
  }

  private JLabel createConfigInstall() {
    JLabel lblConfigInstall = new JLabel("<html><u>Configurar um novo certificado</u>&nbsp;&nbsp;</html>");
    lblConfigInstall.setVerticalAlignment(SwingConstants.BOTTOM);
    lblConfigInstall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblConfigInstall.setForeground(Color.BLUE);
    lblConfigInstall.setFont(new Font("Tahoma", Font.ITALIC, 12));
    lblConfigInstall.setHorizontalAlignment(SwingConstants.LEFT);
    lblConfigInstall.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        clickConfig();
      }
    });
    lblConfigInstall.setVisible(onSaved != IA1A3ConfigSaved.NOTHING);
    return lblConfigInstall;
  }

  private JPanel createCenter() {
    JPanel pnlCenter = new JPanel();
    pnlCenter.setLayout(new CardLayout(0, 0));
    table = new JTable();
    table.setModel(new CertificateModel());
    table.getColumnModel().getColumn(0).setPreferredWidth(70);
    table.getColumnModel().getColumn(1).setPreferredWidth(167);
    table.getColumnModel().getColumn(2).setPreferredWidth(132);
    table.getColumnModel().getColumn(3).setPreferredWidth(111);
    table.getColumnModel().getColumn(3).setMinWidth(111);
    TableCellRenderer renderer = (TableCellRenderer) table.getTableHeader().getDefaultRenderer();
    ((DefaultTableCellRenderer)renderer).setHorizontalAlignment(JLabel.LEFT);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFont(new Font("Tahoma", Font.PLAIN, 13));
    table.setFillsViewportHeight(true);
    table.setBorder(null);
    table.getSelectionModel().addListSelectionListener(this::onCertificateSelected);
    JScrollPane scrollPane = new JScrollPane(table);
    pnlCenter.add(scrollPane);
    return pnlCenter;
  }

  private void onCertificateSelected(ListSelectionEvent e) {
    int selectedRow = table.getSelectedRow();
    boolean enabled = false;
    if (selectedRow < 0) {
      this.selectedEntry = Optional.empty();
      this.chkRememberMe.setSelected(false);
    }else {
      CertificateModel model = (CertificateModel)table.getModel();
      ICertificateEntry rowEntry = model.getEntryAt(selectedRow);
      this.selectedEntry = Optional.of(rowEntry);
      enabled |= !rowEntry.isExpired();
      this.chkRememberMe.setSelected(defaultAlias.equals(rowEntry.getId()) && enabled);
      rowEntry.setRemembered(this.chkRememberMe.isSelected());
    }
    btnOk.setEnabled(enabled);
    chkRememberMe.setEnabled(enabled);
  }

  private JPanel createSouth() {
    JPanel southPane = new JPanel();   
    chkRememberMe = new JCheckBox("Memorizar este certificado como padrão e não perguntar novamente");    
    chkRememberMe.setEnabled(false);
    chkRememberMe.setSelected(false);
    JButton cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(this::clickCancel);
    btnOk = new JButton("OK");
    btnOk.setPreferredSize(cancelButton.getPreferredSize());
    btnOk.setEnabled(false);
    btnOk.addActionListener(arg -> close());
    southPane.setLayout(new MigLayout("fillx", "push[][][]", "[][][]"));
    southPane.add(chkRememberMe);
    southPane.add(btnOk);
    southPane.add(cancelButton);
    return southPane;
  }
  
  private static class CertificateModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private List<ICertificateEntry> entries;

    public CertificateModel() {
      this.entries = new LinkedList<>();
    }

    @Override
    public int getRowCount() {
      return entries.size();
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex){
      case 0:
        return "Dispositivo";
      case 1:
        return "Nome";
      case 2:
        return "Emitido Por";
      case 3:
        return "Validade";
      }
      return "?";  
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      ICertificateEntry entry = entries.get(rowIndex);
      switch (columnIndex) {
      case 0:
        return entry.getDevice();
      case 1:
        return entry.getName();
      case 2:
        return entry.getIssuer();
      case 3:
        return entry.getDate();
      }
      return "?";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    public ICertificateEntry getEntryAt(int row) {
      return entries.get(row);
    }
    
    public void clear() {
      entries.clear();
      fireTableDataChanged();
    }

    public void load(List<ICertificateEntry> entry) {
      clear();
      int i = 0;
      while(i < entry.size()) {
        this.entries.add(entry.get(i));
        fireTableRowsInserted(i, i);
        i++;
      }
    }
  }
  
  private void clickConfig() {
    new CertificateInstaller(this::onConfigSaved).showToFront();
    if (this.choice == IChoice.NEED_RELOAD) {
      this.close();
    }
  }
  
  protected void onConfigSaved(List<IFilePath> a1list, List<IFilePath> a3List) {
    this.choice = IChoice.NEED_RELOAD;    
    this.table.getSelectionModel().clearSelection();
    this.onSaved.call(a1list, a3List);
  }
  
  private void refresh() {
    this.choice = IChoice.NEED_RELOAD;
    this.close();
  }

  private void clickCancel(ActionEvent e) {
    ((CertificateModel)table.getModel()).clear();
    this.selectedEntry = Optional.empty();
    this.close();
  }

  @Override
  public IChoice choose(List<ICertificateEntry> entries) {
    Args.requireNonNull(entries, "entries is null");
    this.chkRememberMe.setSelected(false);
    this.chkRememberMe.setEnabled(false);
    ((CertificateModel)table.getModel()).load(entries);
    if (this.table.getModel().getRowCount() == 1)
      this.table.setRowSelectionInterval(0, 0);
    this.showToFront();
    if (this.selectedEntry.isPresent()) {
      ICertificateEntry selectedEntry = this.selectedEntry.get();
      if (this.defaultAlias.equals(selectedEntry.getId())) {
        if (!this.chkRememberMe.isSelected())
          Config.save("");
      }else if (this.chkRememberMe.isSelected()) {
        Config.save(selectedEntry.getId());
      }
    }
    this.close();
    return this.choice == IChoice.NEED_RELOAD ? this.choice : () -> this.selectedEntry;
  }
  
  public static IChoice display(List<ICertificateEntry> entries) {
    return display(entries, true);
  }

  public static IChoice display(List<ICertificateEntry> entries, boolean auto) {
    return display(entries, auto, IA1A3ConfigSaved.NOTHING);
  }
  
  public static IChoice display(List<ICertificateEntry> entries, boolean auto, IA1A3ConfigSaved onSaved) {
    Args.requireNonNull(entries, "entries is null");
    Args.requireNonNull(onSaved, "onSaved is null");
    String defaultAlias = Config.defaultAlias().orElse("$not_found$");
    if (auto) {
      Optional<ICertificateEntry> defaultEntry = entries
          .stream()
          .filter(c -> c.getId().equals(defaultAlias) && !c.isExpired()) //auto select!
          .findFirst();
      if (defaultEntry.isPresent()) {
        return () -> defaultEntry;
      }
    }
    return new CertificateListUI(defaultAlias, onSaved).choose(entries);
  }
}
