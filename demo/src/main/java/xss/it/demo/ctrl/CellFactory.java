/*
 * Copyright © 2025. XTREME SOFTWARE SOLUTIONS
 *
 * All rights reserved. Unauthorized use, reproduction, or distribution
 * of this software or any portion of it is strictly prohibited and may
 * result in severe civil and criminal penalties. This code is the sole
 * proprietary of XTREME SOFTWARE SOLUTIONS.
 *
 * Commercialization, redistribution, and use without explicit permission
 * from XTREME SOFTWARE SOLUTIONS, are expressly forbidden.
 */

package xss.it.demo.ctrl;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import xss.it.demo.Demo;
import xss.it.demo.entity.Person;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.demo.ctrl package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 06, 2025
 * <p>
 * Created on 09/06/2025 at 21:30
 */
public final class CellFactory extends AnchorPane {
    private final AnchorPane cellBox;
    private final HBox hBox;
    private final ImageView image;
    private final VBox vBox;
    private final HBox hBox1;
    private final Label label;
    private final HBox hBox2;
    private final Label nameLabel;
    private final HBox hBox3;
    private final Label label1;
    private final HBox hBox4;
    private final Label emailLabel;
    private final HBox hBox5;
    private final Label label2;
    private final HBox hBox6;
    private final Label macLabel;
    private final HBox hBox7;
    private final Label label3;
    private final HBox hBox8;
    private final Label cityLabel;
    private final HBox hBox9;
    private final Label label4;
    private final HBox hBox10;
    private final Label dateLabel;
    private final HBox hBox11;
    private final Label label5;
    private final HBox hBox12;
    private final Label cardLabel;

    private final Person person;

    public CellFactory(Person person) {
        this.person = person;
        cellBox = new AnchorPane();
        hBox = new HBox();
        image = new ImageView();
        vBox = new VBox();
        hBox1 = new HBox();
        label = new Label();
        hBox2 = new HBox();
        nameLabel = new Label();
        hBox3 = new HBox();
        label1 = new Label();
        hBox4 = new HBox();
        emailLabel = new Label();
        hBox5 = new HBox();
        label2 = new Label();
        hBox6 = new HBox();
        macLabel = new Label();
        hBox7 = new HBox();
        label3 = new Label();
        hBox8 = new HBox();
        cityLabel = new Label();
        hBox9 = new HBox();
        label4 = new Label();
        hBox10 = new HBox();
        dateLabel = new Label();
        hBox11 = new HBox();
        label5 = new Label();
        hBox12 = new HBox();
        cardLabel = new Label();

        initialize();

        events();

        load();

    }

