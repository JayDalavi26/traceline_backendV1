package com.example.traceline_backend.service;

import com.traceline.contract.TraceabilityLedger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import jakarta.annotation.PostConstruct;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlockchainContractService {

    private final Web3j web3j;
    private final Credentials credentials;

    private TraceabilityLedger contract;
    private boolean isBlockchainAvailable = true;

    @Value("${blockchain.contract-address:}")
    private String contractAddress;

    @Value("${blockchain.gas-limit:6721975}")
    private long gasLimit;

    @Value("${blockchain.gas-price:20000000000}")
    private long gasPrice;

    public BlockchainContractService(Web3j web3j, Credentials credentials) {
        this.web3j = web3j;
        this.credentials = credentials;
    }

    @PostConstruct
    public void init() {
        try {
            // Check if Ganache is reachable
            log.info("🔗 Connecting to Ethereum node at: {}", web3j.ethAccounts().send().getAccounts().get(0));

            // Create custom gas provider (doesn't trigger ENS)
            ContractGasProvider gasProvider = new StaticGasProvider(
                    BigInteger.valueOf(gasPrice),
                    BigInteger.valueOf(gasLimit)
            );

            if (contractAddress == null || contractAddress.isEmpty() || contractAddress.isBlank()) {
                // Deploy new contract
                log.info("📦 No existing contract found. Deploying new TraceabilityLedger...");
                log.info("   This may take 10-15 seconds...");

                contract = TraceabilityLedger.deploy(
                        web3j, credentials, gasProvider
                ).send();

                contractAddress = contract.getContractAddress();
                log.info("✅ Contract deployed successfully!");
                log.info("📦 Contract Address: {}", contractAddress);
                log.warn("⚠️ IMPORTANT: Save this contract address in application.properties:");
                log.warn("   blockchain.contract-address={}", contractAddress);
            } else {
                // Load existing contract
                contract = TraceabilityLedger.load(
                        contractAddress, web3j, credentials, gasProvider
                );
                log.info("✅ Loaded existing contract at address: {}", contractAddress);
            }

            // Verify contract is alive
            BigInteger totalScans = contract.getTotalScans().send();
            log.info("📊 Contract is alive! Total scans recorded: {}", totalScans);
            isBlockchainAvailable = true;

        } catch (Exception e) {
            log.error("❌ Failed to initialize blockchain contract: {}", e.getMessage());
            log.error("   Make sure:");
            log.error("   1. Ganache is running at http://127.0.0.1:7545");
            log.error("   2. The private key in application.properties is from Ganache");
            log.error("   3. The contract address is correct (if loading existing)");
            log.warn("⚠️ Blockchain service will be DISABLED. Continuing without blockchain...");
            isBlockchainAvailable = false;
        }
    }

    /**
     * Record a scan on the blockchain
     * @return Transaction hash
     */
    public String recordScan(String partId, String stage, String operatorId, String operatorName) throws Exception {
        if (!isBlockchainAvailable || contract == null) {
            log.warn("⚠️ Blockchain not available. Skipping blockchain record for part: {}", partId);
            return "blockchain-disabled";
        }

        log.info("⛓️ Recording scan on blockchain: Part={}, Stage={}", partId, stage);

        try {
            // Send transaction to smart contract
            var transactionReceipt = contract.recordScan(
                    partId, stage, operatorId, operatorName
            ).send();

            // Get transaction hash (this is your proof!)
            String transactionHash = transactionReceipt.getTransactionHash();

            log.info("✅ Scan recorded on Ethereum!");
            log.info("   📦 Transaction Hash: {}", transactionHash);
            log.info("   🔗 View: http://localhost:7545/transaction/{}", transactionHash);

            return transactionHash;

        } catch (Exception e) {
            log.error("❌ Failed to record scan on blockchain: {}", e.getMessage());
            throw new Exception("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get scan details from blockchain by record ID
     */
    public ScanDetails getScanDetails(BigInteger recordId) throws Exception {
        if (!isBlockchainAvailable || contract == null) {
            throw new IllegalStateException("Blockchain not available");
        }

        var result = contract.getScan(recordId).send();
        return new ScanDetails(
                result.component1(),  // partId
                result.component2(),  // stage
                result.component3(),  // operatorId
                result.component4(),  // operatorName
                result.component5(),  // timestamp
                result.component6()   // scanner
        );
    }

    /**
     * Get total number of scans on blockchain
     */
    public BigInteger getTotalScans() throws Exception {
        if (!isBlockchainAvailable || contract == null) {
            return BigInteger.ZERO;
        }
        return contract.getTotalScans().send();
    }

    /**
     * Check if blockchain is available
     */
    public boolean isBlockchainAvailable() {
        return isBlockchainAvailable && contract != null;
    }

    // Inner class for returning scan details
    public record ScanDetails(String partId, String stage, String operatorId,
                              String operatorName, BigInteger timestamp, String scanner) {}
}