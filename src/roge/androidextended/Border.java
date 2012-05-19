package roge.androidextended;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Border is meant only to be used as a container class to hold information about a View's border.
 * 
 * @author Nicholas Rogé
 * @version 1.0
 */
public class Border{
	private int _color_bottom=0;
	private int _color_left=0;
	private int _color_right=0;
	private int _color_top=0;
	private int _width_bottom=0;
	private int _width_left=0;
	private int _width_right=0;
	private int _width_top=0;
	
	public static enum Side{
		BOTTOM,
		LEFT,
		RIGHT,
		TOP
	}
	
	/**
	 * Takes all no parameters and sets all of the specifications (stroke size and color) to zero.  
	 */
	public Border(){
		this.setWidth(0,0,0,0);
		this.setColor(0,0,0,0);
	}
	
	/**
	 * Takes two parameters, width and color, and sets all four sides of the border to these arguments.
	 * 
	 * @param width Width to set the Border's stroke to.
	 * @param color Color to set the Border's color to.
	 */
	public Border(int width,int color){
		this.setWidth(width,width,width,width);
		this.setColor(color,color,color,color);
	}
	
	/**
	 * Sets the color and width of each sides of the Border independently.
	 * 
	 * @param width_left   Stroke width for the left side of the Border.
	 * @param width_top    Stroke width for the top side of the Border.
	 * @param width_right  Stroke width for the right side of the Border.
	 * @param width_bottom Stroke width for the bottom side of the Border.
	 * @param color_left   Stroke color for the left side of the Border.
	 * @param color_top    Stroke color for the top side of the Border.
	 * @param color_right  Stroke color for the right side of the Border.
	 * @param color_bottom Stroke color for the bottom side of the Border.
	 */
	public Border(int width_left,int width_top,int width_right,int width_bottom,int color_left,int color_top,int color_right,int color_bottom){
		this.setWidth(width_left,width_top,width_right,width_bottom);
		this.setColor(color_left,color_top,color_right,color_bottom);
	}
	
	/**
	 * Takes a string which is in the form of "[{border width}], [{border width}:{border color}], or [{border width left}:{border color left},{border width top}:{border color top},{border width right}:{border color right},{border width bottom}:{border color bottom}]", and returns an Border object with the specifications given. 
	 * 
	 * @param border_string String containing the specifications for the Border.  Typically, this is the border that was received from the <code>AttributeSet</code> parameter of the View's constructor.
	 *  
	 * @return Returns an allocation object of type Border which has the specifications given in border_string.  Will return <code>null</code> if the border string is <code>null</code> or is an empty string.
	 * 
	 * @throws Exception If the border_string given is incorrectly formatted.
	 */
	public static Border getBorderFromAttribute(String border_string) throws Exception{
		Border border=null;
		int    border_width[]=null;
		int    border_color[]=null;
		String split_string[]=null;
		String split_string_sub[]=null;
		
		
		/*-------------------------*\
		|     Begin Method Code     |
		\*-------------------------*/
		if(border_string==null){
			return null;
		}else if(border_string.equals("")){
			return null;
		}

		
		if(border_string.contains(",")){			
			split_string=border_string.split(",");
			if(split_string.length!=4){
				throw new Exception("Malformed border string given.  String given was \""+border_string+"\".");
			}
			
			border_width=new int[4];
			border_color=new int[4];
			
			for(int i=0;i<4;i++){
				if(split_string[i].contains(":")){
					split_string_sub=split_string[i].split(":");
					if(split_string_sub.length>2){
						throw new Exception("Malformed border string given.  String given was \""+border_string+"\".");
					}				
					
					border_width[i]=Integer.parseInt(split_string_sub[0].trim());
					border_color[i]=(int)Long.parseLong(split_string_sub[1].trim().replace("#",""),16);
				}else{
					border_width[i]=Integer.parseInt(split_string[i].trim());
					border_color[i]=0xFF000000;
				}	
			}
			
			border=new Border(
				border_width[0],
				border_width[1],
				border_width[2],
				border_width[3],
				border_color[0],
				border_color[1],
				border_color[2],
				border_color[3]
			);
		}else{
			if(border_string.contains(":")){
				split_string=border_string.split(":");
				if(split_string.length>2){
					throw new Exception("Malformed border string given.  String given was \""+border_string+"\".");
				}				
				
				border=new Border(
					Integer.parseInt(split_string[0].trim()),
					Integer.parseInt(split_string[0].trim()),
					Integer.parseInt(split_string[0].trim()),
					Integer.parseInt(split_string[0].trim()),
					(int)Long.parseLong(split_string[1].replace("#",""),16),
					(int)Long.parseLong(split_string[1].replace("#",""),16),
					(int)Long.parseLong(split_string[1].replace("#",""),16),
					(int)Long.parseLong(split_string[1].replace("#",""),16)
				);
			}else{				
				border=new Border(
					Integer.parseInt(border_string.trim()),
					Integer.parseInt(border_string.trim()),
					Integer.parseInt(border_string.trim()),
					Integer.parseInt(border_string.trim()),
					0xFF000000,
					0xFF000000,
					0xFF000000,
					0xFF000000
				);
			}
		}
		
		return border;
	}
	
