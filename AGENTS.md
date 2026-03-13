# Repository Guide for Agents

## Project Structure
- `backend/`: Spring Boot 3 (Java 21), PostgreSQL, Web3j integration
- `frontend/`: React + Vite + Tailwind + MUI
- `blockchain/`: Hardhat Solidity contracts and tests

## Key Conventions
- **Backend config lives in** `backend/src/main/resources/application.yml`.  
  Avoid modifying `application.properties` (kept as a pointer only).
- **DB changes must use Flyway** migrations in `backend/src/main/resources/db/migration/`.
- **Smart contract wrapper** in `backend/src/main/java/com/voting/spring_boot_project/contract/Voting.java` is generated.  
  Do not edit manually; regenerate if the ABI changes.
- **Environment variables** hold secrets (JWT, private keys, Alchemy key).  
  Never commit `.env` or key files.

## Development Notes
- Backend uses Web3j to call the contract; changes to Solidity typically require:
  1. Recompile and redeploy contract in `blockchain/`
  2. Update `SEPOLIA_CONTRACT_ADDRESS`
  3. Regenerate the Java wrapper if ABI changes
- Frontend styling prefers Tailwind classes and MUI theme overrides.  
  Global visuals are controlled in `frontend/src/index.css` and the MUI theme in `frontend/src/pages/Dashboard.tsx`.

## Common Commands (Reference)
- Backend: `./mvnw spring-boot:run`
- Frontend: `npm install && npm run dev`
- Blockchain: `npx hardhat test` and `npx hardhat ignition deploy ...`
