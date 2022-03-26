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


package com.github.signer4j.cert.oid;

import static com.github.signer4j.cert.oid.OIDReader.CONTENT;
import static com.github.signer4j.cert.oid.OIDReader.HEADER;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;

import com.github.utils4j.IConstants;

import sun.security.util.DerValue;
import sun.security.x509.OtherName;

@SuppressWarnings("restriction")
class OIDFactory {

  //we have to go back here!
  /*
  private static final String[][] OIDs = new String[][] { 
    { "2.16.76.1.1", "OID para Declarações de Práticas de Certificação" },
    { "2.16.76.1.1.0", "DPC da AC Raiz" },
    { "2.16.76.1.1.1", "DPC da AC Presidência" },
    { "2.16.76.1.1.2", "DPC da AC Serpro" },
    { "2.16.76.1.1.3", "DPC da SERASA Autoridade Certificadora Principal – ACP" },
    { "2.16.76.1.1.4", "DPC da SERASA Autoridade Certificadora – AC" },
    { "2.16.76.1.1.5", "DPC da AC CertiSign na ICP-Brasil" },
    { "2.16.76.1.1.6", "DPC da AC CertiSign SPB na ICP-Brasil" },
    { "2.16.76.1.1.7", "DPC da SERASA Certificadora Digital" },
    { "2.16.76.1.1.8", "DPC da AC SRF" }, 
    { "2.16.76.1.1.9", "DPC da AC CAIXA" },
    { "2.16.76.1.1.10", "DPC da AC CAIXA IN" },
    { "2.16.76.1.1.11", "DPC da AC CAIXA PJ" },
    { "2.16.76.1.1.12", "DPC da AC CAIXA PF" },
    { "2.16.76.1.1.13", "DPC da AC SERPRO SRF" },
    { "2.16.76.1.1.14", "DPC da Autoridade Certificadora CertiSign Múltipla" },
    { "2.16.76.1.1.15", "DPC da Autoridade Certificadora CertiSign para Secretaria da Receita Federal" },
    { "2.16.76.1.1.16", "DPC da AC SERASA SRF" },
    { "2.16.76.1.1.17", "DPC da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.1.18", "DPC da Autoridade Certificadora PRODEMGE" }, 
    { "2.16.76.1.1.19", "DPC da Autoridade Certificadora do Sistema Justiça Federal – AC-JUS" },
    { "2.16.76.1.1.20", "Declaração de Práticas de Certificação da Autoridade Certificadora do SERPRO Final – DPC SERPRO ACF" },
    { "2.16.76.1.1.21", "Declaração de Práticas de Certificação da Autoridade Certificadora SINCOR" },
    { "2.16.76.1.1.22", "Declaração de Práticas de Certificação da Autoridade Certificadora Imprensa Oficial SP SRF" },
    { "2.16.76.1.1.23", "Declaração de Práticas de Certificação da AC FENACOR ADE ICP 04.01.A Versão 1.5" },
    { "2.16.76.1.1.24", "Declaração de Práticas de Certificação da Autoridade Certificadora SERPRO-JUS" },
    { "2.16.76.1.1.25", "DPC da AC Caixa Justiça" },
    { "2.16.76.1.1.26", "DPC da Autoridade Certificadora Imprensa Oficial SP (AC IMESP)" },
    { "2.16.76.1.1.27", "DPC da Autoridade Certificadora PRODEMGE SRF" },
    { "2.16.76.1.1.28", "Declaração de Práticas de Certificação da Autoridade Certificadora CertSign para a Justiça" }, 
    { "2.16.76.1.1.29", "DPC da AC SERASA JUS" },
    { "2.16.76.1.1.30", "Declaração de Práticas de Certificação da Autoridade Certificadora PETROBRAS" },
    { "2.16.76.1.1.31", "VAGO" },
    { "2.16.76.1.1.32", "Declaração de Práticas de Certificação da Autoridade Certificadora SINCOR para a Secretaria da Receita Federal" },
    { "2.16.76.1.1.33", "Declaração de Práticas de Certificação da Autoridade Certificadora Certisign FENACON SRF para a Secretaria da Receita Federal" },
    { "2.16.76.1.1.34", "Declaração de Práticas de Certificação da Autoridade Certificadora Notarial para a Secretaria da Receita Federal" },
    { "2.16.76.1.1.35", "Declaração de Práticas de Certificação da Autoridade Certificadora Brasileira de Registros para a Secretaria da Receita Federal" },
    { "2.16.76.1.2", "PC" },
    { "2.16.76.1.2.1", "A1" },
    { "2.16.76.1.2.1.1", "Política de Certificados da ACSERPRO para certificados SERPRO-SPB - PC SERPRO-SPB" }, 
    { "2.16.76.1.2.1.2", "Política de Certificados para certificados da SERASA Autoridade Certificadora" },
    { "2.16.76.1.2.1.3", "Política de Certificados da Autoridade Certificadora da Presidência da República – PCA1" },
    { "2.16.76.1.2.1.4", "Política de Certificado da Autoridade Certificadora CertiSign Certificadora Digital para o Sistema de Pagamentos Brasileiro na ICP-Brasil- PC da AC CertiSign SPB na ICP-Brasil" },
    { "2.16.76.1.2.1.5", "Política de Certificados SEPROA1" },
    { "2.16.76.1.2.1.6", "Política de Certificado Digital para Certificado de Assinatura Digital Tipo A1 – SERASA CD" },
    { "2.16.76.1.2.1.7", "Política de Certificado de Assinatura Digital do Tipo A1 da AC Caixa IN" },
    { "2.16.76.1.2.1.8", "Política de Certificado de Assinatura Digital do Tipo A1 da AC Caixa PF" },
    { "2.16.76.1.2.1.9", "Política de Certificado de Assinatura Digital do Tipo A1 da AC Caixa PJ" },
    { "2.16.76.1.2.1.10", "Política de Certificados da Autoridade Certificadora do Serpro-SRF para certificados de assinatura digital do tipo A1 (PCSerpro-SRFA1)" },
    { "2.16.76.1.2.1.11", "Política de Certificado de Assinatura Digital do Tipo A1 da Autoridade Certificadora CertiSign Múltipla na Infra-estrutura de Chaves Públicas Brasileira" }, 
    { "2.16.76.1.2.1.12", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora CertiSign para a Secretaria da Receita Federal" },
    { "2.16.76.1.2.1.13", "Política de Certificado de Assinatura Digital Tipo A1 da AC SERASA SRF" },
    { "2.16.76.1.2.1.14", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.1.15", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora PRODEMGE" },
    { "2.16.76.1.2.1.16", "Política de Certificados SERPRO do Tipo A1 – PC SERPRO ACF A1" },
    { "2.16.76.1.2.1.17", "Política de Certificados do SERPRO – SPB – PC SERPRO ACF SPB" },
    { "2.16.76.1.2.1.18", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora SINCOR" },
    { "2.16.76.1.2.1.19", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora SINCOR para Corretores de Seguros" },
    { "2.16.76.1.2.1.20", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora Imprensa Oficial SP SRF" },
    { "2.16.76.1.2.1.21", "Política de Certificados SERPRO-JUS do tipo A1 - PCSERPROJUSA1 ADE ICP 04.01.A Versão 1.5" }, 
    { "2.16.76.1.2.1.22", "Política de Certificado de Assinatura Digital do Tipo A1 da AC Caixa Justiça" },
    { "2.16.76.1.2.1.23", "Política de Certificado de Assinatura Tipo A1 da Autoridade Certificadora PRODEMGE SRF" },
    { "2.16.76.1.2.1.24", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.1.25", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo A1" },
    { "2.16.76.1.2.1.26", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora PETROBRAS" },
    { "2.16.76.1.2.1.27", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora Brasileira de Registros para a Secretaria da Receita Federal" },
    { "2.16.76.1.2.1.28", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora SINCOR" },
    { "2.16.76.1.2.1.29", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora Certisign FENACON SRF" },
    { "2.16.76.1.2.1.30", "Política de Certificado de Assinatura Digital Tipo A1 da Autoridade Certificadora Notarial SRF" },
    { "2.16.76.1.2.2", "A2" }, 
    { "2.16.76.1.2.2.1", "Política de Certificado Digital para Certificado de Assinatura Digital Tipo A2 – SERASA CD" },
    { "2.16.76.1.2.2.2", "Política de Certificado Digital para Certificado de Assinatura Digital Tipo A2 da AC SERASA SRF" },
    { "2.16.76.1.2.2.3", "Política de Certificado de Assinatura Digital do Tipo A2 da Autoridade Certificadora CertiSign Múltipla na Infra-estrutura de Chaves Públicas Brasileira" },
    { "2.16.76.1.2.2.4", "Política de Certificado de Assinatura Digital do Tipo A2 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.2.5", "Política de Certificado de Assinatura Digital do Tipo A2 da AC Caixa Justiça" },
    { "2.16.76.1.2.2.6", "Política de Certificado de Assinatura Digital Tipo A2 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.2.7", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo A2" },
    { "2.16.76.1.2.3", "A3" },
    { "2.16.76.1.2.3.1", "Política de Certificados da Autoridade Certificadora da Presidência da República – PC ACPR" },
    { "2.16.76.1.2.3.2", "Política de Certificados da Autoridade Certificadora do SERPRO para certificados SERPRO do tipo A3 – PCSERPROA3" }, 
    { "2.16.76.1.2.3.3", "Política de Certificado Digital para Certificado de Assinatura Digital Tipo A3 – SERASA CD" },
    { "2.16.76.1.2.3.4", "Política de Certificados da Autoridade Certificadora do Serpro-SRF para certificados de assinatura digital do tipo A3 (PCSerpro-SRFA3)" },
    { "2.16.76.1.2.3.5", "Política de Certificado de Assinatura Digital do Tipo A3 da Autoridade Certificadora CertiSign Múltipla na Infra-estrutura de Chaves Públicas Brasileira" },
    { "2.16.76.1.2.3.6", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora CertiSign para a Secretaria da Receita Federal na Infra-estrutura de Chaves Públicas Brasileira" },
    { "2.16.76.1.2.3.7", "Política de Certificado de Assinatura Digital do Tipo A3 da AC Caixa IN" },
    { "2.16.76.1.2.3.8", "Política de Certificado de Assinatura Digital do Tipo A3 da AC Caixa PF" },
    { "2.16.76.1.2.3.9", "Política de Certificado de Assinatura Digital do Tipo A3 da AC Caixa PJ" },
    { "2.16.76.1.2.3.10", "Política de Certificado de Assinatura Digital do Tipo A3 da AC SERASA SRF" },
    { "2.16.76.1.2.3.11", "Política de Certificado de Assinatura Digital do Tipo A3 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.3.12", "Política de Certificado de Assinatura Digital do Tipo A3 da Autoridade Certificadora PRODEMGE" }, 
    { "2.16.76.1.2.3.13", "Política de Certificados SERPRO do Tipo A3 – PC SERPRO A3 " },
    { "2.16.76.1.2.3.14", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora SINCOR" },
    { "2.16.76.1.2.3.15", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora SINCOR para Corretores de Seguros" },
    { "2.16.76.1.2.3.16", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora Imprensa Oficial SP SRF" },
    { "2.16.76.1.2.3.17", "Política de Certificado da AC FENACOR A3" },
    { "2.16.76.1.2.3.18", "Política de Certificados SERPRO-JUS do tipo A3 - PCSERPROJUSA3" },
    { "2.16.76.1.2.3.19", "Política de Certificado de Assinatura Digital do Tipo A3 da AC Caixa Justiça" },
    { "2.16.76.1.2.3.20", "Política de Certificado de Assinatura Tipo A3 da Autoridade Certificadora PRODEMGE SRF" },
    { "2.16.76.1.2.3.21", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.3.22", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo A3" }, 
    { "2.16.76.1.2.3.23", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora PETROBRAS" },
    { "2.16.76.1.2.3.24", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora Brasileira de Registros para a Secretaria da Receita Federal" },
    { "2.16.76.1.2.3.25", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora SINCOR SRF" },
    { "2.16.76.1.2.3.26", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora CertiSign FENACON SRF" },
    { "2.16.76.1.2.3.27", "Política de Certificado de Assinatura Digital Tipo A3 da Autoridade Certificadora Notarial SRF" },
    { "2.16.76.1.2.4", "A4" },
    { "2.16.76.1.2.4.1", "Política de Certificado Digital para Certificado de Assinatura Digital Tipo A4 – SERASA CD;" },
    { "2.16.76.1.2.4.2", "Política de Certificado de Assinatura Digital tipo A4 SERASA SRF" },
    { "2.16.76.1.2.4.3", "Política de Certificado de Assinatura Digital do Tipo A4 da Autoridade Certificadora CertiSign Múltipla na Infra-estrutura de Chaves Públicas Brasileira" },
    { "2.16.76.1.2.4.4", "Política de Certificado de Assinatura Digital Tipo A4 da Autoridade Certificadora CertiSign para a Secretaria da Receita Federal na Infra-estrutura de Chaves Públicas Brasileira" }, 
    { "2.16.76.1.2.4.5", "Política de Certificado de Assinatura Digital Tipo A4 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.4.6", "Política de Certificado de Assinatura Digital Tipo A4 da Autoridade Certificadora Imprensa Oficial – SP SRF" },
    { "2.16.76.1.2.4.7", "Política de Certificado de Assinatura Tipo A4 da Autoridade Certificadora PRODEMGE SRF" },
    { "2.16.76.1.2.4.8", "Política de Certificado de Assinatura Digital Tipo A4 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.4.9", "VAGO" },
    { "2.16.76.1.2.4.10", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo A4" },
    { "2.16.76.1.2.4.11", "VAGO" },
    { "2.16.76.1.2.4.12", "Política de Certificado de Assinatura Digital Tipo A4 da Autoridade Certificadora SINCOR SRF" },
    { "2.16.76.1.2.101", "S1" },
    { "2.16.76.1.2.101.1", "Política de Certificado Digital para Certificado de Sigilo Tipo S1 – SERASA CD" }, 
    { "2.16.76.1.2.101.2", "Política de Certificado de Sigilo Tipo S1 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.101.3", "Política de Certificado de Sigilo do Tipo S1 da Autoridade Certificadora CertiSign Múltipla" },
    { "2.16.76.1.2.101.4", "Política de Certificado de Sigilo do Tipo S1 da Autoridade Certificadora PRODEMGE" },
    { "2.16.76.1.2.101.5", "Política de Certificado de Sigilo do Tipo S1 da AC Caixa Justiça" },
    { "2.16.76.1.2.101.6", "Política de Certificado de Sigilo Tipo S1 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.101.7", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo S1" },
    { "2.16.76.1.2.101.8", "Política de Certificado de Sigilo Tipo S1 da Autoridade Certificadora PETROBRAS" },
    { "2.16.76.1.2.101.9", "Política de Certificado de Assinatura Digital Tipo S1 da Autoridade Certificadora SINCOR - PC S1 da AC SINCOR" },
    { "2.16.76.1.2.102", "S2" },
    { "2.16.76.1.2.102.1", "Política de Certificado Digital para Certificado de Sigilo Tipo S2 – SERASA CD" }, 
    { "2.16.76.1.2.102.2", "Política de Certificado de Sigilo Tipo S2 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.102.3", "Política de Certificado de Sigilo do Tipo S2 da Autoridade Certificadora CertiSign Múltipla" },
    { "2.16.76.1.2.102.4", "Política de Certificado de Sigilo do Tipo S2 da AC Caixa Justiça" },
    { "2.16.76.1.2.102.5", "Política de Certificado de Sigilo Tipo S2 da Autoridade Certificadora CertiSign para a Justiça " },
    { "2.16.76.1.2.102.6", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo S2" },
    { "2.16.76.1.2.103", "S3" },
    { "2.16.76.1.2.103.1", "Política de Certificado Digital para Certificado de Sigilo Tipo S3 – SERASA CD" },
    { "2.16.76.1.2.103.2", "VAGO" },
    { "2.16.76.1.2.103.3", "Política de Certificado de Sigilo do Tipo S3 da Autoridade Certificadora CertiSign Múltipla" },
    { "2.16.76.1.2.103.4", "Política de Certificado de Sigilo Tipo S3 da Autoridade Certificadora Imprensa Oficial - SP" }, 
    { "2.16.76.1.2.103.5", "Política de Certificado de Sigilo Tipo S3 da Autoridade Certificadora PRODEMGE" },
    { "2.16.76.1.2.103.6", "Política de Certificado de Sigilo do Tipo S3 da AC Caixa Justiça" },
    { "2.16.76.1.2.103.7", "Política de Certificado de Sigilo Tipo S3 da Autoridade Certificadora CertiSign para a Justiça" },
    { "2.16.76.1.2.103.8", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo S3" },
    { "2.16.76.1.2.104", "S4" },
    { "2.16.76.1.2.104.1", "Política de Certificado Digital para Certificado de Sigilo Tipo S4 – SERASA CD" },
    { "2.16.76.1.2.104.2", "VAGO" },
    { "2.16.76.1.2.104.3", "Política de Certificado de Sigilo do Tipo S4 da Autoridade Certificadora CertiSign Múltipla" },
    { "2.16.76.1.2.104.4", "Política de Certificado de Sigilo Tipo S4 da Autoridade Certificadora Imprensa Oficial - SP" },
    { "2.16.76.1.2.104.5", "Política de Certificado de Sigilo Tipo S4 da Autoridade Certificadora CertiSign para a Justiça" }, 
    { "2.16.76.1.2.104.6", "Política de Certificado Digital da AC SERASA-JUS para Certificados Tipo S4" },
    { "2.16.76.1.2.201", "PC de AC" },
    { "2.16.76.1.2.201.1", "PC da Serasa Autoridade Certificadora Principal – ACP" },
    { "2.16.76.1.2.201.2", "PC da AC CertiSign na ICP-Brasil" },
    { "2.16.76.1.2.201.3", "PC da AC SRF" },
    { "2.16.76.1.2.201.4", "Política de Certificados da Autoridade Certificadora Caixa" },
    { "2.16.76.1.2.201.5", "PC da Autoridade Certificadora do Sistema Justiça Federal – ACJUS" },
    { "2.16.76.1.2.201.6", "PC da Autoridade Certificadora do SERPRO (AC SERPRO)" },
    { "2.16.76.1.2.201.7", "PC da Autoridade Certificadora Imprensa Oficial SP (AC IMESP)" },
    { "2.16.76.1.2.303", "PC de Carimbo de Tempo T3" }, 
    { "2.16.76.1.2.303.1", "" },
    { "2.16.76.1.2.303.2", "" },
    { "2.16.76.1.2.303.3", "" },
    { "2.16.76.1.2.303.4", "" },
    { "2.16.76.1.2.303.5", "" },
    { "2.16.76.1.2.303.6", "" },
    { "2.16.76.1.2.303.7", "" },
    { "2.16.76.1.2.304", "PC de Carimbo de Tempo T4" },
    { "2.16.76.1.2.304.1", "" },
    { "2.16.76.1.2.304.2", "" }, 
    { "2.16.76.1.2.304.3", "" },
    { "2.16.76.1.2.304.4", "" },
    { "2.16.76.1.2.304.5", "" },
    { "2.16.76.1.2.304.6", "" },
    { "2.16.76.1.2.304.7", "" },
    { "2.16.76.1.3", "Atributos Obrigatórios de Certificados Implantação" },
    { "2.16.76.1.3.1", "campo otherName em certificado de pessoa física, contento os dados do titular (data de nascimento, CPF, PIS/PASEP/CI, RG); Res. 07, de 12.12.2001" },
    { "2.16.76.1.3.2", "campo otherName em certificado de pessoa jurídica, contendo o nome do responsável pelo certificado; Res. 07, de 12.12.2001" },
    { "2.16.76.1.3.3", "campo otherName em certificado de pessoa jurídica, contendo o Cadastro Nacional de Pessoa Jurídica (CNPJ) da pessoa jurídica titular do certificado; Res. 07, de 12.12.2001" },
    { "2.16.76.1.3.4", "campo otherName em certificado de pessoa jurídica, contendo os dados do responsável pelo certificado de pessoa jurídica titular do certificado (data de nascimento, CPF, PIS/PASEP/CI, RG); Res. 07, de 12.12.2001" }, 
    { "2.16.76.1.3.5", "campo otherName em certificado de pessoa física, contendo informações sobre o Título de Eleitor do titular; Res. 13, de 29.04.2002" },
    { "2.16.76.1.3.6", "campo otherName em certificado de pessoa física, contendo nas 12 posições o número do Cadastro Específico do INSS (CEI) da pessoa física titular do certificado; Res. 31, de 29.01.2004" },
    { "2.16.76.1.3.7", "campo otherName em certificado de pessoa jurídica, contendo o número do Cadastro Específico do INSS (CEI) da pessoa jurídica titular do certificado; Res. 31, de 29.01.2004" },
    { "2.16.76.1.4", "Atributos Opcionais de Certificados Implantação" },
    { "2.16.76.1.4.1", "Entidades Sindicais 29-06-05" },
    { "2.16.76.1.4.1.1", "SINCOR 29-06-05" },
    { "2.16.76.1.4.1.1.1", "Número de registro do corretor associado 29-06-05" },
    { "2.16.76.1.4.2", "Entidades de Classe 13.10.08" },
    { "2.16.76.1.4.2.1", "OAB 13.10.08" },
    { "2.16.76.1.4.2.1.1", "Número de registro do advogado 13.10.08 Esquema de OID na ICP-Brasil - Posição em 27.01.2009" }, 
    { "2.16.76.1.5", "Declaração de Práticas de Carimbo de Tempo" },
    { "2.16.76.1.6", "Políticas de Carimbo de Tempo" },
    { "2.16.76.1.7", "Políticas de Assinatura Implantação" },
    { "2.16.76.1.7.1", "Políticas de Assinatura do Instituto Nacional de Tecnologia da Informação 13.10.2008" },
    { "2.16.76.1.7.1.1", "Política de assinatura tipo AD-CP para CMS 13.10.2008" },
    { "2.16.76.1.7.1.1.1", "Versão 1.0 da Política de assinatura tipo AD-CP para CMS 13.10.2008" },
    { "2.16.76.1.7.1.2", "Política de assinatura tipo AD-T para CMS 13.10.2008" },
    { "2.16.76.1.7.1.2.1", "Versão 1.0 da Política de assinatura tipo AD-T para CMS 13.10.2008" },
    { "2.16.76.1.7.1.3", "Política de assinatura tipo AD-R para CMS 13.10.2008" },
    { "2.16.76.1.7.1.3.1", "Versão 1.0 da Política de assinatura tipo AD-R para CMS 13.10.2008" }, 
    { "2.16.76.1.7.1.4", "Política de assinatura tipo AD-C para CMS 13.10.2008" },
    { "2.16.76.1.7.1.4.1", "Versão 1.0 da Política de assinatura tipo AD-C para CMS 13.10.2008" },
    { "2.16.76.1.7.1.5", "Política de assinatura tipo AD-A para CMS 13.10.2008" },
    { "2.16.76.1.7.1.5.1", "Versão 1.0 da Política de assinatura tipo AD-A para CMS 13.10.2008" },
    { "2.16.76.1.7.1.6", "Política de assinatura tipo AD-CP para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.6.1", "Versão 1.0 da Política de assinatura tipo AD-CP para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.7", "Política de assinatura tipo AD-T para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.7.1", "Versão 1.0 da Política de assinatura tipo AD-T para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.8", "Política de assinatura tipo AD-R para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.8.1", "Versão 1.0 da Política de assinatura tipo AD-R para XMLDSIG 13.10.2008" }, 
    { "2.16.76.1.7.1.9", "Política de assinatura tipo AD-C para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.9.1", "Versão 1.0 da Política de assinatura tipo AD-C para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.10", "Política de assinatura tipo AD-A para XMLDSIG 13.10.2008" },
    { "2.16.76.1.7.1.10.1", "Versão 1.0 da Política de assinatura tipo AD-A para XMLDSIG 13.10.2008" },
    { "2.16.76.1.8", "Tipos de Compromisso em Assinatura Digital Implantação" },
    { "2.16.76.1.8.1", "Concordância - A assinatura aposta indica que o signatário concorda com o conteúdo assinado." },
    { "2.16.76.1.8.2", "Autorização - A assinatura aposta indica que o signatário autoriza o constante no conteúdo assinado." },
    { "2.16.76.1.8.3", "Testemunho - A assinatura aposta indica o compromisso de testemunho do signatário. Não necessariamente indica concordância do signatário com o conteúdo." },
    { "2.16.76.1.8.4", "Autoria - A assinatura aposta indica que o signatário foi autor do conteúdo assinado. Não necessariamente indica concordância do signatário com o conteúdo." },
    { "2.16.76.1.8.5", "Conferência - A assinatura aposta indica que o signatário realizou a conferência do conteúdo." }, 
    { "2.16.76.1.8.6", "Revisão - A assinatura aposta indica que o signatário revisou o conteúdo assinado. Não necessariamente indica concordância do signatário com o conteúdo." },
    { "2.16.76.1.8.7", "Ciência - A assinatura aposta indica que o signatário tomou ciência do conteúdo assinado. Não necessariamente indica concordância do signatário com o conteúdo." },
    { "2.16.76.1.8.8", "Publicação - A assinatura tem o propósito de indicar que o signatário publicou o documento em algum meio de comunicação externo à entidade que o originou." },
    { "2.16.76.1.8.9", "Protocolo - A assinatura aposta indica a intenção do signatário em protocolar o conteúdo." },
    { "2.16.76.1.8.10", "Integridade - A assinatura aposta indica a intenção do signatário em garantir somente a integridade da mensagem." },
    { "2.16.76.1.8.11", "Autenticação de usuário - A assinatura aposta é utilizada somente como prova de autenticação do signatário." },
    { "2.16.76.1.8.12", "Teste - A assinatura aposta indica a intenção do signatário em realizar um teste." },
    { "1.2.840", "United States of America" },
    { "1.2.840.113549", "RSA Data Security Inc." },
    { "1.2.840.113549.3", "Encryption Algorithm" }, 
    { "1.2.840.113549.2", "Digest algorithms" },
    { "2.16.840.1.101.3.4", "NIST Algorithm" },
    { "1.3.6.1.4.1", "This arc is for private entreprises. It is used, among other things, for defining private SNMP MIBs." },
    { "1.3.14", "NIST/OSI Implementors' Workshop (OIW)" },
    { "1.3.14.3.2", "OIW Security Special Interest Group defined algorithms." },
    { "1.2.840.10040", "ANSI standard X9.57 (1997): 'Public Key Cryptography for the Financial Services Industry: Certificate Management'" },
    { "1.2.840.10045", "ANSI X9.62 standard (1998): 'Public Key Cryptography for the Financial Services Industry : The Elliptic Curve Digital Signature Algorithm (ECDSA)'" },
    { "1.2.840.10046", "ANSI X9 standard 'X9.42-2000, Public Key Cryptography for The Financial Services Industry: Agreement of Symmetric Keys Using Discrete Logarithm Cryptography'" },
    { "1.3.133.16.840.63.0", "x9-63-scheme" },
    { "1.2.840.113549.1", "PKCS (Public Key Cryptography Standards)." }, 
    { "1.2.840.113549.1.1", "PKCS#1 (Public Key Cryptography Standards - 1)" },
    { "1.2.840.113549.1.3", "Public Key Cryptography Standard 3" },
    { "1.2.840.113549.1.5", "PKCS #5" },
    { "1.2.840.113549.1.7", "PKCS-7 (Public Key Cryptography Standards - 7) Cryptographic Message Syntax Standard" },
    { "1.2.840.113549.1.9", "PKCS#9 (Public Key Cryptography Standards - 9)" },
    { "1.2.840.113549.1.12", "Personal Information Exchange Syntax Standard" },
    { "1.2.840.113549.1.9.16", "S/MIME" },
    { "1.2.840.10040.4.1", "Digital Signature Algorithm (DSA), also known as the Digital Signature Standard (DSS)" },
    { "1.2.840.10040.4.3", "ANSI X9.57 DSA signature generated with SHA-1 hash (DSA x9.30)" },
    { "1.2.840.113549.1.1.1", "RSA (PKCS #1 v1.5) key transport algorithm" }, 
    { "1.2.840.113549.1.1.1.1", "RSA (PKCS #1 v1.5) key transport algorithm" },
    { "1.2.840.113549.1.1.1.1", "RSA (PKCS #1 v1.5) key transport algorithm" },
    { "1.2.840.113549.1.1.1.1", "RSA (PKCS #1 v1.5) key transport algorithm" },
    { "1.2.840.10046.2.1", "ANSI X9.42 Ephemeral-Static Diffie-Hellman key agreement algorithm" },
    { "2.16.840.1.101.3.4.1", "Advanced Encryption Standard" },
    { "2.16.840.1.101.3.4.1.1", "EBC (128 bit AES information)" },
    { "2.16.840.1.101.3.4.1.2", "Voice encryption using AES (128-bit) in CBC mode and 512-bit DH-group" },
    { "2.16.840.1.101.3.4.1.3", "OFB (128 bit AES information)" },
    { "2.16.840.1.101.3.4.1.4", "CFB (128 bit AES information)" },
    { "2.16.840.1.101.3.4.1.5", "id-aes128-wrap" }, 
    { "2.16.840.1.101.3.4.1.21", "ECB (192 bit AES information)" },
    { "2.16.840.1.101.3.4.1.22", "CBC (192 bit AES information)" },
    { "2.16.840.1.101.3.4.1.23", "OFB (192 bit AES information)" },
    { "2.16.840.1.101.3.4.1.24", "CFB (192 bit AES information)" },
    { "2.16.840.1.101.3.4.1.25", "id-aes192-wrap" },
    { "2.16.840.1.101.3.4.1.41", "ECB (256 bit AES information)" },
    { "2.16.840.1.101.3.4.1.42", "CBC (256 bit AES information)" },
    { "2.16.840.1.101.3.4.1.43", "OFB (256 bit AES information)" },
    { "2.16.840.1.101.3.4.1.44", "CFB (256 bit AES information)" },
    { "2.16.840.1.101.3.4.1.45", "id-aes256-wrap" }, 
    { "1.2.840.113549.3.7", "ANSI X9.52: Triple DES modes of operation" },
    { "1.2.840.113549.1.9.16.3.6", "Triple-DES key encryption" },
    { "1.2.840.113549.3.2", "Voice encryption using RC2-compatible (56 bit) or RC2-compatible in CBC mode and 512-bit DH-group." },
    { "1.2.840.113549.1.9.16.3.7", "RC2 key encryption" },
    { "1.2.840.113549.3.4", "RSA RC4 private key encryption algorithm without MAC" },
    { "1.2.840.113549.3.7", "3Des Triple-Des encryption algorithm 3Des ALGORITHM PARAMETER NULL ::= {algorithm 6} URL for further info: http://www.lancrypto.com " },
    { "1.3.14.3.2.26", "Hash algorithm identifier SHA-1 (Secure Hash Algorithm, Revision 1)" },
    { "2.16.840.1.101.3.4.2", "SHA2 algorithm" },
    { "2.16.840.1.101.3.4.2.4", "A 224-bit One-way Hash Function: SHA-224" },
    { "2.16.840.1.101.3.4.2.1", "SHA 2 algorithm: SHA-256" }, 
    { "2.16.840.1.101.3.4.2.2", "SHA algorithm: 'sha384'" },
    { "2.16.840.1.101.3.4.2.3", "SHA algorithm: 'sha512'" },
    { "1.2.840.113549.2.2", "Message Digest #2" },
    { "1.2.840.113549.2.4", "RSADSI Message Digest #4" },
    { "1.2.840.113549.2.5", "Message Digest #5 algorithm. Commonly used with RSA Digital Signatures. Also used by PEM." },
    { "1.2.840.113549.2.7", "HMAC-SHA-1 pseudorandom function for key generation defined in PKCS-5 ver 2.0" },
    { "1.2.840.113549.2.8", "HMAC-SHA-224 message authentication scheme" },
    { "1.2.840.113549.2.9", "HMAC-SHA-256 message authentication scheme" },
    { "1.2.840.113549.2.10", "HMAC-SHA-384 message authentication scheme" },
    { "1.2.840.113549.2.11", "HMAC-SHA-512 message authentication scheme" }, 
    { "2.16.840.1.101.3.4.1.42", "CBC (256 bit AES information)" },
    { "1.2.840.113549.1.5.1", "Password Based Encryption Algorithm. Uses Data Encryption Standard in Cipher Block Chaining Mode (DES-CBC). Uses MD2 to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.4", "Password Based Encryption Algorithm. Uses RC2 in Cipher Block Chaining Mode (RC2-CBC). Uses MD2 to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.3", "Password Based Encryption Algorithm. Uses Data Encryption Standard in Cipher Block Chaining Mode (DES-CBC). Uses MD5 to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.6", "Password Based Encryption Algorithm. Uses RC2 in Cipher Block Chaining Mode (RC2-CBC). Uses MD5 to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.10", "Password Based Encryption Algorithm. Uses Data Encryption Standard in Cipher Block Chaining Mode (DES-CBC). Uses Secure Hash Algorithm 1 (SHA1) to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.11", "Password Based Encryption Algorithm. Uses RC2 in Cipher Block Chaining Mode (RC2-CBC). Uses SHA1 to hash a password & salt to get Key and IV" },
    { "1.2.840.113549.1.5.13", "This OID describes a key derivation Algorithm and an encryption Scheme for private keys different from old identifiers. URL for further info: http://www.rsasecurity.com/rsalabs/...5/index.html" },
    { "1.2.840.113549.1.5.12", "PBKDF2 key derivation algorithm. Defined in PKCS-5 Ver 2.0 as an identifier to indicate a derivation function" },
    { "1.3.14.3.2.2", "MD4 with RSA algorithm" }, 
    { "1.3.14.3.2.3", "MD5 with RSA algorithm" },
    { "1.3.14.3.2.4", "MD4 with RSA encryption algorithm" },
    { "1.3.14.3.2.7", "Voice encryption using DES (56 bit) in CBC mode and 512-bit DH-group" },
    { "1.3.14.3.2.27", "DSA with SHA1 producing a 320-bit signature" },
    { "2.16.840.1.101.3.4.3", "Signature algorithms" },
    { "2.16.840.1.101.3.4.3.1", "id-dsa-with-sha224" },
    { "2.16.840.1.101.3.4.3.2", "id-dsa-with-sha256" },
    { "2.16.840.1.101.3.4.3.3", "id-dsa-with-sha384" },
    { "2.16.840.1.101.3.4.3.4", "id-dsa-with-sha512" },
    { "1.3.14.3.2.29", "SHA1 with RSA signature (obsolete). This algorithm uses the RSA signature algorithm to sign a 160-but SHA1 digest." }, 
    { "1.2.840.10045.4.1", "ANSI X9.62 ECDSA signatures with SHA-1. Also (see below): ASN.1 module named ANSI-X9-62" },
    { "1.2.840.10045.4.3", "ANSI X9.62 EC-DSA algorithm with Specified" },
    { "1.2.840.10045.4.3.1", "ANSI X9.62 EC-DSA algorithm with Specified - Sha224" },
    { "1.2.840.10045.4.3.2", "ANSI X9.62 EC-DSA algorithm with Specified - Sha256" },
    { "1.2.840.10045.4.3.3", "ANSI X9.62 EC-DSA algorithm with Specified - Sha384" },
    { "1.2.840.10045.4.3.4", "ANSI X9.62 EC-DSA algorithm with Specified - Sha512" },
    { "1.2.840.113549.1.1.2", "MD2 with RSA Encryption. Identifier for MD2 checksum with RSA encryption for use with Public Key Cryptosystem One defined by RSA Inc" },
    { "1.2.840.113549.1.1.3", "PKCS #1 md4withRSAEncryption" },
    { "1.2.840.113549.1.1.4", "RSA (PKCS #1 v1.5) with MD5 signature. Identifier for MD5 checksum with RSA encryption for use with Public Key Cryptosystem One defined by RSA Inc" },
    { "1.2.840.113549.1.1.5", "RSA (PKCS #1 v1.5) with SHA-1 signature" }, 
    { "1.2.840.113549.1.1.14", "sha224WithRSAEncryption" },
    { "1.2.840.113549.1.1.11", "SHA256 with RSA Encryption" },
    { "1.2.840.113549.1.1.12", "SHA384 with RSA Encryption" },
    { "1.2.840.113549.1.1.13", "SHA512 with RSA Encryption" },
    { "1.3.14.7.2.1.1", "ElGamal algorithm" }
  }; 
   */
  
