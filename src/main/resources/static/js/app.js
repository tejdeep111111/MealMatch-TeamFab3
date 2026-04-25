// MealMatch Provider Dashboard — dynamic
(function () {
    'use strict';

    var BASE = window.location.origin;
    var TOKEN_KEY = 'mm_token';
    var EMAIL_KEY = 'mm_email';
    var CAROUSEL_GAP = 16;

    var providerData = null;
    var menuItems = [];
    var carouselIndex = 0;

    // ---- Helpers ----
    function getToken() { return localStorage.getItem(TOKEN_KEY); }
    function getEmail() { return localStorage.getItem(EMAIL_KEY); }

    function escHtml(str) {
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    function relativeTime(dateStr) {
        if (!dateStr) return '';
        var d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        var diffMs = Date.now() - d.getTime();
        var mins = Math.floor(diffMs / 60000);
        if (mins < 1) return 'just now';
        if (mins < 60) return mins + 'm ago';
        var hrs = Math.floor(mins / 60);
        if (hrs < 24) return hrs + 'h ago';
        return Math.floor(hrs / 24) + 'd ago';
    }

    // ---- API fetch ----
    function apiFetch(path, options) {
        var token = getToken();
        var opts = options || {};
        var headers = Object.assign(
            { 'Content-Type': 'application/json' },
            token ? { 'Authorization': 'Bearer ' + token } : {},
            opts.headers || {}
        );
        return fetch(BASE + path, Object.assign({}, opts, { headers: headers }))
            .then(function (res) {
                if (res.status === 401 || res.status === 403) {
                    logout();
                    throw new Error('Unauthorized');
                }
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.text();
            })
            .then(function (text) {
                return text ? JSON.parse(text) : null;
            });
    }

    // ---- Auth / Login overlay ----
    function showLogin() {
        document.getElementById('login-overlay').style.display = 'flex';
    }

    function hideLogin() {
        document.getElementById('login-overlay').style.display = 'none';
    }

    function logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(EMAIL_KEY);
        providerData = null;
        menuItems = [];
        carouselIndex = 0;
        showLogin();
    }

    document.getElementById('logout-btn').addEventListener('click', logout);

    document.getElementById('login-form').addEventListener('submit', function (e) {
        e.preventDefault();
        var emailVal = document.getElementById('login-email').value;
        var passwordVal = document.getElementById('login-password').value;
        var errorEl = document.getElementById('login-error');
        errorEl.textContent = '';

        fetch(BASE + '/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: emailVal, password: passwordVal })
        })
            .then(function (res) {
                if (!res.ok) throw new Error('Invalid credentials');
                return res.json();
            })
            .then(function (data) {
                if (data.role !== 'PROVIDER') {
                    errorEl.textContent = 'Access denied: provider accounts only.';
                    return;
                }
                localStorage.setItem(TOKEN_KEY, data.token);
                localStorage.setItem(EMAIL_KEY, data.email);
                hideLogin();
                loadDashboard();
            })
            .catch(function (err) {
                errorEl.textContent = err.message || 'Login failed. Please try again.';
            });
    });

    // ---- Spinner / Error ----
    function showSpinner() {
        document.getElementById('global-spinner').style.display = 'flex';
    }

    function hideSpinner() {
        document.getElementById('global-spinner').style.display = 'none';
    }

    function showGlobalError(msg) {
        var el = document.getElementById('global-error');
        el.innerHTML = escHtml(msg);
        var retryBtn = document.createElement('button');
        retryBtn.className = 'btn btn-ghost';
        retryBtn.style.cssText = 'margin-left:12px;padding:6px 12px;font-size:12px';
        retryBtn.textContent = 'Retry';
        retryBtn.addEventListener('click', window.loadDashboard);
        el.appendChild(retryBtn);
        el.style.display = 'flex';
    }

    function hideGlobalError() {
        document.getElementById('global-error').style.display = 'none';
    }

    // ---- Dashboard ----
    window.loadDashboard = function () {
        hideGlobalError();
        showSpinner();

        loadProfile()
            .then(function () {
                return Promise.all([
                    loadMenuItems(),
                    loadOrders(),
                    loadSubscriptions(),
                    loadForecast(),
                    loadFeedback()
                ]);
            })
            .catch(function (err) {
                if (err.message !== 'Unauthorized') {
                    showGlobalError('Some data failed to load. ' + (err.message || ''));
                }
            })
            .finally(function () {
                hideSpinner();
            });
    };

    function loadProfile() {
        return apiFetch('/api/provider/me')
            .then(function (data) {
                providerData = data;
                document.getElementById('profile-name').textContent = (data && data.name) || 'My Kitchen';
                document.getElementById('profile-email').textContent = (data && data.email) || getEmail() || '';
            })
            .catch(function () {
                document.getElementById('profile-name').textContent = 'My Kitchen';
                document.getElementById('profile-email').textContent = getEmail() || '';
            });
    }

    // ---- Menu Items ----
    function mealTypeBg(mealType) {
        switch ((mealType || '').toUpperCase()) {
            case 'BREAKFAST': return 'radial-gradient(circle at 50% 55%, #f5e17a 0%, #c8943a 45%, #1a1f1c 78%)';
            case 'LUNCH':     return 'radial-gradient(circle at 50% 55%, #6ec96b 0%, #3d8a4a 45%, #1a1f1c 80%)';
            case 'DINNER':    return 'radial-gradient(ellipse at 50% 50%, #c0382a 0%, #7a1f17 45%, #1a1f1c 75%)';
            case 'SNACK':     return 'radial-gradient(circle at 50% 55%, #f5a524 0%, #b87530 45%, #1a1f1c 78%)';
            default:          return 'radial-gradient(circle at 50% 55%, #d8c8a8 0%, #a99a7a 40%, #1a1f1c 70%)';
        }
    }

    function loadMenuItems() {
        return apiFetch('/api/provider/menu-items')
            .then(function (data) {
                menuItems = Array.isArray(data) ? data : [];
                document.getElementById('stat-menu-items').textContent = menuItems.length;
                renderCarousel();
            })
            .catch(function () {
                document.getElementById('stat-menu-items').textContent = 'N/A';
            });
    }

    function renderCarousel() {
        var track = document.getElementById('carousel-track');
        carouselIndex = 0;
        track.innerHTML = '';

        if (!menuItems.length) {
            track.innerHTML = '<p style="color:var(--text-muted);padding:20px 0;font-size:13px">No menu items yet.</p>';
            return;
        }

        menuItems.forEach(function (item) {
            var article = document.createElement('article');
            article.className = 'dish-card';

            var tags = item.dietaryTags
                ? item.dietaryTags.split(',').map(function (t) { return t.trim(); }).filter(Boolean)
                : [];
            var stockLabel = tags.length
                ? tags.slice(0, 2).join(', ')
                : (item.isAvailable ? 'Available' : 'Out of Stock');
            var stockClass = item.isAvailable ? 'available' : 'out';

            article.innerHTML =
                '<div class="dish-image" style="background:' + mealTypeBg(item.mealType) + '">' +
                '  <span class="price-tag">$' + Number(item.price || 0).toFixed(2) + '</span>' +
                '</div>' +
                '<div class="dish-body">' +
                '  <div class="dish-info">' +
                '    <h3 class="dish-name">' + escHtml(item.name || 'Untitled') + '</h3>' +
                '    <p class="dish-stock ' + stockClass + '">' + escHtml(stockLabel) + '</p>' +
                '  </div>' +
                '  <label class="switch">' +
                '    <input type="checkbox"' + (item.isAvailable ? ' checked' : '') +
                '      data-item-id="' + escHtml(item.id) + '">' +
                '    <span class="slider"></span>' +
                '  </label>' +
                '</div>';

            track.appendChild(article);
        });

        track.querySelectorAll('.switch input').forEach(function (input) {
            input.addEventListener('change', function () {
                var itemId = input.dataset.itemId;
                var newAvail = input.checked;
                var item = menuItems.find(function (m) { return m.id === itemId; });
                if (!item) return;

                apiFetch('/api/provider/menu-items/' + itemId, {
                    method: 'PUT',
                    body: JSON.stringify(Object.assign({}, item, { isAvailable: newAvail }))
                })
                    .then(function () {
                        item.isAvailable = newAvail;
                        var stock = input.closest('.dish-card').querySelector('.dish-stock');
                        if (stock) {
                            stock.className = 'dish-stock ' + (newAvail ? 'available' : 'out');
                            if (!item.dietaryTags || !item.dietaryTags.trim()) {
                                stock.textContent = newAvail ? 'Available' : 'Out of Stock';
                            }
                        }
                    })
                    .catch(function () {
                        input.checked = !newAvail;
                    });
            });
        });

        updateCarousel();
    }

    // ---- Carousel logic ----
    function visibleCount() {
        var carousel = document.querySelector('[data-carousel]');
        if (!carousel) return 3;
        var w = carousel.clientWidth;
        if (w < 520) return 1;
        if (w < 800) return 2;
        return 3;
    }

    function maxCarouselIndex() {
        var track = document.getElementById('carousel-track');
        return Math.max(0, track.children.length - visibleCount());
    }

    function updateCarousel() {
        var track = document.getElementById('carousel-track');
        var carousel = document.querySelector('[data-carousel]');
        if (!track || !carousel) return;

        var visible = visibleCount();
        track.style.gridAutoColumns = 'calc((100% - ' + (CAROUSEL_GAP * (visible - 1)) + 'px) / ' + visible + ')';

        var card = track.children[0];
        if (!card) return;
        var step = card.getBoundingClientRect().width + CAROUSEL_GAP;
        track.style.transform = 'translateX(' + (-carouselIndex * step) + 'px)';
    }

    var prevBtn = document.querySelector('[data-carousel-prev]');
    var nextBtn = document.querySelector('[data-carousel-next]');
    if (prevBtn) {
        prevBtn.addEventListener('click', function () {
            carouselIndex = Math.min(maxCarouselIndex(), Math.max(0, carouselIndex - 1));
            updateCarousel();
        });
    }
    if (nextBtn) {
        nextBtn.addEventListener('click', function () {
            carouselIndex = Math.min(maxCarouselIndex(), Math.max(0, carouselIndex + 1));
            updateCarousel();
        });
    }

    window.addEventListener('resize', function () {
        carouselIndex = Math.min(carouselIndex, maxCarouselIndex());
        updateCarousel();
    });

    // ---- Orders ----
    function statusBadge(status) {
        var s = (status || '').toUpperCase();
        if (s === 'PENDING') return '<span class="badge badge-preparing">Pending</span>';
        if (s === 'CONFIRMED' || s === 'READY') return '<span class="badge badge-ready">Ready</span>';
        if (s === 'DELIVERED' || s === 'COMPLETED') return '<span class="badge badge-delivered">Delivered</span>';
        return '<span class="badge">' + escHtml(status || '') + '</span>';
    }

    function loadOrders() {
        return apiFetch('/api/orders')
            .then(function (data) {
                var orders = Array.isArray(data) ? data : [];
                var pending = orders.filter(function (o) {
                    return (o.status || '').toUpperCase() === 'PENDING';
                });
                document.getElementById('stat-pending-orders').textContent = pending.length;
                renderOrders(orders);
            })
            .catch(function () {
                document.getElementById('stat-pending-orders').textContent = 'N/A';
                renderOrders([]);
            });
    }

    function renderOrders(orders) {
        var tbody = document.getElementById('orders-tbody');
        tbody.innerHTML = '';

        if (!orders.length) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:24px">No orders found.</td></tr>';
            return;
        }

        orders.slice(0, 10).forEach(function (o) {
            var rawId = o.id || o.orderId || '';
            var shortId = rawId.length > 8 ? rawId.substring(0, 8) + '…' : rawId || '—';
            var customer = o.customerEmail || o.userEmail || o.userId || '—';
            var item = o.menuItemName || '—';
            var date = o.scheduledDate || o.createdAt || '—';
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td class="mono">' + escHtml(shortId) + '</td>' +
                '<td>' + escHtml(customer) + '</td>' +
                '<td>' + escHtml(item) + '</td>' +
                '<td>' + statusBadge(o.status) + '</td>' +
                '<td class="muted">' + escHtml(date) + '</td>';
            tbody.appendChild(tr);
        });
    }

    // ---- Subscriptions ----
    function loadSubscriptions() {
        return apiFetch('/api/provider/subscriptions')
            .then(function (data) {
                var subs = Array.isArray(data) ? data : [];
                var active = subs.filter(function (s) {
                    return (s.status || '').toUpperCase() === 'ACTIVE';
                });
                document.getElementById('stat-active-subs').textContent = active.length;
            })
            .catch(function () {
                document.getElementById('stat-active-subs').textContent = 'N/A';
            });
    }

    // ---- Forecast ----
    function loadForecast() {
        return apiFetch('/api/forecast')
            .then(function (data) {
                var forecast = Array.isArray(data) ? data : [];
                var totalRev = forecast.reduce(function (sum, f) {
                    return sum + (f.projectedRevenue || 0);
                }, 0);
                var revStr = totalRev >= 1000
                    ? '$' + (totalRev / 1000).toFixed(1) + 'k'
                    : '$' + totalRev.toFixed(0);
                document.getElementById('stat-projected-rev').textContent = revStr;
                renderForecast(forecast);
            })
            .catch(function () {
                document.getElementById('stat-projected-rev').textContent = 'N/A';
                renderForecast([]);
            });
    }

    function renderForecast(items) {
        var list = document.getElementById('forecast-list');
        list.innerHTML = '';

        if (!items.length) {
            list.innerHTML = '<li style="color:var(--text-muted);font-size:13px">No forecast data available.</li>';
            return;
        }

        var top5 = items.slice(0, 5);
        var maxOrders = top5.reduce(function (m, f) {
            return Math.max(m, f.projectedOrders || 0);
        }, 1);

        top5.forEach(function (f) {
            var pct = Math.round(((f.projectedOrders || 0) / maxOrders) * 100);
            var li = document.createElement('li');
            li.className = 'forecast-row';
            li.innerHTML =
                '<div class="forecast-meta">' +
                '  <span class="forecast-day">' + escHtml(f.menuItemName || '—') + '</span>' +
                '  <span class="forecast-value">' + (f.projectedOrders || 0) + ' orders</span>' +
                '</div>' +
                '<div class="bar"><div class="bar-fill" style="width:' + pct + '%"></div></div>';
            list.appendChild(li);
        });
    }

    // ---- Feedback ----
    function loadFeedback() {
        if (!providerData || !providerData.id) {
            renderFeedback([]);
            return Promise.resolve();
        }
        return apiFetch('/api/reviews/provider/' + providerData.id)
            .then(function (data) {
                renderFeedback(Array.isArray(data) ? data : []);
            })
            .catch(function () {
                renderFeedback([]);
            });
    }

    function renderFeedback(reviews) {
        var list = document.getElementById('feedback-list');
        var pill = document.getElementById('rating-pill-value');
        list.innerHTML = '';

        if (!reviews.length) {
            list.innerHTML = '<li style="color:var(--text-muted);font-size:13px">No reviews yet.</li>';
            if (pill) pill.textContent = '—';
            return;
        }

        var avg = reviews.reduce(function (s, r) { return s + (r.rating || 0); }, 0) / reviews.length;
        if (pill) pill.textContent = avg.toFixed(1);

        reviews.slice(0, 3).forEach(function (r) {
            var stars = '★'.repeat(Math.max(0, Math.min(5, r.rating || 0)));
            var li = document.createElement('li');
            li.className = 'feedback-item';
            li.innerHTML =
                '<div class="feedback-head">' +
                '  <span class="feedback-quote">"' + escHtml(r.comment || 'No comment') + '"</span>' +
                '  <span class="feedback-time">' + relativeTime(r.createdAt) + '</span>' +
                '</div>' +
                '<p class="feedback-body">' + stars + ' (' + (r.rating || 0) + '/5)</p>';
            list.appendChild(li);
        });
    }

    // ---- New Item Modal ----
    var newItemBtn = document.getElementById('new-item-btn');
    var newItemModal = document.getElementById('new-item-modal');
    var modalCloseBtn = document.getElementById('modal-close-btn');
    var modalCancelBtn = document.getElementById('modal-cancel-btn');
    var newItemForm = document.getElementById('new-item-form');

    if (newItemBtn) {
        newItemBtn.addEventListener('click', function () {
            if (newItemModal) newItemModal.style.display = 'flex';
        });
    }

    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', function () {
            if (newItemModal) newItemModal.style.display = 'none';
        });
    }

    if (modalCancelBtn) {
        modalCancelBtn.addEventListener('click', function () {
            if (newItemModal) newItemModal.style.display = 'none';
        });
    }

    if (newItemModal) {
        newItemModal.addEventListener('click', function (e) {
            if (e.target === newItemModal) newItemModal.style.display = 'none';
        });
    }

    if (newItemForm) {
        newItemForm.addEventListener('submit', function (e) {
            e.preventDefault();
            var errorEl = document.getElementById('new-item-error');
            if (errorEl) errorEl.textContent = '';

            var name = document.getElementById('ni-name').value.trim();
            var mealType = document.getElementById('ni-mealtype').value;
            var dietaryTags = document.getElementById('ni-tags').value.trim();
            var price = parseFloat(document.getElementById('ni-price').value);
            var isAvailable = document.getElementById('ni-available').checked;

            if (!name || !mealType || isNaN(price) || price < 0) {
                if (errorEl) errorEl.textContent = 'Please fill in all required fields with valid values.';
                return;
            }

            apiFetch('/api/provider/menu-items', {
                method: 'POST',
                body: JSON.stringify({ name: name, mealType: mealType, dietaryTags: dietaryTags, price: price, isAvailable: isAvailable })
            })
                .then(function () {
                    if (newItemModal) newItemModal.style.display = 'none';
                    newItemForm.reset();
                    return loadMenuItems();
                })
                .catch(function (err) {
                    if (errorEl) errorEl.textContent = 'Failed to create item: ' + (err.message || 'Unknown error');
                });
        });
    }

    // ---- Nav active state (visual only) ----
    document.querySelectorAll('.main-nav .nav-link').forEach(function (link) {
        link.addEventListener('click', function (ev) {
            ev.preventDefault();
            document.querySelectorAll('.main-nav .nav-link').forEach(function (l) { l.classList.remove('active'); });
            link.classList.add('active');
        });
    });

    document.querySelectorAll('.side-nav .side-link').forEach(function (link) {
        link.addEventListener('click', function (ev) {
            ev.preventDefault();
            document.querySelectorAll('.side-nav .side-link').forEach(function (l) { l.classList.remove('active'); });
            link.classList.add('active');
        });
    });

    // ---- Init ----
    if (getToken()) {
        hideLogin();
        loadDashboard();
    } else {
        showLogin();
    }

})();
