import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * This is the superclass for Vehicles.
 * 
 */
public abstract class Vehicle extends SuperSmoothMover
{
    protected double maxSpeed;
    protected double speed;
    protected int direction; // 1 = right, -1 = left
    protected boolean moving;
    protected boolean isNew;
    protected int yOffset;
    protected VehicleSpawner origin;
    protected int followingDistance;
    protected int myLaneNumber;
    protected boolean towing;
    protected boolean towed;
    protected Vehicle tower;
    private VehicleWorld vw;

    protected abstract boolean checkHitPedestrian ();

    public Vehicle (VehicleSpawner origin) {
        // remember the VehicleSpawner I came from. This includes information
        // about which lane I'm in and which direction I should face
        this.origin = origin;
        moving = true;
        towing = false;
        // ask the Spawner that spawned me what my lane number is
        myLaneNumber = origin.getLaneNumber();
        // Determine if this lane is facing towards the right and
        // set the direction accordingly
        if (origin.facesRightward()){ // Right facing vehicles
            direction = 1;        
        } else { // left facing Vehicles
            direction = -1;
            // Reverse the image so it appears correct when moving the opposite direction
            getImage().mirrorHorizontally();
        }
        // If speed modifiers were set for lanes, this will change the max speed
        // accordingly. If speed modifiers are not set, this multiplies by 1.0 (as in,
        // does nothing).
        maxSpeed *= origin.getSpeedModifier();
        speed = maxSpeed;

        isNew = true; // this boolean serves to make sure this Vehicle is only placed in 
        // it's starting position once. Vehicles are removed and re-added
        // to the world (instantly, not visibly) by the z-sort, and without this,
        // they would continue to return to their start points.
    }

    /**
     * This method is called automatically when the Vehicle is added to the World, and places
     * the Vehicle just off screen (centered 100 pixels beyond the center of the lane spawner)
     * so it will appear to roll onto the screen smoothly.
     */
    public void addedToWorld (World w){
        if (isNew){
            setLocation (origin.getX() - (direction * 100), origin.getY() - yOffset);
            isNew = false;
        }
        VehicleWorld vw = (VehicleWorld) w;
    }

    /**
     * The superclass Vehicle's act() method. This can be called by a Vehicle subclass object 
     * (for example, by a Car) in two ways:
     * - If the Vehicle simply does NOT have a method called public void act(), this will be called
     *   instead. 
     * - subclass' act() method can invoke super.act() to call this, as is demonstrated here.
     */
    public void act () {
        Pedestrian front = (Pedestrian) getOneObjectAtOffset (20*direction, 0,Pedestrian.class);
        if (moving) {
            drive();
        }

        if (towed) {
            if(tower != null){
                attachTower(tower);
            } else {
                getWorld().removeObject(this);
            }
        }

        if (!checkHitPedestrian()){
            repelPedestrians();
        }

        if(checkCrash() && moving){
            crash();
        }

        if (checkEdge()){
            getWorld().removeObject(this);
            return;
        }
        
        
    }

    /**
     * A method used by all Vehicles to check if they are at the edge.
     * 
     * Note that this World is set to unbounded (The World's super class is (int, int, int, FALSE) which means
     * that objects should not be stopped from leaving the World. However, this introduces a challenge as there
     * is the potential for objects to disappear off-screen but still be fully acting and thus wasting resources
     * and affecting the simulation even though they are not visible.
     */
    protected boolean checkEdge() {
        if (direction == 1)
        { // if moving right, check 200 pixels to the right (above max X)
            if (getX() > getWorld().getWidth() + 200){
                return true;
            }
        } 
        else 
        { // if moving left, check 200 pixels to the left (negative values)
            if (getX() < -200){
                return true;
            }
        }
        return false;
    }

    // The Repel Pedestrian Experiment - Currently a work in Progress (Feb 2023)
    public void repelPedestrians() {
        ArrayList<Pedestrian> pedsTouching = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);

        ArrayList<Actor> actorsTouching = new ArrayList<Actor>();

        // this works, but doesn't ignore knocked down Pedestrians
        //actorsTouching.addAll(pedsTouching);
        for (Pedestrian p : pedsTouching){
            if (p.isAwake()){
                actorsTouching.add(p);
            }
        }

