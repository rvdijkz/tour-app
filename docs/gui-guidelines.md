# GUI Guidelines

# Design Principles

- Responsive web application
- Desktop-first optimized
- Mobile usable
- Fast navigation
- Minimal clicks
- Clear score visualization
- High readability during live stages

---

# Visual Style

## General
- Clean sports-oriented interface
- Modern flat design
- Limited color palette
- Strong typography hierarchy

## Recommended Stack
- React
- TypeScript
- TailwindCSS
- shadcn/ui
- TanStack Query

---

# Layout

## Main Structure

Desktop:
- Top navigation bar
- Left sidebar navigation
- Main content area

Mobile:
- Collapsible navigation
- Bottom navigation for key screens

---

# Navigation

## Player Navigation
- Dashboard
- My Team
- Riders
- Standings
- Profile

## Admin Navigation
- Dashboard
- Players
- Rider Import
- Stage Results
- Score Processing
- Configuration

---

# Authentication Screens

## Login Page
Requirements:
- Simple centered login form
- Username/password
- Clear validation feedback

## First Login Password Change
Requirements:
- Mandatory password change
- Password strength validation
- Expiration warning

---

# Team Builder Screen

## Main Goals
- Fast rider selection
- Clear point budget visibility
- Validation feedback in real time

## Features
- Drag/drop optional
- Rider search
- Rider filtering
- Rider sorting

## Validation UI
Show:
- Remaining points
- Reserve points usage
- Duplicate rider warnings
- Missing nationality warning

## Team Position Visualization
Clearly distinguish:
- Standard riders
- Nationality rider
- Reserves
- Kluns rider

---

# Rider List Screen

## Columns
- Game number
- Official tour number
- Rider name
- Nationality
- Rider value
- Status

## Features
- Sortable columns
- Filtering
- Search
- Pagination

## Status Indicators
Use visual badges:
- Active
- Abandoned

---

# Standings Screen

## Main Leaderboard

Columns:
- Rank
- Player
- Team
- Stage points
- Total points

Features:
- Sortable
- Responsive
- Highlight movement in ranking

## Ranking Indicators
Use:
- Up/down arrows
- Color indicators
- Previous ranking comparison

---

# Team Detail Screen

## Required Information
- Team composition
- Rider scores
- Score explanations
- Reserve substitutions
- Penalties
- Jersey bonuses

## Visualization
Use:
- Expandable score details
- Rider cards
- Stage-by-stage breakdown

---

# Stage Result Entry Screen

## Admin Features
- Enter top 20 positions
- Enter last 5 positions
- Select jersey holders
- Select combativity rider
- Select abandonments

## UX Requirements
- Rider autocomplete
- Validation against duplicates
- Keyboard-friendly entry

---

# Excel Import Screen

## Requirements
- Upload progress
- Validation errors
- Duplicate detection
- Import summary

---

# Notifications

Use toast notifications for:
- Successful saves
- Validation errors
- Score processing completion
- Import completion

---

# Accessibility

Requirements:
- Keyboard navigable
- Proper contrast ratios
- Semantic HTML
- Screen reader support

---

# Performance Guidelines

- Lazy load large tables
- Paginate rider lists
- Cache standings
- Optimistic updates where safe

---

# Error Handling

Always provide:
- Human-readable errors
- Recovery suggestions
- Retry actions where applicable

---

# Recommended Frontend Structure

```text
frontend/src/
├── app/
├── pages/
├── components/
├── features/
├── services/
├── hooks/
├── types/
├── layouts/
├── store/
└── utils/