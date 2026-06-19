package com.dlc.modules.qd.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QrcodeUtil {

    //本地测试
//    public static final String QRCODE_IMAGE_PATH = "C:/JavaStudy/jx/hzjsf/src/main/webapp/statics/images/qrcode/";
    //服务器地址（main方法测试使用）
    public static final String QRCODE_IMAGE_PATH = "/www/wwwroot/shilijsf.shilisports.com/statics/images/qrcode/";

    public static String generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        String filename = String.valueOf(System.currentTimeMillis()) + ".png";
        Path path = FileSystems.getDefault().getPath(filePath + filename);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filename;
    }

    public static void main(String[] args) {
        try {
            generateQRCodeImage("This is my first QR Code", 350, 350, QRCODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }

    }

}
