/**
 * 
 */
package roge.taekim.content.span;

import java.io.IOException;

import roge.androidextended.ETextView;
import roge.androidextended.Tools;
import roge.taekim.Main;
import roge.taekim.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author Nicholas Rogé
 *
 */
public class AudioSpan extends Span{
    private class Audio extends ClickableSpan{
        private MediaPlayer _player;
        private String      _resource;
        private String      _type;
        
        public Audio(String type,String resource){
            this._resource=resource;
            this._type=type;
        }
        
        @Override public void onClick(View view){           
            Context context=Main.activity.getApplicationContext();
            int     resource_id=0;
            int     ringer_mode=0;
            
            if(this._player==null){                
                ringer_mode=((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
                if(ringer_mode==AudioManager.RINGER_MODE_SILENT||ringer_mode==AudioManager.RINGER_MODE_VIBRATE){
                    Toast.makeText(Main.activity.getApplicationContext(),Main.activity.getApplicationContext().getResources().getString(R.string.audiospan_soundoff),Toast.LENGTH_LONG).show();
                }
                
                if(this._type.equals("file")){
                    this._player=new MediaPlayer();
                    
                    try{
                        this._player.setDataSource(this._resource);
                    }catch(IOException e){
                        Log.e("JP","The resource with name \""+this._resource+"\" could not be loaded.");  //I didn't say loaded here because teh documentation us unclear about what can throw an IOExceptoin
                        
                        return;
                    }catch(Exception e){
                        Tools.logException(e);
                        
                        return;
                    }
                }else if(this._type.equals("resource")){
                    resource_id=context.getResources().getIdentifier(this._resource,"raw",context.getPackageName());
                    if(resource_id==0){
                        Log.e("JP","The resource with name \""+this._resource+"\" could not be found.");
                        
                        return;
                    }
                    
                    this._player=MediaPlayer.create(context,resource_id);
                }
    
                this._player.setOnPreparedListener(new OnPreparedListener(){
                    public void onPrepared(MediaPlayer player){
                        player.start();
                    }
                });
                this._player.setOnCompletionListener(new OnCompletionListener(){
                    public void onCompletion(MediaPlayer player){
                        player.release();
                        
                        _player=null;
                    }
                    
                });
            }
        }
        
        @Override public void updateDrawState(TextPaint paint){
            paint.setColor(0xFFB100C4);  //TODO:  Find a better way to set these values.
            paint.setUnderlineText(false);
            paint.setTypeface(Typeface.create((String)null,Typeface.BOLD));  //Have to add that cast to avoid ambiguity.
        }
    }
    
    /**
     * @see roge.taekim.content.span.Span#parseSpan(CharSequence, int, int, SpanAttributes)
     */
    @Override public CharSequence parseSpan(CharSequence string,int span_start,int span_end,SpanAttributes attributes){
        if(!attributes.hasAttribute("type")){
            Log.e("JP","The "+this.getTag()+" tag MUST have the \"type\" attribute set.");
            
            return string;
        }else{
            if(!(attributes.getAttribute("type").equals("file")||attributes.getAttribute("type").equals("resource"))){
                Log.e("JP","\""+attributes.getAttribute("type")+"\" is an invalid type for attribute \"type\".");
                
                return string;
            }
        }

        ((Spannable)string).setSpan(
            new Audio(
                attributes.getAttribute("type"),
                attributes.getAttribute("file")
            ),
            span_start,
            span_end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        return string;
    }
}
