# Brahmavanam — Hosting Recommendation (Post Oracle Cloud)

**Prepared:** October 10, 2026  
**Context:** Oracle Cloud Always Free plan failed — VM was reclaimed without warning.  
**Traffic:** ~50–100 hits per week  

---

## Why Oracle Cloud Failed

Oracle's Always Free AMD E2.1.Micro VMs are **unreliable by design** — Oracle reclaims them when datacenter capacity is needed, with no prior warning. For a production booking system, this is unacceptable. Many users globally have reported sudden VM terminations. Oracle's ARM-based Ampere VMs are more stable, but harder to provision (often capacity-unavailable).

---

## Options Evaluated

### Option 1 — AWS EC2 t4g.micro (Recommended ✅)

Stay on AWS but with a right-sized, cost-optimized setup.

| Change | Action |
|---|---|
| EC2 instance type | Switch from t2.micro → t4g.micro (ARM, 1GB RAM) |
| Elastic IP | Release it — use dynamic IP + Cloudflare proxy |
| EBS volume | Shrink from 30GB → 8GB |
| DNS | Move from Route 53 → Cloudflare (free) |

**Estimated Monthly Cost**

| Item | Cost (USD) | Cost (INR ~₹84/$) |
|---|---|---|
| EC2 t4g.micro compute | ~$3.00 | ~₹252 |
| EBS 8GB gp3 | ~$0.80 | ~₹67 |
| Data transfer (minimal) | ~$0.50 | ~₹42 |
| Route 53 (if kept) | $0.51 | ₹43 |
| **Total** | **~$4.81** | **~₹404/month** |

**Annual: ~₹4,848 + ₹900 domain = ~₹5,748/year**  
*(vs old cost of ₹1,600/month = ₹19,200/year)*

> **Bonus:** Open a fresh legitimate AWS account → 12-month free tier → ₹0 for the first year.

**Pros:**
- Same stack, no migration needed
- Reliable, no surprise terminations
- Full control over the VM
- Familiar deployment process

**Cons:**
- Not completely free after 12 months (~₹400/month)

---

### Option 2 — Render.com Free Tier

Deploy the Spring Boot JAR on Render's free web service + Aiven free MySQL.

| Item | Cost |
|---|---|
| Render free web service | ₹0 |
| Aiven MySQL (500MB free) | ₹0 |
| Cloudflare DNS + SSL | ₹0 |
| Domain renewal | ~₹900/year |

**Pros:**
- Completely free
- No infrastructure management

**Cons:**
- ⚠️ **Cold starts** — service spins down after 15 min of inactivity; first request takes 10–15 seconds to wake up
- At 50–100 hits/week, cold starts will happen very frequently
- Bad UX for a retreat booking site where users expect responsiveness

---

### Option 3 — Fly.io Free Tier

Always-on tiny VMs with a generous free allowance. Use Fly Postgres instead of MySQL.

| Item | Cost |
|---|---|
| Fly.io VM (always-on) | ₹0 |
| Fly Postgres (1GB free) | ₹0 |
| Cloudflare DNS + SSL | ₹0 |
| Domain renewal | ~₹900/year |

**Pros:**
- Free and always-on (no cold starts)
- More reliable than Oracle Cloud
- Good developer experience

**Cons:**
- ⚠️ Requires migrating from MySQL → PostgreSQL (one-time effort, moderate complexity)
- Less control than a full VM
- Fly.io free tier terms may change

---

## Cost Comparison Summary

| Option | Monthly Cost | Annual Cost | Cold Starts | Migration Effort |
|---|---|---|---|---|
| AWS t4g.micro (optimized) | ~₹400 | ~₹5,748 | None | None |
| AWS t4g.micro (new account free tier) | ₹0 for 12 months | ~₹900 (domain only) | None | None |
| Render.com | ₹0 | ~₹900 | Yes (frequent) | Low |
| Fly.io | ₹0 | ~₹900 | No | Medium (MySQL→Postgres) |
| Oracle Cloud (failed) | ₹0 | ~₹900 | No | — |

---

## Final Recommendation

> **AWS EC2 t4g.micro on a new account (fresh 12-month free tier)**

**Reasoning:**
- Brahmavanam handles real bookings for a spiritual retreat — availability and responsiveness matter
- Cold starts (Render) are bad UX when someone is booking a retreat slot
- Database migration (Fly.io) is unnecessary risk and effort
- Oracle Cloud is unreliable for always-free VMs
- A new legitimate AWS account gives 12 months free, then ~₹400/month after — very affordable

### Immediate Action Plan

1. **Open a new AWS account** with a different email (legitimate, one per person per account)
2. Launch a **t4g.micro** EC2 in `ap-south-1` (Mumbai) — closest to your users
3. Restore from `brahmavanam_backup_fresh.sql` into a fresh MySQL instance
4. Deploy `brahmavanam-0.0.17-SNAPSHOT.jar` with `application-cloud.properties`
5. Set up **Cloudflare DNS** (already done — reuse the same setup)
6. Point `brahmavanam.org` A record to the new EC2 IP via Cloudflare
7. Test bookings, login, calendar end-to-end
8. Go live

---

## Key Assets (Already Available)

| Asset | Location |
|---|---|
| Database backup | `brahmavanam_backup_fresh.sql` |
| Application JAR | `jar/brahmavanam-0.0.17-SNAPSHOT.jar` |
| App config | `src/main/resources/application-cloud.properties` |
| SSH key (old Oracle VM) | `~/.ssh/oracle-brahmavanam.pem` |
| Domain | `brahmavanam.org` (Namecheap, expires March 27, 2028) |
| DNS | Cloudflare (nameservers: `aarav.ns.cloudflare.com`, `kelly.ns.cloudflare.com`) |

---

*Document prepared with assistance from Kiro AI on October 10, 2026.*
