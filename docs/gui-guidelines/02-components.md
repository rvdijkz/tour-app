# Components Specification

Detailed specifications for all reusable UI components.

---

## Forms

### Text Input

Purpose: Single-line text entry
Usage: Username, email, search fields

Visual:

- Height: 40px
- Padding: 8px 12px
- Border: 1px solid #CCCCCC
- Border-radius: 4px
- Font: 14px Regular
- Placeholder: #999999 italic

States:

- Default: White background, gray border
- Focus: Blue border (2px), blue shadow
- Error: Red border, red text below
- Disabled: Gray background, gray text
- Filled: White background, dark text

---

### Number Input

Similar to text input, but:

- May have spinner controls (↑↓)
- Only numeric characters allowed
- Right-aligned text
- Example: Point budget tracker

---

### Select Dropdown

Purpose: Choose from predefined options
Usage: Country, status, roles

Visual:

- Height: 40px
- Padding: 8px 12px (left), 36px (right for arrow)
- Arrow icon: 16x16px, right-aligned
- Options: 32px height each, 12px padding
- Selected option: Blue bg, white text

Behavior:

- Click to open dropdown
- Arrow icon indicates open/closed state
- Options highlight on hover
- Selected option shows checkmark

Keyboard:

- Tab/Shift+Tab: Focus management
- Arrow Up/Down: Change selection
- Enter: Confirm selection
- Escape: Close dropdown

---

### Checkbox

Purpose: Multiple selections from a list
Usage: Filter options, agreements

Visual:

- Size: 16x16px
- Border: 1px solid #CCCCCC (unchecked)
- Checked: Blue background (#0066CC), white checkmark
- Margin-right: 8px
- Label: 14px, left margin 8px

States:

- Unchecked: Gray border, white background
- Checked: Blue background, white checkmark
- Focus: Outline 2px #0066CC, offset 2px
- Disabled: Opacity 0.5, gray border

---

### Radio Button

Purpose: Single selection from a list
Usage: Team size, prediction options

Visual:

- Size: 16x16px circle
- Border: 2px #CCCCCC (unchecked)
- Selected: Border 2px #0066CC, dot 6px #0066CC center
- Margin-right: 8px
- Label: 14px, left margin 8px

States:

- Unchecked: Gray border, white background
- Selected: Blue border + dot
- Focus: Outline 2px #0066CC, offset 2px
- Disabled: Opacity 0.5

---

### File Upload

Purpose: Upload Excel files (cyclists)
Usage: Admin import functionality

Visual:

- Area: 120px height, 1px dashed border #CCCCCC, background #F9F9F9
- Text: "Drag Excel file here or click to browse"
- Accepted: .xlsx, .xls
- Max size: 5MB
- Success: Green checkmark (#00AA00), filename shown
- Error: Red icon (#CC0000), error message in red below

Behavior:

- Drag & drop functionality
- Click to open file browser
- File validation on selection
- Progress indicator during upload
- Success/error message display

---

## Buttons

### Primary Button (CTA)

Background: #0066CC
Text: White, 14px, Bold
Padding: 10px 24px
Border: None
Radius: 4px
Height: 40px
Hover: #0052A3 (darker blue)
Active: #003D7A (even darker)
Focus: Border 2px #0066CC, outline 3px rgba(0,102,204,0.15)
Disabled: Background #CCCCCC, text #999999, cursor not-allowed
Transition: All 0.2s ease

---

### Secondary Button

Background: Transparent
Border: 2px #0066CC
Text: #0066CC, 14px, Bold
Padding: 8px 20px
Radius: 4px
Height: 40px
Hover: Background #F0F5FF
Active: Background #E0E9FF
Focus: Same as primary
Disabled: Border #CCCCCC, text #999999

---

### Danger Button

Background: #CC0000
Text: White, 14px, Bold
Padding: 10px 24px
Radius: 4px
Height: 40px
Hover: #990000
Requires: Confirmation dialog before action

---

## Tables

### Base Table Styling

Row height: 44px
Header row: Dark background #F5F5F5, bold text, sort arrows
Alternate rows: White and #FAFAFA
Borders: 1px #DDDDDD (horizontal only, no vertical)
Hover row: Light blue background #F0F5FF
Selected row: Blue left border 3px
Font: 14px, Regular
Padding: 12px per cell

---

### Cyclist/Team List Table

Columns:

1. Rank (sortable, numeric)
2. Name (searchable, sortable)
3. Game Number (sortable, numeric)
4. Country (filterable)
5. Points Value (sortable, numeric)
6. Status (filterable)
7. Actions

Sorting:

- Click header to sort
- Arrow icons (↑ = ascending, ↓ = descending)
- Visual feedback on hover

---

### Standings/Leaderboard Table

Columns:

1. Rank (#1, #2, etc.)
2. Player Name (clickable → detail)
3. Team Name (clickable → detail)
4. Total Points (bold, larger 16px)
5. Latest Stage Points (with trend ↑↓→)
6. Status

Top 3 Special:

- 🥇 1st: Gold background (#FFD700 with 20% opacity)
- 🥈 2nd: Silver background (#C0C0C0 with 20% opacity)
- 🥉 3rd: Bronze background (#CD7F32 with 20% opacity)

Current User:

- Blue left border (3px)
- Slightly darker background
- "You" label

---

### Stage Results Table

Columns:

1. Position (1-20)
2. Tour Number (cyclist ID)
3. Cyclist Name
4. Country
5. Time / Status (finished, DNF, DNS)
6. Jerseys (if awarded)

Jersey Indicators:

- Yellow: [Y] tag, gold background
- White: [W] tag, light background
- Green: [G] tag, green background
- Polka Dot: [K] tag, orange background

Status:

- DNF (Did Not Finish): Red text, strikethrough
- DNS (Did Not Start): Red text, grayed out

---

## Cards & Panels

### Information Card

Padding: 16px
Border: 1px solid #DDDDDD
Border-radius: 4px
Background: #FFFFFF
Shadow: 0 1px 3px rgba(0,0,0,0.1)
Title: H4, margin-bottom 12px
Content: Body text, margins between elements
Actions: Right-aligned, 8px spacing between buttons

---

### Alert/Status Card

Success: Green left border (4px), light green bg #F0FFF0
Error: Red left border (4px), light red bg #FFF0F0
Warning: Yellow left border (4px), light yellow bg #FFFFF0
Info: Blue left border (4px), light blue bg #F0F8FF
Padding: 12px text + 12px border
Icon: Left-aligned, 20x20px
Message: Bold title (14px) + regular body text
Close button: Top-right, X icon, #999999
Transition: Slide out on close (0.3s ease)

---

## Modals & Dialogs

### Modal Structure

Overlay: 50% opacity dark (#000000), full screen, z-index 1000

Modal Box:

- Max-width: 600px (small), 800px (medium), 1000px (large)
- Border-radius: 8px
- Box-shadow: 0 5px 15px rgba(0,0,0,0.3)
- Padding: 0 (no padding on modal itself)

Header:

- Title: H3, bold
- Close button: X icon, gray (#666), top-right, 16px from edge
- Border-bottom: 1px #DDDDDD
- Padding: 16px 24px

Body:

- Padding: 24px
- Content (form, text, list)
- Max-height: 70vh, overflow-y auto

Footer:

- Border-top: 1px #DDDDDD
- Padding: 16px 24px
- Buttons: Right-aligned, primary button last
- Spacing between buttons: 8px

---

## Popovers & Tooltips

### Tooltip

Purpose: Provide additional context
Trigger: Hover or focus
Display Duration: Show on hover, hide after 2 seconds
Content: 12px, regular, #333333
Background: Dark (#333333)
Text: White (#FFFFFF)
Padding: 8px 12px
Border-radius: 4px
Max-width: 200px
Arrow: 8px, pointing to element
Shadow: Level 3 (0 5px 15px rgba(0,0,0,0.25))
Animation: Fade in 0.2s ease-out

---

### Popover

Purpose: Show additional information without leaving page
Trigger: Click or hover
Content: Up to 300px width
Padding: 16px
Border: 1px solid #DDDDDD
Background: #FFFFFF
Shadow: Level 3
Border-radius: 4px
Arrow: Points to trigger element
Close: Click outside or close button

---

## Progress & Status Indicators

### Progress Bar

Purpose: Show completion percentage
Width: 100% of container
Height: 8px
Background: Light gray (#EEEEEE)
Fill: Blue (#0066CC)
Border-radius: 4px
Animation: Smooth transition 0.3s ease

---

### Budget Tracker

Purpose: Show point budget usage
Display: Horizontal bar with overflow visual

- Standard Pool: Solid bar
- Reserve Pool: Stacked bar below
- Overflow indicator: Different color/pattern
- Percentage labels: Right-aligned
- Legend: Colors explained

---

## Navigation

### Breadcrumb Trail

Purpose: Show current location in hierarchy
Example: Home > Dashboard > My Team

Format: Text links separated by ">"
Styling: 12px, gray (#666666)
Current page: Not a link, bold
Links: Blue (#0066CC), underline on hover
Separator: Gray (>)
Spacing: 4px around separators

---

### Tabs

Purpose: Switch between related content
Styling:

- Tab height: 44px
- Text: 14px, bold
- Inactive: Gray text (#666666), bottom border 2px transparent
- Active: Blue text (#0066CC), bottom border 2px #0066CC
- Hover (inactive): Light gray background (#F5F5F5)

Behavior:

- Click to activate tab
- Content changes below
- Only one tab active at a time
- Keyboard: Left/Right arrows navigate

---
