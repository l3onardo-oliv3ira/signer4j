package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.security.Signature;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignedData;
import com.github.signer4j.ISignerBuilder;
import com.github.signer4j.ISimpleSigner;
import com.github.signer4j.imp.exception.Signer4JException;

class SimpleSigner extends SecurityObject implements ISimpleSigner {

  private Signature signature;
  
  private SimpleSigner(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }
  
  @Override
  public final ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    Args.requireNonEmpty(content, "content is null");
    Args.requireZeroPositive(offset, "offset is negative");
    Args.requirePositive(length, "length is not positive");
    return invoke(() -> {
      IChoice choice = choose();
      signature.initSign(choice.getPrivateKey());
      signature.update(content, offset, length);
      return SignedData.from(signature.sign(), choice);
    });
  }
  
  public static class Builder implements ISignerBuilder {
    
    private ISignatureAlgorithm algorithm = SignatureAlgorithm.SHA1withRSA;
    
    private final Runnable dispose;
    
    private final ICertificateChooser chooser;
    
    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = requireNonNull(chooser, "chooser is null");
      this.dispose = requireNonNull(dispose, "dispose is null");
    }

    @Override
    public final ISignerBuilder usingAlgorigthm(ISignatureAlgorithm algorithm) {
      this.algorithm = requireNonNull(algorithm, "Unabled to using null algorigthm");
      return this;
    }
    
    @Override
    public final ISimpleSigner build() {
      ProviderInstaller.BC.install();
      SimpleSigner signer = new SimpleSigner(chooser, dispose);
      String aName = algorithm.getName();
      signer.signature = Throwables.tryRuntime(
        () -> Signature.getInstance(aName), 
        "Algorítimo " + aName + " é desconhecido"
      );
      return signer;
    }
  }
}
