// FindIT Frontend Enhancements
(function () {
    'use strict';

    // 1. Live Feed Tab Filtering on Homepage
    var filterTabs = document.querySelectorAll('.filter-tab');
    var itemCards = document.querySelectorAll('#itemGrid .card');

    if (filterTabs.length > 0 && itemCards.length > 0) {
        filterTabs.forEach(function (tab) {
            tab.addEventListener('click', function () {
                var selectedFilter = tab.getAttribute('data-filter');

                filterTabs.forEach(function (t) {
                    t.classList.remove('active');
                    t.setAttribute('aria-selected', 'false');
                });
                tab.classList.add('active');
                tab.setAttribute('aria-selected', 'true');

                itemCards.forEach(function (card) {
                    var cardType = card.getAttribute('data-item-type');
                    if (selectedFilter === 'ALL' || cardType === selectedFilter) {
                        card.style.display = 'flex';
                        card.style.animation = 'fadeIn 0.35s ease forwards';
                    } else {
                        card.style.display = 'none';
                    }
                });
            });
        });
    }

    // 2. Register page password confirmation checker
    var pwd = document.getElementById('password');
    var confirm = document.getElementById('confirmPassword');
    var hint = document.getElementById('matchHint');
    if (pwd && confirm && hint) {
        function checkPasswords() {
            hint.hidden = !confirm.value || confirm.value === pwd.value;
        }
        pwd.addEventListener('input', checkPasswords);
        confirm.addEventListener('input', checkPasswords);
    }

    // 3. Close mobile nav when clicking outside
    var navToggle = document.getElementById('nav-toggle');
    if (navToggle) {
        document.addEventListener('click', function (e) {
            if (navToggle.checked && !e.target.closest('.site-header')) {
                navToggle.checked = false;
            }
        });
    }
})();
