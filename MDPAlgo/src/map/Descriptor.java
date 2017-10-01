package map;

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
		
		for(int i = 0; i < hex2.length(); i++){           //convert char one by one
	        binFragment = Integer.toBinaryString(Integer.parseInt(""+hex1.charAt(i),16));
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
		
		return hex1;
	}
	public static String getSecondStringFromStates(PointState[][] states){
		String binary2=new String();
		String hex2=new String();
		
		for(int i=0;i<Map.MAX_X;i++)
			for(int j=0;j<Map.MAX_Y;j++){
				if(states[i][j]==PointState.IsFree)
					binary2 +="0";
				else if(states[i][j]==PointState.Obstacle)
					binary2 += "1";
			}
		int cur=0;
		for(int i=0;i<binary2.length()/4+1;i++){
			String binFragment="";
			while(binFragment.length()<4){
				if(cur<binary2.length())
					binFragment = binFragment+binary2.charAt(cur);
				else
					binFragment = binFragment+"0";
				cur++;
			}
			hex2=hex2+Integer.toString(Integer.parseInt(binFragment, 2), 16);
		}
		
		return hex2;
	}

	
}
