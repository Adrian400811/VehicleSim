import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * The Ambulance subclass
 */
public class Ambulance extends Vehicle
{
    public Ambulance(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first
        
        maxSpeed = 2.5;
        speed = maxSpeed;
    }

    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        killPedestrian();
        super.act();
    }

    public boolean checkHitPedestrian () {
        return false;
    }
    
    public void killPedestrian() {
        Pedestrian p = (Pedestrian) getOneIntersectingObject(Pedestrian.class);
        
        // this works, but doesn't ignore knocked down Pedestrians
        //actorsTouching.addAll(pedsTouching);
        if (p != null && !p.isAwake()){
            getWorld().removeObject(p);
        }
    }
}
