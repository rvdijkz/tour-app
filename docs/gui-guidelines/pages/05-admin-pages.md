# Admin Pages Specification

Administrative functions for managing players, cyclists, stages, and scoring.

---

## Overview

Purpose: Administrative functions for managing players, cyclists, stages, and scoring
Role: ADMIN only
Accessibility: WCAG 2.1 Level AA

---

## Admin Dashboard

Path: `/admin` or `/admin/dashboard`

### Layout

Sidebar Navigation:

- Logo (top)
- Menu items:
  - Dashboard
  - Cyclist Management
    - Import Cyclists
    - View / Edit Cyclist List
  - Tour Management
    - Active Tour
    - Stages
    - Configuration
  - Players
    - Add New Player
    - Player List
    - Manage Permissions
  - Scoring
    - Enter Stage Results
    - Scoring History
  - Reports
    - Standings Export

Main Content Area:

- Breadcrumb trail
- Page title
- Content

---

## 1. Add New Player (Create Player Account)

Path: `/admin/players/add` or modal from `/admin/players`

### Form Structure

Card:

- Padding: 32px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Max-width: 600px

Title: "Add New Player"

- H1, 32px, bold

### Form Fields

**1. Personal Name**

Label: "Personal Name\*"

- 14px, SemiBold, #333333
- Red asterisk (required)

Input:

- Height: 40px
- Placeholder: "e.g., John Smith"
- Type: text
- Required: true

**2. Team Name**

Label: "Team Name\*"

- 14px, SemiBold, #333333
- Required indicator

Input:

- Height: 40px
- Placeholder: "e.g., Team Winners"
- Type: text
- Required: true

**3. Email Address**

Label: "Email Address\*"

- 14px, SemiBold

Input:

- Height: 40px
- Placeholder: "player@example.com"
- Type: email
- Validation: Valid email format
- Required: true

**4. Username**

Label: "Username\*"

- 14px, SemiBold

Input:

- Height: 40px
- Placeholder: "username"
- Type: text
- Validation: Alphanumeric, 3-20 characters
- Help text: "3-20 characters, alphanumeric only"
- Font: 12px, gray #666666
- Required: true

### Buttons

**[CREATE ACCOUNT]**

- Primary style, blue, full width
- Height: 40px
- On click: Validates form, creates player, generates initial password

**[CANCEL]**

- Secondary style, border
- Height: 40px
- Action: Returns to player list

Spacing: 8px between buttons

### Success State

After account creation:

Success card (full width):

- Green border-left (4px), light green background #F0FFF0
- Padding: 16px
- Icon: Green checkmark ✓

Message:
"Player account created successfully!

Login Details Sent:
├── Username: [username]
├── Temporary Password: [auto-generated-password]
└── Password must be changed on first login (within 12 hours)

[Copy to Clipboard] button"

Font: 14px, regular

---

## 2. Import Cyclists (Excel Upload)

Path: `/admin/cyclists/import`

### Form Structure

Card:

- Padding: 32px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Max-width: 800px

Title: "Import Cyclists from Excel"

- H1, 32px, bold

---

### File Upload Section

**Upload Area**

Drag & drop zone:

- Height: 120px
- Border: 1px dashed #CCCCCC
- Background: #F9F9F9
- Border-radius: 4px
- Padding: 24px
- Text-align: center

Icon:

- 🔼 (upload arrow)
- Size: 32px
- Color: #0066CC

Text:
"Drag Excel file here or click to browse"

- Font: 14px, bold, #333333

Support text:
"Supported formats: .xlsx, .xls | Maximum size: 5MB"

- Font: 12px, gray #666666
- Margin-top: 8px

States:

- Hover: Background #F0F5FF, border #0066CC
- Drag over: Border 2px #0066CC, background #E8F4FF

### File Selected State

After file selected:

Success display (inside upload area):

