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

import static java.util.stream.IntStream.range;
import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.github.signer4j.ICertificateListUI;
import com.github.signer4j.IFilePath;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.Repository;
import com.github.signer4j.imp.SwitchRepositoryException;
import com.github.utils4j.gui.imp.SimpleDialog;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Jvms;

import net.miginfocom.swing.MigLayout;

public class CertificateListDialog extends SimpleDialog implements ICertificateListUI {

  private static final long serialVersionUID = -1L;

  private static IChoice UNDEFINED_CHOICE  = () -> Optional.empty();
  
  private static final Dimension MININUM_SIZE = new Dimension(740, 302);

  private JTable table;
  
  private JButton okButton;

  private JCheckBox rememberMeCheckbox;
  
  private final String defaultAlias;
  
  private final IConfigSavedCallback savedCallback;
  
  private Optional<ICertificateEntry> selectedEntry = Optional.empty();
  
  private IChoice choice = UNDEFINED_CHOICE;

  protected CertificateListDialog(String defaultAlias, IConfigSavedCallback savedCallback) {
    super("Seleção de certificado", Config.getIcon(), true);
    this.defaultAlias = Args.requireNonNull(defaultAlias, "defaultAlias is null");
    this.savedCallback = Args.requireNonNull(savedCallback, "onSaved is null");
    setup();    
  }
  
  protected final boolean hasSavedCallback() {
    return savedCallback != IConfigSavedCallback.NOTHING;
  }

