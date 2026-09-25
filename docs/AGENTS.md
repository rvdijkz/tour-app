# Work instructions

This project is used to design and specify a web application consisting of a Spring Boot backend and React/TypeScript frontend.

This workspace corresponds to the `/docs` directory of the application repository.

During the specification phase, work only on files in this workspace unless I explicitly request otherwise.

Treat the approved specification files in this workspace as the source of truth for application requirements and design.

Prefer small, focused specification files over large monolithic documents.

Clearly distinguish requirements, design decisions, assumptions, open questions, acceptance criteria, and implementation details.

Before changing an existing specification, check whether the change affects other specifications or architectural documents.

Do not silently invent missing business requirements. Ask me when a decision materially affects application behavior.

Keep specifications implementation-ready so they can later be consumed by a coding agent such as Claude Code.

## Documentation structure

- `frontend/` contains specifications specific to the React/TypeScript frontend.
- `backend/` contains specifications specific to the Spring Boot backend.
- Specifications that apply to the whole application belong in the workspace root or in an appropriate shared subdirectory.
- Do not create new directories or reorganize the documentation structure without discussing it first.

## Change policy

- Make the smallest change necessary to satisfy the requested requirement.
- Do not rewrite or restructure existing specifications unless this is necessary for the requested change.
- Preserve existing decisions unless I explicitly change them.
- When a requested change conflicts with an existing specification, point out the conflict before resolving it.

## Consistency

- Use the same terminology consistently across all specifications.
- When changing a domain concept, API concept, business rule, or UI behaviour, check related specifications for consistency.
- Avoid duplicating requirements across multiple documents; reference the authoritative specification where appropriate.

## Interaction with the user

- Ask questions only when a missing decision materially affects the specification.
- When reasonable implementation details can be deferred to the coding agent, do not require me to make unnecessary technical decisions.
- Clearly identify assumptions when they are unavoidable.

## Implementation boundary

- Specifications may contain technical constraints and design decisions when they are relevant to implementation.
- Do not generate production code in this workspace.
- Do not prescribe low-level implementation details unless they are required by a design decision or explicitly requested.