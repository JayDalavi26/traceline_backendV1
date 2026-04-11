// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * TraceabilityLedger - Smart Contract for Metal Parts Tracking
 *
 * This contract stores immutable scan records on the Ethereum blockchain.
 * Each scan creates a permanent, unalterable record of a part's journey.
 */
contract TraceabilityLedger {

    // Struct to represent a single scan event
    struct ScanRecord {
        string partId;      // DMC code of the metal part
        string stage;       // Manufacturing stage (Cutting, Drilling, etc.)
        string operatorId;  // ID of the operator who performed the scan
        string operatorName;// Name of the operator
        uint256 timestamp;  // Unix timestamp when scan occurred
        address scanner;    // Ethereum address that initiated the scan
    }

    // Array to store all scan records (immutable history)
    ScanRecord[] private records;

    // Mapping for quick lookup by partId
    mapping(string => uint256[]) private partToRecordIndices;

    // Events for off-chain listening
    event ScanRecorded(
        uint256 indexed recordId,
        string partId,
        string stage,
        string operatorId,
        uint256 timestamp,
        address scanner
    );

    event PartComplete(string partId, uint256 timestamp);

    /**
     * Record a new scan in the blockchain
     * @param partId - Unique identifier of the metal part
     * @param stage - Current manufacturing stage
     * @param operatorId - Operator's ID
     * @param operatorName - Operator's full name
     */
    function recordScan(
        string memory partId,
        string memory stage,
        string memory operatorId,
        string memory operatorName
    ) public returns (uint256) {

        // Create new record
        ScanRecord memory newRecord = ScanRecord({
            partId: partId,
            stage: stage,
            operatorId: operatorId,
            operatorName: operatorName,
            timestamp: block.timestamp,
            scanner: msg.sender
        });

        // Store in array
        uint256 recordId = records.length;
        records.push(newRecord);

        // Add to mapping for quick lookup
        partToRecordIndices[partId].push(recordId);

        // Emit event for real-time tracking
        emit ScanRecorded(
            recordId,
            partId,
            stage,
            operatorId,
            block.timestamp,
            msg.sender
        );

        return recordId;
    }

    /**
     * Get total number of recorded scans
     */
    function getTotalScans() public view returns (uint256) {
        return records.length;
    }

    /**
     * Get scan record by ID
     */
    function getScan(uint256 recordId) public view returns (
        string memory partId,
        string memory stage,
        string memory operatorId,
        string memory operatorName,
        uint256 timestamp,
        address scanner
    ) {
        require(recordId < records.length, "Record does not exist");
        ScanRecord memory record = records[recordId];
        return (
            record.partId,
            record.stage,
            record.operatorId,
            record.operatorName,
            record.timestamp,
            record.scanner
        );
    }

    /**
     * Get all scan records for a specific part
     */
    function getPartHistory(string memory partId) public view returns (uint256[] memory) {
        return partToRecordIndices[partId];
    }
}