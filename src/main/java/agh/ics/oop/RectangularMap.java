package agh.ics.oop;

import agh.ics.oop.gui.App;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

//na poczatku z borderami
public class RectangularMap implements IWorldMap, IPositionChangeObserver{
    public LinkedHashMap<Vector2d, Animal> arena_map = new LinkedHashMap<>();
    protected LinkedHashMap<Vector2d, Grass> arena_grass_map = new LinkedHashMap<>();
    public int width, height;
    int jungle_widht, jungle_height;
    public int Jungle_widht_start, Jungle_widht_end;
    public int Jungle_height_start, Jungle_height_end;
    int jungle_size, savana_size;
    int grass_in_savana, grass_in_jungle;
    int Animal_Stamina_max, Animal_stamina_start;
    int meal_bonus;
    int grass_each_day, animal_at_start;
    public float average_life_time, average_number_of_children, average_energy;
    public int total_number_of_animals, total_number_of_grass;
    private int number_of_dead_animals, total_life_time;
    public int day_counter;
    public int Dominating_genes [];
    private int magic_spawn;
    public RectangularMap(int width,int height, int jungle_widht, int jungle_height, int Animnal_stamina_start,
                          int Animal_stamina_max, int meal_bonus, int grass_each_day, int animal_at_start, boolean magic_evolution){
        this.width = width;
        this.height = height;
        this.jungle_widht = jungle_widht;
        this.jungle_height = jungle_height;
        this.Jungle_widht_start = (int)((width - jungle_widht)/2)+1;
        this.Jungle_widht_end = Jungle_widht_start +jungle_widht;
        this.Jungle_height_start = (int)((height - jungle_height))/2+1;
        this.Jungle_height_end = Jungle_height_start +jungle_height;
        this.grass_in_savana=0;
        this.grass_in_jungle = 0;
        this.jungle_size = jungle_height*jungle_widht;
        this.savana_size = (width*height) - this.jungle_size;
        this.Animal_Stamina_max = Animal_stamina_max;
        this.Animal_stamina_start = Animnal_stamina_start;
        this.meal_bonus = meal_bonus;
        this.grass_each_day =  grass_each_day;
        this.animal_at_start = animal_at_start;
        this.day_counter=0;
        if(magic_evolution)
            magic_spawn = 0;
        else
            magic_spawn = 4;
        add_random_Animals();
    }

    public boolean canMoveTo(Vector2d position) {
        /*check if animal can move or there is end of map */
        if (position.x<=width && position.x>=0 && position.y>=0 && position.y<=height)
            return true;
        return false;
    }


    public LinkedHashMap<Vector2d, Animal> get_arena_map(){
        return arena_map;
    }
    public LinkedHashMap<Vector2d, Grass> get_arena_grass_map(){
        return arena_grass_map;
    }
    public boolean place(Animal animal)
    {
        /*put animal on the map */
        if (canMoveTo(animal.get_position()) && !(this.objectAt(animal.get_position()) instanceof Animal)){
            arena_map.put(animal.get_position(), animal);
            return true;
        }
        throw new IllegalArgumentException(animal.get_position() + "is not legal field to place animal");
    };

    public boolean isOccupied(Vector2d position){
        /*check if field in some position is free or not */
        if (arena_map.get(position) == null)
            if (arena_grass_map.get(position) == null)
                return false;
        return true;
    }

    public Object objectAt(Vector2d position){
        /* return object at some position */
        if (arena_map.get(position)!=null)
            return arena_map.get(position);
        return arena_grass_map.get(position);
    }
    public String toString(){
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(new Vector2d(0,0), new Vector2d(width, height));
    }
    public void generate_grass(){
        /* funtion generating grass each day */
        //dla stepow
        int counter=0;

        for (int i=0; i<this.grass_each_day;i++){

            Vector2d pos = new Vector2d(ThreadLocalRandom.current().nextInt(0, width + 1),
                    ThreadLocalRandom.current().nextInt(0, height + 1));

            if(pos.x>= Jungle_widht_start && pos.x< Jungle_widht_end && pos.y>=Jungle_height_start && pos.y <Jungle_height_end)
                i--;
            else if (objectAt(pos) == null){
                Grass grass = new Grass(pos);
                arena_grass_map.put(pos, grass);
                counter=0;
            }
            else{
                i--;
                counter++;
            }

            if(counter == 150){
                break;
            }
        }
        counter =0;
        for (int i=0; i<this.grass_each_day;i++){

            Vector2d pos = new Vector2d(ThreadLocalRandom.current().nextInt(Jungle_widht_start, Jungle_widht_end),
                    ThreadLocalRandom.current().nextInt(Jungle_height_start, Jungle_height_end ));

            if (objectAt(pos) == null){
                Grass grass = new Grass(pos);
                arena_grass_map.put(pos, grass);
                //grass_in_jungle++;
                counter =0;
            }
            else{
                i--;
                counter++;
            }
            if(counter == 150){
                break;
            }
        }
    }
    public void add_random_Animals(){
        /* add random animals to grid at the start of simulation */
        int counter =0;
        for (int i=0; i<this.animal_at_start;i++){
            Vector2d pos = new Vector2d(ThreadLocalRandom.current().nextInt(0, width+1),
                    ThreadLocalRandom.current().nextInt(0, height+1 ));

            if (!(objectAt(pos) instanceof Animal)){ //można dodać na grassie ale wtedy on ciągle tam zostaje
                Animal ani = new Animal(this, pos, this.Animal_stamina_start, this.Animal_Stamina_max);
                arena_map.put(pos, ani);
                counter =0;
            }
            else{
                i--;
                counter++;
            }
            if(counter == 50){
                break;
            }
        }

    }

