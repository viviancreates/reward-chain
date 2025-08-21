package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.AllocationsRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Allocations;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Repository
@Profile("mock")
public class AllocationsRepoMock implements AllocationsRepo {
    private final Map<Integer, Allocations> allocations = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public AllocationsRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        addAllocation(new Allocations(1, new BigDecimal("0.70"), new BigDecimal("0.30")));
        addAllocation(new Allocations(2, new BigDecimal("0.50"), new BigDecimal("0.50")));
    }

    @Override
    public Allocations getAllocationById(int id) throws RecordNotFoundException {
        Allocations a = allocations.get(id);
        if (a == null) throw new RecordNotFoundException("Allocation not found");
        return a;
    }

    @Override
    public Allocations getAllocationByUserId(int userId) throws RecordNotFoundException {
        return allocations.values().stream()
                .filter(a -> a.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Allocation not found for user"));
    }

    @Override
    public List<Allocations> getAllAllocations() { return new ArrayList<>(allocations.values()); }

    @Override
    public Allocations addAllocation(Allocations allocation) {
        allocation.setAllocationId(idCounter.getAndIncrement());
        allocations.put(allocation.getAllocationId(), allocation);
        return allocation;
    }

    @Override
    public void updateAllocation(Allocations allocation) {
        allocations.put(allocation.getAllocationId(), allocation);
    }

    @Override
    public Allocations deleteAllocation(int id) { return allocations.remove(id); }
}
