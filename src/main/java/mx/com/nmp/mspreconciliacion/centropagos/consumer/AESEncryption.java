package mx.com.nmp.mspreconciliacion.centropagos.consumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.DecoderException;

/**
 * Clase  proporcionada por MIT para el cifrado/descifrado de request/response
 * para ejecutar WSCentro de pagos y obtener movimientos registrados
 * @author apiedra MIT
 * @version 1.0
 * 
 */
public class AESEncryption {
	private static final String ALGORITMO = "AES/CBC/PKCS5Padding";

	/**
	 * Permite encriptar una cadena a partir de un llave proporcionada
	 * @param plaintext
	 * @param key
	 * @return String con la cadena encriptada
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws DecoderException
	 */
	public static String encrypt(String plaintext, String key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {

		
		byte[] raw = DatatypeConverter.parseHexBinary(key);

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
		byte[] iv = cipher.getIV();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);

		byte[] finalData = outputStream.toByteArray();

		return DatatypeConverter
				.printBase64Binary(finalData);

	}

	/**
	 * Permite desencriptar una cadena a partir de la llave proporcionada
	 * @param encodedInitialData
	 * @param key
	 * @return String de la cadena en claro
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws DecoderException
	 */
	public static String decrypt(String encodedInitialData, String key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {

		byte[] encryptedData = DatatypeConverter
				.parseBase64Binary(encodedInitialData);

		byte[] raw = DatatypeConverter.parseHexBinary(key);

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);

		byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);

		byte[] cipherText = Arrays.copyOfRange(encryptedData, 16,
				encryptedData.length);
		IvParameterSpec ivSpecs = new IvParameterSpec(iv);

		cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpecs);

		byte[] plainTextBytes = cipher.doFinal(cipherText);

		return new String(plainTextBytes);
	}
	
	private AESEncryption() {
		
	}
}
