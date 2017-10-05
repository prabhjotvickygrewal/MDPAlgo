/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;
import java.net.*;
import java.io.*;
import algorithm.*;
import map.*;
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
    	
    	try {
            Thread.sleep(100);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    	try{
	        String st="AC"+string;
	        out.write(st,0,st.length());
	        out.flush();
//	        st="ABmove::"+string;
//	        out.write(st, 0, st.length());
//	        out.flush();
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
	    	s=s.substring(2, s.length());
    		System.out.println("received");
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
    		
    		if(s.contains("startpoint")){
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
    		}
    		else if(s.contains("endpoint")){
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
    		return (s.equals(st) || s.equals(st+"\n"));
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
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
