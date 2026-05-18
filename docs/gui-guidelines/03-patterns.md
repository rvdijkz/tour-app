# Interaction Patterns & Workflows

Common interaction patterns used throughout the Tour Game application.

---

## Table of Contents

1. Form Submission Pattern
2. Error Handling Pattern
3. Loading States Pattern
4. Confirmation Dialog Pattern
5. Validation Pattern
6. Search & Filter Pattern
7. Modal Workflows
8. Edit/Save Pattern
9. Multi-Step Wizard Pattern
10. Data Refresh Pattern
11. Tab Navigation Pattern
12. Dropdown Menu Pattern
13. Notification/Toast Pattern
14. Empty State Pattern
15. Breadcrumb Navigation Pattern
16. Disabled State Pattern

---

## 1. Form Submission Pattern

### Purpose

Handle form submission with validation, loading, and feedback.

### Flow

**User initiates submission:**

1. Click [SUBMIT] button
2. Client-side validation runs
3. If validation fails → Show inline errors (don't submit)
4. If validation passes → Proceed to step 4

**Submit request:**

1. Disable form inputs
2. Change button to loading state: "SUBMITTING..."
3. Show spinner icon in button
4. Disable all interactive elements

**Server processing:**

1. Send request to backend
2. Show loading indicator (optional)
3. Wait for response (timeout: 30 seconds)

**Success response:**

1. Enable form inputs
2. Show success message (alert card)
3. Auto-dismiss message after 5 seconds OR manual close
4. Clear form (or redirect to next page)
5. Reset button to normal state: "SUBMIT"

**Error response:**

1. Enable form inputs
2. Show error message (red alert card)
3. Error persists until dismissed
4. Reset button to normal state: "SUBMIT"
5. User can retry

### Visual States

Default State: [ SUBMIT ] ← Blue background, clickable
Loading State: [ 🔄 SUBMITTING... ] ← Disabled, spinner icon
Success State: ✓ Form submitted successfully! [ SUBMIT ] ← Re-enabled
Error State: ✗ Unable to save. Please try again. [ SUBMIT ] ← Re-enabled, red border

---

## 2. Error Handling Pattern

### Error Types

#### Validation Errors (Client-Side)

Shown: Immediately, without server call
Style: Red inline text below field
Persistence: Remains until field corrected
Example: "Email must be a valid format"

Visual:
Email:
[invalid@email]
✗ Email must be a valid format ← Red, 12px

#### Field Errors (Server-Side)

Shown: After form submission attempt
Style: Red alert card above form
Persistence: Remains until acknowledged or form resubmitted
Example: "Email already registered"

Visual:
┌─────────────────────────────────────┐
│ ✗ Email already registered │
│ Please use a different email │ ← Red card
└─────────────────────────────────────┘

#### General Errors (System)

Shown: Full-page alert at top
Style: Red alert card, full width
Persistence: Shows close button (X)
Example: "Unable to connect to server"

Visual:
┌──────────────────────────────────────────┐
│ ✗ Unable to connect to server. Try again.│ [X]
└──────────────────────────────────────────┘

#### Retry Pattern

For transient errors:

- Show error message
- Provide [RETRY] button
- Track retry count (show after 3 retries: "Contact support")

Visual:
✗ Server error. Please try again.
[RETRY] [CANCEL]

### Error Recovery Suggestions

Always provide next steps:

| Error                    | Suggestion                            |
| ------------------------ | ------------------------------------- |
| Invalid email format     | Use format: name@example.com          |
| Password too weak        | Use 8+ characters, numbers, uppercase |
| Email already registered | Log in instead (link)                 |
| Server error             | RETRY button                          |
| Network timeout          | Check connection, then RETRY          |

---

## 3. Loading States Pattern

### Loading Skeletons

Use for: Initial page load, data fetch

Before loading:
[████████░░░░] ← Gray placeholder
[████░░░░░░░░] ← Pulse animation
[██████████░░]

After loading:
[Actual Content Here]

Styling:

- Background: Light gray #EEEEEE
- Animation: Pulse 1s infinite (opacity fade)
- Duration: Until real data loads

### Loading Spinners

Use for: Button states, small operations

[🔄 LOADING...] ← Spinner rotates

Styling:

- Icon: Spinning circle (360° rotation, 1s)
- Color: Match button color
- Position: Left side of button text

### Progress Bars

Use for: Long operations (upload, import)

Uploading cyclists... 45%
[████████░░░░░░░░░░░] ← 8px height

Styling:

- Background: #EEEEEE
- Fill: #0066CC (animated)
- Percentage text: Right-aligned, 12px

### Loading Messages

Initial: "Loading..."
Short: "Still loading..." (after 3 seconds)
Long: "This may take up to 2 minutes..." (after 10 seconds)

Font: 12px, gray #666666

---

## 4. Confirmation Dialog Pattern

### When to Use

- Destructive actions (delete, reset)
- Irreversible actions (submit, lock)
- High-consequence actions (import data)

### Dialog Structure

┌─────────────────────────────────────┐
│ Confirm Action [X] │
├─────────────────────────────────────┤
│ │
│ Are you sure you want to delete │
│ your team? This cannot be undone. │
│ │
│ [CANCEL] [DELETE] │
└─────────────────────────────────────┘

### Components

Title: "Confirm Action"

- H3, 18px, bold

Message: Clear, specific question

- 14px, regular
- Explain consequence in 1-2 sentences

Buttons:

- [CANCEL] - Secondary (border style)
- [DELETE] - Danger (red background)
- Spacing: 8px between buttons

### Behavior

- Focused on dialog (can't interact with page)
- Escape key = CANCEL
- Default focus: CANCEL button (safe default)
- Click outside dialog: CANCEL

### Examples

Action: Delete Team
"Are you sure you want to delete your team? This cannot be undone and you will need to create a new one."
[CANCEL] [DELETE TEAM]

Action: Submit Team (Lock)
"Confirm team submission? Your team will be locked and you cannot make changes until the tour ends. Your finisher prediction will also be locked."
[CANCEL] [SUBMIT TEAM]

Action: Reset Password
"Reset password? A new temporary password will be sent to your email. You'll need to change it on next login."
[CANCEL] [RESET]

---

## 5. Validation Pattern

### Real-Time Validation

Trigger: On blur (field loses focus)
Not on keystroke (too frequent)

Email: [user@example.com]
↓ (user leaves field)
✓ Valid ← Green checkmark (optional)

### Instant Feedback

For: Required fields, format validation

Password: [myPassword123]

         ✓ Length: 8+ characters
         ✓ Uppercase: A-Z
         ✓ Lowercase: a-z
         ✓ Numbers: 0-9

         Strength: Strong ← Green

### Budget Validation

For: Point budget tracking

Standard Pool: [████████░░] 370/400
Remaining: 30 points ← Green if OK

Reserve Pool: [██░░░░░░░░] 75/110 ← Green if OK

Team: ✓ Valid ← Green checkmark
OR
✗ Invalid - Exceeded reserve budget by 5 points ← Red

### Validation Rules

Display rules before field:

Username:
Requirements:
├── 3-20 characters
├── Alphanumeric only (no spaces)
└── Unique (not taken)

[____________]

As user types:
[user123]

✓ Length OK
✓ Characters OK
⌛ Checking availability...

After validation:
[user123]
✓ Available! ← Green

---

## 6. Search & Filter Pattern

### Search Box

Layout: Top of table

[🔍 Search by name...]

┌─────────────────────────┐
│ John Smith [X] │ ← Real-time search
│ Jane Smith │
│ John Doe │
└─────────────────────────┘

Showing: 3 of 145 results

Behavior:

- Real-time search (debounced 300ms)
- Shows dropdown suggestions
- [x] clear button
- Escape key closes dropdown

### Filter Controls

Position: [All Positions ▼]
Country: [All Countries ▼]
Status: [All Statuses ▼]

Sort by: [Game # ▼]

Results: 145 cyclists

Behavior:

- Multiple filters can be active
- Changes update results immediately
- Show active filter count: "2 filters active"
- [Clear all filters] link

### Search Results

No results found for "xyz"

Did you mean: [users] [tours]

Or try:
├── Clearing filters
├── Checking spelling
└── Trying different keywords

---

## 7. Modal Workflows

### Open Modal

Trigger: Click button, link, or row

Before:
[Players] [Add New Player] ← Click

After:
[Overlay - 50% dark]
┌──────────────────┐
│ Add New Player│[X]│
├──────────────────┤
│ [Form fields] │
├──────────────────┤
│ [SAVE] [CANCEL] │
└──────────────────┘

Behavior:

- Fade in overlay (0.2s)
- Modal animates from center (scale + fade, 0.3s)
- Page behind is not scrollable
- Focus trapped in modal
- Escape key closes (if not critical action)

### Close Modal

Trigger: Click [X], [CANCEL], or Escape key

Before:
[Modal open with form]

After:
[Modal fades out]
[Page visible, scrollable again]

Options:

1. Close without saving → Confirm if form has unsaved changes
2. Close after success → Auto-close after 2 seconds
3. Close on error → User clicks [X] or [CANCEL]

### Modal States

Empty/Initial:
┌──────────────────┐
│ Add New Player│[X]│
├──────────────────┤
│ [Empty form] │
├──────────────────┤
│ [SAVE] [CANCEL] │
└──────────────────┘

Filled:
┌──────────────────┐
│ Add New Player│[X]│
├──────────────────┤
│ Name: John Smith │
│ Email: j@ex.com │
├──────────────────┤
│ [SAVE] [CANCEL] │
└──────────────────┘

Loading:
┌──────────────────┐
│ Add New Player│[X]│
├──────────────────┤
│ [🔄 Saving...] │
├──────────────────┤
│ [SAVE] [CANCEL] │ ← Disabled
└──────────────────┘

Success:
┌──────────────────┐
│ ✓ Player created│[X]│
│ │
│ John Smith │
│ john@example.com│
└──────────────────┘
[Auto-closes after 2s]

---

## 8. Edit/Save Pattern

### View Mode

Team Name: My Team
Status: ACTIVE (locked, non-editable)

Points: 450
[View Details]

Buttons: [EDIT] [DELETE]

### Edit Mode

Team Name: [My Team] ← Editable field
Status: ACTIVE (locked, non-editable)

Points: 450
[Edit Details]

Buttons: [SAVE] [CANCEL]

### Behavior

1. Click [EDIT] → Fields become editable
2. Change values
3. Click [SAVE] → Submit changes
4. On success → Back to view mode, show "Changes saved"
5. On error → Remain in edit mode, show error
6. Click [CANCEL] → Discard changes, back to view mode

### Visual Feedback

Edit mode:

- Fields have blue border
- Cursor in field
- Background slightly lighter

Save state:

- [SAVE] disabled (gray) until changes made
- [SAVE] enabled (blue) when changes made

Saving:

- [🔄 SAVING...] ← Loading state

Success:
✓ Changes saved (shown 3 seconds then auto-dismiss)

Error:
✗ Unable to save. [RETRY]

---

## 9. Multi-Step Wizard Pattern

### Page 1: Team Info

Step 1 of 3: Team Information

Name: [____________]
Prediction: [____________]

[NEXT]

Buttons:

- [NEXT] - Goes to step 2
- No previous button (first step)

### Page 2: Select Cyclists

Step 2 of 3: Select Team Cyclists

Available: [List with search/filter]
Your Team: [14 positions to fill]

[BACK] [NEXT]

Validation:

- All 14 positions must be filled
- [NEXT] disabled until valid

### Page 3: Review & Confirm

Step 3 of 3: Review Team

Team Name: My Team
Prediction: 145 finishers
Team Composition: [List all 14]

[BACK] [SUBMIT]

Buttons:

- [BACK] - Return to step 2 (data preserved)
- [SUBMIT] - Final submission

### Visual Progress

Top of page:

Step 1 ● → Step 2 ○ → Step 3 ○

Or:

Step 1 ✓ Step 2 ✓ Step 3 ⊙ (current)

Styling:

- Completed: ✓ (green)
- Current: ⊙ (blue, bold)
- Upcoming: ○ (gray)

### Data Persistence

- Click [BACK] → Data preserved from previous step
- Browser refresh → Data lost (or use localStorage)
- Click [NEXT] → Current step validated before proceeding
- Can't skip steps (no direct clicking to step 3)

---

## 10. Data Refresh Pattern

### Auto-Refresh

Triggered: After successful operation

Player submits team
↓
System shows success message
↓
Page data refreshes (1-2 seconds)
↓
Leaderboard updates
↓
Message dismisses

### Manual Refresh

Trigger: Click [REFRESH] button

[🔄 REFRESH] ← Button

On click:

- Button becomes loading: [🔄 REFRESHING...]
- Data fetched from server
- Button returns to normal: [🔄 REFRESH]
- Data updated if changed

### Polling (For Live Updates)

For: Leaderboard, stage results

Every 30 seconds:

- Check server for new data
- If changed: Update page (no reload)
- If not changed: Continue polling
- Stop polling if user leaves page

Visual:

Last updated: 30 seconds ago ← Updates every second
[🔄 Refresh now] ← Manual refresh option

### Stale Data Warning

⚠️ This data is from 5 minutes ago. [REFRESH]

Or:

Last updated: 5 minutes ago (may be stale)

---

## 11. Tab Navigation Pattern

### Tab Structure

┌─────────────────────────────────────┐
│ [Overall] [By Stage] [History] │
├─────────────────────────────────────┤
│ │
│ [Content for selected tab] │
│ │
└─────────────────────────────────────┘

### Visual States

Inactive Tab:

- Text: Gray #666666
- Background: Transparent
- Border-bottom: 2px transparent
- Cursor: pointer

Active Tab:

- Text: Blue #0066CC, bold
- Background: Transparent
- Border-bottom: 2px #0066CC
- Cursor: default

Hover (Inactive):

- Background: Light gray #F5F5F5
- Text: Darker gray

### Behavior

- Click tab → Content changes below
- Active tab highlighted with blue bottom border
- Only one tab active at a time
- URL updates (e.g., ?tab=by-stage)
- Tab state preserved on page reload

### Keyboard Navigation

- Tab key: Move to next tab
- Shift+Tab: Move to previous tab
- Left/Right arrow: Change tab (when tab focused)
- Enter: Activate tab

---

## 12. Dropdown Menu Pattern

### Closed State

[Select Country ▼]

- Placeholder text (optional)
- Chevron icon (▼) indicates expandable
- Click to open

### Open State

[Select Country ▼]

┌──────────────┐
│ Belgium │
│ France │
│ Germany │
│ Italy │
│ Spain │
│ USA │
└──────────────┘

- Options scroll if > 10 items
- Highlighting on hover
- Selected item shows checkmark

### Behavior

- Click option → Dropdown closes, option selected
- Click outside → Dropdown closes
- Escape key → Dropdown closes
- Arrow keys → Navigate options
- Type letter → Jump to option starting with letter

### Selected State

[Belgium ▼] ← Shows selected value

Or with icon:
[🇧🇪 Belgium ▼]

---

## 13. Notification/Toast Pattern

### Success Toast

Position: Top-right, 20px from edges
Animation: Slide in from right (0.3s)

┌──────────────────────┐
│ ✓ Team saved │ [X]
└──────────────────────┘

Duration: 4 seconds, then auto-dismiss

Styling:

- Background: Light green #F0FFF0
- Border-left: 4px #00AA00
- Icon: Green checkmark ✓
- Font: 14px, #00AA00

### Error Toast

┌──────────────────────┐
│ ✗ Unable to save │ [X]
└──────────────────────┘

Duration: Manual dismiss (no auto-dismiss for errors)

Styling:

- Background: Light red #FFF0F0
- Border-left: 4px #CC0000
- Icon: Red X ✗
- Font: 14px, #CC0000

### Info Toast

┌──────────────────────┐
│ ℹ️ 2 players joined │ [X]
└──────────────────────┘

Styling:

- Background: Light blue #F0F8FF
- Border-left: 4px #0066CC
- Icon: ℹ️ (info)
- Font: 14px, #0066CC

### Animation

Enter: Slide in from right + fade in (0.3s ease-out)
Exit: Slide out to right + fade out (0.3s ease-in)

---

## 14. Empty State Pattern

### Generic Empty

┌─────────────────────────────┐
│ │
│ 📋 (large icon) │
│ │
│ No data available │ ← H3, bold
│ │
│ This section is empty. │ ← Body text
│ │
│ [Take action button] │ ← CTA
│ │
└─────────────────────────────┘

Styling:

- Center-aligned
- Icon: 64x64px, light gray #CCCCCC
- Padding: 48px
- Background: Light gray #F9F9F9 (optional)

### No Results Empty

No cyclists found for "xyz"

Did you mean:
├── [players]
├── [teams]
└── [results]

Or try: [Clear filters]

### No Permission Empty

🔒 Access Denied

You don't have permission to view this section.

Contact an administrator for access.

---

## 15. Breadcrumb Navigation Pattern

### Structure

Home > Dashboard > My Team > Edit

Format:

- Text links separated by ">"
- Current page not a link
- Spacing: 4px around separators

### Styling

- Font: 12px, gray #666666
- Links: Blue #0066CC, underline on hover
- Separator: Gray ">"
- Current: Not a link, bold

### Behavior

- Click link → Navigate to that page
- Current page shows in bold, no link
- Visible on all pages (except home)

### Responsive

Mobile (not supported in MVP):

On mobile, may collapse to:
Home / Current Page [Back button]

---

## 16. Disabled State Pattern

### Disabled Button

[SUBMIT] ← Normal, enabled

[SUBMIT] ← Disabled

Styling (disabled):

- Background: Light gray #CCCCCC
- Text: Gray #999999
- Cursor: not-allowed (not pointer)
- Opacity: 0.6

Why buttons are disabled:

- Form validation failed
- Required data missing
- User lacks permission
- Loading in progress
- Deadline passed

### Disabled Field

Email: [user@example.com] ← Disabled, read-only

Styling (disabled):

- Background: Light gray #F5F5F5
- Border: 1px #CCCCCC
- Text: Gray #999999
- Cursor: not-allowed

### Hover Behavior

Disabled elements:

- No hover effect
- Cursor: not-allowed
- Tooltip (optional): Explains why disabled
- No color change

---
