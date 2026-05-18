# Player Dashboard Specification

## Page Overview

Purpose: Show player overview, standings, and quick actions
Path: `/dashboard` or `/home`
Role: PLAYER only
Accessibility: WCAG 2.1 Level AA

---

## Layout

### Header Section

Navigation:

- Logo/Home link (top-left)
- Main menu: Home, Standings, My Team
- User menu (top-right): Profile, Settings, Logout

Breadcrumb:

- Home > Dashboard
- 12px, gray (#666666)

Title:

- "Welcome, [PlayerName]!"
- H1, 32px, bold
- Personalized greeting

---

## Main Content Grid

Layout: 12-column grid, responsive stacking

### Status Panel (Full Width)

Card:

- Padding: 24px
- Border: 1px solid #DDDDDD
- Background: #FFFFFF
- Shadow: 0 1px 3px rgba(0,0,0,0.1)

Title: "STATUS"

- H3, 18px, bold
- Margin-bottom: 16px

Content (4 rows, 2-column layout on tablet):

**Row 1: Rank & Points**

- Current Rank: "#5 of 28 players"
  - Font: 14px, bold
  - Value in large: "450 points" (20px, blue #0066CC)
- Trend: "↑ +10 points since last stage" (green #00AA00)

**Row 2: Team Status**

- Team Name: "[TeamName]" (bold)
- Status: [DRAFT] / [SUBMITTED] / [LOCKED] / [ACTIVE]
  - Status badges with color coding
  - DRAFT: Gray #999999
  - SUBMITTED: Blue #0066CC
  - LOCKED: Orange #FFAA00
  - ACTIVE: Green #00AA00

**Row 3: Deadline Info**

- Days Until Submission Deadline: "2 days remaining"
  - Bold if < 1 day
  - Red (#CC0000) if expired
  - Green (#00AA00) if team already submitted
- Submission Deadline: "April 20, 2024 at 23:59 UTC"
  - 12px, gray

**Row 4: Latest Stage**

- Latest Stage Completed: "Stage 18"
- Points Gained: "+18 points"
  - Green (#00AA00) if positive
  - Red (#CC0000) if negative
- Time: "Calculated 2 hours ago"

---

## Quick Actions Panel (Left Column, 25% width on desktop)

Card:

- Padding: 24px
- Border: 1px solid #DDDDDD
- Background: #FFFFFF
- Title: "QUICK ACTIONS" (H3, 18px, bold)

Buttons (stacked vertically, full width):

1. "View My Team"
   - Primary style (blue background)
   - Height: 40px
   - Margin-bottom: 12px
   - Links to: `/my-team`

2. "Edit Team"
   - Primary style
   - Height: 40px
   - Margin-bottom: 12px
   - Enabled only if Status = DRAFT/SUBMITTED
   - Disabled & grayed out if locked
   - Links to: `/my-team/edit`

3. "View Rules"
   - Secondary style (border)
   - Height: 40px
   - Margin-bottom: 12px
   - Opens modal with game rules
   - Links to: `/rules`

4. "View Results"
   - Secondary style
   - Height: 40px
   - Links to: `/standings`

---

## Latest Standings Preview (Right Column, 50% width on desktop)

Card:

- Padding: 24px
- Border: 1px solid #DDDDDD
- Background: #FFFFFF
- Title: "LATEST STANDINGS" (H3, 18px, bold)

Table (simplified leaderboard):

Columns:

- Rank
- Player Name
- Total Points
- Trend (↑↓→)

Rows: Top 10 players

Header Row:

- Background: #F5F5F5
- Text: Bold, 14px
- Borders: 1px #DDDDDD (bottom only)

Data Rows:

- Height: 44px
- Padding: 12px per cell
- Alternating backgrounds: White / #FAFAFA
- Hover: Light blue #F0F5FF

Special Styling:

Top 3 Rank:

- 🥇 Rank 1: Gold background #FFD700 (20% opacity)
- 🥈 Rank 2: Silver background #C0C0C0 (20% opacity)
- 🥉 Rank 3: Bronze background #CD7F32 (20% opacity)

Current Player Row:

- Blue left border (3px)
- Slightly darker background
- "You →" label on right side

Link:

- "See Full Standings" link at bottom
- Right-aligned, blue (#0066CC)
- Underline on hover
- Links to: `/standings`

---

## Stage Results Section (Full Width)

Card:

- Padding: 24px
- Border: 1px solid #DDDDDD
- Background: #FFFFFF
- Title: "STAGE RESULTS (Latest 3)" (H3, 18px, bold)
- Margin-top: 32px

Table:

Columns:

- Stage Number
- Your Points (bold)
- Best Score (gray)
- Average Score (gray)
- Top Finisher (player name)

Header Row:

- Background: #F5F5F5
- Text: Bold, 14px, #333333
- Borders: 1px #DDDDDD

Data Rows (3 rows, latest first):

- Height: 48px
- Padding: 12px per cell
- Alternating backgrounds: White / #FAFAFA
- Borders: 1px #DDDDDD (bottom only)

Stage Number:

- Bold, 14px
- Example: "Stage 18"

Your Points:

- Large text, 16px, bold
- Green if positive: #00AA00
- Red if negative: #CC0000
- Plus sign for positive: "+8 pts"

Best Score:

- 14px, gray #666666
- Example: "+25 pts"

Average Score:

- 14px, gray #666666
- Example: "+14 pts"

Top Finisher:

- 14px, bold blue #0066CC
- Clickable → goes to that player's detail
- Example: "Jane Smith"

---

## Visual Layout Example

┌───────────────────────────────────────────────────────────────────┐
│ Home > Dashboard │
│ │
│ Welcome, John Smith! │
│ Your Team: Team Winners │
│ │
│ ┌──────────────────────────────────────────────────────────────┐ │
│ │ STATUS │ │
│ ├──────────────────────────────────────────────────────────────┤ │
│ │ Current Rank: #5 (450 points) │ │
│ │ Latest Stage Points: +18 │ │
│ │ Team Status: [ACTIVE] │ │
│ │ Days Until Submission Deadline: 2 days │ │
│ └──────────────────────────────────────────────────────────────┘ │
│ │
│ ┌──────────────────────┐ ┌────────────────────────────────────┐ │
│ │ QUICK ACTIONS │ │ LATEST STANDINGS │ │
│ ├──────────────────────┤ ├────────────────────────────────────┤ │
│ │ [View My Team] │ │ 🥇 1. Jane Smith 500 pts │ │
│ │ [Edit Team] │ │ 🥈 2. John Doe 490 pts │ │
│ │ [View Rules] │ │ 🥉 3. Alice Brown 480 pts │ │
│ │ [View Results] │ │ 4. Bob Johnson 460 pts │ │
│ └──────────────────────┘ │ ► 5. You 450 pts │ │
│ │ [See Full Standings] │ │
│ └────────────────────────────────────┘ │
│ │
│ ┌──────────────────────────────────────────────────────────────┐ │
│ │ STAGE RESULTS (Latest 3) │ │
│ ├────┬──────────┬──────┬──────────┬─────────────────────────────┤ │
│ │ St │ Your Pts │ Best │ Avg │ Top Finisher │ │
│ ├────┼──────────┼──────┼──────────┼─────────────────────────────┤ │
│ │ 18 │ +8 pts │ +25 │ +14 pts │ Jane Smith │ │
│ │ 17 │ +12 pts │ +22 │ +12 pts │ John Doe │ │
│ │ 16 │ +6 pts │ +30 │ +15 pts │ Alice Brown │ │
│ └────┴──────────┴──────┴──────────┴─────────────────────────────┘ │
│ │
└───────────────────────────────────────────────────────────────────┘

## Responsive Behavior

### Desktop (1920px+)

Layout:

- 3-column: Quick Actions (25%) | Main Content (50%) | Standings (25%)
- Full width sections below

### Laptop (1440px)

Layout:

- Quick Actions & Standings stack (50% each)
- Main content above at full width

### Tablet (768px - 1024px)

Layout:

- Single column, full width
- Quick Actions on top
- Status panel below
- Standings preview below
- Stage results at bottom

All cards: 90% width with side margins
Padding: 16px (reduced from 24px)

---

## Loading States

Initial Load:

- Show skeleton loaders for each section
- Gray placeholder boxes
- Pulse animation 1s ease-in-out

Data Loading:

- Disable "Edit Team" button if data not loaded
- Show spinner in Quick Actions section

---

## Empty States

If No Teams Yet:

- Status Panel: Shows "No team created yet"
- Quick Actions: "View My Team" disabled
- Standings: "No team data available"
- Stage Results: "No results yet"

If Tour Not Started:

- Stage Results: "Waiting for first stage..."
- Latest Stage: "Tour starts on [date]"

---

## Error Handling

### General Error

Alert card (full width, top of page):

- Red border-left (4px), light red background #FFF0F0
- Icon: Red X circle
- Message: "Unable to load dashboard. Please refresh the page."
- Close button: X icon
- Action: Refresh button

### Partial Data Error

Show section with error message:

- Alert styling within section
- Message: "Unable to load this section. Refresh to retry."
- Refresh button within section

---

## Accessibility Features

### Color Contrast

- All text: ≥ 4.5:1 contrast
- Links distinguishable

### Headings

- Proper hierarchy: H1 (page title) → H3 (sections)
- Screen readers can navigate by headings

### Tables

- <thead>, <tbody>, <th> semantic markup
- Column headers read aloud
- Data rows properly associated

### Links

- Blue color (#0066CC) + underline
- "aria-label" for icon links
- Keyboard accessible (Tab navigation)

### Focus Management

- Focus indicator: 2px outline #0066CC
- Tab order: Logical top-to-bottom
- Focus visible on all interactive elements

---

## Performance

- Page load: < 2 seconds
- Data refresh: < 1 second
- Animations: 0.2s - 0.3s (non-blocking)

---

## States & Variations

### Before Tour Starts

- Status Panel: "Waiting for tour to begin"
- Stage Results: Empty / "No stages yet"
- Latest Standing: May show predicted or invitation-only

### During Tour

- Status Panel: Current rank & points live
- Stage Results: Updates after each stage
- Latest Standing: Real-time updates

### After Tour Ends

- Status Panel: Final rank & total points
- Quick Actions: "View Final Results" instead of "Edit Team"
- Special badge: "Tour Complete" on team name

---

## Analytics Events

Track these interactions:

- "View My Team" click
- "Edit Team" click
- "View Rules" click
- "See Full Standings" click
- Page load time
- Data refresh time

---
