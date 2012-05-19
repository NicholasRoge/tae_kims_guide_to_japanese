/**
 * 
 */
package roge.androidextended.menu;

import java.util.HashMap;

import roge.androidextended.ETextView;
import roge.androidextended.menu.MenuXmlParser.MenuItemData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.InflateException;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Intent of class is to provide a specialized version of the TextView class which will provide the Menu class with the necessary set of data, as well has to provide the MenuItem with extra needed information.
 * 
 * @author Nicholas Rogé
 */
public class MenuItem extends ETextView{
    /**
     * Intent of class is to provide a container for the MenuItem's parameters.
     * 
     * @author Nicholas Rogé
     */
    public static class MenuItemParameters{
        private final HashMap<String,String> _extras=new HashMap<String,String>();
        
        private String _action=null;
        private String _id=null;
        private String _title=null;
        
        
        /*Begin Getter Methods*/
        /**
         * Gets the <code>action<code> parameter of the XML element.
         * 
         * @return Returns the action parameter of the XML element if one was given.  Otherwise, this method will return <code>null</code>.
         */
        public String getAction(){
            return this._action;
        }
        
        /**
         * Gets the <code>id<code> parameter of the XML element.
         * 
         * @return Returns the id parameter of the XML element if one was given.  Otherwise, this method will return <code>null</code>.
         */
        public String getId(){
            return this._id;
        }
        
        /**
         * Gets the <code>title<code> parameter of the XML element.
         * 
         * @return Returns the title parameter of the XML element if one was given.  Otherwise, this method will return <code>null</code>.
         */
        public String getTitle(){
            return this._title;
        }
        
        /**
         * Gets the contents of the parameter of the XML element with the specified name.
         * 
         * @param key Name of the parameter whose contents you want to retrieve.
         * 
         *  @return Returns the contents of the parameter of the XML element with the specified name, or <code>null</code> if that parameter does not exist.
         */
        public String getExtra(String key){
            if(!this._extras.containsKey(key)){
                return null;
            }

            return this._extras.get(key);
        }
        /*End Getter Methods*/
        
        /*Begin Setter Methods*/
        /**
         * Sets the <code>action</code> parameter of the XML element.
         * 
         * @param action The string to set the parameter to.
         */
        public void setAction(String action){
            this._action=action;
        }
        
        /**
         * Sets the <code>id</code> parameter of the XML element.
         * 
         * @param id The string to set the parameter to.
         */
        public void setId(String id){
            this._id=id;
        }
        
        /**
         * Sets the <code>title</code> parameter of the XML element.
         * 
         * @param title The string to set the parameter to.
         */
        public void setTitle(String title){
            this._title=title;
        }
        /*End Setter Methods*/
        
        /**
         * Adds any extra parameters to the container which aren't explicitly mentioned
         * 
         * @param key The name of the parameter in the XML element.
         * @param data The contents of the parameter in the XML element.
         */
        public void addExtra(String key,String data){
            this._extras.put(key,data);
        }
    }
    
    /**String to set this object's title to when none is given.*/
    public final static String NO_TITLE="Untitled Item";
    
    private int                _index=-1;
    private Menu               _menu=null;
    private MenuItemData       _menu_item_data=null;
    private MenuItemParameters _parameters=null;
    
    
    /*Begin Initializer Methods*/
    /**
     * Initializes the objects details.
     * 
     * @param params The parameters that the element had in the XML file from which this Item was inflated.  TODO:  Yeah.  That description sucked.  Make a better one.
     * 
     * @throws InflateException Throws an InflateException if either the <code>Menu</code> or <code>MenuItemParameters</code> parameters are <code>null</code>
     */
    protected void _initialize(Menu menu,MenuItemData data,MenuItemParameters params){
        if(menu==null){
            throw new InflateException("This Menu parameter MUST not be <code>null</code>.");
        }else if(data==null){
            throw new InflateException("This MenuItemData parameter MUST not be <code>null</code>.");
        }
        
        this._menu=menu;  //This is the only time the _menu object should be modified.
        this._menu_item_data=data;
        if(params!=null){
            this.setParameters(params);
        }
        
        if(params.getTitle()==null){
            this.setText(MenuItem.NO_TITLE);
        }else{
            this.setText(params.getTitle());
        }
    }
    /*End Initializer Methods*/
    
