package roge.androidextended;

import roge.taekim.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class EView extends View{
	Style style;

	/*Begin Initializer Methods*/
	private void initialize(){
		
	}
	
	private void initialize(AttributeSet attributes){
		Style eview_style=this.getStyle();
		TypedArray resources=getContext().obtainStyledAttributes(attributes,R.styleable.EView);
		
		eview_style.setBackgroundColor(resources.getColor(R.styleable.EView_background_color,0xFFFFFFFF));
		eview_style.setBackgroundColorOntouch(resources.getColor(R.styleable.EView_background_color_ontouch,EColor.darkenColor(eview_style.getBackgroundColor(),.1f)));
		
		try{
			eview_style.setBorder(Border.getBorderFromAttribute(resources.getString(R.styleable.EView_border)));
		}catch(Exception e){
			Tools.logException(e);
		}
		
		eview_style.setPadding(Padding.getFromAttribute(resources.getString(R.styleable.EView_padding)));
		
		resources.recycle();  //Clean up time!
		
		/*Now let's let Android know what we've done.*/
		super.setBackgroundColor(eview_style.getBackgroundColor());
		
		initialize();
	}
	/*End Initializer Methods*/
	
	/*Begin Constructor Methods*/
	public EView(Context context){
		super(context);
		
		this.initialize();
	}
	public EView(Context context,AttributeSet attributes){
		super(context,attributes);
		
		this.initialize(attributes);
	}
	/*End Constructors*/
	
	/*Begin Overridden Methods*/
	@Override public void onDraw(Canvas canvas){
		Style eview_style=this.getStyle();
		
		Border border=eview_style.getBorder();
		Padding padding=eview_style.getPadding();
		
		Border.drawBorder(this,canvas,border);
		
		super.setPadding(border.getWidth(Border.Side.LEFT)+padding.getLeft(),border.getWidth(Border.Side.TOP)+padding.getTop(),border.getWidth(Border.Side.RIGHT)+padding.getRight(),border.getWidth(Border.Side.BOTTOM)+padding.getBottom());
		
		super.onDraw(canvas);
	}
	
	@Override public void setBackgroundColor(int color){
		Style eview_style=this.getStyle();
		
		eview_style.setBackgroundColor(color);
		
		if(eview_style.getBackgroundColorOntouch()==0){  //If the ontouch version of the color hasn't been set yet, we'll go ahead and set it here.
			eview_style.setBackgroundColorOntouch(EColor.darkenColor(color,.1f));
		}
	}
	/*End Overridden Methods*/
	
	/*Begin Setter Methods*/
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public Rect getDrawableArea(){
		Style eview_style=this.getStyle();
		
		Border border=eview_style.getBorder();
		Rect drawable_area=new Rect();
		Padding padding=eview_style.getPadding();
		
		drawable_area.left=border.getWidth(Border.Side.LEFT)+padding.getLeft();
		drawable_area.top=border.getWidth(Border.Side.TOP)+padding.getTop();
		drawable_area.right=this.getMeasuredWidth()-(border.getWidth(Border.Side.RIGHT)+padding.getRight());
		drawable_area.bottom=this.getMeasuredHeight()-(border.getWidth(Border.Side.BOTTOM)+padding.getBottom());
		
		return drawable_area;
	}
	
	public Style getStyle(){
		if(this.style==null){
			this.style=new Style();
		}
		
		return this.style;
	}
	/*End Getter Methods*/
}