  private void setup() {
    setupLayout();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    
    setMinimumSize(MININUM_SIZE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {
        clickCancel(null);
      }
    });
    toCenter();
  }

  private void setupLayout() {
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    contentPane.add(createNorth(), BorderLayout.NORTH);
    contentPane.add(createCenter(), BorderLayout.CENTER);
    contentPane.add(createSouth(), BorderLayout.SOUTH);
    setContentPane(contentPane);
  }

  private JPanel createNorth() {
    JPanel pnlNorth = new JPanel();    
    pnlNorth.setLayout(new BorderLayout(0, 0)); 
    pnlNorth.add(createCertListLabel());
    pnlNorth.add(createSetup(), BorderLayout.EAST);
    return pnlNorth;
  }

  protected JPanel createSetup() {
    JPanel pnlNorthEast = new JPanel();
    pnlNorthEast.setLayout(new BorderLayout(0, 0));
    pnlNorthEast.add(createHeaderConfig(), BorderLayout.NORTH);
    return pnlNorthEast;
  }

  private JPanel createHeaderConfig() {
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BorderLayout());
    headerPanel.add(createConfigInstall(), BorderLayout.CENTER);
    headerPanel.add(createRefresh(createEmptyBorder(15,  2,  0,  4)), BorderLayout.EAST);
    return headerPanel;
  }
  
  private JLabel createCertListLabel() {
    JLabel lblCertificateList = new JLabel("Certificados Disponíveis");
    lblCertificateList.setIcon(Images.CERTIFICATE.asIcon());
    lblCertificateList.setHorizontalAlignment(SwingConstants.LEFT);
    lblCertificateList.setFont(new Font("Tahoma", Font.BOLD, 15));
    return lblCertificateList;
  }

  protected final JLabel createRefresh(Border border) {
    JLabel refreshLabel = new JLabel("");
    refreshLabel.setVerticalAlignment(SwingConstants.BOTTOM);    
    refreshLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    refreshLabel.setIcon(Images.REFRESH.asIcon());
    refreshLabel.setBorder(border);
    refreshLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    refreshLabel.setToolTipText("Atualiza a lista de certificados abaixo.");
    refreshLabel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        refresh();
      }
    });
    refreshLabel.setVisible(hasSavedCallback());
    return refreshLabel;
  }

  private JLabel createConfigInstall() {
    JLabel configLabel = new JLabel("<html><u>Configurar um novo certificado</u>&nbsp;</html>");
    configLabel.setBorder(BorderFactory.createEmptyBorder(0,  0,  0,  4));
    configLabel.setVerticalAlignment(SwingConstants.BOTTOM);
    configLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    configLabel.setForeground(Color.BLUE);
    configLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
    configLabel.setHorizontalAlignment(SwingConstants.LEFT);
    configLabel.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        clickConfig();
      }
    });
    configLabel.setVisible(hasSavedCallback());
    return configLabel;
  }

  private JPanel createCenter() {
    JPanel centerPane = new JPanel();
    centerPane.setLayout(new CardLayout(0, 0));
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
    centerPane.add(scrollPane);
    return centerPane;
  }

  private void onCertificateSelected(ListSelectionEvent e) {
    int selectedRow = table.getSelectedRow();
    boolean enabled = false;
    if (selectedRow < 0) {
      this.selectedEntry = Optional.empty();
      this.rememberMeCheckbox.setSelected(false);
    }else {
      CertificateModel model = (CertificateModel)table.getModel();
      ICertificateEntry rowEntry = model.getEntryAt(selectedRow);
      this.selectedEntry = Optional.of(rowEntry);
      enabled |= !rowEntry.isExpired();
      this.rememberMeCheckbox.setSelected(enabled && defaultAlias.equals(rowEntry.getId()));
      rowEntry.setRemembered(this.rememberMeCheckbox.isSelected());
    }
    okButton.setEnabled(enabled);
    rememberMeCheckbox.setEnabled(enabled);
  }

  private JPanel createSouth() {
    rememberMeCheckbox = new JCheckBox("Memorizar este certificado como padrão e não perguntar novamente.");    
    rememberMeCheckbox.setEnabled(false);
    rememberMeCheckbox.setSelected(false);
    JButton cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(this::clickCancel);
    okButton = new JButton("OK");
    okButton.setPreferredSize(cancelButton.getPreferredSize());
    okButton.setEnabled(false);
    okButton.addActionListener(arg -> close());
    JPanel southPane = new JPanel();
    southPane.setLayout(new MigLayout("fillx", "push[][][]", "[][][]"));
    southPane.add(rememberMeCheckbox);
    southPane.add(okButton);
    southPane.add(cancelButton);
    return southPane;
  }
  
  private static class CertificateModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final List<ICertificateEntry> entries;

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

    public CertificateModel load(List<ICertificateEntry> entry) {
      clear();
      int i = 0;
      while(i < entry.size()) {
        this.entries.add(entry.get(i));
        fireTableRowsInserted(i, i);
        i++;
      }
      return this;
    }

    public void preselect(JTable table) {
      AtomicReference<Integer> idx = new AtomicReference<>();
      if (range(0, entries.size()).filter(i -> !entries.get(i).isExpired()).peek(idx::set).count() == 1) {
        table.setRowSelectionInterval(idx.get(), idx.get());
      }
    }
  }
  
  protected final void clickConfig() {
    new CertificateInstallerDialog(this::saveCallback).showToFront(); //wait user interaction
    onAfterConfig();
  }
  
  protected final boolean isNeedReload() {
    return this.choice == IChoice.NEED_RELOAD;
  }
  
  protected void onAfterConfig() {
    if (this.choice == IChoice.NEED_RELOAD) {
      this.close();
    }
  }
  
  protected void saveCallback(List<IFilePath> a1list, List<IFilePath> a3List) {
    this.choice = IChoice.NEED_RELOAD;    
    this.table.getSelectionModel().clearSelection();
    this.savedCallback.call(a1list, a3List); //response callback
  }
  
  protected final void refresh() {
    this.choice = IChoice.NEED_RELOAD;
    this.close();
  }

  private void clickCancel(ActionEvent e) {
    ((CertificateModel)table.getModel()).clear();
    this.selectedEntry = Optional.empty();
    this.close();
  }

  @Override
  public final IChoice choose(List<ICertificateEntry> entries) throws SwitchRepositoryException {
    Args.requireNonNull(entries, "entries is null");
    this.rememberMeCheckbox.setSelected(false);
    this.rememberMeCheckbox.setEnabled(false);
    
    CertificateModel model = (CertificateModel)this.table.getModel();
    model.load(entries);
    model.preselect(this.table); 
    
    this.showToFront();
    
    this.beforeChoice();
    
    this.selectedEntry.ifPresent(entry -> {
      if (this.defaultAlias.equals(entry.getId())) {
        if (!this.rememberMeCheckbox.isSelected())
          Config.save("");
      } else if (this.rememberMeCheckbox.isSelected()) {
        Config.save(entry.getId());
      }
    });

    this.close();
    return this.choice == IChoice.NEED_RELOAD ? this.choice : () -> this.selectedEntry;
  }
  
  protected void beforeChoice() throws SwitchRepositoryException {}

  public static IChoice display(List<ICertificateEntry> entries) throws SwitchRepositoryException{
    return display(entries, false);
  }
  
  public static IChoice display(List<ICertificateEntry> entries, boolean repoWaiting) throws SwitchRepositoryException{
    return display(entries, repoWaiting, true);
  }

  public static IChoice display(List<ICertificateEntry> entries, boolean repoWaiting, boolean auto) throws SwitchRepositoryException{
    return display(entries, repoWaiting, auto, IConfigSavedCallback.NOTHING);
  }
  
  public static IChoice display(List<ICertificateEntry> entries, boolean repoWaiting, boolean auto, IConfigSavedCallback saveCallback) throws SwitchRepositoryException {
    return display(entries, repoWaiting, auto, saveCallback, Repository.NATIVE);
  }

  public static IChoice display(List<ICertificateEntry> entries, boolean repoWaiting, boolean auto, IConfigSavedCallback saveCallback, Repository repository) throws SwitchRepositoryException{
    Args.requireNonNull(entries, "entries is null");
    Args.requireNonNull(saveCallback, "onSaved is null");
    String defaultAlias = Config.defaultAlias().orElse("$not_found$");
    if (auto) {
      
      Optional<ICertificateEntry> defaultEntry = entries
          .stream()
          .filter(c -> c.getId().equals(defaultAlias) && !c.isExpired()) //auto select!
          .findFirst();
      
      if (defaultEntry.isPresent()) {
        return () -> defaultEntry;
      }
      
      AtomicReference<ICertificateEntry> choosen = new AtomicReference<>();
      if (
        entries
            .stream()
            .filter(c -> !c.isExpired()) //auto select!
            .peek(choosen::set)
            .count() == 1) {
        return () -> Optional.of(choosen.get());
      }
    }
    return (Jvms.isWindows() ? 
      new WindowsCertificateListDialog(defaultAlias, saveCallback, repository, repoWaiting) : 
      new CertificateListDialog(defaultAlias, saveCallback))
    .choose(entries);    
  }
}
