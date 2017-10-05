
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
        
        while(data.charAt(cur)!=','){           //right bottom sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        right_b=getBlockDistance(temp);
        s="";
        System.out.println(right_b);
        
        while(data.charAt(cur)!=','){           //sensors in front
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        up_r=getBlockDistance(temp);
        s="";
        System.out.println(up_r);
        
        while(data.charAt(cur)!=','){
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        up_m=getBlockDistance(temp);
        s="";
        
        while(data.charAt(cur)!=','){
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        up_l=getBlockDistance(temp);
        s="";
        
        while(data.charAt(cur)!=','){                //right top sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        right_t=getBlockDistance(temp);
        s="";
        
        while(data.charAt(cur)!=','){                //left sensor
            s=s+data.charAt(cur);
            cur++;
        }
        cur++;
        temp=Integer.parseInt(s);
        if(temp>0 && temp<9)
        	left_t=1;
        else
        	left_t=getBlockDistance(temp);
        s="";
        System.out.println(left_t);
        
        
    }
    public int getBlockDistance(int temp){
    	if(temp==0)
    		return 0;
        if(temp%5<5)
            return temp/10+1;
        else
            return temp/10+2;
    }
}
