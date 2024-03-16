import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Explosion here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Explosion extends Actor
{
    protected GreenfootImage img;
    protected GreenfootSound sfx;
    
    protected int countdown = 60;
    protected int actCount;
    
    public Explosion(){
        
    }
    
    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
        
    }
    
    public void explode(){
        actCount ++;
        if (actCount > countdown && getWorld() != null) {
            actCount = 0;
            getWorld().removeObject(this);
        }
    }
    
    public void addedToWorld(World w){
        this.sfx = sfx;
        sfx.play();
        setImage(img);
    }
    
    public void scale() {
        
    }
}
