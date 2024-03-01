import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * The Bus subclass
 */
public class Truck extends Vehicle
{
    public Truck(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first
        
        //Set up values for Bus
        maxSpeed = 1.5 + ((Math.random() * 10)/5);
        speed = maxSpeed;
        // because the Bus graphic is tall, offset it a up (this may result in some collision check issues)
        // yOffset = 15;
    }

    /**
     * Act - do whatever the Bus wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        Vehicle ahead = (Vehicle) getOneObjectAtOffset (
         direction * (int)(speed + getImage().getWidth()/2 + 3), 0, Vehicle.class);
        if(ahead != null && detectCrash(ahead)){
            tow(ahead);
        }
        super.act();
    }

    public boolean checkHitPedestrian () {
        // currently empty
        return false;
    }
    
    public boolean detectCrash(Vehicle ahead) {
        if (ahead.checkCrash()){
            return true;
        }
        return false;
    }
    
    public void tow(Vehicle ahead) {
        towing = true;
        ahead.getTowed(this);
    }
}
