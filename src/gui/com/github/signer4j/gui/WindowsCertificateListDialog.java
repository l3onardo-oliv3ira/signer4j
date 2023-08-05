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

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.github.signer4j.IRepository;
import com.github.signer4j.gui.utils.Images;
import com.github.signer4j.imp.Config;
import com.github.signer4j.imp.Repository;
import com.github.signer4j.imp.SwitchRepositoryException;
import com.github.utils4j.imp.Throwables;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;

class WindowsCertificateListDialog extends CertificateListDialog implements ActionListener {

  private static final long serialVersionUID = 1222285112497878845L;

  private JPanel radioOptions;
  
  private BalloonTip balloonTip;

  private JLabel repositoryLabel;

  private JRadioButton mscapiButton;

  private final boolean repoWaiting;
  
  private JRadioButton standardButton;  
  
  private RepositoryStrategy currentRepository;
  
  private Optional<Repository> targetRepository = Optional.empty();
  
  protected WindowsCertificateListDialog(String defaultAlias, IConfigSavedCallback savedCallback, Repository repository, boolean repoWaiting) {
    super(defaultAlias, savedCallback);    
    this.currentRepository = RepositoryStrategy.from(repository).update(this);
    this.repoWaiting = repoWaiting;
  }
  
  private final void switchRepository(Repository repository) {
    this.targetRepository = Optional.ofNullable(repository);
  }

  @Override
  protected void beforeChoice() throws SwitchRepositoryException {
    if (targetRepository.isPresent()) {
      this.close();
      throw new SwitchRepositoryException(repoWaiting, targetRepository.get());
    }
  }
  
  @Override
  public void close() {
    disposeBalloon();
    Config.saveRepository(currentRepository.repo);
    super.close();
  }

  private void disposeBalloon() {
    if (balloonTip != null) {
      balloonTip.closeBalloon();
      balloonTip.removeAll();
      balloonTip = null;
    }
  }
  
  protected void showWindowsTip() {
    disposeBalloon();
    createBalloon();
  }

  private void createBalloon() {
    balloonTip = new BalloonTip(repositoryLabel, new JLabel(Images.MSCAPITIP.asIcon()), new EdgedBalloonStyle(Color.WHITE, Color.DARK_GRAY), true);
    balloonTip.getCloseButton().addActionListener((e) -> this.disposeBalloon());
  } 

  @Override
  protected JPanel createSetup() {
    JPanel pnlNorthEast = new JPanel();
    pnlNorthEast.setLayout(new BorderLayout(0, 0));
    pnlNorthEast.add(createConfigInstall(), BorderLayout.NORTH);
    return pnlNorthEast;
  }

  private JPanel createConfigInstall() {
    JPanel panel = new JPanel(new BorderLayout(4, 0));
    panel.add(createOptions(), BorderLayout.WEST);
    panel.add(createRepositoryLabel(), BorderLayout.CENTER);
    panel.add(createRefresh(createEmptyBorder(0,  2,  10,  4)), BorderLayout.EAST);
    panel.setBorder(BorderFactory.createEmptyBorder(15,  0,  0,  0));
    panel.setVisible(hasSavedCallback());
    return panel;
  }

  private JLabel createRepositoryLabel() {
    repositoryLabel = new JLabel("");
    repositoryLabel.setVerticalAlignment(SwingConstants.BOTTOM);    
    repositoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    repositoryLabel.setBorder(BorderFactory.createEmptyBorder(0,  2,  10,  2));
    repositoryLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return repositoryLabel;
  }

  private JPanel createOptions() {
    radioOptions = new JPanel(new BorderLayout());
    radioOptions.add(createStandardRadio(), BorderLayout.WEST);
    radioOptions.add(createMscapiRadio(), BorderLayout.CENTER);

    ButtonGroup group = new ButtonGroup();
    group.add(mscapiButton);
    group.add(standardButton);
    
    return radioOptions;    
  }

  private JRadioButton createMscapiRadio() {
    mscapiButton = new JRadioButton(RepositoryStrategy.MSCAPI.getText()); 
    mscapiButton.setBorder(BorderFactory.createEmptyBorder(0,  10,  9,  15));
    mscapiButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
    mscapiButton.setMnemonic(KeyEvent.VK_W);
    mscapiButton.setActionCommand(RepositoryStrategy.MSCAPI.name());
    mscapiButton.setToolTipText("Certificados reconhecidos pelo sistema Windows (mscapi).");
    mscapiButton.addActionListener(this);
    return mscapiButton;
  }

