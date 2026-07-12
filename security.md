# Security Policy

This repository follows standard security practices to keep developer and user data safe.

## Reporting issues
- Report security vulnerabilities privately to the maintainers.
- Do not disclose security issues in public issues unless asked by the team.

## Secure development
- Do not commit secrets, API keys, passwords, or tokens.
- Add secrets to environment variables or secure vaults.
- Use `.gitignore` to exclude local config, credentials, and generated artifacts.

## Docker and environment files
- Do not commit `.env` files containing secrets.
- Use `.dockerignore` to keep build context clean and avoid leaking local files.

## Dependency management
- Keep dependencies up to date.
- Review new packages before adding them.
- Use the latest compatible runtime supported by the project.

## Access and branch policy
- Create changes through pull requests, not direct pushes to `develop` or `main`.
- Avoid force-pushing protected branches.
