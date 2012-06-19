package org.sleuthkit.autopsy.thunderbirdparser;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ThunderbirdEmailParser {
    
    
    private InputStream stream;
    //Tika object
    private Tika tika;
    private ThunderbirdMetadata metadata;
    private ContentHandler contentHandler;
    private String mimeType;   
    private ThunderbirdMboxParser parser;
    private ParseContext context;
    
    private static ArrayList<String> tikaMimeTypes;
    
    static
    {
        tikaMimeTypes = new ArrayList<String>();       
        tikaMimeTypes.add(MimeTypes.OCTET_STREAM);
        tikaMimeTypes.add(MimeTypes.PLAIN_TEXT);
        tikaMimeTypes.add(MimeTypes.XML);
    }
    
    public ThunderbirdEmailParser() 
    {
        this.tika = new Tika();
    }
    
    public ThunderbirdEmailParser(InputStream inStream) 
    {
        this.tika = new Tika();
        this.stream = inStream;
    }
    
    public ThunderbirdEmailParser(String filepath) 
    {
        this.tika = new Tika();
        this.stream = this.getClass().getResourceAsStream(filepath);
    }
    
    private void init() throws IOException
    {        
        this.tika.setMaxStringLength(10*1024*1024);
        this.metadata = new ThunderbirdMetadata();           
        //Set MIME Type    
        //this.mimeType = tika.detect(this.stream);  
        this.parser   = new ThunderbirdMboxParser();   
        this.context  = new ParseContext();
        this.contentHandler = new BodyContentHandler(10*1024*1024);
    }
    
    public void parse() throws FileNotFoundException, IOException, SAXException, TikaException
    {   
        init();     
        parser.parse(this.stream,this.contentHandler, this.metadata, context);
    }
    
    public void parse(InputStream inStream) throws FileNotFoundException, IOException, SAXException, TikaException
    {   
        init();        
        parser.parse(inStream,this.contentHandler, this.metadata, context);
    }
    
    public ThunderbirdMetadata getMetadata()
    {
        return this.metadata;
    }
    
    
    //Returns message content, i.e. plain text or html
    public ArrayList<String> getContent()
    {
        return this.parser.getXHTMLDocs();
    }
    
    public String detectEmailFileFormat(String filepath) throws IOException
    {
        return this.tika.detect(filepath);
    }
    
    //Detects the mime type from the first few bytes of the document
    public String detectMediaTypeFromBytes(byte[] firstFewBytes, String inDocName)
    {
        return this.tika.detect(firstFewBytes, inDocName);
    }
    
    
    public boolean isValidMimeTypeMbox(byte[] buffer)
    {      
        return (new String(buffer)).startsWith("From ");
    }
    
    //This assumes the file/stream was parsed since we are looking at the metadata
    public boolean isValidMboxType()
    {
        return this.metadata.get(Metadata.CONTENT_TYPE).equals("application/mbox");
    }
    
    //Get email subject
    public ArrayList<String> getSubjects()
    {
        return this.metadata.getValues(Metadata.SUBJECT);
    }
    
    public ArrayList<String> getTitles()
    {
        return this.metadata.getValues(Metadata.TITLE);
    }
    
    public ArrayList<Long> getDateCreated() 
    {
        Long epochtime;
        Long ftime = 0L;
        ArrayList<Long> dates = new ArrayList<Long>();
        try {
            ArrayList<String> datetime = this.metadata.getValues("date");
            for(String s : datetime){
            epochtime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(s).getTime();
            ftime = epochtime.longValue();
            ftime = ftime / 1000;
            dates.add(ftime);
        }
        } catch (ParseException ex) {
            Logger.getLogger(ThunderbirdMboxFileIngestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dates;
    }
    
    public ArrayList<String>  getContenType()
    {
        return this.metadata.getValues(Metadata.CONTENT_TYPE);
    }
    
    public ArrayList<String>  getContenEncoding()
    {
        return this.metadata.getValues(Metadata.CONTENT_ENCODING);
    }
    
    public ArrayList<String>  getFrom()
    {
        return this.metadata.getValues(Metadata.CREATOR);
    }
    
    public ArrayList<String>  getTo()
    {
        return this.metadata.getValues(Metadata.MESSAGE_TO);
    }
    
    public ArrayList<String>  getCC()
    {
        return this.metadata.getValues(Metadata.MESSAGE_CC);
    }
    
    public ArrayList<String>  getBCC()
    {
        return this.metadata.getValues(Metadata.MESSAGE_BCC);
    }
    
    public ArrayList<String>  getRecipientAddress()
    {
        return this.metadata.getValues(Metadata.MESSAGE_RECIPIENT_ADDRESS);
    }
    
    public String getMboxSupportedMediaType()
    {
        return MediaType.application("mbox").getType();
    }
}