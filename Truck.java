import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * The tow truck subclass
 */
public class Truck extends Vehicle
{
    private GreenfootSound sfx;
    public Truck(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first
        
        //Set up values for Bus
        maxSpeed = 1.5 + ((Math.random() * 10)/5);
        speed = maxSpeed;
        
        sfx = new GreenfootSound("sounds/truckShort.wav");
        sfx.setVolume(70);
    }

    /**
     * Act - do whatever the Bus wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        Vehicle ahead = (Vehicle) getOneObjectAtOffset (
         direction * (int)(speed + getImage().getWidth()/2 + 3), 0, Vehicle.class);
        if(detectCrash(ahead) && !towed){
            tow(ahead);
        }
        super.act();
    }

    public boolean checkHitPedestrian () {
        // currently empty
        return false;
    }
    
    /**
     * Detect if the vehicle in front is crashed
     */
    public boolean detectCrash(Vehicle ahead) {
        if (ahead != null){
            return true;
        }
        return false;
    }
    
    public void tow(Vehicle ahead) {
        towing = true;
        ahead.getTowed(this);
        sfx.play();
    }
}
