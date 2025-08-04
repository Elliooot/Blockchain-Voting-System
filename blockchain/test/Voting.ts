import { loadFixture } from "@nomicfoundation/hardhat-toolbox/network-helpers";
import { expect } from "chai";
import { ethers, ignition } from "hardhat";
// import { Voting } from "../ignition/deployments/chain-1337/deployed_addresses.json";
import VotingModule from "../ignition/modules/deploy";

describe("Voting Contract", function() {

    // Define a fixture to set up the initial state
    async function deployVotingFixture() {
        const [admin, voter1, voter2, voter3] = await ethers.getSigners();
        const { voting } = await ignition.deploy(VotingModule);
        
        return { voting, admin, voter1, voter2, voter3 };
    }

    // Helper function to create commitment hash
    function createCommitment(proposalId: number, salt: string): string {
        return ethers.solidityPackedKeccak256(
            ["uint256", "bytes32"],
            [proposalId, ethers.id(salt)]
        );
    }

    it("1: Should create an ballot in constructor", async function () {
        const { voting } = await loadFixture(deployVotingFixture);
        const ballot = await voting.ballots(1);
        expect(ballot.title).to.equal("First Ballot");
        expect(ballot.proposalCount).to.equal(0);
    });

    it("2: Should add proposals before voting starts", async function () {
        const { voting, admin } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).addProposal(1, "Proposal B");

        const proposalA = await voting.getProposal(1, 0);
        const proposalB = await voting.getProposal(1, 1);
        expect(proposalA.name).to.equal("Proposal A");
        expect(proposalB.name).to.equal("Proposal B");

        const ballot = await voting.ballots(1);
        expect(ballot.proposalCount).to.equal(2);
    });
    
    it("3: Should register voters before voting", async function () {
        const { voting, admin, voter1, voter2, voter3 } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        await (voting.connect(admin) as any).registerVoter(1, voter2.address);

        const registeredVoter1 = await voting.getVoter(1, voter1.address);
        expect(registeredVoter1.isRegistered).to.be.true;
        const registeredVoter2 = await voting.getVoter(1, voter2.address);
        expect(registeredVoter2.isRegistered).to.be.true;
        const registeredVoter3 = await voting.getVoter(1, voter3.address);
        expect(registeredVoter3.isRegistered).to.be.false;
    });

    it("4: Should allow voting during the period", async function () {
        const { voting, admin, voter1, voter2 } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).addProposal(1, "Proposal B");
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        await (voting.connect(admin) as any).registerVoter(1, voter2.address);
        
        await ethers.provider.send("evm_increaseTime", [300]); // Skip 5 mins
        await ethers.provider.send("evm_mine", []); // Mining new block
        
        // Create commitments
        const voter1Salt = ethers.id("voter1_secret");
        const voter2Salt = ethers.id("voter2_secret");
        const commitment1 = createCommitment(1, "voter1_secret");
        const commitment2 = createCommitment(1, "voter2_secret");
        
        await (voting.connect(voter1) as any).commitVote(1, commitment1);
        await (voting.connect(voter2) as any).commitVote(1, commitment2);

        // Skip to reveal phase
        await ethers.provider.send("evm_increaseTime", [86400]); // Skip 1 day
        await ethers.provider.send("evm_mine", []);

        // Reveal votes
        await (voting.connect(voter1) as any).revealVote(1, 1, voter1Salt);
        await (voting.connect(voter2) as any).revealVote(1, 1, voter2Salt);

        const proposalB = await voting.getProposal(1, 1);
        expect(proposalB.voteCount).to.equal(2);
    });
    
    it("5: Should prevent voting after duration", async function () {
        const { voting, voter1, admin } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        
        await ethers.provider.send("evm_increaseTime", [86400 + 3600]); // Skip 1 + 1 hour
        await ethers.provider.send("evm_mine", []); // Mining new block
        
        const commitment = createCommitment(0, "secret");
        await expect((voting.connect(voter1) as any).commitVote(1, commitment)).to.be.revertedWith("Voting is not active");
    });

    it("6: Should get the vote count correctly", async function () {
        const { voting, admin, voter1, voter2, voter3 } = await loadFixture(deployVotingFixture);

        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).addProposal(1, "Proposal B");
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        await (voting.connect(admin) as any).registerVoter(1, voter2.address);
        await (voting.connect(admin) as any).registerVoter(1, voter3.address);

        await ethers.provider.send("evm_increaseTime", [3600]);
        await ethers.provider.send("evm_mine", []);

        const voter1Salt = ethers.id("voter1_secret");
        const voter2Salt = ethers.id("voter2_secret");
        const voter3Salt = ethers.id("voter3_secret");
        const commitment1 = createCommitment(1, "voter1_secret");
        const commitment2 = createCommitment(0, "voter2_secret");
        const commitment3 = createCommitment(1, "voter3_secret");

        await (voting.connect(voter1) as any).commitVote(1, commitment1);
        await (voting.connect(voter2) as any).commitVote(1, commitment2);
        await (voting.connect(voter3) as any).commitVote(1, commitment3);
        
        await ethers.provider.send("evm_increaseTime", [86400]);
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(voter1) as any).revealVote(1, 1, voter1Salt);
        await (voting.connect(voter2) as any).revealVote(1, 0, voter2Salt);
        await (voting.connect(voter3) as any).revealVote(1, 1, voter3Salt);

        const proposalsCount = await voting.getVoteCounts(1);
        expect(proposalsCount[0]).to.equal(1);
        expect(proposalsCount[1]).to.equal(2);
    });
    
    it("7: Should finalize and get result after voting", async function () {
        const { voting, admin, voter1, voter2, voter3 } = await loadFixture(deployVotingFixture);

        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).addProposal(1, "Proposal B");
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        await (voting.connect(admin) as any).registerVoter(1, voter2.address);
        await (voting.connect(admin) as any).registerVoter(1, voter3.address);

        await ethers.provider.send("evm_increaseTime", [3600]);
        await ethers.provider.send("evm_mine", []);

        const voter1Salt = ethers.id("voter1_secret");
        const voter2Salt = ethers.id("voter2_secret");
        const voter3Salt = ethers.id("voter3_secret");
        const commitment1 = createCommitment(1, "voter1_secret");
        const commitment2 = createCommitment(1, "voter2_secret");
        const commitment3 = createCommitment(1, "voter3_secret");

        await (voting.connect(voter1) as any).commitVote(1, commitment1);
        await (voting.connect(voter2) as any).commitVote(1, commitment2);
        await (voting.connect(voter3) as any).commitVote(1, commitment3);
        
        await ethers.provider.send("evm_increaseTime", [86400]);
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(voter1) as any).revealVote(1, 1, voter1Salt);
        await (voting.connect(voter2) as any).revealVote(1, 1, voter2Salt);
        await (voting.connect(voter3) as any).revealVote(1, 1, voter3Salt);

        await (voting.connect(admin) as any).finalizeResult(1);
        const result = await voting.getResult(1);
        expect(result).to.be.equal("Proposal(s) with the most votes: Proposal B");
    })

    it("8: Should terminate voting", async function () {
        const { voting, admin } = await loadFixture(deployVotingFixture);
        
        await (voting.connect(admin) as any).terminateVoting(1);
        const ballot = await voting.ballots(1);
        expect(ballot.terminated).to.be.true;
    });
});