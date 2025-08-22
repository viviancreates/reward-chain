package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.UserCategoryAllocationRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.UserCategoryAllocation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@Profile("mock")
@SuppressWarnings("unused")
public class UserCategoryAllocationRepoMock implements UserCategoryAllocationRepo {

}