  private JRadioButton createStandardRadio() {
    standardButton = new JRadioButton(RepositoryStrategy.NATIVE.getText());
    standardButton.setBorder(BorderFactory.createEmptyBorder(0,  0,  9,  0));
    standardButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
    standardButton.setMnemonic(KeyEvent.VK_P);
    standardButton.setActionCommand(RepositoryStrategy.NATIVE.name());
    standardButton.setToolTipText("Certificados reconhecidos nativamente pelo PJeOffice.");
    standardButton.addActionListener(this);
    return standardButton;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    RepositoryStrategy r = RepositoryStrategy.valueOf(e.getActionCommand());
    if (r != currentRepository) {
      (currentRepository = r).update(this, Optional.ofNullable(e));
    }
  }
  
  private static class MouseListener extends MouseAdapter {
    
    private Runnable runnable;
    
    MouseListener(Runnable runnable) {
      this.runnable = runnable;
    }
    
    public void mouseClicked(MouseEvent e) {
      runnable.run();
    }    
  };
  
  
  private MouseListener mscapiListener, standardListener;

  private enum RepositoryStrategy implements Consumer<WindowsCertificateListDialog>, IRepository {
    MSCAPI("Windows", Repository.MSCAPI) {      
      
      @Override
      protected void doUpdate(WindowsCertificateListDialog dialog, Optional<ActionEvent> e) {
        dialog.repositoryLabel.setIcon(Images.WINDOWS.asIcon());
        dialog.repositoryLabel.removeMouseListener(dialog.standardListener);
        dialog.repositoryLabel.setToolTipText("Abrir repositório de certificados do Windows.");
        dialog.repositoryLabel.addMouseListener(dialog.mscapiListener = new MouseListener(() -> accept(dialog)));
        dialog.mscapiButton.setSelected(true);
        e.ifPresent(a -> {
          dialog.switchRepository(repo);
          dialog.refresh();
        });
      }

      @Override
      public void accept(WindowsCertificateListDialog dialog) {
        Throwables.runQuietly(() -> new ProcessBuilder("cmd", "/c", "certmgr.msc").start().waitFor(400, TimeUnit.MILLISECONDS));
        dialog.showWindowsTip();
      }
    },
    
    NATIVE("PJeOffice", Repository.NATIVE) {      
      @Override
      protected void doUpdate(WindowsCertificateListDialog dialog, Optional<ActionEvent> e) {
        dialog.repositoryLabel.setIcon(Images.GEAR.asIcon());
        dialog.repositoryLabel.setToolTipText("Configurar um novo certificado A1 / A3.");
        dialog.repositoryLabel.removeMouseListener(dialog.mscapiListener);
        dialog.repositoryLabel.addMouseListener(dialog.standardListener = new MouseListener(() -> accept(dialog)));
        dialog.standardButton.setSelected(true);
        e.ifPresent(a -> {
          dialog.switchRepository(repo);
          dialog.refresh();
        });
      }

      @Override
      public void accept(WindowsCertificateListDialog dialog) {
        dialog.clickConfig();
        if (dialog.isNeedReload()) {
          dialog.switchRepository(repo);
        }
      }
    };

    static RepositoryStrategy from(Repository repository) {
      try {
        return RepositoryStrategy.valueOf(repository.getName());
      }catch(Exception e) {
        return RepositoryStrategy.NATIVE;
      }
    }
    
    protected final Repository repo;
    
    private final String text;
    
    RepositoryStrategy(String text, Repository repo) {
      this.text = text;
      this.repo = repo;
    }
    
    @Override
    public final String getName() {
      return repo.getName();
    }
    
    final String getText() {
      return text;
    }
    
    protected final RepositoryStrategy update(WindowsCertificateListDialog dialog) {
      return update(dialog, Optional.empty());
    }
    
    protected final RepositoryStrategy update(WindowsCertificateListDialog dialog,  Optional<ActionEvent> e) {
      //insert template code here!
      doUpdate(dialog, e);
      return this;
    }
    
    protected abstract void doUpdate(WindowsCertificateListDialog dialog,  Optional<ActionEvent> e);
  }
}
