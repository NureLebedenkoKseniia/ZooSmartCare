// Config
const API_BASE_URL = "http://localhost:8000";

// Translations
const i18n = {
    uk: {
        "login-subtitle": "Увійдіть для доступу до порталу",
        "login-username-lbl": "Повне Ім'я",
        "login-password-lbl": "Пароль",
        "login-btn": "Увійти",
        "nav-dashboard": "Дашборд",
        "nav-enclosures": "Вольєри",
        "nav-animals": "Тварини",
        "nav-alerts": "Активні тривоги",
        "nav-species": "Види та норми",
        "nav-users": "Працівники",
        "nav-system": "Система та бекап",
        "logout-btn": "Вийти",
        "stat-active-alerts": "Активні тривоги",
        "stat-enclosures": "Всього вольєрів",
        "stat-animals": "Всього тварин",
        "chart-consumption-title": "Добове споживання кормів (кг)",
        "chart-temperature-title": "Сер. температура вольєрів (24 год)",
        "enclosures-title": "Список вольєрів зоопарку",
        "btn-add-enclosure": "Додати вольєр",
        "btn-back": "Назад",
        "enc-detail-telemetry": "Показники телеметрії",
        "gauge-temp-lbl": "Температура",
        "gauge-hum-lbl": "Вологість",
        "gauge-light-lbl": "Освітленість",
        "tab-feeding": "Розклад годування",
        "tab-logs": "Журнал обслуговування",
        "tab-feeding-title": "Графік годування тварин",
        "tab-logs-title": "Історія робіт",
        "animals-title": "Тварини зоопарку",
        "btn-add-animal": "Додати тварину",
        "animal-info-title": "Дані картки тварини",
        "spec-nickname": "Прізвисько:",
        "spec-species": "Біологічний вид:",
        "spec-enclosure": "Утримується у вольєрі:",
        "spec-birthdate": "Дата народження:",
        "animal-medical-title": "Медична картка",
        "alerts-title": "Активні системні тривоги",
        "species-title": "Довідник видів та кліматичних норм",
        "btn-add-species": "Додати вид",
        "users-title": "Працівники системи",
        "btn-add-user": "Зареєструвати працівника",
        "th-name": "Повне ім'я",
        "th-role": "Роль / Посада",
        "th-contact": "Контакти",
        "th-actions": "Дії",
        "system-title": "Резервне копіювання та відновлення бази даних",
        "card-backup-title": "Резервні копії (Backup & Restore)",
        "card-backup-desc": "Завантажте повний дамп бази даних у форматі JSON або оберіть раніше збережений файл, щоб відновити стан усіх таблиць.",
        "btn-backup-download": "Створити бекап",
        "btn-restore-upload": "Відновити базу з файлу",
        "card-export-title": "Експорт / Імпорт окремих таблиць",
        "card-export-desc": "Оберіть конкретну категорію (таблицю) для вивантаження у JSON-файл або повного перезапису даних з файлу.",
        "lbl-select-table": "Оберіть таблицю для операції",
        "btn-export": "Експортувати",
        "btn-import": "Імпортувати дані"
    },
    en: {
        "login-subtitle": "Sign in to manage your smart zoo portal",
        "login-username-lbl": "Full Name",
        "login-password-lbl": "Password",
        "login-btn": "Login",
        "nav-dashboard": "Dashboard",
        "nav-enclosures": "Enclosures",
        "nav-animals": "Animals",
        "nav-alerts": "Active Alerts",
        "nav-species": "Species & Rules",
        "nav-users": "Users",
        "nav-system": "System & Backup",
        "logout-btn": "Log Out",
        "stat-active-alerts": "Active Alerts",
        "stat-enclosures": "Total Enclosures",
        "stat-animals": "Total Animals",
        "chart-consumption-title": "Daily Food Consumption (kg)",
        "chart-temperature-title": "Avg Temp per Enclosure (24h)",
        "enclosures-title": "Enclosures list",
        "btn-add-enclosure": "Add Enclosure",
        "btn-back": "Back",
        "enc-detail-telemetry": "Telemetry Readings",
        "gauge-temp-lbl": "Temperature",
        "gauge-hum-lbl": "Humidity",
        "gauge-light-lbl": "Light",
        "tab-feeding": "Feeding schedules",
        "tab-logs": "Maintenance Logs",
        "tab-feeding-title": "Enclosure Feeding Schedules",
        "tab-logs-title": "Maintenance action logs",
        "animals-title": "Zoo Animals",
        "btn-add-animal": "Add Animal",
        "animal-info-title": "Animal Card Details",
        "spec-nickname": "Nickname:",
        "spec-species": "Species:",
        "spec-enclosure": "Enclosure location:",
        "spec-birthdate": "Birth date:",
        "animal-medical-title": "Medical Records History",
        "alerts-title": "Active System Alerts",
        "species-title": "Species & Climate Rules Reference",
        "btn-add-species": "Add Species",
        "users-title": "System Users",
        "btn-add-user": "Register User",
        "th-name": "Full Name",
        "th-role": "Role / Title",
        "th-contact": "Contact info",
        "th-actions": "Actions",
        "system-title": "System Administration & Backups",
        "card-backup-title": "Backup & Restore Database",
        "card-backup-desc": "Download a complete backup JSON file of the database or upload a previously saved file to restore the database tables.",
        "btn-backup-download": "Download Backup",
        "btn-restore-upload": "Restore Database",
        "card-export-title": "Export / Import Specific Tables",
        "card-export-desc": "Select a specific asset table (animals, enclosures, species, etc.) to export it as JSON or overwrite its current content using an import.",
        "lbl-select-table": "Select Database Table",
        "btn-export": "Export Table",
        "btn-import": "Import Data"
    }
};

