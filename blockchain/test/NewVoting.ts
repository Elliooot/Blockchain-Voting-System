import { loadFixture } from "@nomicfoundation/hardhat-toolbox/network-helpers";
import { expect } from "chai";
import { ethers, ignition } from "hardhat";
import VotingModule from "../ignition/modules/deploy";

describe("Voting Contract", function() {

    // Define a fixture to set up the initial state
    async function deployVotingFixture() {
        const [admin, voter1, voter2, voter3] = await ethers.getSigners();
        const { voting } = await ignition.deploy(VotingModule);
        
        return { voting, admin, voter1, voter2, voter3 };
    }

    it("1: Should create a ballot successfully", async function () {
        const { voting, admin, voter1, voter2 } = await loadFixture(deployVotingFixture);

        const now = Math.floor(Date.now() / 1000);
        const startTime = now + 300; // Starting 5 mins later
        const duration = 86400; // Lasting for 1 day
        const voters = [voter1.address, voter2.address];

        await (voting.connect(admin) as any).createBallot(
            startTime, duration, voters
        );

        const ballot = await voting.ballots(0);
        expect(ballot.startTime).to.equal(startTime);
        expect(ballot.duration).to.equal(duration);
        expect(ballot.admin).to.equal(admin.address);
        expect(ballot.terminated).to.be.false;

        const proposal0 = await voting.getProposal(0, 0);
        const proposal1 = await voting.getProposal(0, 1);

        const voter1Info = await voting.getVoter(0, voter1.address);
        expect(voter1Info.isRegistered).to.be.true;
        const voter2Info = await voting.getVoter(0, voter2.address);
        expect(voter2Info.isRegistered).to.be.true;
    });

    it("2: Should allow voting during the period", async function () {
        const { voting, admin, voter1, voter2 } = await loadFixture(deployVotingFixture);

        const now = Math.floor(Date.now() / 1000);
        const startTime = now + 300; // Starting 5 mins later
        const duration = 86400; // Lasting for 1 day
        const voters = [voter1.address, voter2.address];

        await (voting.connect(admin) as any).createBallot(
            startTime, duration, voters
        );
        
        await ethers.provider.send("evm_increaseTime", [360]); // Skip 5 + 1 mins
        await ethers.provider.send("evm_mine", []); // Mining new block
        
        await (voting.connect(voter1) as any).vote(0, 1);
        await (voting.connect(voter2) as any).vote(0, 1);
        const proposalB = await voting.getProposal(0, 1);
        expect(proposalB.voteCount).to.equal(2);
    });
    
    it("3: Should finalize and get result after voting", async function () {
        const { voting, admin, voter1, voter2, voter3 } = await loadFixture(deployVotingFixture);

        const now = Math.floor(Date.now() / 1000);
        const startTime = now + 300; // Starting 5 mins later
        const duration = 3600; // Lasting for 1 hour
        const voters = [voter1.address, voter2.address, voter3.address];

        await (voting.connect(admin) as any).createBallot(
            startTime, duration, voters
        );

        await ethers.provider.send("evm_increaseTime", [360]); // Skip 6 mins
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(voter1) as any).vote(0, 0);
        await (voting.connect(voter2) as any).vote(0, 1);
        await (voting.connect(voter3) as any).vote(0, 1);
        
        await ethers.provider.send("evm_increaseTime", [3600]); // Skip 1 hour
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(admin) as any).finalizeResult(0);
        const result = await voting.getResult(0);
        expect(result).to.be.equal("Proposal B");

        const voteCounts = await voting.getVoteCounts(0);
        expect(voteCounts[0]).to.equal(1);
        expect(voteCounts[1]).to.equal(2);
    });

    it("4: Should handle tie results correctly", async function () {
        const { voting, admin, voter1, voter2 } = await loadFixture(deployVotingFixture);

        const now = Math.floor(Date.now() / 1000);
        const startTime = now + 300;
        const duration = 3600;
        const voters = [voter1.address, voter2.address];

        await (voting.connect(admin) as any).createBallot(
            startTime, duration, voters
        );

        await ethers.provider.send("evm_increaseTime", [360]);
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(voter1) as any).vote(0, 0);
        await (voting.connect(voter2) as any).vote(0, 1);
        
        await ethers.provider.send("evm_increaseTime", [3600]);
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(admin) as any).finalizeResult(0);
        const result = await voting.getResult(0);
        
        expect(result).to.equal("Proposal A, Proposal B");
    });
});