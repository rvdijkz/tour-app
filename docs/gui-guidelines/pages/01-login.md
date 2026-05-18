# Login Page Specification

## Page Overview

Purpose: Authenticate users into the Tour Game application
Path: `/login`
Accessibility: WCAG 2.1 Level AA

---

## Visual Layout

### Centered Login Card

┌────────────────────────────────────────────────────┐
│ │
│ [APP LOGO] │
│ Tour Game │
│ Fantasy Cycling Competition │
│ │
│ ┌──────────────────────────────────────────────┐ │
│ │ │ │
│ │ Email or Username: │ │
│ │ [] │ │
│ │ │ │
│ │ Password: │ │
│ │ [] │ │
│ │ │ │
│ │ ☐ Remember me │ │
│ │ │ │
│ │ [ LOGIN ] │ │
│ │ Forgot password? │ │
│ │ │ │
│ │ ──────────────────────────────────── │ │
│ │ │ │
│ │ Don't have an account? │ │
│ │ Contact your administrator │ │
│ │ │ │
│ └──────────────────────────────────────────────┘ │
│ │
└────────────────────────────────────────────────────┘

## Layout

### Visual Hierarchy

Main container:

- Centered card, max-width 400px
- Positioned in center of viewport (both horizontally and vertically)
- Responsive: On tablets, takes 90% width with margins

### Header Section

