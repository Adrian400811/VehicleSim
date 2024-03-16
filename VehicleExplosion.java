import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class VehicleExplosion here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VehicleExplosion extends Explosion
{
    private int radius = 60;
    /**
     * Act - do whatever the VehicleExplosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public VehicleExplosion(){
        super();
        super.img = new GreenfootImage("images/explode_low.png");
        super.sfx = new GreenfootSound("sounds/explode.mp3");
    }
    
    public void act()
    {
        // Add your action code here.
        ArrayList<Pedestrian> pNear = (ArrayList<Pedestrian>) getObjectsInRange(radius,Pedestrian.class);
        for(Pedestrian p: pNear){
            getWorld().removeObject(p);
        }
        explode();
    }
    
    public void addedToWorld(World w){
        
    }
}
