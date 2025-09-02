import { task } from "hardhat/config";
import { HardhatRuntimeEnvironment } from "hardhat/types";
import fs from "fs";
import path from "path";

task("check-ballot", "Fetches and displays information for a specific ballot from the blockchain")
  .addParam("ballotid", "The ID of the ballot to check")
  .setAction(async (taskArgs, hre: HardhatRuntimeEnvironment) => {
    const { ballotid } = taskArgs;
    const { ethers } = hre;

    // --- Automatically read the deployed contract address ---
    const chainId = (await ethers.provider.getNetwork()).chainId.toString();
    const deploymentPath = path.join(
      __dirname,
      `../ignition/deployments/chain-${chainId}/deployed_addresses.json`
    );

    if (!fs.existsSync(deploymentPath)) {
      return;
    }

    const deployedAddresses = JSON.parse(fs.readFileSync(deploymentPath, "utf8"));
    const contractAddress = deployedAddresses["VotingModule#Voting"];

    if (!contractAddress) {
      return;
    }

    // --- Connect to the contract ---
    const votingContract = await ethers.getContractAt("Voting", contractAddress);

    // --- Check if ballot exists ---
    const nextBallotId = await votingContract.nextBallotId();
    if (BigInt(ballotid) >= nextBallotId) {
        return;
    }

    const ballotInfo = await votingContract.ballots(ballotid);

    if (ballotInfo.startTime === 0n) { // 0n is BigInt zero
        return;
    }

    // --- Get Proposal info ---
    if (ballotInfo.proposalCount > 0) {
      for (let i = 0; i < ballotInfo.proposalCount; i++) {
        const proposal = await votingContract.proposalsByBallot(ballotid, i);
      }
    } else {
      console.log("📋 No proposals found for this ballot.");
    }
  });