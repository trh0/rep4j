/***********************************************************************************
 * 
 * Copyright (c) 2015 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0 which accompany
 * this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 
 * <pre>
 * Kamil Baczkowicz - initial API and implementation and/or initial documentation <br>
 * trh0             - changes for usage.
 * </pre>
 * 
 */
package de.trho.rpi.util;

import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Builder class for creating secure socket factories.
 */
public class SecureSocketFactoryBuilder {
  /**
   * Creates an SSL/TLS socket factory with the given protocol version.
   */
  public static SSLSocketFactory getSocketFactory(final String protocolVersion) throws Exception {
    return getSocketFactory(protocolVersion, null, null, null);
  }

  /**
   * Creates an SSL/TLS socket factory with the given CA certificate file and protocol version.
   */
  public static SSLSocketFactory getSocketFactory(final String protocolVersion,
      final String caCertificateFile) throws Exception {
    try {
      Security.addProvider(new BouncyCastleProvider());

      final TrustManager[] tm =
          SecureSocketUtils.getTrustManagerFactory(caCertificateFile).getTrustManagers();

      return getSocketFactory(protocolVersion, null, tm, null);
    } catch (Exception e) {
      throw new Exception("Cannot create TLS/SSL connection", e);
    }
  }

  /**
   * Creates an SSL/TLS socket factory with the given key store details and protocol version.
   */
  public static SSLSocketFactory getSocketFactory(final String protocolVersion,
      final String caKeyStoreFile, final String caKeyStorePassword) throws Exception {
    try {
      Security.addProvider(new BouncyCastleProvider());

      final TrustManager[] tm =
          SecureSocketUtils.getTrustManagerFactory(caKeyStoreFile, caKeyStorePassword,
              SecureSocketUtils.getTypeFromFilename(caKeyStoreFile)).getTrustManagers();

      return getSocketFactory(protocolVersion, null, tm, null);
    } catch (Exception e) {
      throw new Exception("Cannot create TLS/SSL connection", e);
    }
  }

  /**
   * Creates an SSL/TLS socket factory with the given CA certificate file, client certificate,
   * client key&password and protocol version.
   */
  public static SSLSocketFactory getSocketFactory(final String protocolVersion,
      final String serverCrtFile, final String clientCrtFile, final String clientKeyFile,
      final String clientKeyPassword, final boolean pemFormat) throws Exception {
    try {
      Security.addProvider(new BouncyCastleProvider());

      final KeyManager[] km = SecureSocketUtils
          .getKeyManagerFactory(clientCrtFile, clientKeyFile, clientKeyPassword, pemFormat)
          .getKeyManagers();
      final TrustManager[] tm =
          SecureSocketUtils.getTrustManagerFactory(serverCrtFile).getTrustManagers();

      return getSocketFactory(protocolVersion, km, tm, null);
    } catch (Exception e) {
      throw new Exception("Cannot create TLS/SSL connection", e);
    }
  }

  /**
   * Creates an SSL/TLS socket factory with the given key store details and protocol version.
   */
  public static SSLSocketFactory getSocketFactory(final String protocolVersion,
      final String caKeyStoreFile, final String caKeyStorePassword, final String clientKeyStoreFile,
      final String clientKeyStorePassword, final String clientKeyPassword) throws Exception {
    try {
      Security.addProvider(new BouncyCastleProvider());

      final KeyManager[] km = SecureSocketUtils
          .getKeyManagerFactory(clientKeyStoreFile, clientKeyStorePassword, clientKeyPassword,
              SecureSocketUtils.getTypeFromFilename(clientKeyStoreFile))
          .getKeyManagers();

      final TrustManager[] tm =
          SecureSocketUtils.getTrustManagerFactory(caKeyStoreFile, caKeyStorePassword,
              SecureSocketUtils.getTypeFromFilename(caKeyStoreFile)).getTrustManagers();

      return getSocketFactory(protocolVersion, km, tm, null);
    } catch (Exception e) {
      throw new Exception("Cannot create TLS/SSL connection", e);
    }
  }

  private static SSLSocketFactory getSocketFactory(final String protocolVersion,
      final KeyManager[] km, final TrustManager[] tm, final SecureRandom random) throws Exception {
    try {
      Security.addProvider(new BouncyCastleProvider());

      // Create SSL/TLS socket factory
      final SSLContext context = SSLContext.getInstance(protocolVersion);

      context.init(km, tm, random);

      return context.getSocketFactory();
    } catch (Exception e) {
      throw new Exception("Cannot create TLS/SSL connection", e);
    }
  }
}
