package roge.androidextended;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;

public class SwipeDetector extends GestureDetector{
		public static class SwipeChecker extends SimpleOnGestureListener{
			public enum Direction{
				NONE,
				LEFT,
				UP,
				RIGHT,
				DOWN
			};
			
			public enum SwipeType{
				HORIZONTAL,
				VERTICAL
			};
			
			private Direction event;
			private int maximum_off_path;  //In percent of screen traveled
			private int minimum_distance;  //In percent of screen traveled
	        private float minimum_velocity;  //Percent per second
	        private SwipeType type;
	        
	        public SwipeChecker(Context context,int minimum_distance,int maximum_off_path,float minimum_velocity,SwipeType type){
	        	this.minimum_distance=minimum_distance*(((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/100);
	        	this.maximum_off_path=maximum_off_path*(((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight()/100);
	        	this.minimum_velocity=minimum_velocity;
	        	this.type=type;
	        }

			@Override public boolean onFling(MotionEvent first_event,MotionEvent last_event,float velocity_x,float velocity_y){
				switch(this.type){
					case HORIZONTAL: 
			        	if(Math.abs(first_event.getY()-last_event.getY())>this.maximum_off_path){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	if(Math.abs(velocity_x)<this.minimum_velocity){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	if(Math.abs(first_event.getX()-last_event.getX())<this.minimum_distance){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	
		
			        	//And now that you have passed all those tests, you may return true.
			        	if(last_event.getX()>first_event.getX()){
			        		this.event=SwipeDetector.SwipeChecker.Direction.RIGHT;
			        	}else{
			        		this.event=SwipeDetector.SwipeChecker.Direction.LEFT;
			        	}
			        	
			        	break;
		        	case VERTICAL:
		        		if(Math.abs(first_event.getX()-last_event.getX())>this.maximum_off_path){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	if(Math.abs(velocity_y)<this.minimum_velocity){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	if(Math.abs(first_event.getY()-last_event.getY())<this.minimum_distance){
			        		this.event=SwipeDetector.SwipeChecker.Direction.NONE;
			        		
			        		return false;
			        	}
			        	
		
			        	//And now that you have passed all those tests, you may return true.
			        	if(last_event.getY()>first_event.getY()){
			        		this.event=SwipeDetector.SwipeChecker.Direction.UP;
			        	}else{
			        		this.event=SwipeDetector.SwipeChecker.Direction.DOWN;
			        	}
		        		
		        		break;
				}
	        	return true;
	        }
			
			public Direction getEvent(){
				return this.event;
			}
		}
        
        SwipeChecker listener;
        
        public SwipeDetector(Context context,SwipeChecker checker){
        	super(context,checker);
        
        	this.listener=checker;
        }        
        
        public SwipeDetector.SwipeChecker.Direction getEvent(){
        	return this.listener.getEvent();
        }
    }