    /*Begin Constructors*/
    /**
     * Constructs the object.
     * 
     * @param context The context in which the application is currently running.
     * @param params The parameters that the element had in the XML file from which this Item was inflated.  TODO:  Yeah.  That description sucked.  Make a better one.
     */
    public MenuItem(Context context,Menu menu,MenuItemData data,MenuItemParameters params){
        super(context);
        
        this._initialize(menu,data,params);
    }
    /*End Constructors*/
    
    /*Begin Overridden Methods*/
    @Override public boolean onTouchEvent(MotionEvent event){
        Intent result=null;
        
        
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.setBackgroundColor(this.getStyle().getBackgroundColorOntouch());
                
                break;
            case MotionEvent.ACTION_UP:
                if(this.hasChildren()){
                    this.getMenu().navigateTo(this.getIndexPath()); 
                }else{
                    result=new Intent();
                    
                    
                    result.putExtra("path",this.getMenu().getCurrentPath());
                    result.putExtra("result",this.getMenu().menuItemOnTouch(this,event));
                    
                    this.getMenu().setResult(Menu.MENU_RESULT,result);
                    this.getMenu().finish();
                }
                
            case MotionEvent.ACTION_CANCEL:
                this.setBackgroundColor(this.getStyle().getBackgroundColor());
                
                break;
        }
        
        return true;
    }
    /*End Overridden Methods*/
    
    /*Begin Getter Methods*/    
    /**
     * Gets this object's index relative to its parent.
     * 
     * @return Returns this object's index relative to its parent.
     */
    public int getIndex(){
        return this._index;
    }
    
    /**
     * Gets the path the Menu should take when navigating through the MenuData object.
     * 
     * @return Returns the path the Menu should take when navigating through the MenuData object, or <code>null</code> if this object is a top-level element.
     */
    public int[] getIndexPath(){
        return this.getMenuItemData().getIndexPath();
    }
    
    /**
     * Gets the <code>Menu</code> object with which this object is associated.
     * 
     * @return Returns the <code>Menu</code> object with which this object is associated.
     */
    public Menu getMenu(){
        return this._menu;
    }
    
    /**
     * Gets the menu item data associated with this object.
     * 
     * @return Returns the menu item data associated with this object.
     */
    public MenuItemData getMenuItemData(){
        return this._menu_item_data;
    }
    
    /**
     * Gets this objects XML parameters.
     * 
     * @return Returns this objects XML parameters.
     */
    public MenuItemParameters getParameters(){
        if(this._parameters==null){
            return this.getMenuItemData().getParameters(); 
        }else{
            return this._parameters;
        }
    }
    /*End Getter Methods*/

    /*Begin Setter Methods*/    
    /**
     * Sets this object's index relative to its parent.
     * 
     * @param index This object's index relative to its parent.
     */
    public void setIndex(int index){
        this._index=index;
    }
    
    /**
     * Sets this object's XML parameters.
     * 
     * @param params <code>MenuItemParameters</code> properties that this object should have.
     */
    public void setParameters(MenuItemParameters params){
        this._parameters=params;
    }
    /*End Setter Methods*/
    
    /*Begin Boolean Check Methods*/
    /**
     * Check to test whether this object has children.
     * 
     * @return Returns <code>true</code> if this object has children, and <code>false</code> otherwise.
     */
    public boolean hasChildren(){
        return this.getMenuItemData().hasChildren();
    }
    /*End Boolean Check Methods*/
}
