package com.github.signer4j.progress.imp;

import static com.github.signer4j.imp.Strings.computeTabs;
import static com.github.signer4j.imp.SwingTools.invokeLater;
import static com.github.signer4j.imp.Throwables.tryRun;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.gui.utils.SimpleFrame;
import com.github.signer4j.imp.Args;
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IStepEvent;

class ProgressWindow extends SimpleFrame implements ICanceller {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressWindow.class);  
  
  private final JTextArea textArea = new JTextArea();
  
  private final JProgressBar progressBar = new JProgressBar();
  
  private final Map<Thread, List<Runnable>> cancels = new HashMap<>(2);

  ProgressWindow() {
    super("Progresso");
    setBounds(100, 100, 450, 154);
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);

    JPanel pngNorth = new JPanel();
    contentPane.add(pngNorth, BorderLayout.NORTH);
    pngNorth.setLayout(new GridLayout(3, 1, 0, 0));

    JLabel lblLog = new JLabel("Registro de atividades");
    lblLog.setIcon(Images.LOG.asIcon());
    lblLog.setHorizontalAlignment(SwingConstants.LEFT);
    lblLog.setFont(new Font("Tahoma", Font.BOLD, 15));
    pngNorth.add(lblLog);

    pngNorth.add(progressBar);
    resetProgress();

    final JPanel pnlSouth = new JPanel();
    
    JLabel lbldetalhes = new JLabel("Ver detalhes   ");
    lbldetalhes.setVerticalAlignment(SwingConstants.BOTTOM);
    lbldetalhes.setHorizontalAlignment(SwingConstants.RIGHT);
    lbldetalhes.setForeground(Color.RED);
    lbldetalhes.setFont(new Font("Tahoma", Font.ITALIC, 12));
    lbldetalhes.addMouseListener(new MouseAdapter(){  
      public void mouseClicked(MouseEvent e) {
        boolean show = lbldetalhes.getText().contains("Ver");
        if (show) {
          setBounds(getBounds().x, getBounds().y, 450, 312);
          lbldetalhes.setText("Esconder detalhes   ");
        }else {
          setBounds(getBounds().x, getBounds().y, 450, 154);
          lbldetalhes.setText("Ver detalhes   ");
        }
        pnlSouth.setVisible(show);
      }
    });
    pngNorth.add(lbldetalhes);
    
    contentPane.add(pnlSouth, BorderLayout.SOUTH);
    pnlSouth.setLayout(new GridLayout(0, 3, 10, 0));

    JSeparator separator = new JSeparator();
    pnlSouth.add(separator);

    JButton btnLimpar = new JButton("Limpar");
    btnLimpar.addActionListener((e) -> onClear(e));
    pnlSouth.add(btnLimpar);

    JButton btnNewButton = new JButton("Cancelar");
    btnNewButton.addActionListener((e) -> onEscPressed(e));
    pnlSouth.add(btnNewButton);
    pnlSouth.setVisible(false);

    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);

    textArea.setRows(8);
    textArea.setEditable(false);
    scrollPane.setViewportView(textArea);
    setLocationRelativeTo(null);
    setAutoRequestFocus(true);
  }
  
  protected void onClear(ActionEvent e) {
    textArea.setText("");
  }
  
  @Override
  protected void onEscPressed(ActionEvent e) {
    int option = JOptionPane.showConfirmDialog(null, 
      "Deseja mesmo cancelar a operação?", 
      "Cancelamento da operação", 
      JOptionPane.YES_NO_OPTION
    );
    if (option == JOptionPane.YES_OPTION) {
      this.cancel();
      this.unreveal();
    }
  }

  private void resetProgress() {
    this.progressBar.setIndeterminate(false);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("");
    this.textArea.setText("");
  }

  final void reveal() {
    invokeLater(() -> { 
      this.setLocationRelativeTo(null);
      this.showToFront(); 
    });
  }
  
  final void exit() {
    invokeLater(super::close);
  }

  final void unreveal() {
    invokeLater(() -> {
      this.setVisible(false);
      this.resetProgress();
    });
  }

  final void stepToken(IStepEvent e) {
    final int step = e.getStep();
    final int total = e.getTotal();
    final boolean indeterminated = e.isIndeterminated();
    final String message = e.getMessage();
    final StringBuilder text = new StringBuilder(computeTabs(e.getStackSize()));
    final String log;
    if (indeterminated) {
      log = text.append(message).toString();
    } else {
      log = text.append(format("Passo %s de %s: %s", step, total, message)).toString();
    }
    LOGGER.info(log);
    invokeLater(() -> {
      progressBar.setString(message);
      textArea.append(log + "\n\r");
    });
  }
  
  final void stageToken(IStageEvent e) {
    final String tabSize = computeTabs(e.getStackSize());
    final String message = e.getMessage();
    final String text = tabSize + message;
    LOGGER.info(text);
    invokeLater(() -> {
      if (!progressBar.isIndeterminate())
        progressBar.setIndeterminate(true);
      this.progressBar.setString(message);
      textArea.append(text + "\n\r");
    });    
  }
  
  final synchronized void cancel() {
    Runnable cancelCode = () -> {
      cancels.entrySet().stream()
        .peek(k -> {
          //currentThread poderá ser a DispatchThread que não deveria ser interrompida!
          Thread key = k.getKey();
          if (key != Thread.currentThread()) { 
            key.interrupt();
          }
        })
        .map(k -> k.getValue())
        .flatMap(Collection::stream)
        //falhas na execução de códigos de cancelamentos (r::run) não podem influenciar na execução dos demais códigos da iteração
        .forEach(r -> tryRun(r::run)); 
      cancels.clear();
    };
    invokeLater(cancelCode);
  }
  
  @Override
  public synchronized final void cancelCode(Runnable cancelCode) {
    Args.requireNonNull(cancelCode, "cancelCode is null");
    Thread thread = Thread.currentThread();
    List<Runnable> codes = cancels.get(thread);
    if (codes == null)
      cancels.put(thread, codes = new ArrayList<>(2));
    codes.add(cancelCode);
  }
}