- APP Logo (optional, 64x64px)
- Title: "Tour Game" (H1, 32px, bold)
- Subtitle: "Fantasy Cycling Competition" (14px, gray #666666)
- Spacing: 32px margin-bottom

---

## Form Structure

### Card Container

- Background: #FFFFFF
- Border: 1px solid #DDDDDD
- Border-radius: 8px
- Padding: 32px
- Box-shadow: 0 2px 8px rgba(0,0,0,0.12)
- Max-width: 400px

### Form Fields

**1. Username/Email Field**

Label: "Email or Username"

- 14px SemiBold, #333333
- Margin-bottom: 8px
- Required indicator: Red asterisk (\*)

Input:

- Height: 40px
- Padding: 8px 12px
- Border: 1px solid #CCCCCC
- Placeholder: "Enter your email or username"
- Focus: Blue border (2px), blue shadow
- Autocomplete: email or username

**2. Password Field**

Label: "Password"

- 14px SemiBold, #333333
- Margin-bottom: 8px
- Required indicator: Red asterisk (\*)

Input:

- Height: 40px
- Padding: 8px 12px
- Border: 1px solid #CCCCCC
- Type: password (dots/circles)
- Placeholder: "Enter your password"
- Focus: Blue border (2px), blue shadow

**3. Remember Me Checkbox (Optional)**

Checkbox:

- Size: 16x16px
- Margin-right: 8px
- Label: "Remember me" (14px)
- Checked state: Blue background (#0066CC)
- Margin-bottom: 16px

---

## Buttons

### Login Button (Primary)

- Width: 100% (full width of form)
- Height: 40px
- Background: #0066CC
- Text: "LOGIN" (white, 14px, bold)
- Border: None
- Border-radius: 4px
- Hover: #0052A3
- Active: #003D7A
- Disabled: #CCCCCC
- Margin-bottom: 16px
- Cursor: pointer

### Forgot Password Link (Text Link)

- Text: "Forgot password?" (14px, blue #0066CC)
- Position: Right-aligned, below login button
- Hover: Underline
- Margin-bottom: 24px
- Action: Opens password reset modal or page

---

## Additional Content

### Divider Line

- 1px solid #DDDDDD
- Margin: 24px 0

### Footer Message

Text: "Don't have an account? Contact your administrator"

- 14px, gray (#666666)
- Centered
- Margin-top: 24px

---

## Error Handling

### Form Validation

**Empty Fields:**

- Show on form submission attempt
- Display: Red error message below field
- Message: "[Field name] is required"
- Text color: #CC0000
- Font: 12px, bold

**Invalid Credentials:**

- Show: "Invalid email/username or password"
- Position: Red alert card above form
- Padding: 12px 16px
- Border-left: 4px #CC0000
- Background: Light red #FFF0F0
- Icon: Red X circle
- Dismissible: X button on right
- Animation: Slide down 0.3s ease

**Account Locked:**

- Show: "Your account is temporarily locked. Please try again in 15 minutes."
- Same styling as invalid credentials
- Appear after 5 failed login attempts
- Auto-dismiss after 10 seconds

**Server Error:**

- Show: "Unable to connect to server. Please try again later."
- Same styling as invalid credentials
- Include retry button

---

## Password Change (First Login)

### Modal Overlay

Appears immediately after successful first login if password change is required.

### Modal Overlay

Appears immediately after successful first login if password change is required.
┌─────────────────────────────────────────────────┐
│ Reset Your Password [X] │
├─────────────────────────────────────────────────┤
│ │
│ This is your first login. You must change │
│ your password. │
│ │
│ Current Password: │
│ [***************] │
│ │
│ New Password: │
│ [myPassword123] │
│ │
│ ✓ Length: 8+ characters │
│ ✓ Uppercase: A-Z │
│ ✓ Lowercase: a-z │
│ ✓ Numbers: 0-9 │
│ Strength: Strong ✓ │
│ │
│ Confirm Password: │
│ [myPassword123] │
│ │
│ Password expires in: 11 hours 45 minutes │
│ │
│ [SAVE & LOGIN] [CANCEL] │
│ │
└─────────────────────────────────────────────────┘

Modal Content:

Title: "Reset Your Password"

- H3, bold, 18px

Explanation:

- 14px, gray (#666666)
- Text: "This is your first login. You must change your password."
- Margin-bottom: 16px

**Form Fields:**

1. Current Password:
   - Label: "Current Password"
   - Input: password type
   - Height: 40px
   - Margin-bottom: 16px

2. New Password:
   - Label: "New Password"
   - Input: password type
   - Height: 40px
   - Strength indicator below:
     - Weak: Red (#CC0000) "Password is too weak"
     - Medium: Yellow (#FFAA00) "Password is medium strength"
     - Strong: Green (#00AA00) "Password is strong"
   - Requirements (12px gray text):
     - At least 8 characters
     - Contains uppercase letter
     - Contains lowercase letter
     - Contains number or special character
   - Margin-bottom: 16px

3. Confirm Password:
   - Label: "Confirm Password"
   - Input: password type
   - Height: 40px
   - Validation: Must match "New Password"
   - Error message if mismatch: Red text "Passwords don't match"
   - Margin-bottom: 24px

**Timer:**

- Display: "Password expires in: XX hours YY minutes"
- Position: Below form fields
- Text: 12px, orange/yellow (#FFAA00)
- Update every minute

**Buttons:**

1. Save Button:
   - Width: 100%
   - Height: 40px
   - Background: #0066CC
   - Text: "SAVE & LOGIN" (white, bold)
   - Hover: #0052A3
   - Disabled if passwords don't match
   - Margin-bottom: 8px

2. Cancel Button:
   - Width: 100%
   - Height: 40px
   - Background: Transparent
   - Border: 2px #0066CC
   - Text: "#0066CC"
   - Hover: Light blue background #F0F5FF
   - Note: Not recommended (time limit still applies)

**Success Message:**

- After successful password change
- Redirect to main dashboard automatically
- Show brief toast notification: "Password changed successfully"
- Animation: Fade in/out 0.3s

---

## Responsive Behavior

### Tablet (768px - 1024px)

- Card width: 90% with side margins
- Padding: 24px (reduced from 32px)
- Font sizes: Same
- Button heights: Same

### Mobile (< 768px)

NOT SUPPORTED in MVP
Future phase will handle mobile login

---

## Keyboard Navigation

Tab Order:

1. Email/Username input
2. Password input
3. Remember me checkbox
4. Login button
5. Forgot password link

Enter Key:

- From any input field: Submit form
- On login button: Activate login

Escape Key:

- If modal open (password change): Do nothing (force completion)

---

## Accessibility Features

### Color Contrast

- All text: ≥ 4.5:1 contrast ratio
- Links: Distinguishable without color

### Labels

- All form fields have associated labels (not just placeholder)
- "Required" asterisks: Red color + aria-label "required"

### Error Messages

- Associated with form fields using aria-describedby
- Announced to screen readers
- Clear, actionable text

### Screen Readers

- "Login form" announced as main landmark
- Field labels read aloud
- Error messages read immediately when shown
- Button purposes clear

### Focus Management

- Focus indicator: 2px blue outline (#0066CC)
- Visible on all interactive elements
- Focus order: Logical top-to-bottom

---

## Performance

- Page load: < 2 seconds
- Form submission: Provide feedback within 0.5 seconds
- Error display: Instant (no loading)

---

## Security Notes

- Password field: Type="password" (dots/circles)
- Form: HTTPS only
- Credentials: Not logged or cached
- Session: Expires after 30 minutes of inactivity
- CSRF protection: Use token-based validation

---

## States & Variations

### Loading State

- Login button:
  - Disabled during request
  - Show spinner/loading icon
  - Text: "LOGGING IN..."
  - Duration: 1-3 seconds typically

### Session Expired

- Redirect to login page
- Show message: "Your session has expired. Please log in again."
- Alert styling: Orange/yellow
- Auto-dismiss: After 5 seconds

### IP Restricted

- Show: "Login from this location is not allowed."
- Contact support message
- Allow 1 retry button

---
