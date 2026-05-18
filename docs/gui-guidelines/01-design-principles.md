# Design Principles

## Philosophy

The Tour Game UI prioritizes:

- Clarity: Information is organized logically
- Validation: Errors caught immediately with helpful feedback
- Accessibility: WCAG 2.1 Level AA compliant
- Responsiveness: Works on tablets and desktops
- Consistency: Unified visual language throughout

---

## Layout System

### Grid

12-column grid layout
Gutter width: 16px
Container max-width: 1920px

### Spacing Scale

4px - minimal spacing
8px - base unit (most common)
16px - medium spacing
24px - large spacing
32px - section spacing
48px - major section breaks

All spacing should be multiples of 8px.

---

## Color Palette

### Primary Colors

Primary Blue: #0066CC (buttons, links, highlights)
Secondary Gray: #666666 (secondary text, borders)
Success Green: #00AA00 (confirmations, valid input)
Warning Yellow: #FFAA00 (cautions, double-check prompts)
Danger Red: #CC0000 (errors, dropouts, deletions)
Neutral White: #FFFFFF (backgrounds)
Neutral Dark: #333333 (primary text)

### Jersey Colors (Semantic)

Yellow Jersey: #FFD700 (bright yellow)
White Jersey: #FFFFFF (with border #CCCCCC)
Green Jersey: #00AA00 (cycling green)
Polka Dot: #FF6600 (cycling polka dot orange/red)

### Semantic Colors