	/**
	 * Draw a Border onto a given <code>Canvas</code>.  If any of the parameters given are null, the method will immediately return.
	 * 
	 * @param view   This should be the view that the <code>Border</code> is to be drawn onto.  In most cases, passing <code>this</code> to this method within the View's <code>onDraw(Canvas canvas)</code> call is sufficient.
	 * @param canvas This should be the <code>Canvas</code> that the border is to be drawn onto.  Usually this is the <code>Canvas</code> that was passed into the View's <code>onDraw(Canvas canvas)</code> method.
	 * @param border The <code>Border</code> to be drawn.
	 */
	public static void drawBorder(View view,Canvas canvas,Border border){
		Rect  border_bottom;
		Rect  border_left;
		Rect  border_right;
		Rect  border_top;
		Paint paint_bottom;
		Paint paint_left;
		Paint paint_right;
		Paint paint_top;
		
		
		/*-------------------------*\
		|     Begin Method Code     |
		\*-------------------------*/
		if(view==null||canvas==null||border==null){
			return;
		}
		
		/*Size the rectangles to the correct size for the border*/
		border_bottom=new Rect();
		view.getLocalVisibleRect(border_bottom);
		border_bottom.top=border_bottom.bottom-border.getWidth(Border.Side.BOTTOM);
		
		border_left=new Rect();
		view.getLocalVisibleRect(border_left);
		border_left.right=border_left.left+border.getWidth(Border.Side.LEFT);
		
		border_right=new Rect();
		view.getLocalVisibleRect(border_right);
		border_right.left=border_right.right-border.getWidth(Border.Side.RIGHT);
		
		border_top=new Rect();
		view.getLocalVisibleRect(border_top);
		border_top.bottom=border_top.top+border.getWidth(Border.Side.TOP);
		
		/*Create the paints for the borders*/
		paint_bottom=new Paint();
		paint_bottom.setStyle(Paint.Style.FILL);
		paint_bottom.setColor(border.getColor(Border.Side.BOTTOM));
		
		paint_left=new Paint();
		paint_left.setStyle(Paint.Style.FILL);
		paint_left.setColor(border.getColor(Border.Side.LEFT));
		
		paint_right=new Paint();
		paint_right.setStyle(Paint.Style.FILL);
		paint_right.setColor(border.getColor(Border.Side.RIGHT));
		
		paint_top=new Paint();
		paint_top.setStyle(Paint.Style.FILL);
		paint_top.setColor(border.getColor(Border.Side.TOP));
		

		/*Put the borders on teh canvas*/
		canvas.drawRect(border_left,paint_left);
		canvas.drawRect(border_right,paint_right);
		canvas.drawRect(border_top,paint_top);
		canvas.drawRect(border_bottom,paint_bottom);
	}
	
