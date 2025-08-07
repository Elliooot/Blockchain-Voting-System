// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

contract Voting {
    struct Voter {
        bool isRegistered;
        bool hasVoted;
        uint256 vote;
    }
    struct Proposal {
        uint256 index;
        string name;
        uint256 voteCount;
    }
    struct Ballot {
        uint256 ballotId;
        string title;
        uint256 startTime;
        uint256 duration;
        bool terminated;
        uint256[] resultProposalIds;
        address admin;
        uint256 proposalCount;
    }
    
    
    mapping(uint256 => Ballot) public ballots;
    mapping(uint256 => mapping(address => Voter)) public votersByBallot;
    mapping(uint256 => mapping(uint256 => Proposal)) public proposalsByBallot;

    uint256 public nextBallotId = 1;

    event BallotCreated(uint256 ballotId, string title); // For Spring Boot to get the ballot ID
    event ProposalCreated(uint256 ballotId, uint256 proposalId, string name); // For Spring Boot to get the proposal ID
    event VoteRecorded(address indexed voter, uint256 ballotId, uint256 proposalId); // For Spring Boot to get the vote details
    event BallotResultFinalized(uint256 ballotId, uint256[] resultProposalIds); // For Spring Boot to get the final result

    modifier onlyAdmin(uint256 _ballotId) {
        require(msg.sender == ballots[_ballotId].admin, "Only admin can perform this action");
        _;
    }

    modifier onlyBeforeVoting(uint256 _ballotId) {
        Ballot storage ballot = ballots[_ballotId];
        require(block.timestamp < ballot.startTime, "Voting has already started");
        _;
    }

    modifier onlyDuringVoting(uint256 _ballotId) {
        Ballot storage ballot = ballots[_ballotId];
        require(block.timestamp >= ballot.startTime && block.timestamp < ballot.startTime + ballot.duration, "Voting is not active");
        _;
    }

    modifier onlyAfterVoting(uint256 _ballotId) {
        Ballot storage ballot = ballots[_ballotId];
        require(block.timestamp >= ballot.startTime + ballot.duration, "Voting period has not ended yet");
        _;
    }

    modifier whenNotTerminated(uint256 _ballotId) {
        require(!ballots[_ballotId].terminated, "Voting is terminated");
        _;
    }

    address public contractAdmin;

    modifier onlyContractAdmin() {
        require(msg.sender == contractAdmin, "Only contract admin can perform this action");
        _;
    }

    constructor() {
        // Set the contract deployer as the admin since he will also call the contract when voters vote
        contractAdmin = msg.sender; 
    }

    function createBallot(string memory _title, uint256 _startTime, uint256 _duration, string[] memory _proposalNames, address[] memory _voters) public {
        require(_startTime > block.timestamp, "Start time must be in the future");
        require(_proposalNames.length > 1, "Must have at least two proposals");
        require(_voters.length > 0, "Must have at least one voter");

        uint256 ballotId = nextBallotId++;

        Ballot storage newBallot = ballots[ballotId];
        newBallot.ballotId = ballotId;
        newBallot.title = _title;
        newBallot.startTime = _startTime;
        newBallot.duration = _duration;
        newBallot.terminated = false;
        newBallot.resultProposalIds = new uint256[](_proposalNames.length);
        newBallot.admin = msg.sender;
        newBallot.proposalCount = _proposalNames.length;

        for(uint256 i = 0; i < _proposalNames.length; i++) {
            proposalsByBallot[ballotId][i] = Proposal({
                index: i,
                name: _proposalNames[i],
                voteCount: 0
            });
            emit ProposalCreated(ballotId, i, _proposalNames[i]);
        }

        for(uint256 i = 0; i < _voters.length; i++) {
            address voterAddress = _voters[i];
            require(voterAddress != address(0), "Invalid voter address");
            require(!votersByBallot[ballotId][voterAddress].isRegistered, "Voter is already registered");
            votersByBallot[ballotId][voterAddress].isRegistered = true;
        }

        emit BallotCreated(ballotId, _title);
    }

    function addProposal(uint256 _ballotId, string memory _name) public onlyAdmin(_ballotId) onlyBeforeVoting(_ballotId) {
        Ballot storage ballot = ballots[_ballotId];
        uint256 newProposalId = ballot.proposalCount;

        proposalsByBallot[_ballotId][newProposalId] = Proposal({
            index: newProposalId,
            name: _name,
            voteCount: 0
        });
        
        ballot.proposalCount++;
        emit ProposalCreated(_ballotId, newProposalId, _name);
    }

    function vote(uint256 _ballotId, uint256 _proposalId, address _voterAddress) public whenNotTerminated(_ballotId) onlyContractAdmin {
        require(votersByBallot[_ballotId][_voterAddress].isRegistered, "Voter is not registered");
        require(!votersByBallot[_ballotId][_voterAddress].hasVoted, "Voter has already voted");

        votersByBallot[_ballotId][_voterAddress].hasVoted = true;
        votersByBallot[_ballotId][_voterAddress].vote = _proposalId;
        proposalsByBallot[_ballotId][_proposalId].voteCount += 1;

        emit VoteRecorded(_voterAddress, _ballotId, _proposalId);
    }

    // function commitVote(uint256 _ballotId, bytes32 _commitment) external onlyDuringVoting(_ballotId) whenNotTerminated(_ballotId){ // Commit a vote using a hash to achieve anonymous voting
    //     require(ballots[_ballotId].voters[msg.sender].isRegistered, "You must be registered to vote");
    //     require(!ballots[_ballotId].voters[msg.sender].hasVoted, "You have already voted");
    //     require(!ballots[_ballotId].commitments[_commitment], "Commitment already exists");


    //     ballots[_ballotId].voters[msg.sender].hasVoted = true;
    //     ballots[_ballotId].voters[msg.sender].voteHash = _commitment;
    //     ballots[_ballotId].commitments[_commitment] = true;

    //     emit VoteRecorded(msg.sender, _ballotId, _commitment);
    // }

    // function revealVote(uint256 _ballotId, uint256 _proposalId, bytes32 _salt) public onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) {
    //     require(ballots[_ballotId].voters[msg.sender].hasVoted, "You have not committed a vote yet");
    //     require(ballots[_ballotId].voters[msg.sender].voteHash == keccak256(abi.encodePacked(_proposalId, _salt)), "Invalid vote reveal");
    //     bytes32 commitment = keccak256(abi.encodePacked(_proposalId, _salt));
    //     require(ballots[_ballotId].commitments[commitment], "Invalid commitment");

    //     ballots[_ballotId].voters[msg.sender].vote = _proposalId;
    //     ballots[_ballotId].proposals[_proposalId].voteCount += 1;
    //     ballots[_ballotId].commitments[commitment] = false; // Remove commitment after revealing
    // }

    function getVoteCounts(uint256 _ballotId) public view onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) returns (uint256[] memory) {
        uint256 proposalCount = ballots[_ballotId].proposalCount;
        uint256[] memory counts = new uint256[](proposalCount);

        for(uint256 i = 0; i < proposalCount; i++) {
            counts[i] = proposalsByBallot[_ballotId][i].voteCount;
        }

        return counts;
    }

    function finalizeResult(uint256 _ballotId) public onlyAdmin(_ballotId) onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId){
        uint256 maxVoteCount = 0;
        uint256 proposalCount = ballots[_ballotId].proposalCount;

        for (uint256 i = 0; i < proposalCount; i++) {
            if(proposalsByBallot[_ballotId][i].voteCount > maxVoteCount) {
                maxVoteCount = proposalsByBallot[_ballotId][i].voteCount;
            } 
        }

        uint256[] memory winningProposalIds = new uint256[](proposalCount);
        uint256 tieCount = 0;

        for (uint256 i = 0; i < proposalCount; i++) {
            if (proposalsByBallot[_ballotId][i].voteCount == maxVoteCount) {
                winningProposalIds[tieCount] = i;
                tieCount++;
            }
        }

        uint256[] memory finalResultIds = new uint256[](tieCount);
        for (uint256 i = 0; i < tieCount; i++) {
            finalResultIds[i] = winningProposalIds[i];
        }

        ballots[_ballotId].resultProposalIds = finalResultIds;
        emit BallotResultFinalized(_ballotId, finalResultIds);
    } 

    function getResult(uint256 _ballotId) public view onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) returns (uint256[] memory) {
        return ballots[_ballotId].resultProposalIds;
    }

    function terminateVoting(uint256 _ballotId) public onlyAdmin(_ballotId) {
        ballots[_ballotId].terminated = true;
    }

    function registerVoter(uint256 _ballotId, address voter) public onlyAdmin(_ballotId) onlyBeforeVoting(_ballotId) {
        require(!votersByBallot[_ballotId][voter].isRegistered, "Voter is already registered");

        votersByBallot[_ballotId][voter].isRegistered = true;
        votersByBallot[_ballotId][voter].hasVoted = false;
        votersByBallot[_ballotId][voter].vote = 0;
    }
    
    function getProposal(uint256 _ballotId, uint256 _proposalId) public view returns (Proposal memory) {
        return proposalsByBallot[_ballotId][_proposalId];
    }

    function getVoter(uint256 _ballotId, address _voterAddress) public view returns(Voter memory) {
        return votersByBallot[_ballotId][_voterAddress];
    }
}