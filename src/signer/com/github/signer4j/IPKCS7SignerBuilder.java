package com.github.signer4j;

public interface IPKCS7SignerBuilder extends IFileSignerBuilder<IPKCS7SignerBuilder> {
  
  IPKCS7SignerBuilder usingEmailAddress(String ... emailAddress);
  
  IPKCS7SignerBuilder usingUnstructuredName(String ... unstructuredName);;
  
  IPKCS7SignerBuilder usingChallengePassword(String challengePassword);
  
  IPKCS7SignerBuilder usingUnstructuredAddress(String ... unstructuredAddress);
  
  IPKCS7SignerBuilder usingSignatureTimestamp(byte[] signatureTimestamp);
  
  IPKCS7Signer build();
}
