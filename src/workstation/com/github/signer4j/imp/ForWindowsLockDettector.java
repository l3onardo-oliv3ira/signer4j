package com.github.signer4j.imp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.signer4j.IWindowLockDettector;
import com.github.signer4j.IWorkstationLockListener;
import com.sun.jna.Callback;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Wtsapi32;

class ForWindowsLockDettector implements IWindowLockDettector {

  public static final Logger LOGGER = LoggerFactory.getLogger(IWindowLockDettector.class);
  
  private final List<IWorkstationLockListener> listeners = new ArrayList<>();

  private Thread thread;
  
  public ForWindowsLockDettector() {
  }
  
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

    public int getLastError() {
      int rc = Kernel32.INSTANCE.GetLastError();
      if (rc != 0) {
        LOGGER.warn("WorkStation dettector capture error: " + rc);
      }
      return rc;
    }

    public WinDef.LRESULT callback(WinDef.HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
      switch (uMsg) {
      case 2:
        User32.INSTANCE.PostQuitMessage(0);
        return new WinDef.LRESULT(0L);
      case 689:
        onSessionChange(wParam, lParam);
        return new WinDef.LRESULT(0L);
      } 
      return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
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
      String windowClass = "PjeWindowClass";
      WinDef.HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");
      WinUser.WNDCLASSEX wClass = new WinUser.WNDCLASSEX();
      wClass.hInstance = (WinDef.HINSTANCE)hInst;
      wClass.lpfnWndProc = (Callback)this;
      wClass.lpszClassName = windowClass;
      User32.INSTANCE.RegisterClassEx(wClass);
      getLastError();
      WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(8, 
        windowClass, 
        "PjeWorkstationLockListening", 
        0, 0, 0, 0, 0, null, null, 
        (WinDef.HINSTANCE)hInst, null
      );
      getLastError();
      LOGGER.warn("WorkStation dettector windows successfully created! window hwnd: " + hWnd.getPointer().toString());
      Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, 0);
      WinUser.MSG msg = new WinUser.MSG();
      while (!thread.isInterrupted() && User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
        User32.INSTANCE.TranslateMessage(msg);
        User32.INSTANCE.DispatchMessage(msg);
      } 
      Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
      User32.INSTANCE.UnregisterClass(windowClass, (WinDef.HINSTANCE)hInst);
      User32.INSTANCE.DestroyWindow(hWnd);
    }
  }


  @Override
  public void start() {
    if (this.thread == null) {
      this.thread = new Thread(new WorkStation());
      this.thread.start();
    }
  }

  @Override
  public void stop() {
    if (this.thread != null) {
      this.thread.interrupt();
      this.thread = null;
    }
  }
}