- Icon: Green checkmark ✓ (20px, green #00AA00)
- Filename: "[cyclists_list.xlsx]" (bold, 14px)
- File size: "2.3 MB" (gray, 12px)
- [Clear] link (blue, 12px, removes selection)

### Template Section

Info box:

- Background: #F0F8FF
- Border-left: 3px #0066CC
- Padding: 16px
- Margin-top: 24px

Title: "Excel Template"

- Bold, 14px, #333333

Content:
"Download the template to ensure correct format:

Required Columns:
├── Name (text)
├── Game Number (numeric, unique)
├── Points Value (numeric)
└── Country Code (3-letter, e.g., BEL, USA)

Optional Columns:
└── Tour Number (numeric, leave blank initially)

[Download Template] link (blue, underlined)"

Font: 12px, regular

### Column Mapping (Optional)

If file is selected, show:

Title: "Column Mapping" (14px, bold)

- Font: 14px, SemiBold, margin-top: 24px

Auto-detection message:
"Columns detected automatically. Adjust if needed:"

- Font: 12px, gray

Dropdowns (4 rows):

"Name Column:" [Select column ▼]
"Game Number Column:" [Select column ▼]
"Points Value Column:" [Select column ▼]
"Country Code Column:" [Select column ▼]

- Height: 40px
- Default: Auto-detected values
- Allow manual override

### Preview & Validation

Title: "Preview" (14px, bold)

- Margin-top: 24px

Table preview:

Sample rows from Excel:

| Name           | Game # | Points | Country |
| -------------- | ------ | ------ | ------- |
| Giro Giromax   | 45     | 35     | ITY     |
| Speedy McWheel | 12     | 32     | USA     |
| Alpine Expert  | 67     | 28     | SUI     |

Header: Bold, #F5F5F5 background
Rows: Alternating white/#FAFAFA
Font: 12px

Pagination:

- "Showing 1-3 of 145 rows"
- [< Prev] [Next >]

### Validation Results

After selecting file:

**If valid:**

Green card:

- Border-left: 4px #00AA00
- Background: #F0FFF0
- Message: "✓ 145 cyclists ready to import"
- Details:
  - "145 new cyclists to add"
  - "0 duplicates detected"
  - "0 validation errors"

**If errors:**

Red card:

- Border-left: 4px #CC0000
- Background: #FFF0F0
- Message: "✗ Validation errors found (3)"

Error list:

- "Row 12: Missing Country Code"
- "Row 28: Game Number already exists"
- "Row 45: Invalid Points Value (must be numeric)"

Font: 12px, regular

### Buttons

**[IMPORT CYCLISTS]**

- Primary style, blue
- Height: 40px
- Full width
- Enabled only if file valid & no errors
- Disabled & grayed if errors
- On click: Uploads and imports

**[CANCEL]**

- Secondary style, border
- Height: 40px
- Action: Clears upload, returns to admin panel

Spacing: 8px

### Import Progress

During import:

Progress bar:

- Full width
- Height: 8px
- Background: #EEEEEE
- Fill: #0066CC
- Border-radius: 4px
- Percentage: "Importing... 45%" (right side)

Message:
"Importing 145 cyclists... This may take a minute."

- Font: 14px, gray #666666

Cancel button:

- [CANCEL IMPORT]
- Secondary style
- Stops import process

### Import Success

After completion:

Success card (full width):

- Green border-left (4px), light green background
- Padding: 24px

Icon: Green checkmark ✓

Message:
"Successfully imported 145 cyclists!

Summary:
├── Added: 145 new cyclists
├── Updated: 0 existing cyclists
└── Errors: 0

[View Cyclist List] button (blue link)"

Font: 14px, regular

### Import Error

If errors during import:

Error card:

- Red border-left (4px), light red background
- Padding: 24px

Message:
"Import completed with errors.

Summary:
├── Successfully imported: 142 cyclists
├── Skipped (errors): 3 cyclists
└── Total attempted: 145

Failed rows:
├── Row 12: [Error message]
├── Row 28: [Error message]
└── Row 45: [Error message]

[Retry] button | [Download Error Report] button"

Font: 14px, regular

---

## 3. Enter Stage Results

Path: `/admin/scoring/stage/{stageNumber}`

### Form Structure

Card:

- Padding: 32px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Enter Stage Results - Stage [N]"

- H1, 32px, bold

Subtitle: "[Stage Name] - [Date]"

- 14px, gray #666666

---

### Section 1: Finish Positions (1-20)

Title: "Finish Positions"

- H3, 18px, bold

Instruction: "Enter the Tour Numbers for cyclists who finished in positions 1-20"

- 12px, gray #666666
- Margin-bottom: 16px

20 numbered input fields (two columns):

Position 1: [________] (placeholder: "Tour Number")
Position 2: [________]
...
Position 20: [________]

Input specs:

- Height: 40px
- Width: Flexible, but side-by-side in 2 columns
- Type: number
- Required: true for all
- Validation: Valid tour number, not duplicate

---

### Section 2: Jersey Winners

Title: "Jersey Winners"

- H3, 18px, bold

Instruction: "Select or search for cyclists who won jerseys today"

- 12px, gray #666666

Four dropdowns (or search fields):

**Yellow Jersey**
Label: "Yellow Jersey (Leader):"
[🔍 Search or select cyclist...]
Placeholder: "Search by name or tour number"

**White Jersey**
Label: "White Jersey (Young Rider):"
[🔍 Search or select cyclist...]
Placeholder: "Search by name or tour number"

**Green Jersey**
Label: "Green Jersey (Points):"
[🔍 Search or select cyclist...]
Placeholder: "Search by name or tour number"

**Polka Dot Jersey**
Label: "Polka Dot Jersey (Mountains):"
[🔍 Search or select cyclist...]
Placeholder: "Search by name or tour number"

Behavior:

- Each dropdown searchable
- Shows cyclist name + tour number
- Can be left empty if not awarded
- Selected cyclist shows with badge

---

### Section 3: Combative Cyclist (Optional)

Title: "Combative Cyclist"

- H3, 18px, bold

Instruction: "Enter tour number of the most combative cyclist (if awarded)"

- 12px, gray #666666

Search field:
[🔍 Search cyclist...]
Placeholder: "Leave empty if not awarded"

Can be empty

---

### Section 4: Dropouts

Title: "Cyclists Who Dropped Out"

- H3, 18px, bold

Instruction: "Enter tour numbers of cyclists who did not finish this stage"

- 12px, gray #666666

Multi-select input:
[🔍 Search and add cyclists...]

List below (if added):
✓ Cyclist Name (Tour #45) [X]
✓ Cyclist Name (Tour #67) [X]
✓ Cyclist Name (Tour #89) [X]

Behavior:

- Search as you type
- Click to add
- [x] to remove
- Can be empty

---

### Section 5: Double Points Stage (Checkbox)

Title: "Special Stage"

- H3, 18px, bold

Checkbox with label:
☐ This is a double points stage

Help text:
"Check this box if today's stage awards double points"

- 12px, gray #666666

---

### Validation Status Card

Position: Below all sections

**Valid Form:**

Green card:

- Border-left: 4px #00AA00
- Background: #F0FFF0
- Message: "✓ All required fields completed"
- Details:
  - "20 finish positions entered"
  - "0 validation errors"
  - "Ready to calculate scores"

**Invalid Form:**

Red card:

- Border-left: 4px #CC0000
- Background: #FFF0F0
- Message: "✗ Validation errors found"
- Details:
  - "Missing: Position 5"
  - "Duplicate: Tour number 45 appears twice"
  - "Invalid: Position 12 is not a valid tour number"

---

### Action Buttons

**[CALCULATE SCORES]**

- Primary style, blue
- Height: 40px
- Full width
- Only enabled if form valid
- Disabled & grayed if invalid
- On click: Calculates scores for all teams

**[SAVE AS DRAFT]**

- Secondary style, border
- Height: 40px
- Saves data without calculating (for editing later)

**[CANCEL]**

- Secondary style, border
- Height: 40px
- Discards changes

Spacing: 8px between buttons

---

### Calculation Progress

During score calculation:

Progress card:

- Padding: 24px
- Background: Light blue #F0F8FF
- Border-left: 3px #0066CC

Message:
"Calculating scores for all teams..."

- Font: 14px, bold

Progress bar:

- Full width
- Height: 8px
- Showing: "Processing team 15 of 28..."

Cancel option:

- [CANCEL CALCULATION]
- Secondary style
- Stops process (can restart)

---

### Calculation Success

After completion:

Success card (full width):

- Green border-left (4px), light green background
- Padding: 24px

Icon: Green checkmark ✓

Message:
"Stage results calculated successfully!

Summary:
├── Stage: 18 ([Stage Name])
├── Teams processed: 28
├── Scoring completed: [Timestamp]
├── Finishers this stage: 145
└── Dropouts: 5

Actions:
[View Updated Standings] | [Enter Next Stage]"

Font: 14px, regular

---

### Calculation Error

If errors during calculation:

Error card:

- Red border-left (4px), light red background
- Padding: 24px

Message:
"Error calculating scores.

Details:

- Team ID: abc123 - Invalid cyclist in position 5
- Team ID: def456 - Dropout not found in database

[View Error Report] | [Retry]"

Font: 12px, regular

---

## 4. Scoring History

Path: `/admin/scoring/history`

### Overview Section

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Scoring History"

- H1, 32px, bold

Filters:

Tour: [Select Tour ▼]
Stage: [1 ▼] to [21 ▼]
Date: [From Date] to [To Date]

[FILTER] button (Primary)
[CLEAR FILTERS] link

---

### Results Table

Columns:

- Stage
- Date
- Status
- Teams Scored
- Finishers
- Last Updated
- Actions

Header:

- Background: #F5F5F5
- Bold text, 14px

Data rows:

- Height: 44px
- Alternating: White / #FAFAFA
- Hover: Light blue #F0F5FF

Example rows:

| Stage | Date         | Status   | Teams | Finishers | Last Updated | Actions                |
| ----- | ------------ | -------- | ----- | --------- | ------------ | ---------------------- |
| 21    | May 25, 2024 | Complete | 28    | 145       | 2 hours ago  | [VIEW] [EDIT] [DELETE] |
| 20    | May 24, 2024 | Complete | 28    | 146       | 1 day ago    | [VIEW] [EDIT] [DELETE] |
| 19    | May 23, 2024 | Draft    | 28    | 147       | 3 days ago   | [VIEW] [EDIT] [DELETE] |

Status badge:

- Complete: Green #00AA00
- Draft: Gray #999999
- Error: Red #CC0000

---

### Actions

**[VIEW]**

- Shows full stage details + results
- Links to detailed view page

**[EDIT]**

- Allows modification of stage data
- Links to edit form (same as "Enter Stage Results")

**[DELETE]**

- Removes stage results (with confirmation)
- Allows re-entry of corrected data

---

## 5. Configuration

Path: `/admin/configuration`

### Overview Section

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Game Configuration"

- H1, 32px, bold

Subtitle: "Configure game rules and parameters"

- 14px, gray #666666

---

### Section 1: Tour Settings

Title: "Tour Settings"

- H3, 18px, bold

**Current Tour**

Label: "Active Tour\*"
[Select Tour ▼]

Options:

- Tour de France 2024
- Giro d'Italia 2024
- Vuelta a España 2024

Default: Currently active tour

**Team Submission Deadline**

Label: "Team Submission Deadline\*"
[Date Picker] [Time Picker]

Format: "April 20, 2024 at 23:59 UTC"

Help text: "Teams cannot be modified after this time"

- 12px, gray

---

### Section 2: Budget Settings

Title: "Budget Limits"

- H3, 18px, bold

**Standard Pool Budget**

Label: "Standard Pool Maximum (Positions 1-11)\*"
[________] points

Default: 100
Help text: "Maximum points spendable on standard cyclists"

- 12px, gray

**Reserve Pool Budget**

Label: "Reserve Pool Maximum (Positions 12-13)\*"
[________] points

Default: 20
Help text: "Base maximum before overflow from standard pool"

- 12px, gray

---

### Section 3: Nationality Requirement

Title: "Nationality Specialist"

- H3, 18px, bold

**Nationality Requirement**

Label: "Position 11 Country Code\*"
[Select Country ▼]

Default: Belgium (BEL)
Help text: "Cyclist in position 11 must be from this country"

- 12px, gray

---

### Section 4: Special Rules

Title: "Special Rules"

- H3, 18px, bold

**Double Points Stages**

Label: "Double Points Stages"
[Multi-select or checkbox list]

Stages:
☐ Stage 1
☐ Stage 5
☐ Stage 10
☐ Stage 15
☐ Stage 20 (usually selected)
☐ Stage 21

Help text: "Select stages where points are doubled"

- 12px, gray

**Password Reset Window**

Label: "New Player Password Reset Window\*"
[________] hours

Default: 12
Help text: "Time for new players to change initial password"

- 12px, gray

---

### Validation Status

At bottom of form:

Green card (if valid):

- "✓ All settings valid"

Red card (if invalid):

- "✗ Fix errors: Standard budget must be > Reserve budget"

---

### Action Buttons

**[SAVE CONFIGURATION]**

- Primary style, blue
- Height: 40px
- Full width
- Only enabled if form valid

**[RESET TO DEFAULTS]**

- Secondary style, border
- Height: 40px
- Requires confirmation
- Resets all to defaults

**[CANCEL]**

- Secondary style, border
- Height: 40px

Spacing: 8px between buttons

---

### Save Success

Success card:

- Green border-left (4px)
- Message: "✓ Configuration saved successfully"
- Auto-dismiss after 5 seconds

---

## 6. Player Management

Path: `/admin/players`

### Overview Section

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Player Management"

- H1, 32px, bold

Quick actions:
[+ Add New Player] button (Primary)
[View Inactive Players] link

---

### Players Table

Columns:

- Player Name
- Team Name
- Email
- Status
- Joined Date
- Team Submitted
- Actions

Header:

- Background: #F5F5F5
- Bold, 14px

Data rows:

- Height: 44px
- Alternating: White / #FAFAFA

Example:

| Player Name | Team Name     | Email       | Status   | Joined | Team Submitted | Actions                     |
| ----------- | ------------- | ----------- | -------- | ------ | -------------- | --------------------------- |
| John Smith  | Team Winners  | john@ex.com | Active   | Apr 15 | ✓              | [VIEW] [RESET PWD] [DELETE] |
| Jane Doe    | Cycling Kings | jane@ex.com | Pending  | Apr 18 | ✗              | [VIEW] [RESET PWD] [DELETE] |
| Bob Johnson | Team Racers   | bob@ex.com  | Inactive | Apr 10 | ✓              | [VIEW] [RESET PWD] [DELETE] |

Status badge:

- Active: Green #00AA00
- Pending: Yellow #FFAA00 (password not reset yet)
- Inactive: Gray #999999

---

### Actions

**[VIEW]**

- Shows player detail page
- Team composition
- Scoring history
- Points breakdown

**[RESET PWD]**

- Generates new temporary password
- Shows confirmation dialog
- New password sent to player's email

**[DELETE]**

- Deletes player account (with confirmation)
- Removes team from standings
- Data archived for records

---

## 7. Standings Export/Reports

Path: `/admin/reports`

### Overview Section

Title: "Export Reports"

- H1, 32px, bold

---

### Section 1: Current Standings

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Current Standings"

- H3, 18px, bold

Buttons:

[EXPORT AS CSV]

- Downloads current leaderboard as CSV file

[EXPORT AS PDF]

- Downloads formatted standings as PDF

[EXPORT AS EXCEL]

- Downloads with additional charts and analysis

Format options after click:

- "Include stage details" checkbox
- "Include team composition" checkbox
- "Include finisher predictions" checkbox

---

### Section 2: Detailed Reports

Card:

- Padding: 24px
- Background: #FFFFFF
- Border: 1px solid #DDDDDD

Title: "Detailed Reports"

- H3, 18px, bold

**Player Scores by Stage**

[DOWNLOAD REPORT]

- Excel file
- Each row: Player, Stage, Points, Breakdown
- Useful for analysis

**Scoring Analysis**

[DOWNLOAD REPORT]

- Excel file
- Scoring distribution
- Average points per stage
- Prediction accuracy analysis

**Team Composition Summary**

[DOWNLOAD REPORT]

- Excel file
- All teams with cyclist selections
- Budget usage
- Point distribution

---

### Download Progress

When downloading:

Progress card:

- "Preparing your report..."
- Message: "This may take a moment"
- File downloads automatically

---

## Responsive Behavior

### Tablet (768px - 1024px)

- Form fields: Full width, stacked
- Tables: Scrollable horizontally if needed
- Buttons: Full width at bottom
- Padding: 16px (reduced from 32px)
- Font sizes: Same as desktop

### Mobile (< 768px)

NOT SUPPORTED for admin panel
Admin functions require desktop/laptop

---

## Loading States

Initial Load:

- Show skeleton loaders
- Gray placeholder boxes
- Pulse animation

Form Submission:

- Disable form inputs
- Show loading spinner
- Display "PROCESSING..." message

Table Data:

- Show skeleton rows
- Until real data loads

---

## Error Handling

### General Error

Full-width alert at top:

- Red border-left (4px), light red background
- Message: "Error [specific error]. Please try again."
- Close button: X
- Retry button (if applicable)

### Field Validation Error

Below field:

- Red text, 12px
- Message: "[Field name] is required" or specific error

### Delete Confirmation

Modal:

- Title: "Confirm Delete"
- Message: "Are you sure? This cannot be undone."
- Buttons: [CANCEL] [DELETE]

---

## Accessibility Features

### Color Contrast

- All text ≥ 4.5:1 contrast ratio
- Links distinguishable without color alone

### Forms

- All labels associated with inputs
- Error messages linked to fields (aria-describedby)
- Required fields marked

### Tables

- Semantic <table>, <thead>, <tbody> markup
- Column headers (<th>)
- Data rows properly associated

### Focus Management

- Focus indicator: 2px #0066CC outline
- Logical tab order
- Focus visible on all interactive elements

### Keyboard Navigation

- All admin functions keyboard accessible
- Tab/Shift+Tab for navigation
- Enter to submit
- Escape to close modals

---

## Performance

- Page load: < 2 seconds
- Form submission: < 1 second
- Table rendering: < 500ms
- Report generation: < 5 seconds

---

## Analytics Events

Track these admin actions:

- Player creation
- Cyclist imports
- Stage result entry
- Scoring calculations
- Configuration changes
- Report exports
- Time spent per admin task

---
