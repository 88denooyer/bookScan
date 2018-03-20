import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.Graphics2D;
import com.jaunt.*;
import com.jaunt.component.Table;

import java.util.Scanner;


public class bookScanner {

    // ================================================================================================================

    private static String decodeQRCode(File qrCodeImage) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(qrCodeImage);    // reads in an image (.png) source
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        //source.getMatrix();

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));    // creates bitmap of the read image
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }

    // ================================================================================================================

    private static void findTitle(String isbn) throws JauntException {

        UserAgent userAgent = new UserAgent();      //create new userAgent (headless browser)
        userAgent.settings.autoSaveAsHTML = true;

        userAgent.visit("http://bookfinder4u.com/IsbnSearch.aspx?isbn=" + isbn + "&mode=direct");
        Element elements;   // creates object 'elements'

        elements = userAgent.doc.findFirst("<b class=t9>");
        //elements = userAgent.doc.findFirst("<h1>");
        System.out.println("The name of the book you entered is: " + "'" + elements.innerText() + "'");
    }

    // ================================================================================================================

    private static void convertBW() {

        try{
            File file = new File("/Users/andrewdenooyer/Documents/bookScanner/src/main/resources/res/" +
                    "CUDABOOK.jpeg");
            BufferedImage originalImage = ImageIO.read(file);

            BufferedImage bwImage = new BufferedImage(
                    originalImage.getWidth(), originalImage.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D graphics = bwImage.createGraphics();
            graphics.drawImage(originalImage, 0, 0, null);

            ImageIO.write(bwImage, "png", new File("/Users/andrewdenooyer/Documents/bookScanner/" +
                    "src/main/resources/del/CUDABOOKBW.jpeg"));


        } catch(IOException e){
            System.out.println(e.getMessage());
        }


    }

    public static void main(String[] args) throws JauntException {

        double start = System.currentTimeMillis();
        try {

            convertBW();
            File readFile = new File("/Users/andrewdenooyer/Documents/bookScanner/src/main/resources/del/" +
                    "CUDABOOKBW.jpeg");
            String decodedText = decodeQRCode(readFile);
            try {
                findTitle(decodedText);

            } finally {
                System.out.println("\n");}

            if (decodedText == null) {
                System.out.println("ERR: NO BARCODE DETECTED");
            } else {
                System.out.println("The ISBN for your book is:  " + decodedText);
            }
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
        }
        // ================================================================================================================

        File deleteFile = new File("/Users/andrewdenooyer/Documents/bookScanner/src/main/resources/del/" +
                "CUDABOOKBW.jpeg");

        if(deleteFile.delete()) {System.out.println("\n");}
        else { System.out.println("FAILED");}

        double end = System.currentTimeMillis();
        double time = end - start;

        System.out.println("Time = " + time + " milliseconds");

    }
}
