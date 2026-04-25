// MealMatch Provider Dashboard — small interactions
(function () {
    'use strict';

    var CAROUSEL_GAP = 16; // px, matches .carousel-track gap in styles.css

    // ---- Signature Items carousel ----
    document.querySelectorAll('[data-carousel]').forEach(function (carousel) {
        var track = carousel.querySelector('.carousel-track');
        if (!track) return;

        var panel = carousel.closest('.panel');
        var prevBtn = panel ? panel.querySelector('[data-carousel-prev]') : null;
        var nextBtn = panel ? panel.querySelector('[data-carousel-next]') : null;

        var index = 0;

        function visibleCount() {
            var w = carousel.clientWidth;
            if (w < 520) return 1;
            if (w < 800) return 2;
            return 3;
        }

        function maxIndex() {
            return Math.max(0, track.children.length - visibleCount());
        }

        function update() {
            var visible = visibleCount();
            // Lock auto columns to the visible count so children sit side-by-side.
            track.style.gridAutoColumns = 'calc((100% - ' + (CAROUSEL_GAP * (visible - 1)) + 'px) / ' + visible + ')';

            var card = track.children[0];
            if (!card) return;
            var step = card.getBoundingClientRect().width + CAROUSEL_GAP;
            track.style.transform = 'translateX(' + (-index * step) + 'px)';
        }

        function go(delta) {
            var max = maxIndex();
            index = Math.min(max, Math.max(0, index + delta));
            update();
        }

        if (prevBtn) prevBtn.addEventListener('click', function () { go(-1); });
        if (nextBtn) nextBtn.addEventListener('click', function () { go(1); });

        window.addEventListener('resize', function () {
            index = Math.min(index, maxIndex());
            update();
        });

        update();
    });

    // ---- Dish availability toggle visual sync ----
    document.querySelectorAll('.dish-card .switch input').forEach(function (input) {
        input.addEventListener('change', function () {
            var card = input.closest('.dish-card');
            if (!card) return;
            var stock = card.querySelector('.dish-stock');
            if (!stock) return;
            if (input.checked) {
                stock.classList.remove('out');
                stock.classList.add('available');
                if (/out of stock/i.test(stock.textContent)) {
                    stock.textContent = 'Available';
                }
            } else {
                stock.classList.remove('available');
                stock.classList.add('out');
                stock.textContent = 'Out of Stock';
            }
        });
    });

    // ---- Top-nav active state (visual only) ----
    document.querySelectorAll('.main-nav .nav-link').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelectorAll('.main-nav .nav-link').forEach(function (l) {
                l.classList.remove('active');
            });
            link.classList.add('active');
        });
    });

    document.querySelectorAll('.side-nav .side-link').forEach(function (link) {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelectorAll('.side-nav .side-link').forEach(function (l) {
                l.classList.remove('active');
            });
            link.classList.add('active');
        });
    });
})();
