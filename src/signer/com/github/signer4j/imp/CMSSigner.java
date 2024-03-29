/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.signer4j.imp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteRangeArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;

import com.github.signer4j.IByteProcessor;
import com.github.signer4j.ICMSConfigSetup;
import com.github.signer4j.ICMSSigner;
import com.github.signer4j.ICMSSignerBuilder;
import com.github.signer4j.ICertificateChooser;
import com.github.signer4j.IChoice;
import com.github.signer4j.ISignatureAlgorithm;
import com.github.signer4j.ISignatureType;
import com.github.signer4j.ISignedData;
import com.github.signer4j.imp.exception.Signer4JException;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Directory;


class CMSSigner extends SecurityObject implements ICMSSigner {
  
  private long memoryLimit; 
      
  private ISignatureAlgorithm algorithm;
  
  private ISignatureType signatureType;
  
  private ICMSConfigSetup config;
  
  private boolean hasNoSignedAttributes;
  
  private String provider;
  
  private CMSSigner(ICertificateChooser chooser, Runnable dispose) {
    super(chooser, dispose);
  }
  
  @Override
  public ICMSSigner usingAttributes(boolean hasSignedAttributes) {
    hasNoSignedAttributes = !hasSignedAttributes;
    return this;
  }
  
  @Override
  public IByteProcessor config(Object param) {
    config.call(this, param);
    return this;
  }
  
  @Override
  public ISignedData process(byte[] content, int offset, int length) throws Signer4JException {
    Args.requireNonNull(content, "content is null");
    Args.requireZeroPositive(offset, "offset is negative");
    Args.requirePositive(length, "length is not positive");
    return process(new CMSProcessableByteRangeArray(content, offset, length), length);
  }
  
  @Override
  public ISignedData process(File content) throws Signer4JException {
    Args.requireNonNull(content, "content is null");
    return process(new CMSProcessableFile(content), content.length());
  }
  
  private ISignedData process(CMSTypedData content, long length) throws Signer4JException {
    return invoke(() -> {
      final IChoice choice = choose();

      JcaSimpleSignerInfoGeneratorBuilder builder = new JcaSimpleSignerInfoGeneratorBuilder()
        .setDirectSignature(hasNoSignedAttributes);
      
      if (provider != null)
        builder.setProvider(provider);
      
      CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
      generator.addSignerInfoGenerator(builder.build(
        algorithm.getName(), 
        choice.getPrivateKey(), 
        (X509Certificate)choice.getCertificate() 
      ));
      generator.addCertificates(new JcaCertStore(choice.getCertificateChain()));

      CMSSignedData data = generator.generate(
        content, 
        SignatureType.ATTACHED.equals(signatureType)
      );

      /**
       * Nesta instância 'data' acima pode ter pendurado um byte[]/ContentInfo bem grande. Se invocado data.getEncoded(), um segundo
       * volume de byte[] será criado, duplicando o consumo de espaço no Heap da JVM ainda que por um período 
       * curto de tempo. A idéia básica aqui é que se escrevermos no disco o que já se tem em memmória seguido da liberação 
       * da referencia 'data' para a coleta de lixo (não há outras referências na memória então a JVM vai acabar coletando cedo ou tarde), 
       * poderemos usar a memória em um único volume ao invés de dois, o que diminuirá as chances de se alcançar 
       * OutOfMemoryError. Neste cenário um OutOfMemoryError só será alcançado se um ÚNICO arquivo (e NÃO um lote deles) já for 
       * maior do que o todo o Heap da JVM. 
       */

      /**
       * Se o tamanho é aceitável, calcula em memória mesmo deixando "duplicar" temporariamente
       */
      if (length <= memoryLimit) { 
        return SignedData.from(
          data.getEncoded(ASN1Encoding.DER), //ou seria sem o parâmetro?
          choice
        );
      }
      
      /**
       * Se o tamanho não é aceitável
       * 1. grava o que se tem até agora em disco, 
       * 2. livra-se dos recursos que estão sendo consumidos
       * 3. Lê do disco usando apenas um único volume de memória ao invés de dois
       */
      File tmp = Directory.createTempFile("pje_office_tmp", ".pjeoffice");
      try {
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp), 32 * 1024)) {
          data.toASN1Structure().encodeTo(out, ASN1Encoding.DER);
        } finally {
          data = null; //this is very important!
          generator = null;
          System.gc(); //sugestão de limpeza!
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

    private String provider = null;

    private ICMSConfigSetup config = (p, o) -> {};
    
    private boolean hasNoSignedAttributes = false;
    
    private ISignatureType signatureType = SignatureType.ATTACHED;
    
    private final Runnable dispose;
    
    public Builder(ICertificateChooser chooser, Runnable dispose) {
      this.chooser = Args.requireNonNull(chooser, "chooser is null");
      this.dispose = Args.requireNonNull(dispose, "dispose is null");
    }
    
    @Override
    public final ICMSSignerBuilder usingMemoryLimit(long memoryLimit) {
      this.memoryLimit = Args.requireZeroPositive(memoryLimit, "memory limit is invalid");
      return this;
    }
    
    @Override
    public final ICMSSignerBuilder usingConfig(ICMSConfigSetup config) {
      this.config = Args.requireNonNull(config, "config is null");
      return this;
    }

    @Override
    public final ICMSSignerBuilder usingSignatureAlgorithm(ISignatureAlgorithm algorithm) { 
      this.algorithm = Args.requireNonNull(algorithm, "Unabled to using null algorithm");
      return this;
    }
    
    @Override
    public final ICMSSignerBuilder usingSignatureType(ISignatureType signatureType) {
      this.signatureType = Args.requireNonNull(signatureType, "signatureType is null");
      return this;
    }

    @Override
    public final ICMSSignerBuilder usingProvider(String provider) {
      this.provider = Args.requireText(provider, "provider is empty");
      return this;
    }
    
    @Override
    public final ICMSSignerBuilder usingAttributes(boolean hasSignedAttributes) {
      this.hasNoSignedAttributes = !hasSignedAttributes; //a lógica da api é invertida!
      return this;
    }
    
    @Override
    public final ICMSSigner build() {
      CMSSigner signer = new CMSSigner(chooser, dispose);
      signer.provider = provider;
      signer.config = config;
      signer.memoryLimit = memoryLimit;
      signer.algorithm = this.algorithm;
      signer.signatureType = this.signatureType;
      signer.hasNoSignedAttributes = hasNoSignedAttributes;
      return signer;
    }
  }
}
