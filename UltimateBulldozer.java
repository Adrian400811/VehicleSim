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
    
    
    public UltimateBulldozer(VehicleSpawner origin) {
        this.origin = origin;
    }

    protected void addedToWorld(World world){
        VehicleWorld vw = (VehicleWorld)world;
        height = origin.getHeight();
        width = world.getWidth();
        y = vw.getLaneY(origin.getLaneNumber());
        setLocation(0, y);
        image = new GreenfootImage(width, height);
        image.setColor(TRANSPARENT_RED);
        image.fillRect(0, 0, width, height);
        setImage(image);
        System.out.println("add "+origin.getLaneNumber()+" "+System.currentTimeMillis());
        boom();
    }

    public void boom(){
        ArrayList<Vehicle> pedsTouching = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);
        for (Vehicle v : pedsTouching){
            v.explode();
        }
        System.out.println("boom "+origin.getLaneNumber()+" "+System.currentTimeMillis());
        getWorld().removeObject(this);
    }
}
