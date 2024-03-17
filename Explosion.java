import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
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
    private VehicleWorld vw;
    
    protected int countdown = 60;
    protected int actCount;
    protected int playing = 0;
    private int radius;
    public Explosion(){
        img = new GreenfootImage("images/explode_low.png");
        sfx = new GreenfootSound("sounds/explode.mp3");
    }
    
    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
        ArrayList<Vehicle> vNear = (ArrayList<Vehicle>) getObjectsInRange(radius,Vehicle.class);
        ArrayList<Pedestrian> pNear = (ArrayList<Pedestrian>) getObjectsInRange(radius,Pedestrian.class);
        for(Vehicle v: vNear){
            getWorld().removeObject(v);
        }
        for(Pedestrian p: pNear){
            getWorld().removeObject(p);
            vw.reducePed2Count();
        }
        explode();
    }
    
    public void explode(){
        actCount ++;
        if (playing == 0) {
            sfx.play();
            playing = 1;
        }
        if (actCount > countdown && getWorld() != null) {
            actCount = 0;
            getWorld().removeObject(this);
        }
    }
    
    public void explode(int countdown, int volume){
        this.countdown = countdown;
        sfx.setVolume(volume);
        actCount ++;
        if (playing == 0) {
            sfx.play();
            playing = 1;
        }
        if (actCount > countdown && getWorld() != null) {
            actCount = 0;
            getWorld().removeObject(this);
        }
    }
    
    public void addedToWorld(World w){
        vw = (VehicleWorld) w;
        this.sfx = sfx;
        playing = 0;
        sfx.play();
        setImage(img);
    }
}
