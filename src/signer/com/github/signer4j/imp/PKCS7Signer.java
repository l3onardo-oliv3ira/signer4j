package com.github.signer4j.imp;

import static com.github.utils4j.imp.Args.requireNonNull;
import static org.bouncycastle.util.Arrays.copyOfRange;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.IPKCS7Signer;
import com.github.signer4j.IPKCS7SignerBuilder;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.OpenByteArrayOutputStream;
import com.github.utils4j.imp.Strings;
import com.github.utils4j.imp.Throwables;

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

  private ISignatureType signatureType;
  
  private String[] emailAddress;
  
  private String[] unstructuredName;
  
  private String challengePassword;
  
  private String[] unstructuredAddress;
  
  private byte[] signatureTimestamp;
  
  private PKCS7Signer(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }

  @Override
  public ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    return invoke(() -> {
      messageDigest.update(content, offset, length);

      final byte[] hashContent = messageDigest.digest();

      List<PKCS9Attribute> attrList = new ArrayList<>();
      attrList.add(new PKCS9Attribute(PKCS9Attribute.CONTENT_TYPE_OID, ContentInfo.DATA_OID));
      attrList.add(new PKCS9Attribute(PKCS9Attribute.SIGNING_TIME_OID, new Date()));
      attrList.add(new PKCS9Attribute(PKCS9Attribute.MESSAGE_DIGEST_OID, hashContent));
      if (!Containers.isEmpty(emailAddress))
        attrList.add(new PKCS9Attribute(PKCS9Attribute.EMAIL_ADDRESS_OID, emailAddress));
      if (!Containers.isEmpty(unstructuredName))
        attrList.add(new PKCS9Attribute(PKCS9Attribute.UNSTRUCTURED_NAME_OID, unstructuredName));
      if (Strings.hasText(challengePassword))
        attrList.add(new PKCS9Attribute(PKCS9Attribute.CHALLENGE_PASSWORD_OID, challengePassword));
      if (!Containers.isEmpty(unstructuredAddress))
        attrList.add(new PKCS9Attribute(PKCS9Attribute.UNSTRUCTURED_ADDRESS_OID, unstructuredAddress));
      if (!Containers.isEmpty(signatureTimestamp))
        attrList.add(new PKCS9Attribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID, signatureTimestamp));
      PKCS9Attribute[] attributesArray = attrList.toArray(new PKCS9Attribute[attrList.size()]);
      final PKCS9Attributes attributes =  new PKCS9Attributes(attributesArray);
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

    private byte[] signatureTimestamp = new byte[0];

    private String challengePassword = Strings.empty();

    private String[] emailAddress = Strings.emptyArray();
    
    private String[] unstructuredName = Strings.emptyArray();
    
    private String[] unstructuredAddress = Strings.emptyArray();
    
    private ISignatureType signatureType = SignatureType.ATTACHED;
    
    private ISignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.SHA1withRSA;
    
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
    public IPKCS7SignerBuilder usingEmailAddress(String... emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    @Override
    public IPKCS7SignerBuilder usingUnstructuredName(String... unstructuredName) {
      this.unstructuredName = unstructuredName;
      return this;
    }

    @Override
    public IPKCS7SignerBuilder usingChallengePassword(String challengePassword) {
      this.challengePassword = challengePassword;
      return this;
    }

    @Override
    public IPKCS7SignerBuilder usingUnstructuredAddress(String... unstructuredAddress) {
      this.unstructuredAddress = unstructuredAddress;
      return this;
    }

    @Override
    public IPKCS7SignerBuilder usingSignatureTimestamp(byte[] signatureTimestamp) {
      this.signatureTimestamp = signatureTimestamp;
      return this;
    }
    
    @Override
    public final IPKCS7Signer build() {
      PKCS7Signer signer = new PKCS7Signer(chooser, dispose);
      final String signatureAlgorithm = this.signatureAlgorithm.getName();
      signer.signature = Throwables.tryRuntime(
        () -> Signature.getInstance(signatureAlgorithm), 
        "Algorítimo " + signatureAlgorithm + " é desconhecido"
      );
      final String hashAlgorithm = this.signatureAlgorithm.getHashName();
      signer.messageDigest = Throwables.tryRuntime(
        () -> MessageDigest.getInstance(hashAlgorithm),
        "Algorítimo " + hashAlgorithm + " é desconhecido"
      );
      signer.signatureType = signatureType;
      signer.challengePassword = challengePassword;
      signer.emailAddress = emailAddress;
      signer.signatureTimestamp = signatureTimestamp;
      signer.unstructuredAddress = unstructuredAddress;
      signer.unstructuredName = unstructuredName;
      return signer;
    }
  }
}
