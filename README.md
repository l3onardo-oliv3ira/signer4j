# Signer4j

A simpler way to perform digital signature operations with A1 and A3 certificates.

## Listing automatic detected devices
```java
IDeviceManager dm = new DeviceManager();

System.out.println("Device info");
dm.getDevices().stream().forEach(d -> {
  IDevice device = d;
  System.out.println("Driver: " + device.getDriver());
  System.out.println("Label: " + device.getLabel());
  System.out.println("Model: " + device.getModel());
  System.out.println("Serial: " + device.getSerialNumber());
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
  
  List<IDriver> getDrivers();
  List<IDriver> getDrivers(boolean forceReload);
  List<IDriver> getDrivers(Predicate<IDriver> predicate);
  List<IDriver> getDrivers(Predicate<IDriver> predicate, boolean forceReload);
  
  Optional<IDriver> firstDriver();
  Optional<IDriver> firstDriver(boolean forceReload);
  
  void install(List<Path> pkcs12Files);
  void uninstall(List<Path> pkcs12File);
  void uninstallPkcs12();
  void close();
}
```
#### Device interface
```java
public interface IGadget {
  String getLabel();
  String getModel();
  String getSerialNumber();
}
public interface IDevice extends IGadget {
  enum Type { A1, A3 }
  Type getType();
  String getDriver();
  ISlot getSlot();
  ICertificates getCertificates();
}
```

## Access to the first device and certificate listing
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
public interface ICertificate {
  Date getAfterDate();
  Date getBeforeDate();
  
  IDistinguishedName getCertificateIssuerDN();
  IDistinguishedName getCertificateSubjectDN();
  
  List<String> getCRLDistributionPoint() throws IOException;
  
  Optional<String> getEmail();
  Optional<ICertificatePF> getCertificatePF();
  Optional<ICertificatePJ> getCertificatePJ();
  
  KeyUsage getKeyUsage();
  ISubjectAlternativeNames getSubjectAlternativeNames();
  
  String getName();
  String getSerialNumber();
  
  X509Certificate getX509Certificate();
  
  boolean hasCertificatePF();
  boolean hasCertificatePJ();
}
```
## Show slot informations
```java
ISlot slot = device.getSlot();

System.out.println("Slot information");
System.out.println("Description: " + slot.getDescription());
System.out.println("FirmewareVersion: " + slot.getFirmewareVersion());
System.out.println("HardwareVersion: " + slot.getHardwareVersion());
System.out.println("ManufacturerId: " + slot.getManufacturerId());
System.out.println("Number: " + slot.getNumber());
System.out.println("Serial: " + slot.getSerial());
```
#### Slot interface
```java
public interface ISlot {
  long getNumber();
  IToken getToken();
  IDevice toDevice();
  String getDescription();
  String getManufacturerId();
  String getHardwareVersion();
  String getFirmewareVersion();
  String getSerial();
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
  System.out.println("Your token is blocked");
} catch (InvalidPinException e) {
  System.out.println("Your password is incorrect!");
} catch (NoTokenPresentException e) {
  System.out.println("Your token is not connected to USB");
} catch (LoginCanceledException e) {
  System.out.println("Authentication canceled by user");
} catch (KeyStoreAccessException e) {
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
  int chainSize();
}
public interface ISignedData extends IPersonalData {
  byte[] getSignature();
  void writeTo(OutputStream out) throws IOException;
  String getSignature64();
  String getCertificate64() throws CertificateException;
  String getCertificateChain64() throws CertificateException;
}
```
#### Token interface
```java
public interface IToken extends IGadget{
  IToken login() throws KeyStoreAccessException;
  IToken login(IPasswordCollector collector) throws KeyStoreAccessException;
  IToken login(char[] password) throws KeyStoreAccessException;

  void login(IPasswordCallbackHandler callback) throws KeyStoreAccessException;
  void logout();

  long getMinPinLen();
  long getMaxPinLen();
  
  String getManufacture();
  boolean isAuthenticated();
  
  TokenType getType();
  ISlot getSlot();
  
  IKeyStoreAccess getKeyStoreAccess();
  ICertificates getCertificates();
  
  ISignerBuilder signerBuilder();
  ISignerBuilder signerBuilder(ICertificateChooserFactory factory);
  
  ICMSSignerBuilder cmsSignerBuilder();
  ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory);
}
```

## Signing file to .p7s output
```java
try {
  token.login();

  ICMSSigner cmsSigner = token.cmsSignerBuilder()
      .usingAlgorigthm(SignatureAlgorithm.SHA1withRSA)
      .usingAttributes(true)
      .usingMemoryLimit(50 * 1024 * 1024)
      .usingSignatureType(SignatureType.ATTACHED)
      .build();

  ISignedData data = cmsSigner.process(new File("./input.pdf"));

  try(OutputStream out = new FileOutputStream(new File("./input.pdf.p7s"))) {
    data.writeTo(out);
  }
} catch (TokenLockedException e) {
  System.out.println("Your token is blocked");
} catch (InvalidPinException e) {
  System.out.println("Your password is incorrect!");
} catch (NoTokenPresentException e) {
  System.out.println("Your token is not connected to USB");
} catch (LoginCanceledException e) {
  System.out.println("Authentication canceled by user");
} catch (KeyStoreAccessException e) {
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
  ISignedData process(byte[] content, int offset, int length) throws KeyStoreAccessException;
  ISignedData process(byte[] content) throws KeyStoreAccessException;
  ISignedData process(File content) throws KeyStoreAccessException, IOException;
  ISignedData process(String content) throws KeyStoreAccessException;
  ISignedData process(String content, Charset charset) throws KeyStoreAccessException;
  byte[] processRaw(byte[] content) throws KeyStoreAccessException;
  byte[] processRaw(String content) throws KeyStoreAccessException;
  byte[] processRaw(String content, Charset charset) throws KeyStoreAccessException;
  String process64(byte[] content) throws KeyStoreAccessException;
  String process64(String content) throws KeyStoreAccessException;
  String process64(String content, Charset charset) throws KeyStoreAccessException;
  String process64(File input) throws KeyStoreAccessException, IOException;
}

public interface ISimpleSigner extends IByteProcessor{
}
public interface ICMSSigner extends IByteProcessor {
}
```
