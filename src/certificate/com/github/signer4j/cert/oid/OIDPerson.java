package com.github.signer4j.cert.oid;

import java.util.Optional;

abstract class OIDPerson extends OIDBasic {

  private static enum Fields implements IMetadata {
    BIRTH_DATE(8), 
    CPF(11), 
    NIS(11), 
    RG(15), 
    UF_ISSUING_AGENCY_RG(6);

    private final int length;
    
    Fields(int length) {
      this.length = length;
    }
    
    @Override
    public int length() {
      return length;
    }
  }
  
	protected OIDPerson(String oid, String content) {
    super(oid, content);
  }

  @Override
  protected void setup() {
		super.setup(Fields.values());
	}

	public final Optional<String> getBirthDate() {
		return get(Fields.BIRTH_DATE);
	}

	public final Optional<String> getCPF() {
		return get(Fields.CPF);
	}

	public final Optional<String> getNIS() {
		return get(Fields.NIS);
	}

	public final Optional<String> getRg() {
		return get(Fields.RG);
	}

	public final Optional<String> getIssuingAgencyRg() {
	  Optional<String> value = get(Fields.UF_ISSUING_AGENCY_RG);
	  if (value.isPresent()) {
  		String s = value.get();
  		int len = s.length();
  		if (len > 2) {
  			return Optional.of(s.substring(0, len - 2));
  		}
	  }
	  return value;
	}

	public final Optional<String> getUfIssuingAgencyRg() {
	  Optional<String> value = get(Fields.UF_ISSUING_AGENCY_RG);
	  if (value.isPresent()) {
  	  String s = value.get();
  		int len = s.length();
  		if (len > 1) {
  			return Optional.of(s.substring(len - 2, len));
  		}
	  }
	  return value;
	}
}