Active/Loaded: Green (#00AA00)
Dropped Out: Red (#CC0000) with strikethrough
Reserved: Light Blue (#CCDDFF)
Not Yet Active: Light Gray (#EEEEEE)
Disabled: Light Gray (#CCCCCC)

### Color Accessibility

- Text/background contrast must be ≥ 4.5:1 for normal text
- Links must be distinguishable without relying on color alone
- Color blind friendly: use patterns or icons in addition to color

---

## Typography

### Font Stack

Primary: Segoe UI, Roboto, Helvetica Neue, Arial (sans-serif)
Monospace: Courier New, Monaco (for numbers, codes)

### Type Scale

H1 (Page Title): 32px, Bold, #333333, line-height 1.2
H2 (Section Title): 24px, Bold, #333333, line-height 1.3
H3 (Subsection): 18px, Bold, #333333, line-height 1.4
H4 (Label/Emphasis): 16px, SemiBold, #333333, line-height 1.4
Body (Regular text): 14px, Regular, #333333, line-height 1.5
Small (Helper/caption): 12px, Regular, #666666, line-height 1.4

### Text Styling

Link text: 14px, Regular, #0066CC, underline on hover
Form labels: 14px, SemiBold, #333333
Form helper text: 12px, Regular, #666666, italic
Error text: 12px, Regular, #CC0000, bold

### Spacing

Paragraph margins: 16px bottom
List item spacing: 8px
Line height: 1.5 (for readability)
Letter-spacing: normal (no adjustments)

---

## Components

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

### Danger Button

Background: #CC0000
Text: White, 14px, Bold
Padding: 10px 24px
Radius: 4px
Height: 40px
Hover: #990000
Requires: Confirmation dialog before action

---

## Form Elements

### Text Input

Height: 40px
Padding: 8px 12px
Border: 1px solid #CCCCCC
Border-radius: 4px
Font: 14px, Regular, #333333
Background: #FFFFFF
Placeholder: #999999, italic
Focus: Border #0066CC (2px), shadow 0 0 0 3px rgba(0,102,204,0.15)
Error state: Border #CC0000, error message in #CC0000 below
Disabled: Background #F5F5F5, color #999999, cursor not-allowed
Transition: Border-color 0.2s ease, box-shadow 0.2s ease

### Select Dropdown

Height: 40px
Padding: 8px 12px (left), 36px (right for arrow)
Border: 1px solid #CCCCCC
Options row: 32px height, 12px padding
Selected: Blue background (#0066CC), white text
Focus: Same as text input
Disabled: Same as text input

### Checkbox

Size: 16x16px
Border: 1px solid #CCCCCC (unchecked)
Checked: Blue background (#0066CC), white checkmark
Margin-right: 8px
Label: 14px, left margin 8px
Focus: Outline 2px #0066CC, offset 2px
Disabled: Opacity 0.5

### Radio Button

Size: 16x16px circle
Border: 2px #CCCCCC (unchecked)
Selected: Border 2px #0066CC, dot 6px #0066CC center
Margin-right: 8px
Label: 14px, left margin 8px
Focus: Outline 2px #0066CC, offset 2px
Disabled: Opacity 0.5

### File Upload

Area: 120px height, 1px dashed border #CCCCCC, background #F9F9F9
Text: "Drag Excel file here or click to browse"
Accepted: .xlsx, .xls
Max size: 5MB
Success: Green checkmark (#00AA00), filename shown
Error: Red icon (#CC0000), error message in red below
Hover: Background #F0F5FF, border #0066CC

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

### Cyclist/Team List Table

Columns:

- Rank (sortable, numeric)
- Name (searchable, sortable)
- Game Number (sortable, numeric)
- Country (filterable)
- Points Value (sortable, numeric)
- Status (filterable)
- Actions

Sorting:

- Click header to sort
- Arrow icons (↑ = ascending, ↓ = descending)
- Visual feedback on hover

### Standings/Leaderboard Table

Columns:

- Rank (#1, #2, etc.)
- Player Name (clickable → detail)
- Team Name (clickable → detail)
- Total Points (bold, larger 16px)
- Latest Stage Points (with trend ↑↓→)
- Status

Top 3 Special:

- 🥇 1st: Gold background (#FFD700 with 20% opacity)
- 🥈 2nd: Silver background (#C0C0C0 with 20% opacity)
- 🥉 3rd: Bronze background (#CD7F32 with 20% opacity)

Current User:

- Blue left border (3px)
- Slightly darker background
- "You" label

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
Actions: Right-aligned, 8px spacing

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

## Responsive Design

### Breakpoints

Desktop (1920px+) - Full horizontal layout
Laptop (1440px) - Optimized multi-column
Tablet (768px) - Responsive stacking
Mobile (< 768px) - NOT SUPPORTED (future phase)

### Responsive Behavior

At ≤ 1024px:

- Sidebar collapses to hamburger menu
- Multi-column layouts stack vertically
- Table columns may hide secondary info
- Modals may reduce max-width

At ≤ 768px:

- Full single-column layout
- Buttons expand to full width
- Table may scroll horizontally
- Padding reduces to 12px

---

## Accessibility (WCAG 2.1 AA)

### Color

- All text must have ≥ 4.5:1 contrast ratio
- Never use color alone to convey information
- Color blindness friendly (use patterns + color)

### Keyboard Navigation

- All interactive elements must be keyboard accessible
- Focus order must be logical (top-to-bottom, left-to-right)
- Focus indicators must be clearly visible (2px outline)
- No keyboard traps

### Forms

- All form fields must have associated labels
- Error messages must be associated with fields
- Required fields must be clearly marked
- Form instructions should be near the fields

### Images & Icons

- All images must have descriptive alt text
- Icons must have aria-labels or tooltips
- Decorative elements must be hidden from screen readers (aria-hidden)

### Structure

- Proper heading hierarchy (H1 → H2 → H3, no skipping levels)
- Semantic HTML (buttons for buttons, links for navigation)
- Lists marked with <ul>, <ol>
- Tables with <thead>, <tbody>, <th> cells

---

## Animations & Transitions

### General Rules

Duration: 0.2s - 0.3s for most transitions
Easing: ease or ease-in-out (avoid linear)
Performance: Use transform and opacity only (no layout shifts)

### Common Animations

Hover effects: 0.2s ease (button color, border changes)
Modal appearance: 0.3s ease-out (fade-in overlay, scale modal)
Dropdown open: 0.2s ease (expand height)
Alert slide-out: 0.3s ease (slide right + fade)
Page transitions: 0.2s fade (if applicable)

---

## Shadows

### Elevation System

Level 1 (Subtle): 0 1px 3px rgba(0,0,0,0.1)
Level 2 (Cards): 0 2px 8px rgba(0,0,0,0.12)
Level 3 (Modals): 0 5px 15px rgba(0,0,0,0.25)
Level 4 (Floating): 0 8px 24px rgba(0,0,0,0.3)

---

## Density & Whitespace

### Comfortable Spacing

The application uses generous whitespace for clarity:

- Card padding: 16px+
- Section margins: 24px+
- Form field spacing: 16px
- Element margins: 8px-16px

Never compress spacing to fit content. If content is too long, simplify the content instead.

---
