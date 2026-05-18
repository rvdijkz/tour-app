# Team Composition Page Specification

## Page Overview

Purpose: Build and manage cycling team for the tour
Path: `/my-team` or `/team/{id}`
Role: PLAYER only
Accessibility: WCAG 2.1 Level AA

---

## Layout

### Header

Breadcrumb:

- Dashboard > My Team
- 12px, gray

Title:

- "Team: [TeamName]"
- H1, 32px, bold

Right side:

- Status badge: [DRAFT] / [SUBMITTED] / [LOCKED] / [ACTIVE]
- Countdown timer (if not locked): "2 days remaining"
  - Red (#CC0000) if < 24 hours
  - Normal black if > 24 hours

---

## Point Budget Tracker (Full Width)

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 32px

Title: "POINT BUDGET TRACKER (Flexible Budget System)"

- H3, 18px, bold
- Margin-bottom: 24px
- Examples below assume default config: Standard Pool = 100 points, Reserve Pool = 20 points

### Standard Pool Section

Label: "STANDARD POOL (Positions 1-10 + Position 11)"

- 14px, SemiBold, #333333

Progress Bar:

- Full width
- Background: Light gray #EEEEEE
- Fill: Blue #0066CC
- Height: 12px
- Border-radius: 6px
- Example: [████████░░] (70% filled)

Stats Row Below:

- "70/100 pts" (bold, left-aligned)
- "30 points remaining" (gray #666666, right-aligned)
- Font: 14px

---

### Reserve Pool Section (Flexible)

Label: "RESERVE POOL (Positions 12-13)"

- 14px, SemiBold, #333333
- Margin-top: 24px

Sub-rows:

- "Base Budget: 20 pts" (gray #666666, 12px)
- "Overflow from Standard: +30 pts" (blue #0066CC, 12px, bold - shows when applicable)

Progress Bar:

- Same styling as Standard Pool
- Example: [██████░░░░░░] (65% filled)

Stats Row:

- "35/50 pts" (bold, left-aligned)
- "15 points remaining" (gray #666666, right-aligned)
- Font: 14px

---

### Kluns Section

Label: "KLUNS (Position 14)"

- 14px, SemiBold, #333333
- Margin-top: 24px

Text: "No point limit - Choose any cyclist!"

- 14px, normal
- Color: Green #00AA00 (to indicate flexibility)

---

### Help Text

Rounded box, light blue background #F0F8FF:

- Padding: 12px 16px
- Border-left: 3px #0066CC
- Icon: ℹ️ (info)

Text:
"Tip: Save money in Standard Pool to get more Reserve budget flexibility.
Unused points automatically add to Reserve budget!"

- 12px, regular, #333333

---

## Team Composition Table (Full Width)

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 24px

Title: "TEAM COMPOSITION"

- H3, 18px, bold
- Margin-bottom: 16px

Table Structure:

Columns:

- Pos (Position)
- Cyclist Name
- Game Number
- Country
- Points Value
- Action

Header:

- Background: #F5F5F5
- Text: Bold, 14px, #333333
- Border-bottom: 1px #DDDDDD

Data Rows (14 rows, one per position):

- Height: 48px
- Padding: 12px per cell
- Alternating backgrounds: White / #FAFAFA
- Border-bottom: 1px #DDDDDD

### Row Styling by Position Type

**Positions 1-10: Standard Cyclists**

- Row background: White / #FAFAFA (normal)
- Text: 14px, regular
- Position marker: Numeric (1-10)

**Position 11: Nationality Specialist**

- Row background: Light blue #F0F8FF (highlighted)
- Text: 14px, regular, bold for name
- Position label: "11 ☆" (with star icon)
- Subtitle: "(Nationality ☆)" in gray below name
- Name color: Blue #0066CC

**Positions 12-13: Reserves**

- Row background: Light blue #F0F8FF (highlighted)
- Position label:
  - "12 ⚪ Reserve 1" or
  - "13 ⚪ Reserve 2"
- Text: 14px, regular
- Activation rules (display help text near reserve rows):
  - Reserves activate from the next stage after a dropout in positions 1-11.
  - If multiple dropouts occur in one stage, replacements are assigned by ascending team position (lowest number first).
  - Reserve 1 is consumed before Reserve 2.

**Position 14: Kluns**

- Row background: Light orange #FFF9F0 (special)
- Position label: "14 ★ Kluns"
- Subtitle: "(Joker ★)" in gray below name
- Text: 14px, regular, bold
- Border: 1px dashed #FF6600 (special attention)

### Position Column (Pos)

Width: 60px
Text: 14px, bold, center-aligned
Examples:

- "1"
- "11 ☆"
- "12 ⚪"
- "14 ★"

### Cyclist Name Column

Width: Flexible (auto)
Text: 14px, regular
If empty: "[Select cyclist]" (gray placeholder #999999, italic)
If filled: Cyclist name (black #333333)
Clickable if filled: Links to cyclist detail modal (optional)

### Game Number Column

Width: 80px
Text: 14px, right-aligned
Examples:

- "45"
- "12"
- "67"
  If empty: "-"

### Country Column

Width: 100px
Text: 14px, center-aligned
Format: 3-letter country code in brackets
Examples:

- "[ITY]" (blue for position 11)
- "[USA]"
- "[BEL]"
  If empty: "-"

### Points Value Column

Width: 80px
Text: 14px, right-aligned, bold (to emphasize budget)
Examples:

- "35"
- "32"
- "28"
  If empty: "-"

### Action Column

Width: 120px
Buttons:

If position empty:

- Button: "[Select]" (secondary style)
- Action: Opens Cyclist Selection Modal
- Height: 32px
- Font: 12px

If position filled:

- Button 1: "[Edit]" (secondary style)
  - Action: Opens Cyclist Selection Modal
  - Height: 32px
  - Font: 12px
  - Margin-right: 8px

- Button 2: "[Remove]" (danger outline style)
  - Action: Removes cyclist, updates budget
  - Requires confirmation dialog
  - Height: 32px
  - Font: 12px

---

## Validation Status Card

Position: Below team composition table

### Valid Team

Green card styling:

- Border-left: 4px #00AA00
- Background: Light green #F0FFF0
- Padding: 16px
- Icon: Green checkmark ✓

Messages (may include):

- "✓ Team Valid - All 14 positions filled"
- "✓ Budget OK - 30 points remaining in standard pool"
- "✓ Reserve budget - 15 points remaining (35/50 used)"
- "✓ Tour finisher prediction - 145 cyclists"

---

### Invalid Team

Red card styling:

- Border-left: 4px #CC0000
- Background: Light red #FFF0F0
- Padding: 16px
- Icon: Red X ✗

Messages (vary by issue):

- "✗ Team Incomplete - 2 positions empty"
- "✗ Standard Pool Exceeded - Reduce by 8 points"
- "✗ Reserve Pool Exceeded - Reduce by 5 points"
- "✗ Missing Nationality Specialist (Position 11 must be from [CountryName])"
- "✗ Duplicate Cyclist - [CyclistName] appears twice in team"

---

## Tour Finisher Prediction Section

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Margin-bottom: 24px

### Title & Context

Title: "TOUR FINISHER PREDICTION"

- H3, 18px, bold

Subtitle: "Predict how many cyclists from the entire tour will finish"

- 14px, gray #666666
- Margin-bottom: 16px

### Input Field

Label: "Expected Tour Finishers: "

- 14px, SemiBold, #333333

Number input with spinner:

- Width: 100px
- Height: 40px
- Value range: 0 to tour size (e.g., 0-200)
- Spinner controls: ↑ ↓ buttons on right
- Border: 1px solid #CCCCCC
- Focus: Blue border (2px), blue shadow
- Example value: "145"

Context info after field:

- "(e.g., 145 out of 176 starters)"
- 12px, gray #666666

### Help Text Box

Box, light blue background #F0F8FF:

- Padding: 12px 16px
- Border-left: 3px #0066CC
- Margin-top: 16px

Title: "ℹ️ Why make this prediction?"

- Bold, 14px

Body:
"This prediction is used as a TIEBREAKER if multiple players end up
with the same total points.

How it works:

1. You predict the total number of cyclists who will finish the tour
2. At the end, we compare all predictions to the actual number
3. If you're tied on points with other players, the person with the
   CLOSEST prediction wins the tiebreaker and gets +1 bonus point
4. If multiple players tie for best prediction, they ALL get +1 bonus point

Examples:

Example 1 (You win):
├── You predicted: 144 finishers
├── Actual result: 145 finishers
├── Difference: 1 ← BEST among tied players
└── Result: +1 tiebreaker point (you win!)

Example 2 (Multiple winners):
├── You predicted: 144 finishers
├── Another player predicted: 144 finishers
├── Actual result: 145 finishers
├── Both differences: 1 ← Both BEST
└── Result: Both get +1 tiebreaker point (co-winners)"

- Font: 12px, regular, #333333
- Multiple paragraphs with line breaks
- Use example boxes

### Warning Box

Box, yellow background #FFFFF0:

- Padding: 12px 16px
- Border-left: 3px #FFAA00
- Margin-top: 12px
- Icon: ⚠️ (warning)

Text:
"⚠️ This prediction is locked when you submit your team
and cannot be changed during the tour."

- Font: 12px, regular, #333333
- Bold for "locked" and "cannot be changed"

### Tour Context (Optional Info Box)

If available, show:

- Box, light gray background #F9F9F9
- Padding: 12px 16px
- Border-left: 3px #CCCCCC
- Margin-top: 16px

Content:
"Tour Information:
├── Tour: Tour de France 2024
├── Total Starters: 176 cyclists
├── Historical Finish Rate: 80-85%
├── Typical Finishers: 140-150"

## Font: 12px, gray #666666

## Cyclist Selection Modal

### Modal Structure

Trigger: Click [Select] or [Edit] button in team table

Title: "Select Cyclist for Position [X]"

- H3, 18px, bold

Close Button: X icon (gray #666), top-right

---

### Search & Filter Section

Padding: 16px
Background: Light gray #F5F5F5
Border-bottom: 1px #DDDDDD

Row 1: Search Input

- Placeholder: "Search by name..."
- Width: 100% (or 70% if side-by-side)
- Height: 40px

Row 1 (continued): Country Filter Dropdown

- Label: "[Country ▼]"
- Width: 200px
- Height: 40px
- Default: "All Countries"

Row 2 (new row): Sort & Checkbox

Left: Sort Dropdown

- Label: "Sort: [Game # ▼]"
- Options:
  - Game Number (default)
  - Points Value
  - Country
  - Name

Right: Checkbox

- Label: "Only available"
- Checked by default (shows only unselected cyclists)
- Unchecked: Shows all cyclists including already-selected

---

### Cyclist List Table

Column Headers:

- Num (Game Number)
- Name
- Ctry (Country)
- Points
- Status

Header styling:

- Background: #F5F5F5
- Bold text, 14px

Data Rows:

- Height: 44px
- Padding: 12px per cell
- Alternating: White / #FAFAFA
- Hover: Light blue #F0F5FF
- Font: 14px, regular

### Columns

**Num (Game Number)**

- 60px width
- Right-aligned
- Font: Bold
- Example: "45"

**Name**

- Flexible width
- Left-aligned
- Font: Regular
- Example: "Giro Giromax"

**Ctry (Country)**

- 80px width
- Center-aligned
- Format: 3-letter code
- Example: "[ITY]"

**Points**

- 80px width
- Right-aligned
- Bold (budget awareness)
- Example: "35"

**Status**

- 100px width
- Center-aligned
- Values:
  - ✓ (green) - Available
  - ⚠️ (yellow) - In your team
  - ✗ (red) - In another team / Dropped out

---

### Pagination

Position: Below table

Format: "Showing 1-7 of 145 cyclists"

- Text: 12px, gray #666666
- Center-aligned

Controls:

- [< Prev] button (secondary style)
- Page selector: "Page 1 of 20"
- [Next >] button (secondary style)

---

### Modal Buttons

Position: Footer of modal

- [SELECT] button (primary, blue, 40px height)
- [CANCEL] button (secondary, border style, 40px height)
- Spacing: 8px between buttons
- Right-aligned

---

## Bottom Action Buttons

Position: Below all sections
Full width layout

Primary Button:

- [SAVE DRAFT]
- Background: #0066CC
- Width: Auto (left-aligned)
- Height: 40px
- Only if Status = DRAFT/SUBMITTED
- Grayed out if team invalid
- Action: Saves team but doesn't lock it

Secondary Button:

- [SUBMIT TEAM]
- Background: Transparent
- Border: 2px #0066CC
- Width: Auto
- Height: 40px
- Only if Status = DRAFT/SUBMITTED
- Requires valid team + expected finishers set
- Grayed out if invalid
- Action: Submits team, locks prediction, changes status to SUBMITTED

Tertiary Button:

- [CANCEL]
- Background: Light gray #F5F5F5
- Width: Auto
- Height: 40px
- Action: Goes back without saving

Spacing: 8px between buttons

---

## Read-Only View (After Locked)

Tour Finisher Prediction:

- When team is LOCKED, the predicted number is shown as read-only
- Display: "Expected Tour Finishers: 145 (locked)"
- Field disabled, gray background
  When Status = LOCKED or ACTIVE:

- All [Select], [Edit], [Remove] buttons hidden
- Point budget tracker read-only (grayed out)
- Expected finishers field read-only
- All input fields disabled
- Show instead: "Team is locked. View scoring details." link
- Only buttons: [Back] or [Close]

---

## Responsive Behavior

### Tablet (768px - 1024px)

- Table: May scroll horizontally if needed
- Modal: Reduced padding (16px)
- Buttons: Full width at bottom
- Font sizes: Same as desktop
- Spacing: 16px margins (reduced from 24px)

### Mobile (< 768px)

NOT SUPPORTED in MVP
Future phase

---

## Loading States

Modal Opening:

- Cyclist list loading
- Show skeleton rows
- Search disabled until loaded
- Pagination disabled

Form Submission:

- Disable buttons during submission
- Show spinner on [SUBMIT TEAM] button
- Text changes to "SUBMITTING..."

---

## Error Handling

### Cyclist Selection Error

Modal:

- Alert card in cyclist list section
- Red border-left (4px), light red background
- Message: "Unable to load cyclists. Please refresh and try again."
- Close button: X icon
- Refresh link in message

### Save/Submit Error

Page-level alert:

- Full-width red card at top
- Message: "Unable to save team. Please try again."
- Auto-dismiss after 5 seconds or manual close
- Retry button

### Validation Error

Inline in validation status card:

- Shows specific validation failures
- Red styling
- Clear action items to fix

---

## Accessibility Features

### Tables

- Semantic <table>, <thead>, <tbody> markup
- <th> headers with proper associations
- Screen reader can navigate rows/columns

### Forms

- All labels associated with inputs
- Required fields marked with aria-required
- Error messages associated with fields via aria-describedby
- Focus order: Logical top-to-bottom

### Modals

- aria-modal="true"
- Focus trap (Tab stays within modal)
- Escape key closes modal (if allowed)
- aria-label on close button

### Color Contrast

- All text ≥ 4.5:1 contrast ratio
- Status indicators use icons + color

### Focus Indicators

- 2px outline #0066CC on all interactive elements
- Visible on buttons, inputs, links

---

## Performance

- Page load: < 2 seconds
- Cyclist search: < 500ms (debounced)
- Team save: < 1 second
- Form validation: Instant (client-side)

---

## Analytics Events

Track:

- Team composition page load
- Add/edit/remove cyclist events
- Submission attempts and success
- Time spent editing team
- Budget exceeded attempts
- Modal open/close events

---