	/*
	TODO_LOW:  Make the EView method
	
	public static void drawBorder(EView view,Canvas canvas,Border border){
		Rect border_left;
		Rect border_top;
		Rect border_right;
		Rect border_bottom;
		
		Paint paint_left;
		Paint paint_top;
		Paint paint_right;
		Paint paint_bottom;
		
		int location[]=new int[2];
		
		if(view==null||canvas==null||border==null){
			return;
		}
		

		view.getLocationInWindow(location);
		location[1]=Math.abs(location[1]-25);
		
		if(border.getWidth(Border.Side.LEFT)-location[0]>0){
			border_left=new Rect();
			view.getLocalVisibleRect(border_left);
			border_left.right=border_left.left+border.getWidth(Border.Side.LEFT);
			
			paint_left=new Paint();
			paint_left.setStyle(Paint.Style.FILL);
			paint_left.setColor(border.getColor(Border.Side.LEFT));
			
			canvas.drawRect(border_left,paint_left);
		}
		
		location[1]+=((WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()-view.getMeasuredWidth() ;
		if(border.getWidth(Border.Side.RIGHT)+location[0]>0){
			border_right=new Rect();
			view.getLocalVisibleRect(border_right);
			border_right.left=border_right.right-border.getWidth(Border.Side.RIGHT);
		
			paint_right=new Paint();
			paint_right.setStyle(Paint.Style.FILL);
			paint_right.setColor(border.getColor(Border.Side.RIGHT));
					
			canvas.drawRect(border_right,paint_right);
		}
		
		if(border.getWidth(Border.Side.TOP)-location[1]>0){
			border_top=new Rect();
			view.getLocalVisibleRect(border_top);
			border_top.bottom=border_top.top+border.getWidth(Border.Side.TOP)+location[1];
			
			paint_top=new Paint();
			paint_top.setStyle(Paint.Style.FILL);
			paint_top.setColor(border.getColor(Border.Side.TOP));
			
			canvas.drawRect(border_top,paint_top);
		}
		
		location[1]+=((WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()-view.getMeasuredHeight()-25;
		if(border.getWidth(Border.Side.BOTTOM)+location[1]>0){
			border_bottom=new Rect();
			view.getLocalVisibleRect(border_bottom);
			border_bottom.top=border_bottom.bottom-border.getWidth(Border.Side.BOTTOM)-location[1];
			
			paint_bottom=new Paint();
			paint_bottom.setStyle(Paint.Style.FILL);
			paint_bottom.setColor(border.getColor(Border.Side.BOTTOM));
			
			canvas.drawRect(border_bottom,paint_bottom);
		}
	}*/
	
	/*Start Setter Methods*/
	
	/**
	 * Sets the width of the given sides to the given sizes.
	 * 
	 * @param left   Size of the left side of the Border.
	 * @param top    Size of the top side of the Border.
	 * @param right  Size of the right side of the Border.
	 * @param bottom Size of the bottom side of the Border.
	 */
	public void setWidth(int left,int top,int right,int bottom){
		this._width_bottom=bottom;
		this._width_left=left;
		this._width_right=right;
		this._width_top=top;
	}
	
	/**
	 * Sets the width of the side of the Border specified by <code>side</code> to the width specified by <code>width</code>.
	 * 
	 * @param side  Side of the Border to affect.
	 * @param width Size the side of the Border should be set to.
	 */
	public void setWidth(Border.Side side,int width){
		switch(side){
			case BOTTOM:
				this._width_bottom=width;
				break;
			case LEFT:
				this._width_left=width;
				break;
			case RIGHT:
				this._width_right=width;
				break;
			case TOP:
				this._width_top=width;
				break;
		}
	}
	
	/**
	 * Sets the color of the given sides to the given colors.
	 * 
	 * @param left   Color of the left side of the Border.
	 * @param top    Color of the top side of the Border.
	 * @param right  Color of the right side of the Border.
	 * @param bottom Color of the bottom side of the Border.
	 */
	public void setColor(int left,int top,int right,int bottom){
	    this._color_bottom=bottom;
	    this._color_left=left;
		this._color_right=right;
		this._color_top=top;
	}
	
	/**
     * Sets the color of the side of the Border specified by <code>side</code> to the color specified by <code>color</code>.
     * 
     * @param side  Side of the Border to affect.
     * @param color Color the side of the Border should be set to.
     */
	public void setColor(Border.Side side,int color){
		switch(side){
		    case BOTTOM:
                this._color_bottom=color;
                break;
			case LEFT:
				this._color_left=color;
				break;
			case RIGHT:
				this._color_right=color;
				break;
			case TOP:
                this._color_top=color;
                break;
		}
	}
	/*End Setter Methods*/
	
	/*Start Getter Methods*/
	
	/**
	 * Gets the width of the side of the Border specified by <code>side</code>.
	 * 
	 * @param side Side of the border to get the width from. 
	 * 
	 * @return Returns the stroke width for the specified side.
	 */
	public int getWidth(Border.Side side){
		switch(side){
		    case BOTTOM:
                return this._width_bottom;
			case LEFT:
				return this._width_left;
			case RIGHT:
                return this._width_right;
			case TOP:
				return this._width_top;
		}
		
		return -1;  //It will never get to this point, but I had to add it otherwise the compiler complains.  :P
	}
	
	/**
     * Gets the color of the side of the Border specified by <code>side</code>.
     * 
     * @param side Side of the border to get the color from. 
     * 
     * @return Returns the color for the specified side.
     */
	public int getColor(Border.Side side){
		switch(side){
		    case BOTTOM:
                return this._color_bottom;
			case LEFT:
				return this._color_left;
			case RIGHT:
                return this._color_right;
			case TOP:
				return this._color_top;
		}
		
		return -1;  //It will never get to this point, but I had to add it otherwise the compiler complains.  :P
	}
	/*End Getter Methods*/
}