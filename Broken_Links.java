// URL Broken Link and Accessiblity Checker

/*Name: Md Farhadur Reza
PhD Student
The Center for Advanced Computer Studies
University of Louisiana at Lafayette
CLID: mxr7945
*/



import java.awt.*;
import java.awt.event.*;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;

import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import javax.swing.text.html.*;

import java.applet.*;
//import com.jscape.inspect.*;


public class Broken_Links extends javax.swing.JFrame implements Runnable{

	  boolean frameSizeAdjusted = false;
	  JLabel label1 = new JLabel();
	  JLabel label2 = new JLabel();
	  JLabel label3 = new JLabel();
	  JLabel label4 = new JLabel();
	  JButton begin = new JButton();
	  JTextField url = new JTextField();
	  JTextField url_depth = new JTextField();
	  JTextField type_url = new JTextField();
	  JTextArea output_url = new JTextArea();
	  JScrollPane errorScroll = new JScrollPane();
	 
	  JTextArea errors = new JTextArea();
	  JLabel current = new JLabel();	 
	  JLabel url_label = new JLabel();
	  public Thread backgroundThread;
	  public URL base;
	  public int depth;
	  public String type;
	 
	  public Collection<String> url_type = new ArrayList<String>();
	  public String depth_test;
	  public String type_test;
	  public String [] names ={"http","ftp", "mailto", "file", "Local_Links","https"};	 
	  JList url_list= new JList(names);
	  JList url_selection_list;
	  JButton select_buttton;
	  ArrayList<String> list_3= new ArrayList<String>();
	  JScrollPane urlScroll;
	  JScrollPane SelectUrlScroll;
	  public Collection error_url = new ArrayList(3);
	  public Collection waiting_url = new ArrayList(3);
	  public Collection processed_url = new ArrayList(3); 
	  public boolean cancel = false;
	  
	
  public Broken_Links()
  {
	  setLayout(new FlowLayout());
    setTitle("Find Broken Links");
    getContentPane().setLayout(null);
    
    setSize(1000,600);
    setVisible(false);
    label1.setText("Enter a URL");
    getContentPane().add(label1);
    label1.setBounds(200,10,90,30);
    getContentPane().add(label1);   
    setVisible(true);
    label2.setText("Enter Depth");
    getContentPane().add(label2);
    label2.setBounds(400,10,90,30);
    label3.setText("Select URL Type");
    getContentPane().add(label3);
    label3.setBounds(500,10,150,30);
    
    label4.setText("Selected URL Type");
    getContentPane().add(label4);
    label4.setBounds(800,10,150,30);      
    begin.setText("Begin");
    begin.setActionCommand("Begin");
    getContentPane().add(begin);
    begin.setBounds(12,36,84,24);
    getContentPane().add(url);
    url.setBounds(108,36,288,24);
    
    getContentPane().add(url_depth);
    url_depth.setBounds(400,36,48,24);
    
    
    
    //url_list.setAutoscrolls(true);
    url_list.setVisibleRowCount(5);
    url_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    urlScroll= new JScrollPane(url_list);
  //  getContentPane().add(url_list);  
    getContentPane().add(urlScroll); 
    select_buttton= new JButton("Select");
    //url_list.setBounds(500,36,90,90);
    urlScroll.setBounds(500,36,120,100);
    
    select_buttton.addActionListener(
    		new ActionListener(){
    			public void actionPerformed(ActionEvent event){
    				url_selection_list.setListData(url_list.getSelectedValues());
    			}
    		}
    		
    		);
    
    getContentPane().add(select_buttton);
    
    select_buttton.setBounds(650,36,90,30);
    
    url_selection_list= new JList();
    url_selection_list.setVisibleRowCount(5);
    
    url_selection_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    SelectUrlScroll= new JScrollPane(url_selection_list);
    getContentPane().add(SelectUrlScroll);
    SelectUrlScroll.setBounds(800,36,90,90);
    
    errorScroll.setAutoscrolls(true);
    errorScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    errorScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    errorScroll.setOpaque(true);
    getContentPane().add(errorScroll);
    errorScroll.setBounds(12,150,900,400);
    output_url.setEditable(false);
    //errors.setEditable(false);
    errorScroll.getViewport().add(output_url);
    //errorScroll.getViewport().add(errors);
    
    output_url.setBounds(0,0,900,400);
    //errors.setBounds(0,0,1000,800);
    current.setText("Currently Processing: ");
    getContentPane().add(current);
    current.setBounds(12,72,384,12);

    StartAction start_action = new StartAction();
    begin.addActionListener(start_action);
 
  }


