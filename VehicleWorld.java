import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Collections;
import java.util.ArrayList;
/**
 * <h1>VehicleSim by Adrian Lee</h1>
 * <p>Update log: Github commits<br/>
 * Github: https://github.com/Adrian400811/VehicleSim</p>
 * 
 * <h2>Extra features:</h2>
 * <p>Vehicles crashes<br/>
 * Tow truck tows vehicles in front of it<br/>
 * Lane cleaner (UltimateBulldozer) cleans up spawner if blocked<br/>
 * Pedestrian 1 goes to a random lane and explodes when vehicle is nearby<br/>
 * Pedestrian 2 stopping Pedestrian 1, but 50/50 explodes<br/>
 * Airstrike when there is too much Pedestrian 2 in the world<br/>
 * Plane drops bomb and explode as airstrike<br/>
 * Explosion effects for lane cleaner, pedestrian 1 and airstrike<br/>
 * Sound effects for explosion, towing and pedestrian knockdown</p>
 * 
 * <h2>Credit:</h2>
 * <p>Ped2.java Line 42-72 Inspired by Gevater_Tod4177 on greenfoot.org<br/>
 * https://www.greenfoot.org/topics/4911</p>
 * 
 * <p>VehicleWorld.java Line 112-117 Inspired by danpost on greenfoot.org<br />
 * https://www.greenfoot.org/topics/57369</p>
 * 
 * <p>Vehicles: Awesome Car Pack - UnLucky Studio (Sujit Yadav)<br />
 * https://unluckystudio.com/game-art-giveaway-7-top-down-vehicles-sprites-pack/</p>
 * <p>Explosion Image: Explosion - pixelartmaker.com<br />
 * https://pixelartmaker.com/art/695c3a296d3fc8c</p>
 * <p>Explosion Sound: Small Bomb Explosion Sound Effect<br />
 * https://youtu.be/9FMquJzgDGQ</p>
 * <p>Scream: Vilhelm Scream</p>
 * <p>Truck Sound: Truck in Reverse - Beeping - Sound Effect<br />
 * https://youtu.be/fRzYqsDSplg</p>
 * <p>Plane Image: Image by gstudioimagen on Freepik<br />
 * https://www.freepik.com/free-vector/plane-flying-travel-machine_137585846.htm</p>
 * <p>Jet Engine Sound: Sound Effect from Pixabay<br />
 * https://pixabay.com/?utm_source=link-attribution&utm_medium=referral&utm_campaign=music&utm_content=34032</p>
 */
public class VehicleWorld extends World
{
    private GreenfootImage background;

    // Color Constants
    public static Color GREY_BORDER = new Color (108, 108, 108);
    public static Color GREY_STREET = new Color (88, 88, 88);
    public static Color YELLOW_LINE = new Color (255, 216, 0);

    public static boolean SHOW_SPAWNERS = true;
    
    // Instance variables / Objects
    protected boolean twoWayTraffic, splitAtCenter;
    protected int laneHeight, laneCount, spaceBetweenLanes;
    private int[] lanePositionsY;
    private VehicleSpawner[] laneSpawners;
    private int ped2Count = 0;
    private int maxPed2 = 20;
    private int planeCount = 0;
    
    // latch lock
    private boolean cDown;
    private boolean settingShown;
    
    /**
     * Constructor for objects of class MyWorld.
     * 
     * Note that the Constrcutor for the default world is always called
     * when you click the reset button in the Greenfoot scenario screen -
     * this is is basically the code that runs when the program start.
     * Anything that should be done FIRST should go here.
     * 
     */
    public VehicleWorld()
    {    
        // Create a new world with 1024x800 pixels, UNBOUNDED
        super(1024, 800, 1, false); 

        // This command (from Greenfoot World API) sets the order in which 
        // objects will be displayed. In this example, Pedestrians will
        // always be on top of everything else, then Vehicles (of all
        // sub class types) and after that, all other classes not listed
        // will be displayed in random order. 
        //setPaintOrder (Pedestrian.class, Vehicle.class); // Commented out to use Z-sort instead

        // set up background -- If you change this, make 100% sure
        // that your chosen image is the same size as the World
        background = new GreenfootImage ("background01.png");
        setBackground (background);

        // Set critical variables - will affect lane drawing
        laneCount = 5;
        laneHeight = 48;
        spaceBetweenLanes = 6;
        splitAtCenter = true;
        twoWayTraffic = true;

        // Init lane spawner objects 
        laneSpawners = new VehicleSpawner[laneCount];

        // Prepare lanes method - draws the lanes
        lanePositionsY = prepareLanes (this, background, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes, twoWayTraffic, splitAtCenter);

        laneSpawners[0].setSpeedModifier(0.8);
        laneSpawners[3].setSpeedModifier(1.4);

        setBackground (background);
        addObject(new Label("C: config", 28), 100, 760);
    }

