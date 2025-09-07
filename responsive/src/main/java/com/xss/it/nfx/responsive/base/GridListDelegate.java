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

package com.xss.it.nfx.responsive.base;

import com.xss.it.nfx.responsive.misc.FocusModel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import xss.it.nfx.responsive.control.NfxGridCell;
import xss.it.nfx.responsive.control.NfxGridListView;
import xss.it.nfx.responsive.misc.SelectionModel;

import java.util.*;

/**
 * nfx-responsive
 * <p>
 * Description:
 * This class is part of the com.xss.it.nfx.responsive.base package.
 *
 * <p>
 *
 * @author XDSSWAR
 * @version 1.0
 * @since September 05, 2025
 * <p>
 * Created on 09/05/2025 at 18:04
 * <p>
 * Delegate responsible for virtualization, layout, and scrolling of a {@link NfxGridListView}.
 * <p>
 * This class extends {@link ScrollPane} to host a virtualized content pane and manage
 * efficient creation, reuse, and display of item nodes as the user scrolls.
 *
 * @param <T> the type of items displayed by the associated {@code NfxGridListView}
 */
public final class GridListDelegate<T> extends ScrollPane {

    /**
     * The {@link NfxGridListView} that this delegate manages and renders.
     */
    private final NfxGridListView<T> listView;

    /**
     * The set of nodes currently considered visible in the viewport.
     * <p>
     * Backed by an {@link IdentityHashMap} to track nodes by identity and avoid
     * accidental equality-based collisions.
     */
    private final Set<Node> visibleSet = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * The number of off-screen buffer rows to render above and below the viewport.
     * <p>
     * Buffering helps reduce flicker and layout churn during fast scrolling.
     */
    private static final int BUFFER_ROWS = 2;

    /**
     * A mapping from item value to its corresponding visual {@link Node}.
     * <p>
     * Enables node reuse and quick lookup during virtualization updates.
     */
    private final Map<T, Node> itemToNodeMap;

    /**
     * The virtualized container that arranges and displays cell nodes.
     */
    private final VirtualPane contentPane;

    /**
     * The current number of cells laid out per row, recalculated as size or style changes.
     */
    private final IntegerProperty currentCellsPerRow;

    /**
     * Listener for selection model changes to keep visual selection state in sync.
     */
    private final ListChangeListener<T> selectionModelChangeListener;

    /**
     * Listener for changes to the backing items list to add, remove, or update cells.
     */
    private final ListChangeListener<T> listChangeListener;

    /**
     * Event handler for {@link KeyEvent#KEY_PRESSED} events.
     * <p>
     * When the user presses the {@code CONTROL} key, the selection mode
     * of this {@link NfxGridListView} is switched to
     * {@link SelectionModel.Mode#MULTIPLE}, allowing multiple items to be selected
     * simultaneously.
     */
    private final EventHandler<KeyEvent> keyPressedEvent;

    /**
     * Event handler for {@link KeyEvent#KEY_RELEASED} events.
     * <p>
     * When the user releases the {@code CONTROL} key, the selection mode
     * of this {@link NfxGridListView} is reverted back to
     * {@link SelectionModel.Mode#SINGLE}, restricting selection to a single item.
     */
    private final EventHandler<KeyEvent> keyReleasedEvent;

    /**
     * Anchor index used for mouse Shift+click range selection.
     * - -1 means "unset"
     * - Preserved across interactions so subsequent Shift+clicks extend from here.
     */
    private final IntegerProperty anchorIndex = new SimpleIntegerProperty(this, "anchorIndex", -1);

    /**
     * Shift+click range selection handler (mouse press).
     * <p>
     * Node-scoped (attached to this control only). On primary+Shift press it:
     * <ul>
     *   <li>Computes the range anchor (last selected item if any, else focused, else clicked).</li>
     *   <li>Consumes the event to suppress default selection.</li>
     *   <li>Applies the exact [anchor..clicked] selection on the next pulse (via {@code Platform.runLater}).</li>
     *   <li>Keeps focus on the clicked cell.</li>
     * </ul>
     */
    private final EventHandler<MouseEvent> shiftClickRangeHandler;

    /**
     * Mouse release normalizer for selection updates.
     * <p>
     * Node-scoped. Used to finalize/correct selection at the end of a mouse gesture
     * (e.g., after deferred range updates) so the final state is consistent.
     */
    private final EventHandler<MouseEvent> updateSelectionOnReleaseEvent;

    /**
     * Keyboard shortcut handler for Select All (Ctrl/⌘ + A).
     * <p>
     * Node-scoped; acts only when this control owns focus. Applies an atomic
     * {@code setAll(items)} on the selection model and preserves the current focus/anchor.
     */
    private final EventHandler<KeyEvent> selectAllHandler;

