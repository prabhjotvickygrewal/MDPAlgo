/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;
import java.net.*;
import java.io.*;
import algorithm.*;
import map.*;
import simulation.GUI;
/**
 *
 * @author kokc0009
 */
public class Comm {
	private static final String rpiAddress = "192.168.13.1";	//127.0.0.1
	private static final int rpiPort = 12345;
    private static Socket socket = null;
    private static OutputStreamWriter out;
    private static BufferedReader in;
    
    public Comm(){
        try{
            socket=new Socket(InetAddress.getByName(rpiAddress), rpiPort);
//            out=new OutputStreamWriter(socket.getOutputStream());
            socket.setTcpNoDelay(true);
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
            String st="AB"+string+"\n";
            out.write(st,0,st.length());
            out.flush();
//            out.write(st, 0, st.length());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void sendToRobot(String string){
    	
//    	try {
//            Thread.sleep(50);                 //delay for rpi
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
    	try{
	        String st="AC"+string+"\n";
	        out.write(st,0,st.length());
	        out.flush();
    	}
        catch(IOException e){
        	e.printStackTrace();
        }
    }
    public static boolean checkActionCompleted(){
//    	System.out.println("action executing");
    	try{
	    	String s=in.readLine();
	    	System.out.println(s);
//    		System.out.println("received");
    		System.out.println(s);
	    	return (s.equals("-2") || s.equals("-2\n") || s.equals("-2\r\n"));
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
    		System.out.println(s);
    		return (s.equals("-2") || s.equals("-2\n") || s.equals("-2\r\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    }
    public static boolean checkArduinoMessage(String st){
    	try{
    		String s=in.readLine();
    		System.out.println(s);
    		return (s.equals(st) || s.equals(st+"\n") || s.equals(st+"\r\n"));
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
//    		System.out.println("recived");
    		if(s.length()>=11)    			
    			return s;
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
        return "0";
    }
    public static boolean checkAndroidMessage(String st){
    	System.out.println("wait for android instruction "+st);
    	try{
    		String s=in.readLine();
            System.out.println(s);
//    		System.out.println("received");
    		
    		if(s.contains("startpoint")){
    			System.out.println(s);
    			int cur=11;
    			String fragment="";
    			while(s.charAt(cur)!=':'){
    				fragment=fragment+s.charAt(cur);
    				cur++;
    			}
    			cur++;
    			int x=Integer.parseInt(fragment);
    			
    			fragment=s.substring(cur, s.length());
    			int y=Integer.parseInt(fragment);
    			Algorithm.startPoint=new Vector(x,y);
    			
    			System.out.println(Algorithm.startPoint.toString());
    		}
    		else if(s.contains("endpoint")){
    			System.out.println(s);
    			int cur=9;
    			String fragment="";
    			while(s.charAt(cur)!=':'){
    				fragment=fragment+s.charAt(cur);
    				cur++;
    			}
    			cur++;
    			int x=Integer.parseInt(fragment);
    			
    			fragment=s.substring(cur, s.length());
    			int y=Integer.parseInt(fragment);
    			Algorithm.endPoint=new Vector(x,y);
    			
    			System.out.println(Algorithm.endPoint.toString());
    		}
    		else if(s.contains("waypoint")){
    			int cur=9;
    			String fragment="";
    			while(s.charAt(cur)!=':'){
    				fragment=fragment+s.charAt(cur);
    				cur++;
    			}
    			cur++;
    			int x=Integer.parseInt(fragment);

    			fragment=s.substring(cur, s.length());
    			int y=Integer.parseInt(fragment);
    			Algorithm.wayPoint= new Vector(x,y);
    		}
    		return (s.equals(st) || s.equals(st+"\n") || s.equals(st+"\r\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    }

    public static void close(){
    	if(socket != null) {
	    	try{
	    		socket.close();
	    	}
	    	catch(IOException e){
	    		e.printStackTrace();
	    	}
    	}
    }
}