    public void act () {
        spawn();
        zSort ((ArrayList<Actor>)(getObjects(Actor.class)), this);
        if (cDown != Greenfoot.isKeyDown("c")){  // inspired by danpost
            cDown = ! cDown;
            if(cDown){
                if (!settingShown){
                    addObject(new Settings(), 0, 0);
                } else {
                    removeObjects(getObjects(Settings.class));
                }
                settingShown = !settingShown;
            }
        }
    }

    private void spawn () {
        // Chance to spawn a vehicle
        int lane = Greenfoot.getRandomNumber(laneCount);
        if (laneSpawners[lane].checkPileUp()){
            addObject(new UltimateBulldozer(laneSpawners[lane]), 0, 0);
        }
        
        if (Greenfoot.getRandomNumber (laneCount * 5) == 0 && 
            !laneSpawners[lane].isTouchingVehicle()){
            int vehicleType = Greenfoot.getRandomNumber(4);
            if (vehicleType == 0){
                addObject(new Car(laneSpawners[lane]), 0, 0);
            } else if (vehicleType == 1){
                addObject(new Bus(laneSpawners[lane]), 0, 0);
            } else if (vehicleType == 2){
                addObject(new Ambulance(laneSpawners[lane]), 0, 0);
            } else if (vehicleType == 3){
                addObject(new Truck(laneSpawners[lane]), 0, 0);
            }
        }

        // Chance to spawn a Pedestrian
        if (Greenfoot.getRandomNumber (60) == 0){
            int xSpawnLocation = Greenfoot.getRandomNumber (600) + 100; // random between 99 and 699, so not near edges
            int bw = Greenfoot.getRandomNumber(3);
            int pedType = Greenfoot.getRandomNumber(2);
            if (bw >= 2){
                addObject (new Ped1 (1), xSpawnLocation, getLaneY(0)-50);
            } else {
                addObject (new Ped2 (-1), xSpawnLocation, getLaneY(laneCount-1)+50);
                ped2Count ++;
            }
        }
        
        if (ped2Count > maxPed2 && planeCount == 0){
            addObject(new Plane(), -200, 100);
            planeCount ++;
        }
    }

    /**
     *  Return Y value by given lane number
     *  Does not deal with offset
     *  
     *  @param lane the lane number (zero-indexed)
     *  @return int the y position of the lane's center, or -1 if invalid
     */
    public int getLaneY (int lane){
        if (lane != -1 && lane < lanePositionsY.length){
            return lanePositionsY[lane];
        } 
        return -1;
    }

