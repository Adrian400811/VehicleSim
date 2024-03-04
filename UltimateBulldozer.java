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
    protected World world;
    protected int height;
    protected int width = 48;
    
    
    public UltimateBulldozer(VehicleSpawner origin) {
        this.origin = origin;
        this.world = getWorld();
        this.height = origin.getHeight();
        //this.width = world.getLaneY(origin.getLaneNumber());
    }

    public void addedToWorld(){
        image = new GreenfootImage(100, 100);
        image.setColor(TRANSPARENT_RED);
        image.fillRect(0, 0, height, width);
    }

    /**
     * Act - do whatever the UltimateBulldozer wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act(){
        ArrayList<Vehicle> pedsTouching = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);

        ArrayList<Actor> actorsTouching = new ArrayList<Actor>();

        for (Vehicle v : pedsTouching){
            getWorld().removeObject(v);
        }
        getWorld().removeObject(this);
    }
}
