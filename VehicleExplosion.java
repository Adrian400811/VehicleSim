import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class VehicleExplosion here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VehicleExplosion extends Explosion
{
    int volume = 80;
    /**
     * Act - do whatever the VehicleExplosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public VehicleExplosion(){
        super();
        super.img = new GreenfootImage("images/explode_low.png");
        super.sfx = new GreenfootSound("sounds/explode.mp3");
    }
    
    public VehicleExplosion(int countdown){
        super();
        this.countdown = countdown;
    }
    
    public void act()
    {
        // Add your action code here.
        explode(countdown,volume);
    }
    
    public void addedToWorld(World w){
        
    }
}
