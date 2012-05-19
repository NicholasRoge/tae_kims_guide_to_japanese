package roge.androidextended;

public class Padding{
	private int left=0;
	private int top=0;
	private int right=0;
	private int bottom=0;
	
	/*Begin Constructors*/
	public Padding(){
	}
	
	public Padding(int padding_width){
		this.left=padding_width;
		this.top=padding_width;
		this.right=padding_width;
		this.bottom=padding_width;
	}
	
	public Padding(int left,int top,int right,int bottom){
		this.left=left;
		this.top=top;
		this.bottom=bottom;
		this.right=right;
	}
	
	public Padding(Padding padding){
	    this.left=padding.getLeft();
	    this.top=padding.getTop();
	    this.bottom=padding.getBottom();
	    this.right=padding.getRight();
	}
	/*End Constructors*/
	
	/*Begin Setter Methods*/
	public void setBottom(int size){
		this.bottom=size;
	}
	
	public void setLeft(int size){
		this.left=size;
	}
	
	public void setRight(int size){
		this.right=size;
	}
	
	public void setTop(int size){
		this.top=size;
	}
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public int getBottom(){
		return this.bottom;
	}
	
	public int getLeft(){
		return this.left;
	}
	
	public int getRight(){
		return this.right;
	}
	
	public int getTop(){
		return this.top;
	}
	/*End Getter Methods*/
	
	/*Begin Static Methods*/
	public static Padding getFromAttribute(String padding_attribute){
		Padding ret_padding=new Padding();
		String padding_attribute_split[]=null;
		
		if(padding_attribute==null||padding_attribute.equals("")){
			return ret_padding;
		}
		
		if(padding_attribute.contains(",")){
			padding_attribute_split=padding_attribute.split(",");
			
			ret_padding.left=Integer.parseInt(padding_attribute_split[0].trim());
			ret_padding.top=Integer.parseInt(padding_attribute_split[1].trim());
			ret_padding.right=Integer.parseInt(padding_attribute_split[2].trim());
			ret_padding.bottom=Integer.parseInt(padding_attribute_split[3].trim());
		}else{
			ret_padding.left=Integer.parseInt(padding_attribute.trim());
			ret_padding.top=Integer.parseInt(padding_attribute.trim());
			ret_padding.right=Integer.parseInt(padding_attribute.trim());
			ret_padding.bottom=Integer.parseInt(padding_attribute.trim());
		}
		
		return ret_padding;
	}
	/*End Static Methods*/
}

