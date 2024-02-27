import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Spawner object is a place where a Vehicle can spawn. Each spawner is
 * able to check if there is already a Vehicle in the spot to avoid spawning
 * multiple Vehicles on top of each other.
 * 
 * These can be shown in order to help understand and build the simulation,
 * but should be hidden when your project is complete.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VehicleSpawner extends Actor
{
    public static final Color TRANSPARENT_RED = new Color (255, 0, 0, 128);

    // minimum distance between Vehicles when they spawn. This will be used
    // as the width of the Spawners and additional Vehicles cannot spawn until
    // the previous Vehicle clears the spawner so this is effectively the 
    // minimum distance between Vehicles at the moment they spawn.
    public static final int DIST_BETWEEN_CARS = 128;

    private GreenfootImage image;

    private int laneNumber;

    private double speedModifier;

    private boolean rightward;
    private boolean visible;
    private int height, width;

    public VehicleSpawner (boolean rightward, int laneHeight, int laneNumber)
    {
        this.laneNumber = laneNumber;
        this.rightward = rightward;
        this.height = (int)(laneHeight * 0.75);
        width = DIST_BETWEEN_CARS;
        Font laneFont = new Font ("Courier New", true, false, (int)(height* 0.8));
        // set this to true (in World) to see the Spawners to help understand how it works,
        // and then false to hidethem at the end to make your project look clean.
        visible = VehicleWorld.SHOW_SPAWNERS;
        speedModifier = 1.0; // 100%, unless changed elsewhere
        image = new GreenfootImage (width, height);
        // If visible, fill this image with red to show its size, and mark the 
        // lane number. If not visible, simply do nothing and this Spawner will
        // be represented by a completely transparent (empty) image.
        if(visible){
            image.setColor(TRANSPARENT_RED);
            image.fillRect(0, 0, width-1, height - 1);
            image.setColor(Color.WHITE);
            image.setFont(laneFont);
            image.drawString ("" + laneNumber, 10, (int)(height * 0.8));
            image.drawString ("" + laneNumber, width - 28, (int)(height * 0.8));

        }
        setImage(image);
    }

    /**
     * Do you want a FAST LANE? Or a SLOW LANE?
     * 
     * Speed Modifier works as follows:
     * - A speed modifier of 1.0 would have no effect.
     * - A speed modifier of 1.2 would cause Vehicles to spawn moving 20% faster
     * - A speed modifier of 0.5 would cause Vehicles to spawn at half speed (slowww).
     * 
     * This affects the values for a Vehicles MAX speed, not current speed. So,
     * for example, Vehicles spawned the left lane might be set to be in a bigger
     * hurry than Vehicles spawned in the right lane. The speed still uses the 
     * Vehicle subclass' values, but then multiplies it by the modifier to determine
     * the actual max speed. You can simply not call this method if you do 
     * not want to implement this.
     * 
     * @param double    the multiplier (1.0 = 100%, 0.5 = 50%, 1.9 = 190%, etc.)
     *                  which will affect spawned Vehicle's max speed. Must be positive.
     */
    public void setSpeedModifier (double speedModifier){
        if (speedModifier > 0){
            this.speedModifier = speedModifier;
        }
    }
    
    public double getSpeedModifier (){
        return this.speedModifier;
    }

    public boolean facesRightward (){
        return rightward;
    }

    public boolean isTouchingVehicleOld () {
        return this.isTouching (Car.class) || this.isTouching (Bus.class) || this.isTouching (Ambulance.class);
    }

    public boolean isTouchingVehicle () {
        return this.isTouching(Vehicle.class);
    }

    /**
     * Get the lane number (0 indexed) that this VehicleSpawner represents.
     * This can be used by the Vehicle to figure out where it is, and when
     * faced with the lane change algorithm task, which lanes it can move into.
     * 
     * @return int  the lane number in a 0-indexed fashion
     */
    public int getLaneNumber(){
        return this.laneNumber;
    }
}
