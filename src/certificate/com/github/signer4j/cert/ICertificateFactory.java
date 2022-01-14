package com.github.signer4j.cert;

import java.security.cert.CertificateException;

import com.github.signer4j.ICertificate;
import com.github.signer4j.imp.function.Caller;

public interface ICertificateFactory extends Caller<Object, ICertificate, CertificateException>{
}
