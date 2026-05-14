# Frontend Copilot Instructions

## Technical Stack
* React (Latest stable version)
* TypeScript (Strict mode enabled)

## Frontend Rules
1. **Functional Components Only:** Never write class-based components. Use React Hooks (`useState`, `useEffect`, `useMemo`, etc.) for state and lifecycle management.
2. **Strict TypeScript:** Avoid the `any` type completely. Define explicit interfaces or types for all component props, state, and API responses.
3. **Dedicated API Layer:** Do not use `fetch` or `axios` directly inside React components. All network requests must live in a dedicated API service layer (`/src/api`).
4. **State Management:** Keep state local where possible. Use React Context or a dedicated state manager only for global state (e.g., user authentication status).
5. **Component Design:** Keep components small, reusable, and single-responsibility. Extract UI elements into atomic components.
