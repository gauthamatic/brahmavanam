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
*Last updated: September 20, 2026 — Cloudflare DNS active, nginx setup pending.*
