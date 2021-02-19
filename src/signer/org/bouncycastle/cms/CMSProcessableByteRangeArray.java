package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteRangeArray implements CMSTypedData, CMSReadable {
  
  private final ASN1ObjectIdentifier type;
  private final byte[]  bytes;
  private final int offset;
  private final int length;
  
  public CMSProcessableByteRangeArray(byte[] bytes) {
    this(CMSObjectIdentifiers.data, bytes, 0, bytes.length);
  }

  public CMSProcessableByteRangeArray(byte[] bytes, int offset, int length) {
    this(CMSObjectIdentifiers.data, bytes, offset, length);
  }

  public CMSProcessableByteRangeArray(ASN1ObjectIdentifier type, byte[]  bytes, int offset, int length) {
    this.type = type;
    this.bytes = bytes;
    this.offset = offset;
    this.length = length;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(bytes, offset, length);
  }

  @Override
  public void write(OutputStream zOut) throws IOException, CMSException {
    zOut.write(bytes, offset, length);
  }

  @Override
  public Object getContent() {
    return Arrays.copyOfRange(bytes,  offset,  length);
  }

  @Override
  public ASN1ObjectIdentifier getContentType() {
    return type;
  }
}
