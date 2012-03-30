package com.projectsexception.mzdroid.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class IOUtil {
    
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        final TelephonyManager mTelephony = (TelephonyManager) 
                context.getSystemService(Context.TELEPHONY_SERVICE);
        
        
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        
        int netType = networkInfo.getType();
        int netSubtype = networkInfo.getSubtype();
        
        // Cuando no está conectado o está conectado pero a roaming
        if (!networkInfo.isConnected()
                || (netType == ConnectivityManager.TYPE_MOBILE 
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && mTelephony.isNetworkRoaming())) {
            return false;
        }
        
        return true;
    }
    
    public static void copyFiles(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = from.read(buffer)) > 0) {
                to.write(buffer, 0, length);
            }
            to.flush();            
        } finally {
            try {
                to.close();
                from.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void lanzaParseador(InputStream stream, ContentHandler handler) throws IOException {
        if (stream != null) {
            try {
                final InputSource source = new InputSource(stream);
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();
                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                xr.setContentHandler(handler);
                xr.parse(source);
            } catch (SAXException e) {
                throw new IOException(e);
            } catch (ParserConfigurationException e) {
                throw new IOException(e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    // No hacer nada
                }                
            }
        }
    }

}
