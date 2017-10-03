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
    public static OutputStreamWriter out;
    public static BufferedReader in;
    
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
            String st="c"+string;
            out.write(st,0,st.length());
            out.flush();
//            out.write(st, 0, st.length());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
