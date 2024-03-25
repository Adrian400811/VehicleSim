import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Pedestrian 1 chooses a random lane as target and explodes
 * when it reaches the Y value of the target lane
 * 
 * @author Adrian Lee
 * @version 20240317
 */
public class Ped1 extends Pedestrian
{
    VehicleWorld vw;
    private double speed;
    private double maxSpeed;
    private int direction, target, radius;
    private boolean awake, targetReached, entering;
    
    public Ped1(int direction){
        super(direction);
        targetReached = false;
        radius = 96;
    }
    
    public void addedToWorld(World w){
        vw = (VehicleWorld) w;
        target = Greenfoot.getRandomNumber(vw.laneCount);
    }
    
    public void act()
    {
        if(getY() == vw.getLaneY(target)){
            targetReached = true;
        }
        if(targetReached){
            ArrayList<Vehicle> vNear = (ArrayList<Vehicle>) getObjectsInRange(radius,Vehicle.class);
            if(vNear.size() > 0){
                explode();
            }
        }
        if(!targetReached){
            super.act();
        }
    }
    
    public void explode(){
        vw.addObject(new PedExplosion(),getX(),getY());
        vw.removeObject(this);
    }
}
