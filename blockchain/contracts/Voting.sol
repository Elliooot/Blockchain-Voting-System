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
        address admin;
        mapping(address => Voter) voters;
        mapping(uint256 => Proposal) proposals;
        uint256 proposalCount;
    }
    
    // address public admin;
    
    // mapping(uint256 => Proposal[]) public electionProposals;
    mapping(uint256 => Election) public elections;
    uint256 public nextElectionId;
    // bool public terminated = false;

    event ElectionCreated(uint256 electionId, string title);
    event VoteRecorded(address indexed voter, uint256 electionId, uint256 proposalIndex);

    modifier onlyAdmin(uint256 _electionId) {
        require(msg.sender == elections[_electionId].admin, "Only admin can perform this action");
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

    constructor(string memory _title, uint256 _startTime, uint256 _duration) {
        nextElectionId = 1;

        Election storage newElection = elections[nextElectionId];

        newElection.electionId = nextElectionId;
        newElection.title = _title;
        newElection.startTime = _startTime;
        newElection.duration = _duration;
        newElection.terminated = false;
        newElection.result = "";
        newElection.admin = msg.sender;
        newElection.proposalCount = 0;

        // elections[nextElectionId] = Election({
        //     electionId: nextElectionId,
        //     title: _title,
        //     startTime: _startTime,
        //     duration: _duration,
        //     terminated: false,
        //     result: ""
        //     // voters: mapping(address => Voter)()
        // });

        emit ElectionCreated(nextElectionId, _title);
    }

    function createElection(string memory _title, uint256 _startTime, uint256 _duration) public {
        uint256 electionId = nextElectionId++;

        Election storage newElection = elections[electionId];
        newElection.electionId = electionId;
        newElection.title = _title;
        newElection.startTime = _startTime;
        newElection.duration = _duration;
        newElection.terminated = false;
        newElection.result = "";
        newElection.admin = msg.sender;
        newElection.proposalCount = 0;

        emit ElectionCreated(electionId, _title);
    }

    function addProposal(uint256 _electionId, string memory _name) public onlyAdmin(_electionId) onlyBeforeVoting(_electionId) {
        Election storage election = elections[_electionId];

        uint256 newProposalId = elections[_electionId].proposalCount;

        elections[_electionId].proposals[newProposalId] = Proposal({
            index: newProposalId,
            name: _name,
            voteCount: 0
        });
        
        election.proposalCount++;
    }

    function vote(uint256 _electionId, uint256 _proposalId) public onlyDuringVoting(_electionId) whenNotTerminated(_electionId){
        require(elections[_electionId].voters[msg.sender].isRegistered, "You must be registered to vote");
        require(!elections[_electionId].voters[msg.sender].hasVoted, "You have already voted");

        elections[_electionId].voters[msg.sender].hasVoted = true;
        elections[_electionId].voters[msg.sender].vote = _proposalId;
        elections[_electionId].proposals[_proposalId].voteCount += 1;

        emit VoteRecorded(msg.sender, _electionId, _proposalId);
    }

    function getVoteCounts(uint256 _electionId) public view onlyAfterVoting(_electionId) whenNotTerminated(_electionId) returns (uint256[] memory) {
        uint256 proposalCount = elections[_electionId].proposalCount;
        uint256[] memory counts = new uint256[](proposalCount);

        for(uint256 i = 0; i < proposalCount; i++) {
            counts[i] = elections[_electionId].proposals[i].voteCount;
        }

        return counts;
    }

    function finalizeResult(uint256 _electionId) public onlyAdmin(_electionId) onlyAfterVoting(_electionId) whenNotTerminated(_electionId){
        string memory result;
        uint256 maxVoteCount = 0;
        uint256 proposalCount = elections[_electionId].proposalCount;
        for (uint256 i = 0; i < proposalCount; i++) {
            if(elections[_electionId].proposals[i].voteCount > maxVoteCount) {
                maxVoteCount = elections[_electionId].proposals[i].voteCount;
                result = elections[_electionId].proposals[i].name;
            }
        }
        elections[_electionId].result = result;
    } 

    function getResult(uint256 _electionId) public view onlyAfterVoting(_electionId) whenNotTerminated(_electionId) returns (string memory) {
        return elections[_electionId].result;
    }

    function terminateVoting(uint256 _electionId) public onlyAdmin(_electionId) {
        elections[_electionId].terminated = true;
    }

    function registerVoter(uint256 _electionId, address voter) public onlyAdmin(_electionId) onlyBeforeVoting(_electionId) {
        require(!elections[_electionId].voters[voter].isRegistered, "Voter is already registered");

        elections[_electionId].voters[voter].isRegistered = true;
        elections[_electionId].voters[voter].hasVoted = false;
        elections[_electionId].voters[voter].vote = 0;
    }
    
    function getProposal(uint256 _electionId, uint256 _proposalId) public view returns (Proposal memory) {
        return elections[_electionId].proposals[_proposalId];
    }

    function getVoter(uint256 _electionId, address _voterAddress) public view returns(Voter memory) {
        return elections[_electionId].voters[_voterAddress];
    }
}