  private static final String PACKAGE = OIDFactory.class.getPackage().getName() + ".OID_";

  private OIDFactory() {}

  private static String toClass(Object id) {
    return PACKAGE + id.toString().replaceAll("[.]", "_");
  }

  private static OIDBasic create(ASN1ObjectIdentifier oid, String content) throws Exception {
    return create(oid.getId(), content);
  }

  public static OIDBasic create(DerValue der) throws IOException, Exception {
    OtherName otherName = new OtherName(der);
    return create(otherName.getOID(), new String(otherName.getNameValue()).substring(6));
  }

  public static OIDBasic create(byte[] data) throws IOException, Exception {
    try(ASN1InputStream is = new ASN1InputStream(data)){
      DLSequence sequence = (DLSequence) is.readObject();
      ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) sequence.getObjectAt(HEADER.intValue());
      ASN1TaggedObject tag = (ASN1TaggedObject)sequence.getObjectAt(CONTENT.intValue());
      ASN1TaggedObject next = (ASN1TaggedObject) tag.getObject();
      return create(oid, getContent(next));
    }
  }

  private static String getContent(ASN1TaggedObject tag) {
    Object content = tag.getObject();
    if (content instanceof ASN1String)
      return ((ASN1String)content).getString();
    if (content instanceof ASN1OctetString)
      return new String(((ASN1OctetString)content).getOctets(), IConstants.ISO_8859_1);
    throw new RuntimeException("Unabled to read content from ASN1TaggedObject tag");
  }

  private static OIDBasic create(Object oid, String content) throws Exception {
    try {
      Class<?> clazz = Class.forName(toClass(oid));
      Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
      constructor.setAccessible(true);
      OIDBasic instance = (OIDBasic)constructor.newInstance(content);
      instance.setup();
      return instance;
    } catch (InstantiationException e) {
      throw new Exception("Unabled to instantiate class for OID " + oid, e);
    } catch (IllegalAccessException e) {
      throw new Exception("Unabled to access class for OID " + oid, e);
    } catch (ClassNotFoundException e) {
      throw new Exception("Unabled to found class for OID " + oid, e);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }



}
