package roge.androidextended;

import roge.taekim.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import roge.androidextended.EColor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class ETextView extends TextView{
	private Style style;
	
	private int height_offset=0;
	private int width_offset=0;
	
	private void initialize(){
		this.setId(Tools.getFreshId());
	}
	private void initialize(AttributeSet attributes){
		TypedArray resources=getContext().obtainStyledAttributes(attributes,R.styleable.ETextView);
		
		this.setStyle(resources);
		
		this.setText(resources.getString(R.styleable.ETextView_text));
		this.setTextSize(resources.getFloat(R.styleable.ETextView_font_size,12));
		
		resources.recycle();  //Clean up time!
		
		initialize();
	}
	
	/*Begin Constructors*/
	public ETextView(Context context){
		super(context);
		
		this.initialize();
	}
	public ETextView(Context context,AttributeSet attributes){		
		super(context,attributes);

		try{
			this.initialize(attributes);
		}catch (Exception e) {
			Log.e("JP","Error encountered:\n  "+e.toString());
			Log.d("JP","Stacktrace:\n");
			for(int counter=0;counter<e.getStackTrace().length; counter++){
				Log.d("JP","  "+ e.getStackTrace()[counter].toString());
			}
		}
	}
	public ETextView(Context context,AttributeSet attributes,int style_resource){
		super(context,attributes,style_resource);
	}
	/*End Constructors*/
	
	/*Begin Overridden Methods*/
	@Override public void onDraw(Canvas canvas){
		Border border=this.getStyle().getBorder();
		Padding padding=this.getStyle().getPadding();
		
		Border.drawBorder(this,canvas,border);
		
		super.setPadding(border.getWidth(Border.Side.LEFT)+padding.getLeft(),border.getWidth(Border.Side.TOP)+padding.getTop(),border.getWidth(Border.Side.RIGHT)+padding.getRight(),border.getWidth(Border.Side.BOTTOM)+padding.getBottom());
		
		super.onDraw(canvas);
	}
	
	@Override public void onMeasure(int width_spec,int height_spec){
		super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(width_spec)+this.width_offset,MeasureSpec.getMode(width_spec)),MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(height_spec)+this.height_offset,MeasureSpec.getMode(height_spec)));
	}
	
	@Override public void setPadding(int left,int top,int right,int bottom){		
		this.getStyle().setPadding(new Padding(left,top,right,bottom));
		
		super.setPadding(left,top,right,bottom);
	}
	/*End Overridden Methods*/
	
	/*Begin Setter Methods*/	
	public void setHeightOffset(int offset){
		this.height_offset=offset;
	}
	
	public void setStyle(TypedArray resources){
		if(resources==null){
			return;
		}
	
		Style stylesheet=this.getStyle();
		int font_style=0;
		int resource_font_style=0;
		String tmp_string=null;

		stylesheet.setBackgroundColor(resources.getColor(R.styleable.ETextView_background_color,0x0));
		stylesheet.setBackgroundColorActive(resources.getColor(R.styleable.ETextView_background_color_active,EColor.adjustSaturation(stylesheet.getBackgroundColor(),.1f)));
		stylesheet.setBackgroundColorOntouch(resources.getColor(R.styleable.ETextView_background_color_ontouch,(stylesheet.getBackgroundColorOntouch()==0?EColor.darkenColor(stylesheet.getBackgroundColor(),.1f):stylesheet.getBackgroundColorOntouch())));
		this.setBackgroundColor(stylesheet.getBackgroundColor());
		
		tmp_string=resources.getString(R.styleable.ETextView_border);
		if(tmp_string!=null){
			try{
				stylesheet.setBorder(Border.getBorderFromAttribute(tmp_string));
			}catch(Exception e){
				Tools.logException(e);
			}
		}
		
		tmp_string=resources.getString(R.styleable.ETextView_padding);
		if(tmp_string!=null){
			stylesheet.setPadding(Padding.getFromAttribute(tmp_string));
		}
		
		this.setTextColor(resources.getColor(R.styleable.ETextView_font_color,this.getCurrentTextColor()));
		
		this.setTextSize(resources.getFloat(R.styleable.ETextView_font_size,this.getTextSize()));
		
		resource_font_style=resources.getInt(R.styleable.ETextView_font_style,-1);
		if(resource_font_style!=-1){
			if((resource_font_style&0x1)==0x1){//0001 is the flag for normal style
				font_style|=Typeface.NORMAL;
			}
			if((resource_font_style&0x2)==0x2){//0010 is the flag for bold style
				font_style|=Typeface.BOLD;
			}
			if((resource_font_style&0x4)==0x4){//0100 is teh flagg for italics
				font_style|=Typeface.ITALIC;
			}
			
			this.setTypeface(null,font_style);
		}
		
		switch(resources.getInt(R.styleable.ETextView_text_align,-1)){
			case 0:
				this.setGravity(Gravity.LEFT);
				break;
			case 1:
				this.setGravity(Gravity.CENTER);
				break;
			case 2:
				this.setGravity(Gravity.RIGHT);
				break;
		}
		/*<item name="font_size">60</item>
        <item name="android:textStyle">bold</item>
        <item name="background_color">#FFCCCCCC</item>
        <item name="border">0,0,0,2:#FF999999</item>
        <item name="android:padding">3dp</item>*/
	}
	
	public void setWidthOffset(int offset){
		this.width_offset=offset;
	}
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public int getBackgroundColor(){
		return this.getStyle().getBackgroundColor();
	}
	
	public Style getStyle(){
		if(this.style==null){
			this.style=new Style();
		}
		
		return this.style;
	}
	/*End Getter Methods*/
}
