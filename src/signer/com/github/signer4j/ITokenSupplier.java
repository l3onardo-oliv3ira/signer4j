package com.github.signer4j;

import com.github.signer4j.imp.SwitchRepositoryException;
import com.github.utils4j.imp.function.IProcedure;

public interface ITokenSupplier<T> extends IProcedure<T, SwitchRepositoryException> {}