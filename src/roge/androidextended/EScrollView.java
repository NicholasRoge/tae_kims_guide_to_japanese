package roge.androidextended;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.ScrollView;

public class EScrollView extends ScrollView {
	private int width_offset=0;
	private int height_offset=0;
	
	public EScrollView(Context context) {
		super(context);
	}

	public EScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override public void onMeasure(int width_spec,int height_spec){
		super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(width_spec)+this.width_offset,MeasureSpec.getMode(width_spec)),MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(height_spec)+this.height_offset,MeasureSpec.getMode(height_spec)));
	}
	
	public void setWidthOffset(int offset){
		this.width_offset=offset;
	}
	
	public void setHeightOffset(int offset){
		this.height_offset=offset;
	}
}
