## Service Layer & Service Interfaces

### Service Layer
- Holds the **business logic** of the application.
- Calls repositories and external APIs to perform operations.
- Example: Create wallet -> verify user exists -> save in DB -> call API.

### Service Interfaces
- Define the **contract** for what a service does.
- Allow **multiple implementations**:
    - Real (production)
    - Simulated/fake (testing, dev)
- Other layers (controllers, etc.) depend on the **interface**, not the implementation.



Benefits
- Separation of concerns
- Easier testing (mock/fake services)
- Flexible for future changes

---
## How Wallet Components Work Together

1. `WalletRepo` (Repository Interface)
- Defines **data access methods** for the `Wallet` table.
- Example methods:
  ```
  public interface WalletRepo {
      Wallet getWalletById(int walletId);
      List<Wallet> getWalletsByUserId(int userId);
      Wallet addWallet(Wallet wallet);
  }
  ```
- No logic inside -> just the contract for data access.

2. WalletRepoImpl (Repository Implementation)
- Contains the actual SQL queries using JdbcTemplate.
- Calls the WalletMapper to turn database rows into Wallet objects.

Example:
```
@Repository
public class WalletRepoImpl implements WalletRepo {
@Autowired
private JdbcTemplate jdbcTemplate;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Wallet getWalletById(int walletId) {
        final String sql = "SELECT * FROM Wallet WHERE WalletID = ?";
        return jdbcTemplate.queryForObject(sql, walletMapper, walletId);
    }
}
```

3. WalletMapper (RowMapper)
- Converts a ResultSet row -> Wallet object.
- Used only by the repository layer to map SQL results.

Example:
```
@Component
public class WalletMapper implements RowMapper<Wallet> {
@Override
public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
    Wallet wallet = new Wallet();
    wallet.setWalletId(rs.getInt("WalletID"));
    wallet.setUserId(rs.getInt("UserID"));
    wallet.setWalletAddress(rs.getString("WalletAddress"));
    wallet.setNetwork(rs.getString("Network"));
    return wallet;
    }
}
```

4. WalletService (Service Interface)
- Defines business logic operations involving wallets.
- Uses the repository to read/write the database, but may also:
- Validate inputs
- Call external APIs
- Combine data from multiple tables

Example:
```
public interface WalletService {
    Wallet createWalletForUser(int userId, String network);
    Wallet getUserWallet(int userId);
}
```
In short:
- Repo Interface -> defines DB operations
- Repo Impl -> runs SQL & maps results
- Mapper -> turns SQL rows into objects
- Service Interface -> defines higher-level operations that use repos and other services


---
TO DO
- kept password from showing up, but saves to database, learn how to hash
- refactor controllers with dto if time
- for now adding minimal crud to controllers, add deletes and updates later or when necessary for time
- add more graceful error messages for the controller -> choose a layer? learn about that or is it int he controller?



---

### RUN in Mock Mode (no DB)
./mvnw spring-boot:run -Dspring-boot.run.profiles=mock

### RUN real impl
./mvnw spring-boot:run
curl -s http://localhost:8080/actuator/beans | grep -i repomock || echo "No mocks found ✅"
