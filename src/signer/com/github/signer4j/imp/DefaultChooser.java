package com.github.signer4j.imp;

import java.util.List;
import java.util.Optional;

import com.github.signer4j.ICertificateListUI.ICertificateEntry;
import com.github.signer4j.ICertificates;
import com.github.signer4j.IChoice;
import com.github.signer4j.IKeyStoreAccess;
import com.github.signer4j.gui.CertificateListUI;
import com.github.signer4j.imp.exception.Signer4JException;

public class DefaultChooser extends AbstractCertificateChooser {
  
  public DefaultChooser(IKeyStoreAccess keyStore, ICertificates certificates) {
    super(keyStore, certificates);
  }
  
  @Override
  protected IChoice doChoose(List<CertificateEntry> options) throws Signer4JException {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    Optional<ICertificateEntry> ce = CertificateListUI.display((List)options).get();
    if (!ce.isPresent()) {
      return Choice.CANCEL;
    }
    return toChoice((CertificateEntry)ce.get());
  }
}
