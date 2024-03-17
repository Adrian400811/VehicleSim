import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class UltimateBulldozer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UltimateBulldozer extends Actor
{
    public static final Color TRANSPARENT_RED = new Color (255, 0, 0, 128);
    private GreenfootImage image;
    protected boolean visible;
    protected VehicleSpawner origin;
    protected int height;
    protected int width;
    protected int y;
    protected int cooldown = 5;
    
    public UltimateBulldozer(VehicleSpawner origin) {
        this.origin = origin;
    }

    protected void addedToWorld(World world){
        VehicleWorld vw = (VehicleWorld)world;
        height = origin.getHeight();
        width = world.getWidth();
        y = vw.getLaneY(origin.getLaneNumber());
        if (origin.facesRightward()) {
            setLocation(10,y);
        } else {
            setLocation(vw.getWidth()-10, y);
        }
        image = new GreenfootImage(width, height);
        image.setColor(TRANSPARENT_RED);
        image.fillRect(0, 0, width, height);
        setImage(image);
        boom();
    }

    /**
     * Removes all touching vehicle and play explode effects on them
     */
    public void boom(){
        ArrayList<Vehicle> vehTouching = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);
        for (Vehicle v : vehTouching){
            v.explode();
            getWorld().removeObject(v);
        }
        getWorld().removeObject(this);
    }
}
