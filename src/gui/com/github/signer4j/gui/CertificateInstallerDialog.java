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

import static com.github.utils4j.gui.imp.SwingTools.invokeLater;
import static com.github.utils4j.imp.Threads.sleep;
import static com.github.utils4j.imp.Threads.startDaemon;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.signer4j.AllowedExtensions;
import com.github.signer4j.ICertificateListUI.IConfigSavedCallback;
import com.github.signer4j.IDriverSetup;
import com.github.signer4j.IFilePath;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.DriverSetup;
import com.github.signer4j.imp.FilePath;
import com.github.signer4j.imp.SystemSupport;
import com.github.utils4j.gui.imp.ButtonRenderer;
import com.github.utils4j.gui.imp.DefaultFileChooser;
import com.github.utils4j.gui.imp.SimpleDialog;
import com.github.utils4j.imp.Args;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.miginfocom.swing.MigLayout;

class CertificateInstallerDialog extends SimpleDialog {

  private static final long serialVersionUID = 1L;
  
  private static final int MININUM_WIDTH = 474;
  
  private static final int MININUM_HEIGHT = 545;
  
  private static final Color SELECTED = new Color(234, 248, 229);
  
  private static String SEARCH_TITLE_FORMAT = "<html><u>Busca automática</u>%s</html>";
  
  private JTable table;
  private JButton a1Button;
  private JButton a3Button;
  private JButton saveButton;

  private JPanel contentPane;
  private JButton locateButton;
  private ButtonRenderer buttonRenderer;

  private String searchResultCount = "&nbsp;";
  
  private Optional<CertType> current = Optional.empty();
  private List<IFilePath> a1List = new ArrayList<>();
  private List<IFilePath> a3List = new ArrayList<>();
  private final IConfigSavedCallback savedCallback;

  private final AtomicReference<Thread> searchThread = new AtomicReference<>();

  CertificateInstallerDialog() {
    this(IConfigSavedCallback.NOTHING);
  }

  CertificateInstallerDialog(IConfigSavedCallback savedCallback) {
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
    setFixedMinimumSize(new Dimension(MININUM_WIDTH, 249));
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
    step1_a1a3_Pane.setLayout(new GridLayout(1, 2, 10, 10));
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
    a3Button.setToolTipText("Certificado A3 (pkcs11)");
    a3Button.addActionListener(e -> onClickA3());
    a3Button.setIcon(new ImageIcon(Images.ICON_A3.asImage()));
    a3Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return a3Button;
  }

  private JButton createStep1_A1Button() {
    a1Button = new JButton("A1");
    a1Button.setToolTipText("Certificado A1 (pkcs12)");
    a1Button.setIcon(new ImageIcon(Images.ICON_A1.asImage()));
    a1Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    a1Button.addActionListener(e -> onClickA1());
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
    table.getColumnModel().getColumn(0).setPreferredWidth(330);
    table.getColumnModel().getColumn(1).setPreferredWidth(70);
    buttonRenderer = new ButtonRenderer((arg) -> current.ifPresent(c -> c.remove(table.getSelectedRow())));
    table.getColumnModel().getColumn(1).setCellRenderer(buttonRenderer);
    table.getColumnModel().getColumn(1).setCellEditor(buttonRenderer);
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
    step2NorthPane.setLayout(new BorderLayout(0, 0));
    step2NorthPane.add(createStep2_Title(), BorderLayout.NORTH);
    step2NorthPane.add(createStep2_FinderPane(type), BorderLayout.CENTER);
    return step2NorthPane;
  }

  @Override
  public void close() {
    interruptSearchThread();
    super.close();
  }

  private JPanel createStep2_OkCancelPane() {
    JButton cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(e -> close());    
    saveButton = new JButton("OK");
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
    finderPane.setLayout(new GridLayout(2, 1, 0, 0));
    finderPane.add(createStep2_Headers(type));
    finderPane.add(createStep2_LocateButton());
    return finderPane;
  }

  private JPanel createStep2_Headers(CertType type) {
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.add(createStep2_Title(type), BorderLayout.CENTER);
    titlePanel.add(createStep2_AutoSearch(type), BorderLayout.EAST);
    return titlePanel;
  }

  private JLabel createStep2_AutoSearch(CertType type) {
    return type.createAutoSearchLabel(this);
  }
  
  private void beginAutoSearch() {
    a1Button.setEnabled(false);
    a3Button.setEnabled(false);
    locateButton.setEnabled(false);
    buttonRenderer.setViewEnabled(false);
    saveButton.setEnabled(false);
    table.setEnabled(false);
  }
  
  private void endAutoSearch(List<IFilePath> found) {
    a1Button.setEnabled(true);
    a3Button.setEnabled(true);
    locateButton.setEnabled(true);
    saveButton.setEnabled(true);
    buttonRenderer.setViewEnabled(true);
    table.setEnabled(true);
    searchResultCount = "&nbsp;(" + found.size() + " localizado" + (found.size() == 1 ? "" : "s" ) + ") &nbsp;";
    setComponent(CertType.A3, found);
  }

  private JLabel createStep2_Title() {
    JLabel step2Label = new JLabel("Passo 2");
    step2Label.setFont(new Font("Tahoma", Font.PLAIN, 20));
    return step2Label;
  }

  private JButton createStep2_LocateButton() {
    locateButton = new JButton("Localizar...");
    locateButton.addActionListener(e -> onLocate());
    return locateButton;
  }

  private JLabel createStep2_Title(CertType type) {
    JLabel pathTitle = new JLabel(type.labelTitle());
    pathTitle.setFont(new Font("Tahoma", Font.PLAIN, 16));
    return pathTitle;
  }
  