// Global State
let currentLanguage = localStorage.getItem("lang") || "uk";
let token = localStorage.getItem("token") || null;
let currentUser = null;
let currentDirection = "ltr";

// Cache lists for dropdowns / names mapping
let speciesCache = [];
let enclosuresCache = [];
let usersCache = [];

// Chart References
let consumptionChart = null;
let tempChart = null;
let enclosureHistoryChart = null;

// App Init
document.addEventListener("DOMContentLoaded", () => {
    initLocalization();
    initDirection();
    initRouting();
    initAuth();
});

// Localization Functions
function initLocalization() {
    const langSelect = document.getElementById("lang-select");
    langSelect.value = currentLanguage;
    langSelect.addEventListener("change", (e) => {
        currentLanguage = e.target.value;
        localStorage.setItem("lang", currentLanguage);
        document.documentElement.lang = currentLanguage;
        applyLocalization();
        // Refresh charts & tables on language change
        refreshCurrentView();
    });
    document.documentElement.lang = currentLanguage;
    applyLocalization();
}

function applyLocalization() {
    const elements = document.querySelectorAll("[data-i18n]");
    elements.forEach(el => {
        const key = el.getAttribute("data-i18n");
        if (i18n[currentLanguage] && i18n[currentLanguage][key]) {
            el.textContent = i18n[currentLanguage][key];
        }
    });
    
    // Update date fields place holders or texts
    const textFields = document.querySelectorAll("[placeholder]");
    textFields.forEach(el => {
        const key = el.getAttribute("placeholder");
        // Simple mapping placeholder translates if desired
    });
}

function formatLocalDate(dateStr) {
    if (!dateStr) return "--";
    try {
        const date = new Date(dateStr);
        const options = { 
            year: 'numeric', month: 'numeric', day: 'numeric',
            hour: '2-digit', minute: '2-digit' 
        };
        const locale = currentLanguage === "uk" ? "uk-UA" : "en-US";
        return new Intl.DateTimeFormat(locale, options).format(date);
    } catch (e) {
        return dateStr;
    }
}

// Direction (RTL/LTR) Functions
function initDirection() {
    const dirToggle = document.getElementById("dir-toggle");
    dirToggle.addEventListener("click", () => {
        currentDirection = currentDirection === "ltr" ? "rtl" : "ltr";
        document.body.dir = currentDirection;
    });
}

