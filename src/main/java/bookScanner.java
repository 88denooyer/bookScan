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
    /* This function initializes the objects needed to initiate a scan for the barcode */
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
    /* This function initializes a "search object" which is userAgent
     * It visits the website and inserts the decoded ISBN into the search URL
     * An element needs to be found
     * The title on this specific website is contained in "<b class=t9>"
     * It will then find and print the "innerText" which is what comes after the <b> tag within the code */

    private static void findTitle(String isbn) throws JauntException {

        UserAgent userAgent = new UserAgent();      //create new userAgent (headless browser)
        userAgent.settings.autoSaveAsHTML = true;

        userAgent.visit("http://bookfinder4u.com/IsbnSearch.aspx?isbn=" + isbn + "&mode=direct");
        Element elements;   // creates object 'elements'

        elements = userAgent.doc.findFirst("<b class=t9>");
        // TODO: FIND THE TAGS FOR *AUTHOR, EDITION(?), CHECK AMAZON PRICES(?)
        //elements = userAgent.doc.findFirst("<h1>");
        System.out.println("The name of the book you entered is: " + "'" + elements.innerText() + "'");
    }

    // ================================================================================================================
    /* Creates new file object to be read as the original image
     * Reads image object and gets parameters to begin converting to black and white
     * Writes new image which has now been converted to BW
     * Note: this file is placed in a "delete" folder which will be called for deletion later to save space
     *      This may be fixed by the TODO below */

    private static void convertBW() {
        // TODO: FIND WAY TO INIT FILE OBJECT WITHOUT CALLING LOCAL DIRECTORY
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

        double start = System.currentTimeMillis();  // just to see how long the program takes
        try {

            /* Calls black and white conversion function
             * Uses black and white photo for decoding (RGB photo does not scan)
             * Then calls function to find the title of the book from the searched ISBN
             *
             * */
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

        double end = System.currentTimeMillis();    // again just to see how long the program takes to run
        double time = end - start;
        System.out.println("Time = " + time + " milliseconds");

    }
}
