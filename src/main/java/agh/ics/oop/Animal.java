package agh.ics.oop;

import java.util.ArrayList;

public class Animal implements IMapElement {
    /* animal tat can move eat and make children */
    private IWorldMap map;
    private MapDirection orientation;
    private Vector2d position;
    public int stamina;
    public int staamina_max;
    private int energy_to_copulate;
    public int num_of_children;
    public int life_time;
    public Gene genes;
    public boolean isfollowed;
    public int number_of_children_from_now;
    public int number_of_descendants_from_now;
    public Animal parent_if_followed;
    @Override
    public String get_image_path() {
        /*get path for animal depending on orientation */
        if(isDead())
            return "src/main/resources/dead.png";
        switch (orientation){
            case NORTH: return "src/main/resources/up.png";
            case SOUTH: return "src/main/resources/down.png";
            case WEST: return "src/main/resources/left.png";
            case EAST:return "src/main/resources/right.png";

            case EAST_NORTH: return "src/main/resources/right_top.png";
            case EAST_SOUTH: return "src/main/resources/right_bottom.png";
            case WEST_NORTH: return "src/main/resources/left_top.png";
            case WEST_SOUTH:return "src/main/resources/left_bottom.png";
        }
        return "";
    }

    public ArrayList<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(IWorldMap map, Vector2d initialPosition, int stamina, int staamina_max){
        this.map = map;
        this.position = initialPosition;
        this.orientation = MapDirection.NORTH;
        this.stamina = stamina;
        this.staamina_max = staamina_max;
        this.genes = new Gene();
        this.num_of_children = 0;
        this.life_time=0;
        this.energy_to_copulate = staamina_max/2;
        isfollowed = false;
        number_of_children_from_now=0;
        number_of_descendants_from_now=0;
        parent_if_followed = null;
    }

    public Vector2d get_position(){
        /*get postiopn */
        return position;
    }

    public void change_orientation(int nr){
        /*change animal orientation by moving it to the left*/
        for(int i=0;i<nr;i++)
            this.move(MoveDirection.RIGHT);
    }

    public String toString(){
        switch (orientation){
            case NORTH: return "N";
            case SOUTH: return "S";
            case WEST: return "W";
            case EAST:return "E";
            case EAST_NORTH: return "NE";
            case WEST_NORTH: return "WN";
            case WEST_SOUTH: return "WS";
            case EAST_SOUTH:return "ES";
        }
        return  null;
    }

    public boolean isDead(){return this.stamina<=0;}
    /* detects if animal is dead or not */

    public void regenerate_stamina(int bonus){
        /* feed animal to max energy*/
        this.stamina +=bonus;
        if (this.stamina>staamina_max)
            this.stamina = staamina_max;
    }

    public void make_child(Animal ani){
        /*function that creates new child when 2 animals meet */
        if (this.stamina < this.energy_to_copulate || ani.stamina < this.energy_to_copulate)
            return;
        Vector2d child_pos = null;
        Vector2d [] around = {new Vector2d(0,1), new Vector2d(0,-1), new Vector2d(1,1), new Vector2d(1,-1), new Vector2d(1,0)
                , new Vector2d(-1,0), new Vector2d(-1,-1), new Vector2d(-1,1)};
        for(Vector2d v: around)
            if(map.canMoveTo(this.position.add(v)) && !(map.objectAt(this.position.add(v) ) instanceof Animal))
            {
                child_pos = this.position.add(v);
                break;
            }
        if (child_pos == null)
            return;

        int child_energy = (int) (this.stamina*1/4) + (int) (ani.stamina*1/4);
        ani.stamina = ani.stamina - (int) (ani.stamina*1/4);
        this.stamina = this.stamina - (int) (this.stamina*1/4);

        Animal child = new Animal(this.map, child_pos, child_energy, this.staamina_max);

        if(ani.stamina > this.stamina)
            child.genes = new Gene(ani, this);
        else
            child.genes = new Gene(this, ani);

        this.map.place(child);

        this.num_of_children++;
        ani.num_of_children++;


        //is followed handle
        if(this.isfollowed){
            this.number_of_children_from_now ++;
            this.number_of_descendants_from_now ++;
            child.parent_if_followed = this;
        }
        else if(ani.isfollowed){
                ani.number_of_children_from_now ++;
                ani.number_of_descendants_from_now ++;
                child.parent_if_followed = ani;
        }
        else if(this.parent_if_followed!=null){
            this.parent_if_followed.number_of_descendants_from_now++;
            child.parent_if_followed = this.parent_if_followed;
        }
        else if(ani.parent_if_followed!=null){
            ani.parent_if_followed.number_of_descendants_from_now++;
            child.parent_if_followed = ani.parent_if_followed;
        }

    }
    public void move(MoveDirection direction){

        switch (direction){

            case RIGHT: orientation = orientation.next();
                break;
            case LEFT: orientation = orientation.previous();
                break;
            case FORWARD:
                Object G_or_A = (map.objectAt(position.add(orientation.toUnitVector())));
                if (map.canMoveTo(position.add(orientation.toUnitVector())) && !(G_or_A instanceof Animal))    //o tu
                {
                    position = position.add(orientation.toUnitVector());

                }
                else if (map.canMoveTo(position.add(orientation.toUnitVector())) && (G_or_A instanceof Animal))    //o tu
                {
                    make_child((Animal) G_or_A);
                }

                break;
            case BACKWARD:
                Object G_or_A2 = (map.objectAt(position.subtract(orientation.toUnitVector()))); //sub
                if (map.canMoveTo(position.subtract(orientation.toUnitVector())) && !(G_or_A2 instanceof Animal)){
                    position = position.subtract(orientation.toUnitVector());
                }
                else if (map.canMoveTo(position.add(orientation.toUnitVector())) && (G_or_A2 instanceof Animal))    //o tu
                {
                    make_child((Animal) G_or_A2);
                }
                break;
        }
    }
    //observer
    public void addObserver(IPositionChangeObserver observer){
        observers.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        observers.remove(observer);
    }
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver o : observers) {
                o.positionChanged(oldPosition, newPosition);
            }
        }
}