// Authentication & Core API Functions
function initAuth() {
    const loginForm = document.getElementById("login-form");
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const usernameInput = document.getElementById("login-username").value.trim();
        const passwordInput = document.getElementById("login-password").value;
        const errorDiv = document.getElementById("login-error");
        
        errorDiv.classList.add("hidden");
        
        try {
            // OAuth2 Login fields
            const params = new URLSearchParams();
            params.append("username", usernameInput);
            params.append("password", passwordInput);

            const res = await fetch(`${API_BASE_URL}/api/admin/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params
            });

            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.detail || "Authentication failed");
            }

            const data = await res.json();
            token = data.access_token;
            localStorage.setItem("token", token);
            
            // Get user information
            await fetchCurrentUser();
            showPortal();
            
        } catch (err) {
            errorDiv.textContent = err.message;
            errorDiv.classList.remove("hidden");
        }
    });

    const logoutBtn = document.getElementById("logout-btn");
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("token");
        token = null;
        currentUser = null;
        showLogin();
    });

    if (token) {
        fetchCurrentUser().then(() => {
            showPortal();
        }).catch(() => {
            showLogin();
        });
    } else {
        showLogin();
    }
}

async function fetchCurrentUser() {
    const res = await fetch(`${API_BASE_URL}/api/admin/auth/me`, {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (!res.ok) {
        throw new Error("Session expired");
    }
    currentUser = await res.json();
    document.getElementById("user-display-name").textContent = currentUser.full_name;
    document.getElementById("user-display-role").textContent = currentUser.role;
    
    // Adapt layout for specific roles
    adaptRolesUI(currentUser.role);
}

function adaptRolesUI(role) {
    const adminElements = document.querySelectorAll(".admin-only");
    const techKeeperAdminElements = document.querySelectorAll(".tech-keeper-admin-only");
    const adminZoologistElements = document.querySelectorAll(".admin-zoologist-only");
    const adminZoologistKeeperElements = document.querySelectorAll(".admin-zoologist-keeper-only");
    const vetElements = document.querySelectorAll(".vet-only");

    const normRole = role.toLowerCase();

    // Toggle Admin Only Elements
    adminElements.forEach(el => {
        if (normRole === "admin") el.classList.remove("hidden");
        else el.classList.add("hidden");
    });

    // Tech, Keeper, Admin Only
    techKeeperAdminElements.forEach(el => {
        if (["admin", "technician", "keeper"].includes(normRole)) el.classList.remove("hidden");
        else el.classList.add("hidden");
    });

    // Admin and Zoologist Only
    adminZoologistElements.forEach(el => {
        if (["admin", "zoologist"].includes(normRole)) el.classList.remove("hidden");
        else el.classList.add("hidden");
    });

    // Admin, Zoologist, Keeper Only
    adminZoologistKeeperElements.forEach(el => {
        if (["admin", "zoologist", "keeper"].includes(normRole)) el.classList.remove("hidden");
        else el.classList.add("hidden");
    });

    // Vet Only
    vetElements.forEach(el => {
        if (normRole === "vet") el.classList.remove("hidden");
        else el.classList.add("hidden");
    });
}

function showLogin() {
    document.getElementById("portal-layout").classList.add("hidden");
    document.getElementById("view-login").classList.remove("hidden");
}

function showPortal() {
    document.getElementById("view-login").classList.add("hidden");
    document.getElementById("portal-layout").classList.remove("hidden");
    
    // Load species and enclosures in background for caches
    loadCaches();
    
    // Navigate to Dashboard initially
    navigateToView("view-dashboard");
}

async function loadCaches() {
    try {
        const resSpec = await fetch(`${API_BASE_URL}/api/business/species/`);
        if (resSpec.ok) speciesCache = await resSpec.json();

        const resEnc = await fetch(`${API_BASE_URL}/api/admin/enclosures/`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (resEnc.ok) enclosuresCache = await resEnc.json();
    } catch(e){}
}

// Router & Screen Switcher
function initRouting() {
    const navItems = document.querySelectorAll(".nav-menu .nav-item");
    navItems.forEach(item => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            
            navItems.forEach(i => i.classList.remove("active"));
            item.classList.add("active");
            
            const viewId = item.getAttribute("data-view");
            navigateToView(viewId);
        });
    });
    
    // Sub-screen navigations back button listeners
    document.getElementById("back-to-enclosures").addEventListener("click", () => navigateToView("view-enclosures"));
    document.getElementById("back-to-animals").addEventListener("click", () => navigateToView("view-animals"));
}

function navigateToView(viewId) {
    const panels = document.querySelectorAll(".workspace-panel");
    panels.forEach(p => p.classList.remove("active"));
    
    const targetPanel = document.getElementById(viewId);
    if (targetPanel) {
        targetPanel.classList.add("active");
        
        // Update top bar view title
        const matchingNav = document.querySelector(`.nav-menu [data-view="${viewId}"]`);
        if (matchingNav) {
            document.getElementById("current-view-title").textContent = matchingNav.querySelector("span:last-child").textContent;
        }
        
        // Trigger specific view load routines
        triggerViewLoad(viewId);
    }
}

function refreshCurrentView() {
    const activePanel = document.querySelector(".workspace-panel.active");
    if (activePanel) {
        triggerViewLoad(activePanel.id);
    }
}

function triggerViewLoad(viewId) {
    if (viewId === "view-dashboard") loadDashboardData();
    else if (viewId === "view-enclosures") loadEnclosuresList();
    else if (viewId === "view-animals") loadAnimalsList();
    else if (viewId === "view-alerts") loadAlertsList();
    else if (viewId === "view-species") loadSpeciesList();
    else if (viewId === "view-users") loadUsersList();
    else if (viewId === "view-system") loadSystemPanel();
}

// 1. Dashboard Logic & Analytics Charts
async function loadDashboardData() {
    try {
        const resAlerts = await fetch(`${API_BASE_URL}/api/business/alerts/`);
        const alerts = await resAlerts.json();
        document.getElementById("stat-alerts-count").textContent = alerts.length;

        const resEnc = await fetch(`${API_BASE_URL}/api/admin/enclosures/`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const enclosures = await resEnc.json();
        document.getElementById("stat-enclosures-count").textContent = enclosures.length;

        const resAnimals = await fetch(`${API_BASE_URL}/api/business/animals/`);
        const animals = await resAnimals.json();
        document.getElementById("stat-animals-count").textContent = animals.length;

        // Load Reports
        loadConsumptionReport();
        loadTemperatureReport(enclosures);

    } catch (e) {
        console.error("Dashboard load failed", e);
    }
}

async function loadConsumptionReport() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/reports/feeding-consumption`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await res.json();
        
        // Update Consumption chart
        const ctx = document.getElementById("feeding-consumption-chart").getContext("2d");
        if (consumptionChart) consumptionChart.destroy();
        
        const labelText = currentLanguage === "uk" ? "М'ясо (кг)" : "Meat (kg)";
        consumptionChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: [labelText],
                datasets: [{
                    label: currentLanguage === "uk" ? "Добове споживання" : "Daily Consumption",
                    data: [data.total_meat_daily_kg],
                    backgroundColor: "rgba(76, 175, 80, 0.4)",
                    borderColor: "rgba(76, 175, 80, 1)",
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    } catch(e){}
}

async function loadTemperatureReport(enclosures) {
    try {
        const labels = [];
        const temps = [];
        
        for (let enc of enclosures) {
            const res = await fetch(`${API_BASE_URL}/api/business/reports/temperature-avg/${enc.enclosure_id}`);
            if (res.ok) {
                const data = await res.json();
                labels.push(enc.name);
                temps.push(data.avg_temp_24h);
            }
        }

        const ctx = document.getElementById("enclosure-temp-chart").getContext("2d");
        if (tempChart) tempChart.destroy();

        tempChart = new Chart(ctx, {
            type: "line",
            data: {
                labels: labels,
                datasets: [{
                    label: currentLanguage === "uk" ? "Середня температура (°C)" : "Avg Temperature (°C)",
                    data: temps,
                    backgroundColor: "rgba(255, 193, 7, 0.2)",
                    borderColor: "rgba(255, 193, 7, 1)",
                    borderWidth: 2,
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    } catch(e){}
}

// 2. Enclosures list & Details
async function loadEnclosuresList() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/admin/enclosures/`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        let enclosures = await res.json();
        
        // Sorting alphabetically by name using localeCompare
        enclosures.sort((a, b) => a.name.localeCompare(b.name, currentLanguage));

        const grid = document.getElementById("enclosures-grid");
        grid.innerHTML = "";
        
        enclosures.forEach(enc => {
            const card = document.createElement("div");
            card.className = "enclosure-card glass-panel";
            card.innerHTML = `
                <div class="card-header-icon">
                    <span class="material-icons">place</span>
                    <span class="card-badge">ID: ${enc.enclosure_id}</span>
                </div>
                <h3>${enc.name}</h3>
                <p class="item-desc">${enc.geo_location || "--"}</p>
            `;
            card.addEventListener("click", () => showEnclosureDetail(enc.enclosure_id));
            grid.appendChild(card);
        });

        // Add Enclosure dialog listener
        document.getElementById("add-enclosure-btn").onclick = () => showEnclosureForm();

    } catch (e){}
}

async function showEnclosureDetail(id) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/admin/enclosures/${id}`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const enc = await res.json();
        
        document.getElementById("enclosure-detail-name").textContent = enc.name;
        navigateToView("view-enclosure-detail");
        
        // Load live gauges
        loadLiveTelemetry(id);
        
        // Load schedules & logs tab actions
        const tabFeedingBtn = document.getElementById("tab-feeding-btn");
        const tabLogsBtn = document.getElementById("tab-logs-btn");
        const feedContent = document.getElementById("tab-feeding-content");
        const logsContent = document.getElementById("tab-logs-content");
        
        tabFeedingBtn.onclick = () => {
            tabFeedingBtn.classList.add("active");
            tabLogsBtn.classList.remove("active");
            feedContent.classList.remove("hidden");
            logsContent.classList.add("hidden");
            loadEnclosureSchedules(id);
        };
        
        tabLogsBtn.onclick = () => {
            tabLogsBtn.classList.add("active");
            tabFeedingBtn.classList.remove("active");
            logsContent.classList.remove("hidden");
            feedContent.classList.add("hidden");
            loadEnclosureMaintenanceLogs(id);
        };
        
        // Default select feeding
        tabFeedingBtn.click();
        
        document.getElementById("add-schedule-btn").onclick = () => showFeedingScheduleForm(id);
        document.getElementById("add-log-btn").onclick = () => showMaintenanceLogForm(id);

    } catch (e){}
}

async function loadLiveTelemetry(enclosureId) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/telemetry/enclosure/${enclosureId}/latest`);
        const data = await res.json();
        
        if (data) {
            document.getElementById("gauge-temp").textContent = `${data.temperature_val?.toFixed(1)}°C` || "--°C";
            document.getElementById("gauge-humidity").textContent = `${data.humidity_val?.toFixed(1)}%` || "--%";
            document.getElementById("gauge-light").textContent = `${data.light_val?.toFixed(1)} lx` || "-- lx";
        } else {
            document.getElementById("gauge-temp").textContent = "--°C";
            document.getElementById("gauge-humidity").textContent = "--%";
            document.getElementById("gauge-light").textContent = "-- lx";
        }

        // Draw Telemetry History Chart
        const resHistory = await fetch(`${API_BASE_URL}/api/business/telemetry/history/${enclosureId}?limit=10`);
        const history = await resHistory.json();
        
        const labels = history.map(h => formatLocalDate(h.timestamp)).reverse();
        const temps = history.map(h => h.temperature_val).reverse();
        const hums = history.map(h => h.humidity_val).reverse();

        const ctx = document.getElementById("enclosure-history-chart").getContext("2d");
        if (enclosureHistoryChart) enclosureHistoryChart.destroy();

        enclosureHistoryChart = new Chart(ctx, {
            type: "line",
            data: {
                labels: labels,
                datasets: [
                    {
                        label: currentLanguage === "uk" ? "Температура" : "Temp",
                        data: temps,
                        borderColor: "rgba(244, 67, 54, 1)",
                        borderWidth: 1.5,
                        fill: false
                    },
                    {
                        label: currentLanguage === "uk" ? "Вологість" : "Humidity",
                        data: hums,
                        borderColor: "rgba(33, 150, 243, 1)",
                        borderWidth: 1.5,
                        fill: false
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    } catch(e){}
}

async function loadEnclosureSchedules(enclosureId) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/enclosures/${enclosureId}/schedules`);
        const schedules = await res.json();
        
        const container = document.getElementById("feeding-schedule-list");
        container.innerHTML = "";
        
        schedules.forEach(s => {
            const item = document.createElement("div");
            item.className = "schedule-item";
            item.innerHTML = `
                <div class="item-content">
                    <span class="item-title">${s.food_type || "Food"}</span>
                    <span class="item-desc">${s.feed_time} | ${s.portion_size} kg</span>
                    <span class="item-meta">${s.days_of_week || "--"}</span>
                </div>
                <button class="btn btn-secondary btn-sm admin-zoologist-keeper-only" onclick="deleteFeedingSchedule(${enclosureId}, ${s.schedule_id})">
                    <span class="material-icons">delete</span>
                </button>
            `;
            container.appendChild(item);
        });
        
        adaptRolesUI(currentUser.role);
    } catch (e){}
}

async function deleteFeedingSchedule(enclosureId, scheduleId) {
    if (confirm("Are you sure you want to delete this schedule?")) {
        try {
            await fetch(`${API_BASE_URL}/api/business/schedules/${scheduleId}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });
            loadEnclosureSchedules(enclosureId);
        } catch(e){}
    }
}

async function loadEnclosureMaintenanceLogs(enclosureId) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/enclosures/${enclosureId}/logs`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const logs = await res.json();
        
        const container = document.getElementById("maintenance-logs-list");
        container.innerHTML = "";
        
        logs.forEach(l => {
            const item = document.createElement("div");
            item.className = "log-item";
            item.innerHTML = `
                <div class="item-content">
                    <span class="item-title">${l.action_type || "Maintenance"}</span>
                    <span class="item-desc">${l.notes || "--"}</span>
                    <span class="item-meta">User #${l.user_id} | ${formatLocalDate(l.timestamp)}</span>
                </div>
            `;
            container.appendChild(item);
        });
    } catch(e){}
}

// 3. Animals logic
async function loadAnimalsList() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/animals/`);
        let animals = await res.json();

        // Search filter
        const searchInput = document.getElementById("animal-search");
        searchInput.oninput = () => {
            const query = searchInput.value.toLowerCase();
            const filtered = animals.filter(a => a.nickname.toLowerCase().includes(query));
            renderAnimalsGrid(filtered);
        };

        // Sorting alphabetically by nickname
        animals.sort((a, b) => a.nickname.localeCompare(b.nickname, currentLanguage));

        renderAnimalsGrid(animals);

        document.getElementById("add-animal-btn").onclick = () => showAnimalForm();

    } catch (e){}
}

function renderAnimalsGrid(animals) {
    const grid = document.getElementById("animals-grid");
    grid.innerHTML = "";
    
    animals.forEach(a => {
        const species = speciesCache.find(s => s.species_id === a.species_id);
        const speciesName = species ? (species.common_name || species.scientific_name) : `Species #${a.species_id}`;
        
        const card = document.createElement("div");
        card.className = "animal-card glass-panel";
        card.innerHTML = `
            <div class="card-header-icon">
                <span class="material-icons">pets</span>
                <span class="card-badge">ID: ${a.animal_id}</span>
            </div>
            <h3>${a.nickname}</h3>
            <p class="item-desc">${speciesName}</p>
        `;
        card.addEventListener("click", () => showAnimalDetail(a.animal_id));
        grid.appendChild(card);
    });
}

async function showAnimalDetail(id) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/animals/${id}`);
        const animal = await res.json();
        
        document.getElementById("animal-detail-name").textContent = animal.nickname;
        navigateToView("view-animal-detail");
        
        document.getElementById("spec-nickname-val").textContent = animal.nickname;
        
        const species = speciesCache.find(s => s.species_id === animal.species_id);
        document.getElementById("spec-species-val").textContent = species ? (species.common_name || species.scientific_name) : animal.species_id;
        
        const enclosure = enclosuresCache.find(e => e.enclosure_id === animal.enclosure_id);
        document.getElementById("spec-enclosure-val").textContent = enclosure ? enclosure.name : animal.enclosure_id;
        
        document.getElementById("spec-birthdate-val").textContent = animal.birth_date || "--";

        // Load Medical History
        loadMedicalHistory(id);

        document.getElementById("add-medical-btn").onclick = () => showMedicalRecordForm(id);

    } catch (e){}
}

async function loadMedicalHistory(animalId) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/animals/${animalId}/medical-history`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const records = await res.json();
        
        const container = document.getElementById("medical-records-list");
        container.innerHTML = "";
        
        records.forEach(r => {
            const item = document.createElement("div");
            item.className = "medical-item";
            item.innerHTML = `
                <div class="item-content">
                    <span class="item-title">${r.diagnosis || "Checkup"}</span>
                    <span class="item-desc">${r.treatment_notes || "--"}</span>
                    <span class="item-meta">Severity: ${r.severity || "Low"} | Date: ${formatLocalDate(r.event_date)}</span>
                </div>
            `;
            container.appendChild(item);
        });
        
        adaptRolesUI(currentUser.role);
    } catch(e){}
}

