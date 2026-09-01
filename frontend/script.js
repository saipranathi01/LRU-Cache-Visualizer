
/*
 * Statistics.
 */
const API = "http://localhost:8080";
let hits = 0;

let misses = 0;


/* =====================================================
   SET CAPACITY
===================================================== */

async function setCapacity() {

    const capacity =
        document.getElementById(
            "capacityInput"
        ).value;


    if (capacity === "") {

        showStatus(
            "Please enter a cache capacity.",
            "error"
        );

        return;
    }


    if (Number(capacity) <= 0) {

        showStatus(
            "Capacity must be greater than 0.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API}/capacity?value=${capacity}`
            );


        const data =
            await response.json();


        if (!response.ok) {

            showStatus(
                data.error,
                "error"
            );

            return;
        }


        /*
         * Reset statistics because
         * a new cache was created.
         */
        hits = 0;
        misses = 0;


        /*
         * Update cache display.
         */
        renderCache(data.cache);


        /*
         * Update statistics.
         */
        updateStats();


        /*
         * Add history.
         */
        addHistory(
            `CAPACITY SET → ${capacity}`
        );


        /*
         * Show success message.
         */
        showStatus(
            `Cache capacity set to ${capacity}.`,
            "success"
        );


        /*
         * Clear input.
         */
        document.getElementById(
            "capacityInput"
        ).value = "";

    }

    catch (error) {

        showStatus(
            "Cannot connect to Java backend. Start LRUServer first.",
            "error"
        );
    }
}


/* =====================================================
   PUT
===================================================== */

async function putItem() {

    const key =
        document.getElementById(
            "putKey"
        ).value;


    const value =
        document.getElementById(
            "putValue"
        ).value;


    if (key === "" || value === "") {

        showStatus(
            "Please enter both key and value.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API}/put?key=${key}&value=${value}`
            );


        const data =
            await response.json();


        if (!response.ok) {

            showStatus(
                data.error,
                "error"
            );

            return;
        }


        renderCache(
            data.cache
        );


        showStatus(
            `PUT(${key}, ${value}) completed.`,
            "success"
        );


        addHistory(
            `PUT(${key}, ${value})`
        );


        document.getElementById(
            "putKey"
        ).value = "";


        document.getElementById(
            "putValue"
        ).value = "";


        updateStats();

    }

    catch (error) {

        showStatus(
            "Cannot connect to Java backend. Start LRUServer first.",
            "error"
        );
    }
}


/* =====================================================
   GET
===================================================== */

async function getItem() {

    const key =
        document.getElementById(
            "getKey"
        ).value;


    if (key === "") {

        showStatus(
            "Please enter a key.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API}/get?key=${key}`
            );


        const data =
            await response.json();


        if (!response.ok) {

            showStatus(
                data.error,
                "error"
            );

            return;
        }


        /*
         * Cache HIT
         */
        if (data.hit) {

            hits++;


            showStatus(
                `Cache Hit! Key ${key} = ${data.value}. Moved to MRU.`,
                "success"
            );


            addHistory(
                `GET(${key}) → HIT → ${data.value}`
            );
        }


        /*
         * Cache MISS
         */
        else {

            misses++;


            showStatus(
                `Cache Miss! Key ${key} not found.`,
                "error"
            );


            addHistory(
                `GET(${key}) → MISS`
            );
        }


        renderCache(
            data.cache
        );


        updateStats();


        document.getElementById(
            "getKey"
        ).value = "";

    }

    catch (error) {

        showStatus(
            "Cannot connect to Java backend. Start LRUServer first.",
            "error"
        );
    }
}


/* =====================================================
   CLEAR CACHE
===================================================== */

async function clearCache() {

    try {

        const response =
            await fetch(
                `${API}/clear`
            );


        const data =
            await response.json();


        renderCache(
            data.cache
        );


        hits = 0;
        misses = 0;


        updateStats();


        addHistory(
            "CLEAR CACHE"
        );


        showStatus(
            "Cache cleared successfully.",
            "success"
        );
    }

    catch (error) {

        showStatus(
            "Cannot connect to Java backend.",
            "error"
        );
    }
}


/* =====================================================
   RENDER CACHE
===================================================== */

function renderCache(cacheData) {

    const container =
        document.getElementById(
            "cache"
        );


    container.innerHTML = "";


    if (
        !cacheData ||
        cacheData.length === 0
    ) {

        container.innerHTML =
            `<div class="empty">
                Cache is empty
            </div>`;

        return;
    }


    /*
     * Java sends cache in:
     *
     * MRU → LRU
     */
    cacheData.forEach(
        (item, index) => {

            const node =
                document.createElement(
                    "div"
                );


            node.className =
                "cache-node";


            /*
             * Last node = LRU.
             */
            if (
                index === cacheData.length - 1
            ) {

                node.classList.add(
                    "lru"
                );
            }


            const label =
                index === 0
                    ? "MRU"
                    : index === cacheData.length - 1
                        ? "LRU"
                        : "";


            node.innerHTML = `

                ${
                    label
                        ? `<div class="label">${label}</div>`
                        : ""
                }

                <div class="key">
                    Key
                </div>

                <div class="value">
                    ${item.value}
                </div>

                <div class="key">
                    ${item.key}
                </div>

            `;


            container.appendChild(
                node
            );


            /*
             * Arrow between nodes.
             */
            if (
                index < cacheData.length - 1
            ) {

                const arrow =
                    document.createElement(
                        "div"
                    );


                arrow.className =
                    "arrow";


                arrow.textContent =
                    "→";


                container.appendChild(
                    arrow
                );
            }
        }
    );
}


/* =====================================================
   UPDATE STATISTICS
===================================================== */

async function updateStats() {

    try {

        const response =
            await fetch(
                `${API}/cache`
            );


        const data =
            await response.json();


        document.getElementById(
            "capacity"
        ).textContent =
            data.capacity;


        document.getElementById(
            "size"
        ).textContent =
            data.size;


        document.getElementById(
            "hits"
        ).textContent =
            hits;


        document.getElementById(
            "misses"
        ).textContent =
            misses;

    }

    catch (error) {

        console.log(
            "Backend not available."
        );
    }
}


/* =====================================================
   STATUS MESSAGE
===================================================== */

function showStatus(
    message,
    type
) {

    const status =
        document.getElementById(
            "status"
        );


    status.textContent =
        message;


    if (type === "error") {

        status.style.background =
            "#fee2e2";

        status.style.color =
            "#b91c1c";
    }

    else if (type === "success") {

        status.style.background =
            "#dcfce7";

        status.style.color =
            "#166534";
    }

    else {

        status.style.background =
            "#eff6ff";

        status.style.color =
            "#1d4ed8";
    }
}


/* =====================================================
   HISTORY
===================================================== */

function addHistory(message) {

    const history =
        document.getElementById(
            "history"
        );


    const empty =
        history.querySelector(
            ".empty"
        );


    if (empty) {

        empty.remove();
    }


    const item =
        document.createElement(
            "div"
        );


    item.className =
        "history-item";


    item.textContent =
        message;


    history.prepend(
        item
    );
}


/* =====================================================
   INITIAL LOAD
===================================================== */

updateStats();

