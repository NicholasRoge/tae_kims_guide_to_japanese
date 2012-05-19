package roge.androidextended;

import roge.taekim.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ERelativeLayout extends RelativeLayout {
	private Border border;
	private int true_padding_left;
	private int true_padding_top;
	private int true_padding_right;
	private int true_padding_bottom;
	
	private void initialize(){
		
	}
	private void initialize(AttributeSet attributes){
		TypedArray resources=getContext().obtainStyledAttributes(attributes,R.styleable.Input);
		
		try{
			border=Border.getBorderFromAttribute(resources.getString(R.styleable.Input_border));
		}catch(Exception e){
			Tools.logException(e);
		}
		
		resources.recycle();
		
		this.initialize();
	}

	public ERelativeLayout(Context context) {
		super(context);
	}

	public ERelativeLayout(Context context,AttributeSet attributes){
		super(context, attributes);

		this.initialize(attributes);
	}
	
	@Override public void onDraw(Canvas canvas){		
		Border.drawBorder(this,canvas,border);
		if(this.border!=null){
			super.setPadding(border.getWidth(Border.Side.LEFT)+this.true_padding_left,border.getWidth(Border.Side.TOP)+this.true_padding_top,border.getWidth(Border.Side.RIGHT)+this.true_padding_right,border.getWidth(Border.Side.BOTTOM)+this.true_padding_bottom);
		}
		
		super.onDraw(canvas);
	}
	
	@Override public void setPadding(int left,int top,int right,int bottom){
		this.true_padding_left=left;
		this.true_padding_top=top;
		this.true_padding_right=right;
		this.true_padding_bottom=bottom;
		
		super.setPadding(left,top,right,bottom);
	}
	
	public void setBorder(Border border){
		this.border=border;
	}
	
	public Border getBorder(){
		return this.border;
	}
}