  static public void main(String args[])
  {
	
	  
    (new Broken_Links()).setVisible(true);
	 
  }
  
  //Add notifications.
  public void addNotify()
  {
    Dimension size = getSize();

    super.addNotify();

    if ( frameSizeAdjusted )
      return;
    frameSizeAdjusted = true;
    Insets insets = getInsets();
    javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
    int menuBarHeight = 0;
    if ( menuBar != null )
      menuBarHeight = menuBar.getPreferredSize().height;
    setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
  }

  
  
  
  public Collection<String> url_type_list()
  {
    return url_type;
  }

  //Internal class used to dispatch events
  class StartAction implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      Object object = event.getSource();
      if ( object == begin )
        start_actionPerformed(event);
    }
  }

  //Called when the begin or cancel buttons are clicked

  void start_actionPerformed(java.awt.event.ActionEvent event)
  {
    if ( backgroundThread==null ) {
      begin.setLabel("Cancel");
      //begin.setLabel("begin");
      backgroundThread = new Thread(this);
      backgroundThread.start();
     
    } else {
    	cancel();
    }

  }

  //This method starts the background thread.
  
  public void run()
  {
    try {
      Scanner scan = new Scanner(System.in);           
      errors.setText("");        
      clear();
      base = new URL(url.getText());
      //depth = scan.nextInt();
      depth_test= url_depth.getText();
      depth= Integer.parseInt(depth_test);
      List<String> list_1 = new ArrayList<String>();
     // Iterator<String> it= url_selection_list.getModel().getSize()
      List<String> list_2 = new ArrayList<String>();
      list_2.add("http");
      list_2.add("ftp");
      list_2.add("file");
      list_2.add("Local_Links");
      list_2.add("mailto");
      list_2.add("https");
     
      
      URL_output out = new URL_output();
      out.msg = "Root URL is: "+base+"\n";
      SwingUtilities.invokeLater(out);
      
      type="http";
      URL base2=base;
      int depth_check= depth;
   
          addURL(base);
          begin(depth);
           
      
      Runnable doLater = new Runnable()
      {
        public void run()
        {
          begin.setText("Begin");
        }
      };
      SwingUtilities.invokeLater(doLater);
      backgroundThread=null;
      scan.close();

    } catch ( MalformedURLException e ) {
      UpdateErrors err = new UpdateErrors();
      err.msg = "Bad address.";
      SwingUtilities.invokeLater(err);          

    }
    
  }

  //called when links are found. Links and links type are validated here.
  public void FoundURL(URL base,URL url,int depth_count)
  {
    UpdateCurrentStats cs = new UpdateCurrentStats();
    cs.msg = url.toString();
    SwingUtilities.invokeLater(cs);
    URL_output out = new URL_output();
    
    for (int i = 0; i < url_selection_list.getModel().getSize(); i++) {
        Object item = url_selection_list.getModel().getElementAt(i);
        String s=item.toString();
        
        if(url.getProtocol().equals(s) || s.equals("Local_Links") || s.equals("https")){
    		          

    if ( !checkLink(url)) 
    {
    	 if (url.getProtocol().equals("mailto") && (s.equals("mailto"))){
    	
    	        out.msg = "\t"+url+"  (Link Type:mailto link, Status-not checked, Parent URL is- " + base + ",Depth-"+(depth_count+1)+")\n";
    	       
    	        SwingUtilities.invokeLater(out);
    	    }
    	
    	 if (url.getProtocol().equals("file")&& (s.equals("file"))){
   		
   			    	
   			        out.msg = "\t"+url+"  (Link Type:File link, Status:Borken, Parent URL is- " + base +",Depth-"+(depth_count+1)+")\n";
   			      
   			        SwingUtilities.invokeLater(out);
   			        
   			        
   			        
   			    }  
    	 
    	 if (url.getProtocol().equals("ftp")&& (s.equals("ftp"))){
    	    	
    	        out.msg = "\t"+url+"  (Link Type:ftp link, Status:Broken, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
    	       
    	        SwingUtilities.invokeLater(out);
    	    	
    	    	
    	    }
    	 
    	 
    	
    	  if (url.getHost().toString().equals(base.getHost().toString()) && (s.equals("Local_Links"))){
        	
             out.msg = "\t"+url+"  (Link Type:Local link, Status:Broken, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
             
             SwingUtilities.invokeLater(out);
             
             }
    	 
    	 
    
    	if (url.getProtocol().equals("http") && s.equals("http")){
    	    	
    	    	
    	         out.msg = "\t"+url+"  (Link Type: http link, Status: Broken, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
    	        
    	         SwingUtilities.invokeLater(out);    
    	    	
    	       
    	        	       
    	        
    		  }
    	 
    	 if (url.getProtocol().equals("https") &&  s.equals("https")){
 	    	
 	    	
 	         out.msg = "\t"+url+"  (Link Type: https link, Status: Broken, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
 	       
 	         SwingUtilities.invokeLater(out);    
 	    	
 	       
 	        	       
 	        
 		  }
      
      
    }

    
    
    
    
    else{
    if (url.getProtocol().equals("file")&& (s.equals("file"))){
		 
			    	
			        out.msg = "\t"+url+"  (Link Type:File link, Status:Active, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
			    
			        SwingUtilities.invokeLater(out);
			        
			        
			      
			    }  
  
  
    
    if (url.getProtocol().equals("ftp")&& (s.equals("ftp"))){
    	
        out.msg = "\t"+url+"  (Link Type:ftp link, Status:Active, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
       
        SwingUtilities.invokeLater(out);
    	
    	
    }
    
     if (url.getHost().toString().equals(base.getHost().toString()) && (s.equals("Local_Links"))){
   
    	
        out.msg = "\t"+url+"  (Link Type:Local link, Status-Active, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
     
        SwingUtilities.invokeLater(out);
    	
    	
     }
       
   
    
   
 
    
 
     if (url.getProtocol().equals("http") && s.equals("http")){
    	
    	
    	 out.msg = "\t"+url+"  (Link Type: http link, Status:Active, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
    	
         SwingUtilities.invokeLater(out);    
    	
       
        
	  }
    
    if (url.getProtocol().equals("https") && s.equals("https")){
    	
    	
    	 out.msg = "\t"+url+"  (Link Type: https link, Status:Active, Parent URL is- " + base +",Depth-"+(depth_count+1)+ ")\n";
    	
         SwingUtilities.invokeLater(out);    
    	
       
        
	  }
  
       addURL(url);
        } 
      }
        
        
     
    }
    
  }

  public void FoundURLError(URL url)
  {
	  URL_output out = new URL_output();
	  out.msg = "\t"+url+"(Found Broken Links)\n";
	  
  }
  
  public void FoundEMail(String email,URL base)
  {
	  URL_output out = new URL_output();
	  out.msg = "\t"+email+"(mailto link: Parent URL is- " + base + ")\n";
     
      SwingUtilities.invokeLater(out);
  }
  
  public void FoundContent(URL url)
  {
	  URL_output out = new URL_output();
	  out.msg = "\t"+url+"(Found content: Not processing: Parent URL is- " + base + ")\n";
     
      SwingUtilities.invokeLater(out);
  }
  
  
 

  protected boolean checkLink(URL url)
  {
    try {
      URLConnection connection = url.openConnection();
      connection.connect();  
      return true;
    } catch ( IOException e ) {
    	
    	
      return false;
    }
  }
  
  protected boolean checkLink_https(URL url)
  {
    try {
    	HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.connect();            
      return true;
    } catch ( IOException e ) {
    	
    	
      return false;
    }
  }
  
  

  //class used to update the error information

  public class UpdateErrors implements Runnable {
    public String msg;
    public void run()
    {
      output_url.append(msg);
    }
  }

  
  