    private void initialize(){

        AnchorPane.setBottomAnchor(cellBox, 0.0);
        AnchorPane.setLeftAnchor(cellBox, 0.0);
        AnchorPane.setRightAnchor(cellBox, 0.0);
        AnchorPane.setTopAnchor(cellBox, 0.0);

        //cellBox.setPrefHeight(200.0);
        //cellBox.setPrefWidth(200.0);
        cellBox.getStyleClass().add("cell-box");

        AnchorPane.setBottomAnchor(hBox, 5.0);
        AnchorPane.setLeftAnchor(hBox, 5.0);
        AnchorPane.setRightAnchor(hBox, 5.0);
        AnchorPane.setTopAnchor(hBox, 5.0);
        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.setPrefHeight(100.0);
        hBox.setPrefWidth(200.0);

        image.setFitHeight(80.0);
        image.setFitWidth(80.0);
        image.setPickOnBounds(true);
        image.setPreserveRatio(true);
        image.setImage(new Image(Demo.load("/pay.png").toExternalForm()));
        HBox.setMargin(image, new Insets(0.0, 0.0, 0.0, 10.0));

        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPrefHeight(200.0);
        vBox.setPrefWidth(5000.0);
        HBox.setMargin(vBox, new Insets(0.0, 10.0, 0.0, 20.0));

        hBox1.setAlignment(Pos.CENTER_LEFT);
        hBox1.setPrefWidth(200.0);

        label.setMinWidth(110.0);
        label.getStyleClass().add("cell-label");
        label.setText("Neme");
        HBox.setMargin(label, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox2.setAlignment(Pos.CENTER_RIGHT);
        hBox2.setPrefWidth(5000.0);

        nameLabel.getStyleClass().add("cell-label");
        nameLabel.setText("--");
        HBox.setMargin(nameLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox1, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox3.setAlignment(Pos.CENTER_LEFT);
        hBox3.setPrefWidth(200.0);

        label1.setMinWidth(110.0);
        label1.getStyleClass().add("cell-label");
        label1.setText("Email Address");
        HBox.setMargin(label1, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox4.setAlignment(Pos.CENTER_RIGHT);
        hBox4.setPrefWidth(5000.0);

        emailLabel.getStyleClass().add("cell-label");
        emailLabel.setText("--");
        HBox.setMargin(emailLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox3, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox5.setAlignment(Pos.CENTER_LEFT);
        hBox5.setPrefWidth(200.0);

        label2.setMinWidth(110.0);
        label2.getStyleClass().add("cell-label");
        label2.setText("Mac Address");
        HBox.setMargin(label2, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox6.setAlignment(Pos.CENTER_RIGHT);
        hBox6.setPrefWidth(5000.0);

        macLabel.getStyleClass().add("cell-label");
        macLabel.setText("--");
        HBox.setMargin(macLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox5, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox7.setAlignment(Pos.CENTER_LEFT);
        hBox7.setPrefWidth(200.0);

        label3.setMinWidth(110.0);
        label3.getStyleClass().add("cell-label");
        label3.setText("City");
        HBox.setMargin(label3, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox8.setAlignment(Pos.CENTER_RIGHT);
        hBox8.setPrefWidth(5000.0);

        cityLabel.getStyleClass().add("cell-label");
        cityLabel.setText("--");
        HBox.setMargin(cityLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox7, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox9.setAlignment(Pos.CENTER_LEFT);
        hBox9.setPrefWidth(200.0);

        label4.setMinWidth(110.0);
        label4.getStyleClass().add("cell-label");
        label4.setText("Date");
        HBox.setMargin(label4, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox10.setAlignment(Pos.CENTER_RIGHT);
        hBox10.setPrefWidth(5000.0);

        dateLabel.getStyleClass().add("cell-label");
        dateLabel.setText("--");
        HBox.setMargin(dateLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox9, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox11.setAlignment(Pos.CENTER_LEFT);
        hBox11.setPrefWidth(200.0);

        label5.setMinWidth(110.0);
        label5.getStyleClass().add("cell-label");
        label5.setText("Credit Card");
        HBox.setMargin(label5, new Insets(0.0, 0.0, 0.0, 20.0));

        hBox12.setAlignment(Pos.CENTER_RIGHT);
        hBox12.setPrefWidth(5000.0);

        cardLabel.getStyleClass().add("cell-label");
        cardLabel.setText("--");
        HBox.setMargin(cardLabel, new Insets(0.0, 0.0, 0.0, 20.0));
        VBox.setMargin(hBox11, new Insets(2.0, 5.0, 2.0, 0.0));

        hBox.getChildren().add(image);
        hBox1.getChildren().add(label);
        hBox2.getChildren().add(nameLabel);
        hBox1.getChildren().add(hBox2);
        vBox.getChildren().add(hBox1);
        hBox3.getChildren().add(label1);
        hBox4.getChildren().add(emailLabel);
        hBox3.getChildren().add(hBox4);
        vBox.getChildren().add(hBox3);
        hBox5.getChildren().add(label2);
        hBox6.getChildren().add(macLabel);
        hBox5.getChildren().add(hBox6);
        vBox.getChildren().add(hBox5);
        hBox7.getChildren().add(label3);
        hBox8.getChildren().add(cityLabel);
        hBox7.getChildren().add(hBox8);
        vBox.getChildren().add(hBox7);
        hBox9.getChildren().add(label4);
        hBox10.getChildren().add(dateLabel);
        hBox9.getChildren().add(hBox10);
        vBox.getChildren().add(hBox9);
        hBox11.getChildren().add(label5);
        hBox12.getChildren().add(cardLabel);
        hBox11.getChildren().add(hBox12);
        vBox.getChildren().add(hBox11);
        hBox.getChildren().add(vBox);
        cellBox.getChildren().add(hBox);
        getChildren().add(cellBox);


    }

    private void events(){
        cellBox.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), cellBox);
            st.setToX(1.008);
            st.setToY(1.008);
            st.play();
        });

        cellBox.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), cellBox);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    public void load(){
        nameLabel.setText(person.getName());
        emailLabel.setText(person.getEmail());
        cityLabel.setText(person.getCity());
        macLabel.setText(person.getMac());
        cardLabel.setText(person.getCreditCard().isBlank() || person.getCreditCard().isEmpty() ? "" : "****-****-****-"+person.getCreditCard().substring(15));
        dateLabel.setText(this.person.getTimestamp().length()<10 ? "Unknown" : person.getTimestamp().substring(0,10));
    }
}