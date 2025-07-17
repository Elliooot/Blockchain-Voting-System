import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

export default buildModule("VotingModule", (m) => {
    const title = "First Ballot";
    const startTime = Math.floor(Date.now() / 1000) + 300; // Starting 5 mins later
    const duration = 86400; // Lasting for 1 day

    const voting = m.contract("Voting", [title, startTime, duration]);

    return { voting };
});