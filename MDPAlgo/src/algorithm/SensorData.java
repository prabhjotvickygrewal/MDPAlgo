
package algorithm;

/**
 *
 * @author WGUO002
 */
public class SensorData {
    public int left_t;
    public int right_t;
    public int right_b;
    public int up_r;
    public int up_m;
    public int up_l;
    
    public SensorData(String data){
        processDataFromArduino(data);
    }
    
    public SensorData(){}
    
    public void processDataFromArduino(String data){
        int cur=0;
        int temp;
        String s="";
        data= (data.replaceAll("\n", "")).replaceAll("\r", "")+",";
        System.out.println(data);
        
        while(data.charAt(cur)!=','){           //right bottom sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(checkAlignmentRequired(temp))
        	Calibration.forceNextRightAlignment();
        right_b=getBlockDistance(temp);
        s="";
        System.out.print(right_b+"  ");
        
        while(data.charAt(cur)!=','){           //sensors in front
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(checkAlignmentRequired(temp))
        	Calibration.forceNextFrontAlignment();
        up_r=getBlockDistance(temp);
        s="";
        System.out.print(up_r+"  ");
        
        while(data.charAt(cur)!=','){
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(checkAlignmentRequired(temp))
        	Calibration.forceNextFrontAlignment();
        up_m=getBlockDistance(temp);
        s="";
        System.out.print(up_m+"  ");

        
        while(data.charAt(cur)!=','){
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(checkAlignmentRequired(temp))
        	Calibration.forceNextFrontAlignment();
        up_l=getBlockDistance(temp);
        s="";
        System.out.print(up_l+"  ");

        
        while(data.charAt(cur)!=','){                //right top sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(checkAlignmentRequired(temp))
        	Calibration.forceNextRightAlignment();
        right_t=getBlockDistance(temp);
        s="";
        System.out.print(right_t+"  ");

        
        while(data.charAt(cur)!=','){                //left sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(temp>0 && temp<9){
        	left_t=1;
        }
        else{
        	left_t=getBlockDistance(temp);
        }
        s="";
        System.out.print(left_t+"  \n");
        
        
    }
    public int getBlockDistance(int temp){
    	if(temp==-99)
    		return 0;
    	if(temp<0)
    		return 1;
        if(temp%10<5)
            return temp/10+1;
        else
            return temp/10+2;
    }
    public boolean checkAlignmentRequired(int temp){
    	if(temp==-99)
    		return false;
    	if(temp<(MapLayer.Sensor_ShortRange*10+5)) 
    		return temp%10>1 && temp%10<9;
    	
    	return false;
    }
}