    public void add_five_Animals(){
        /*magic evolution function - add 5 new animals with the same genome as those who are on the map
        * it can happen max 3 times*/
        ArrayList <int []> genes= new ArrayList<>();
        for (Animal ani: this.arena_map.values())
            genes.add(ani.genes.Genes_arr);

        int counter =0;
        for (int i=0; i<5;i++){
            Vector2d pos = new Vector2d(ThreadLocalRandom.current().nextInt(0, width+1),
                    ThreadLocalRandom.current().nextInt(0, height+1 ));

            if (!(objectAt(pos) instanceof Animal)){ //można dodać na grassie ale wtedy on ciągle tam zostaje
                Animal ani = new Animal(this, pos, this.Animal_Stamina_max, this.Animal_Stamina_max);
                ani.genes.Genes_arr = genes.get(i);
                arena_map.put(pos, ani);
                //grass_in_jungle++;
                counter =0;
            }
            else{
                i--;
                counter++;
            }
            if(counter == 50){
                // System.out.println("to much grass in the jungle");
                break;
            }
        }
        System.out.println("magiczna evolucja");
    }

    public void remove_dead_body() {
        /* remove dead body of dead animals from map */
        ArrayList<Vector2d> dead_body = new ArrayList<>();
        for (Animal ani: arena_map.values()) {
           // Animal ani = arena_map.get(i);
            if (ani.isDead()) {
               // System.out.println("umarlo" + ani.get_position());
                //ani.removeObserver(this);
                dead_body.add(ani.get_position());
            }
        }
        for(Vector2d pos: dead_body){
            this.total_life_time += arena_map.get(pos).life_time; //ile
            this.number_of_dead_animals ++;
            arena_map.remove(pos);
        }

    }
    public void eat() {
        /* thigns related with eating - removing grass and feeding the aniaml */
        ArrayList<Vector2d> grass_to_remove = new ArrayList<>();
      for (Vector2d meal_pos : arena_grass_map.keySet()) {
            if(arena_map.containsKey(meal_pos))      //czyli zwierze zmieniło pozycje na taka gdzie jest trawa wiec trzeba ja usunąć i dodać stamine temu zwierzęciu
            {
                grass_to_remove.add(meal_pos);
                arena_map.get(meal_pos).regenerate_stamina(this.meal_bonus);
            }
        }
      for (Vector2d pos: grass_to_remove)
      {
          arena_grass_map.remove(pos);
      }
    }
    public void make_animals_older(){
        /*make animals older by one day*/
        for(Animal ani: arena_map.values())
            ani.life_time++;
    }
    public void calculate_average_lifetime(){
        /*function that calculate average lifetime of dead animals*/
        if(this.number_of_dead_animals==0)
            this.average_life_time= 0;
        else
            this.average_life_time = this.total_life_time/this.number_of_dead_animals;
    }
    public void calculate_average_energy(){
        /*function that calculate average energy of living animals*/
        int total_energy = 0;
        for(Animal ani: arena_map.values())
            total_energy+=ani.stamina;
        if(this.total_number_of_animals==0)
            this.average_energy = 0;
        else
            this.average_energy = total_energy/this.total_number_of_animals;
    }
    public void calculate_average_numberofchildren(){
        /*function that calculate average number of children of living animals*/
        int number_of_children=0;
        for(Animal ani: arena_map.values())
            number_of_children += ani.num_of_children;
        if(this.total_number_of_animals==0)
            this.average_number_of_children = 0;
        else
            this.average_number_of_children = number_of_children/this.total_number_of_animals;
    }
    public void another_day(){
        /*day cycle*/
        eat();
        remove_dead_body();
        if(total_number_of_animals == 5)
            if(magic_spawn < 3)
            {
                magic_spawn++;
                add_five_Animals();
            }

        this.total_number_of_animals = arena_map.size();
        this.total_number_of_grass = arena_grass_map.size();
        calculate_average_energy();
        calculate_average_lifetime();
        calculate_average_numberofchildren();
        dominating_Genome();

        make_animals_older();
        day_counter++;
    }
    public void dominating_Genome(){
        /*find dominating genes */
        this.Dominating_genes = null;
        ArrayList<int []> aray_of_genome = new ArrayList<>();

        for(Animal ani: arena_map.values())
            aray_of_genome.add(ani.genes.Genes_arr);
        int [] counter_of_occures = new int[aray_of_genome.size()];
        for(int i=0; i< aray_of_genome.size(); i++){
            for(int j=0; j< aray_of_genome.size(); j++){
                if(i!=j){
                    if(aray_of_genome.get(i) == aray_of_genome.get(j))
                        counter_of_occures[i]++;
                }
            }
        }
        int max = 0;
        for(int i=0;i<counter_of_occures.length; i++)
            if(counter_of_occures[i]>max)
                max=counter_of_occures[i];

        if(max!=0)
            this.Dominating_genes = aray_of_genome.get(max);
        else
            this.Dominating_genes = null;
    }
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        /* inform if position was changed*/

            Object a_g = objectAt(oldPosition);
            if (a_g instanceof Animal) {
                arena_map.remove(oldPosition);
                arena_map.put(newPosition, (Animal) a_g);
            }
    };
}
