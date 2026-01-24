# Fix: Datasource Configuration Issue

## Problem

You're getting: `Error executing command: No JDBC datasource found for universityDS`

## Root Cause

The JDBC URL format was incorrect. You used:
```
jdbc:postgresql://postgres:WIF_3006_G2@db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres
```

**This is wrong!** The JDBC URL should NOT include username and password.

## Correct Configuration

### Step 1: Verify Pax JDBC is Installed

```karaf
feature:list | grep pax-jdbc
```

If not installed:
```karaf
feature:repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/1.5.0/xml/features
feature:install pax-jdbc-postgresql pax-jdbc-config
```

### Step 2: Fix the Datasource Configuration

```karaf
# Start fresh - delete the incorrect configuration
config:delete org.ops4j.datasource-university

# Create new configuration
config:edit org.ops4j.datasource-university

# Set PostgreSQL driver (MUST match exactly what's in DataSourceFactory service)
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"

# Set URL (ONLY host, port, database - NO username/password!)
config:property-set url jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require

# Set username separately
config:property-set user postgres

# Set password separately
config:property-set password WIF_3006_G2

# Set datasource name
config:property-set dataSourceName universityDS

# Save configuration
config:update
```

### Step 3: Verify

```karaf
# List datasources
jdbc:ds-list

# Test connection
jdbc:query universityDS "SELECT version()"
```

## Key Points

1. **JDBC URL format**: `jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require`
   - ✅ Correct: `jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require`
   - ❌ Wrong: `jdbc:postgresql://postgres:password@host:port/db`

2. **Username and password are separate properties**, not in the URL

3. **Always include `?sslmode=require`** for Supabase connections

## Real Issue: Driver Name Mismatch

The DataSourceFactory service exists, but the driver name in your config doesn't match!

**Your config has:** `osgi.jdbc.driver.name = PostgreSQL`  
**But the service has:** `osgi.jdbc.driver.name = PostgreSQL JDBC Driver`

Pax JDBC Config requires an **exact match** on the driver name property.

## THE FIX: Update Driver Name in Config

```karaf
# Edit the configuration
config:edit org.ops4j.datasource-university

# Change the driver name to match the actual service property
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"

# Save configuration
config:update
```

**Note:** The driver name must be in quotes because it contains spaces!

## Alternative: Use Driver Class Instead

If driver name matching still doesn't work, try using `osgi.jdbc.driver.class` instead:

```karaf
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.class org.postgresql.Driver
config:update
```

This matches the `osgi.jdbc.driver.class = org.postgresql.Driver` property from the DataSourceFactory service.

## Verify It Works

After updating, wait 3-5 seconds, then:

```karaf
# Check if DataSource service is now registered
service:list javax.sql.DataSource

# Check datasource list
jdbc:ds-list

# Test connection
jdbc:query universityDS "SELECT version()"
```

## Check Logs for Errors

If still not working, check logs:

```karaf
log:tail | findstr /i "pax-jdbc datasource error exception"
```

Look for messages about:
- "Detected config for DataSource"
- "No DataSourceFactory found"
- Connection errors

## Solution: Force Pax JDBC Config to Process Configuration

If the service isn't registered, force Pax JDBC Config to reload:

```karaf
# 1. Restart Pax JDBC Config bundle (bundle ID 84)
bundle:restart 84

# 2. Wait 3-5 seconds for processing

# 3. Check if DataSource service is now registered
service:list javax.sql.DataSource

# 4. Check datasource list
jdbc:ds-list
```

## If Still Not Working

### Check 1: Verify PostgreSQL Driver Bundle is Installed

```karaf
bundle:list | grep postgresql
```

If missing or not started, install:
```karaf
bundle:install mvn:org.postgresql/postgresql/42.7.1
bundle:start <bundle-id>
```

### Check 2: Verify Pax JDBC Config Service is Running

```karaf
bundle:list | grep pax-jdbc-config
service:list | grep DataSourceFactory
```

If pax-jdbc-config bundle is not Active, restart it:
```karaf
bundle:restart <pax-jdbc-config-bundle-id>
```

### Check 3: Verify Configuration File Location

The configuration should be saved in:
```
<KARAF_HOME>/etc/org.ops4j.datasource-university.cfg
```

Check if the file exists and has correct content:
```karaf
config:list | grep datasource
```

Or check the file directly (outside Karaf):
```bash
cat <KARAF_HOME>/etc/org.ops4j.datasource-university.cfg
```

Expected content:
```properties
osgi.jdbc.driver.name=PostgreSQL
url=jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require
user=postgres
password=WIF_3006_G2
dataSourceName=universityDS
```

### Check 4: Check Logs for Errors

```karaf
log:tail
```

Look for errors related to:
- Datasource creation
- PostgreSQL driver
- Connection failures

### Check 5: Verify DataSource Service Registration

The issue might be that the service is registered but with different properties. Check:

```karaf
# List all DataSource services
service:list javax.sql.DataSource

# Check service properties - look for osgi.jndi.service.name or dataSourceName
service:list javax.sql.DataSource | findstr /i "university"
```

If you see a DataSource service but `jdbc:ds-list` is empty, the service might be registered with different properties. Try querying by service properties:

```karaf
# Try to find the service by JNDI name
service:list "(osgi.jndi.service.name=universityDS)"
```

### Check 5: Alternative - Create Configuration File Manually

If `config:edit` isn't working, create the file manually:

1. **Stop Karaf** (if running)
2. **Create file**: `<KARAF_HOME>/etc/org.ops4j.datasource-university.cfg`
3. **Add content**:
   ```properties
   osgi.jdbc.driver.name=PostgreSQL
   url=jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require
   user=postgres
   password=WIF_3006_G2
   dataSourceName=universityDS
   ```
4. **Start Karaf** and verify:
   ```karaf
   jdbc:ds-list
   ```

### Check 6: Verify All Required Features are Installed

```karaf
feature:list | grep -E "(jdbc|pax-jdbc)"
```

Should show:
- `jdbc` (installed)
- `pax-jdbc-postgresql` (installed)
- `pax-jdbc-config` (installed)

If any are missing:
```karaf
feature:install jdbc pax-jdbc-postgresql pax-jdbc-config
```

### Check 7: Try Different Configuration PID Format

Sometimes the PID format matters. Try:
```karaf
config:delete org.ops4j.datasource-university
config:edit org.ops4j.datasource.university
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"
config:property-set url jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require
config:property-set user postgres
config:property-set password WIF_3006_G2
config:property-set dataSourceName universityDS
config:update
```

Note: Changed from `org.ops4j.datasource-university` to `org.ops4j.datasource.university` (dot instead of hyphen).
