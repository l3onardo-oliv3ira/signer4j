package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.util.Arrays;

import com.github.signer4j.ICMSSigner;
import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.KeyStoreAccessException;

class CMSSigner extends SecurityObject implements ICMSSigner {

  private long memoryLimit; 
      
  private ISignatureAlgorithm algorithm;
  
  private ISignatureType signatureType;
  
  private boolean hasNoSignedAttributes;
  
  private CMSSigner(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }
  
  @Override
  public ISignedData process(byte[] content, int offset, int length) throws KeyStoreAccessException {
    return process(Arrays.copyOfRange(content, offset, offset + length));
  }
  
  @Override
  public ISignedData process(byte[] content) throws KeyStoreAccessException {
    Args.requireNonEmpty(content, "content is null");
    return process(new CMSProcessableByteArray(content), content.length);
  }
  
  @Override
  public ISignedData process(File content) throws KeyStoreAccessException {
    Args.requireNonNull(content, "content is null");
    return process(new CMSProcessableFile(content), content.length());
  }
  
  private ISignedData process(CMSTypedData content, long length) throws KeyStoreAccessException {
    return invoke(() -> {
      final IChoice choice = choose();
      CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
      generator.addSignerInfoGenerator(
        new JcaSimpleSignerInfoGeneratorBuilder()
          .setProvider(choice.getProvider())
          .setDirectSignature(hasNoSignedAttributes)
          .build(
            algorithm.getName(), 
            choice.getPrivateKey(), 
            (X509Certificate)choice.getCertificate() 
          )
      );
      generator.addCertificates(new JcaCertStore(choice.getCertificateChain()));
      CMSSignedData data = generator.generate(
        content, 
        SignatureType.ATTACHED.equals(signatureType)
      );

      /**
       * Se o tamanho é aceitável, calcula em memória
       */
      if (length <= memoryLimit) { 
        return SignedData.from(
          data.getEncoded(ASN1Encoding.DER),
          choice
        );
      }
      
      /**
       * Se o tamanho não é aceitável
       * 1. grava o que se tem até agora em disco, 
       * 2. livra-se dos recursos que estão sendo consumidos
       * 3. Lê do disco usando apenas um volume de memória ao invés de dois em encode
       */
      File tmp = Files.createTempFile("pje_office_tmp", ".pjeoffice").toFile();
      try {
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp))) {
          data.toASN1Structure().encodeTo(out);
        } finally {
          data = null;
          generator = null;
        }
        return SignedData.from(
          Files.readAllBytes(tmp.toPath()), 
          choice
        );
      }finally {
        tmp.delete();
        tmp = null;
      }
    });
  }

  public static class Builder implements ICMSSignerBuilder {
    
    private long memoryLimit = 50 * 1024 * 1024;
    
    private ISignatureAlgorithm algorithm = SignatureAlgorithm.SHA1withRSA;
    
    private final ICertificateChooser chooser;
    
    private boolean hasNoSignedAttributes = false;
    
    private ISignatureType signatureType = SignatureType.ATTACHED;
    
    private final Runnable dispose;
    
    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = requireNonNull(chooser, "chooser is null");
      this.dispose = requireNonNull(dispose, "dispose is null");
    }
    
    @Override
    public final ICMSSignerBuilder usingMemoryLimit(long memoryLimit) {
      this.memoryLimit = Args.requireZeroPositive(memoryLimit, "memory limit is invalid");
      return this;
    }

    @Override
    public final ICMSSignerBuilder usingAlgorigthm(ISignatureAlgorithm algorithm) { 
      this.algorithm = requireNonNull(algorithm, "Unabled to using null algorithm");
      return this;
    }
    
    @Override
    public final ICMSSignerBuilder usingSignatureType(ISignatureType signatureType) {
      this.signatureType = requireNonNull(signatureType, "signatureType is null");
      return this;
    }
    
    @Override
    public final ICMSSignerBuilder usingAttributes(boolean hasSignedAttributes) {
      this.hasNoSignedAttributes = !hasSignedAttributes; //a lógica da api é invertida!
      return this;
    }
    
    @Override
    public final ICMSSigner build() {
      Providers.installBouncyCastleProvider();
      CMSSigner signer = new CMSSigner(chooser, dispose);
      signer.memoryLimit = memoryLimit;
      signer.algorithm = this.algorithm;
      signer.signatureType = this.signatureType;
      signer.hasNoSignedAttributes = hasNoSignedAttributes;
      return signer;
    }
  }
}
