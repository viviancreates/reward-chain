package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.AllocationsRepo;
import com.example.reward_chain.model.Allocations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AllocationRepoIT extends BaseDbTest {

    @Autowired AllocationsRepo allocationsRepo;

    @Test
    @DisplayName("seed: Alice ETH% = 0.70")
    void seed_allocation_for_alice() throws Exception {
        BigDecimal expected = new BigDecimal("0.70");
        Allocations a = allocationsRepo.getAllocationByUserId(1);
        BigDecimal actual = a.getEthPercent();
        assertEquals(0, expected.compareTo(actual),
                "ETH%: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("update changes ETH%")
    void update_changes_ethPercent() throws Exception {
        Allocations a = allocationsRepo.getAllocationByUserId(1);
        BigDecimal expected = new BigDecimal("0.75");
        a.setEthPercent(expected);
        allocationsRepo.updateAllocation(a);

        BigDecimal actual = allocationsRepo.getAllocationById(a.getAllocationId()).getEthPercent();
        assertEquals(0, expected.compareTo(actual),
                "ETH%: expected=" + expected + ", actual=" + actual);
    }
}
