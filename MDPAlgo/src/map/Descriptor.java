package map;
import java.util.*;

import map.Map;
import map.Vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
public class Descriptor {

	public static PointState[][] getStatesFromHex(String hex1, String hex2){
		PointState[][] states=new PointState[Map.MAX_X][Map.MAX_Y];
		hex1=hex1.trim();
		hex2=hex2.trim();
		String binFragment;
		String binary1=Integer.toBinaryString(Integer.parseInt(hex1.charAt(0)+"", 16));
		String binary2=new String();
		binary1=binary1.replaceFirst("11", "");
		
		for(int i = 1; i < hex1.length(); i++){           //convert char one by one
	        binFragment = Integer.toBinaryString(Integer.parseInt(""+hex1.charAt(i),16));
	        while(binFragment.length() < 4){
	            binFragment = "0" + binFragment;
	        }
	        binary1 += binFragment;
	    }
		for(int i=0;i<Map.MAX_X*Map.MAX_Y;i++)             //mark unknown points
			if(binary1.charAt(i)=='0')
				states[i/Map.MAX_Y][i%Map.MAX_Y]=PointState.Unknown;
		
		if(hex2!=null)
			for(int i = 0; i < hex2.length(); i++){           //convert char one by one
		        binFragment = Integer.toBinaryString(Integer.parseInt(""+hex2.charAt(i),16));
		        while(binFragment.length() < 4){
		            binFragment = "0" + binFragment;
		        }
		        binary2 += binFragment;
		    }
			int cur=0;
			for(int i=0;i<Map.MAX_X;i++)
				for(int j=0;j<Map.MAX_Y;j++){
					if(states[i][j]!=PointState.Unknown){
						if(binary2.charAt(cur)=='0')
							states[i][j]=PointState.IsFree;
						else
							states[i][j]=PointState.Obstacle;
						cur++;
					}
				}
		
		Map m=new Map();
		m.updatePointMap(states);
		m.printMap();
		return states;
	}
	
	public static String getFirstStringFromStates(PointState[][] states){
		String binary1="11";
		String hex1=new String();
		
		for(int i=0;i<Map.MAX_X;i++)
			for(int j=0;j<Map.MAX_Y;j++){
				if(states[i][j]==PointState.Unknown)
					binary1 += "0";
				else
					binary1 +="1";
			}
		binary1 +="11";
		for(int i=0; i<binary1.length();i+=4){           //convert to string digit by digit
			String binFragment=binary1.substring(i, i+4);
			hex1=hex1+Integer.toString(Integer.parseInt(binFragment, 2), 16);
		}
//		System.out.println(hex1);
		return hex1;
	}
	public static String getSecondStringFromStates(PointState[][] states){
		String binary2=new String();
		String hex2=new String();
		
		for(int i=0;i<Map.MAX_X;i++)
			for(int j=0;j<Map.MAX_Y;j++){
				if(states[i][j]==PointState.IsFree)
					binary2 =binary2+"0";
				else if(states[i][j]==PointState.Obstacle)
					binary2 =binary2+ "1";
			}
		int cur=0;
		while(binary2.length()%4!=0)
			binary2 = binary2+"0";
		for(int i=0;i<binary2.length()/4;i++){
			String binFragment=binary2.substring(i*4, i*4+4);
			hex2 = hex2+Integer.toString(Integer.parseInt(binFragment, 2), 16);
		}
//		System.out.println(hex2);
		return hex2;
	}

	public static PointState[][] getStatesFromFile(String fileName){
            String filePath=new File("").getAbsolutePath();
            try{
                BufferedReader br=new BufferedReader(new FileReader(
                    filePath+File.separator+"src"+File.separator+"map"+File.separator+"test"+File.separator+fileName));
                String line1, line2;
                line1=br.readLine();
                System.out.println(line1);
                line2=br.readLine();
                System.out.println(line2);
                if(line1!=null && line2!=null)
                    return getStatesFromHex(line1,line2);
                return null;
            }
            catch(IOException e){
                e.printStackTrace();
                return null;
            }
        }
        public static void writeFileFromStates(String fileName, PointState[][] states){
            String hex1=getFirstStringFromStates(states);
            String hex2=getSecondStringFromStates(states);
            String filePath=new File("").getAbsolutePath();
            System.out.println(filePath+File.separator+"src"+File.separator+"map"+File.separator+"result"+File.separator+fileName);
            if(hex1.length()!=0 && hex2.length()!=0)
	            try{
	//                File file=new File(filePath+File.separator+"src"+File.separator+"map"+File.separator+"result"+File.separator+fileName);
	                Files.write(Paths.get(filePath+File.separator+"src"+File.separator+"map"+File.separator+"result"+File.separator+fileName), 
	                		String.format("%s%n%s", hex1, hex2).getBytes(),
	                		StandardOpenOption.CREATE);
	//                BufferedWriter br=new BufferedWriter(new FileWriter(file, true));
	//                br.write(hex1);
	//                System.out.print(hex1);
	//                br.newLine();
	//                br.write(hex2);
	//                System.out.print(hex2);
	
	            }
	            catch(IOException e){
	                e.printStackTrace();
	            }
        }
        
//        public static void main(String[] args){
//            LinkedList<Vector> obstacle=new LinkedList<>();
//            obstacle.add(new Vector(15,7));
//            obstacle.add(new Vector(14,7));
//            obstacle.add(new Vector(13,7));
//            obstacle.add(new Vector(16,7));
//            obstacle.add(new Vector(16,8));
//            obstacle.add(new Vector(16,9));
//            obstacle.add(new Vector(16,10));
//            obstacle.add(new Vector(15,10));
//            obstacle.add(new Vector(14,10));
//            obstacle.add(new Vector(13,10));
//            Map m=new Map(obstacle);
//            
//        	PointState[][] states=new PointState[Map.MAX_X][Map.MAX_Y];
//        	for(int i=0;i<Map.MAX_X;i++)
//        		for(int j=0;j<Map.MAX_Y;j++)
//        			states[i][j]=m.getPointStateAt(new Vector(i,j));
//        	
//        	Descriptor.writeFileFromStates("test.txt", states);
//        }
}
