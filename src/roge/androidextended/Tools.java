package roge.androidextended;

import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.Display;

public class Tools{
	public static int getFreshId(){
		int id=(int)System.currentTimeMillis();
		
		try{
			Thread.sleep(1);
		}catch(Exception e){
			Tools.logException(e);
		}
		
		return id;
	}
	
	public static void logException(Exception e){
		Log.e("JP","Error encountered:\n  " + e.toString());
		Log.d("JP", "Stacktrace:\n");
		for (int counter=0;counter<e.getStackTrace().length;counter++){
			Log.d("JP","  "+e.getStackTrace()[counter].toString());
		}
	}
}
