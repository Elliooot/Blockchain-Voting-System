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
        uint256 voteCount;
    }
    struct Ballot {
        uint256 ballotId;
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

    event BallotCreated(uint256 ballotId); // For Spring Boot to get the ballot ID
    event ProposalCreated(uint256 ballotId, uint256 proposalId); // For Spring Boot to get the proposal ID
    event VoteRecorded(address indexed voter, uint256 ballotId, uint256 proposalId); // For Spring Boot to get the vote details
    event BallotResultFinalized(uint256 ballotId, uint256[] resultProposalIds); // For Spring Boot to get the final result

    // Because I use proxy mode (using contract deployer to do every action), so cannot use this
    // modifier onlyAdmin(uint256 _ballotId) {
    //     require(msg.sender == ballots[_ballotId].admin, "Only admin can perform this action");
    //     _;
    // }

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

    function createBallot(uint256 _startTime, uint256 _duration, uint256 _proposalCount, address[] memory _voters) public onlyContractAdmin(){
        require(_startTime > block.timestamp, "Start time must be in the future");
        require(_proposalCount > 1, "Must have at least two proposals");
        require(_voters.length > 0, "Must have at least one voter");

        uint256 ballotId = nextBallotId++;

        Ballot storage newBallot = ballots[ballotId];
        newBallot.ballotId = ballotId;
        newBallot.startTime = _startTime;
        newBallot.duration = _duration;
        newBallot.terminated = false;
        newBallot.resultProposalIds = new uint256[](_proposalCount);
        newBallot.admin = msg.sender;
        newBallot.proposalCount = _proposalCount;

        for(uint256 i = 0; i < _proposalCount; i++) {
            proposalsByBallot[ballotId][i] = Proposal({
                index: i,
                voteCount: 0
            });
            emit ProposalCreated(ballotId, i);
        }

        for(uint256 i = 0; i < _voters.length; i++) {
            address voterAddress = _voters[i];
            require(voterAddress != address(0), "Invalid voter address");
            require(!votersByBallot[ballotId][voterAddress].isRegistered, "Voter is already registered");
            votersByBallot[ballotId][voterAddress].isRegistered = true;
        }

        emit BallotCreated(ballotId);
    }

    function updateStartTime(uint256 _ballotId, uint256 _newStartTime) public onlyContractAdmin() onlyBeforeVoting(_ballotId) {
        require(_newStartTime > block.timestamp, "Start time must be in the future");
        ballots[_ballotId].startTime = _newStartTime;
    }

    function updateDuration(uint256 _ballotId, uint256 _newDuration) public onlyContractAdmin() onlyBeforeVoting(_ballotId) {
        ballots[_ballotId].duration = _newDuration;
    }

    function registerVoter(uint256 _ballotId, address voter) public onlyContractAdmin() onlyBeforeVoting(_ballotId) {
        require(!votersByBallot[_ballotId][voter].isRegistered, "Voter is already registered");

        votersByBallot[_ballotId][voter].isRegistered = true;
    }

    function unregisterVoter(uint256 _ballotId, address voter) public onlyContractAdmin() onlyBeforeVoting(_ballotId) {
        require(votersByBallot[_ballotId][voter].isRegistered, "Voter is not registered");

        votersByBallot[_ballotId][voter].isRegistered = false;
    }

    function vote(uint256 _ballotId, uint256 _proposalId, address _voterAddress) public whenNotTerminated(_ballotId) onlyContractAdmin {
        require(votersByBallot[_ballotId][_voterAddress].isRegistered, "Voter is not registered");
        require(!votersByBallot[_ballotId][_voterAddress].hasVoted, "Voter has already voted");

        votersByBallot[_ballotId][_voterAddress].hasVoted = true;
        votersByBallot[_ballotId][_voterAddress].vote = _proposalId;
        proposalsByBallot[_ballotId][_proposalId].voteCount += 1;

        emit VoteRecorded(_voterAddress, _ballotId, _proposalId);
    }

    function getVoteCounts(uint256 _ballotId) public view onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) returns (uint256[] memory) {
        uint256 proposalCount = ballots[_ballotId].proposalCount;
        uint256[] memory counts = new uint256[](proposalCount);

        for(uint256 i = 0; i < proposalCount; i++) {
            counts[i] = proposalsByBallot[_ballotId][i].voteCount;
        }

        return counts;
    }

    function finalizeResult(uint256 _ballotId) public onlyContractAdmin() onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId){
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

    function terminateVoting(uint256 _ballotId) public onlyContractAdmin() {
        ballots[_ballotId].terminated = true;
    }
    
    function getProposal(uint256 _ballotId, uint256 _proposalId) public view returns (Proposal memory) {
        return proposalsByBallot[_ballotId][_proposalId];
    }

    function getVoter(uint256 _ballotId, address _voterAddress) public view returns(Voter memory) {
        return votersByBallot[_ballotId][_voterAddress];
    }
}