// 4. Alerts list
async function loadAlertsList() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/alerts/`);
        const alerts = await res.json();
        
        const container = document.getElementById("active-alerts-list");
        container.innerHTML = "";
        
        if (alerts.length === 0) {
            container.innerHTML = `<div class="status-msg success" style="padding:24px; text-align:center;">No active alerts. All systems operational!</div>`;
            return;
        }

        alerts.forEach(a => {
            const item = document.createElement("div");
            item.className = "alert-item";
            item.innerHTML = `
                <div class="item-content">
                    <span class="item-title">${a.alert_type || "Alert"}</span>
                    <span class="item-desc">${a.message || "--"}</span>
                    <span class="item-meta">Enclosure ID #${a.enclosure_id} | ${formatLocalDate(a.timestamp)}</span>
                </div>
                <button class="btn btn-primary btn-sm" onclick="resolveAlert(${a.alert_id})">
                    <span class="material-icons">check</span>
                    <span>Resolve</span>
                </button>
            `;
            container.appendChild(item);
        });
    } catch(e){}
}

async function resolveAlert(id) {
    try {
        await fetch(`${API_BASE_URL}/api/business/alerts/${id}/resolve`, {
            method: "PUT",
            headers: { "Authorization": `Bearer ${token}` }
        });
        loadAlertsList();
    } catch(e){}
}

// 5. Species list
async function loadSpeciesList() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/business/species/`);
        let species = await res.json();

        // Sort alphabetically
        species.sort((a, b) => (a.common_name || a.scientific_name).localeCompare((b.common_name || b.scientific_name), currentLanguage));

        const grid = document.getElementById("species-grid");
        grid.innerHTML = "";

        species.forEach(s => {
            const card = document.createElement("div");
            card.className = "species-card glass-panel";
            card.innerHTML = `
                <div class="card-header-icon">
                    <span class="material-icons">menu_book</span>
                </div>
                <h3>${s.common_name || "Unknown"}</h3>
                <p class="item-desc"><em>${s.scientific_name}</em></p>
                <p class="item-meta">Diet: ${s.general_diet_info || "--"}</p>
            `;
            grid.appendChild(card);
        });

        document.getElementById("add-species-btn").onclick = () => showSpeciesForm();

    } catch(e){}
}