    /**
     * Keyboard handler for Refresh (F5 without modifiers).
     * <p>
     * Node-scoped; triggers {@code listView.refresh()} and attempts to keep the
     * currently focused item visible afterward.
     */
    private final EventHandler<KeyEvent> refreshKeyHandler;

    /**
     * Keyboard navigation handler (arrows/Home/End/PageUp/PageDown).
     * <p>
     * Focus-only: moves the single focused cell and ensures it is visible; does not mutate selection.
     * Lets ENTER bubble to the cell so per-cell activation can run; may consume handled nav keys.
     * Installed via {@code initializeKeyEvents()} on this control (not globally).
     */
    private final EventHandler<KeyEvent> keyNavHandler;


    /**
     * Item-only focus model for this grid/list:
     * - Guarantees at most one focused item (or none).
     * - Updates cell focus visuals via delegate.getCell(...).setFocusedCell(...).
     * - Independent of selection; keyboard nav moves focus only.
     */
    private final FocusModel<T> focusModel;


    /**
     * Creates a new {@code GridListDelegate} for the given {@link NfxGridListView}.
     * <p>
     * This constructor sets up the virtualization infrastructure by:
     * <ul>
     *   <li>Storing a reference to the associated {@code NfxGridListView}.</li>
     *   <li>Initializing the content pane that lays out cells.</li>
     *   <li>Preparing the mapping of items to their visual nodes.</li>
     *   <li>Configuring the property that tracks cells per row.</li>
     *   <li>Registering listeners for changes in the item list and selection model.</li>
     *   <li>Setting the content of this {@code ScrollPane} to the virtualized pane.</li>
     * </ul>
     *
     * @param listView the {@link NfxGridListView} that this delegate manages
     */
    public GridListDelegate(NfxGridListView<T> listView) {
        this.listView = listView;
        this.itemToNodeMap = new HashMap<>();
        this.contentPane = new VirtualPane();
        this.currentCellsPerRow = new SimpleIntegerProperty(1);
        this.focusModel = new FocusModel<>(this);
        selectionModelChangeListener = c-> {
            while (c.next()){
                if (c.wasAdded()){
                    for (T t : c.getAddedSubList()) {
                        Node node = getCellNode(t);
                        if (node instanceof NfxGridCell<?> cell) {
                            cell.setSelectedCell(true);
                        }
                    }

                    if (!c.getAddedSubList().isEmpty()){
                        focusModel.setFocusedItem(c.getAddedSubList().getLast());
                    }
                }
                else if (c.wasRemoved()){
                    for (T t : c.getRemoved()) {
                        Node node = getCellNode(t);
                        //We don't want to unselect a removed cell, no point on doing it , right? :)
                        if (listView.getItems().contains(t)  && node instanceof NfxGridCell<?> cell) {
                            cell.setSelectedCell(false);
                            focusModel.onItemRemoved(t);
                        }
                    }
                }
            }
        };

        this.listChangeListener = c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (T item : c.getRemoved()) {
                        listView.getSelectionModel().unselect(item);
                    }
                }
            }

            onUpdate();
        };

        updateSelectionOnReleaseEvent = e->{
            if (!e.isControlDown() && !e.isShiftDown()) {
                listView.getSelectionModel().setSelectionMode(SelectionModel.Mode.SINGLE);
            }
        };

        keyPressedEvent = e -> {
            if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {
                listView.getSelectionModel().setSelectionMode(SelectionModel.Mode.MULTIPLE);
            }
        };

        keyReleasedEvent = e -> {
            if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {
                // Defer so any runLater selection changes finish first
                Platform.runLater(() ->
                        listView.getSelectionModel().setSelectionMode(SelectionModel.Mode.SINGLE)
                );
            }
        };

        keyNavHandler = e -> {
            if (!isOurFocusOwner()) return;

            final var items = listView.getItems();
            final int count = (items == null) ? 0 : items.size();
            if (count == 0) return;

            final boolean shift = e.isShiftDown();
            final int cpr       = Math.max(1, currentCellsPerRow.get());

            // Focus index (derived from FocusModel only)
            int idx = -1;
            {
                final T fi = focusModel.getFocusedItem();
                if (fi != null) idx = items.indexOf(fi);
            }
            if (idx < 0 || idx >= count) {
                idx = 0; // default to first item
                focusModel.setFocusedItem(items.get(idx));
                anchorIndex.set(idx);
            }

            int next = idx;
            switch (e.getCode()) {
                case RIGHT     -> next = Math.min(count - 1, idx + 1);
                case LEFT      -> next = Math.max(0, idx - 1);
                case DOWN      -> next = Math.min(count - 1, idx + cpr);
                case UP        -> next = Math.max(0, idx - cpr);
                case HOME      -> next = idx - (idx % cpr);
                case END       -> { int rowStart = idx - (idx % cpr); next = Math.min(count - 1, rowStart + cpr - 1); }
                case PAGE_DOWN -> next = Math.min(count - 1, idx + cpr * Math.max(1, visibleRowCountEstimate()));
                case PAGE_UP   -> next = Math.max(0, idx - cpr * Math.max(1, visibleRowCountEstimate()));

                case ENTER, SPACE -> {
                    ensureFocusVisible(idx);
                    return;
                }

                default -> { return; }
            }

            if (next == idx) return;

            // Move focus only (no selection mutation)
            focusModel.setFocusedItem(items.get(next));
            ensureFocusVisible(next);
            if (!shift) {
                anchorIndex.set(next);
            }
            e.consume();
        };

        shiftClickRangeHandler = e -> {
            if (e.getEventType() != MouseEvent.MOUSE_PRESSED) return;
            if (e.getButton() != MouseButton.PRIMARY || !e.isShiftDown()) return;

            final NfxGridCell<T> cell = findCellFromEvent(e);
            if (cell == null) return;

            final T item = cell.getItem();
            if (item == null) return;

            final var items = listView.getItems();
            if (items == null || items.isEmpty()) return;

            final int clicked = items.indexOf(item);
            if (clicked < 0) return;

            final var sm  = listView.getSelectionModel();
            final var sel = sm.getSelectedItems();

            // Anchor = last selected if any; else focused; else clicked
            int anchor;
            if (sel != null && !sel.isEmpty()) {
                T lastSel = sel.getLast();
                int idxLast = items.indexOf(lastSel);
                anchor = (idxLast >= 0) ? idxLast : clicked;
            } else {
                final T fi = focusModel.getFocusedItem();
                final int fiIdx = (fi == null) ? -1 : items.indexOf(fi);
                anchor = (fiIdx >= 0) ? fiIdx : clicked;
            }

            final int a = Math.min(anchor, clicked);
            final int b = Math.max(anchor, clicked);

            // Block default click selection
            e.consume();

            // Apply after the click so our range wins
            Platform.runLater(() -> {
                // No need to set MULTIPLE here; we replace the selection atomically.
                replaceRangeSelection(a, b);           // setAll(range): clears then adds exact [a..b]
                focusModel.setFocusedItem(items.get(clicked));
                ensureFocusVisible(clicked);
                anchorIndex.set(clicked);              // next Shift+click anchors from here
            });
        };

        selectAllHandler = e -> {
            if (e.getEventType() != KeyEvent.KEY_PRESSED) return;
            if (!isOurFocusOwner()) return;

            // Ctrl on Win/Linux, Command on macOS
            if (e.getCode() == KeyCode.A && e.isShortcutDown()) {
                final var items = listView.getItems();
                final var sm    = listView.getSelectionModel();

                if (items != null && !items.isEmpty()) {
                    sm.getSelectedItems().setAll(items);  // atomic "select all"
                } else {
                    sm.getSelectedItems().clear();
                }

                // keep anchor at current focus (helps next Shift+click range)
                int idx = -1;
                final var fi = focusModel.getFocusedItem();
                if (fi != null && items != null) idx = items.indexOf(fi);
                if (idx >= 0) {
                    anchorIndex.set(idx);
                    ensureFocusVisible(idx);
                }

                e.consume();
            }
        };

        refreshKeyHandler = e -> {
            if (e.getEventType() != KeyEvent.KEY_PRESSED) return;
            if (!isOurFocusOwner()) return;

            // Plain F5 only (no modifiers)
            if (e.getCode() == KeyCode.F5
                    && !e.isControlDown() && !e.isShiftDown()
                    && !e.isAltDown() && !e.isMetaDown()) {

                // Remember focused item (so we can keep it in view after refresh)
                final var items = listView.getItems();
                final var focusedItem = focusModel.getFocusedItem();

                listView.refresh();  // or your custom refresh()

                // Restore visibility of the focused cell if it still exists
                if (focusedItem != null && items != null) {
                    int idx = items.indexOf(focusedItem);
                    if (idx >= 0) ensureFocusVisible(idx);
                }

                e.consume();
            }
        };

        initialize();
    }

    /**
     * Initializes this delegate after construction.
     * <p>
     * This method wires up listeners and bindings to ensure the delegate
     * stays in sync with the {@link NfxGridListView} state. Specifically, it:
     * <ul>
     *   <li>Adds the {@link VirtualPane} as the content of the scroll pane.</li>
     *   <li>Registers a listener on the {@code items} list to update cells
     *       when items are added, removed, or replaced.</li>
     *   <li>Registers a listener on the selection model so that visual
     *       selection state reflects the model’s selected items.</li>
     *   <li>Tracks width and styleable properties (such as min cell width
     *       and max cells per row) to recalculate layout when the control
     *       is resized or styled.</li>
     *   <li>Ensures virtualization stays efficient by maintaining the
     *       {@code visibleSet} and {@code itemToNodeMap} in sync with
     *       the viewport.</li>
     * </ul>
     */
    private void initialize(){
        listView.setFocusTraversable(true);
        getStyleClass().add("container");
        setContent(contentPane);
        setFitToWidth(true);

        widthProperty().addListener((obs, oldVal, newVal) -> handleResize());
        heightProperty().addListener((obs, oldVal, newVal) -> handleResize());
        vvalueProperty().addListener((obs, oldVal, newVal) -> updateCells());

        listView.maxCellsPerRowProperty().addListener((obs, oldVal, newVal) -> handleResize());
        listView.minCellWidthProperty().addListener((obs, oldVal, newVal) -> handleResize());
        listView.boxInsetsProperty().addListener((obs, oldVal, newVal) -> handleResize());
        listView.cellSpacingProperty().addListener((obs, oldVal, newVal) -> handleResize());
        currentCellsPerRow.addListener((obs, oldVal, newVal) -> updateCells());

        handleSelectionMode(listView.getSelectionMode());
        listView.selectionModeProperty().addListener((obs, o, mode) -> handleSelectionMode(mode));
        listView.getSelectionModel().getSelectedItems().addListener(selectionModelChangeListener);


        listView.itemsProperty().addListener((obs, o, n) -> {
            if (o != null) {
                o.removeListener(listChangeListener);
            }

            listView.getItems().addListener(listChangeListener);
            /*
             * Clear selected items & focus
             */
            listView.getSelectionModel().clearSelection();
            focusModel.clearFocus();

            onUpdate();
        });

        listView.getItems().addListener(listChangeListener);

        listView.cellFactoryProperty().addListener(obs -> onUpdate());

        listView.cellHeightProperty().addListener(obs -> onUpdate());

        listView.addEventFilter(KeyEvent.KEY_PRESSED,  keyNavHandler);
        listView.addEventFilter(MouseEvent.MOUSE_RELEASED, updateSelectionOnReleaseEvent);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, shiftClickRangeHandler);
        listView.addEventFilter(KeyEvent.KEY_PRESSED,  keyPressedEvent);
        listView.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedEvent);
        listView.addEventFilter(KeyEvent.KEY_PRESSED, selectAllHandler);
        listView.addEventFilter(KeyEvent.KEY_PRESSED, refreshKeyHandler);

        onUpdate();
    }

    /**
     * Refreshes the {@code NfxGridListView} by clearing the current selection
     * and forcing a full update of the visual cells.
     * <p>
     * This method is useful when external changes occur that affect the
     * rendering of cells but are not automatically observed by the view,
     * such as:
     * <ul>
     *   <li>Changes to the underlying data model that do not fire proper
     *       change events.</li>
     *   <li>Updates to visual styling or layout constraints that require
     *       a reset of cached cells.</li>
     * </ul>
     * <p>
     * Calling this method will:
     * <ol>
     *   <li>Clear all selections from the {@link SelectionModel}.</li>
     *   <li>Invoke {@link #onUpdate()} to reset and recompute the layout.</li>
     * </ol>
     */
    public void refresh() {
        listView.getSelectionModel().clearSelection();
        focusModel.clearFocus();
        onUpdate();
    }

    /**
     * Returns the owning grid/list view for this delegate/cell.
     *
     * @return the {@link NfxGridListView} instance associated with this component
     */
    public NfxGridListView<T> getListView() {
        return listView;
    }


    /**
     * Returns a read-only view of the focused item property for observers/bindings.
     *
     * @return the read-only focused item property
     */
    public ReadOnlyObjectProperty<T> focusedItemProperty() {
        return focusModel.focusedItemProperty();
    }

    /**
     * Handles resize events for the {@code GridListDelegate}.
     * <p>
     * This method recalculates how many cells can fit per row based on:
     * <ul>
     *   <li>The current width of the delegate minus horizontal insets
     *       defined by {@link NfxGridListView#getBoxInsets()}.</li>
     *   <li>The {@code minCellWidth} styleable property, which sets
     *       the minimum width of each cell.</li>
     *   <li>The {@code maxCellsPerRow} styleable property, which caps
     *       how many cells may appear in a single row.</li>
     * </ul>
     * <p>
     * If the calculated number of cells per row differs from the current
     * value, the {@code currentCellsPerRow} property is updated. Afterward,
     * {@link #updateCells()} is called to refresh the layout.
     */
    private void handleResize() {
        double availableWidth = getWidth() - listView.getBoxInsets().getLeft() - listView.getBoxInsets().getRight();
        int calculatedCellsPerRow = Math.min(listView.getMaxCellsPerRow(), Math.max(1, (int) (availableWidth / listView.getMinCellWidth())));
        if (currentCellsPerRow.get() != calculatedCellsPerRow) {
            currentCellsPerRow.set(calculatedCellsPerRow);
        }
        updateCells();
    }

    /**
     * Applies the given selection mode to the {@link NfxGridListView}'s selection model.
     * <p>
     * This method updates how selections are handled in the list view,
     * for example:
     * <ul>
     *   <li>{@link SelectionModel.Mode#SINGLE} – only one item can be selected at a time.</li>
     *   <li>{@link SelectionModel.Mode#MULTIPLE} – multiple items can be selected simultaneously.</li>
     * </ul>
     *
     * @param selectionMode the new selection mode (must not be {@code null})
     * @throws NullPointerException if {@code selectionMode} is {@code null}
     */
    private void handleSelectionMode(SelectionModel.Mode selectionMode) {
        Objects.requireNonNull(selectionMode);
        listView.getSelectionModel().setSelectionMode(selectionMode);
    }


    /**
     * Updates visible cells and lays them out inside the {@code VirtualPane},
     * honoring both the outer {@code boxInsets} (space between content and borders)
     * and the per-cell {@code cellSpacing} (margins around each cell).
     * <p>
     * Layout model:
     * <ul>
     *   <li>Horizontal: the inner content width is split into {@code cellsPerRow} equal
     *       slots of size {@code baseSlotW = viewportW / cellsPerRow}. Each cell’s
     *       <em>inner</em> width is {@code baseSlotW - (leftSpacing + rightSpacing)} and
     *       its X is {@code leftGap + col * baseSlotW + leftSpacing}.</li>
     *   <li>Vertical: row pitch is {@code pitchH = cellHeight + topSpacing + bottomSpacing}.
     *       Each cell’s Y is {@code topGap + row * pitchH + topSpacing} and its inner
     *       height is {@code cellHeight - (topSpacing + bottomSpacing)}.</li>
     * </ul>
     * Using this approach ensures consistent spacing between cells while keeping the
     * scrollbar off the cells (thanks to real padding set on the content pane).
     */
    private void updateCells() {
        contentPane.beginBatch();
        try {
            // ----- Outer gutters (keeps cells away from ScrollPane borders) -----
            final Insets box = listView.getBoxInsets();
            final double leftGap   = box.getLeft();
            final double rightGap  = box.getRight();
            final double topGap    = box.getTop();
            final double bottomGap = box.getBottom();

            // Reserve REAL space so scrollbars don’t overlap cells
            contentPane.setPadding(box);

            // ----- Per-cell spacing (margins around each cell) -----
            final Insets spacing = listView.getCellSpacing();
            final double sL = spacing.getLeft();
            final double sR = spacing.getRight();
            final double sT = spacing.getTop();
            final double sB = spacing.getBottom();
            final double hMargin = sL + sR;   // horizontal margin per cell
            final double vMargin = sT + sB;   // vertical margin per cell

            // ----- Dimensions and guards -----
            final double viewportW   = Math.max(0, getWidth()  - leftGap - rightGap);
            final double viewportH   = Math.max(0, getHeight());
            final int    cellsPerRow = Math.max(1, currentCellsPerRow.get());

            // Base slot width allocated to each column (before subtracting cell margins)
            final double baseSlotW   = viewportW / cellsPerRow;

            // Inner width/height available to the cell content after margins
            final double cellInnerW  = Math.max(1, baseSlotW - hMargin);
            final double cellInnerH0 = Math.max(1, listView.getCellHeight()); // requested cell content height
            final double cellInnerH  = Math.max(1, cellInnerH0 - vMargin);

            // Vertical pitch between rows (content height + margins)
            final double rowPitch    = cellInnerH0 + vMargin;

            final int totalItems = listView.getItems().size();
            if (totalItems == 0) {
                if (!contentPane.getChildren().isEmpty()) {
                    contentPane.getChildren().clear();
                    visibleSet.clear();
                }
                contentPane.setContentHeight(topGap + bottomGap);
                return;
            }

            // ----- Content height includes top/bottom gutters and row spacing -----
            final int rowCount        = (int) Math.ceil((double) totalItems / cellsPerRow);
            final double rowsHeight   = rowCount * rowPitch;
            final double contentHeight= topGap + rowsHeight + bottomGap;
            contentPane.setContentHeight(contentHeight);

            // ----- Compute visible rows (account for top gutter) -----
            final double scrollable = Math.max(1, contentHeight - viewportH);
            final double v          = Math.min(1, Math.max(0, getVvalue()));
            final double scrolledY  = v * scrollable;
            final double contentY   = Math.max(0, scrolledY - topGap);

            int firstVisibleRow     = (int) Math.floor(contentY / rowPitch);
            int visibleRowCount     = (int) Math.ceil(viewportH / rowPitch);

            int firstRow            = Math.max(0, firstVisibleRow - BUFFER_ROWS);
            int lastRowExclusive    = Math.min(rowCount, firstVisibleRow + visibleRowCount + BUFFER_ROWS);

            // ----- Build new visible set -----
            final int expectedCells = Math.min(totalItems, cellsPerRow * Math.max(0, lastRowExclusive - firstRow));
            final ArrayList<Node> newCells = new ArrayList<>(expectedCells);

            for (int row = firstRow; row < lastRowExclusive; row++) {
                final double y = topGap + row * rowPitch + sT;  // top gutter + row pitch + top margin
                final int baseIndex = row * cellsPerRow;

                for (int col = 0; col < cellsPerRow; col++) {
                    final int index = baseIndex + col;
                    if (index >= totalItems) break;

                    final double x = leftGap + col * baseSlotW + sL; // left gutter + column slot + left margin

                    final T item       = listView.getItems().get(index);
                    final Node cellNode= getCellNode(item);

                    // Size and position the inner content area (after margins)
                    cellNode.resizeRelocate(x, y, cellInnerW, cellInnerH);

                    newCells.add(cellNode);
                }
            }

            // ----- Minimal-diff child update -----
            final Set<Node> newSet = Collections.newSetFromMap(new IdentityHashMap<>());
            newSet.addAll(newCells);

            if (!visibleSet.isEmpty()) {
                ArrayList<Node> toRemove = null;
                for (Node old : visibleSet) {
                    if (!newSet.contains(old)) {
                        if (toRemove == null) toRemove = new ArrayList<>();
                        toRemove.add(old);
                    }
                }
                if (toRemove != null) contentPane.getChildren().removeAll(toRemove);
            }

            if (!newCells.isEmpty()) {
                ArrayList<Node> toAdd = null;
                for (Node n : newCells) {
                    if (!visibleSet.contains(n)) {
                        if (toAdd == null) toAdd = new ArrayList<>();
                        toAdd.add(n);
                    }
                }
                if (toAdd != null) contentPane.getChildren().addAll(toAdd);
            }

            visibleSet.clear();
            visibleSet.addAll(newSet);
        } finally {
            contentPane.endBatch();
        }
    }


    /**
     * Returns the visual node associated with the given item.
     * <p>
     * This method checks the {@code itemToNodeMap} cache:
     * <ul>
     *   <li>If a node already exists for the item, it is returned directly.</li>
     *   <li>If no node exists, a new cell is created via {@link #createCell(Object)},
     *       its node is retrieved, stored in the map, and then returned.</li>
     * </ul>
     * <p>
     * This ensures that each item is backed by a single node instance,
     * supporting efficient reuse during virtualization.
     *
     * @param item the item whose cell node is requested
     * @return the node corresponding to the given item
     */
    private Node getCellNode(T item) {
        if (!itemToNodeMap.containsKey(item)) {
            Node cellNode = createCell(item).node();
            itemToNodeMap.put(item, cellNode);
        }
        return itemToNodeMap.get(item);
    }

    /**
     * Returns the {@link NfxGridCell} associated with the given item.
     * <p>
     * This method delegates to {@link #getCellNode(Object)} and casts the
     * result to {@code NfxGridCell<T>}. If the item is {@code null},
     * this method returns {@code null}.
     * <p>
     * The {@code @SuppressWarnings("all")} annotation is currently used
     * to silence unchecked cast warnings. This can be narrowed to
     * {@code "unchecked"} for clarity.
     *
     * @param item the item for which to retrieve the cell
     * @return the corresponding {@code NfxGridCell}, or {@code null} if the item is {@code null}
     */
    @SuppressWarnings("all")
    public NfxGridCell<T> getCell(T item) {
        if (item == null){
            return null;
        }
        return (NfxGridCell<T>) getCellNode(item);
    }

    /**
     * Returns the first item in the backing {@link NfxGridListView}.
     * <p>
     * If the list of items is empty, this method returns {@code null}.
     *
     * @return the first item, or {@code null} if the list is empty
     */
    public T getFirstItem() {
        return listView.getItems().isEmpty()
                ? null
                : listView.getItems().getFirst();
    }

    /**
     * Returns the last item in the backing {@link NfxGridListView}.
     * <p>
     * If the list of items is empty, this method returns {@code null}.
     *
     * @return the last item, or {@code null} if the list is empty
     */
    public T getLastItem() {
        return listView.getItems().isEmpty()
                ? null
                : listView.getItems().getLast();
    }


    /**
     * Smoothly scrolls the viewport to bring the row containing the given item into view.
     * <p>
     * The target vertical scroll value is computed from the item's row index:
     * {@code vvalue = (row * cellHeight) / (contentHeight - viewHeight)}, then clamped to {@code [0,1]}.
     * If the item is not present or content is not scrollable, this method returns without action.
     *
     * @param item the item to scroll into view; if {@code null} or not found, no scrolling occurs
     */
    public void scroll(T item) {
        if (item == null || listView.getItems().isEmpty()) {
            return;
        }

        int index = listView.getItems().indexOf(item);
        if (index < 0) {
            return;
        }

        int cellsPerRow = currentCellsPerRow.get();
        if (cellsPerRow <= 0) {
            handleResize();
            cellsPerRow = Math.max(1, currentCellsPerRow.get());
        }

        int row = index / cellsPerRow;
        double cellHeight = Math.max(0.0, listView.getCellHeight());
        double contentHeight = Math.max(0.0, contentPane.getHeight());
        double viewHeight = Math.max(0.0, getHeight());

        double denom = Math.max(1.0, contentHeight - viewHeight);
        double target = (row * cellHeight) / denom;
        double clamped = Math.max(0.0, Math.min(1.0, target));

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0.5),
                        new KeyValue(vvalueProperty(), clamped, Interpolator.EASE_BOTH)
                )
        );
        timeline.setOnFinished(evt -> updateCells());
        timeline.play();
    }


    /**
     * Creates an {@link NfxGridCell} for the given item using the control's {@code cellFactory}.
     * <p>
     * The created cell is updated with the provided item and then wrapped into a {@link Cell}
     * so the delegate can manage it uniformly as a {@link Node}.
     *
     * @param item the item for which to create the cell
     * @return the created cell wrapper
     * @throws IllegalStateException if no cell factory is set or it returns {@code null}
     */
    private Cell<T, ?> createCell(T item) {
        Callback<NfxGridListView<T>, NfxGridCell<T>> factory = listView.getCellFactory();
        if (factory == null) {
            throw new IllegalStateException("NfxGridListView.cellFactory must be set before creating cells.");
        }

        NfxGridCell<T> cell = factory.call(this.listView);
        if (cell == null) {
            throw new IllegalStateException("Cell factory returned null NfxGridCell.");
        }

        cell.update(item);
        return Cell.wrap(cell);
    }


    /**
     * Handles a full update pass triggered by changes to items, sizing, or styling.
     * <p>
     * This method clears cached state and then recomputes layout-dependent values
     * (such as cells-per-row) by delegating to {@link #handleResize()}.
     */
    private void onUpdate() {
        reset();
        handleResize();
    }

    /**
     * Resets internal virtualization state.
     * <p>
     * This:
     * <ul>
     *   <li>Clears the visual content in the {@link VirtualPane}.</li>
     *   <li>Empties the item-to-node map so new cells can be created as needed.</li>
     *   <li>Clears the set of currently visible nodes.</li>
     * </ul>
     * Invoke this before recomputing layout or repopulating cells.
     */
    private void reset() {
        contentPane.reset();
        itemToNodeMap.clear();
        visibleSet.clear();
    }


    /**
     * Ensures the item at the given {@code index} has its cell fully visible within the viewport.
     * <p>
     * Behavior:
     * <ol>
     *   <li>Validates {@code index} against the current items.</li>
     *   <li>Resolves the cell {@link Node} via {@code getCellNode(item)}; no-ops if missing.</li>
     *   <li>Computes the current visible vertical range from {@code getViewportBounds()} and {@code getVvalue()}.</li>
     *   <li>If the cell lies above/below the visible range, adjusts {@code vvalue} to reveal it.</li>
     *   <li>Requests focus on the cell node so CSS (e.g., {@code :focused}) can update.</li>
     * </ol>
     *
     * @param index zero-based index of the item whose cell should be revealed
     *
     * @implNote
     * Uses {@code layoutY} and {@code prefHeight(-1)} to estimate the cell’s vertical span and assumes
     * vertical scrolling with {@code vvalue} in {@code [0,1]}. No scrolling occurs if the cell is already
     * within the visible region, if the node is not attached, or if sizes are unavailable.
     */
    private void ensureFocusVisible(int index) {
        if (index < 0 || index >= listView.getItems().size()) return;

        final T it = listView.getItems().get(index);
        final Node n = getCellNode(it);
        if (n == null) return;

        // Dimensions
        double viewportH = getViewportBounds().getHeight();
        double vvalue    = getVvalue();
        double contentH  = contentPane.getHeight();

        // Position of the cell in content coords
        double cellY = n.getLayoutY();
        double cellH = n.prefHeight(-1);

        // Current visible range in content coords
        double visibleStart = vvalue * (contentH - viewportH);
        double visibleEnd   = visibleStart + viewportH;

        // Only scroll if outside viewport
        if (cellY < visibleStart) {
            // Scroll up to reveal top of cell
            double newV = cellY / (contentH - viewportH);
            setVvalue(Math.max(0, newV));
        } else if (cellY + cellH > visibleEnd) {
            // Scroll down to reveal bottom of cell
            double newV = (cellY + cellH - viewportH) / (contentH - viewportH);
            setVvalue(Math.min(1, newV));
        }
        n.requestFocus();
    }

    /**
     * Estimates the number of fully visible rows in the current viewport.
     * <p>
     * Calculation:
     * <ul>
     *   <li>{@code pitch} = {@code cellHeight} + vertical spacing (top + bottom)</li>
     *   <li>{@code rows} = {@code floor(viewportHeight / pitch)}</li>
     * </ul>
     * Guards against zero/negative dimensions with {@code Math.max(1, ...)} so the
     * result is always at least 1.
     *
     * @return estimated count of fully visible rows (≥ 1)
     */
    private int visibleRowCountEstimate() {
        double viewportH = Math.max(1, getHeight());
        double cellH     = Math.max(1, listView.getCellHeight());
        Insets s         = listView.getCellSpacing();
        double pitch     = cellH + s.getTop() + s.getBottom();
        return (int) Math.max(1, Math.floor(viewportH / pitch));
    }

    /**
     * Returns whether this control (the {@code listView}) currently owns focus,
     * i.e., the Scene's {@code focusOwner} is either the {@code listView} itself
     * or any of its descendant nodes.
     *
     * <p>Used to gate keyboard navigation so we don't react when focus is
     * elsewhere in the scene (e.g., a search field or another control).</p>
     *
     * @return {@code true} if the scene's focus owner is the listView or a child of it; {@code false} otherwise
     */
    private boolean isOurFocusOwner() {
        final var scene = listView.getScene();
        if (scene == null) return false;
        final var f = scene.getFocusOwner();
        return f == listView || (f != null && isNodeDescendantOf(f, listView));
    }

    /**
     * Returns whether {@code node} is a descendant of (or equal to) {@code ancestor}
     * by walking up the parent chain using {@link Node#getParent()}.
     *
     * @param node      the node to test; may be {@code null}
     * @param ancestor  the potential ancestor; may be {@code null}
     * @return {@code true} if {@code ancestor} appears on {@code node}'s parent chain (including {@code node} itself);
     *         {@code false} otherwise
     *
     * @implNote Runs in O(depth) time using reference equality. Works even if nodes are not yet in a Scene.
     */
    private boolean isNodeDescendantOf(Node node, Parent ancestor) {
        for (Node n = node; n != null; n = n.getParent()) if (n == ancestor) return true;
        return false;
    }

    /**
     * Replaces the current selection with the inclusive range {@code [a, b]}.
     * <p>
     * Behavior:
     * <ul>
     *   <li>Swaps {@code a} and {@code b} if {@code a > b} so the range is well-ordered.</li>
     *   <li>Builds a contiguous list of items from the backing {@code listView.getItems()}.</li>
     *   <li>Applies the selection atomically via {@code setAll(range)} (clear then add).</li>
     * </ul>
     *
     * <p><b>Preconditions:</b> The caller should ensure the selection model is in
     * {@code MULTIPLE} mode if range selection is desired.</p>
     *
     * @param a start index (inclusive), may be greater than {@code b}
     * @param b end index (inclusive), may be less than {@code a}
     *
     * @implNote This is O(n) over the range length; indices are assumed valid for the current items.
     */
    private void replaceRangeSelection(int a, int b) {
        if (a > b) { int t = a; a = b; b = t; }
        final var sm    = listView.getSelectionModel();
        final var items = listView.getItems();

        final int size = b - a + 1;
        final ArrayList<T> range = new ArrayList<>(size);
        for (int i = a; i <= b; i++) range.add(items.get(i));

        sm.select(range); // atomic: clear then add range
    }

    /**
     * Finds the nearest {@link NfxGridCell} ancestor of the given mouse event's target.
     * <p>
     * Starts from {@code e.getTarget()} and walks up the parent chain until an
     * {@code NfxGridCell<?>} is found, then casts it to {@code NfxGridCell<T>} and returns it.
     * Returns {@code null} if no such ancestor exists.
     *
     * @param e the mouse event whose target hierarchy will be searched
     * @return the enclosing {@code NfxGridCell<T>} for the event target, or {@code null} if none
     *
     * @implNote Uses reference equality and {@link Node#getParent()} traversal (O(depth)).
     * The {@code @SuppressWarnings("unchecked")} is safe because the cell instance
     * originates from this control's own cell factory, which is parameterized with {@code T}.
     */
    @SuppressWarnings("unchecked")
    private NfxGridCell<T> findCellFromEvent(MouseEvent e) {
        Node n = (Node) e.getTarget();
        while (n != null) {
            if (n instanceof NfxGridCell<?> c) {
                return (NfxGridCell<T>) c;
            }
            n = n.getParent();
        }
        return null;
    }
}
