package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Throwables.tryRun;

import java.beans.Transient;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.github.signer4j.ILibraryAware;
import com.github.signer4j.ISlot;
import com.github.signer4j.exception.DriverException;
import com.github.signer4j.exception.DriverFailException;

import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

@SuppressWarnings("restriction")
class PKCS11Driver extends AbstractDriver implements ILibraryAware {

  private static Map<?, ?> bugMap;
  
  static {
    tryRun(() -> {
      Field field = PKCS11.class.getDeclaredField("moduleMap");
      field.setAccessible(true);
      bugMap = (Map<?, ?>)field.get(null);
    });
  }
  
  private PKCS11 pk;
  
  private final Path library;
  
  PKCS11Driver(Path library) {
    this.library = requireNonNull(library, "null library").toAbsolutePath();
  }

  @Transient
  final PKCS11 getPK() {
    return pk;
  }
  
  @Override
  public final String getId() {
    return getLibrary();
  }
  
  @Override
  public final String getLibrary() {
    return library.toFile().getAbsolutePath();
  }
  
  @Override
  protected final boolean isSame(AbstractDriver obj) {
    return Streams.isSame(library, ((PKCS11Driver)obj).library);
  }
  
  @Override
  protected void loadSlots(List<ISlot> output) throws DriverException {
    bugMap.remove(getLibrary());

    final CK_C_INITIALIZE_ARGS initArgs = new CK_C_INITIALIZE_ARGS();

    try {
        this.pk = PKCS11.getInstance(getLibrary(), "C_GetFunctionList", initArgs, false);
    } catch (IOException | PKCS11Exception e) {
      throw new DriverFailException("Unabled to create PKCS11 instance", e);
    }
    
    try {
      long[] slots = new long[0];
      
      try {
        slots = pk.C_GetSlotList(true);
      } catch (PKCS11Exception e) {
        throw new DriverFailException("Unabled to list slot from driver: " + this, e);
      }
      
      if (slots.length >= 1) {
        
        try {
          pk.C_GetMechanismList(slots[0]); //prelist
        } catch (PKCS11Exception e) {
          throw new DriverFailException("Unabled to list mechanism from: " + this, e);
        }
        
        for(final long slot: slots) {
          try {
            PKCS11Slot s = new PKCS11Slot(this, slot);
            output.add(s);
            addDevice(s.toDevice());
          }catch(DriverException e) {
            handleException(e);
          }
        }
      }
    }finally {
      try {
        this.pk.C_Finalize(null);
      } catch (PKCS11Exception e) {
        throw new DriverFailException("Unabled to finalize PKCS11 driver: " + this, e);
      }
    }
  }
}
