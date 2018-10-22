package com.apollocurrency.aplwallet.apl.crypto.asymmetric.signature;

public interface Signer {

    /**
     * Sign message using private key
     * @param message
     * @param privateKey
     * @return
     */
    byte[] sign(byte[] message, java.security.PrivateKey privateKey);

    /**
     * Verifies signature using theirPublicKey
     * @param message
     * @param signature
     * @param theirPublicKey
     * @return
     */
    boolean verify(byte[] message, byte[] signature, java.security.PublicKey theirPublicKey);

}
