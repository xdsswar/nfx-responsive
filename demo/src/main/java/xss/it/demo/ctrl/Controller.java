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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import xss.it.demo.entity.Person;
import xss.it.demo.model.PersonModel;
import xss.it.nfx.responsive.control.NfxGridCell;
import xss.it.nfx.responsive.control.NfxGridListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

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
 * Created on 09/06/2025 at 18:32
 */
public class Controller implements Initializable {
    @FXML
    private TextField searchInput;

    @FXML
    private NfxGridListView<Person> listView;

    @FXML
    private ScrollPane scrollPane;

    static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private final ObservableList<Person> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);

        listView.setMinCellWidth(480);
        listView.setCellHeight(220);

        listView.setCellFactory(new Callback<>() {
            @Override
            public NfxGridCell<Person> call(NfxGridListView<Person> param) {
                return new NfxGridCell<>(param){
                    @Override
                    public void update(Person item) {
                        super.update(item);
                        setText(null);
                        if (item != null){
                            CellFactory factory = new CellFactory(item);
                            setGraphics(factory);
                        }
                    }
                };
            }
        });

        load();
    }



    private void load(){
        Task<ObservableList<Person>> task = new Task<>() {
            @Override
            protected ObservableList<Person> call() throws Exception {
                String json = PersonModel.readFileFromResources("/people.json");
                if (json == null || json.isBlank()){
                    throw  new RuntimeException("Empty json file");
                }
                List<Person> data = PersonModel.fromJsonArray(json);
                return FXCollections.observableArrayList(data);
            }
        };

        task.setOnSucceeded(e->{
            if (task.getValue() != null){
                masterList.setAll(task.getValue());
                Platform.runLater(()->{
                    listView.setItems(masterList);
                    intiSearchFilter();
                });
            }
        });


        THREAD_POOL.submit(task);
    }

    private void intiSearchFilter(){
        searchInput.textProperty().addListener((obs, o, txt) -> {
            final String filter = txt.toLowerCase();
            Task<FilteredList<Person>> task = new Task<>() {
                @Override
                protected FilteredList<Person> call() {
                    Predicate<Person> p = person -> {
                        if (filter.isBlank()) return true;
                        return (person.getName().toLowerCase().contains(filter))
                                || (person.getEmail().toLowerCase().contains(filter))
                                || (person.getCity().toLowerCase().contains(filter));
                    };
                    return new FilteredList<>(masterList, p);
                }

                @Override
                protected void succeeded() {
                    ObservableList<Person> list = FXCollections.observableArrayList(getValue());
                    Platform.runLater(() -> listView.setItems(list));
                }
            };

            THREAD_POOL.submit(task);
        });
    }
}
