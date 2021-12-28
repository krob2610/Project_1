package agh.ics.oop.gui;


import agh.ics.oop.Animal;
import agh.ics.oop.IMapElement;
import agh.ics.oop.Vector2d;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javafx.scene.paint.Color;



public class GuiElementBox {
    /* class that holds elements in grid - Animals, Grass - positions were scaling bad */
    private String address;
    public Vector2d pos;
    IMapElement e;
        public GuiElementBox(IMapElement el){
        address = el.get_image_path();
        pos = el.get_position();
        e = el;
    }

    public VBox createVbox() throws Exception {
            /*creating vbox with image that will go to grid */
        Image image = new Image(new FileInputStream(this.address));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400/App.map.width);
        imageView.setFitHeight(400/App.map.height);
        if(this.e instanceof Animal)
            imageView = change_colour(imageView);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(imageView);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }


public ImageView change_colour(ImageView imageView){
            /* function that changes color of image */
    Color col= set_colour((Animal) this.e);
    Lighting lighting = new Lighting(new Light.Distant(45, 90, col));
    ColorAdjust bright = new ColorAdjust(0, 1, 1, 1);
    lighting.setContentInput(bright);
    lighting.setSurfaceScale(0.0);

    imageView.setEffect(lighting);
    return imageView;
}

public Color set_colour(Animal a){
            /*(function that chooses color depending on energy*/
    Color col;
    float fullness =  (a.stamina*100/a.staamina_max);
    if(fullness>90)
        col = Color.DARKGREEN;
    else if(fullness>80)
        col = Color.GREEN;
    else if(fullness>70)
        col = Color.YELLOW;
    else if(fullness>60)
        col = Color.GOLD;
    else if(fullness>50)
        col = Color.ORANGE;
    else if(fullness>40)
        col = Color.ORANGERED;
    else if(fullness>30)
        col = Color.RED;
    else if(fullness>20)
        col = Color.DARKRED;
    else if(fullness>10)
        col = Color.BLACK;
    else
        col = Color.GREY;
    return col;
}
}
