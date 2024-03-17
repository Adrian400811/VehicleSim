import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Explosion effect for vehicles, triggered by UltimateBulldozer.
 * Only removes nearby pedestrians.
 * 
 * @author Adrian Lee
 * @version 20240317
 */
public class VehicleExplosion extends Explosion
{
    private int radius;
    
    public VehicleExplosion(){
        super();
        radius = img.getWidth();
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
}
