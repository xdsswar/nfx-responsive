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

package xss.it.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import xss.it.demo.ctrl.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the xss.it.demo package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 06, 2025
 * <p>
 * Created on 09/06/2025 at 18:31
 */
public class Demo extends Application {

    /**
     * The entry point of the Java application.
     * This method calls the launch method to start a JavaFX application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is called after the application has been launched.
     * Override this method to create and set up the primary stage of the application.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent p = load("/demo.fxml", new Controller());
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setMinHeight(680);
        stage.setMinWidth(720);
        stage.setTitle("Responsive JavaFX Dashboard Demo");
        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource("/admin.png")).toExternalForm())
        );
        stage.setWidth(1200);
        stage.setHeight(680);
        stage.show();
    }

    /**
     * The initialization method for the application.
     * This method is called immediately after the application class is loaded and
     * constructed. An application can override this method to perform initialization
     * tasks before the application is shown.
     *
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     *
     * @throws Exception if an error occurs during stopping the application.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private  <T extends Parent> T load(String location, Object ctrl) throws IOException {
        FXMLLoader loader = new FXMLLoader(load(location));
        if (ctrl != null){
            loader.setController(ctrl);
        }
        return loader.load();
    }

    public static URL load(final String location){
        return Demo.class.getResource(location);
    }


}
