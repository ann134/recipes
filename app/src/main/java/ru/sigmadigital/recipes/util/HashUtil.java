package ru.sigmadigital.recipes.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author dima
 */
public class HashUtil {

    private static final String MD5 = "MD5";
    private static final String SHA256 = "SHA-256";

    private HashUtil() {
    }

    public static String md5(String data) {
        return getHash(data, MD5);
    }

    public static String sha256(String data) {
        return getHash(data, SHA256);
    }

    public static String md5(byte[] data) {
        return getHash(data, MD5);
    }

    public static String sha256(byte[] data) {
        return getHash(data, SHA256);
    }

    public static String md5(InputStream is) {
        return getHash(is, MD5);
    }

    public static String sha256(InputStream is) {
        return getHash(is, SHA256);
    }

    public static String md5(File f) {
        return getHash(f, MD5);
    }

    public static String sha256(File f) {
        return getHash(f, MD5);
    }

    public static byte[] md5Bytes(String data) {
        return getHashBytes(data, MD5);
    }

    public static byte[] sha256Bytes(String data) {
        return getHashBytes(data, MD5);
    }

    public static byte[] md5Bytes(InputStream is) {
        return getHashBytes(is, MD5);
    }

    public static byte[] sha256Bytes(InputStream is) {
        return getHashBytes(is, MD5);
    }

    public static byte[] md5Bytes(File f) {
        return getHashBytes(f, MD5);
    }

    public static byte[] sha256Bytes(File f) {
        return getHashBytes(f, MD5);
    }

    public static byte[] md5Bytes(byte[] data) {
        return getHashBytes(data, MD5);
    }

    public static byte[] sha256Bytes(byte[] data) {
        return getHashBytes(data, MD5);
    }

    private static String getHash(String data, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            byte[] array = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    private static String getHash(byte[] data, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            byte[] array = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    private static String getHash(InputStream is, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            DigestInputStream dis = new DigestInputStream(is, md);
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = dis.read(buf)) > 0) {
                md.update(buf, 0, read);
            }
            byte[] array = md.digest();
            dis.close();
            is.close();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    private static String getHash(File f, String algoritm) {
        try {
            InputStream is = new FileInputStream(f);
            return getHash(is, algoritm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] getHashBytes(String data, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            byte[] array = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().getBytes("utf-8");
        } catch (Exception e) {
        }
        return new byte[]{};
    }

    private static byte[] getHashBytes(File f, String algoritm) {
        try {
            InputStream is = new FileInputStream(f);
            return getHashBytes(is, algoritm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private static byte[] getHashBytes(InputStream is, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            DigestInputStream dis = new DigestInputStream(is, md);
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = dis.read(buf)) > 0) {
                md.update(buf, 0, read);
            }
            byte[] array = md.digest();
            dis.close();
            is.close();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private static byte[] getHashBytes(byte[] data, String algoritm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritm);
            byte[] array = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

}

