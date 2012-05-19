package roge.androidextended;

import android.util.Log;

public class Style{
	/*Begin Data*/
	private int background_color=0;
	private int background_color_active=0;
	private int background_color_ontouch=0;
	private Border border=null;
	private Padding padding=null;
	/*End Data*/
	
	/*Begin Setter Methods*/
	public void setBackgroundColor(int color){
		this.background_color=color;
	}
	
	public void setBackgroundColorActive(int color){
		this.background_color_active=color;
	}
	
	public void setBackgroundColorOntouch(int color){
		this.background_color_ontouch=color;
	}
	
	public void setBorder(Border border){
		this.border=border;
	}
	
	public void setPadding(Padding padding){
		this.padding=padding;
	}
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public int getBackgroundColor(){
		return this.background_color;
	}
	
	public int getBackgroundColorActive(){
		return this.background_color_active;
	}
	
	public int getBackgroundColorOntouch(){
		return this.background_color_ontouch;
	}
	
	public Border getBorder(){
		if(this.border==null){
			this.border=new Border();
		}
		
		return this.border;
	}
	
	public Padding getPadding(){
		if(this.padding==null){
			this.padding=new Padding();
		}
		
		return this.padding;
	}
	/*End Getter Methods*/
}
