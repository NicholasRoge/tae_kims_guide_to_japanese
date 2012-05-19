package roge.androidextended;

import java.util.ArrayList;
import java.util.List;

import roge.taekim.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;

public class EText extends EView {
	private List<Integer> line_heights;
	private List<String> line_text;
	private List<Paint> span_styles;
	private String text;
	
	/*Begin Additional Style Attribute Variables*/
	Paint default_paint;
	private int font_color;
	private int font_size;
	/*Begin Additional Style Attribute Variables*/
	
	/*Begin Initializer Methods*/
	private void initialize(){
		
	}
	
	private void initialize(AttributeSet attributes){
		Paint paint=this.getDefaultPaint();
		TypedArray resources=getContext().obtainStyledAttributes(attributes,R.styleable.EText);
		
		this.setText(resources.getString(R.styleable.EText_text));
		paint.setColor(resources.getColor(R.styleable.EText_font_color,0xFF000000));
		paint.setTextSize(resources.getInt(R.styleable.EText_font_size,12));
		paint.setStyle(Paint.Style.FILL);
		
		resources.recycle();
		
		
		
		this.initialize();
	}
	/*End Initializer Methods*/
	
	/*Begin Constructor Methods*/
	public EText(Context context){
		super(context);

		this.initialize();
	}
	public EText(Context context,AttributeSet attributes){
		super(context,attributes);
		
		this.initialize(attributes);
	}
	/*End constructor Methods*/
	
	/*Begin Overridden Methods*/
	@Override public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		int distance_from_top=0;
		
		Rect drawable_area=null;
		int line_height=0;
		int line_number=0;
		int line_space_used=0;
		FontMetrics font_metrics=null;
		Rect font_rectangle=null;
		Paint paint=null;
		int space_size=0;
		String words[]=null;
		
		/*Initialize a few things*/
		font_rectangle=new Rect();
		words=text.split(" ");
		drawable_area=this.getDrawableArea();
		
		/*Prepare teh font decoration*/
		paint=this.getDefaultPaint();
		
		/*Set metrics for drawing the text*/
		font_metrics=paint.getFontMetrics();
		font_metrics.ascent=0-font_metrics.ascent;  //The reason for the "0-" bit there, is that metrics ascent will always be a negative number, so it's just a faster way to make it positive
		line_height=(int)(font_metrics.ascent+font_metrics.descent);
		
		space_size=(this.font_size/3);
		
		/*Done*/
		
		for(int index=0;index<this.line_text.size();index++){
			canvas.drawText(line_text.get(index),drawable_area.left,drawable_area.top+font_metrics.ascent+distance_from_top,paint);
			
			distance_from_top+=this.line_heights.get(index);
		}
		
