package cn.wxjia.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;

import cn.wxjia.qrcode.MyQRCode;

/**
 * Test class
 * @author Jerry
 *
 */
public class Test {
	public static void main(String[] args) {
		// Test file
		File file = new File("file/qrcode.jpg");
		MyQRCode myQRCode = new MyQRCode();

		try {
			// Parse the file(picture whose format is PNG)
			List<Result> list = myQRCode.parseQRCode(file);
			for (Result result : list) {
				System.out.println("The result of picture prasing : "+result.getText());
			}

			//Generate QR Code
			myQRCode.generateQRCode(700, "http://www.csu.edu.cn/", file);
		} catch (ReaderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
