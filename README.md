# Signer4j

A simpler way to perform digital signature operations with A1 and A3 certificates.

## Listing automatic detected devices
```java
IDeviceManager dm = new DeviceManager();

dm.getDevices().stream().forEach(d -> {
  IDevice device = d;
  System.out.println("Driver: " + device.getDriver());
  System.out.println("Label: " + device.getLabel());
  System.out.println("Model: " + device.getModel());
  System.out.println("Serial: " + device.getSerial());
});
```
#### Device manager interface
```java
public interface IDeviceManager {
  List<IDevice> getDevices();
  List<IDevice> getDevices(boolean forceReload);
  List<IDevice> getDevices(Predicate<IDevice> predicate);
  List<IDevice> getDevices(Predicate<IDevice> predicate, boolean forceReload);
  
  Optional<IDevice> firstDevice();
  Optional<IDevice> firstDevice(boolean forceReload);
  Optional<IDevice> firstDevice(Predicate<IDevice> predicate);
  Optional<IDevice> firstDevice(Predicate<IDevice> predicate, boolean forceReload);
 
  void close();
}

```
#### Device interface
```java
public interface ISerialItem {
  String getManufacturer();
  String getSerial();
}
public interface IGadget extends ISerialItem {
  String getLabel();
  String getModel();
}
public interface IDevice extends IGadget {
  enum Type { A1, A3 }
  Type getType();
  String getDriver();
  ISlot getSlot();
  ICertificates getCertificates();
}
```

## First device access and certificate listing
```java
IDevice device = dm.firstDevice().get();
System.out.println("Certificates");
device.getCertificates().stream().forEach(c -> {
  System.out.println("---------");
  System.out.println("Name: " + c.getName());
  System.out.println("After date: " + c.getAfterDate());
  System.out.println("Before date: " + c.getBeforeDate());
  System.out.println("Email: " + c.getEmail().orElse("Unknown"));
  //.... c.get*
  
  if (certificate.hasCertificatePF()) {
    ICertificatePF pf = c.getCertificatePF().get();
    System.out.println("CPF: " + pf.getCPF().get());
    //.... pf.get*
  
  }
  if (certificate.hasCertificatePJ()) {
    ICertificatePJ pj = c.getCertificatePJ().get();
    System.out.println("CNPJ: " + pj.getCNPJ().get());
    //.... pj.get*
  
  }
});
```
#### Certificate interface
```java
public interface ICertificate extends ISerialItem {
  Date getAfterDate();
  Date getBeforeDate();
  
  IDistinguishedName getCertificateIssuerDN();
  IDistinguishedName getCertificateSubjectDN();
  
  List<String> getCRLDistributionPoint() throws IOException;
  
  Optional<String> getEmail();
  Optional<ICertificatePF> getCertificatePF(); //full abstraction for Pessoa Física
  Optional<ICertificatePJ> getCertificatePJ(); //full abstraction for Pessoa Jurídica
  
  KeyUsage getKeyUsage();
  ISubjectAlternativeNames getSubjectAlternativeNames();
  
  String getName();
  
  X509Certificate toX509();
  
  boolean hasCertificatePF();
  boolean hasCertificatePJ();
}
```
## Brazilian abstraction for individual and legal entity certificates
```java
public interface ICertificatePF {
  Optional<String> getCPF();
  Optional<LocalDate> getBirthDate();
  Optional<String> getNis();
  Optional<String> getRg();
  Optional<String> getIssuingAgencyRg();
  Optional<String> getUfIssuingAgencyRg();
  Optional<String> getElectoralDocument();
  Optional<String> getSectionElectoralDocument();
  Optional<String> getZoneElectoralDocument();
  Optional<String> getCityElectoralDocument();
  Optional<String> getUFElectoralDocument();
  Optional<String> getCEI();
}

public interface ICertificatePJ {
  Optional<String> getResponsibleName();
  Optional<String> getResponsibleCPF();
  Optional<String> getCNPJ();
  Optional<String> getBirthDate();
  Optional<String> getBusinessName();
  Optional<String> getNis();
  Optional<String> getRg();
  Optional<String> getIssuingAgencyRg();
  Optional<String> getUfIssuingAgencyRg();
  Optional<String> getCEI();
}
```
## Show slot informations
```java
ISlot slot = device.getSlot();

System.out.println("Slot information");
System.out.println("Description: " + slot.getDescription());
System.out.println("FirmewareVersion: " + slot.getFirmewareVersion());
System.out.println("HardwareVersion: " + slot.getHardwareVersion());
System.out.println("Manufacturer: " + slot.getManufacturer());
System.out.println("Number: " + slot.getNumber());
System.out.println("Serial: " + slot.getSerial());
```
#### Slot interface
```java
public interface ISlot extends ISerialItem {
  long getNumber();
  IToken getToken();
  IDevice toDevice();
  String getDescription();
  String getHardwareVersion();
  String getFirmewareVersion();
}
```
## Token abstraction for a slot
```java
IToken token = slot.getToken();

try {
  token.login();

  String message = "Hello world!";
  
  ISimpleSigner simpleSigner = token.signerBuilder().build();
  
  ISignedData data = simpleSigner.process(message);
  
  System.out.println("base64 signed data" + data.getSignature64());
} catch (TokenLockedException e) {
  System.out.println("Your token is locked");
} catch (InvalidPinException e) {
  System.out.println("Your password is incorrect!");
} catch (NoTokenPresentException e) {
  System.out.println("Your token is not connected to USB");
} catch (LoginCanceledException e) {
  System.out.println("Authentication canceled by user");
} catch (Signer4JException e) {
  System.out.println(e.getMessage());
} finally {
  token.logout(); 
}
```
#### Signed data interface
```java
public interface IPersonalData {
  PrivateKey getPrivateKey();
  Certificate getCertificate();
  List<Certificate> getCertificateChain();
  String getCertificate64() throws CertificateException;
  String getCertificateChain64() throws CertificateException;
  int chainSize();
}

public interface ISignedData extends IPersonalData {
  byte[] getSignature();
  String getSignature64();
  void writeTo(OutputStream out) throws IOException;
}
```
#### Token interface
```java
public interface IToken extends IGadget {
  IToken login() throws Signer4JException;
  IToken login(char[] password) throws Signer4JException;
  IToken login(IPasswordCollector collector) throws Signer4JException;
  IToken login(IPasswordCallbackHandler callback) throws Signer4JException;

  void logout();

  long getMinPinLen();
  long getMaxPinLen();
  
  String getManufacturer();
  boolean isAuthenticated();
  
  ISlot getSlot();
  TokenType getType();
  
  ICertificates getCertificates();
  IKeyStoreAccess getKeyStoreAccess();
  
  ISignerBuilder signerBuilder();
  ISignerBuilder signerBuilder(ICertificateChooserFactory factory);
  
  ICMSSignerBuilder cmsSignerBuilder();
  ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory);
  
  IPKCS7SignerBuilder pkcs7SignerBuilder();
  IPKCS7SignerBuilder pkcs7SignerBuilder(ICertificateChooserFactory factory);
}
```

