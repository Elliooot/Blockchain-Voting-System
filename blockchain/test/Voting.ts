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

    it("1: Should create an election in constructor", async function () {
        const { voting } = await loadFixture(deployVotingFixture);
        const election = await voting.elections(1);
        expect(election.title).to.equal("First Election");
        expect(election.proposalCount).to.equal(0);
    });

    it("2: Should add proposals before voting starts", async function () {
        const { voting, admin } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).addProposal(1, "Proposal B");

        const proposalA = await voting.getProposal(1, 0);
        const proposalB = await voting.getProposal(1, 1);
        expect(proposalA.name).to.equal("Proposal A");
        expect(proposalB.name).to.equal("Proposal B");

        const election = await voting.elections(1);
        expect(election.proposalCount).to.equal(2);
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
        
        await (voting.connect(voter1) as any).vote(1, 1);
        await (voting.connect(voter2) as any).vote(1, 1);
        const proposalB = await voting.getProposal(1, 1);
        expect(proposalB.voteCount).to.equal(2);
    });
    
    it("5: Should prevent voting after duration", async function () {
        const { voting, voter1, admin } = await loadFixture(deployVotingFixture);
        await (voting.connect(admin) as any).addProposal(1, "Proposal A");
        await (voting.connect(admin) as any).registerVoter(1, voter1.address);
        
        await ethers.provider.send("evm_increaseTime", [86400 + 3600]); // Skip 1 day
        await ethers.provider.send("evm_mine", []); // Mining new block
        
        await expect((voting.connect(voter1) as any).vote(1, 0)).to.be.revertedWith("Voting is not active");
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

        await (voting.connect(voter1) as any).vote(1, 1);
        await (voting.connect(voter2) as any).vote(1, 0);
        await (voting.connect(voter3) as any).vote(1, 1);
        
        await ethers.provider.send("evm_increaseTime", [86400]);
        await ethers.provider.send("evm_mine", []);

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

        await (voting.connect(voter1) as any).vote(1, 0);
        await (voting.connect(voter2) as any).vote(1, 1);
        await (voting.connect(voter3) as any).vote(1, 1);
        
        await ethers.provider.send("evm_increaseTime", [86400]);
        await ethers.provider.send("evm_mine", []);

        await (voting.connect(admin) as any).finalizeResult(1);
        const result = await voting.getResult(1);
        expect(result).to.be.equal("Proposal B");
    })

    it("8: Should terminate voting", async function () {
        const { voting, admin } = await loadFixture(deployVotingFixture);
        
        await (voting.connect(admin) as any).terminateVoting(1);
        const election = await voting.elections(1);
        expect(election.terminated).to.be.true;
    });
});