        pushAwayFromObjects(actorsTouching, 4);
    }

    /**
     * New repel method! Seems to work well. Can be used in both directions, but for now
     * commented out movement on x so players are only "repelled" in a y-direction.
     * 
     * @author Mr Cohen
     * @since February 2023
     */
    public void pushAwayFromObjects(ArrayList<Actor> nearbyObjects, double minDistance) {
        // Get the current position of this actor
        int currentX = getX();
        int currentY = getY();

        // Iterate through the nearby objects
        for (Actor object : nearbyObjects) {
            // Get the position and bounding box of the nearby object
            int objectX = object.getX();
            int objectY = object.getY();
            int objectWidth = object.getImage().getWidth();
            int objectHeight = object.getImage().getHeight();

            // Calculate the distance between this actor and the nearby object's bounding oval
            double distance = Math.sqrt(Math.pow(currentX - objectX, 2) + Math.pow(currentY - objectY, 2));

            // Calculate the effective radii of the bounding ovals
            double thisRadius = Math.max(getImage().getWidth() / 2.0, getImage().getHeight() / 2.0);
            double objectRadius = Math.max(objectWidth / 2.0, objectHeight / 2.0);

            // Check if the distance is less than the sum of the radii
            if (distance < (thisRadius + objectRadius + minDistance)) {
                // Calculate the direction vector from this actor to the nearby object
                int deltaX = objectX - currentX;
                int deltaY = objectY - currentY;

                // Calculate the unit vector in the direction of the nearby object
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double unitX = deltaX / length;
                double unitY = deltaY / length;

                // Calculate the amount by which to push the nearby object
                double pushAmount = (thisRadius + objectRadius + minDistance) - distance;

                // Update the position of the nearby object to push it away

                object.setLocation(objectX, objectY + (int)(pushAmount * unitY));

                // 2d version, allows pushing on x and y axis, commented out for now but it works, just not the
                // effect I'm after:
                //object.setLocation(objectX + (int)(pushAmount * unitX), objectY + (int)(pushAmount * unitY));
            }
        }
    }

    /**
     * Method that deals with movement. Speed can be set by individual subclasses in their constructors
     */
    public void drive() 
    {
        move (speed * direction);
        Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
        if (ahead != null && getSpeed() > ahead.getSpeed() && !(this instanceof Truck)){
            int[] availableLanes = checkAvailbleLanes();
            int[] clearLanes = laneIsClear(availableLanes);
            changeLane(clearLanes);
        }
    }   

    /**
     * An accessor that can be used to get this Vehicle's speed. Used, for example, when a vehicle wants to see
     * if a faster vehicle is ahead in the lane.
     */
    public double getSpeed(){
        if (moving){
            return speed;
        }
        return 0;
    }

    // new stuff
    /**
     * Tell the vehicle that it is being towed and by who
     * 
     * @param towtruck The tow truck that is towing this vehicle
     */
    public void getTowed(Vehicle towtruck) {
        towing = true;
        towed = true;
        tower = towtruck;
    }

    /**
     * Return if the vehicle is crashed
     */
    public boolean isNotCrashed() {
        return moving;
    }

    /**
     * Check if the vehicle is colliding with another vehicle
     */
    public boolean checkCrash() {
        Vehicle collidingObject = (Vehicle)getOneIntersectingObject(Vehicle.class);
        if (collidingObject != null && !towing){
            return true;
        }
        return false;
    }

    /**
     * A method that set the location of the vehicle to some distance
     * behind the tow truck
     * 
     * @param tower The tow truck that is towing this vehicle
     */
    public void attachTower(Vehicle tower) {
        try {
            int towX = tower.getX();
            int selfX = getX();
            int selfY = getY();
            if (towX-100 != (direction * selfX) && origin.facesRightward()){
                setLocation(towX-100, selfY);
            } else if (towX+100 != (direction * selfX) && !origin.facesRightward()){
                setLocation(towX+100, selfY);
            }
        } catch(Exception e) {
            if (e.toString() == "java.lang.IllegalStateException: Actor has been removed from the world.") {
                getWorld().removeObject(this);
            }
        }
    }
    
    /**
     * Tell the vehicle that it crahsed
     */
    public void crash() {
        moving = false;
    }

    public void explode() {
        getWorld().addObject(new VehicleExplosion(),getX(),getY());
    }
    
    // switching lane stuff
    /**
     * Checks if there is a lane on the left and right
     * instead of opposite lane or grass
     */
    public int[] checkAvailbleLanes(){
        vw = (VehicleWorld) getWorld();
        int leftLane = myLaneNumber - (1*direction);
        int rightLane = myLaneNumber + (1*direction);
        int barrier;
        int[] available = {0,0};
        if (vw.twoWayTraffic && direction == 1){
            barrier = vw.laneCount/2 - 1;
        } else if (direction == -1){
            barrier = vw.laneCount/2;
        } else {
            barrier = -1;
        }
        if (vw.getLaneY(leftLane) != -1 && leftLane != barrier){
            available[0] = 1;
        }
        if (vw.getLaneY(rightLane) != -1){
            available[1] = 1;
        }
        return available;
    }
    
    /**
     * Check if there is a vehicle on the available lanes
     * 
     * @param availableLanes An integer array of available lanes
     */
    public int[] laneIsClear(int[] availableLanes){
        int[] clear = {0,0};
        Vehicle left = (Vehicle) getOneObjectAtOffset (0,-48*direction,Vehicle.class);
        Vehicle right = (Vehicle) getOneObjectAtOffset (0,48*direction,Vehicle.class);
        if (availableLanes[0] == 1 && left == null){
            clear[0] = 1;
        }
        if (availableLanes[1] == 1 && right == null){
            clear[1] = 1;
        }
        return clear;
    }
    
    /**
     * Set myLaneNumber and Y value to the new lane
     * Prioritize left above right
     * 
     * @param clearLanes An integer array of clear lanes
     */
    public void changeLane(int[] clearLanes){
        if (clearLanes[0] == 1){
            myLaneNumber = myLaneNumber - (1*direction);
            setLocation(getX(), vw.getLaneY(myLaneNumber));
        } else if (clearLanes[1] == 1){
            myLaneNumber = myLaneNumber + (1*direction);
            setLocation(getX(), vw.getLaneY(myLaneNumber));
        }
    }
}