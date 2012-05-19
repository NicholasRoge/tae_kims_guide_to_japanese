/**
 * 
 */
package roge.androidextended.menu;

import android.content.Context;
import android.widget.LinearLayout;
import roge.androidextended.menu.MenuXmlParser.MenuData;

/**
 * @author Nicholas Rogé
 *
 */
public class MenuInflater{
    Context _context=null;
    MenuData _menu_data=null;
    
    
    /*Begin Constructors*/
    public MenuInflater(Context context,MenuData data){
        if(context==null||data==null){
            throw new RuntimeException("Neither the Context or Data parameters may be null when instantiating the MenuInflater class.");
        }
        
        this._context=context;
        this._menu_data=data;
    }
    /*End Constructors*/
    
    /*Begin Getter Methods*/
    /**
     * Gets the context of the running application.
     * 
     * @return Returns the context of the running application.
     */
    private Context _getContext(){
        return this._context;
    }
    
    /**
     * Gets the data for the <code>Menu<code> object with which this object is associated.
     * 
     * @return Returns the data for the <code>Menu<code> object with which this object is associated.
     */
    private MenuData _getMenuData(){
        return this._menu_data;
    }
    /*End Getter Methods*/
    
    /*Begin Additional Methods*/
    /**
     * Builds the Menu for the specified path.
     * 
     * @param path The path to build the menu to.  If this parameter is null, then the menu will be built using the root elements.
     */
    public void rebuild(LinearLayout view,int path[]){
        if(path==null){
            
        }else{
            
        }
    }
    /*End Additional Methods*/
}
