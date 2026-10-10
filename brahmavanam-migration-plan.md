# Brahmavanam — AWS Migration Plan
**Prepared:** August 2, 2026  
**Author:** Gowtham Govind  

---

## 1. Background

**Brahmavanam** is a Spring Boot web application for managing bookings at a spiritual retreat/meditation center. It is hosted at [brahmavanam.org](https://brahmavanam.org).

### Current Tech Stack
- Java 17, Spring Boot 3.4.0
- Thymeleaf (server-side templates)
- MySQL database
- Deployed on AWS EC2 (t2.micro) in ap-south-1 (Mumbai)
- MySQL running on the same EC2 instance with data on an attached EBS volume
- Domain managed via AWS Route 53

---

## 2. The Problem — Bill Jumped from ₹56 to ₹1,800

The AWS **12-month free tier expired**. Starting July 2026, all services are being billed at full price.

### July 2026 Bill Breakdown

| AWS Service | Cost (USD) | Cost (INR ~₹84/$) |
|---|---|---|
| EC2 t2.micro (compute) | $9.23 | ₹775 |
| Amazon VPC / Networking | $3.72 | ₹312 |
| EBS 30GB Storage | $2.74 | ₹230 |
| Tax | $2.91 | ₹244 |
| Route 53 Hosted Zone | $0.51 | ₹43 |
| **Total** | **~$19.11** | **~₹1,604** |

### Why Each Service Costs Money Now
- **EC2** — Free tier gave 750 hours/month of t2.micro for 12 months. Now paying ~$0.0116/hour × 24 × 30 = ~$8.4/month.
- **EBS** — Free tier gave 30GB/month. Now paying ~$0.10/GB/month.
- **VPC** — Elastic IP and networking charges that were previously covered under free tier.
- **Route 53** — $0.50/month per hosted zone (was always charged but small).

---

## 3. Current AWS Resources

| Resource | Details |
|---|---|
| EC2 Instance | `i-0232c5b06b95a0812`, t2.micro, running since Mar 6 2026 |
| Public IP | `13.234.171.166` (Elastic IP, tagged `brahmavanam`) |
| EBS Volume | `vol-0fcbb371e20e44422`, 30GB gp3, attached |
| Domain | `brahmavanam.org` (registered via Amazon Registrar) |
| Domain Expiry | **March 27, 2027** |
| DNS | Route 53 hosted zone, A record → `13.234.171.166` |

---

## 4. Backups Taken

Before migration, the following backups have been secured:

| Item | Location | Status |
|---|---|---|
| MySQL database dump | `brahmavanam/brahmavanam_backup.sql` | ✅ Done |
| Application JAR | `brahmavanam/jar/brahmavanam-0.0.17-SNAPSHOT.jar` | ✅ In repo |
| DB credentials & config | `src/main/resources/application-cloud.properties` | ✅ On EC2 |
| EC2 SSH key | `~/.ssh/brahmavanam-key.pem` | ✅ Created |

---

## 5. Migration Options Evaluated

### Where to Host the App

| Platform | Free Tier | Verdict |
|---|---|---|
| AWS (new account) | 12 months free | ❌ Against ToS, risk of suspension |
| Railway | $5 credit/month | ⚠️ Limited, may not be enough |
| Render | Free (spins down on inactivity) | ⚠️ Cold start delay on first request |
| Fly.io | 3 free VMs + free Postgres | ⚠️ MySQL workaround needed |
| Google Cloud | e2-micro always free (us-central1 only) | ✅ Good but limited region |
| **Oracle Cloud** | **2 VMs always free, no expiry** | ✅ **Best option** |

**Decision: Oracle Cloud Always Free**
- Real VM (similar to EC2), no expiry
- Can install MySQL directly
- Deploy the JAR exactly as on EC2 today
- No credit card charges as long as within free tier

### Where to Manage DNS

| Service | Cost | Verdict |
|---|---|---|
| AWS Route 53 | $0.50/month per hosted zone | ❌ Unnecessary cost |
| **Cloudflare** | **Free forever** | ✅ **Best option** |
| Oracle Cloud DNS | Free | ✅ Good but basic |

**Decision: Cloudflare**
- Completely free DNS
- Free SSL certificate (HTTPS)
- Built-in DDoS protection
- Faster resolution than Route 53

### Where to Register the Domain

| Registrar | Transfer/Year | Renewal/Year |
|---|---|---|
| Amazon Registrar (current) | — | ~₹1,100 |
| **Namecheap** | **~₹900 (includes 1yr extension)** | **~₹900** |
| Porkbun | ~₹750 | ~₹900 |
| GoDaddy | ~₹200 promo | ~₹1,500 (expensive) |

**Decision: Transfer to Namecheap**
- Cheaper than staying on Amazon Registrar
- Transfer includes a 1-year extension
- Must be done before AWS account is suspended

---

## 6. Cost Comparison

### Option A — Pay July Bill, Keep Route 53 Till March 2027

| Item | Cost |
|---|---|
| July bill (one-time) | ₹1,600 |
| Route 53 hosted zone (Aug–Mar, 8 months) | ₹344 |
| Domain renewal (March 2027) | ₹1,100 |
| Oracle Cloud + Cloudflare | ₹0 |
| **Total** | **₹3,044** |

### Option B — Don't Pay July Bill, Transfer Domain, Move Everything Out

| Item | Cost |
|---|---|
| July bill | ₹0 (don't pay) |
| Domain transfer to Namecheap (includes 1yr) | ₹900 |
| Route 53 hosted zone | ₹0 (deleted) |
| Oracle Cloud + Cloudflare | ₹0 |
| **Total** | **₹900** |

### Savings with Option B: ~₹2,144

**Decision: Option B**

> Note: July bill is unpaid with a debit card. AWS may suspend the account ~30 days after the due date and terminate it ~60 days after. Since it's a debit card (not credit), there is no collections risk and no credit score impact.

---

## 7. Migration Plan (Step by Step)

### Step 1 — Transfer Domain to Namecheap ✅ COMPLETED (Aug 27, 2026)
- ~~Must be completed **before** AWS account is suspended~~
- ~~Go to Route 53 → Registered Domains → `brahmavanam.org` → Transfer out~~
- ~~Sign up on Namecheap, initiate transfer, enter authorization code~~
- ~~Takes 5–7 days to complete~~

**Execution Notes:**
- Auth code retrieved via AWS CLI: `Z]6417V2S~S6A41h`
- Transfer initiated on Namecheap, paid ₹1,171.78
- AWS approval email received and approved same day
- Transfer completed in ~4 hours (Aug 27, 2026)
- Domain now managed by Namecheap (expires March 27, 2028)

### Step 2 — Set Up Oracle Cloud VM ✅ COMPLETED (Aug 27, 2026)
- ~~Sign up at [cloud.oracle.com](https://cloud.oracle.com) (free, requires credit card for verification but won't charge)~~
- ~~Create an Always Free VM (AMD or Ampere)~~
- ~~Open ports 80, 443, 8080 in security rules~~
- ~~Install Java 17 and MySQL~~

**Execution Notes:**
- Oracle Cloud account created (tenancy: gauthamatic, region: ap-hyderabad-1)
- VM created: `brahmavanam`, shape VM.Standard.E2.1.Micro (1 OCPU, 1GB RAM)
- OS: Ubuntu 22.04
- Public IP: **140.245.255.20** (Reserved)
- SSH key: `~/.ssh/oracle-brahmavanam.pem`
- Security list updated: ports 22, 80, 443, 8080 open
- Java 17.0.20 installed
- MySQL 8.0.46 installed and running

### Step 3 — Restore Database on Oracle Cloud ⏳ IN PROGRESS
- Copy `brahmavanam_backup.sql` to the new VM
- Create the `brahmavanam` database and import the dump:
  ```bash
  mysql -u root -p brahmavanam < brahmavanam_backup.sql
  ```

### Step 4 — Deploy the App on Oracle Cloud
- Copy the JAR to the new VM
- Update `application-cloud.properties` with new DB host/credentials
- Start the app:
  ```bash
  java -Dspring.profiles.active=cloud -jar brahmavanam-0.0.17-SNAPSHOT.jar &
  ```

### Step 5 — Set Up Cloudflare DNS
- Sign up at [cloudflare.com](https://cloudflare.com) (free)
- Add `brahmavanam.org`
- Cloudflare auto-imports existing DNS records
- Get Cloudflare nameservers (e.g. `nina.ns.cloudflare.com`)

### Step 6 — Update Nameservers on Namecheap
- After domain transfer completes, go to Namecheap → Domain → Nameservers
- Replace with Cloudflare nameservers
- DNS propagates within minutes to a few hours

### Step 7 — Update A Record on Cloudflare
- Add A record: `brahmavanam.org → 140.245.255.20`
- Enable Cloudflare proxy (orange cloud) for free SSL and DDoS protection

### Step 8 — Verify and Go Live
- Test the app at `brahmavanam.org`
- Verify bookings, login, calendar all work
- Monitor for a day or two

### Step 9 — Let AWS Account Wind Down
- No action needed — account will be suspended and terminated automatically
- All data already backed up, domain transferred, DNS moved

---

## 8. Ongoing Costs After Migration

| Item | Cost |
|---|---|
| Oracle Cloud VM + MySQL | ₹0/month (always free) |
| Cloudflare DNS + SSL | ₹0/month |
| Domain renewal (once a year) | ~₹900/year |
| **Monthly recurring** | **₹0/month** |
| **Annual cost** | **~₹900/year** |

**Compared to current AWS cost: ₹1,800/month = ₹21,600/year**

**Annual savings: ~₹20,700**

---

## 9. Risk & Notes

| Risk | Mitigation |
|---|---|
| Domain lost if AWS suspends before transfer | Transfer domain this week (urgent) |
| Data loss | MySQL backup already taken ✅ |
| App downtime during migration | Set up Oracle Cloud first, switch DNS only after app is verified |
| Oracle Cloud free tier ends | Oracle Always Free has no expiry — it's permanent |
| SSH key management | New key saved at `~/.ssh/brahmavanam-key.pem` |

---

---

## 10. Migration Progress

| Step | Status | Date |
|------|--------|------|
| 1. Domain transfer to Namecheap | ✅ Complete | Aug 27, 2026 |
| 2. Oracle Cloud VM setup | ✅ Complete | Aug 27, 2026 |
| 3. Restore MySQL database | ✅ Complete | Aug 27, 2026 |
| 4. Deploy Spring Boot app | ✅ Complete | Aug 27, 2026 |
| 5. Set up Cloudflare DNS | ✅ Complete | Sep 20, 2026 |
| 6. Update Namecheap nameservers | ✅ Complete | Sep 20, 2026 |
| 7. Update Cloudflare A record | ✅ Complete | Sep 20, 2026 |
| 8. Verify and go live | ⏳ In Progress | — |

### Session Notes (Sep 20, 2026)
- **Cloudflare activated** — DNS propagated, nameservers: `aarav.ns.cloudflare.com`, `kelly.ns.cloudflare.com`
- **A record** points to `140.245.255.20`
- **Issue:** Cloudflare proxy (orange cloud) expects port 80/443, but app runs on 8080
- **Fix needed:** Install nginx on VM to proxy port 80 → 8080
- **VM rebooted** via OCI CLI — will auto-start app via systemd on boot

### Next Steps (Resume Here)
1. SSH into VM: `ssh -i ~/.ssh/oracle-brahmavanam.pem ubuntu@140.245.255.20`
2. Install nginx: `sudo apt install -y nginx`
3. Configure nginx to proxy port 80 → localhost:8080
4. Test https://brahmavanam.org

### Key Info
- **Oracle Cloud IP:** `140.245.255.20`
- **SSH Key:** `~/.ssh/oracle-brahmavanam.pem`
- **SSH User:** `ubuntu`
- **App Port:** 8080 (systemd service: `brahmavanam`)
- **Cloudflare Nameservers:** `aarav.ns.cloudflare.com`, `kelly.ns.cloudflare.com`

### New Infrastructure Details

| Resource | Value |
|----------|-------|
| Oracle Cloud VM IP | `140.245.255.20` |
| VM Shape | VM.Standard.E2.1.Micro (1 OCPU, 1GB RAM) |
| OS | Ubuntu 22.04 |
| Java | 17.0.20 |
| MySQL | 8.0.46 |
| SSH Key | `~/.ssh/oracle-brahmavanam.pem` |
| Domain Registrar | Namecheap |
| Domain Expiry | March 27, 2028 |

---

*Document prepared with assistance from Kiro AI on August 2, 2026.*  
*Last updated: October 10, 2026 — Oracle Cloud abandoned, migrated to AWS EC2. Site fully live.*

---

## 11. Oracle Cloud Failure & Final Migration to AWS EC2 (Oct 10, 2026)

### What Happened to Oracle Cloud

The Oracle Cloud Always Free VM (`140.245.255.20`) was reclaimed without warning — a known issue with Oracle's AMD E2.1.Micro shape where Oracle silently terminates free VMs when datacenter capacity is needed. The app went down and the VM was unrecoverable.

### Decision: Move to AWS EC2 (Permanently)

Rather than fight unreliable free tiers, the decision was made to run on a proper AWS EC2 instance with a right-sized, cost-optimized setup. At 50–100 hits/week, a t3.small is more than sufficient and reliable.

See `hosting-recommendation.md` for the full options analysis.

---

### What Was Done (Oct 10, 2026)

#### Step 1 — Allocate Elastic IP
- Allocated Elastic IP **`18.60.183.229`** via AWS Console
- Attached to instance `i-0bda66782097a1877`
- IP is now permanent — survives all reboots

#### Step 2 — Remove Oracle DB
- Stopped and deleted Oracle DB 23c instance
- Removed Oracle RPM package (`oracle-database-free-23c`)
- Cleaned up `/opt/oracle` — freed ~9GB disk space (30GB disk now at 29% used)

#### Step 3 — Install Java 17 + MySQL 8
- Installed **Amazon Corretto Java 17** (`java-17-amazon-corretto-headless`)
- Installed **MySQL 8.4.11** Community Server
- MySQL enabled on boot, running on port 3306

#### Step 4 — Restore Database
- Created database `brahmavanam` with user `admin`
- Restored from `brahmavanam_backup_fresh.sql`
- Verified: **126 events**, **34 users** restored successfully

#### Step 5 — Deploy App as systemd Service
- Copied `application-cloud.properties` to `jar/` directory
- Updated DB URL from old IP `172.31.9.195` → `localhost`
- Created `/etc/systemd/system/brahmavanam.service`
  - Starts after MySQL (`Requires=mysqld.service`)
  - Auto-restarts on failure (`Restart=on-failure`)
  - Enabled on boot
- App running on port 8080 ✅

#### Step 6 — Install nginx
- Installed nginx, configured reverse proxy: port 80 → localhost:8080
- Config at `/etc/nginx/conf.d/brahmavanam.conf`
- nginx enabled on boot

#### Step 7 — Memory Protection
- 4GB swap file already present and active
- Set `vm.swappiness=10` permanently in `/etc/sysctl.conf`
- Prevents EC2 from hanging under memory pressure

#### Step 8 — Cloudflare DNS Updated
- Updated A record: `brahmavanam.org → 18.60.183.229`
- Cloudflare proxy (orange cloud) enabled — free SSL + DDoS protection
- DNS propagated successfully

#### Step 9 — Site Verified Live
- `http://brahmavanam.org` → HTTP 200 ✅
- `https://brahmavanam.org` → HTTP 200 ✅

---

### Final Infrastructure (Oct 10, 2026)

| Resource | Value |
|---|---|
| EC2 Instance | `i-0bda66782097a1877` |
| Instance Type | t3.small (2 vCPU, 2GB RAM) |
| Region | ap-south-2 (Hyderabad) |
| Elastic IP | `18.60.183.229` (permanent) |
| OS | Amazon Linux 2023 |
| Java | Amazon Corretto 17.0.20.1 |
| MySQL | 8.4.11 Community |
| App Port | 8080 (behind nginx on port 80) |
| Systemd Services | `mysqld`, `brahmavanam`, `nginx` (all enabled) |
| Swap | 4GB (`/swapfile`), swappiness=10 |
| Domain Registrar | Namecheap |
| Domain Expiry | March 27, 2028 |
| DNS | Cloudflare (proxy enabled, free SSL) |
| SSH Key | `~/.ssh/brahmavanam-key.pem` |

### Ongoing Costs

| Item | Cost |
|---|---|
| EC2 t3.small | ~₹1,500/month (or ₹0 under free tier) |
| EBS 30GB | ~₹200/month |
| Elastic IP (attached) | ₹0 |
| Cloudflare DNS + SSL | ₹0 |
| Domain renewal | ~₹900/year |

### Reboot Safety

| Scenario | Data Safe? |
|---|---|
| EC2 reboot | ✅ Yes — EBS persists, all services auto-start |
| EC2 stop → start | ✅ Yes — Elastic IP stays attached |
| EC2 terminate (delete) | ⚠️ Data lost unless EBS retained manually |

### Pending
- [ ] Set up automated daily MySQL backup (cron job)
- [ ] Verify bookings, login, calendar end-to-end on live site
