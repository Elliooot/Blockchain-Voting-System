const hre = require("hardhat");

async function main() {
    const Voting = await hre.ethers.getContractFactory("Voting");

    const startTime = Math.floor(Date.now() / 1000) + 3600; // Start at 1 hour later
    const duration = 86400; // Last for 1 day
    const voting = await Voting.deploy(startTime, duration);

    await voting.waitForDeployment();

    console.log("Voting contract deployed to: " + await voting.getAddress());
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    }
);