## Signing file to .p7s output
```java
try {
  token.login();

  ICMSSigner cmsSigner = token.cmsSignerBuilder()
      .usingAlgorithm(SignatureAlgorithm.SHA1withRSA)
      .usingAttributes(true)
      .usingMemoryLimit(50 * 1024 * 1024)
      .usingSignatureType(SignatureType.ATTACHED)
      .build();

  ISignedData data = cmsSigner.process(new File("./input.pdf"));

  try(OutputStream out = new FileOutputStream(new File("./input.pdf.p7s"))) {
    data.writeTo(out);
  }
} catch (TokenLockedException e) {
  System.out.println("Your token is locked");
} catch (InvalidPinException e) {
  System.out.println("Your password is incorrect!");
} catch (NoTokenPresentException e) {
  System.out.println("Your token is not connected to USB");
} catch (LoginCanceledException e) {
  System.out.println("Authentication canceled by user");
} catch (Signer4JException e) {
  System.out.println(e.getMessage());
} catch (IOException e) {
  System.out.println("Unabled to read input file");
} finally {
  token.logout(); 
}
```

#### Byte processor interface
```java
public interface IByteProcessor {
  ISignedData process(byte[] content, int offset, int length) throws Signer4JException;
  ISignedData process(byte[] content) throws Signer4JException;
  ISignedData process(File content) throws Signer4JException, IOException;
  ISignedData process(String content) throws Signer4JException;
  ISignedData process(String content, Charset charset) throws Signer4JException;
  byte[] processRaw(byte[] content) throws Signer4JException;
  byte[] processRaw(String content) throws Signer4JException;
  byte[] processRaw(String content, Charset charset) throws Signer4JException;
  String process64(byte[] content) throws Signer4JException;
  String process64(String content) throws Signer4JException;
  String process64(String content, Charset charset) throws Signer4JException;
  String process64(File input) throws Signer4JException, IOException;
}

```
