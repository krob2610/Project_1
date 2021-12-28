package agh.ics.oop;

import java.util.Arrays;
import java.util.Objects;

import static agh.ics.oop.Directions.*;


public class OptionsParser {
    public static MoveDirection[] parse(String []args){
        MoveDirection [] dir = new MoveDirection[args.length];
        for(int i=0;i<args.length;i++){
            switch(args[i])
            {
                case "f" :
                case "forward":
                    dir[i] = MoveDirection.FORWARD;
                    break;
                case "b" :
                case "backward":
                    dir[i] = MoveDirection.BACKWARD;
                    break;
                case "r" :
                case "right":
                    dir[i] = MoveDirection.RIGHT;
                    break;
                case "l" :
                case "left":
                    dir[i] = MoveDirection.LEFT;
                    break;
                default:
                    throw new IllegalArgumentException(args[i] + " is not legal move specification");
            }
        }
        MoveDirection[] clone_dir = Arrays.stream(dir).filter(Objects::nonNull).toArray(MoveDirection[]::new);
        return clone_dir;
    }
}
