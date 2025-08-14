-- Optional sanity
SELECT DATABASE();
USE `RewardChainTest`;

-- Clean up old versions
DROP PROCEDURE IF EXISTS set_known_good_state;
DROP PROCEDURE IF EXISTS reset_db;

DELIMITER //

CREATE PROCEDURE reset_db()
BEGIN
  SET FOREIGN_KEY_CHECKS = 0;

  -- Wipe tables (order matters if you add FKs later)
  TRUNCATE TABLE `Rewards`;
  TRUNCATE TABLE `Transaction`;
  TRUNCATE TABLE `Allocations`;
  TRUNCATE TABLE `Wallet`;
  TRUNCATE TABLE `Category`;
  TRUNCATE TABLE `User`;

  -- Reset PKs
  ALTER TABLE `Rewards`     AUTO_INCREMENT = 1;
  ALTER TABLE `Transaction` AUTO_INCREMENT = 1;
  ALTER TABLE `Allocations` AUTO_INCREMENT = 1;
  ALTER TABLE `Wallet`      AUTO_INCREMENT = 1;
  ALTER TABLE `Category`    AUTO_INCREMENT = 1;
  ALTER TABLE `User`        AUTO_INCREMENT = 1;

  -- Users (8)
  INSERT INTO `User` (`FirstName`,`LastName`,`Email`,`Password`) VALUES
    ('Alice','Admin','alice.admin@example.com','pass'),
    ('Bob','Buyer','bob.buyer@example.com','pass'),
    ('Carol','Cook','carol.cook@example.com','pass'),
    ('Dave','Driver','dave.driver@example.com','pass'),
    ('Eve','Engineer','eve.eng@example.com','pass'),
    ('Frank','Finance','frank.fin@example.com','pass'),
    ('Grace','Guest','grace.g@example.com','pass'),
    ('Heidi','Hopper','heidi.h@example.com','pass');

  -- Categories (6)
  -- IDs will be: 1 Groceries, 2 Dining, 3 Gas, 4 Travel, 5 Electronics, 6 Online Services
  INSERT INTO `Category` (`CategoryName`,`RewardPercentage`) VALUES
    ('Groceries',        0.03),
    ('Dining',           0.05),
    ('Gas',              0.02),
    ('Travel',           0.02),
    ('Electronics',      0.01),
    ('Online Services',  0.04);

  -- Wallets (one per user)
  INSERT INTO `Wallet` (`UserID`,`WalletAddress`,`Network`) VALUES
    (1,'0xALICE_WALLET','ETH_MAINNET'),
    (2,'0xBOB_WALLET','ETH_MAINNET'),
    (3,'0xCAROL_WALLET','ETH_MAINNET'),
    (4,'0xDAVE_WALLET','ETH_MAINNET'),
    (5,'0xEVE_WALLET','ETH_MAINNET'),
    (6,'0xFRANK_WALLET','ETH_MAINNET'),
    (7,'0xGRACE_WALLET','ETH_MAINNET'),
    (8,'0xHEIDI_WALLET','ETH_MAINNET');

  -- Allocations (ETH vs USDC)
  INSERT INTO `Allocations` (`UserID`,`EthPercent`,`UsdcPercent`) VALUES
    (1,0.70,0.30),
    (2,0.60,0.40),
    (3,0.50,0.50),
    (4,0.80,0.20),
    (5,0.40,0.60),
    (6,0.65,0.35),
    (7,0.55,0.45),
    (8,0.75,0.25);

  -- Transactions (18) across categories & users
  -- CategoryIDs reference insert order above
  INSERT INTO `Transaction` (`UserID`,`CategoryID`,`Merchant`,`Amount`,`TransactionDate`) VALUES
    (2,1,'Fresh Market',      42.50, NOW() - INTERVAL 10 DAY),
    (1,2,'Cafe Corner',       18.25, NOW() - INTERVAL 9 DAY),
    (3,3,'Gas-N-Go',          65.00, NOW() - INTERVAL 8 DAY),
    (4,4,'Flightly',         420.00, NOW() - INTERVAL 7 DAY),
    (5,5,'ElectroHub',       199.99, NOW() - INTERVAL 6 DAY),
    (6,6,'StreamBox',          9.99, NOW() - INTERVAL 5 DAY),
    (7,2,'Taco Town',         27.30, NOW() - INTERVAL 4 DAY),
    (8,1,'Green Grocer',      54.10, NOW() - INTERVAL 4 DAY),
    (1,3,'Fuel Center',       52.00, NOW() - INTERVAL 3 DAY),
    (2,6,'Cloudy SaaS',       25.00, NOW() - INTERVAL 3 DAY),
    (3,1,'Neighborhood Mart', 33.75, NOW() - INTERVAL 2 DAY),
    (4,2,'Burger Barn',       16.80, NOW() - INTERVAL 2 DAY),
    (5,4,'RoadRail',         120.00, NOW() - INTERVAL 2 DAY),
    (6,5,'Tech Plaza',       349.00, NOW() - INTERVAL 1 DAY),
    (7,6,'MusicSub',           7.99, NOW() - INTERVAL 1 DAY),
    (8,3,'Pump-It',           47.20, NOW() - INTERVAL 1 DAY),
    (1,1,'Family Foods',      23.40, NOW() - INTERVAL 12 HOUR),
    (2,2,'Sushi Spot',        58.60, NOW() - INTERVAL 6 HOUR);

  -- Rewards: compute from transactions + category % (all in ETH @ $4000)
  INSERT INTO `Rewards`
    (`TransactionID`,`UserID`,`CoinType`,`RewardPercentage`,
     `RewardAmountUsd`,`RewardAmountCrypto`,`CoinPriceUsd`,
     `WalletAddress`,`TransactionHash`,`Status`,`CreatedDate`)
  SELECT
    t.TransactionID,
    t.UserID,
    'ETH' AS CoinType,
    c.RewardPercentage,
    ROUND(t.Amount * c.RewardPercentage, 4) AS RewardAmountUsd,
    ROUND((t.Amount * c.RewardPercentage) / 4000.00, 8) AS RewardAmountCrypto,
    4000.00 AS CoinPriceUsd,
    w.WalletAddress,
    CONCAT('0xTX', LPAD(t.TransactionID, 6, '0')) AS TransactionHash,
    'PENDING' AS Status,
    t.TransactionDate + INTERVAL 1 HOUR AS CreatedDate
  FROM `Transaction` t
  JOIN `Category` c ON c.CategoryID = t.CategoryID
  JOIN `Wallet`   w ON w.UserID     = t.UserID;

  SET FOREIGN_KEY_CHECKS = 1;
END //

-- Optional compatibility wrapper (so older calls still work)
CREATE PROCEDURE set_known_good_state()
BEGIN
  CALL reset_db();
END //

DELIMITER ;

-- Manual check:
-- CALL reset_db();
-- SELECT COUNT(*) Users     FROM `User`;
-- SELECT COUNT(*) Tx        FROM `Transaction`;
-- SELECT COUNT(*) Rewards   FROM `Rewards`;
