package com.github.signer4j.progress.imp;

import static com.github.signer4j.imp.SwingTools.invokeLater;
import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import com.github.signer4j.progress.IStageEvent;
import com.github.signer4j.progress.IStepEvent;

class ProgressWindow extends SimpleFrame {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressWindow.class);
  
  private static final long serialVersionUID = 1L;
  
  private final JTextArea textArea = new JTextArea();
  
  private final JProgressBar progressBar = new JProgressBar();

  private volatile Thread thread;

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
    btnLimpar.addActionListener((e) -> clickClear(e));
    pnlSouth.add(btnLimpar);

    JButton btnNewButton = new JButton("Cancelar");
    btnNewButton.addActionListener((e) -> clickCancel(e));
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
  
  @Override
  protected void onEscPressed(ActionEvent e) {
    clickCancel(e);
  }

  private void clickCancel(ActionEvent e) {
    if (thread != null) {
      int reply = JOptionPane.showConfirmDialog(null, 
        "Deseja mesmo cancelar a operação?", 
        "Cancelamento da operação", 
        JOptionPane.YES_NO_OPTION
      );
      if (reply != JOptionPane.YES_OPTION) {
        return;
      }
      if (thread != null) //double checking
        thread.interrupt();
    }
    this.unreveal();
  }

  private void clickClear(ActionEvent e) {
    textArea.setText("");
  }

  final void reveal() {
    invokeLater(() -> { 
      this.setLocationRelativeTo(null);
      this.showToFront(); 
    });
  }

  final void unreveal() {
    invokeLater(() -> {
      this.setVisible(false);
      this.resetProgress();
    });
  }

  private void resetProgress() {
    this.progressBar.setIndeterminate(false);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("");
    this.textArea.setText("");
  }

  final void stepToken(IStepEvent e) {
    final int step = e.getStep();
    final int total = e.getTotal();
    final String message = e.getMessage();
    final StringBuilder text = new StringBuilder(computeTabs(e.getStackSize()));
    final String log;
    if (total > 0) {
      log = text.append(format("Passo %s de %s: %s", step, total, message)).toString();
    } else {
      log = text.append(message).toString();
    }
    invokeLater(() -> {
      if (!progressBar.isIndeterminate())
        progressBar.setIndeterminate(true);
      progressBar.setString(log);
      textArea.append(log + "\n\r");
      LOGGER.info(log); 
    });
  }
  
  final void stageToken(IStageEvent e) {
    final String tabSize = computeTabs(e.getStackSize());
    final String text = tabSize + e.getMessage();
    invokeLater(() -> {
      progressBar.setIndeterminate(true);
      progressBar.setString(text);
      textArea.append(text + "\n\r");
      LOGGER.info(text);
    });    
  }
  
  final void attach(Thread thread) {
    this.thread = thread;
  }

  private static String computeTabs(int stackSize) {
    StringBuilder b = new StringBuilder(6);
    while(stackSize-- > 0)
      b.append("  ");
    return b.toString();
  }
}
