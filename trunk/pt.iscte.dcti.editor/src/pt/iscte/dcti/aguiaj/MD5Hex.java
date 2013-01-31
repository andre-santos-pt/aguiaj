/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.iscte.dcti.aguiaj;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hex {
	
	public static String toMD5(String s) {
		String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            result = new String(digest);
        }
        catch (NoSuchAlgorithmException e) {
            // this won't happen, we know Java has MD5!
        }
        return result;
	}
    public static String toMD5Hex(String s) {
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            result = toHex(digest);
        }
        catch (NoSuchAlgorithmException e) {
            // this won't happen, we know Java has MD5!
        }
        return result;
    }

    public static String toHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(a[i] & 0x0f, 16));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("MD5 for abcde:\t" + toMD5Hex("abcde"));
        System.out.println("MD5 for bbb:\t" + toMD5Hex("bbb"));
        System.out.println("MD5 for abcde:\t" + toMD5Hex("abcde"));
        System.out.println("MD5 for 12345:\t" + toMD5Hex("12345"));
    }
}
