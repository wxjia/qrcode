package cn.wxjia.qrcode;

import com.google.zxing.*;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

import javax.imageio.ImageIO;

import java.io.*;
import java.util.*;

/**
 * My QR Code parsing and generating class
 * @author Jerry
 *
 */
public class MyQRCode {
	/**
	 * 
	 * @param size
	 * @param content
	 * @param file
	 * @throws WriterException
	 * @throws IOException
	 */
	public void generateQRCode(int size, String content, File file)
			throws WriterException, IOException {
		Map<EncodeHintType, Object> HINTS;
		HINTS = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		HINTS.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, size, size, HINTS);
		MatrixToImageWriter.writeToFile(bitMatrix, "png", file);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws ReaderException
	 * @throws IOException
	 */
	public List<Result> parseQRCode(File file) throws ReaderException,
			IOException {

		Map<DecodeHintType, Object> HINTS;
		Map<DecodeHintType, Object> HINTS_PURE;

		HINTS = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		HINTS.put(DecodeHintType.POSSIBLE_FORMATS,
				EnumSet.allOf(BarcodeFormat.class));
		HINTS_PURE = new EnumMap<DecodeHintType, Object>(HINTS);
		HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

		LuminanceSource source = new BufferedImageLuminanceSource(
				ImageIO.read(new FileInputStream(file)));
		BinaryBitmap binaryBitmap = new BinaryBitmap(
				new HybridBinarizer(source));

		List<Result> results = new ArrayList<Result>();
		ReaderException savedException = null;
		Reader reader = new MultiFormatReader();
		try {
			// Find Multiple QR code
			MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(
					reader);
			Result[] theResults = multiReader.decodeMultiple(binaryBitmap,
					HINTS);
			if (theResults != null) {
				results.addAll(Arrays.asList(theResults));
			}
		} catch (ReaderException re) {
			savedException = re;
		}

		if (results.isEmpty()) {
			try {
				// Find pure code
				Result theResult = reader.decode(binaryBitmap, HINTS_PURE);
				if (theResult != null) {
					results.add(theResult);
				}
			} catch (ReaderException re) {
				savedException = re;
			}
		}

		if (results.isEmpty()) {
			try {
				// Find normal code
				Result theResult = reader.decode(binaryBitmap, HINTS);
				if (theResult != null) {
					results.add(theResult);
				}
			} catch (ReaderException re) {
				savedException = re;
			}
		}

		if (results.isEmpty()) {
			try {
				// Find special code
				BinaryBitmap hybridBitmap = new BinaryBitmap(
						new HybridBinarizer(source));
				Result theResult = reader.decode(hybridBitmap, HINTS);
				if (theResult != null) {
					results.add(theResult);
				}
			} catch (ReaderException re) {
				savedException = re;
			}
		}
		if (results.isEmpty()) {
			throw savedException;
		} else {
			return results;
		}
	}
}
