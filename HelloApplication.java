package com.example.rocknikovapr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class HelloApplication extends Application {

    /*
     * Počet řádků v poli
     */
    int radky = 6;

    /*
     * Počet sloupců v poli
     */
    int sloupce = 7;

    /*
     * Počet spojených bodů k výhře
     */
    int conectforwin = 2;

    /*
    * Hrací pole pro hru
     */
    Kruh[][] hraciPole;

    Stage okno;

    Scene scena;

    Pane pane = new Pane();

    int prumer = 50;

    int hracNaTahu = 1;

    Text text = new Text();

    @Override
    public void start(Stage stage) throws IOException {
        okno = stage;
        okno.setTitle("Connect Four");
        okno.setWidth(sloupce * 100);
        okno.setHeight(radky * 100);
        pane.setStyle("-fx-background-color: #ccc");
        text.setFill(Color.BLACK);
        text.setText("Hráč č.1 je na tahu");
        text.setLayoutY(20);
        text.setTextAlignment(TextAlignment.CENTER);
        pane.getChildren().add(text);

        hraciPole = new Kruh[sloupce][radky];

        for (int s = 0; s < sloupce; s++) {
            for (int r = 0; r < radky; r++) {
                Kruh k = new Kruh();
                k.setCenterX((prumer * s) + (prumer / 2) + prumer);
                k.setCenterY((prumer * r) + (prumer / 2) + prumer);
                k.setRadius(prumer / 2 - 2);
                k.setFill(Color.WHITE);
                hraciPole[s][r] = k;
                pane.getChildren().add(k);
            }
        }

        pane.setOnMouseClicked(e -> {

            if (e.getX() > prumer && e.getX() < (sloupce + 1) * prumer) {
                int sloupec = (int) Math.floor(e.getX() / prumer - 1);
                for(int r = radky - 1; r >= 0; r-- ) {
                    if (hraciPole[sloupec][r].getHrac() == 0) {
                        hraciPole[sloupec][r].setHrac(hracNaTahu % 2 == 0? 2 : 1);
                        hraciPole[sloupec][r].setFill(hracNaTahu % 2 == 0? Color.YELLOW: Color.RED);
                        hracNaTahu++;
                        text.setText("Hráč č."+ (hracNaTahu % 2 == 0? 2 : 1) + " je na tahu");
                        int vyteznyHrac = kontrola();

                        if (vyteznyHrac > 0) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Konec hry, vyhrává hráč č." + vyteznyHrac);
                            Optional<ButtonType> vysledek = alert.showAndWait();
                            System.out.println(vysledek.get().getButtonData().toString());
                            System.exit(0);
                        }
                        break;
                    }
                }
            }
        });

        scena = new Scene(pane);
        okno.setScene(scena);
        text.setWrappingWidth(scena.getWidth());
        okno.show();
    }

    public int kontrola() {
        int posledniHrac = 0;
        int pocetSpojeni = 0;

        /*
        Kontrola vertikálního spojení.
         */
        for(int s = 0; s < sloupce; s++) {
            posledniHrac = 0;
            pocetSpojeni = 0;

            for(int r = 0; r < radky; r++) {

                if (hraciPole[s][r].getHrac() == 0) {
                    posledniHrac = 0;
                    pocetSpojeni = 0;
                    continue;
                }
                if (posledniHrac == 0 || posledniHrac != hraciPole[s][r].getHrac()) {
                    posledniHrac = hraciPole[s][r].getHrac();
                    pocetSpojeni = 1;
                } else {
                    pocetSpojeni++;
                }
                if (pocetSpojeni == 4) {
                    return posledniHrac;

                }
            }
        }
        /*
        Kontrola horizontálního spojení.
         */

        for(int r = 0; r < radky; r++) {
            posledniHrac = 0;
            pocetSpojeni = 0;
            for(int s = 0; s < sloupce; s++) {

                if (hraciPole[s][r].getHrac() == 0) {
                    posledniHrac = 0;
                    pocetSpojeni = 0;
                    continue;
                }
                if (posledniHrac == 0 || posledniHrac != hraciPole[s][r].getHrac()) {
                    posledniHrac = hraciPole[s][r].getHrac();
                    pocetSpojeni = 1;
                } else {
                    pocetSpojeni++;
                }
                if (pocetSpojeni == 4) {
                    return posledniHrac;

                }
            }
        }

        /*
        Kontrola diagonálních spojení ze stran (levá, pravá)
         */

        int[] prava;
        int[] leva;

        for(int r = 2; r < radky; r++) {
            prava = new int[2];
            leva = new int[2];

            for(int s = 0; s <= r; s++) {

                leva = kontrolaPolicka(leva, hraciPole[s][r - s]);
                if (leva[1] == 4) {
                    return leva[0];
                }

                prava = kontrolaPolicka(prava, hraciPole[sloupce - 1 - s][r - s]);
                if (prava[1] == 4) {
                    return prava[0];
                }
            }
        }

        /*
        Kontrola diagonál zespoda oběma směry
         */

        for(int s = 0; s < sloupce; s++) {
            prava = new int[2];
            leva = new int[2];

            for(int r = 0; r < radky; r++) {
                if (s + r <= sloupce - 1) {
                    leva = kontrolaPolicka(leva, hraciPole[s + r][radky - 1 - r]);
                    if (leva[1] == 4) {
                        return leva[0];
                    }

                }
                if (sloupce - 1 - s - r >= 0) {
                    prava = kontrolaPolicka(prava, hraciPole[sloupce - 1 - s - r][radky - 1 - r]);
                    if (prava[1] == 4) {
                        return prava[0];
                    }
                }
            }

        }

        return 0;
    }

    private int[] kontrolaPolicka(int[] array, Kruh kruh) {

        if (kruh.getHrac() == 0) {
            array = new int[2];
        } else if (array[0] != kruh.getHrac()) {
            array[0] = kruh.getHrac();
            array[1] = 1;
        } else {
            array[1]++;
        }
        return array;
    }

    public static void main(String[] args) {
        launch();
    }
}