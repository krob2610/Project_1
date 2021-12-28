package agh.ics.oop;

import java.util.Objects;

public class Vector2d {
    /* gives position on map*/
    public final int x;
    public final int y;
    public int get_x(){return x;}
    public int get_y(){return y;}
    public Vector2d(int x, int y){
        this.x = x;
        this.y = y;
    }
    public String toString(){
        return "(" + String.valueOf(x) + "," + String.valueOf(y) + ")";
    }

    public boolean precedes(Vector2d other){
        if(this.x <= other.x && this.y <= other.y)
            return true;
        return false;
    }

    public boolean follows(Vector2d other){
        if(this.x >= other.x && this.y >= other.y)
            return true;
        return false;
    }

    public Vector2d upperRight(Vector2d other){
        int max_x = Math.max(this.x, other.x);
        int max_y = Math.max(this.y, other.y);
        return new Vector2d(max_x, max_y);
    }
    public Vector2d lowerLeft(Vector2d other){
        int min_x = Math.min(this.x, other.x);
        int min_y = Math.min(this.y, other.y);
        return new Vector2d(min_x, min_y);
    }
    public Vector2d add(Vector2d other){
        int sum_x = Math.addExact(this.x , other.x);
        int sum_y = Math.addExact(this.y , other.y);
        return new Vector2d(sum_x, sum_y);
    }
    public Vector2d subtract(Vector2d other){
        int sub_x = Math.subtractExact(this.x , other.x);
        int sub_y = Math.subtractExact(this.y , other.y);
        return new Vector2d(sub_x, sub_y);
    }
    @Override                               //Pomoga odróżnić overriding od overloadingu w czasie kompilacji
    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Vector2d))
            return false;
        Vector2d that = (Vector2d) other;           //rzutowanie
        if(this.x == that.x && this.y == that.y)
            return true;
        return false;
    }

    public Vector2d opposite(){
        return new Vector2d(x*-1, y*-1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public int compareTo() {
        return Objects.hash(this.x, this.y);
    }

}


