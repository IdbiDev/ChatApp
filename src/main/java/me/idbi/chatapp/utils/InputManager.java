package me.idbi.chatapp.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import me.idbi.chatapp.Main;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class InputManager {

    public InputManager() {

    }

    public String getInput(String text) {
        System.out.print(text);
        Scanner scn = new Scanner(System.in);
        //DataInputStream scn = new DataInputStream(System.in);
        String nextLine = scn.nextLine();
        Main.debug("TESZT: " + StandardCharsets.UTF_8.decode(StandardCharsets.UTF_8.encode(nextLine)).get());
        String input = StandardCharsets.UTF_8.decode(StandardCharsets.UTF_8.encode(nextLine)).toString();
//        try {
//            input = scn.readUTF().strip();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //scn.useLocale(loc);

      //  if(!scn.hasNextLine()) return null;



        Main.debug(Arrays.toString(input.getBytes()));
        Main.debug(new String(input.getBytes()));
        for (char c : input.toCharArray()) {
            Main.debug("CHAR: " + c);
        }

        String oldEncoding = getEncoding(input);
        //testRnb(input, oldEncoding);
        Main.debug("INPUT: " + input + " ENCODING: " + oldEncoding);

        input = convert(input, oldEncoding, "UTF-8");
        Main.debug("NEW INPUT: " + input + " NEW ENCODING: " + getEncoding(input));
        return input;
    }

    public String getEncoding(String text) {
        byte[] bytes = null;
        bytes = text.getBytes(Charset.defaultCharset());
        CharsetDetector detector = new CharsetDetector();
        detector.setText(bytes);
        CharsetMatch match = detector.detect();
        InputStreamReader reader = (InputStreamReader) match.getReader();
        return reader.getEncoding();
    }

    public String convert(String value, String fromEncoding, String toEncoding) {
        try {
            return new String(value.getBytes(fromEncoding), toEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void testRnb(String originalString, String encoding) {
        // Assume the initial encoding is ISO-8859-1 for this example
        Charset initialCharset = Charset.forName(encoding);

        // Sample input string in ISO-8859-1 encoding

        // Convert the string to bytes using the initial encoding
        byte[] inputBytes = originalString.getBytes(initialCharset);

        // Convert the byte array to a string using the assumed initial encoding
        String decodedString = new String(inputBytes, initialCharset);

        // Encode the string to UTF-8 using Apache Commons Lang
        byte[] utf8Bytes = StringUtils.getBytes(decodedString, StandardCharsets.UTF_8);

        // Decode the UTF-8 bytes back to a string
        String utf8String = new String(utf8Bytes, StandardCharsets.UTF_8);

        // Verify the result
        System.out.println("Original String: " + originalString);
        System.out.println("Decoded String: " + decodedString);
        System.out.println("UTF-8 String: " + utf8String);
    }

}
