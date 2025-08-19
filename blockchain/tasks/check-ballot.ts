import { task } from "hardhat/config";
import { HardhatRuntimeEnvironment } from "hardhat/types";
import fs from "fs";
import path from "path";

task("check-ballot", "Fetches and displays information for a specific ballot from the blockchain")
  .addParam("ballotid", "The ID of the ballot to check")
  .setAction(async (taskArgs, hre: HardhatRuntimeEnvironment) => {
    const { ballotid } = taskArgs;
    const { ethers } = hre;

    console.log(`🔍 Fetching information for Ballot ID: ${ballotid}...`);

    // --- Automatically read the deployed contract address ---
    const chainId = (await ethers.provider.getNetwork()).chainId.toString();
    const deploymentPath = path.join(
      __dirname,
      `../ignition/deployments/chain-${chainId}/deployed_addresses.json`
    );

    if (!fs.existsSync(deploymentPath)) {
      console.error(`❌ Deployment address file not found at: ${deploymentPath}`);
      console.error("Please make sure you have deployed the contract using 'npx hardhat ignition deploy'");
      return;
    }

    const deployedAddresses = JSON.parse(fs.readFileSync(deploymentPath, "utf8"));
    const contractAddress = deployedAddresses["VotingModule#Voting"];

    if (!contractAddress) {
      console.error("❌ Voting contract address not found in deployed_addresses.json");
      return;
    }
    console.log(`🏢 Using contract at address: ${contractAddress}`);

    // --- Connect to the contract ---
    const votingContract = await ethers.getContractAt("Voting", contractAddress);

    // --- Check if ballot exists ---
    const nextBallotId = await votingContract.nextBallotId();
    if (BigInt(ballotid) >= nextBallotId) {
        console.error(`❌ Error: Ballot ID ${ballotid} does not exist.`);
        console.log(`ℹ️ The next available Ballot ID is ${nextBallotId.toString()}.`);
        return;
    }

    const ballotInfo = await votingContract.ballots(ballotid);

    if (ballotInfo.startTime === 0n) { // 0n is BigInt zero
        console.error(`❌ Error: Ballot ID ${ballotid} seems to exist but has no data (startTime is 0). It might have been created on a previous deployment.`);
        return;
    }

    console.log("\n✅ Ballot Information Found:");
    console.log("===================================");
    console.log(`  Admin: ${ballotInfo.admin}`);
    console.log(`  Start Time: ${new Date(Number(ballotInfo.startTime) * 1000).toLocaleString()} (${ballotInfo.startTime})`);
    console.log(`  Duration: ${ballotInfo.duration} seconds`);
    console.log(`  Terminated: ${ballotInfo.terminated}`);
    console.log(`  Proposal Count: ${ballotInfo.proposalCount}`);
    console.log("===================================\n");

    // --- Get Proposal info ---
    if (ballotInfo.proposalCount > 0) {
      console.log("📋 Proposals:");
      for (let i = 0; i < ballotInfo.proposalCount; i++) {
        const proposal = await votingContract.proposalsByBallot(ballotid, i);
        console.log(`      Vote Count: ${proposal.voteCount}`);
      }
      console.log("-----------------------------------");
    } else {
      console.log("📋 No proposals found for this ballot.");
    }
  });