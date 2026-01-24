# Supabase Setup Guide for Team

This guide explains how to create and share a Supabase PostgreSQL database project for the University Management System team.

## Table of Contents

1. [Creating a Supabase Project](#creating-a-supabase-project)
2. [Sharing with Team Members](#sharing-with-team-members)
3. [Getting Connection Details](#getting-connection-details)
4. [Configuring in Apache Karaf](#configuring-in-apache-karaf)
5. [Security Best Practices](#security-best-practices)

## Creating a Supabase Project

### Step 1: Sign Up / Sign In

1. Go to [Supabase](https://supabase.com/)
2. Click **"Start your project"** or **"Sign In"**
3. Sign in with GitHub, Google, or email

### Step 2: Create a New Project

1. Click **"New Project"** button
2. Fill in the project details:
   - **Name**: `university-management-system` (or your preferred name)
   - **Database Password**: Create a strong password (save this securely!)
   - **Region**: Choose the closest region to your team
   - **Pricing Plan**: Free tier is sufficient for development

3. Click **"Create new project"**
4. Wait 2-3 minutes for the project to be provisioned

### Step 3: Note Your Project Details

Once created, you'll see your project dashboard. Keep this information handy:
- **Project URL**: `https://xxxxx.supabase.co`
- **Project Reference ID**: `xxxxx`

## Sharing with Team Members

### Option 1: Invite Team Members to Organization (Recommended)

1. **Create an Organization** (if you haven't):
   - Click on your profile icon (top right)
   - Select **"New Organization"**
   - Name it: `University Management System Team`
   - Click **"Create organization"**

2. **Add Team Members**:
   - Go to Organization Settings
   - Click **"Members"** tab
   - Click **"Invite members"**
   - Enter team member emails
   - Assign role: **"Developer"** (gives access to projects)
   - Click **"Send invitations"**

3. **Move Project to Organization**:
   - Go to Project Settings
   - Under **"General"**, find **"Transfer project"**
   - Select your organization
   - Confirm transfer

### Option 2: Share Database Credentials (Less Secure)

⚠️ **Not Recommended for Production** - Only use for development/testing

1. **Get Connection String**:
   - Go to Project Settings > Database
   - Find **"Connection string"** section
   - Copy the **"URI"** connection string
   - Share this with team members via secure channel (encrypted message, password manager)

2. **Share via Password Manager**:
   - Use tools like LastPass, 1Password, or Bitwarden
   - Create a shared vault
   - Store connection details securely

## Getting Connection Details

### Step 1: Open Connection Modal

1. In your Supabase project dashboard, click on **"Project Settings"** (gear icon in the left sidebar)
2. Click on **"Database"** in the settings menu
3. Scroll down to find the **"Connection string"** section
4. Look for a button that says **"Connect"** or **"Connect to your project"** and click it

   **Alternative**: If you don't see a Connect button, look for a section that shows connection strings directly. You may see tabs like "Connection String", "App Frameworks", etc.

This will open a modal dialog titled **"Connect to your project"** with tabs at the top.

### Step 2: Get Direct Connection Details

In the connection modal:

1. **Select the connection type**:
   - Click on the **"Connection String"** tab (should be selected by default)
   - You'll see tabs like "Connection String", "App Frameworks", "Mobile Frameworks", etc.

2. **Configure the connection string dropdowns** (below the tabs):
   - **Type**: Select **"URI"** from the first dropdown
   - **Source**: Select **"Primary Database"** from the second dropdown
   - **Method**: Select **"Direct connection"** from the third dropdown

3. **Copy the connection string**:
   - Below the dropdowns, you'll see a large text box with a connection string like:
     ```
     postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres
     ```
   - **Important**: The `[YOUR-PASSWORD]` is a placeholder. Replace it with the actual database password you set when creating the project
   - Click inside the text box and copy the entire connection string (Ctrl+C / Cmd+C)

4. **Extract connection details for Karaf**:
   From the connection string `postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres`, extract:
   - **Host**: `db.xxxxx.supabase.co` (the part after `@` and before `:5432`)
   - **Port**: `5432` (the number after the host)
   - **Database**: `postgres` (the part after `:5432/`)
   - **User**: `postgres` (the part before `:` in `postgres:[YOUR-PASSWORD]`)
   - **Password**: The actual password you set when creating the project (not the placeholder `[YOUR-PASSWORD]`)

**Example**: If your connection string is:
```
postgresql://postgres:[YOUR-PASSWORD]@db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres
```

Then your details are:
- Host: `db.yopvpvgbgajkwadxevda.supabase.co`
- Port: `5432`
- Database: `postgres`
- User: `postgres`
- Password: (the password you created, not `[YOUR-PASSWORD]`)

### Step 3: Handle IPv4 Compatibility (If Needed)

⚠️ **Important**: If you see a warning "Not IPv4 compatible":

**Option A: Use Session Pooler (Recommended for IPv4 networks)**
1. In the same connection modal, change **Method** dropdown from **"Direct connection"** to **"Session Pooler"**
2. The port will change from `5432` to `6543` in the connection string
3. Copy the new connection string
4. Use this for your Karaf configuration

**Option B: Use Direct Connection with IPv4 Add-on**
1. Click **"IPv4 add-on"** button in the warning
2. Follow instructions to purchase/enable IPv4 support
3. Use the direct connection string

**For most development scenarios, use the Session Pooler option (Option A).**

### Step 4: Note Your Connection Details

Write down or save these details securely:

```
Host: db.xxxxx.supabase.co
Port: 5432 (or 6543 if using pooler)
Database: postgres
User: postgres
Password: [your-password]
```

**Full JDBC URL for Karaf:**
```
jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
```

Or if using pooler:
```
jdbc:postgresql://db.xxxxx.supabase.co:6543/postgres?sslmode=require
```

## Configuring in Apache Karaf

### Step 1: Configure Datasource in Karaf

In Karaf console:

```karaf
# Edit datasource configuration
config:edit org.ops4j.datasource-university

# Set PostgreSQL driver (MUST match exactly: "PostgreSQL JDBC Driver")
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"

# Set connection URL (IMPORTANT: Only host, port, and database - NO username/password!)
# Format: jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require
# Example for direct connection (replace db.xxxxx with your actual host):
config:property-set url jdbc:postgresql://db.yopvpvgbgajkwadxevda.supabase.co:5432/postgres?sslmode=require

# Example for session pooler (if IPv4 network):
# config:property-set url jdbc:postgresql://db.xxxxx.supabase.co:6543/postgres?sslmode=require

# Set username (separate from URL)
config:property-set user postgres

# Set password (separate from URL - replace with your actual password)
config:property-set password WIF_3006_G2

# Set datasource name (must match persistence.xml)
config:property-set dataSourceName universityDS

# Save configuration
config:update
```

**⚠️ Common Mistake**: Do NOT include username and password in the URL. The URL should only contain:
- `jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require`

Username and password are set as separate properties (`user` and `password`).

### Step 2: Verify Connection

```karaf
# List datasources
jdbc:ds-list

# Test connection
jdbc:query universityDS "SELECT version()"
```

If successful, you'll see PostgreSQL version information.

**If `jdbc:ds-list` shows empty**, the datasource wasn't created. See troubleshooting below.

### Troubleshooting: Datasource Not Appearing

If `jdbc:ds-list` is empty after configuration:

1. **Check PostgreSQL driver bundle**:
   ```karaf
   bundle:list | grep postgresql
   ```
   If missing, install:
   ```karaf
   bundle:install mvn:org.postgresql/postgresql/42.7.1
   bundle:start <bundle-id>
   ```

2. **Check configuration file exists**:
   ```karaf
   config:list | grep datasource
   ```
   Or check file: `<KARAF_HOME>/etc/org.ops4j.datasource-university.cfg`

3. **Check logs for errors**:
   ```karaf
   log:tail | grep -i "datasource\|postgresql\|jdbc"
   ```

4. **Restart Pax JDBC Config bundle**:
   ```karaf
   bundle:list | grep pax-jdbc-config
   bundle:restart <bundle-id>
   ```

5. **Verify all features are installed**:
   ```karaf
   feature:list | grep -E "(jdbc|pax-jdbc)"
   ```
   Should show: `jdbc`, `pax-jdbc-postgresql`, `pax-jdbc-config` all installed.

## Security Best Practices

### 1. Database Password Security

- ✅ Use a strong, unique password (16+ characters, mixed case, numbers, symbols)
- ✅ Store password in a password manager
- ✅ Never commit passwords to Git
- ✅ Rotate passwords periodically

### 2. Connection Security

- ✅ Supabase uses SSL/TLS by default
- ✅ Connection strings include SSL parameters automatically
- ✅ For production, use connection pooling with SSL

### 3. Access Control

- ✅ Only share credentials with team members who need access
- ✅ Use Supabase's built-in access control features
- ✅ Review team member access regularly
- ✅ Remove access when team members leave

### 4. Environment Variables (Recommended)

Instead of hardcoding credentials, use environment variables:

**Create a configuration file** (not committed to Git):
```properties
# .env.local (DO NOT COMMIT THIS FILE)
SUPABASE_HOST=db.xxxxx.supabase.co
SUPABASE_PORT=5432
SUPABASE_DB=postgres
SUPABASE_USER=postgres
SUPABASE_PASSWORD=your_password_here
```

**Add to .gitignore**:
```
.env.local
*.env
```

### 5. Row Level Security (RLS)

For production, enable Row Level Security in Supabase:
1. Go to **Authentication** > **Policies**
2. Create policies to restrict data access
3. This adds an extra layer of security

## Team Setup Checklist

- [ ] Create Supabase account
- [ ] Create new project
- [ ] Create organization
- [ ] Invite all team members
- [ ] Move project to organization
- [ ] Share connection details securely
- [ ] Test connection from each team member's environment
- [ ] Document connection details in shared password manager
- [ ] Add `.env.local` to `.gitignore`
- [ ] Update team documentation with Supabase connection details

## Troubleshooting

### Connection Refused

- Check if Supabase project is active (not paused)
- Verify host and port are correct
- Check firewall settings
- Verify password is correct

### Authentication Failed

- Double-check username and password
- Ensure you're using the correct database name (`postgres`)
- Check if IP restrictions are enabled (disable for development)

### SSL Connection Issues

- Supabase requires SSL by default
- Add SSL parameters to connection string:
  ```
  jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
  ```

### Project Paused

Free tier projects pause after 7 days of inactivity:
- Go to project dashboard
- Click **"Restore project"**
- Wait a few minutes for restoration

## Connection String Examples

### Standard Connection
```
jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
```

### With SSL (Recommended)
```
jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
```

### Connection Pooling (Better Performance)
```
jdbc:postgresql://db.xxxxx.supabase.co:6543/postgres?sslmode=require
```

## Quick Reference for Karaf Configuration

```karaf
# Complete Supabase configuration in one go
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"
config:property-set url jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres?sslmode=require
config:property-set user postgres
config:property-set password YOUR_PASSWORD_HERE
config:property-set dataSourceName universityDS
config:update

# Verify
jdbc:ds-list
jdbc:query universityDS "SELECT current_database(), current_user"
```

## Additional Resources

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Database Guide](https://supabase.com/docs/guides/database)
- [PostgreSQL Connection Strings](https://www.postgresql.org/docs/current/libpq-connect.html#LIBPQ-CONNSTRING)
- [Supabase Dashboard](https://app.supabase.com/)

---

**Important Notes:**
- Free tier includes 500MB database storage
- Free tier projects pause after 7 days of inactivity
- Connection limit: 60 connections (free tier)
- For production, consider upgrading to paid plan

**Last Updated**: January 2026