// 6. Users CRUD list (Admin Only)
async function loadUsersList() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/admin/users/`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        let users = await res.json();

        // Sort users alphabetically
        users.sort((a, b) => a.full_name.localeCompare(b.full_name, currentLanguage));

        const tbody = document.getElementById("users-table-body");
        tbody.innerHTML = "";

        users.forEach(u => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><strong>${u.full_name}</strong></td>
                <td><span class="user-role" style="background: rgba(255,255,255,0.08); padding:4px 8px; border-radius:6px;">${u.role}</span></td>
                <td>${u.contact_info || "--"}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editUser(${u.user_id}, '${u.full_name}', '${u.role}', '${u.contact_info || ""}')">
                        <span class="material-icons">edit</span>
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="deleteUser(${u.user_id})">
                        <span class="material-icons">delete</span>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        document.getElementById("add-user-btn").onclick = () => showUserForm();

    } catch(e){}
}

async function deleteUser(id) {
    if (confirm("Are you sure you want to delete this user?")) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/admin/users/${id}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (res.ok) loadUsersList();
            else {
                const err = await res.json();
                alert(err.detail || "Failed to delete user");
            }
        } catch(e){}
    }
}

// 7. System panels and Backups (Admin Only)
function loadSystemPanel() {
    const backupStatus = document.getElementById("backup-status");
    const exportStatus = document.getElementById("export-status");
    
    backupStatus.className = "status-msg";
    backupStatus.textContent = "";
    
    exportStatus.className = "status-msg";
    exportStatus.textContent = "";

    // Backup Download
    document.getElementById("backup-download-btn").onclick = async () => {
        try {
            const res = await fetch(`${API_BASE_URL}/api/admin/system/backup`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Backup failed");
            
            const data = await res.json();
            const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
            const url = URL.createObjectURL(blob);
            
            const a = document.createElement("a");
            a.href = url;
            a.download = `zoosmartcare_backup_${new Date().toISOString().split('T')[0]}.json`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            
            backupStatus.textContent = "Backup created successfully!";
            backupStatus.classList.add("success");
        } catch (err) {
            backupStatus.textContent = err.message;
            backupStatus.classList.add("error");
        }
    };

    // Restore Database upload
    const restoreFileInput = document.getElementById("restore-file-input");
    restoreFileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = async (event) => {
            try {
                const jsonData = JSON.parse(event.target.result);
                const res = await fetch(`${API_BASE_URL}/api/admin/system/restore`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(jsonData)
                });
                
                if (!res.ok) {
                    const err = await res.json();
                    throw new Error(err.detail || "Restore failed");
                }
                
                backupStatus.textContent = "Database restored successfully!";
                backupStatus.classList.add("success");
                restoreFileInput.value = ""; // Clear file
                
            } catch (err) {
                backupStatus.textContent = err.message;
                backupStatus.classList.add("error");
                restoreFileInput.value = "";
            }
        };
        reader.readAsText(file);
    };

    // Export Specific Table
    document.getElementById("export-table-btn").onclick = async () => {
        const table = document.getElementById("table-select").value;
        try {
            const res = await fetch(`${API_BASE_URL}/api/admin/system/export/${table}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Export failed");
            
            const data = await res.json();
            const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
            const url = URL.createObjectURL(blob);
            
            const a = document.createElement("a");
            a.href = url;
            a.download = `${table}_export.json`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
            
            exportStatus.textContent = "Table exported successfully!";
            exportStatus.classList.add("success");
        } catch (err) {
            exportStatus.textContent = err.message;
            exportStatus.classList.add("error");
        }
    };

    // Import Table
    const importFileInput = document.getElementById("import-file-input");
    importFileInput.onchange = (e) => {
        const table = document.getElementById("table-select").value;
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = async (event) => {
            try {
                const jsonData = JSON.parse(event.target.result);
                const res = await fetch(`${API_BASE_URL}/api/admin/system/import/${table}`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(jsonData)
                });
                
                if (!res.ok) {
                    const err = await res.json();
                    throw new Error(err.detail || "Import failed");
                }
                
                exportStatus.textContent = `Table '${table}' imported successfully!`;
                exportStatus.classList.add("success");
                importFileInput.value = "";
            } catch (err) {
                exportStatus.textContent = err.message;
                exportStatus.classList.add("error");
                importFileInput.value = "";
            }
        };
        reader.readAsText(file);
    };
}

