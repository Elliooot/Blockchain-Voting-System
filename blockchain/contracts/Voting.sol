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
    struct Election {
        uint256 electionId;
        string title;
        uint256 startTime;
        uint256 duration;
        bool terminated;
        string result;
    }
    
    address public admin;
    mapping(address => Voter) public voters;
    mapping(uint256 => Proposal[]) public electionProposals;
    mapping(uint256 => Election) public elections;
    // Proposal[] public proposals;
    // Proposal public proposal;
    uint256 public nextElectionId;
    bool public terminated = false;

    event ElectionCreated(uint256 electionId, string title);
    event VoteRecorded(address indexed voter, uint256 electionId, uint256 proposalIndex);

    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }

    modifier onlyBeforeVoting(uint256 _electionId) {
        Election storage election = elections[_electionId];
        require(block.timestamp < election.startTime, "Voting has already started");
        _;
    }

    modifier onlyDuringVoting(uint256 _electionId) {
        Election storage election = elections[_electionId];
        require(block.timestamp >= election.startTime && block.timestamp < election.startTime + election.duration, "Voting is not active");
        _;
    }

    modifier onlyAfterVoting(uint256 _electionId) {
        Election storage election = elections[_electionId];
        require(block.timestamp >= election.startTime + election.duration, "Voting period has not ended yet");
        _;
    }

    modifier whenNotTerminated(uint256 _electionId) {
        require(!elections[_electionId].terminated, "Voting is terminated");
        _;
    }

    constructor() {
        admin = msg.sender;
        nextElectionId = 1;
    }

    function createElection(string memory _title, uint256 _startTime, uint256 _duration) public onlyAdmin() {
        uint256 electionId = nextElectionId++;

        elections[electionId] = Election({
            electionId: electionId,
            title: _title,
            startTime: _startTime,
            duration: _duration,
            result: ""
        });

        emit ElectionCreated(electionId, _title);
    }

    function addProposal(uint256 _electionId, string memory _name) public onlyAdmin() onlyBeforeVoting(_electionId) {
        electionProposals[_electionId].push(Proposal({
            index: electionProposals[_electionId].length,
            name: _name,
            voteCount: 0
        }));
    }

    function vote(uint256 _electionId, uint256 _proposalIndex) public onlyDuringVoting(_electionId) whenNotTerminated(){
        require(voters[msg.sender].isRegistered, "You must be registered to vote");
        require(!voters[msg.sender].hasVoted, "You have already voted");

        voters[msg.sender].hasVoted = true;
        voters[msg.sender].vote = proposalIndex;
        electionProposals[_electionId][_proposalIndex].voteCount += 1;

        emit VoteRecorded(msg.sender, _electionId, _proposalIndex);
    }

    function getVoteCounts(uint256 _electionId) public view onlyAfterVoting(_electionId) whenNotTerminated() returns (uint256[] memory) {
        uint256[] memory counts = new uint256[](electionProposals[_electionId].length);

        for(uint256 i = 0; i < electionProposals[_electionId].length; i++) {
            counts[i] = electionProposals[_electionId][i].voteCount;
        }

        return counts;
    }

    function finalizeResult(uint256 _electionId) public onlyAdmin() onlyAfterVoting(_electionId) whenNotTerminated(){
        string memory result;
        uint256 maxVoteCount = 0;
        for (uint256 i = 0; i < electionProposals[_electionId].length; i++) {
            if(electionProposals[_electionId][i].voteCount > maxVoteCount) {
                maxVoteCount = electionProposals[_electionId][i].voteCount;
                result = electionProposals[_electionId][i].name;
            }
        }
        elections[_electionId].result = result;
    } 

    function getResult() public view onlyAfterVoting() whenNotTerminated() returns (string memory) {
        return elections[_electionId].result;
    }

    function terminateVoting(uint256 _electionId) public onlyAdmin() {
        elections[_electionId].terminated = true;
    }

    function registerVoter(address voter) public onlyAdmin() onlyBeforeVoting() {
        require(!voters[voter].isRegistered, "Voter is already registered");

        voters[voter].isRegistered = true;
        voters[voter].hasVoted = false;
        voters[voter].vote = 0;
    }
}