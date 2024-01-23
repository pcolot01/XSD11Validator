package eu.europa.publications.xml.tools;

import java.io.BufferedInputStream;
// import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

public class Input implements LSInput {
    
    private String publicId;
    
    private String systemId;
    
    private String baseURI;
    
    private InputStream inputStream;
    
    
    public String getPublicId() {
        return publicId;
    }
    
    public String getBaseURI() {
        return baseURI;
    }
    
    public InputStream getByteStream() {
        try {
            String path = XSD11Validator.resolveRelativePath(baseURI, systemId);
            System.out.print("InputStream getByteStream: " + path + "\n");
            inputStream = XSD11Validator.getXmlInputStream(path).getInputStream();
            return inputStream;
        } catch (ApplicationHandler e) {
            return null;
        }    
    }
    
    public boolean getCertifiedText() {
        return false;
    }
    
    public Reader getCharacterStream() {
        return null;
    }
    
    public String getEncoding() {
        return null;
    }
    
    public String getStringData() {
//        synchronized (inputStream) {
//            try {
//                byte[] input = new byte[inputStream.available()];
//                inputStream.read(input);
//                String contents = new String(input);
//                return contents;
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Exception " + e);
//                return null;
//            }
//        }
        return null;
    }
    
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }
    
    public void setByteStream(InputStream byteStream) {
    }
    
    public void setCertifiedText(boolean certifiedText) {
    }
    
    public void setCharacterStream(Reader characterStream) {
    }
    
    public void setEncoding(String encoding) {
    }
    
    public void setStringData(String stringData) {
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public BufferedInputStream getInputStream() {
        try {
            String path = XSD11Validator.resolveRelativePath(baseURI, systemId);
            System.out.print("BufferedInputStream getByteStream: " + path + "\n");
            inputStream = XSD11Validator.getXmlInputStream(path).getInputStream();
            return new BufferedInputStream(inputStream);
        } catch (ApplicationHandler e) {
            return null;
        }    
    }
    
    public void setInputStream(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    
    
    public Input(String publicId, String sysId, InputStream input) {        
        this.publicId = publicId;
        this.systemId = sysId;
        this.inputStream = input;
    }
    
}