// Modal dialog dynamic forms
const modalContainer = document.getElementById("modal-container");
const modalTitle = document.getElementById("modal-title");
const modalForm = document.getElementById("modal-form");
const modalClose = document.getElementById("modal-close");

modalClose.onclick = () => modalContainer.classList.add("hidden");

function showModal(title, fields, onSubmit) {
    modalTitle.textContent = title;
    modalForm.innerHTML = "";
    
    fields.forEach(f => {
        const group = document.createElement("div");
        group.className = "input-group";
        
        const label = document.createElement("label");
        label.textContent = f.label;
        label.setAttribute("for", `field-${f.name}`);
        group.appendChild(label);
        
        if (f.type === "select") {
            const select = document.createElement("select");
            select.id = `field-${f.name}`;
            select.className = "form-select";
            f.options.forEach(opt => {
                const option = document.createElement("option");
                option.value = opt.value;
                option.textContent = opt.text;
                if (f.value === opt.value) option.selected = true;
                select.appendChild(option);
            });
            group.appendChild(select);
        } else if (f.type === "textarea") {
            const textarea = document.createElement("textarea");
            textarea.id = `field-${f.name}`;
            textarea.className = "form-control";
            textarea.rows = 4;
            textarea.value = f.value || "";
            group.appendChild(textarea);
        } else {
            const input = document.createElement("input");
            input.id = `field-${f.name}`;
            input.type = f.type || "text";
            input.className = "form-control";
            input.value = f.value || "";
            if (f.required) input.required = true;
            group.appendChild(input);
        }
        
        modalForm.appendChild(group);
    });
    
    // Add Save Button
    const saveBtn = document.createElement("button");
    saveBtn.type = "submit";
    saveBtn.className = "btn btn-primary btn-block";
    saveBtn.textContent = "Save Changes";
    modalForm.appendChild(saveBtn);
    
    modalForm.onsubmit = (e) => {
        e.preventDefault();
        const data = {};
        fields.forEach(f => {
            const el = document.getElementById(`field-${f.name}`);
            if (el) {
                if (f.type === "number") data[f.name] = Number(el.value);
                else data[f.name] = el.value;
            }
        });
        onSubmit(data);
        modalContainer.classList.add("hidden");
    };
    
    modalContainer.classList.remove("hidden");
}

