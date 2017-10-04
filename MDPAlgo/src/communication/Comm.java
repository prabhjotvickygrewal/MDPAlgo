/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;
import java.net.*;
import java.io.*;
/**
 *
 * @author kokc0009
 */
public class Comm {
    private static Socket socket;
    private static OutputStreamWriter out;
    private static BufferedReader in;
    
    public Comm(){
        try{
            socket=new Socket(InetAddress.getByName("192.168.13.1"), 12345);
//            out=new OutputStreamWriter(socket.getOutputStream());
            out=new OutputStreamWriter(socket.getOutputStream());
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void test(){
        try{
//            out.write("ctest",0,5);
            out.write("test",0,4);
            System.out.println("test passed");
            out.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void sendToAndroid(String string){
        try{
            String st="AB"+string;
            out.write(st,0,st.length());
            out.flush();
//            out.write(st, 0, st.length());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void sendToRobot(String string){
    	try{
	        String st="AC"+string;
	        out.write(st,0,st.length());
	        out.flush();
	        st="ABmove::"+string;
	        out.write(st, 0, st.length());
    	}
        catch(IOException e){
        	e.printStackTrace();
        }
    }
    public static boolean checkActionCompleted(){
//    	System.out.println("action executing");
    	try{
	    	String s=in.readLine();
	    	s=s.substring(2, s.length());
    		System.out.println("received");
    		System.out.println(s);
	    	return (s.equals("-2") || s.equals("-2\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static boolean checkCalibrationCompleted(){
//    	System.out.println("calibrating");
    	try{
    		String s=in.readLine();
    		s=s.substring(2, s.length());
    		System.out.println(s);
    		return (s.equals("1") || s.equals("1\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static String receiveSensorData(){
    	System.out.println("scanning");
    	try{
    		String s=in.readLine();
    		s=s.substring(2, s.length());
    		System.out.println("recived");
    		if(s.length()>=11)    			
    			return s;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
        return "0,0,0,0,0,0";
    }
    public static boolean checkAndroidMessage(String st){
    	System.out.println("wait for android instruction "+st);
    	try{
    		String s=in.readLine();
    		s=s.substring(2, s.length());
    		System.out.println("received");
    		return (s.equals(st) || s.equals(st+"\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static void checkAndroidMessage(){
    	
    }
    public static void close(){
    	try{
    		socket.close();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
