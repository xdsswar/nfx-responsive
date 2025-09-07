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
 * <p>
 * Controller for a simple people browser backed by {@code NfxGridListView<Person>}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Load {@code people.json} on a background thread.</li>
 *   <li>Populate the grid list with {@link Person} cards.</li>
 *   <li>Provide live filtering from {@code searchInput} without blocking the FX thread.</li>
 * </ul>
 */
public class Controller implements Initializable {

    /**
     * Text field used to enter a case-insensitive filter (by name, email, or city).
     * <p>
     * Injected via FXML.
     */
    @FXML
    private TextField searchInput;

    /**
     * Grid-based list view that displays {@link Person} items using {@link NfxGridCell} cards.
     * <p>
     * Injected via FXML.
     */
    @FXML
    private NfxGridListView<Person> listView;

    /**
     * Scroll container hosting the grid list; configured to fit width so cells wrap responsively.
     * <p>
     * Injected via FXML.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * Single-threaded background executor for IO and filtering work.
     * <p>
     * Uses a daemon thread so it does not prevent application exit.
     */
    static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    /**
     * Master, unfiltered dataset loaded from {@code /people.json}.
     * <p>
     * UI displays filtered copies derived from this list to keep the model intact.
     */
    private final ObservableList<Person> masterList = FXCollections.observableArrayList();

    /**
     * JavaFX initialization hook.
     * <ul>
     *   <li>Configures {@code scrollPane} and cell sizing.</li>
     *   <li>Installs a cell factory that renders {@link Person} cards.</li>
     *   <li>Starts async loading of data.</li>
     * </ul>
     */
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
                            // Render a card for this person (implementation in your CellFactory)
                            CellFactory factory = new CellFactory(item);
                            setGraphics(factory);
                        }
                    }
                };
            }
        });

        load();
    }

    /**
     * Loads {@code /people.json} off the FX thread, parses it into {@link Person} objects,
     * and updates the UI on success. Also wires the live search filter afterward.
     */
    private void load(){
        Task<ObservableList<Person>> task = new Task<>() {
            @Override
            protected ObservableList<Person> call() throws Exception {
                String json = PersonModel.readFileFromResources("/people.json");
                if (json == null || json.isBlank()){
                    throw new RuntimeException("Empty json file");
                }
                List<Person> data = PersonModel.fromJsonArray(json);
                return FXCollections.observableArrayList(data);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue() != null){
                masterList.setAll(task.getValue());
                Platform.runLater(() -> {
                    listView.setItems(masterList);
                    intiSearchFilter();
                });
            }
        });

        THREAD_POOL.submit(task);
    }

    /**
     * Installs a text listener on {@code searchInput} that filters {@link #masterList}
     * on a background thread and publishes the result to the grid view.
     * <p>
     * Matching is case-insensitive against name, email, or city.
     */
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