// Form Triggers
function showEnclosureForm() {
    showModal("Add Enclosure", [
        { name: "name", label: "Enclosure Name", required: true },
        { name: "geo_location", label: "Geographical Location (Coordinates)" }
    ], async (data) => {
        try {
            await fetch(`${API_BASE_URL}/api/admin/enclosures/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadEnclosuresList();
            loadCaches();
        } catch(e){}
    });
}

function showAnimalForm() {
    const specOpts = speciesCache.map(s => ({ value: s.species_id, text: s.common_name || s.scientific_name }));
    const encOpts = enclosuresCache.map(e => ({ value: e.enclosure_id, text: e.name }));
    
    showModal("Add Animal", [
        { name: "nickname", label: "Animal Nickname", required: true },
        { name: "species_id", label: "Species", type: "select", options: specOpts },
        { name: "enclosure_id", label: "Enclosure", type: "select", options: encOpts },
        { name: "birth_date", label: "Birth Date (YYYY-MM-DD)", type: "date" }
    ], async (data) => {
        try {
            await fetch(`${API_BASE_URL}/api/admin/animals/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadAnimalsList();
        } catch(e){}
    });
}

function showSpeciesForm() {
    showModal("Add Species", [
        { name: "scientific_name", label: "Scientific Name", required: true },
        { name: "common_name", label: "Common Name" },
        { name: "general_diet_info", label: "Diet Information", type: "textarea" }
    ], async (data) => {
        try {
            await fetch(`${API_BASE_URL}/api/business/species/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadSpeciesList();
            loadCaches();
        } catch(e){}
    });
}

function showFeedingScheduleForm(enclosureId) {
    showModal("Add Feeding Schedule", [
        { name: "food_type", label: "Food Type", required: true },
        { name: "feed_time", label: "Feed Time (HH:MM)", required: true },
        { name: "portion_size", label: "Portion Size (kg)", type: "number", required: true },
        { name: "days_of_week", label: "Days of Week (comma-separated)", value: "Mon,Wed,Fri" }
    ], async (data) => {
        data.enclosure_id = enclosureId;
        try {
            await fetch(`${API_BASE_URL}/api/business/schedules/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadEnclosureSchedules(enclosureId);
        } catch(e){}
    });
}

function showMaintenanceLogForm(enclosureId) {
    showModal("Log Maintenance Action", [
        {
            name: "action_type",
            label: "Action Type",
            type: "select",
            options: [
                { value: "Routine Maintenance", text: "Routine Maintenance" },
                { value: "Cleaning", text: "Cleaning" },
                { value: "Equipment Repair", text: "Equipment Repair" },
                { value: "Equipment Failure", text: "Equipment Failure" },
                { value: "Feed Leftover", text: "Feed Leftover" },
                { value: "Health Observation", text: "Health Observation" }
            ]
        },
        { name: "notes", label: "Description / Notes", type: "textarea", required: true }
    ], async (data) => {
        data.enclosure_id = enclosureId;
        data.user_id = currentUser.user_id;
        try {
            await fetch(`${API_BASE_URL}/api/business/maintenance-logs/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadEnclosureMaintenanceLogs(enclosureId);
        } catch(e){}
    });
}

function showMedicalRecordForm(animalId) {
    showModal("Add Medical Record", [
        { name: "diagnosis", label: "Diagnosis", required: true },
        { name: "treatment_notes", label: "Treatment Notes", type: "textarea" },
        {
            name: "severity",
            label: "Severity",
            type: "select",
            options: [
                { value: "Low", text: "Low" },
                { value: "Medium", text: "Medium" },
                { value: "High", text: "High" },
                { value: "Critical", text: "Critical" }
            ]
        },
        { name: "event_date", label: "Date (YYYY-MM-DD)", type: "date", required: true }
    ], async (data) => {
        data.animal_id = animalId;
        data.user_id = currentUser.user_id;
        // Format ISO String
        data.event_date = new Date(data.event_date).toISOString();
        try {
            await fetch(`${API_BASE_URL}/api/business/medical-records/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadMedicalHistory(animalId);
        } catch(e){}
    });
}

function showUserForm() {
    showModal("Add System User", [
        { name: "full_name", label: "Full Name", required: true },
        { name: "login_credentials", label: "Password (login credential)", type: "password", required: true },
        {
            name: "role",
            label: "Role",
            type: "select",
            options: [
                { value: "Admin", text: "Admin" },
                { value: "Zoologist", text: "Zoologist" },
                { value: "Keeper", text: "Keeper" },
                { value: "Vet", text: "Vet" },
                { value: "Technician", text: "Technician" }
            ]
        },
        { name: "contact_info", label: "Contact Info" }
    ], async (data) => {
        try {
            const res = await fetch(`${API_BASE_URL}/api/admin/users/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            if (res.ok) loadUsersList();
            else {
                const err = await res.json();
                alert(err.detail || "Registration failed");
            }
        } catch(e){}
    });
}

// Edit User (Global function)
window.editUser = function(userId, fullName, role, contactInfo) {
    showModal("Edit User Info", [
        { name: "full_name", label: "Full Name", value: fullName, required: true },
        {
            name: "role",
            label: "Role",
            type: "select",
            value: role,
            options: [
                { value: "Admin", text: "Admin" },
                { value: "Zoologist", text: "Zoologist" },
                { value: "Keeper", text: "Keeper" },
                { value: "Vet", text: "Vet" },
                { value: "Technician", text: "Technician" }
            ]
        },
        { name: "contact_info", label: "Contact Info", value: contactInfo },
        { name: "password", label: "Password (leave blank to keep unchanged)", type: "password" }
    ], async (data) => {
        // If password blank, exclude it
        if (!data.password) delete data.password;
        try {
            await fetch(`${API_BASE_URL}/api/admin/users/${userId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(data)
            });
            loadUsersList();
        } catch(e){}
    });
};
