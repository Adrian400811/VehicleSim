import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Pedestrian 2 approaches the nearest Pedestrian 1 and stop them
 * before they explodes
 * 
 * @author Adrian Lee 
 * @version 20240317
 */
public class Ped2 extends Pedestrian
{
    VehicleWorld vw;
    private double speed;
    private double maxSpeed;
    private int direction;
    private int target;
    private boolean awake, entering;
    public Ped2(int direction){
        super(direction);
        maxSpeed = Math.random() * 2 + 1;
        speed = maxSpeed*0.8;
        enableStaticRotation();
    }
    
    public void addedToWorld(World w){
        vw = (VehicleWorld) w;
    }
    
    /**
     * Act - do whatever the Ped2 wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Awake is false if the Pedestrian is "knocked down"
        if (super.isAwake()){
            followNearestP();
            arrestPed1(detectCollidingPed1());
        }
    }
    
    /**
     * Get distance to a given Pedestrian 1
     * Inspired by Gevater_Tod4177 on greenfoot.org
     * https://www.greenfoot.org/topics/4911
     * 
     * @param p Pedestrian 1
     */
    public double getDistance(Ped1 p) {
        return Math.hypot(p.getX() - getX(), p.getY() - getY());
    }
    
    /**
     * Get the nearest Pedestrian 1 by comparing distances between
     * all the Pedestrian 1s in range
     * Inspired by Gevater_Tod4177 on greenfoot.org
     * https://www.greenfoot.org/topics/4911
     */
    public Ped1 getNearestPed() {
        ArrayList<Ped1> pNear = (ArrayList<Ped1>) getObjectsInRange(1280,Ped1.class);
        Ped1 nearestP = null;
        double nearestDistance = 999;
        double distance;
        for(Ped1 p: pNear){
            distance = getDistance(p);
            if(distance < nearestDistance){
                nearestP = p;
                nearestDistance = distance;
            }
        }
        return nearestP;
    }
    
    /**
     * Move towards the nearest Pedestrian 1 using getNearestPed()
     */
    public void followNearestP() {
        Ped1 nearestPed = getNearestPed();
        if(nearestPed != null){
            turnTowards(nearestPed);
            move(speed);
        }        
    }
    
    /**
     * Returns intersecting Pedestrian 1
     */
    public Ped1 detectCollidingPed1() {
        Ped1 p = (Ped1) getOneIntersectingObject(Ped1.class);
        return p;
    }
    
    /**
     * Arrests Pedestrian 1 if intersecting
     * Currently simply removing them from world
     * Random triggers Pedestrian 1 explode
     */
    public void arrestPed1(Ped1 collidingPed1) {
        if (collidingPed1 != null){
            if(Greenfoot.getRandomNumber(2) == 1){
                collidingPed1.explode();
            }
            vw.removeObject(collidingPed1);
            vw.removeObject(this);
            vw.reducePed2Count();
        }
    }
}