  private void onLocate() {
    current.ifPresent(c -> {
      DefaultFileChooser chooser = new DefaultFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setMultiSelectionEnabled(true);
      chooser.setDialogTitle(c.chooseTitle());
      FileNameExtensionFilter filter = c.fileFilter();
      chooser.resetChoosableFileFilters();
      chooser.setFileFilter(filter);
      chooser.addChoosableFileFilter(filter);
      
      if (JFileChooser.CANCEL_OPTION == chooser.showOpenDialog(null))
        return;
      final File[] files = chooser.getSelectedFiles();
      if (files == null || files.length == 0)
        return;
      for(File file: files) {
        c.load(file);
      }
    });
  }

  private void onSave() {
    current.ifPresent(c -> c.save(a1List, a3List));
    this.close();
    this.savedCallback.call(
      unmodifiableList(a1List), 
      unmodifiableList(a3List)
    );
  }

  private void onClickA1() {
    if (!CertType.A1.equals(current.orElse(CertType.A3))) {
      a1Button.setBackground(SELECTED);
      a3Button.setBackground(null);
      current.ifPresent(c -> a3List = new ArrayList<>(c.model.entries));
      setComponent(CertType.A1, a1List);
    }
  }

  private void onClickA3() {
    if (!CertType.A3.equals(current.orElse(CertType.A1))) {
      a3Button.setBackground(SELECTED);
      a1Button.setBackground(null); //not selected!
      current.ifPresent(c -> a1List = new ArrayList<>(c.model.entries));
      setComponent(CertType.A3, a3List);
    }
  }
  
  private void setComponent(CertType type, List<IFilePath> actual) {
    frameRefit(!current.isPresent());
    renderStep2(type);
    type.load(actual);
    current = Optional.of(type);
  }

  private void frameRefit(boolean center) {
    setFixedMinimumSize(new Dimension(MININUM_WIDTH, MININUM_HEIGHT));
    setBounds(getX(), getY(), MININUM_WIDTH,  MININUM_HEIGHT);
    if (center) toCenter();
  }

  private void renderStep2(CertType type) {
    JPanel step2Pane = createStep2(type);
    current.ifPresent(c -> contentPane.remove(contentPane.getComponentCount() - 1));
    ((GridLayout)contentPane.getLayout()).setRows(2);
    contentPane.add(step2Pane);
    contentPane.revalidate();
    contentPane.updateUI();
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
      super("Arquivo");
    }
  }
  
  private static class A3PathModel extends PathModel {
    private static final long serialVersionUID = 1L;

    A3PathModel(){
      super("Biblioteca");
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

      @Override
      protected JLabel createAutoSearchLabel(CertificateInstallerDialog dialog) {
        return new JLabel(""); //empty!
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

      @Override
      protected JLabel createAutoSearchLabel(CertificateInstallerDialog dialog) {
        JLabel autoSearch = new JLabel(String.format(SEARCH_TITLE_FORMAT, dialog.searchResultCount));
        autoSearch.setBorder(BorderFactory.createEmptyBorder(0,  0,  0,  2));
        autoSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        autoSearch.setForeground(Color.BLUE);
        autoSearch.setFont(new Font("Tahoma", Font.ITALIC, 12));
        autoSearch.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            autoSearch.setText(String.format(SEARCH_TITLE_FORMAT, "&nbsp;"));
            dialog.autoSearchA3Library(autoSearch);
          }
        });
        return autoSearch;
      }
    };
    
    public final PathModel model;
    
    CertType(PathModel model) {
      this.model = model;
    }

    protected abstract JLabel createAutoSearchLabel(CertificateInstallerDialog dialog);

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
  
  private void interruptSearchThread() {
    Optional.ofNullable(searchThread.getAndSet(null)).ifPresent(Thread::interrupt);
  }
  
  protected void autoSearchA3Library(JLabel autoSearch) {
    MouseListener[] mls = autoSearch.getMouseListeners();
    for(MouseListener ml: mls)
      autoSearch.removeMouseListener(ml);
    
    beginAutoSearch();
    
    final List<IFilePath> libs = SystemSupport.getDefault().getStrategy().queriedPaths().stream().map(Paths::get).map(FilePath::new).collect(toList());
    
    final JProgressBar progressBar = new JProgressBar();
    progressBar.setMaximum(libs.size());
    progressBar.setIndeterminate(false);
    progressBar.setMinimum(0);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setString("");
    
    final BalloonTip balloonTip = new BalloonTip(autoSearch, progressBar, new EdgedBalloonStyle(Color.WHITE, Color.DARK_GRAY), false);
    balloonTip.setVisible(true);

    searchThread.set(startDaemon(() -> {
      final Set<IDriverSetup> found = new HashSet<>(5);

      for(int i = 0; i < libs.size() && !Thread.currentThread().isInterrupted(); i++) {
        int it = i;
        IFilePath file = libs.get(it);
        invokeLater(() -> {
          Path path = file.toPath();
          DriverSetup.create(path).ifPresent(found::add); //not duplicated using md5 hash!
          progressBar.setValue(it);
          progressBar.setString(path.toFile().getName());
        });
        sleep(25);
      };
      
      invokeLater(() -> {
        List<IFilePath> hits = found.stream().map(ds -> new FilePath(ds.getLibrary())).collect(toList());
        endAutoSearch(hits);
        balloonTip.closeBalloon();
        balloonTip.removeAll();
        searchThread.set(null);
      });
    }));
  }
}
