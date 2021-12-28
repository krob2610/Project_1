package agh.ics.oop;
import agh.ics.oop.gui.App;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.Random;


public class SimulationEngine implements  Runnable{
    /* runable, run the simulation */
    RectangularMap map;
    IPositionChangeObserver observer;
    ArrayList<Animal> animalz = new ArrayList<>();
    private Boolean stop = false;

    public SimulationEngine(RectangularMap map){
        this.map = map;
        this.observer = (IPositionChangeObserver) map;
        for(Animal ani: map.arena_map.values())
            animalz.add(ani);

    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }
    public void run() {
        if (!stop) {

            for (Animal ani : animalz)
                ani.addObserver(observer);

            for (Animal ani : animalz) {
                int rnd = new Random().nextInt(ani.genes.Genes_arr.length);
                int ani_move = ani.genes.Genes_arr[rnd];
                Vector2d oldPosition = ani.get_position();
                if (ani_move == 0) {
                    ani.move(MoveDirection.FORWARD);
                } else if (ani_move == 4) {
                    ani.move(MoveDirection.BACKWARD);
                } else
                    ani.change_orientation(ani_move);
                Vector2d newPosition = ani.get_position();
                ani.positionChanged(oldPosition, newPosition);
                ani.stamina--;
            }
            for (Animal ani : animalz)
                ani.removeObserver(observer);

            this.map.another_day();
            animalz.removeAll(animalz);
            for (Animal ani : map.arena_map.values())
                animalz.add(ani);
            this.map.generate_grass();

            Platform.runLater(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
                try {
                    App.update_grid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }
}
