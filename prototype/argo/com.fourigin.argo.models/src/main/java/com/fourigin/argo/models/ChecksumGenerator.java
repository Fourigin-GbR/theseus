package com.fourigin.argo.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public final class ChecksumGenerator {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static final String HASH_ALG = "MD5";

    private static final MessageDigest MESSAGE_DIGEST;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance(HASH_ALG);
        }
        catch(NoSuchAlgorithmException ex){
            throw new IllegalStateException("Unable to initialize MessageDigest!", ex);
        }
    }

    private ChecksumGenerator(){
    }

    public static String getChecksum(Serializable object){
        byte[] serializedObject;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            serializedObject = baos.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error serializing object!", ex);
        }

        MESSAGE_DIGEST.update(serializedObject);
        byte[] hash = MESSAGE_DIGEST.digest();

        return bytesToHex(hash);
    }

    public static String getChecksum(Collection<? extends Serializable> collection){
        byte[] serializedObject;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            if(collection != null) {
                for (Serializable item : collection) {
                    oos.writeObject(item);
                }
            }
            serializedObject = baos.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error serializing object!", ex);
        }

        MESSAGE_DIGEST.update(serializedObject);
        byte[] hash = MESSAGE_DIGEST.digest();

        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
