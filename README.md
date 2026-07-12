# Digital Nepal Ecosystem

Backend services for Digital Nepal Ecosystem platform by Intersect Info Developers.

## Developer setup

1. Clone the repo and checkout `develop`.
2. Install dependencies for the service in your runtime stack.
3. Copy `.env.example` to `.env` and update your local values.
4. Start with Docker if the service supports it:
   - `docker compose up --build`
5. For local development, use the app-specific startup command once dependencies are installed.

## Security and branch rules

- Use `security.md` for security guidance.
- Do not commit secrets or `.env` files.
- Use pull requests for changes to `develop` and `main`.
- Avoid force pushes to protected branches.
