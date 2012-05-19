package roge.taekim;

import java.util.List;

import roge.androidextended.Tools;
import roge.androidextended.menu.Menu;
import roge.taekim.content.Content;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * @author Nicholas Rogé
 *
 * The main activity.<br /> 
 * <code>Herp herp=new Derp();</code>
 */
public class Main extends Activity{
    public static final int CONTENT_MENU_RESULT=100;
    
    /**Used in the event that another view needs to use the Activity object.*/
	public static Activity activity;
	public static Content  content;
	
	/**Allows all the Objects created during the lifetime of this activity to get any preferences the user may have set.*/
	public static SharedPreferences preferences;
	
	private int _last_menu_path[]=null;
	
	
    @Override public void onCreate(Bundle bundle){    	
        super.onCreate(bundle);

        Main.activity=this;  //Share the love!  Let everyone use the activity object
        preferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        setContentView(R.layout.main);

        Main.content=(Content)findViewById(R.id.content);    //Save these two views
        
        //Add teh version name to teh title
        this.setTitle(this.getTitle()+" - "+this.getApplicationContext().getString(R.string.version_name));
    }
    
    
    
    /*@Override public boolean onKeyDown(int key_code,KeyEvent event){
    	if(key_code==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){  //We only want the first "back" that was entered.  :/
            if(!event.isTracking()){
                event.startTracking();
            }
            
            return true;
	    }
    	
    	return super.onKeyDown(key_code,event);
    }*/
    
    @Override public boolean onKeyUp(int key_code,KeyEvent event){
        List<String> content_history;
        
        
        if(key_code==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            content_history=Main.content.getContentHistory();
            
            if(content_history.size()>1){  //If the size is one, then the page we're currently on is the last page in the history
                content_history.remove(content_history.size()-1);  //This has to come before you set teh content, otherwise it will just add the page you're about to change to back into the history
                
                try{
                    Main.content.setContent(content_history.get(content_history.size()-1));
                }catch(Exception e){
                    Tools.logException(e);
                }
            }else{
                Toast.makeText(getApplicationContext(),this.getApplicationContext().getString(R.string.last_content_history),Toast.LENGTH_LONG).show();
            }
            
            return true;
        }
        
        return super.onKeyUp(key_code,event);
    }
    
    /*@Override public boolean onKeyLongPress(int key_code,KeyEvent event){
        Log.d("JP","HIT");
        
        if(key_code==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            Log.d("JP","Back button held.");
            
            this.finish();
            
            return true;
        }
        
        return super.onKeyLongPress(key_code,event);
    }*/
    
    @Override public boolean onCreateOptionsMenu(android.view.Menu menu){
    	getMenuInflater().inflate(R.menu.main,menu);
    	
    	return true;
    }
    
    @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
        Intent intent=null;
        
        
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.email_dev:
                intent=new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[]{"NicholasRoge@gmail.com"});
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"[Suggestion]:  ");
                intent.setType("plain/text");
                
                startActivity(Intent.createChooser(intent,"E-mail Suggestion With:"));
                
                return true;
	        case R.id.open_menu:
	            intent=new Intent(this.getApplicationContext(),ContentMenu.class);
	            intent.putExtra("path",this._last_menu_path);
	            
	            startActivityForResult(intent,Main.CONTENT_MENU_RESULT);
	        	
	            return true;
	        case R.id.options:
	            this.startActivity(new Intent(this.getBaseContext(),Preferences.class));
	        	
	            return true;
	        case R.id.visit_forum:
	        	Intent visit_forum=new Intent(Intent.ACTION_VIEW);
	        	visit_forum.setData(Uri.parse("http://www.guidetojapanese.org/forum/"));
	        	startActivity(visit_forum);
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override public void onActivityResult(int request_code,int result_code,Intent data){
        switch(request_code){
            case Main.CONTENT_MENU_RESULT:
                switch(result_code){
                    case ContentMenu.MENU_RESULT:
                        if(data.getStringExtra("result")!=null){
                            try{
                                this._last_menu_path=data.getIntArrayExtra("path");

                                Main.content.setContent(data.getStringExtra("result"));
                            }catch(Exception e){
                                Tools.logException(e);
                            }
                        }
                        
                        break;
                }
                break;
        }
    }
}

/*Master TODO Sheet*/
//TODO:  Fix the fact that the cancel MotionEvent isn't being hit correctly for the navigation buttons.