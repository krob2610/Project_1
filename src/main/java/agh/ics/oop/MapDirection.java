package agh.ics.oop;

public enum MapDirection {
    NORTH,
    EAST_NORTH,
    WEST_NORTH,
    SOUTH,
    EAST_SOUTH,
    WEST_SOUTH,
    EAST,
    WEST;

    public String toString(){
        switch(this) {
            case NORTH: return "Północ";
            case SOUTH: return "Południe";
            case EAST: return "Wschód";
            case WEST: return "Zachód";
            case EAST_NORTH: return "EAST_NORTH";
            case WEST_NORTH: return "WEST_NORTH";
            case EAST_SOUTH: return "EAST_SOUTH";
            case WEST_SOUTH: return "WEST_SOUTH";
        }
        return null;
    }

    public MapDirection next(){
        switch (this){
            case NORTH: return EAST_NORTH;
            case SOUTH: return WEST_SOUTH;
            case EAST: return EAST_SOUTH;
            case WEST: return WEST_NORTH;
            case EAST_NORTH: return EAST;
            case WEST_NORTH: return NORTH;
            case EAST_SOUTH: return SOUTH;
            case WEST_SOUTH: return WEST;
        }
        return null;
        //return values()[(ordinal()+1) % values().length];
    }
    public MapDirection previous(){
        switch (this){
            case NORTH: return WEST_NORTH;
            case SOUTH: return EAST_SOUTH;
            case EAST: return EAST_NORTH;
            case WEST: return WEST_SOUTH;
            case EAST_NORTH: return NORTH;
            case WEST_NORTH: return WEST;
            case EAST_SOUTH: return EAST;
            case WEST_SOUTH: return SOUTH;
        }
        return null;   //gdy null
        //return values()[(ordinal()-1) % values().length];
    }
    public Vector2d toUnitVector(){
        switch(this) {
            case NORTH: return new Vector2d(0,1);
            case SOUTH: return new Vector2d(0,-1);
            case EAST: return new Vector2d(1,0);
            case WEST: return new Vector2d(-1,0);
            case EAST_NORTH: return new Vector2d(1,1);
            case WEST_NORTH: return new Vector2d(-1,1);
            case EAST_SOUTH: return new Vector2d(1,-1);
            case WEST_SOUTH: return new Vector2d(-1,-1);
        }
        return null;
    }
}