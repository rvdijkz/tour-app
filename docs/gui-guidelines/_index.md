# GUI Guidelines - Complete Documentation

## Overview

This folder contains comprehensive UI/UX guidelines for the Tour Game web application.
The guidelines are organized by topic and page type for easy reference and modular development.

---

## Quick Navigation

### 📐 Design Foundations

- [Design Principles](01-design-principles.md) - Colors, typography, grid, spacing
- [Components](02-components.md) - Forms, buttons, tables, cards, modals
- [Patterns](03-patterns.md) - Common interaction patterns

### 🖼️ Page-Specific Guides

See [pages/](pages/) folder for detailed wireframes and specifications:

- [Login Page](pages/01-login.md)
- [Player Dashboard](pages/02-player-dashboard.md)
- [Team Composition](pages/03-team-composition.md)
- [Standings/Leaderboard](pages/04-standings.md)
- [Admin Pages](pages/05-admin-pages.md)

---

## Document Structure

Each document follows this pattern:

1. Overview / Purpose
2. Visual Specifications
3. Interactive Behavior
4. Responsive Behavior
5. Accessibility Notes
6. Code Examples (where applicable)

---

## Key Design System Values

| Aspect        | Value                                       |
| ------------- | ------------------------------------------- |
| Layout Grid   | 12-column, 16px gutter                      |
| Primary Font  | Segoe UI, Roboto, Helvetica Neue            |
| Primary Color | #0066CC (Blue)                              |
| Spacing Unit  | 8px (multiples)                             |
| Breakpoints   | Desktop 1920px, Laptop 1440px, Tablet 768px |

---

## Color Palette

### Primary Colors

- Primary Blue: #0066CC
- Success Green: #00AA00
- Warning Yellow: #FFAA00
- Danger Red: #CC0000
- Neutral Dark: #333333
- Neutral White: #FFFFFF

### Jersey Colors

- Yellow Jersey: #FFD700
- White Jersey: #FFFFFF (with #CCCCCC border)
- Green Jersey: #00AA00
- Polka Dot: #FF6600

---

## Typography Scale

- H1: 32px, Bold
- H2: 24px, Bold
- H3: 18px, Bold
- Body: 14px, Regular
- Small: 12px, Regular

---

## For Frontend Developers

When implementing UI:

1. Start with: [Design Principles](01-design-principles.md)
2. Use components from: [Components](02-components.md)
3. Reference page layout: [Pages folder](pages/)
4. Check interactions: [Patterns](03-patterns.md)

---

## For Product/Design Team

When reviewing design:

1. Check consistency against [Design Principles](01-design-principles.md)
2. Verify component usage against [Components](02-components.md)
3. Validate page layout against [Pages](pages/) folder

---

## Accessibility Checklist

All pages should meet:

- WCAG 2.1 Level AA compliance
- Keyboard navigation support
- Screen reader friendly
- Color contrast ≥ 4.5:1 for text
- Form error messages clear and visible

---

## Responsive Design

The application is designed mobile-first for tablet+ devices:

- Tablet (768px): Full responsive layout
- Laptop (1440px): Optimized multi-column layouts
- Desktop (1920px): Expanded horizontal space

---

## Related Documents

- PRD: See `../prd.md` for feature requirements
- Domain Model: See `../domain-model.md` for data structures
- Backend API: See `../../backend/.github/` for API contracts

---
