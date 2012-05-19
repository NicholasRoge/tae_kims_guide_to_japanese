/**
 * 
 */
package roge.taekim;

import roge.androidextended.menu.Menu;
import roge.androidextended.menu.MenuItem;
import android.os.Bundle;
import android.view.MotionEvent;

/** 
 * @author Nicholas Rogé
 */
public class ContentMenu extends Menu{    
    @Override public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        int path[]=null;
        
        
        if(this.getIntent().getExtras()!=null){
            path=this.getIntent().getExtras().getIntArray("path");
        }
        this._createFromXml(R.xml.content_select,path);
    }
    
    @Override public String menuItemOnTouch(MenuItem item,MotionEvent event){
        String test=null;
        
        
        test=item.getMenuItemData().getParameters().getAction();
        if(test==null){
            return "This item has no content associated with it.";
        }else{
            return test;
        }
    }

}