    /**
     * Return corresponding lane number by given Y value
     * 
     * @param y - the y position of the lane the Vehicle is in
     * @return int the lane number, zero-indexed
     * 
     */
    public int getLane (int y){
        for (int i = 0; i < lanePositionsY.length; i++){
            if (y == lanePositionsY[i]){
                return i;
            }
        }
        return -1;
    }

    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit, int centreSpacing)
    {
        // Declare an array to store the y values as I calculate them
        int[] lanePositions = new int[lanes];
        // Pre-calculate half of the lane height, as this will frequently be used for drawing.
        // To help make it clear, the heightOffset is the distance from the centre of the lane (it's y position)
        // to the outer edge of the lane.
        int heightOffset = heightPerLane / 2;
        // draw top border
        target.setColor (GREY_BORDER);
        target.fillRect (0, startY, target.getWidth(), spacing);

        // Main Loop to Calculate Positions and draw lanes
        for (int i = 0; i < lanes; i++){
            // calculate the position for the lane
            lanePositions[i] = startY + spacing + (i * (heightPerLane+spacing)) + heightOffset ;

            // draw lane
            target.setColor(GREY_STREET); 
            // the lane body
            target.fillRect (0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
            // the lane spacing - where the white or yellow lines will get drawn
            target.fillRect(0, lanePositions[i] + heightOffset, target.getWidth(), spacing);

            // Place spawners and draw lines depending on whether its 2 way and centre split
            if (twoWay && centreSplit){
                // first half of the lanes go rightward (no option for left-hand drive, sorry UK students .. ?)
                if ( i < lanes / 2){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else { // second half of the lanes go leftward
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw yellow lines if middle 
                if (i == lanes / 2){
                    target.setColor(YELLOW_LINE);
                    target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                } else if (i > 0){ // draw white lines if not first lane
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                } 

            } else if (twoWay){ // not center split
                if ( i % 2 == 0){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else {
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw Grey Border if between two "Streets"
                if (i > 0){ // but not in first position
                    if (i % 2 == 0){
                        target.setColor(GREY_BORDER);
                        target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                    } else { // draw dotted lines
                        for (int j = 0; j < target.getWidth(); j += 120){
                            target.setColor (YELLOW_LINE);
                            target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                        }
                    } 
                }
            } else { // One way traffic
                spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                world.addObject(spawners[i], 0, lanePositions[i]);
                if (i > 0){
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                }
            }
        }
        // draws bottom border
        target.setColor (GREY_BORDER);
        target.fillRect (0, lanePositions[lanes-1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    /**
     * Sort Z value of actors depending on their Y value
     * creating a better perspective.
     */
    public static void zSort (ArrayList<Actor> actorsToSort, World world){
        ArrayList<ActorContent> acList = new ArrayList<ActorContent>();
        // Create a list of ActorContent objects and populate it with all Actors sent to be sorted
        for (Actor a : actorsToSort){
            acList.add (new ActorContent (a, a.getX(), a.getY()));
        }    
        // Sort the Actor, using the ActorContent comparitor (compares by y coordinate)
        Collections.sort(acList);
        // Replace the Actors from the ActorContent list into the World, inserting them one at a time
        // in the desired paint order (in this case lowest y value first, so objects further down the 
        // screen will appear in "front" of the ones above them).
        for (ActorContent a : acList){
            Actor actor  = a.getActor();
            world.removeObject(actor);
            world.addObject(actor, a.getX(), a.getY());
        }
    }

    /**
     * <p>The prepareLanes method is a static (standalone) method that takes a list of parameters about the desired roadway and then builds it.</p>
     * 
     * <p><b>Note:</b> So far, Centre-split is the only option, regardless of what values you send for that parameters.</p>
     *
     * <p>This method does three things:</p>
     * <ul>
     *  <li> Determines the Y coordinate for each lane (each lane is centered vertically around the position)</li>
     *  <li> Draws lanes onto the GreenfootImage target that is passed in at the specified / calculated positions. 
     *       (Nothing is returned, it just manipulates the object which affects the original).</li>
     *  <li> Places the VehicleSpawners (passed in via the array parameter spawners) into the World (also passed in via parameters).</li>
     * </ul>
     * 
     * <p> After this method is run, there is a visual road as well as the objects needed to spawn Vehicles. Examine the table below for an
     * in-depth description of what the roadway will look like and what each parameter/component represents.</p>
     * 
     * <pre>
     *                  <=== Start Y
     *  ||||||||||||||  <=== Top Border
     *  /------------\
     *  |            |  
     *  |      Y[0]  |  <=== Lane Position (Y) is the middle of the lane
     *  |            |
     *  \------------/
     *  [##] [##] [##| <== spacing ( where the lane lines or borders are )
     *  /------------\
     *  |            |  
     *  |      Y[1]  |
     *  |            |
     *  \------------/
     *  ||||||||||||||  <== Bottom Border
     * </pre>
     * 
     * @param world     The World that the VehicleSpawners will be added to
     * @param target    The GreenfootImage that the lanes will be drawn on, usually but not necessarily the background of the World.
     * @param spawners  An array of VehicleSpawner to be added to the World
     * @param startY    The top Y position where lanes (drawing) should start
     * @param heightPerLane The height of the desired lanes
     * @param lanes     The total number of lanes desired
     * @param spacing   The distance, in pixels, between each lane
     * @param twoWay    Should traffic flow both ways? Leave false for a one-way street (Not Yet Implemented)
     * @param centreSplit   Should the whole road be split in the middle? Or lots of parallel two-way streets? Must also be two-way street (twoWay == true) or else NO EFFECT
     * 
     */
    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit){
        return prepareLanes (world, target, spawners, startY, heightPerLane, lanes, spacing, twoWay, centreSplit, spacing);
    }
    
    /** 
     * Set planeCount to zero
     */
    public void resetPlaneCount(){
        planeCount = 0;
    }
    
    /**
     * Reduce ped2Count by 1
     */
    public void reducePed2Count(){
        ped2Count --;
    }
}

/**
 * Container to hold and Actor and an LOCAL position (so the data isn't lost when the Actor is temporarily
 * removed from the World).
 */
class ActorContent implements Comparable <ActorContent> {
    private Actor actor;
    private int xx, yy;
    public ActorContent(Actor actor, int xx, int yy){
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public void setLocation (int x, int y){
        xx = x;
        yy = y;
    }

    public int getX() {
        return xx;
    }

    public int getY() {
        return yy;
    }

    public Actor getActor(){
        return actor;
    }

    public String toString () {
        return "Actor: " + actor + " at " + xx + ", " + yy;
    }

    public int compareTo (ActorContent a){
        return this.getY() - a.getY();
    }

}
