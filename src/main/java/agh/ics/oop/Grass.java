package agh.ics.oop;

public class Grass implements IMapElement {
    private Vector2d position;
    public Grass(Vector2d position){
        this.position = position;
    }
    public Vector2d get_position(){
        return position;
    }
    public String toString(){
        return "*";
    }

    @Override
    public String get_image_path() {
        return "src/main/resources/grass.png";
    }
}
