package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class App extends Application {

    static SimulationEngine engine;
    static Stage stage;
    static int num_of_animals;
    public static boolean stop;
    public static GridPane gridPane;
    static RectangularMap map;
    static Thread thread;
    static LineChart<Number,Number> lineChart;
    static XYChart.Series number_of_animals, number_of_grass, average_age,average_stamina, average_number_of_children;
    static int x_val, y_val;
    static BorderPane root;
    static Animal followed_animal;
    static int last_day_alive = 0;
    static int move_deley_;
    static Boolean can_save;
    static ArrayList<String []> data = new ArrayList<>();

    Boolean magical_evolution;

    public void init(){
        /*setting some inital values*/
        data.add(new String[]{"day", "number of animals", "number of grass", "average energy", "average life time", "average number of children"});
        followed_animal = null;
        stop=false;
    }

    public void start(Stage primaryStage) throws Exception {
        /* starting simulation, setting stage, displaying menu */
        stage = primaryStage;
        menu();

        //closing window
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    public static HBox stop_game(){
        /*function to stop the game when button pressed */
        Button button = new Button("STOP");
        button.setOnAction(e ->
        {
            stop = true;
            engine.setStop(true);
        }
        );
        HBox hBox = new HBox(button);
        hBox.setPadding(new Insets(30, 30, 30, 30));

        hBox.setAlignment(Pos.BOTTOM_LEFT);
        return hBox;
    }


    public static HBox start_game(){
        /*function to start game when button presswed */

        Button button = new Button("START");
        button.setOnAction(e ->
                {
                    engine.setStop(false);
                    stop = false;
                    run_again();
                }
        );
        HBox hBox = new HBox(button);
        hBox.setPadding(new Insets(30, 30, 30, 30));
        hBox.setAlignment(Pos.BOTTOM_LEFT);
        return hBox;
    }


    public static void update_grid() throws Exception{
        /*function that updates gui every time animals make move*/

        can_save = true;    // can add row to csv only onece
        BorderPane root = new BorderPane();

        //displaing button stop or start
        if(!stop){
            root.setCenter(stop_game());
        }
        if (stop) {
            root.setCenter(start_game());
        }
            gridPane = set_grid();
            add_data_to_chart(map.day_counter, map.total_number_of_animals, map.total_number_of_grass, map.average_life_time, map.average_number_of_children, map.average_energy);
            root.setRight(gridPane);
            VBox v;
            if(followed_animal==null)
                v = lineStats();
            else{
                  v = followed_panel(lineStats());
            }
            //buton handling saving to csv
            Button to_csv = new Button("SAVE TO CSV");
            to_csv.setOnAction(e ->
            {   if(can_save) {
                can_save = false;
                try {
                    add_to_data();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            });

            if(stop){       //only shown when stop= true
                v.getChildren().add(new Label(find_dominating_animals()));
                v.getChildren().add(to_csv);
            }
                root.setLeft(v);
            if(!stop)       //only wait when not paused
            try {
                Thread.sleep(move_deley_);
            } catch (InterruptedException ex) {
                System.out.println("main thread interrupted");
            }
        Scene scene = new Scene(root, 1500, 900);
        stage.setScene(scene);
        stage.show();
        if(stop)
            root.setOnMouseClicked(new EventHandler<MouseEvent>() { //click to select animal to follow and get more information when simulation paused
                @Override
                public void handle(MouseEvent event) {
                    if(map.arena_map.get(new Vector2d(x_val, y_val))!=null) {
                        HBox selected_animal = new HBox();  //selected animal panel
                        selected_animal.getChildren().add(new Label("Selected Animal Genome: " + map.arena_map.get(new Vector2d(x_val, y_val)).genes.print_gene()+ "<-- This Animal is followed"));
                        follow(map.arena_map.get(new Vector2d(x_val, y_val)));
                        VBox center_v = lineStats();

                        center_v.getChildren().add(selected_animal);
                        center_v.getChildren().add(new Label(find_dominating_animals()));
                        center_v.getChildren().add(to_csv);

                        root.setLeft(center_v);
                        scene.setRoot(root);
                    }
                }
            });

        run_again();
    }
    public static void add_to_data() throws IOException {
        /* function adding data to CSV file */

        //data to add
        String [] row = {String.valueOf(map.day_counter), String.valueOf(map.total_number_of_animals), String.valueOf(map.total_number_of_grass), String.valueOf(map.average_energy), String.valueOf(map.average_life_time), String.valueOf(map.average_number_of_children)};
        data.add(row);
        FileWriter fw = new FileWriter("src/main/java/agh/ics/oop/statistics.csv",false);
        BufferedWriter writer = new BufferedWriter( fw );
        for(String [] r: data)
        {
            String csv = String.join(",", r);
            writer.write(csv);
            writer.newLine();
        }
        //calculating average values (not for day)
        float [] average_values = new float[6];
        average_values[0] = 0; // -> bo to jest dzień

        for(int i = 1; i< data.size(); i++)
        {
            average_values[1] += Float.parseFloat(data.get(i)[1]);
            average_values[2] += Float.parseFloat(data.get(i)[2]);
            average_values[3] += Float.parseFloat(data.get(i)[3]);
            average_values[4] += Float.parseFloat(data.get(i)[4]);
            average_values[5] += Float.parseFloat(data.get(i)[5]);
        }
        String [] av_v = {"none", String.valueOf(average_values[1]/(data.size()-1)),String.valueOf(average_values[2]/(data.size()-1)),
                String.valueOf(average_values[3]/(data.size()-1)), String.valueOf(average_values[4]/(data.size()-1)), String.valueOf(average_values[5]/(data.size()-1))};
        writer.write(String.join(",", av_v));

        //closing
        writer.close();
        fw.close();
    }

    public static VBox followed_panel(VBox linestat){
        /*function that return panel of followed animal */
        HBox hbox = new HBox();
        hbox.getChildren().add(new Label("Selected Animal Genome: " +followed_animal.genes.print_gene()+ "<-- This Animal is followed"));

        linestat.getChildren().add(hbox);
        linestat.getChildren().add(new Label("is dead: " + followed_animal.isDead()));

        if(!(followed_animal.isDead())){
            linestat.getChildren().add(new Label("pos of animal: " + "(" +followed_animal.get_position().x + ", "+ followed_animal.get_position().y + ")"));
            last_day_alive = map.day_counter;
        }

        if(followed_animal.isDead())
            linestat.getChildren().add(new Label("Dead from: "+ (last_day_alive+1)));
        linestat.getChildren().add(new Label("Number of children: " +  followed_animal.number_of_children_from_now));
        linestat.getChildren().add(new Label("Number of descendant: "+ followed_animal.number_of_descendants_from_now));

        return linestat;
    }
    public static VBox lineStats(){
        /*helper function that conectl line chart with other components */
        VBox v = new VBox();
        v.getChildren().add(lineChart);
        Label label;
        if(map.Dominating_genes!=null)
            label = new Label("Dominating Genes: " + Arrays.toString(map.Dominating_genes));
        else
            label = new Label("There is no Dominating Gene");
        label.setFont(Font.font("Arial", 15));
        v.getChildren().add(label);
        v.setPadding(new Insets(5, 50, 5, 50));
        return v;
    }
    public static void run_again(){
        /* function that create new thread and start it*/
        thread = new Thread (engine);
        thread.setDaemon(true);
        thread.start();

    }

    public void menu(){
        /* function thats set up menu - gets user input, then start simulation */
        BorderPane root = new BorderPane();

        //seting textFields
        TextField moveDelay = new TextField();
        moveDelay.setPromptText("set move deley");
        TextField starting_number_of_animals = new TextField();
        TextField height = new TextField();
        TextField width = new TextField();
        TextField jungle_height = new TextField();
        TextField jungle_width = new TextField();
        TextField grass_regen = new TextField();
        TextField stamina_to_make_baby = new TextField();
        TextField starting_stamina = new TextField();
        TextField max_stamina = new TextField();
        TextField grass_per_day = new TextField();

        //sizes of textfields
        moveDelay.setMaxSize(100, 100);
        height.setMaxSize(100, 100);
        width.setMaxSize(100, 100);
        starting_number_of_animals.setMaxSize(100, 100);
        jungle_height.setMaxSize(100, 100);
        jungle_width.setMaxSize(100, 100);
        grass_regen.setMaxSize(100, 100);
        stamina_to_make_baby.setMaxSize(100, 100);
        starting_stamina.setMaxSize(100, 100);
        max_stamina.setMaxSize(100, 100);
        grass_per_day.setMaxSize(100, 100);

        //magical evolution
        CheckBox magical = new CheckBox("Magical Evolution");
        magical.setIndeterminate(false);

        //map
        Label height_l = new Label("Height:");
        height_l.setFont(Font.font("Arial", 20));
        height_l.setMinSize(200, 20);

        Label width_l = new Label("Width:");
        width_l.setFont(Font.font("Arial", 20));
        width_l.setMinSize(200, 20);

        Label jungle_height_l = new Label("Jungle Height:");
        jungle_height_l.setFont(Font.font("Arial", 20));
        jungle_height_l.setMinSize(200, 20);

        Label jungle_width_l = new Label("Jungle Width:");
        jungle_width_l.setFont(Font.font("Arial", 20));
        jungle_width_l.setMinSize(200, 20);

        //spawn
        Label starting_number_of_animals_l = new Label("Number Of Animals:");
        starting_number_of_animals_l.setFont(Font.font("Arial", 20));
        starting_number_of_animals_l.setMinSize(200, 20);

        Label grass_per_day_l = new Label("Grass Per Day:");
        grass_per_day_l.setFont(Font.font("Arial", 20));
        grass_per_day_l.setMinSize(200, 20);

        //Stamina
        Label starting_stamina_l = new Label("Starting Stamina :");
        starting_stamina_l.setFont(Font.font("Arial", 20));
        starting_stamina_l.setMinSize(200, 20);

        Label max_stamina_l = new Label("Max Stamina:");
        max_stamina_l.setFont(Font.font("Arial", 20));
        max_stamina_l.setMinSize(200, 20);

        Label grass_profit_l= new Label("Grass Profit:");
        grass_profit_l.setFont(Font.font("Arial", 20));
        grass_profit_l.setMinSize(200, 20);

        //Time

        Label move_deley_l= new Label("Move Deley:");
        move_deley_l.setFont(Font.font("Arial", 20));
        move_deley_l.setMinSize(200, 20);



        //map
        HBox height_box = new HBox(height_l, height);
        height_box.setPadding(new Insets(5, 50, 5, 50));

        HBox width_box = new HBox(width_l, width);
        width_box.setPadding(new Insets(0, 50, 5, 50));
        //height_box.setPrefWidth(40);

        HBox jungle_height_box = new HBox(jungle_height_l, jungle_height);
        jungle_height_box.setPadding(new Insets(0, 50, 5, 50));

        HBox jungle_width_box = new HBox(jungle_width_l, jungle_width);
        jungle_width_box.setPadding(new Insets(0, 50, 5, 50));

        //spawn
        HBox animals_spawn_box = new HBox(starting_number_of_animals_l, starting_number_of_animals);
        animals_spawn_box.setPadding(new Insets(5, 50, 5, 50));

        HBox grass_spawn_box = new HBox(grass_per_day_l, grass_per_day);
        grass_spawn_box.setPadding(new Insets(0, 50, 5, 50));

        //stamina
        HBox starting_stamina_box = new HBox(starting_stamina_l, starting_stamina);
        starting_stamina_box.setPadding(new Insets(5, 50, 5, 50));

        HBox max_stamina_box = new HBox(max_stamina_l, max_stamina);
        max_stamina_box.setPadding(new Insets(0, 50, 5, 50));

        HBox grass_profit_box = new HBox(grass_profit_l, grass_regen);
        grass_profit_box.setPadding(new Insets(0, 50, 5, 50));

        //time
        HBox move_deley_box = new HBox(move_deley_l, moveDelay);
        move_deley_box.setPadding(new Insets(0, 50, 5, 50));

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(30, 50, 50, 50));
        vbox.setSpacing(10);

        //setting fonts
        Text title = new Text("EVOLUTION GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 50));

        Text map_cat = new Text("Map:");
        map_cat.setFont(Font.font("Arial", 30));

        Text spawn_cat = new Text("Spawn:");
        spawn_cat.setFont(Font.font("Arial", 30));

        Text stamina_cat = new Text("Stamina:");
        stamina_cat.setFont(Font.font("Arial", 30));

        Text time_cat = new Text("Time:");
        time_cat.setFont(Font.font("Arial", 30));

        //adding elements to vbox
        vbox.getChildren().add(title);

        vbox.getChildren().add(map_cat);
            vbox.getChildren().add(height_box);
            vbox.getChildren().add(width_box);
            vbox.getChildren().add(jungle_height_box);
            vbox.getChildren().add(jungle_width_box);
        vbox.getChildren().add(spawn_cat);
            vbox.getChildren().add(animals_spawn_box);
            vbox.getChildren().add(grass_spawn_box);
        vbox.getChildren().add(stamina_cat);
            vbox.getChildren().add(max_stamina_box);
            vbox.getChildren().add(starting_stamina_box);
            vbox.getChildren().add(grass_profit_box);
        vbox.getChildren().add(time_cat);
        vbox.getChildren().add(move_deley_box);
            vbox.getChildren().add(magical);

            //this button will load data, create new map and start simulation
        Button play = new Button("PLAY");

        HBox hbButtons = new HBox();
        hbButtons.getChildren().add(play);

        play.setOnMouseClicked(e ->
                {
                    if(magical.isSelected()){
                        magical_evolution = true;
                        System.out.println(magical_evolution);
                    } else {
                        magical_evolution = false;
                    }
                    move_deley_ = Integer.parseInt(moveDelay.getText());

                    map = new RectangularMap(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Integer.parseInt(jungle_width.getText()), Integer.parseInt(jungle_height.getText()),
                            Integer.parseInt(starting_stamina.getText()), Integer.parseInt(max_stamina.getText()), Integer.parseInt(grass_regen.getText()),
                            Integer.parseInt(grass_per_day.getText()), Integer.parseInt(starting_number_of_animals.getText()), magical_evolution);

                    num_of_animals = map.arena_map.size();
                    engine = new SimulationEngine(map);

                    //fill_grid();
                    try {
                        gridPane = set_grid();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    set_chart();
                    BorderPane rootv = new BorderPane();
                    rootv.setRight(gridPane);

                    Scene scene = new Scene(rootv, 1500, 900);
                    stage.setScene(scene);
                    stage.show();

                    //clear_grid();
                    App.root = new BorderPane();
                    thread = new Thread (engine);
                    thread.setDaemon(true);
                    thread.start();
                }
        );
        hbButtons.setAlignment(Pos.CENTER_RIGHT);
        hbButtons.setPadding(new Insets(30, 50, 50, 50));
        vbox.getChildren().add(hbButtons);

        //displaying menu on screen
        root.setCenter(vbox);
        Scene scene = new Scene(root, 650, 800);
        stage.setScene(scene);
        stage.show();


    }

    public static GridPane set_grid() throws Exception {

        int width = map.width;

        int height = map.height;
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        //setting grid
        for(int i= 0; i<=width+1;i++){
            gridPane.getColumnConstraints().add(new ColumnConstraints(Math.round(400./map.width)));
        }
        for(int i= 0; i<=height+1;i++){
            gridPane.getRowConstraints().add(new RowConstraints(Math.round(600./map.height)));
        }
        for(int i= 0; i<=width+1;i++){
            for(int j=0; j<=height+1;j++){
                if(i==0 && j==0) {

                    Label label = new Label("y/x");
                    gridPane.add(label,i,j);
                    gridPane.setHalignment(label, HPos.CENTER);
                }
                else if(i==0){
                    Label label = new Label(Integer.toString(height-j+1));
                    gridPane.add(label, i, j);
                    gridPane.setHalignment(label, HPos.CENTER);
                }
                else if(j==0){
                    Label label = new Label(Integer.toString(i-1));
                    gridPane.add(label, i, j);
                    gridPane.setHalignment(label, HPos.CENTER);
                }
                if(i>map.Jungle_widht_start && i<map.Jungle_widht_end+1 && j<height+2 - map.Jungle_height_start&& j> height+1 - map.Jungle_height_end){
                    Pane pane = new Pane();
                    pane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    gridPane.add(pane,i,j);
                }

            }
        }
        //populating grid with animals
        ArrayList<Animal> animals = new ArrayList<>();
        for(Animal ani:map.arena_map.values()){
            animals.add(ani);
            }

        for(Animal ani: animals){
            Pane pane = new Pane();
            GuiElementBox el = new GuiElementBox(ani);
            VBox v = el.createVbox();
            pane.getChildren().add(v);
            StackPane.setAlignment(v,Pos.CENTER);
            pane.setOnMouseEntered(e -> {
                x_val =  ani.get_position().x;
                y_val = ani.get_position().y;
            });
            gridPane.add(pane,(el.pos.x+1), height+1 - el.pos.y);
        }
        //populating grid with grass
        ArrayList<Grass> grasses = new ArrayList<>();
        for(Grass grass:map.get_arena_grass_map().values()){
            grasses.add(grass);
            }
        for(Grass gras: grasses){
            Pane pane = new Pane();
            GuiElementBox el = new GuiElementBox(gras);
            VBox v = el.createVbox();
            pane.getChildren().add(v);
            StackPane.setAlignment(v,Pos.CENTER);
            pane.setOnMouseEntered(e -> {
                x_val =  el.pos.y;
                y_val = el.pos.x;
            });
            gridPane.add(pane, (el.pos.x+1), height+1 - el.pos.y);
        }
        gridPane.setPadding(new Insets(30, 30, 30, 30));
        return gridPane;
    }
    public static void set_chart(){
        /* function that set up line chart for data like number ov animals, number of grass, average life time etc. */
        Platform.runLater(() -> {
                    NumberAxis xAxis = new NumberAxis();
                    NumberAxis yAxis = new NumberAxis();
                    xAxis.setLabel("Number of Days");

                    lineChart = new LineChart<>(xAxis, yAxis);
                    lineChart.setMaxSize(1000, 700);
                    lineChart.setPadding(new Insets(30, 30, 30, 30));
                    lineChart.setTitle("Statistics");
                    number_of_animals = new XYChart.Series();
                    number_of_animals.setName("Number Of Animals");
                    number_of_grass = new XYChart.Series();
                    number_of_grass.setName("Number Of Grass");
                    average_age = new XYChart.Series();
                    average_age.setName("Average Age");
                    average_stamina = new XYChart.Series();
                    average_stamina.setName("Average Stamina");
                    average_number_of_children = new XYChart.Series();
                    average_number_of_children.setName("Average Number Of Children");

                    lineChart.getData().add(number_of_animals);
                    lineChart.getData().add(number_of_grass);
                    lineChart.getData().add(average_age);
                    lineChart.getData().add(average_stamina);
                    lineChart.getData().add(average_number_of_children);

                    lineChart.setAnimated(false);
                });
    }
    public static void add_data_to_chart(int day, int total_num_animals, int total_num_grass,
                                  float average_life_time, float average_number_of_children_, float average_stamina_){
     /* function adding data to line chart */

        Platform.runLater(() -> {
            number_of_animals.getData().add(new XYChart.Data(day, total_num_animals));
            number_of_grass.getData().add(new XYChart.Data(day, total_num_grass));
            average_age.getData().add(new XYChart.Data(day, average_life_time));
            average_stamina.getData().add(new XYChart.Data(day, average_stamina_));
            average_number_of_children.getData().add(new XYChart.Data(day, average_number_of_children_));

        });
    }
    public static void follow(Animal ani){
        /*function that erase previously followed and follow chosen animal*/
        for(Animal animal: map.arena_map.values())
        {
            animal.isfollowed = false;
            animal.parent_if_followed = null;
            animal.number_of_children_from_now = 0;
            animal.number_of_descendants_from_now = 0;
        }
        ani.isfollowed = true;
        followed_animal = ani;

    }
    public static String find_dominating_animals(){
        /* function that find positions of animals with dominating genes */
        if(map.Dominating_genes==null)
            return "There are no animals with dominating genes to show";
        else {
            String result = "Position on grid of animals with dominating genes are: ";
            for (Animal ani : map.arena_map.values()) {
                if (ani.genes.Genes_arr == map.Dominating_genes)
                    result = result + "(" + String.valueOf(ani.get_position().x) + ", " +  String.valueOf(ani.get_position().y) + ") ";
            }
            return result;
        }

    }
}
