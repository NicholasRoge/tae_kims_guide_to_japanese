package roge.androidextended;

import android.view.MotionEvent;
import android.view.View;

public class OnTouchArgs {
	public View view;
	public MotionEvent event;
	
	public OnTouchArgs(View view,MotionEvent event){
		this.view=view;
		this.event=event;
	}
}