//class used to update the output
  public class URL_output implements Runnable {
	    public String msg;	 
	    public void run()
	    {
	    	 	 
			            
	    	
	                   output_url.append(msg);
			          
	         
	  
  }

  } 
  
//Used to update the current status information
 public class UpdateCurrentStats implements Runnable {
    public String msg;
    public void run()
    {
      current.setText("Currently Processing: " + msg );
     
    }
  }

 //Get the URLs that resulted in an error.
 public Collection Error_URL()
 {
   return error_url;
 }

//Get the URLs that were waiting to be processed
 public Collection Waiting_URL_Queue()
 {
   return waiting_url;
 }

//Get the URLs that were processed
 public Collection Processed_URL()
 {
   return processed_url;
 }    

 //Clear all of the workloads.
 public void clear()
 {
	  Error_URL().clear();
	  Waiting_URL_Queue().clear();
     Processed_URL().clear();
 }


 //Set a flag that will cause the begin method to return before it is done.
 public void cancel()
 {
   cancel = true;
 }


 //Add a URL for processing.
 
 public void addURL(URL url)
 {
   if ( Waiting_URL_Queue().contains(url) )
     return;
   if ( Error_URL().contains(url) )
     return;
   if ( Processed_URL().contains(url) )
     return;  
   Waiting_URL_Queue().add(url);
  
 }
 

 public void processURL(URL url,int depth_count)
 {
   try {
   
     URLConnection connection = url.openConnection();
     
     
     if ( (connection.getContentType()!=null) &&
          !connection.getContentType().toLowerCase().startsWith("text/") ) {
   	  
   	 
   	  Waiting_URL_Queue().remove(url);        
   	  Processed_URL().add(url);   
   	
   		  FoundContent(url);
   	    
       return;
     }
     
       
     // read the contents of URL
     InputStream is = connection.getInputStream();    
     Reader r = new InputStreamReader(is);
        
     // parse the URL
     HTMLEditorKit.Parser parse = new HTMLParse().getParser();
     parse.parse((r),new Parser(url,depth_count),true);
   } catch ( IOException e ) {
     Waiting_URL_Queue().remove(url);
     Error_URL().add(url);
     System.out.println("Error: " + url );
     FoundURLError(url);
     return;
   }
   // mark URL as complete
   Waiting_URL_Queue().remove(url);        
   Processed_URL().add(url);
 

 }

 
 //Called to start the webpage scanning
 public void begin(int depth)
 {
   cancel = false;
   
   int depth_count=0;
   URL_output out = new URL_output();
  while ( !Waiting_URL_Queue().isEmpty() && !cancel && depth_count<depth) 
   {
   
     Object list[] = Waiting_URL_Queue().toArray();
     for ( int i=0;(i<list.length)&&!cancel;i++)
     {
    	
   	    out.msg = "\nChild of URL: "+(URL)list[i]+" : " +" Depth of the URL is: "+(depth_count)+"\n";
   	    SwingUtilities.invokeLater(out);  
    	processURL((URL)list[i],depth_count);
          
          
                       
   }
   depth_count++;
   System.out.println("Depth"+ depth_count);
  
   }
   
  out.msg = "Depth of the tree reached: "+ depth_count;
  SwingUtilities.invokeLater(out);
 
 }

 
 //call the "getParser" method of the "HTMLEditorKit" class
 public class HTMLParse extends HTMLEditorKit {

	  public HTMLEditorKit.Parser getParser()
	  {
	    return super.getParser();
	  }
 }
 
