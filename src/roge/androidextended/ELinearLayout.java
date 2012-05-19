package roge.androidextended;

import roge.taekim.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ELinearLayout extends LinearLayout{
	private Style style;
	
	/*Begin Initializer Methods*/
	private void initialize(){
		
	}
	
	private void initialize(AttributeSet attributes){
		//TypedArray resources=getContext().obtainStyledAttributes(attributes,R.styleable.Heading);
		
		//this.getStyle().background_color=resources.getColor(R.styleable.ELinearLayout_background_color,0xFFFFFFFF);
		
	//resources.recycle();
		
		
		/*Let android know what we've done.*/
		this.setBackgroundColor(this.getStyle().getBackgroundColor());
		
		this.initialize();
	}
	/*End Initializer Methods*/
	
	/*Begin Constructors*/
	public ELinearLayout(Context context) {
		super(context);
	}

	public ELinearLayout(Context context, AttributeSet attributes) {
		super(context,attributes);
		
		this.initialize(attributes);
	}
	/*End Constructors*/
	
	/*Begin Setter Methods*/
	/*End Setter Methods*/
	
	/*Begin Getter Methods*/
	public Style getStyle(){
		if(this.style==null){
			this.style=new Style();
		}
		
		return this.style;
	}
	/*End Getter Methods*/
}
