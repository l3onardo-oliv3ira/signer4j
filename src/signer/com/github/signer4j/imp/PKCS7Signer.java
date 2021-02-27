package com.github.signer4j.imp;

import static com.github.signer4j.imp.Args.requireNonNull;
import static com.github.signer4j.imp.Signer4JInvoker.INVOKER;
import static com.github.signer4j.imp.Throwables.tryCall;
import static org.bouncycastle.util.Arrays.copyOfRange;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Date;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IPKCS7Signer;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.Signer4JException;

import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs.PKCS9Attributes;
import sun.security.pkcs.SignerInfo;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

import sun.security.x509.X500Name;

@SuppressWarnings("restriction")
class PKCS7Signer extends SecurityObject implements IPKCS7Signer {

  private Signature signature;

  private MessageDigest messageDigest;

  public ISignatureType signatureType;

  private PKCS7Signer(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }

  @Override
  public ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    return INVOKER.invoke(() -> {
      messageDigest.update(content, offset, length);

      final byte[] hashContent = messageDigest.digest();

      final PKCS9Attributes attributes =  new PKCS9Attributes(new PKCS9Attribute[] { 
        new PKCS9Attribute(PKCS9Attribute.CONTENT_TYPE_OID, ContentInfo.DATA_OID), 
        new PKCS9Attribute(PKCS9Attribute.SIGNING_TIME_OID, new Date()), 
        new PKCS9Attribute(PKCS9Attribute.MESSAGE_DIGEST_OID, hashContent) 
      });
      
      final IChoice choice = choose();
      final X509Certificate certificate = (X509Certificate)choice.getCertificate();
      final PrivateKey privateKey = choice.getPrivateKey();
      signature.initSign(privateKey);
      signature.update(attributes.getDerEncoding());
      
      final AlgorithmId hashAlgorithm = AlgorithmId.get(messageDigest.getAlgorithm());
      final SignerInfo signerInfo = new SignerInfo(
        new X500Name(certificate.getIssuerX500Principal().getName()),
        certificate.getSerialNumber(),
        hashAlgorithm,
        attributes,
        AlgorithmId.get(signature.getAlgorithm()),
        signature.sign(),
        null
      );
      
      DerValue value = null;
      
      if (SignatureType.ATTACHED.equals(signatureType)) {
        value = new DerValue(DerValue.tag_OctetString, 
          offset == 0 && content.length == length ? 
          content : 
          copyOfRange(content, offset, offset + length)
        );
      }

      final PKCS7 p7 = new PKCS7(
        new AlgorithmId[] { hashAlgorithm }, 
        new ContentInfo(ContentInfo.DATA_OID, value),
        new X509Certificate[] { certificate }, 
        new SignerInfo[] { signerInfo }
      );

      try(OpenByteArrayOutputStream output = new OpenByteArrayOutputStream()) {
        p7.encodeSignedData(output);
        return SignedData.from(output.toByteArray(), choice);
      }
    });
  }
  
  public static class Builder implements IPKCS7SignerBuilder {

    private ISignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.SHA1withRSA;
    
    private ISignatureType signatureType = SignatureType.ATTACHED;
    
    private final Runnable dispose;

    private final ICertificateChooser chooser;

    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = requireNonNull(chooser, "chooser is null");
      this.dispose = requireNonNull(dispose, "dispose is null");
    }

    @Override
    public IPKCS7SignerBuilder usingSignatureAlgorithm(ISignatureAlgorithm signatureAlgorithm) {
      this.signatureAlgorithm = Args.requireNonNull(signatureAlgorithm, "signatureAlgorithm is null");
      return this;
    }

    @Override
    public IPKCS7SignerBuilder usingSignatureType(ISignatureType signatureType) {
      this.signatureType = Args.requireNonNull(signatureType, "signatureType is null");
      return this;
    }
    
    @Override
    public final IPKCS7Signer build() {
      PKCS7Signer signer = new PKCS7Signer(chooser, dispose);
      final String signatureAlgorithm = this.signatureAlgorithm.getName();
      signer.signature = tryCall(
          () -> Signature.getInstance(signatureAlgorithm), 
          () -> "Algorítimo " + signatureAlgorithm + " é desconhecido"
          );
      final String hashAlgorithm = this.signatureAlgorithm.getHashName();
      signer.messageDigest = tryCall(
          () -> MessageDigest.getInstance(hashAlgorithm),
          () -> "Algorítimo " + hashAlgorithm + " é desconhecido"
          );
      signer.signatureType = signatureType;
      return signer;
    }
  }
}