//A HTML parser used to detect links
 public class Parser
 extends HTMLEditorKit.ParserCallback {
   protected URL base;
   protected int depth_count2;
   protected String alt;

   public Parser(URL base,int depth_count2)
   {
     this.base = base;
     this.depth_count2=depth_count2;
   }

   //find link, make link and check html tags
   public void handleSimpleTag(HTML.Tag t,
                               MutableAttributeSet a,int pos)
   {
	 
	   
     String href = (String)a.getAttribute(HTML.Attribute.HREF);
     URL_output out = new URL_output();
         
     if((href==null)){
    	 if((t==HTML.Tag.FRAME) || (t==HTML.Tag.IMG) || (t==HTML.Tag.SCRIPT)||(t==HTML.Tag.HTML) ||(t==HTML.Tag.APPLET)||t==HTML.Tag.AREA){
     
       href = (String)a.getAttribute(HTML.Attribute.SRC);
       
       if(t==HTML.Tag.IMG){
       
       if((String)a.getAttribute(HTML.Attribute.ALT)==null){
      	   System.out.println("Found Image without ALT attribute:- "+href);
      	        	     		 
      	 }
       
       if((String)a.getAttribute(HTML.Attribute.ALT)!=null){
    	   if(((String)a.getAttribute(HTML.Attribute.ALT.COLOR)!=null)){
    		   System.out.println("Found Image with ALT attribute color that is not useful for blind users:-"+href);
           	}
    	   if(((String)a.getAttribute(HTML.Attribute.ALT.TEXT))==((String)a.getAttribute(HTML.Attribute.SRC.TEXT))){
    		   System.out.println("ALT description shouldn't be same as Image name:-"+href);
             	}
    	   
    	   if( (((String)a.getAttribute(HTML.Attribute.ALT.TEXT))=="picture") || (((String)a.getAttribute(HTML.Attribute.ALT.TEXT))=="spacer")){
    		   System.out.println("ALT description shouldn't contain placeholder text like 'picture' or 'spacer':-"+href);
               	}
       }
       }
    	   
    	   
       if((t==HTML.Tag.FRAME)){
    		   if((String)a.getAttribute(HTML.Attribute.TITLE)==null){
    			   System.out.println("No TITLE attributes found for the frames on these pages:-"+href);
    		   }
    		   
    		   if(((String)a.getAttribute(HTML.Attribute.TITLE.TEXT))==((String)a.getAttribute(HTML.Attribute.SRC.TEXT))){
    			   System.out.println("Frame description shouldn't be same as frame name:- "+href);
    	          }
    	   }
       
       
       
       if(t==HTML.Tag.HTML){
      	 
      	 if((String)a.getAttribute(HTML.Attribute.LANG)==null){
      		System.out.println("Found webpage without Language attribute:- "+base);          	             	   
             }
      
      	 
      	
      	if((String)a.getAttribute(HTML.Attribute.TITLE)==null){
      		System.out.println("Found webpage without TITLE attribute "+base);
     	   
         }    	 
       }
             
       if(t==HTML.Tag.AREA){
    	     
      	 if((String)a.getAttribute(HTML.Attribute.ALT)==null){
      		System.out.println("Found area without ALT attribute:- "+base);
          	        	     		 
          	 }
           
           if((String)a.getAttribute(HTML.Attribute.ALT)!=null){
        	   if(((String)a.getAttribute(HTML.Attribute.ALT.COLOR)!=null)){
        		   System.out.println("Found area with ALT attribute color that is not useful for blind users:- "+base);
               }
        	   if(((String)a.getAttribute(HTML.Attribute.ALT.TEXT))==((String)a.getAttribute(HTML.Attribute.SRC.TEXT))){
        		   System.out.println("ALT description shouldn't be same as area name:- "+base);
              }
           }
       }
       
       if(t==HTML.Tag.APPLET){
           
      	 if((String)a.getAttribute(HTML.Attribute.ALT)==null){
      		System.out.println("Found applet without ALT attribute:- "+base);
          	        	     		 
          	 }
           
           if((String)a.getAttribute(HTML.Attribute.ALT)!=null){
        	   if(((String)a.getAttribute(HTML.Attribute.ALT.COLOR)!=null)){
        		   System.out.println("Found applet with ALT attribute color that is not useful for blind users:- "+base);
               	}
        	   if(((String)a.getAttribute(HTML.Attribute.ALT.TEXT))==((String)a.getAttribute(HTML.Attribute.SRC.TEXT))){
        		   System.out.println("ALT description shouldn't be same as applet name:- "+base);
                }
           }
       }
         
       
     
     }
     }
            
     if ( href==null )
       return;

     
  // Skip empty links.
     if (href.length() < 1) {
     return;
     }
     // Skip links that are just page anchors.
     if (href.charAt(0) == '#') {
     return;
     }
     
     int i = href.indexOf('#');
     if ( i!=-1 )
       href = href.substring(0,i);
     
     if ((href.toLowerCase().indexOf("javascript")) != -1) {
   	  return;
   	 }
  
    
    handleLink(base,href,depth_count2);
	
    
     
   }

   public void handleStartTag(HTML.Tag t,
                              MutableAttributeSet a,int pos)
   {
     handleSimpleTag(t,a,pos);// handle the same way

   }

   //call FoundURL method by passing URL to it
   protected void handleLink(URL base,String str,int depth_count3)
   {
     try {
       URL url = new URL(base,str); 
      
       FoundURL(base,url,depth_count3);
       
      
     } catch ( MalformedURLException e ) {
    	 System.out.println("Found malformed URL: " + str +" " + base );
     }
    
   }

 }   


}