# signer4j

A simpler way to perform digital signature operations with A1 and A3 certificates.

## Listing All Detected Devices
```java
IDeviceManager dm = new DeviceManager();

System.out.println("**************************");
System.out.println("Device info");
dm.getDevices().stream().forEach(d -> {
  IDevice device = d;
  System.out.println("Driver: " + device.getDriver());
  System.out.println("Label: " + device.getLabel());
  System.out.println("Model: " + device.getModel());
  System.out.println("Serial: " + device.getSerialNumber());
});
```

#### Device Interface
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

## Accessing The First Device and Listing Certificates
```java
IDevice device = dm.firstDevice().get();
System.out.println("**************************");
System.out.println("Certificates");
device.getCertificates().stream().forEach(c -> {
  ICertificate certificate = c;
  System.out.println("---------");
  System.out.println("Name: " + certificate.getName());
  System.out.println("After date: " + certificate.getAfterDate());
  System.out.println("Before date: " + certificate.getBeforeDate());
  System.out.println("Email: " + certificate.getEmail().orElse("Unknown"));
  //.... certificate.get*
  if (certificate.hasCertificatePF()) {
    ICertificatePF pf = certificate.getCertificatePF().get();
    System.out.println("CPF: " + pf.getCPF().get());
    //.... pf.get*
  }
  if (certificate.hasCertificatePJ()) {
    ICertificatePJ pj = certificate.getCertificatePJ().get();
    System.out.println("CNPJ: " + pj.getCNPJ().get());
    //.... pj.get*
  }
});
```
#### Certificate Interface
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
## Show Slot Informations
```java
ISlot slot = device.getSlot();

System.out.println("**************************");
System.out.println("Slot information");
System.out.println("Description: " + slot.getDescription());
System.out.println("FirmewareVersion: " + slot.getFirmewareVersion());
System.out.println("HardwareVersion: " + slot.getHardwareVersion());
System.out.println("ManufacturerId: " + slot.getManufacturerId());
System.out.println("Number: " + slot.getNumber());
System.out.println("Serial: " + slot.getSerial());
```
#### Slot Interface
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
## Token Abstraction For a Slot
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
#### Token Interface
```java
public interface IToken extends IGadget{
  default IToken login() throws KeyStoreAccessException { 
    return login(p -> {});
  }
  
  default IToken login(IPasswordCollector collector) throws KeyStoreAccessException {
    login(new JDialogPasswordCallbackHandler(this, collector));
    return this;
  }
  
  default IToken login(char[] password) throws KeyStoreAccessException {
    login(new LiteralPasswordCallbackHandler(password));
    return this;
  }

  void login(IPasswordCallbackHandler callback) throws KeyStoreAccessException;

  String getManufacture();
  
  long getMinPinLen();
  long getMaxPinLen();
  
  boolean isAuthenticated();
  
  TokenType getType();
  ISlot getSlot();
  
  IKeyStoreAccess getKeyStoreAccess();
  ICertificates getCertificates();
  
  ISignerBuilder signerBuilder();
  ISignerBuilder signerBuilder(ICertificateChooserFactory factory);
  
  ICMSSignerBuilder cmsSignerBuilder();
  ICMSSignerBuilder cmsSignerBuilder(ICertificateChooserFactory factory);
  
  void logout();
}
```
