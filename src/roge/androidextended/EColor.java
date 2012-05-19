package roge.androidextended;

import android.graphics.Color;

public class EColor extends Color{
	public class RGBA{
		public int red;
		public int green;
		public int blue;
		public int alpha;
	}
	
	public class CMYK{
		public int cyan;
		public int magenta;
		public int yellow;
		public int black;
	}
	
	public class HSV{
		public int hue;
		public int saturation;
		public int value;  //contrast
		
		public HSV(int hue,int saturation,int value){
			this.hue=hue;
			this.saturation=saturation;
			this.value=value;
		}
	}
	
	public HSV rgbToHsv(RGBA rgb_triplet){
		HSV hsv_triplet=new HSV(0,0,0);
		
		return hsv_triplet;
	}
	
	public static int darkenColor(int base_color,float points_to_darken){
		float hsv_triplet[]=new float[3];
		
		Color.colorToHSV(base_color,hsv_triplet);
		
		hsv_triplet[2]-=points_to_darken;
		
		return Color.HSVToColor(base_color>>24,hsv_triplet);
	}
	
	public static int lightenColor(int base_color,int percent_to_lighten){
		int darkened_color=0;
		
		int alpha=base_color&0xFF000000;
		float red=((float)((base_color&0x00FF0000)>>16))/255;
		float green=((float)((base_color&0x0000FF00)>>8))/255;
		float blue=((float)(base_color&0x000000FF))/255;
		
		float cyan=0;
		float magenta=0;
		float yellow=0;
		float black=0;
		
		float max_color=0;
		
		//Convert the input RGB color to CMYK
		max_color=Math.max(red,Math.max(green,blue)); //We have to get this number into an x/1 number.
		
		black=1-max_color;
		cyan=(max_color-red)/max_color;
		magenta=(max_color-green)/max_color;
		yellow=(max_color-yellow)/max_color;
		
		//Increase black to the proper percentage
		black=Math.max(black-(((float)percent_to_lighten)/100),0);
		
		//Convert back to RGB
		red=(1-(cyan*(1-black))-black)*255;
		green=(1-(magenta*(1-black))-black)*255;
		blue=(1-(yellow*(1-black))-black)*255;

		darkened_color=alpha+((int)red<<16)+((int)green<<8)+(int)blue;
		
		return darkened_color;
	}
	
	public static int fromRGBA(int red,int green,int blue,int alpha){
		if(
				(red>0xFF||red<0)
				||
				(green>0xFF||green<0)
				||
				(blue>0xFF||blue<0)
				||
				(alpha>0xFF||alpha<0)
		){
			return 0x00000000;  //could have just used "return 0;" too, but I like this one more.  :3  
		}
		
		return ((alpha<<24)+(red<<16)+(green<<8)+blue);
	}

	public static int adjustSaturation(int base_color,float adjustment){
		float hsv_triplet[];
		
		if(adjustment==0){
			return base_color;
		}
		
		hsv_triplet=new float[3];
		Color.colorToHSV(base_color,hsv_triplet);
		
		if(adjustment>0){
			hsv_triplet[1]=Math.min(hsv_triplet[1]+adjustment,1);
		}else{
			hsv_triplet[1]=Math.max(hsv_triplet[1]+adjustment,0);
		}
		
		return Color.HSVToColor(base_color>>24,hsv_triplet);
	}
}