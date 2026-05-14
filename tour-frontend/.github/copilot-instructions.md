# Frontend Copilot Instructions

## Technical Stack
* React (Latest stable version)
* TypeScript (Strict mode enabled)

## Educational & Code Style Requirements (Developer is Learning)
1. **Explain the 'Why':** When writing or modifying components, briefly explain the reasoning behind your architectural choices (e.g., why a specific Hook or state structure was chosen).
2. **Comprehensive Comments:** Add descriptive, clean comments to important blocks of code. Do not just state *what* the code does, but explain *how* React handles it (e.g., re-renders, dependency arrays).
3. **JSDoc for TypeScript:** Document interfaces, custom types, and component props using JSDoc to make tooltips informative within the IDE (VS Code/IntelliJ).

## Frontend Rules
1. **Functional Components Only:** Never write class-based components. Use React Hooks (`useState`, `useEffect`, `useMemo`, etc.) for state and lifecycle management.
2. **Strict TypeScript:** Avoid the `any` type completely. Define explicit interfaces or types for all component props, state, and API responses.
3. **Dedicated API Layer:** Do not use `fetch` or `axios` directly inside React components. All network requests must live in a dedicated API service layer (`/src/api`).
4. **State Management:** Keep state local where possible. Use React Context or a dedicated state manager only for global state (e.g., user authentication status).
5. **Component Design:** Keep components small, reusable, and single-responsibility. Extract UI elements into atomic components.
