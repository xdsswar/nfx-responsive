# NfxGridListView & NfxGridLayout

A pair of JavaFX building blocks for responsive, keyboard‑friendly grid UIs:

- **NfxGridListView** — a grid‑aware, virtualized list view with precise focus and selection behavior designed for keyboard navigation first.
- **NfxGridLayout** — a breakpoint‑driven, responsive grid container. **Only NfxGridItem children are supported**, because those items carry the breakpoint properties used by the layout.

No global scene hooks, no surprise side effects — everything is scoped to the control.

---

## What’s inside

- Overview of key concepts
- NfxGridListView: features, navigation, focus/selection rules, scrolling/visibility, styling hooks
- NfxGridLayout: features, breakpoint model, layout behavior, styling hooks
- Known limitations and FAQ

---

## Demo Video
[![Watch the demo](https://img.youtube.com/vi/TsJTyd2h_VM/hqdefault.jpg)](https://www.youtube.com/watch?v=TsJTyd2h_VM)

---

## Screenshots
### Dashboard Example
![Dashboard Maximized](screenshots/1.png)


![Dashboard Maximized](screenshots/2.png)


![Dashboard Maximized](screenshots/3.png)

---

## NfxGridListView

### Purpose
NfxGridListView displays items in a grid where the number of cells per row can change with available space. It emphasizes predictable keyboard behavior, one focused cell at a time, and clear selection semantics.

### Key capabilities
- **Grid‑aware navigation**: Up/Down move by the number of cells per row, Left/Right move within a row, with Home/End and Page Up/Down shortcuts.
- **Focus‑only cursoring**: Arrow keys move the focus without mutating selection, preventing unintended multi‑selection growth.
- **Deterministic range selection**: Shift + click replaces the current selection with the exact range between the anchor and the clicked cell.
- **Exclusive activation**: Enter selects the focused item exclusively (the previously selected items are cleared).
- **Select all**: Ctrl or Command + A selects all items at once.
- **Refresh**: F5 triggers a visual refresh and preserves the focused item’s visibility if it still exists.
- **Local‑only event handling**: Keyboard and mouse interactions are attached to the control itself, avoiding conflicts with other parts of your application.
- **Virtualization**: Only visible cells are realized; off‑screen cells are recycled to keep scrolling smooth.

### Navigation and shortcuts
- **Arrow keys**: Move focus only. Up/Down step by the current cells‑per‑row value; Left/Right step within a row.
- **Home / End**: Jump to the first or last cell of the current row.
- **Page Up / Page Down**: Move by an estimated number of visible rows.
- **Enter**: Activates exclusive selection for the focused cell.
- **Shift + Click**: Selects a contiguous range, replacing any items outside that range.
- **Ctrl or Command + A**: Selects all items.
- **F5**: Refreshes the view.

### Focus and selection model
- **Single focused cell**: A lightweight, item‑based focus model guarantees that at most one cell is visually focused at any time.
- **Selection model**: Supports single or multiple selection modes with atomic operations to replace the selection list, ensuring consistent results during range and select‑all actions.
- **Range anchor rule**: If a selection exists, the anchor is the most recently selected item; otherwise the focused item is used. The final range is always exactly between the anchor and the clicked cell.

### Scrolling and visibility
- The view keeps the focused cell visible by adjusting the scroll position when focus changes via keyboard.
- Page navigation uses a row‑estimate based on current viewport size, cell size, and spacing to feel natural.

### Styling hooks
- **Cell style class**: grid‑cell.
- **Pseudo‑classes**: selected, focused, and (optionally) pressed if you toggle it from your cell logic.
- **Inner content coloring**: The styling model expects text and graphics inside each cell to change appearance when the cell is selected or focused. If you are using raw text nodes, ensure they are styled as descendants so they can inherit selected visuals.

### Integration tips
- Keep keyboard navigation focus‑only; perform selection on explicit activation (Enter) or by mouse gestures. This avoids “sticky” selections while navigating.
- If you support toggling by keyboard (for example, a modifier plus Space), apply selection changes atomically to avoid flicker and race conditions with default behaviors.
- Because event handling is local, this control won’t interfere with global shortcuts or other controls in your Scene.

---

## NfxGridLayout

### Purpose
NfxGridLayout is a responsive container that arranges content across breakpoints, similar to a CSS grid. It uses a 12‑column conceptual grid and recomputes layout when the container width crosses defined thresholds.

### Critical requirement
**Only NfxGridItem children are supported.** NfxGridItem carries the breakpoint properties (such as spans and optional visibility) that NfxGridLayout reads to perform responsive placement. Regular nodes are not accepted as direct children.

### Key capabilities
- **Breakpoint‑driven layout**: Items declare spans per breakpoint (for example, compact through extra‑large). As the container width changes, items reflow automatically.
- **12‑column grid**: Column spans are evaluated against a 12‑column model and wrap as needed to new rows.
- **Consistent rhythm**: Horizontal and vertical gaps and container padding produce a clean, predictable layout rhythm.
- **Visibility and ordering**: If provided by your NfxGridItem API, items may be shown or hidden per breakpoint and can influence placement order predictably.

### How it lays out
- The container listens to size changes and determines the active breakpoint.
- Each NfxGridItem contributes its span for the active breakpoint.
- Items are placed left‑to‑right, top‑to‑bottom, wrapping based on available columns, with gaps and padding respected.

### Styling hooks
- Apply styling to the content inside NfxGridItem as you would with standard JavaFX nodes. The layout itself focuses on structure and spacing rather than visuals.
- Keep typography and color concerns inside the item content so that breakpoint changes do not alter the visual language unexpectedly.

---

## Known limitations

- NfxGridLayout requires NfxGridItem as direct children; other node types are not supported.
- NfxGridListView maintains only one focused cell by design to keep keyboard navigation predictable.
- Because all event handling is local to the controls, application‑wide shortcuts are unaffected. If you need global shortcuts, wire them at the application level.

---

## FAQ

**Why don’t arrow keys change selection in NfxGridListView?**  
To keep navigation predictable and avoid growing the selection by accident. Selection changes happen on explicit activation or by mouse selection gestures.

**Why does Shift plus click replace the selection instead of extending it?**  
This control favors deterministic range selection. The result is always the contiguous range between the anchor and the clicked cell, with everything else cleared.

**Can I change how selected cells look?**  
Yes. Use the grid‑cell style class and pseudo‑classes such as selected and focused. Ensure your inner text and graphics are styled as descendants so they reflect the selected state.

**What happens to focus during refresh?**  
The view attempts to keep the previously focused item visible and focused if it still exists after refresh.

---





