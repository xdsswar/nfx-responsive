# Nfx Responsive Toolkit for JavaFX

**Primary:** `NfxFluidPane` — CSS‑driven, responsive fluid grid  
**Also included:** `NfxGridListView` (keyboard‑first virtualized grid list), `NfxGridLayout` (breakpoint grid for `NfxGridItem`)

No global scene hooks, no surprise side effects — everything is scoped to the control.

> 👉 Looking for the doughnut chart? It now lives in its own doc: **[NfxDoughnut Chart README](./NfxDoughnut.md)**

---

## Table of Contents
1. [NfxFluidPane (Primary)](#nfxfluidpane-primary)
    - [What it is](#what-it-is)
    - [Breakpoints](#breakpoints)
    - [Child layout via CSS](#child-layout-via-css)
    - [Child layout via Java API](#child-layout-via-java-api)
    - [Resolution order & rules](#resolution-order--rules)
    - [Examples](#examples)
    - [Tips & Troubleshooting](#tips--troubleshooting)
2. [NfxGridLayout](#nfxgridlayout)
    - [Purpose](#purpose)
    - [How it lays out](#how-it-lays-out)
    - [Using NfxGridItem](#using-nfxgriditem)
    - [Integration tips](#integration-tips)
3. [NfxGridItem](#nfxgriditem)
    - [Breakpoint spans & offsets](#breakpoint-spans--offsets)
    - [Gaps](#gaps)
4. [NfxGridListView](#nfxgridlistview)
    - [What it is](#what-it-is-1)
    - [Keyboard model](#keyboard-model)
    - [Selection model](#selection-model)
    - [Scrolling & virtualization](#scrolling--virtualization)
    - [Public API](#public-api)
    - [CSS hooks](#css-hooks)
5. [NfxGridCell](#nfxgridcell)
6. [Screenshots](#screenshots)
7. [Known Limitations](#known-limitations)
8. [FAQ](#faq)

---

## NfxFluidPane (Primary)

### What it is
`NfxFluidPane` is a fluid, multi‑row **12‑column** layout container. It arranges direct children into rows with **spans**, **offsets**, and **margins**, and switches layouts at **breakpoints**: `xs`, `sm`, `md`, `lg`, `xl`, `xxl`. It preserves child order and is RTL‑aware.

### Breakpoints

| Key | Typical Target            | Notes                                  |
|-----|---------------------------|----------------------------------------|
| xs  | Phones                    | Anything below the `sm` threshold      |
| sm  | Large phones / small tabs |                                        |
| md  | Tablets                   |                                        |
| lg  | Small laptops             |                                        |
| xl  | Desktops                  |                                        |
| xxl | Wide desktops             |                                        |

The active breakpoint is derived from the pane’s width and configurable thresholds. The pane computes rows against a fixed **12‑column** grid at every breakpoint.

### Child layout via CSS

Each child node can declare its grid constraints via CSS‑like custom properties. You can scope these to breakpoints using pseudo‑classes on your selectors: `.card:md { … }`, `.sidebar:xl { … }`.

**Layout properties (per child):**

| Property        | Type            | Range / Format                | Breakpoint‑aware | Description |
|-----------------|-----------------|-------------------------------|------------------|-------------|
| `-col-span`     | integer         | `1..12`                       | ✔                | How many columns the child spans. If an empty row would overflow (`offset + span > 12`), span is **shrunk to fit** the remaining columns. |
| `-col-offset`   | integer         | `0..11`                       | ✔                | Columns to skip before placing the child on a row. |
| `-col-margin`   | Insets (shorthand) | `a` • `v h` • `t h b` • `t r b l` | ✔           | Outside margins for the child. Units: number or `px`. |
| `-col-align`    | string          | e.g., `start` \| `center` \| `end`  | ✔           | Horizontal alignment **inside its allocated span** (optional). |

> Breakpoint scoping uses **pseudo‑classes** on the selector (e.g., `.tile:lg { -col-span: 4; }`). Base rules apply to all breakpoints; exact `:bp` rules override the base for that breakpoint.

### Child layout via Java API

Every layout property has per‑breakpoint setters/getters:

**Spans (1..12)**  
`setXsColSpan(node, int)`, `setSmColSpan(node, int)`, `setMdColSpan(node, int)`, `setLgColSpan(node, int)`, `setXlColSpan(node, int)`, `setXxlColSpan(node, int)`  
`getXsColSpan(node)`, `getSmColSpan(node)`, `getMdColSpan(node)`, `getLgColSpan(node)`, `getXlColSpan(node)`, `getXxlColSpan(node)`

**Offsets (0..11)**  
`setXsColOffset(node, int)`, `setSmColOffset(node, int)`, `setMdColOffset(node, int)`, `setLgColOffset(node, int)`, `setXlColOffset(node, int)`, `setXxlColOffset(node, int)`  
`getXsColOffset(node)`, `getSmColOffset(node)`, `getMdColOffset(node)`, `getLgColOffset(node)`, `getXlColOffset(node)`, `getXxlColOffset(node)`

**Margins (Insets)**  
`setXsInsets(node, Insets)`, `setSmInsets(node, Insets)`, `setMdInsets(node, Insets)`, `setLgInsets(node, Insets)`, `setXlInsets(node, Insets)`, `setXxlInsets(node, Insets)`  
`getXsInsets(node)`, `getSmInsets(node)`, `getMdInsets(node)`, `getLgInsets(node)`, `getXlInsets(node)`, `getXxlInsets(node)`

> **Edge gutters:** it’s common to clamp outside gutters: zero left margin at column 0 and zero right margin at the last column.

### Resolution order & rules
- **Last‑wins** within the same breakpoint: later rules override earlier ones.
- **Exact breakpoint overrides base:** `.card:md` overrides `.card` at `md`.
- **Order‑preserving packing:** children are placed left→right, top→bottom; wrap when a row fills. If the row is empty and `offset + span > 12`, span is reduced to fit.

### Examples

Attach classes and rely on CSS utilities:

```java
NfxFluidPane grid = new NfxFluidPane();
grid.getStyleClass().add("nfx-grid");

Label a = new Label("A");
Label b = new Label("B");
Label c = new Label("C");
a.getStyleClass().addAll("box", "card");
b.getStyleClass().addAll("box", "card");
c.getStyleClass().addAll("box", "sidebar", "demo");

grid.getChildren().addAll(a, b, c);
```

```css
/* Layout utilities */
.card:xs  { -col-span: 12; -col-margin: 6; }
.card:sm  { -col-span: 6;  -col-margin: 8; }   /* 2-up on sm */
.card:lg  { -col-span: 4;  -col-margin: 10; }  /* 3-up on lg+ */

.sidebar:xs { -col-span: 12; }
.sidebar:lg { -col-span: 3;  -col-offset: 1; }

/* Optional visual skins per breakpoint */
.box:xs  { -fx-background-color: #fff7f6; -fx-border-color: #ff6f85; }
.box:sm  { -fx-background-color: #fffbed; -fx-border-color: #e6b800; }
.box:md  { -fx-background-color: #f2fff6; -fx-border-color: #2dbd2d; }
.box:lg  { -fx-background-color: #f4f9ff; -fx-border-color: #3399ff; }
.box:xl  { -fx-background-color: #f7f4ff; -fx-border-color: #8b6ff0; }
.box:xxl { -fx-background-color: #fff7ee; -fx-border-color: #ff8c2b; }
```

### Tips & Troubleshooting
- **Left gap at xs**: ensure `-col-offset: 0` is allowed; offsets clamp to `[0..11]`.
- **Overflow at xs**: if a row is empty and `offset + span > 12`, span is shrunk to fit.
- **Last row clipped**: let the pane compute its preferred height; in a `ScrollPane`, avoid forcing `fitToHeight=true` unless desired.

---

## NfxGridLayout

### Purpose
`NfxGridLayout` is a responsive container that arranges **only** `NfxGridItem` children on a 12‑column grid across breakpoints. It listens to width changes, resolves the active breakpoint, and reflows items predictably.

### How it lays out
1. The container determines the active breakpoint from its width.
2. Each `NfxGridItem` contributes its **span** and **offset** for that breakpoint.
3. Items are placed left→right, top→bottom, wrapping as needed. Container padding and item gaps are respected.

### Using `NfxGridItem`
Add only `NfxGridItem` instances as direct children (other node types are rejected automatically). Within each `NfxGridItem`, put the actual content node (e.g., your card, chart, etc.).

```java
NfxGridLayout root = new NfxGridLayout();

NfxGridItem tile1 = new NfxGridItem();
tile1.setSmColSpan(6);     // 2-up on sm+
tile1.setMdColSpan(4);     // 3-up on md+
tile1.setContent(new MyCard());

root.getChildren().addAll(tile1 /*, tile2, ... */);
```

**`NfxGridItem` layout fields the layout reads**
- **Spans per bp (1..12)**: `setXsColSpan(int)`, `setSmColSpan(int)`, `setMdColSpan(int)`, `setLgColSpan(int)`, `setXlColSpan(int)`, `setXxlColSpan(int)`  
  Default XS = full width (`12`).
- **Offsets per bp (0..11)**: `setXsColOffset(int)`, `setSmColOffset(int)`, `setMdColOffset(int)`, `setLgColOffset(int)`, `setXlColOffset(int)`, `setXxlColOffset(int)`  
  Default = `0`.
- **Per‑item gaps (px)**: `setLeftGap(double)`, `setRightGap(double)`, `setTopGap(double)`, `setBottomGap(double)`  
  Default = `0` each.

### Integration tips
- Encapsulate visuals inside each `NfxGridItem` so changes in breakpoint don’t affect your visual hierarchy.
- Use item gaps for fine‑grained spacing; use container padding for outer rhythm.

---

## NfxGridItem

An item container that carries breakpoint‑specific spans/offsets and optional per‑side **gaps** read by `NfxGridLayout`.

### Breakpoint spans & offsets
- **Span (1..12)**: `get/setXsColSpan`, `Sm`, `Md`, `Lg`, `Xl`, `Xxl` — default XS span is full width (`12`).
- **Offset (0..11)**: `get/setXsColOffset`, `Sm`, `Md`, `Lg`, `Xl`, `Xxl` — default `0`.

Values are clamped by the layout; invalid inputs are normalized.

### Gaps
- **Per‑side gaps (px)** applied around this item: `leftGap`, `rightGap`, `topGap`, `bottomGap`.  
  Each is a `DoubleProperty`; default `0` (no extra gap).

---

## NfxGridListView

### What it is
A **virtualized grid list** with a predictable keyboard model. Scales to large item counts by realizing only visible cells. Focus is separate from selection to avoid accidental selection growth during navigation.

### Keyboard model
- **Arrow keys**: move focus only. Up/Down jump by the current cells‑per‑row; Left/Right move within a row.
- **Home / End**: move focus to start/end of current row.
- **Page Up / Page Down**: move by an estimated number of visible rows.
- **Enter**: activate exclusive selection on the focused cell.
- **Shift + Click**: select contiguous range (anchor → clicked), replacing any prior selection.
- **Ctrl/⌘ + A**: select all.
- **F5**: refresh skin and try to keep focused cell visible if it still exists.

### Selection model
- Single focused cell at a time (visual cursor).
- Selection modes via `SelectionModel.Mode` (`SINGLE`, `MULTIPLE`).
- **Deterministic range** semantics (anchor + end define the exact contiguous range).

### Scrolling & virtualization
- Keeps the focused cell visible by adjusting scroll when focus changes via keys.
- Virtualizes offscreen cells for smoother scrolling.

### Public API

| Property                  | Type                     | Default                | CSS (if styleable)            | Description |
|--------------------------|--------------------------|------------------------|-------------------------------|-------------|
| `minCellWidth`           | `double`                 | `50.0`                 | `-fx-min-cell-width`          | Minimum width a cell will occupy before wrapping to fewer per row. |
| `cellHeight`             | `double`                 | `100.0`                | `-fx-cell-height`             | Fixed cell height used for layout/virtualization. |
| `maxCellsPerRow`         | `int`                    | `Integer.MAX_VALUE`    | `-fx-max-cells-per-row`       | Hard cap on cells per row regardless of width. |
| `boxInsets`              | `Insets`                 | `Insets.EMPTY`         | `-fx-box-insets`              | Padding inside each cell's content box. |
| `cellSpacing`            | `Insets`                 | `Insets.EMPTY`         | `-cell-spacing`               | Extra spacing around cells (`top right bottom left`). |
| `placeHolder`            | `Node`                   | `null`                 | —                             | Optional placeholder node (e.g., when list is empty). |
| `selectionMode`          | `SelectionModel.Mode`    | —                      | —                             | `SINGLE` or `MULTIPLE`. |
| `unselectOnClick`        | `boolean`                | `false`                | —                             | If `true`, clicking an already‑selected cell will unselect it. |

> Notes: In doc comments, some CSS names appear with `-fx-` prefixes; the control also documents custom names like `-cell-spacing`. Use the ones above as published by the control.


### CSS hooks
- **Cell style class:** `grid-cell`
- **Pseudo‑classes:** `selected`, `focused`
- **Default cell factory label class:** `default-grid-cell-factory-label` (for the built‑in simple label factory)

---

## NfxGridCell
Cell used by `NfxGridListView`.  
Adds style class **`grid-cell`** and toggles pseudo‑classes **`selected`** and **`focused`**; style your inner content to react to these states (e.g., change text fill, background, border).

---

## Screenshots

![Dashboard Maximized](screenshots/1.png)

![Dashboard Maximized](screenshots/2.png)

![Dashboard Maximized](screenshots/3.png)

---

## Known Limitations

- `NfxGridLayout` accepts only `NfxGridItem` as direct children; other node types are rejected.
- `NfxGridListView` maintains a single focused cell; this is intentional for a predictable keyboard model.
- Event handling for controls is local and won’t interfere with application‑wide shortcuts.

---

## FAQ

**Why don’t arrow keys change selection in `NfxGridListView`?**  
To keep navigation predictable and avoid growing the selection by accident. Selection changes happen on Enter, with range by Shift+Click, or via explicit APIs.

**How do breakpoint CSS rules override base rules?**  
Base class rule (e.g., `.card`) applies everywhere unless a specific breakpoint rule exists (e.g., `.card:md`). The exact breakpoint wins at that breakpoint.

**What if a child’s `offset + span` overflows an empty row?**  
`NfxFluidPane` shrinks the span to fit the remaining columns on that row.

**Does RTL affect placement order?**  
`NfxFluidPane` respects `NodeOrientation.RIGHT_TO_LEFT` while preserving child order.
