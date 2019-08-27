package com.net.client;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Base64;

public class SecretKeyGenerator {

	public static void main( String[] args ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
    	
    	KeyGenerator kg=KeyGenerator.getInstance("AES");
    	kg.init(128);
    	SecretKey sk=kg.generateKey();  // 生成了密钥
    	byte[] keyByte=sk.getEncoded();    	
    	String base64String=Base64.toBase64String(keyByte);
    	System.out.println("请将下面生成的密钥复制到client与server的配置文件中去：");
    	System.out.println(base64String);  //生成了Base64的密钥

    }
	
}
