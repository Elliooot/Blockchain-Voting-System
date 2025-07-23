// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

contract Voting {
    struct Voter {
        bool isRegistered;
        bool hasVoted;
        uint256 vote;
        bytes32 voteHash;
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
        string result;
        address admin;
        mapping(address => Voter) voters;
        mapping(uint256 => Proposal) proposals;
        uint256 proposalCount;
        mapping(bytes32 => bool) commitments;
    }
    
    
    mapping(uint256 => Ballot) public ballots;
    uint256 public nextBallotId;

    event BallotCreated(uint256 ballotId, string title);
    event VoteRecorded(address indexed voter, uint256 ballotId, bytes32 commitment);

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

    constructor(string memory _title, uint256 _startTime, uint256 _duration) {
        nextBallotId = 0;

        Ballot storage newBallot = ballots[nextBallotId];

        newBallot.ballotId = nextBallotId;
        newBallot.title = _title;
        newBallot.startTime = _startTime;
        newBallot.duration = _duration;
        newBallot.terminated = false;
        newBallot.result = "";
        newBallot.admin = msg.sender;
        newBallot.proposalCount = 0;
        newBallot.commitments[keccak256(abi.encodePacked("dummy"))] = true; // Initialize with a dummy commitment

        emit BallotCreated(nextBallotId, _title);
    }

    function createBallot(string memory _title, uint256 _startTime, uint256 _duration) public {
        uint256 ballotId = nextBallotId++;

        Ballot storage newBallot = ballots[ballotId];
        newBallot.ballotId = ballotId;
        newBallot.title = _title;
        newBallot.startTime = _startTime;
        newBallot.duration = _duration;
        newBallot.terminated = false;
        newBallot.result = "";
        newBallot.admin = msg.sender;
        newBallot.proposalCount = 0;

        emit BallotCreated(ballotId, _title);
    }

    function addProposal(uint256 _ballotId, string memory _name) public onlyAdmin(_ballotId) onlyBeforeVoting(_ballotId) {
        Ballot storage ballot = ballots[_ballotId];

        uint256 newProposalId = ballots[_ballotId].proposalCount;

        ballots[_ballotId].proposals[newProposalId] = Proposal({
            index: newProposalId,
            name: _name,
            voteCount: 0
        });
        
        ballot.proposalCount++;
    }

    // function vote(uint256 _ballotId, uint256 _proposalId) public onlyDuringVoting(_ballotId) whenNotTerminated(_ballotId){
    //     require(ballots[_ballotId].voters[msg.sender].isRegistered, "You must be registered to vote");
    //     require(!ballots[_ballotId].voters[msg.sender].hasVoted, "You have already voted");

    //     ballots[_ballotId].voters[msg.sender].hasVoted = true;
    //     ballots[_ballotId].voters[msg.sender].vote = _proposalId;
    //     ballots[_ballotId].proposals[_proposalId].voteCount += 1;

    //     emit VoteRecorded(msg.sender, _ballotId, _proposalId);
    // }

    function commitVote(uint256 _ballotId, bytes32 _commitment) external onlyDuringVoting(_ballotId) whenNotTerminated(_ballotId){
        require(ballots[_ballotId].voters[msg.sender].isRegistered, "You must be registered to vote");
        require(!ballots[_ballotId].voters[msg.sender].hasVoted, "You have already voted");
        require(!ballots[_ballotId].commitments[_commitment], "Commitment already exists");


        ballots[_ballotId].voters[msg.sender].hasVoted = true;
        ballots[_ballotId].voters[msg.sender].voteHash = _commitment;
        ballots[_ballotId].commitments[_commitment] = true;

        emit VoteRecorded(msg.sender, _ballotId, _commitment);
    }

    function revealVote(uint256 _ballotId, uint256 _proposalId, bytes32 _salt) public onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) {
        require(ballots[_ballotId].voters[msg.sender].hasVoted, "You have not committed a vote yet");
        require(ballots[_ballotId].voters[msg.sender].voteHash == keccak256(abi.encodePacked(_proposalId, _salt)), "Invalid vote reveal");
        bytes32 commitment = keccak256(abi.encodePacked(_proposalId, _salt));
        require(ballots[_ballotId].commitments[commitment], "Invalid commitment");

        ballots[_ballotId].voters[msg.sender].vote = _proposalId;
        ballots[_ballotId].proposals[_proposalId].voteCount += 1;
        ballots[_ballotId].commitments[commitment] = false; // Remove commitment after revealing
    }

    function getVoteCounts(uint256 _ballotId) public view onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) returns (uint256[] memory) {
        uint256 proposalCount = ballots[_ballotId].proposalCount;
        uint256[] memory counts = new uint256[](proposalCount);

        for(uint256 i = 0; i < proposalCount; i++) {
            counts[i] = ballots[_ballotId].proposals[i].voteCount;
        }

        return counts;
    }

    function finalizeResult(uint256 _ballotId) public onlyAdmin(_ballotId) onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId){
        uint256 maxVoteCount = 0;
        uint256 proposalCount = ballots[_ballotId].proposalCount;
        uint256[] memory maxVoteProposals = new uint256[](proposalCount);
        uint256 tieCount = 0;
        string memory result;
        for (uint256 i = 0; i < proposalCount; i++) {
            if(ballots[_ballotId].proposals[i].voteCount > maxVoteCount) {
                maxVoteCount = ballots[_ballotId].proposals[i].voteCount;
                maxVoteProposals[0] = i;
                tieCount = 1;
            } else if (ballots[_ballotId].proposals[i].voteCount == maxVoteCount) {
                maxVoteProposals[tieCount] = i;
                tieCount++;
            }
        }

        result = "Proposal(s) with the most votes: ";
        for (uint256 i = 0; i < tieCount; i++) {
            if (i > 0) {
                result = string(abi.encodePacked(result, ", "));
            }
            result = string(abi.encodePacked(result, ballots[_ballotId].proposals[maxVoteProposals[i]].name));
        }

        ballots[_ballotId].result = result;
    } 

    function getResult(uint256 _ballotId) public view onlyAfterVoting(_ballotId) whenNotTerminated(_ballotId) returns (string memory) {
        return ballots[_ballotId].result;
    }

    function terminateVoting(uint256 _ballotId) public onlyAdmin(_ballotId) {
        ballots[_ballotId].terminated = true;
    }

    function registerVoter(uint256 _ballotId, address voter) public onlyAdmin(_ballotId) onlyBeforeVoting(_ballotId) {
        require(!ballots[_ballotId].voters[voter].isRegistered, "Voter is already registered");

        ballots[_ballotId].voters[voter].isRegistered = true;
        ballots[_ballotId].voters[voter].hasVoted = false;
        ballots[_ballotId].voters[voter].vote = 0;
    }
    
    function getProposal(uint256 _ballotId, uint256 _proposalId) public view returns (Proposal memory) {
        return ballots[_ballotId].proposals[_proposalId];
    }

    function getVoter(uint256 _ballotId, address _voterAddress) public view returns(Voter memory) {
        return ballots[_ballotId].voters[_voterAddress];
    }

    function getCommitment(uint256 _ballotId, bytes32 _commitment) public view returns (bool) {
        return ballots[_ballotId].commitments[_commitment];
    }
}