package com.github.signer4j.imp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Wtsapi32;

class ForWindowsLockDettector implements IWindowLockDettector {

  public static final Logger LOGGER = LoggerFactory.getLogger(IWindowLockDettector.class);
  
  private final List<IWorkstationLockListener> listeners = new ArrayList<>();

  private Thread thread;

  private WorkStation workstation;
  
  @Override
  public IWindowLockDettector notifyTo(IWorkstationLockListener listener) {
    if (listener != null) {
      synchronized(this.listeners) {
        this.listeners.add(listener);
      }
    }
    return this;
  }
  
  private class WorkStation implements WinUser.WindowProc, Runnable {

    private final String windowClass = "WorkStationClass_" + System.currentTimeMillis();
    
    private WinDef.HWND windowHandle;

    private boolean fail = false;
    
    private WorkStation() {
      error("Falha em GetModuleHandle");
      WinUser.WNDCLASSEX wClass = new WinUser.WNDCLASSEX();
      wClass.hInstance = null;
      wClass.lpfnWndProc = this;
      wClass.lpszClassName = windowClass;
      User32.INSTANCE.RegisterClassEx(wClass);
      error("Falha em RegisterClassEx");
    }
    
    public void info(String message) {
      LOGGER.info(message);
    }

    public int error(String message) {
      int rc = Kernel32.INSTANCE.GetLastError();
      if (rc != 0) {
        message += ". Codigo: " + rc;
        LOGGER.error(message);
      }
      return rc;
    }

    public WinDef.LRESULT callback(WinDef.HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
      switch (uMsg) {
      case 2:
        User32.INSTANCE.PostQuitMessage(0);
        return new WinDef.LRESULT(0L);
      case WinUser.WM_SESSION_CHANGE:
        onSessionChange(wParam, lParam);
        return new WinDef.LRESULT(0L);
      } 
      WinDef.LRESULT r = User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
      error("Falha em DefWindowProc");
      return r;
    }

    protected void onSessionChange(WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
      switch (wParam.intValue()) {
      case Wtsapi32.WTS_CONSOLE_DISCONNECT:
      case Wtsapi32.WTS_SESSION_LOGOFF:
      case  Wtsapi32.WTS_SESSION_LOCK:
        synchronized(listeners) {
          Throwables.tryRun(() -> listeners.forEach(l -> l.onMachineLocked(lParam.intValue())));
        }
        break;
      case Wtsapi32.NOTIFY_FOR_ALL_SESSIONS:
      case Wtsapi32.WTS_SESSION_LOGON:
      case Wtsapi32.WTS_SESSION_UNLOCK:
        synchronized(listeners) {
          Throwables.tryRun(() -> listeners.forEach(l -> l.onMachineUnlocked(lParam.intValue())));
        }
        break;
      } 
    }

    public void run() {
      windowHandle = User32.INSTANCE.CreateWindowEx(8, 
        windowClass, 
        "WorkstationLockListening", 
        0, 0, 0, 0, 0, null, null, 
        null, null
      );
      if (windowHandle == null) { 
        fail = true;
        error("Não foi possível a criação da janela de monitoração login/logout");
        return;
      }
      info("Criada janela de monitoração login/logout. Handle: " + windowHandle.getPointer().toString());
      Wtsapi32.INSTANCE.WTSRegisterSessionNotification(windowHandle, 0);
      error("Não foi possível registrar notificação de sessão no SO");
      info("Registrada notificação de sessão do SO");
      WinUser.MSG msg = new WinUser.MSG();
      while (User32.INSTANCE.GetMessage(msg, windowHandle, 0, 0) != 0) {
        User32.INSTANCE.TranslateMessage(msg);
        User32.INSTANCE.DispatchMessage(msg);
      } 
      Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(windowHandle);
      error("Não foi possível desativar notificação de sessão do SO");
      User32.INSTANCE.DestroyWindow(windowHandle);
      error("Não foi possível a destruição da janela de monitoração login/logout");
      info("Thread de monitoração log(in/off) finalizada");
    }

    public void interrupt(Thread thread) {
      thread.interrupt();
      if (windowHandle != null) {
        User32.INSTANCE.PostMessage(windowHandle, WinUser.WM_QUIT, null, null);
        try {
          thread.join();
        } catch (InterruptedException e) {
          error("Não foi possível aguardar a finalização da thread de monitoração de logon. Exceção: " + e.getMessage());
        } finally {
          User32.INSTANCE.UnregisterClass(windowClass, null);
          error("Não foi possível desregistrar classe de janela login/logout");
        }
      }
    }
  }


  @Override
  public void start() {
    if (this.thread == null) {
      this.thread = new Thread(workstation = new WorkStation());
      this.thread.start();
    }
  }

  @Override
  public void stop() {
    if (this.thread != null) {
      LOGGER.debug("Interrompendo monitoração login/logoff");
      try {
        workstation.interrupt(this.thread);
        LOGGER.debug("Interrompido com sucesso!");
      }finally {
        synchronized(this.listeners) {
          this.listeners.clear();
        }
        this.thread = null;
      }
    }
  }
}
