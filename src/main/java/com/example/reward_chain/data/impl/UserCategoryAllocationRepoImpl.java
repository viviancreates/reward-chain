package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.UserCategoryAllocationRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.UserCategoryAllocation;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Profile("!mock")
public class UserCategoryAllocationRepoImpl implements UserCategoryAllocationRepo {

}