		for(int i=0;i<words.length;i++){
			paint.getTextBounds(words[i],0,words[i].length(),font_rectangle);
			if(line_space_used+font_rectangle.width()+5>=drawable_area.width()){  //The +5 is padding because the getTextBounds rectangle is coming up a bit short.  TODO:  Fix that. 
				line_number++;
				
				line_space_used=0;
			}else{
				if(line_space_used>0){
					line_space_used+=space_size;
				}
			}
			
			
			
			line_space_used+=font_rectangle.width()+5;  //Now we've actually used the space, so add it in.  Again, the +5 is padding
		}
	}
	
	@Override public void onMeasure(int width_spec, int height_spec){
		super.onMeasure(width_spec,height_spec);
		
		this.parseText();
		
		Rect drawable_area=null;
		int text_height=0;
		int height=MeasureSpec.getSize(height_spec);
		int height_mode=MeasureSpec.getMode(height_spec);
		int width=MeasureSpec.getSize(width_spec);
		int width_mode=MeasureSpec.getMode(width_spec);
		
		drawable_area=this.getDrawableArea();
		
		for(int index=0;index<line_text.size();index++){
			text_height+=this.line_heights.get(index);
			
			if(text_height>drawable_area.height()&&height_mode!=MeasureSpec.UNSPECIFIED){
				//Remove all the rest of the elements in the array_list
					
				while(index!=line_text.size()){
					this.line_text.remove(index);
				}
			}
		}
		
		if(height_mode==MeasureSpec.UNSPECIFIED||height_mode==MeasureSpec.AT_MOST){
			Log.d("JP","TH:  "+text_height);
			
			height=text_height+this.getStyle().getPadding().getTop()+this.style.getPadding().getBottom()+this.getStyle().getBorder().getWidth(Border.Side.TOP)+this.getStyle().getBorder().getWidth(Border.Side.BOTTOM)+((int)this.getDefaultPaint().descent());//Adding the descent is a temporary fix.  TODO:  Find out what's actually causing the issue.;
		}
		
		super.onMeasure(MeasureSpec.makeMeasureSpec(width,width_mode),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
	}
	/*End Overridden Methods*/
	
	/*Begin Setter Methods*/
	public void setDefaultPaint(Paint paint){
		this.default_paint=paint;
	}
	
	public void setFontColor(int color){
		this.font_color=color;
	}
	
	public void setFontSize(int size){
		this.font_size=size;
	}
	
	public void setText(String text){
		this.text=text;
		
		this.invalidate();
	}
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public Paint getDefaultPaint(){
		if(this.default_paint==null){
			this.default_paint=new Paint();
		}
		
		return this.default_paint;
	}
	
	public int getFontColor(){
		return this.font_color;
	}
	
	public int getFontSize(){
		return this.font_size;
	}
	
	public String getText(){		
		return this.text;
	}
	/*End Getter Methods*/
	
	/*Begin Additional Essential Methods*/
	private void parseText(){
		Rect drawable_area=null;
		FontMetrics font_metrics=null;
		Rect font_rectangle=null;
		String line_text="";
		int line_height=0;
		int line_space_used=0;
		Paint paint=this.getDefaultPaint();
		String words[]=null;
		String newline_split[]=null;
		
		if(this.line_heights==null){
			this.line_heights=new ArrayList<Integer>();
		}else{
			this.line_heights.clear();
		}
		
		if(this.line_text==null){
			this.line_text=new ArrayList<String>();
		}else{
			this.line_text.clear();
		}
		
		if(this.span_styles==null){
			this.span_styles=new ArrayList<Paint>();
		}else{
			this.span_styles.clear();
		}
		
		/*Initialize a few things*/
		font_rectangle=new Rect();
		words=text.split(" ");
		drawable_area=this.getDrawableArea();
		
		/*Prepare teh font decoration*/
		paint=this.getDefaultPaint();
		
		/*Set metrics for drawing the text*/
		font_metrics=paint.getFontMetrics();
		font_metrics.ascent=0-font_metrics.ascent;  //The reason for the "0-" bit there, is that metrics ascent will always be a negative number, so it's just a faster way to make it positive
		line_height=(int)(font_metrics.ascent+font_metrics.descent);
		

		for(int i=0;i<words.length;i++){
			paint.getTextBounds(line_text+words[i],0,(line_text+" "+words[i]).length()-1,font_rectangle);
			Log.d("JP","LINE:  "+words[i]);
			if((font_rectangle.width()+5)>=drawable_area.width()){  //The +5 is padding because the getTextBounds rectangle is coming up a bit short.  TODO:  Fix that				
				this.line_text.add(line_text);
				this.line_heights.add(line_height);
				
				line_space_used=0;
				line_text="";
			}else{
				if(line_space_used>0){
					line_text+=" ";
				}
			}
			
			line_text+=words[i];
			line_space_used+=font_rectangle.width()+5;  //Now we've actually used the space, so add it in.  Again, the +5 is padding
		}
		
		this.line_text.add(line_text);  //There will always be a line that wasn't added
		this.line_heights.add(line_height);
	}
	
	private Paint parseSpan(String string){
		Paint paint=null;
		
		return paint;
	}
	/*End Additional Essential Methods*/
}
