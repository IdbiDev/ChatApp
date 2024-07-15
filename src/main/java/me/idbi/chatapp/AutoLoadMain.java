package me.idbi.chatapp;

import lombok.Getter;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class AutoLoadMain {
    @Getter
    private static boolean useAutoupdate = true;
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException, InvocationTargetException, AWTException, NoSuchMethodException, IllegalAccessException {
        if(useAutoupdate) {
            URL myJarFile = new URI("http://192.168.1.112:8000/chatapp-1.0-SNAPSHOT-me.idbi.chatapp.jar").toURL();
            URL[] urls = { myJarFile };
            URLClassLoader child = new URLClassLoader(urls);

            Class DPRConfLoad = Class.forName("me.idbi.chatapp.Main", true, child);

            Method method = DPRConfLoad.getMethod("startApp", String[].class);
            method.invoke(null, (Object) args);
        } else {
            Main.startApp(args);
        }
    }
}
