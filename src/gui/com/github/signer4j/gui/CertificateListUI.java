package com.github.signer4j.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.ICertificateListUI;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.gui.utils.SimpleDialog;
import com.github.signer4j.imp.Args;
import com.github.signer4j.imp.Config;

public class CertificateListUI extends SimpleDialog implements ICertificateListUI {

  private static final long serialVersionUID = -1L;

  private JPanel contentPane;
  private JTable table;
  private JPanel pnlCenter;
  private JPanel pnlSouth;
  private JPanel pnlSouthInner;
  private JCheckBox chkRememberMe;
  private JPanel btnButtons;
  private JButton btnOk;
  private JButton btnCancel;
  private JPanel pnlNorth;
  private JLabel lblCertificateList;
  private JLabel lblConfigInstall;
  private final String defaultAlias;
  private final IA1A3ConfigSaved onSaved;
  
  private Optional<ICertificateEntry> selectedEntry = Optional.empty();
  
  private boolean needReload = false;

  private CertificateListUI(String defaultAlias, IA1A3ConfigSaved onSaved) {
    super("Seleção de certificado", true);
    this.defaultAlias = Args.requireNonNull(defaultAlias, "defaultAlias is null");
    this.onSaved = onSaved;
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 560, 287);
    contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    pnlCenter = new JPanel();
    contentPane.add(pnlCenter, BorderLayout.CENTER);
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
    table.getSelectionModel().addListSelectionListener((e) -> {
      int selectedRow = table.getSelectedRow();
      boolean enabled = false;
      if (selectedRow < 0) {
        this.selectedEntry = Optional.empty();
        this.chkRememberMe.setSelected(false);
      }else {
        CertificateModel model = (CertificateModel)table.getModel();
        ICertificateEntry rowEntry = model.getEntryAt(selectedRow);
        this.selectedEntry = Optional.of(rowEntry);
        enabled |= rowEntry.isValid();
        this.chkRememberMe.setSelected(defaultAlias.equals(rowEntry.getId()) && enabled);
        rowEntry.setRemembered(this.chkRememberMe.isSelected());
      }
      btnOk.setEnabled(enabled);
      chkRememberMe.setEnabled(enabled);
    });

    JScrollPane scrollPane = new JScrollPane(table);
    pnlCenter.add(scrollPane);

    pnlSouth = new JPanel();
    contentPane.add(pnlSouth, BorderLayout.SOUTH);
    pnlSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

    pnlSouthInner = new JPanel();
    pnlSouth.add(pnlSouthInner);

    chkRememberMe = new JCheckBox("Memorizar e não perguntar novamente");
    chkRememberMe.setEnabled(false);
    chkRememberMe.setSelected(false);
    pnlSouthInner.setLayout(new GridLayout(0, 2, 0, 0));
    pnlSouthInner.add(chkRememberMe);

    btnButtons = new JPanel();
    pnlSouthInner.add(btnButtons);
    btnButtons.setLayout(new GridLayout(1, 0, 30, 0));

    btnOk = new JButton("OK");
    btnOk.setEnabled(false);
    btnOk.addActionListener((arg) -> close());
    btnButtons.add(btnOk);

    btnCancel = new JButton("Cancelar");
    btnCancel.addActionListener((e) -> clickCancel(e));
    btnButtons.add(btnCancel);
    
    pnlNorth = new JPanel();
    contentPane.add(pnlNorth, BorderLayout.NORTH);
    pnlNorth.setLayout(new BorderLayout(0, 0));
    
    JPanel pnlNorthEast = new JPanel();
    
    pnlNorth.add(pnlNorthEast, BorderLayout.EAST);

    lblCertificateList = new JLabel("Certificados Disponíveis");
    lblCertificateList.setIcon(Images.CERTIFICATE.asIcon());
    lblCertificateList.setHorizontalAlignment(SwingConstants.LEFT);
    lblCertificateList.setFont(new Font("Tahoma", Font.BOLD, 15));
    pnlNorth.add(lblCertificateList);

    lblConfigInstall = new JLabel("<html><u>Configurar um novo certificado</u>&nbsp;&nbsp;</html>");
    lblConfigInstall.setVerticalAlignment(SwingConstants.BOTTOM);
    lblConfigInstall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblConfigInstall.setForeground(Color.RED);
    lblConfigInstall.setFont(new Font("Tahoma", Font.ITALIC, 12));
    lblConfigInstall.setHorizontalAlignment(SwingConstants.LEFT);
    lblConfigInstall.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        clickConfig();
      }
    });
    lblConfigInstall.setVisible(onSaved != null);
    
    JLabel lblRefresh = new JLabel("");
    lblRefresh.setVerticalAlignment(SwingConstants.BOTTOM);
    
    lblRefresh.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRefresh.setIcon(Images.REFRESH.asIcon());
    lblRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lblRefresh.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        refresh();
      }
    });
    lblRefresh.setVisible(onSaved != null);
    
    pnlNorthEast.setLayout(new BorderLayout(0, 0));
    pnlNorthEast.add(lblConfigInstall, BorderLayout.CENTER);
    pnlNorthEast.add(lblRefresh, BorderLayout.EAST);

    setLocationRelativeTo(null);
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
    new CertificateInstaller((a, b) -> {
      this.needReload = true;
      this.table.getSelectionModel().clearSelection();
      if (this.onSaved != null) {
        this.onSaved.execute(a, b);
      }
    }).setVisible(true);
    if (this.needReload) {
      this.close();
    }
  }
  
  private void refresh() {
    this.needReload = true;
    this.close();
  }

  private void clickCancel(ActionEvent e) {
    ((CertificateModel)table.getModel()).clear();
    this.selectedEntry = Optional.empty();
    this.close();
  }

  @Override
  public Optional<ICertificateEntry> choose(List<ICertificateEntry> entries) {
    Args.requireNonNull(entries, "entries is null");
    this.chkRememberMe.setSelected(false);
    this.chkRememberMe.setEnabled(false);
    ((CertificateModel)table.getModel()).load(entries);
    this.setVisible(true);  
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
    return needReload ? null : this.selectedEntry; 
  }
  
  public static Optional<ICertificateEntry> display(List<ICertificateEntry> entries) {
    return display(entries, true);
  }

  public static Optional<ICertificateEntry> display(List<ICertificateEntry> entries, boolean auto) {
    return display(entries, auto, null);
  }
  
  public static Optional<ICertificateEntry> display(List<ICertificateEntry> entries, boolean auto, IA1A3ConfigSaved onSaved) {
    Args.requireNonNull(entries, "entries is null");
    String defaultAlias = Config.defaultAlias().orElse("$not_found$");
    if (auto) {
      Optional<ICertificateEntry> defaultEntry = entries
          .stream()
          .filter(c -> c.getId().equals(defaultAlias)) //auto select!
          .findFirst();
      if (defaultEntry.isPresent()) {
        return defaultEntry;
      }
    }
    return new CertificateListUI(defaultAlias, onSaved).choose(entries);
  }
}
