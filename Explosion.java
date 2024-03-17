import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Superclass of every explosion effect.
 * Explosion radius defined by image size.
 * Remove all vehicles and pedestrians in radius.
 * 
 * @author Adrian Lee 
 * @version 20240317
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
    
    public void addedToWorld(World w){
        vw = (VehicleWorld) w;
        this.sfx = sfx;
        playing = 0;
        sfx.play();
        setImage(img);
    }
    
    public void act()
    {
        // Add your action code here.
        explode();
    }
    
    /**
     * Play explode effect with default settings
     */
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
    
    /**
     * Play explode effect by a custom period of time
     * and volume
     * 
     * @param countdown The length of time the effect stays on screen in acts.
     * @param volume    The amplitude of sound. (0-100)
     */
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
}
