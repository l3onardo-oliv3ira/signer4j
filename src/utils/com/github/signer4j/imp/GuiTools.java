package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class GuiTools {
  private GuiTools() {}
  
  public static void mouseTracker(final Window dialog) {
    mouseTracker(dialog, 1000);
  }
  
  public static void mouseTracker(final Window dialog, long delayMillis) {
    requireNonNull(dialog, "dialog is null");
    dialog.addWindowListener(new MouseTracker(delayMillis, dialog));
  }
  
  public static void showOnMousePointer(final Window dialog) {
    setMouseLocation(dialog);
    if (!dialog.isVisible()) {
      dialog.setVisible(true);
    }
  }
  
  private static class MouseTracker extends WindowAdapter implements Runnable {
    
    private final Window dialog;
    private final long delayMillis;
    private volatile boolean running = true;
    private final Thread thread = new Thread(this, "Mouse tracker");

    private MouseTracker(long delayMillis, Window dialog) {
      this.delayMillis = delayMillis;
      this.dialog = dialog;
      this.thread.setDaemon(true);
      this.thread.setPriority(Thread.MIN_PRIORITY);
      this.thread.start();
    }
    
    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
      running = false;
    }
    
    @Override
    public void run() {
      while(running)
        try {
          Thread.sleep(delayMillis);
          invokeLater(() -> {
            if (!dialog.isVisible() || !dialog.isDisplayable()) {
              running = false;
            }
            if (running) {
              setMouseLocation(dialog);
            }
          });
        }catch(Throwable e) {
          running = false;
        }
    }
  }

  public static void setMouseLocation(final Window dialog) {
    requireNonNull(dialog, "dialog is null");
    if (dialog instanceof JFrame) {
      JFrame frame = (JFrame)dialog;
      if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
        return;
      }
    }
    final PointerInfo mouseInfo = MouseInfo.getPointerInfo();
    if (mouseInfo == null)
      return;
    final GraphicsDevice mouseDevice = mouseInfo.getDevice();
    if (mouseDevice == null)
      return;
    final GraphicsConfiguration configuration = mouseDevice.getDefaultConfiguration();
    if (configuration == null)
      return;
    final Rectangle bounds = configuration.getBounds();
    if (bounds == null)
      return;
    if (bounds.contains(dialog.getLocation()))
      return;
    final int width = bounds.width, height = bounds.height;
    final int xcoordinate = ((width / 2) - (dialog.getSize().width / 2)) + bounds.x;
    final int ycoordinate = ((height / 2) - (dialog.getSize().height / 2)) + bounds.y;
    dialog.setLocation(xcoordinate, ycoordinate);
    dialog.toFront();
  }
}
